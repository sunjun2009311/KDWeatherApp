package org.jan.kdweather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jan.kdweather.db.DBManager;
import org.jan.kdweather.db.LocationHistory;
import org.jan.kdweather.location.Location;
import org.jan.kdweather.util.AccitvitManager;
import org.jan.kdweather.util.Weather;
import org.jan.kdweather.util.WeatherConstant;
import org.jan.kdweather.util.WebServiceUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * �������ӳ��еĽ���
 * 
 * @author jan
 * 
 */
public class SetLocationActivity extends Activity {
	public static List<String> provinces = new ArrayList<String>();
	public static List<String> citiesList = new ArrayList<String>();
	private Spinner province_spin;
	private Spinner city_spin;
	private Button ok;
	private Button getLocation;
	private LocationClient locationClient;
	private boolean isStart = false;
	private DBManager db;
	private long exitTime = 0;
	ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_city_activity);
		ok = (Button) findViewById(R.id.ok);
		getLocation = (Button) findViewById(R.id.getlocation);
		db = new DBManager(this, "locationHisotry.db3");

		locationClient = ((Location) getApplication()).mLocationClient;
		locationClient.registerLocationListener(new MyLocationListenner());
		// û��������handler�ﴦ����д�����������޸İɡ�����
		// provinces = WebServiceUtil.getProvinceList();
		city_spin = (Spinner) findViewById(R.id.city_spinner);
		province_spin = (Spinner) findViewById(R.id.provinces_spinner);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (provinces == null) {
					mHandler.sendEmptyMessage(0x126);
				} else {
					String cityName = city_spin.getSelectedItem().toString();
					boolean isExist = db.queryCitylistByCityName(cityName);
					if (isExist) {
						Toast.makeText(SetLocationActivity.this, "�Ѿ����", 800)
								.show();
					} else {
						LocationHistory lh = new LocationHistory();
						lh.setCityName(cityName);
						db.add(lh);
						Toast.makeText(SetLocationActivity.this, "��ӳɹ�", 800)
								.show();
					}
					try {
						Log.d("DEBUG", "LIST.size=" + db.queryCityList().size());
						new SetLocationThread(cityName).start();
						pd.show();
					} catch (Exception e) {
						// TODO: �����ѯʧ�ܺ��NullpointException
						e.printStackTrace();

					}
				}
			}
		});

		// �����λ�¼�
		getLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View source) {
				if (provinces == null) {
					mHandler.sendEmptyMessage(0x126);
				} else {
					if (!isStart) {
						pd.setTitle("��ʾ");
						pd.setMessage("���ڶ�λ��...");
						pd.show();
						locationClient.start();
						getLocation.setText("ȡ��\\���¶�λ");
						setLocationOption();
						locationClient.requestLocation();
						isStart = true;
					} else {
						Toast.makeText(SetLocationActivity.this, "ȡ����λ", 500)
								.show();
						locationClient.stop();
						getLocation.setText("�Զ���λ");
						isStart = false;
					}
				}
			}
		});
	}

	// ������ز���
	private void setLocationOption() {
		LocationClientOption option = new LocationClientOption();
		option.setCoorType("bd09ll"); // ������������
		option.setServiceName("com.baidu.location.service_v2.9");
		option.setAddrType("all");
		// option.setPriority(LocationClientOption.NetWorkFirst); // ������������
		option.disableCache(true);
		option.setPoiNumber(10);
		locationClient.setLocOption(option);
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x123:
				if (pd.isShowing()) {
					pd.dismiss();
				}
				HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
				Weather todayWeather = (Weather) map
						.get(WeatherConstant.TODAY_WEATHER);
				Intent intent = new Intent(SetLocationActivity.this,
						WeatherPaper.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // �����һ��ջ����activity
				Bundle bundle = new Bundle();
				bundle.putParcelableArrayList("cityList",
						(ArrayList<LocationHistory>) db.queryCityList());
				intent.putExtras(bundle);
				startActivity(intent);
				finish();
				break;
			case 0x124:
				if (pd.isShowing()) {
					pd.dismiss();
				}
				Toast.makeText(SetLocationActivity.this, "��Ǹ�����س�������û�гɹ�",
						Toast.LENGTH_LONG).show();
				break;
			case 0x125:
				if (pd.isShowing()) {
					pd.dismiss();
				}
				provinces = (List<String>) msg.obj;
				SpinnerListAdapter adapter = new SpinnerListAdapter(
						SetLocationActivity.this, provinces);
				province_spin.setAdapter(adapter);
				province_spin
						.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> source,
									View parent, int position, long id) {
								// TODO: ��Ҫ�½��̸߳���
								try {
									citiesList = WebServiceUtil
											.getCityListByProvince(province_spin
													.getSelectedItem()
													.toString());

									SpinnerListAdapter cityAdapter = new SpinnerListAdapter(
											SetLocationActivity.this,
											citiesList);
									city_spin.setAdapter(cityAdapter);
								} catch (Exception e) {
									// TODO: handle exception
									e.printStackTrace();
									startActivity(new Intent(
											SetLocationActivity.this,
											WelcomeActivity.class));
								}
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}
						});
				city_spin
						.setOnItemSelectedListener(new OnItemSelectedListener() {

							@Override
							public void onItemSelected(AdapterView<?> source,
									View view, int position, long id) {
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}
						});
				break;
			case 0x126:
				if (pd.isShowing()) {
					pd.dismiss();
				}
				Toast.makeText(SetLocationActivity.this, "����δ���ӣ����������������",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		locationClient.stop();
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		AccitvitManager.getInstance().addActivity(this);
		super.onStart();
	}

	@Override
	protected void onResume() {
		pd = new ProgressDialog(this);
		pd.setTitle("������ʾ");
		pd.setMessage("���ڼ��س�����Ϣ�����Ե�");
		pd.setCancelable(true);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();
		if (provinces == null) {
			Log.d("debug", "provinces list is null");
			new SetLocationThread(null).start();
		} else {
			Log.d("debug", "provinceslist-->" + provinces.toString()
					+ ",provinces size=" + provinces.size());
			if (provinces.size() == 0) {
				new SetLocationThread(null).start();
			} else {
				Message hasRead = new Message();
				hasRead.obj = provinces;
				hasRead.what = 0x125;
				mHandler.sendMessage(hasRead);
			}
		}
		super.onResume();
	}

	public class SetLocationThread extends Thread {

		private String cityName;

		public SetLocationThread() {
		}

		public SetLocationThread(String cityName) {
			this.cityName = cityName;
		}

		@Override
		public void run() {
			Message msg = new Message();
			if (cityName != null) {
				HashMap<String, Object> map = (HashMap<String, Object>) WebServiceUtil
						.getWeatherByCity(cityName);
				if (map != null) {
					msg.obj = map;
					msg.what = 0x123;
					mHandler.sendMessage(msg);
				} else {
					// ��ѯʧ��
					msg.what = 0x124;
					mHandler.sendEmptyMessage(msg.what);
				}
			} else {
				// �������ʡ�ݡ����е�����
				provinces = WebServiceUtil.getProvinceList();
				if (provinces == null) {
					mHandler.sendEmptyMessage(0x126);
				} else {
					msg.obj = provinces;
					msg.what = 0x125;
					mHandler.sendMessage(msg);
				}
			}
		}

	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		Log.d("debug[SetLocationActivity]", "onStop");
		super.onStop();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				AccitvitManager.getInstance().exit(this);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * λ�ü�������
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			Log.d("debug", "onReceiveLocation=>" + location);
			if (location == null) {
				Toast.makeText(SetLocationActivity.this, "����ʧ�ܣ������¶�λ", 1000)
						.show();
				return;
			}
			Toast.makeText(SetLocationActivity.this,
					"��ǰλ��=" + location.getAddrStr(), 500).show();
			Log.d("debug", "location=>" + location.getCity());
			if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				String cityName = location.getCity().substring(0,
						location.getCity().indexOf('��'));
				boolean isExist = db.queryCitylistByCityName(cityName);
				if (isExist) {
					Toast.makeText(SetLocationActivity.this,
							"�Ѿ����[" + cityName + "]", 500).show();
				} else {
					LocationHistory lh = new LocationHistory();
					lh.setCityName(cityName);
					db.add(lh);
					Toast.makeText(SetLocationActivity.this, "��ӳɹ�", 500)
							.show();
				}
				try {
					Log.d("DEBUG", "�����ļ��еĳ����б���Ϊ" + db.queryCityList().size());
					new SetLocationThread(cityName).start();
					pd.show();
				} catch (Exception e) {
					// TODO: �����ѯʧ�ܺ��NullpointException
					e.printStackTrace();

				}
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			Log.d("debug", "onReceivePoi");
			if (poiLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
				Toast.makeText(SetLocationActivity.this,
						"�Ѿ����" + poiLocation.getCity(), 500).show();
				String cityName = poiLocation.getCity();
				boolean isExist = db.queryCitylistByCityName(cityName);
				if (isExist) {
					Toast.makeText(SetLocationActivity.this, "�Ѿ����", 500)
							.show();
				} else {
					LocationHistory lh = new LocationHistory();
					lh.setCityName(cityName);
					db.add(lh);
					Toast.makeText(SetLocationActivity.this, "��ӳɹ�", 500)
							.show();
				}
				try {
					Log.d("DEBUG", "�����ļ��еĳ����б���Ϊ" + db.queryCityList().size());
					new SetLocationThread(cityName).start();
					pd.show();
				} catch (Exception e) {
					// TODO: �����ѯʧ�ܺ��NullpointException
					e.printStackTrace();

				}

			}
		}
	}

}
