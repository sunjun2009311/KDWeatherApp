package org.jan.kdweather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jan.kdweather.db.DBManager;
import org.jan.kdweather.db.WeatherInfo;
import org.jan.kdweather.util.Weather;
import org.jan.kdweather.util.WeatherConstant;
import org.jan.kdweather.util.WeatherDataUtil;
import org.jan.kdweather.util.WebServiceUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class WeatherFragment extends Fragment {
	// 天气情况
	private TextView weather_txt;
	// 温度
	private TextView weather_temp;
	// 温差
	private TextView wencha;
	// 湿度
	private TextView humidity;
	// 风向
	private TextView wind;
	// 日期
	private TextView date;
	// 今日提示
	private TextView tip;
	// 更新时间
	private TextView time;
	private View mainView;
	private ImageButton settings;
	private static final String TAG = "WeatherFragment";
	private static DBManager db;
	private String cityName;
	HashMap<String, Object> weatherInfo;
	public List<Map<String, Object>> tomorrowInfo;
	public FThread fThread;
	public boolean isUpdated;
	public Thread updateThread;
	public TomorrowThread tthread;

	public WeatherFragment() {
	}

	public WeatherFragment(String tagStr) {
		this.cityName = tagStr;
	}

	public static WeatherFragment getInstance(String tagStr) {
		WeatherFragment fragment = new WeatherFragment(tagStr);
		Bundle data = new Bundle();
		data.putString("cityName", tagStr);
		fragment.setArguments(data);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "fargment --->OnCreate  isExit=" + WeatherDataUtil.isExit);
		super.onCreate(savedInstanceState);

	}

	public Handler fHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				time.setText("正在更新...");
				break;
			case 0x888:
				weatherInfo = (HashMap<String, Object>) msg.obj;
				Weather todayWeather = (Weather) weatherInfo
						.get(WeatherConstant.TODAY_WEATHER);
				mainView.setBackgroundResource(WeatherDataUtil.parseIcon(
						"main", todayWeather.getIcon_1()));
				weather_txt.setText(todayWeather.getWeather());
				weather_temp.setText((CharSequence) weatherInfo
						.get(WeatherConstant.CURRENT_TEMPERATURE));
				wind.setText(todayWeather.getWindDirection());
				humidity.setText((CharSequence) weatherInfo
						.get(WeatherConstant.HUMIDITY));
				date.setText((CharSequence) weatherInfo
						.get(WeatherConstant.WS_DATE));
				wencha.setText(todayWeather.getTemperature());
				tip.setText((CharSequence) weatherInfo
						.get(WeatherConstant.AIR_QUALITY));
				time.setText("今日" + weatherInfo.get(WeatherConstant.WS_TIME)
						+ "发布");
				WeatherInfo info = new WeatherInfo();
				info.setCityName(cityName);
				info.setWeather_txt(todayWeather.getWeather());
				info.setWeather_temp((String) weatherInfo
						.get(WeatherConstant.CURRENT_TEMPERATURE));
				info.setWencha(todayWeather.getTemperature());
				info.setWind(todayWeather.getWindDirection());
				info.setHumidity((String) weatherInfo
						.get(WeatherConstant.HUMIDITY));
				info.setDate((String) weatherInfo.get(WeatherConstant.WS_DATE));
				info.setTip((String) weatherInfo
						.get(WeatherConstant.AIR_QUALITY));
				info.setTime((String) weatherInfo.get(WeatherConstant.WS_TIME));
				if (db.queryWeatherInfoIsExist(cityName)) {
					Log.d("debug", "更新城市天气，cityName=" + cityName);
					db.updateWeatherInfo(info);
				} else {
					Log.d("debug", "保存城市天气，cityName=" + cityName);
					db.addWeatherInfo(info);
				}
				break;
			case 0x889:
				WeatherInfo wi = (WeatherInfo) msg.obj;
				weather_txt.setText(wi.getWeather_txt());
				weather_temp.setText(wi.getWeather_temp());
				wind.setText(wi.getWind());
				humidity.setText(wi.getHumidity());
				date.setText(wi.getDate());
				humidity.setText(wi.getHumidity());
				wencha.setText(wi.getWencha());
				tip.setText(wi.getTip());
				time.setText("今日" + wi.getTime() + "发布");
				break;
			case 0x887:
				weatherInfo = (HashMap<String, Object>) msg.obj;
				Weather tw = (Weather) weatherInfo
						.get(WeatherConstant.TODAY_WEATHER);
				mainView.setBackgroundResource(WeatherDataUtil.parseIcon(
						"main", tw.getIcon_1()));
				weather_txt.setText(tw.getWeather());
				weather_temp.setText((CharSequence) weatherInfo
						.get(WeatherConstant.CURRENT_TEMPERATURE));
				wind.setText(tw.getWindDirection());
				date.setText((CharSequence) weatherInfo
						.get(WeatherConstant.WS_DATE));
				wencha.setText(tw.getTemperature());
				humidity.setText((CharSequence) weatherInfo
						.get(WeatherConstant.HUMIDITY));
				tip.setText((CharSequence) weatherInfo
						.get(WeatherConstant.AIR_QUALITY));
				time.setText("今日" + weatherInfo.get(WeatherConstant.WS_TIME)
						+ "发布");
				WeatherInfo winfo = new WeatherInfo();
				winfo.setCityName(cityName);
				winfo.setWeather_txt(tw.getWeather());
				winfo.setWeather_temp((String) weatherInfo
						.get(WeatherConstant.CURRENT_TEMPERATURE));
				winfo.setWencha(tw.getTemperature());
				winfo.setWind(tw.getWindDirection());
				winfo.setDate((String) weatherInfo.get(WeatherConstant.WS_DATE));
				winfo.setTip((String) weatherInfo
						.get(WeatherConstant.AIR_QUALITY));
				winfo.setTime((String) weatherInfo.get(WeatherConstant.WS_TIME));
				winfo.setHumidity((String) weatherInfo
						.get(WeatherConstant.HUMIDITY));
				db.updateWeatherInfo(winfo);
				Toast.makeText(getActivity(), "更新完成", Toast.LENGTH_SHORT)
						.show();
				break;
			case 1:
				Toast.makeText(getActivity(), "网络连接异常请重新操作", Toast.LENGTH_SHORT)
						.show();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "fargment is onCreateView");
		View view = null;
		try {
			view = inflater.inflate(R.layout.apaper, container, false);
			mainView = view;
			view.setBackgroundResource(R.drawable.default_weatherbackground);
		} catch (Exception e) {
			// TODO: 处理不可控的事件
			e.printStackTrace();
			view = inflater.inflate(R.layout.apaper, container, false);
			mainView = view;
			view.setBackgroundResource(R.drawable.default_weatherbackground);
			
		}
		weather_txt = (TextView) view.findViewById(R.id.weather_tianqi);
		weather_temp = (TextView) view.findViewById(R.id.weather_temp);
		wencha = (TextView) view.findViewById(R.id.wencha);
		wind = (TextView) view.findViewById(R.id.wind);
		humidity = (TextView) view.findViewById(R.id.humidity);
		date = (TextView) view.findViewById(R.id.date);
		tip = (TextView) view.findViewById(R.id.tip);
		time = (TextView) view.findViewById(R.id.time);

		fThread = new FThread(cityName);
		fThread.start();
		if (weatherInfo == null) {
			Log.d("debug", "weatherInfo is null ");
		}
		return view;
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestory");
		super.onDestroy();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public class FThread extends Thread {
		public FThread() {
		}

		String cityName;

		public FThread(String cityName) {
			this.cityName = cityName;
		}

		@Override
		public void run() {
			db = WeatherPaper.db;
			if (cityName == null) {
				Log.d("debug[WeatherFragment]", "cityName is null");
				return;
			}
			if (db.queryWeatherInfoIsExist(cityName)) {
				WeatherInfo info = db.queryWeatherInfo(cityName);
				if (info != null) {
					Log.d("debug", "db query weatherInfo is ok");
					Message msg = new Message();
					msg.obj = info;
					msg.what = 0x889;
					fHandler.sendMessage(msg);
				}
			} else {
				weatherInfo = (HashMap<String, Object>) WebServiceUtil
						.getWeatherByCity(cityName);
				if (weatherInfo != null) {
					if (weatherInfo.containsKey("errorCode")) {
						fHandler.sendEmptyMessage(1);
					} else {
						Log.d("debug", "weatherInfo ok ");
						Message msg = new Message();
						msg.obj = weatherInfo;
						msg.what = 0x888;
						fHandler.sendMessage(msg);
					}
				}
			}
		}

	}

	public class UpdateThread extends Thread {
		String tag;

		public UpdateThread() {
		}

		public UpdateThread(String tag) {
			this.tag = tag;
		}

		@Override
		public void run() {
			Message msg = new Message();
			if (tag != null) {
				weatherInfo = (HashMap<String, Object>) WebServiceUtil
						.getWeatherByCity(cityName);
				if (weatherInfo != null) {
					if (weatherInfo.containsKey("errorCode")) {
						WeatherPaper.mHandler.sendEmptyMessage(2);	
					} else {
						msg.obj = weatherInfo.get(WeatherConstant.WS_TIP).toString();
						Log.d("debug", "msg.obj=" + msg.obj.toString());
						msg.what = 0x885;
						WeatherPaper.mHandler.sendMessage(msg);					
					}
				}else{
					//网络异常
					WeatherPaper.mHandler.sendEmptyMessage(2);
				}
			} else {
				fHandler.sendEmptyMessage(0);
				weatherInfo = (HashMap<String, Object>) WebServiceUtil
						.getWeatherByCity(cityName);
				if (weatherInfo != null) {
					if (weatherInfo.containsKey("errorCode")) {
						fHandler.sendEmptyMessage(0);
					} else {
						Log.d("debug", "weatherInfo ok ");
						msg.obj = weatherInfo;
						msg.what = 0x887;
						fHandler.sendMessage(msg);
					}
				}
			}

		}
	}

	public class TomorrowThread extends Thread {

		@Override
		public void run() {
			Message msg = new Message();

			weatherInfo = (HashMap<String, Object>) WebServiceUtil
					.getWeatherByCity(cityName);
			if (weatherInfo != null) {
				Log.d("debug", " TTHREAD weatherInfo ok ");
				if (weatherInfo.containsKey("errorCode")) {
					WeatherPaper.mHandler.sendEmptyMessage(1);
				} else {
					tomorrowInfo = new ArrayList<Map<String, Object>>();
					Map<String, Object> item_1 = new HashMap<String, Object>();
					Weather tomInfo = (Weather) weatherInfo
							.get(WeatherConstant.TOMORROW_WEATHER);
					item_1.put("weather_a",
							WeatherDataUtil.parseIcon(tomInfo.getIcon_1()));
					item_1.put("tm_date",
							tomInfo.getDate() + " " + tomInfo.getWeather());
					item_1.put("tm_weatherinfo", tomInfo.getTemperature() + " "
							+ tomInfo.getWindDirection());
					tomorrowInfo.add(item_1);
					Map<String, Object> item_2 = new HashMap<String, Object>();
					Weather aftinfo = (Weather) weatherInfo
							.get(WeatherConstant.AFTERDAY_WEATHER);
					item_2.put("weather_a",
							WeatherDataUtil.parseIcon(aftinfo.getIcon_1()));
					item_2.put("tm_date",
							aftinfo.getDate() + " " + aftinfo.getWeather());
					item_2.put("tm_weatherinfo", aftinfo.getTemperature() + " "
							+ aftinfo.getWindDirection());
					tomorrowInfo.add(item_2);
					Map<String, Object> item_3 = new HashMap<String, Object>();
					Weather thirdDay = (Weather) weatherInfo
							.get(WeatherConstant.DATEWS_3);
					item_3.put("weather_a",
							WeatherDataUtil.parseIcon(thirdDay.getIcon_1()));
					item_3.put("tm_date",
							thirdDay.getDate() + " " + thirdDay.getWeather());
					item_3.put("tm_weatherinfo", thirdDay.getTemperature()
							+ " " + thirdDay.getWindDirection());
					tomorrowInfo.add(item_3);
					Map<String, Object> item_4 = new HashMap<String, Object>();
					Weather fourthDay = (Weather) weatherInfo
							.get(WeatherConstant.DATEWS_4);
					item_4.put("weather_a",
							WeatherDataUtil.parseIcon(fourthDay.getIcon_1()));
					item_4.put("tm_date",
							fourthDay.getDate() + " " + fourthDay.getWeather());
					item_4.put("tm_weatherinfo", fourthDay.getTemperature()
							+ " " + fourthDay.getWindDirection());
					tomorrowInfo.add(item_4);
					Log.d("debug", " 查询未来天气已经成功，发送中。。。 ");
					WeatherPaper.mHandler.sendEmptyMessage(0x886);
				}
			}else{
				//网络异常
				fHandler.sendEmptyMessage(1);
			}

		}
	}

}
