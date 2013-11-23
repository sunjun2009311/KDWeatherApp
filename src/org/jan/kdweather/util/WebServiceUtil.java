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
 * ��������Ԥ����WebService�Ĺ�����
 * 
 * @author jan
 * 
 */
public class WebServiceUtil {
	public static final String SERVICE_NAMESPACE = "http://WebXml.com.cn/";// webservice�������ռ�
	public static final String SERVICE_URL = "http://webservice.webxml.com.cn/WebServices/WeatherWS.asmx";
	public static final String USER_ID = "";//"b57efcab08f346199d610d6acc02b53f";

	/**
	 * ��ȡʡ��
	 * 
	 * @return
	 */
	public static List<String> getProvinceList() {
		final String methodName = "getRegionProvince";
		final String resultName = "getRegionProvinceResult";
		// ����HttpTransportSE�������,debug=true �ɵ��Գ���
		final HttpTransportSE htse = new HttpTransportSE(SERVICE_URL);
		htse.debug = true;
		// ʹ��SOAP1.1����Enveloap����
		final SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		// ʵ����SoapObject����
		SoapObject sObject = new SoapObject(SERVICE_NAMESPACE, methodName);
		envelope.bodyOut = sObject;
		envelope.dotNet = true;// ��Ϊ��WebService��.Net������
		FutureTask<List<String>> task = new FutureTask<List<String>>(
				new Callable<List<String>>() {
					@Override
					public List<String> call() throws Exception {
						// ����WebService
						try {
							htse.call(SERVICE_NAMESPACE + methodName, envelope);
						} catch (Exception e) {
							// TODO: ���糬ʱ�������
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
	 * ����ʡ�ݻ�ȡ��ӵ�еĳ�����Ϣ
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
	 * ���ݳ����ҵ��ó�����������Ϣ
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
			//��ʱ������ܻ����
			e.printStackTrace();
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("errorCode","-1");
			return map;
		}
		return null;
	}

	/**
	 * �������ַ������ơ�<string>������,3113</string> <string>����,3114</string>
	 * <string>����,3115</string>...��
	 * 
	 * @param sobj
	 *            ��Ҫ�����spapObject ����
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
	 * <string>���� ����</string> <string>����</string> <string>2002</string>
	 * <string>2013/11/13 19:48:13</string> <string>��������ʵ�������£�12�棻����/����������
	 * 1����ʪ�ȣ�71%</string> <string>��������������������ǿ�ȣ�����</string>
	 * <string>����ָ�������䣬�����ź����׼�ë�µȷ�װ�� ����ָ���������׷������赣�Ĺ������ɷ���������������
	 * �˶�ָ�����ϲ��ˣ��н�ˮ���Ƽ��������ڽ��������˶��� ϴ��ָ�������ˣ����꣬��ˮ����ˮ��Ū�మ����
	 * ��ɹָ�������ˣ���ˮ���ܻ���ʪ�����ѡ����������ɹ�� ����ָ�������ˣ��н�ˮ���¶����ˣ���Ҫ������λ��ᡣ
	 * ·��ָ������ʪ���н�ˮ��·�泱ʪ����С�ļ�ʻ�� ���ʶ�ָ�������ʣ����첻�䲻�ȣ��������� ������Ⱦָ���������������������ڿ�����Ⱦ����ɢ��
	 * ������ָ������������������Ϳ��SPF8-12��ɹ����Ʒ��</string> <string>11��14�� С��ת����</string>
	 * <string>8��/17��</string> <string>����3-4��ת������3-4��</string>
	 * <string>7.gif</string> <string>1.gif</string> <string>11��15��
	 * ����ת��</string> <string>6��/18��</string> <string>������3-4��ת����3-4��</string>
	 * <string>1.gif</string> <string>0.gif</string> <string>11��16�� ��</string>
	 * <string>6��/19��</string> <string>����3-4��ת������3-4��</string>
	 * <string>0.gif</string> <string>0.gif</string> <string>11��17��
	 * ����ת��</string> <string>4��/16��</string> <string>������3-4��ת������3-4��</string>
	 * <string>1.gif</string> <string>0.gif</string> <string>11��18�� ��</string>
	 * <string>5��/14��</string> <string>������3-4��ת���Ϸ�3-4��</string>
	 * <string>0.gif</string> <string>0.gif</string> �����������ݣ��������ֵ����Map��
	 * 
	 * @param obj
	 * @return
	 */
	public static Map<String, Object> parseToWeatherInfo(SoapObject obj) {
		/**
		 * ��Ҫ�����ݣ����� �� ���� �� ʱ�䡢��������ʵ����������������ܰ��ʾ�����졢����ͺ��������(�ֱ�ȡ��δ��4�������)
		 */
		if (obj.getPropertyCount() < 2) {
			String errorCode = obj.getPropertyAsString(0);
			Log.d("debug", "webservice������ִ���" + errorCode);
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
					.substring(currentInfo.indexOf("���£�") + 3,
							currentInfo.indexOf('��') + 1));
			weatherInfo.put(WeatherConstant.HUMIDITY, currentInfo
					.substring(currentInfo.indexOf("ʪ�ȣ�"),
							currentInfo.indexOf('%') + 1));
		} catch (Exception e) {
			// TODO: handle exception
			weatherInfo.put(
					WeatherConstant.CURRENT_TEMPERATURE,
					currentInfo.substring(currentInfo.indexOf("��")+1,
							currentInfo.length()));
		}
		weatherInfo.put(WeatherConstant.AIR_QUALITY, airQuality);
		weatherInfo.put(WeatherConstant.WS_TIP, tip);
		Weather todayWeather = new Weather(obj.getPropertyAsString(7)
				.split(" ")[0], obj.getPropertyAsString(7).split(" ")[1],
				obj.getPropertyAsString(8), obj.getPropertyAsString(9),
				obj.getPropertyAsString(10), obj.getPropertyAsString(11));
		weatherInfo.put(WeatherConstant.TODAY_WEATHER, todayWeather); // �����������
		// webservice ���º��ֶ���Ҫ����
		System.out.println("�����������" + todayWeather.toString());
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
