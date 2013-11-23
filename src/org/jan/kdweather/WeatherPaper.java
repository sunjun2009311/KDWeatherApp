package org.jan.kdweather;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import org.jan.kdweather.db.DBManager;
import org.jan.kdweather.db.LocationHistory;
import org.jan.kdweather.util.AccitvitManager;
import org.jan.kdweather.util.WeatherDataUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 城市天气主界面 最主要的Acitivity
 * 
 * @author jan
 * 
 */
public class WeatherPaper extends FragmentActivity implements OnClickListener {

	private ViewPager weatherPaper;
	private static final String DEBUG_TAG = "WeatherPaperActivity";
	private TextView city_txt;
	private ImageButton settings;
	private ImageButton reFresh;
	private Button close;
	private ImageButton tomBtn;
	private ImageButton tipBtn;
	private ArrayList<WeatherFragment> fargmentList;
	private HashMap<String, Object> cityWeather;
	private static ArrayList<LocationHistory> citylist;
	public static DBManager db;
	public int fargmentPosition;
	MyFargemntPagerAdapter myAdapter;
	private long exitTime = 0;
	public static Handler mHandler;

	private boolean isDeleted = false;
	private boolean isRefresh = false;
	private boolean isReadtomInfo = false;
	public int displayHeight;
	private int headHeight;
	private int bottomHeight;

	private Timer timer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		displayHeight = dm.heightPixels;
		displayHeight = getWindowManager().getDefaultDisplay().getHeight();
		Log.d("debug", "displayHeight=" + displayHeight);
		setContentView(R.layout.cityweather_paper);
		db = new DBManager(this, null);
		city_txt = (TextView) findViewById(R.id.city_head);
		city_txt.setTextColor(Color.WHITE);
		citylist = getCitylist();
		fargmentPosition = citylist.size() - 1;
		settings = (ImageButton) findViewById(R.id.set_btn5);
		settings.setOnClickListener(this);
		reFresh = (ImageButton) findViewById(R.id.set_btn2);
		reFresh.setOnClickListener(this);
		close = (Button) findViewById(R.id.delete_page);
		close.setOnClickListener(this);
		tomBtn = (ImageButton) findViewById(R.id.tomorrow_btn);
		tomBtn.setOnClickListener(this);
		tipBtn = (ImageButton) findViewById(R.id.tip_btn);
		tipBtn.setOnClickListener(this);
		initViewPaper();
	}

	public ArrayList<LocationHistory> getCitylist() {
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		return data.getParcelableArrayList("cityList");
	}

	public void initViewPaper() {

		weatherPaper = (ViewPager) findViewById(R.id.viewpaper);
		fargmentList = new ArrayList<WeatherFragment>();
		if (citylist.size() > 0) {
			for (LocationHistory location : citylist) {
				fargmentList.add(WeatherFragment.getInstance(location
						.getCityName()));
			}
		}
		myAdapter = new MyFargemntPagerAdapter(getSupportFragmentManager(),
				fargmentList);
		weatherPaper.setAdapter(myAdapter);
		weatherPaper.setCurrentItem(citylist.size() - 1);

		weatherPaper.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int id) {
				Log.d(DEBUG_TAG, "selected id=" + id);
				// 这个fragmentPosition不可靠，因为只有被移动pager的时候才会被赋值
				fargmentPosition = id;
				String cityName = citylist.get(id).getCityName();
				city_txt.setText(cityName);
				WeatherFragment wf = fargmentList.get(id);
				WeatherFragment.FThread tf = wf.new FThread(cityName);
				tf.start();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	public void bingAdapter(MyFargemntPagerAdapter adapter) {
		this.weatherPaper.setAdapter(adapter);
		weatherPaper.setCurrentItem(fargmentList.size() - 1);
	}

	@Override
	protected void onDestroy() {
		AccitvitManager.getInstance().addActivity(this);
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		if (WeatherDataUtil.isExit) {
			finish();
		}
		// 默认显示第一个城市天气
		if (citylist == null || citylist.size() <= 0) {
			city_txt.setText("未选择");
		} else {
			city_txt.setText(citylist.get(citylist.size() - 1).getCityName());
		}
		super.onResume();
	}

	@Override
	public void onClick(View source) {
		switch (source.getId()) {
		case R.id.set_btn5:
			// 跳转到设置城市的界面
			Intent intent = new Intent(WeatherPaper.this,
					SetLocationActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		case R.id.set_btn2:
			// 完成刷新功能
				isRefresh = true;
				String cityName = null;
				WeatherFragment wf = null;
				try {
					cityName = citylist.get(fargmentPosition).getCityName();
				} catch (Exception e) {
					// TODO: 处理数组越界异常，因为fargmentPosition参数的不稳定性导致
					cityName = citylist.get(fargmentList.size() - 1)
							.getCityName();
					fargmentPosition = fargmentList.size() - 1;
				}
				city_txt.setText(cityName);
				try {
					wf = fargmentList.get(fargmentPosition);
				} catch (Exception e) {
					wf = fargmentList.get(fargmentPosition - 1);
					fargmentPosition = fargmentList.size() - 1;
				}
				WeatherFragment.UpdateThread uf = wf.new UpdateThread();
				uf.start();
			break;
		case R.id.delete_page:
			// 完成关闭当前的pager
			// 注意：根据fragmentPosition来删除时，如果selectId=1时，点击删除，selectedId是不会自动变0
			// ，所以根据fragmentPosition可能出现数组越界异常
			Log.d("debug[WeatherPaper]", "fragmentPosition=" + fargmentPosition);
			String cityname = null;
			this.close.setEnabled(false);
			try {
				cityname = citylist.get(fargmentPosition).getCityName();
			} catch (Exception e) {
				// TODO: 可能出现IndexOutOfBoundsException  直接重新 去设置城市
				cityname = citylist.get(fargmentPosition - 1).getCityName();
				fargmentPosition = fargmentList.size() - 1;
				this.close.setEnabled(true);
//				Intent it = new Intent(WeatherPaper.this,
//						SetLocationActivity.class);
//				it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(it);
//				finish();
			}
			if (fargmentList != null) {
				if(fargmentList.size()>0){					
					fargmentList.remove(fargmentPosition);
					citylist.remove(fargmentPosition);
					db.delete(cityname);
				}
				if (citylist.size() == 0 || fargmentList.size() == 0) {
					Log.d("debug[WetherPaper]",
							"citylist.size=" + citylist.size()
									+ ", fargmentList.size="
									+ fargmentList.size());
					Intent it = new Intent(WeatherPaper.this,
							SetLocationActivity.class);
					startActivity(it);
					finish();
					this.close.setEnabled(true);
				} else {
					myAdapter = new MyFargemntPagerAdapter(
							getSupportFragmentManager(), fargmentList);
					bingAdapter(myAdapter);
					myAdapter.notifyDataSetChanged();
					city_txt.setText(citylist.get(citylist.size() - 1)
							.getCityName());
					this.close.setEnabled(true);
					Log.d("debug", "删除当前城市 city=" + cityname);
				}
			}
		
			break;
		case R.id.tomorrow_btn:
			// 显示未来四天 的天气预报
			if (!isReadtomInfo) {
				isReadtomInfo = true;
				this.tomBtn.setEnabled(false);
				Toast.makeText(WeatherPaper.this, "正在读取...", 800).show();
				try {
					showTomorrowDialog(fargmentPosition);
				} catch (Exception e) {
					// TODO: handle exception
					this.tomBtn.setEnabled(true);
				}
			} else {
				Toast.makeText(WeatherPaper.this, "取消", 800).show();
				isReadtomInfo = false;
			}
			break;
		case R.id.tip_btn:
			// 显示指数内容
			this.tipBtn.setEnabled(false);
			try {
				showTipDialog(fargmentPosition);
			} catch (Exception e) {
				// TODO: handle exception
				this.tipBtn.setEnabled(true);
			}
			break;
		default:
			break;
		}
	}

	public void showTomorrowDialog(int id) {
		final ListView view = (ListView) getLayoutInflater().inflate(
				R.layout.tomorrow_wsinfo, null);
		WeatherFragment wf = null;
		try {
			wf = fargmentList.get(id);
		} catch (Exception e) {
			// TODO: handle exception 主要是数组越界异常
			wf = fargmentList.get(id - 1);
			fargmentPosition = fargmentList.size() - 1;
			id = id - 1;
		}
		final WeatherFragment wf_1 = wf;
		WeatherFragment.TomorrowThread tf = wf.new TomorrowThread();
		tf.start();
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 0x886:
					view.setAdapter(new SimpleAdapter(WeatherPaper.this,
							wf_1.tomorrowInfo, R.layout.simple_item,
							new String[] { "tm_date", "tm_weatherinfo",
									"weather_a" }, new int[] { R.id.tm_date,
									R.id.tm_weatherinfo, R.id.weather_a }));
					AlertDialog dialog = new AlertDialog.Builder(
							WeatherPaper.this).create();
					dialog.setTitle("未来的天气预测");
					try {
						dialog.setView(view);
						dialog.show();
						tomBtn.setEnabled(true);
					} catch (Exception e) {
						// TODO: handle exception
						tomBtn.setEnabled(true);
					}
					break;
				case 1:
					tomBtn.setEnabled(true);
					Toast.makeText(WeatherPaper.this, "网络连接超时！", 3000).show();
					break;
				default:
					break;
				}
			};
		};

	}

	public void showTipDialog(int id) {
		final TextView tv = new TextView(WeatherPaper.this);
		WeatherFragment wf = null;
		try {
			wf = fargmentList.get(id);
		} catch (Exception e) {
			// TODO: handle exception
			wf = fargmentList.get(id - 1);
			fargmentPosition = fargmentList.size() - 1;
			id = id - 1;
			tipBtn.setEnabled(true);
		}
		String tag = id + "";
		WeatherFragment.UpdateThread ut = wf.new UpdateThread(tag);
		ut.start();
		mHandler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case 0x885:
					Log.d("debug", "收到数据：" + msg.obj);
					tv.setText((CharSequence) msg.obj);
					tv.setTextColor(Color.WHITE);
					AlertDialog dialog = new AlertDialog.Builder(
							WeatherPaper.this).create();
					dialog.setTitle("今日指数");
					dialog.setView(tv);
					dialog.show();
					tipBtn.setEnabled(true);
					break;
				case 2:
					tipBtn.setEnabled(true);
					Toast.makeText(WeatherPaper.this, "网络出现故障！", 3000).show();
					break;
				default:
					break;
				}
			};
		};
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序..",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				AccitvitManager.getInstance().exit(this);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
