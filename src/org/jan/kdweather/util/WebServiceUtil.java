package org.jan.kdweather.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

/**
 * 调用天气预报的WebService的工具类
 * 
 * @author jan
 * 
 */
public class WebServiceUtil {
	public static final String SERVICE_NAMESPACE = "http://WebXml.com.cn/";// webservice的命名空间
	public static final String SERVICE_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx";
	public static final String USER_ID = "";//"b57efcab08f346199d610d6acc02b53f";

	/**
	 * 获取省份
	 * 
	 * @return
	 */
	public static List<String> getProvinceList() {
		final String methodName = "getRegionProvince";
		final String resultName = "getRegionProvinceResult";
		// 创建HttpTransportSE传输对象,debug=true 可调试程序
		final HttpTransportSE htse = new HttpTransportSE(SERVICE_URL);
		htse.debug = true;
		// 使用SOAP1.1创建Enveloap对象
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		// 实例化SoapObject对象
		SoapObject sObject = new SoapObject(SERVICE_NAMESPACE, methodName);
		envelope.bodyOut = sObject;
		envelope.dotNet = true;// 因为该WebService是.Net开发的
		FutureTask<List<String>> task = new FutureTask<List<String>>(
				new Callable<List<String>>() {
					@Override
					public List<String> call() throws Exception {
						// 调用WebService
						try {
							htse.call(SERVICE_NAMESPACE + methodName, envelope);
						} catch (Exception e) {
							// TODO: 网络超时的情况！
						}
						if (envelope.getResponse() != null) {
							SoapObject genResult = (SoapObject) envelope.bodyIn;
							SoapObject result = (SoapObject) genResult
									.getProperty(resultName);
							System.out.println("result=" + result.toString());
							return parseProvinceOrCity(result);
						}
						return null;
					}
				});
		new Thread(task).start();
		try {
			return task.get();
		} catch (Exception e) {
			e.printStackTrace();
//			List<String> list=new ArrayList<String>();
//			list.add("-1");
//			return list;
			return null;
		}
	}

	/**
	 * 更具省份获取所拥有的城市信息
	 * 
	 * @param province
	 * @return list
	 */
	public static List<String> getCityListByProvince(String province) {
		final String methodName = "getSupportCityString";
		final String resultName = "getSupportCityStringResult";
		final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
		ht.debug = true;
		SoapObject sObj = new SoapObject(SERVICE_NAMESPACE, methodName);
		sObj.addProperty("theRegionCode", province);
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = sObj;
		envelope.dotNet = true;
		FutureTask<List<String>> task = new FutureTask<List<String>>(
				new Callable<List<String>>() {
					@Override
					public List<String> call() throws Exception {
						ht.call(SERVICE_NAMESPACE + methodName, envelope);
						if (envelope.getResponse() != null) {
							SoapObject response = (SoapObject) envelope.bodyIn;
							SoapObject result = (SoapObject) response
									.getProperty(resultName);
							return parseProvinceOrCity(result);
						}
						return null;
					}
				});
		new Thread(task).start();
		try {
			return task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
//			List<String> list=new ArrayList<String>();
//			list.add("-1");
//			return list;
			return null;
		}
		return null;
	}

	/**
	 * 根据城市找到该城市天气的信息
	 * 
	 * @param city
	 * @return map
	 */
	public static Map<String, Object> getWeatherByCity(String city) {
		final String methodName = "getWeather";
		final String resultName = "getWeatherResult";
		final HttpTransportSE ht = new HttpTransportSE(SERVICE_URL);
		ht.debug = true;
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		SoapObject sObject = new SoapObject(SERVICE_NAMESPACE, methodName);
		sObject.addProperty("theCityCode", city);
		sObject.addProperty("theUserID", USER_ID);
		envelope.bodyOut = sObject;
		envelope.dotNet = true;
		FutureTask<Map<String, Object>> task = new FutureTask<Map<String, Object>>(
				new Callable<Map<String, Object>>() {
					@Override
					public Map<String, Object> call() throws Exception {
						ht.call(SERVICE_NAMESPACE + methodName, envelope);
						if (envelope.getResponse() != null) {
							SoapObject response = (SoapObject) envelope.bodyIn;
							SoapObject result = (SoapObject) response
									.getProperty(resultName);
							return parseToWeatherInfo(result);
						}
						return null;
					}
				});
		new Thread(task).start();
		try {
			return task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			//超时情况可能会出现
			e.printStackTrace();
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("errorCode","-1");
			return map;
		}
		return null;
	}

	/**
	 * 处理返回字符串类似“<string>黑龙江,3113</string> <string>吉林,3114</string>
	 * <string>辽宁,3115</string>...”
	 * 
	 * @param sobj
	 *            需要处理的spapObject 对象
	 * @return List<String>
	 */
	public static List<String> parseProvinceOrCity(SoapObject sobj) {
		List<String> resultList = new ArrayList<String>();
		for (int i = 0; i < sobj.getPropertyCount(); i++) {
			resultList.add(sobj.getProperty(i).toString().split(",")[0]);
		}
		return resultList;
	}

	/**
	 * <string>江苏 无锡</string> <string>无锡</string> <string>2002</string>
	 * <string>2013/11/13 19:48:13</string> <string>今日天气实况：气温：12℃；风向/风力：东风
	 * 1级；湿度：71%</string> <string>空气质量：良；紫外线强度：最弱</string>
	 * <string>穿衣指数：较冷，建议着厚外套加毛衣等服装。 过敏指数：极不易发，无需担心过敏，可放心外出，享受生活。
	 * 运动指数：较不宜，有降水，推荐您在室内进行休闲运动。 洗车指数：不宜，有雨，雨水和泥水会弄脏爱车。
	 * 晾晒指数：不宜，降水可能会淋湿衣物，请选择在室内晾晒。 旅游指数：适宜，有降水，温度适宜，不要错过出游机会。
	 * 路况指数：潮湿，有降水，路面潮湿，请小心驾驶。 舒适度指数：舒适，白天不冷不热，风力不大。 空气污染指数：良，气象条件有利于空气污染物扩散。
	 * 紫外线指数：最弱，辐射弱，涂擦SPF8-12防晒护肤品。</string> <string>11月14日 小雨转多云</string>
	 * <string>8℃/17℃</string> <string>东风3-4级转东北风3-4级</string>
	 * <string>7.gif</string> <string>1.gif</string> <string>11月15日
	 * 多云转晴</string> <string>6℃/18℃</string> <string>西北风3-4级转北风3-4级</string>
	 * <string>1.gif</string> <string>0.gif</string> <string>11月16日 晴</string>
	 * <string>6℃/19℃</string> <string>北风3-4级转西北风3-4级</string>
	 * <string>0.gif</string> <string>0.gif</string> <string>11月17日
	 * 多云转晴</string> <string>4℃/16℃</string> <string>西北风3-4级转东北风3-4级</string>
	 * <string>1.gif</string> <string>0.gif</string> <string>11月18日 晴</string>
	 * <string>5℃/14℃</string> <string>东北风3-4级转东南风3-4级</string>
	 * <string>0.gif</string> <string>0.gif</string> 处理这种数据，将必须的值存入Map中
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> parseToWeatherInfo(SoapObject obj) {
		/**
		 * 需要的数据：城市 、 日期 和 时间、今日天气实况、空气质量，温馨提示，今天、明天和后天的天气(分别取出未来4天的天气)
		 */
		if (obj.getPropertyCount() < 2) {
			String errorCode = obj.getPropertyAsString(0);
			Log.d("debug", "webservice服务出现错误：" + errorCode);
			return null;
		}
		HashMap<String, Object> weatherInfo = new HashMap<String, Object>();
		String cityName = obj.getProperty(1).toString();
		String date = obj.getProperty(3).toString().split(" ")[0];// 2013/11/06
																	// 12:04:34
		String time = obj.getProperty(3).toString().split(" ")[1];
		String currentInfo = obj.getProperty(4).toString();
		String airQuality = obj.getProperty(5).toString();
		String tip = obj.getProperty(6).toString();
		weatherInfo.put(WeatherConstant.CITY_NAME, cityName);
		weatherInfo.put(WeatherConstant.WS_DATE, date);
		weatherInfo.put(WeatherConstant.WS_TIME, time);
		weatherInfo.put(WeatherConstant.WS_WEATHERINFO, currentInfo);
		try {
			weatherInfo.put(WeatherConstant.CURRENT_TEMPERATURE, currentInfo
					.substring(currentInfo.indexOf("气温：") + 3,
							currentInfo.indexOf('℃') + 1));
			weatherInfo.put(WeatherConstant.HUMIDITY, currentInfo
					.substring(currentInfo.indexOf("湿度："),
							currentInfo.indexOf('%') + 1));
		} catch (Exception e) {
			// TODO: handle exception
			weatherInfo.put(
					WeatherConstant.CURRENT_TEMPERATURE,
					currentInfo.substring(currentInfo.indexOf("：")+1,
							currentInfo.length()));
		}
		weatherInfo.put(WeatherConstant.AIR_QUALITY, airQuality);
		weatherInfo.put(WeatherConstant.WS_TIP, tip);
		Weather todayWeather = new Weather(obj.getPropertyAsString(7)
				.split(" ")[0], obj.getPropertyAsString(7).split(" ")[1],
				obj.getPropertyAsString(8), obj.getPropertyAsString(9),
				obj.getPropertyAsString(10), obj.getPropertyAsString(11));
		weatherInfo.put(WeatherConstant.TODAY_WEATHER, todayWeather); // 今天的天机情况
		// webservice 更新后字段需要处理
		System.out.println("今天的天气：" + todayWeather.toString());
		Weather tomorrowWeather = new Weather(obj.getPropertyAsString(12)
				.split(" ")[0], obj.getPropertyAsString(12).split(" ")[1],
				obj.getPropertyAsString(13), obj.getPropertyAsString(14),
				obj.getPropertyAsString(15), obj.getPropertyAsString(16));
		weatherInfo.put(WeatherConstant.TOMORROW_WEATHER, tomorrowWeather);
		Weather afterdayWeather = new Weather(obj.getPropertyAsString(17)
				.split(" ")[0], obj.getPropertyAsString(17).split(" ")[1],
				obj.getPropertyAsString(18), obj.getPropertyAsString(19),
				obj.getPropertyAsString(20), obj.getPropertyAsString(21));
		weatherInfo.put(WeatherConstant.AFTERDAY_WEATHER, afterdayWeather);
		
		Weather thirdDay = new Weather(obj.getPropertyAsString(22)
				.split(" ")[0], obj.getPropertyAsString(22).split(" ")[1],
				obj.getPropertyAsString(23), obj.getPropertyAsString(24),
				obj.getPropertyAsString(25), obj.getPropertyAsString(26));
		weatherInfo.put(WeatherConstant.DATEWS_3, thirdDay);
		Weather forthday = new Weather(obj.getPropertyAsString(27)
				.split(" ")[0], obj.getPropertyAsString(27).split(" ")[1],
				obj.getPropertyAsString(28), obj.getPropertyAsString(29),
				obj.getPropertyAsString(30), obj.getPropertyAsString(31));
		weatherInfo.put(WeatherConstant.DATEWS_4, forthday);
		System.out.println("weatherinfo map=>" + weatherInfo.toString());
		return weatherInfo;
	}
}
