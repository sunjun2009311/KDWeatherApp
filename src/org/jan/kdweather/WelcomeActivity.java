package org.jan.kdweather;

import java.util.ArrayList;

import org.jan.kdweather.db.DBManager;
import org.jan.kdweather.db.LocationHistory;
import org.jan.kdweather.util.AccitvitManager;
import org.jan.kdweather.util.WebServiceUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class WelcomeActivity extends Activity {

	private static final int DELAY_LENGTH=2500;
	private DBManager db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AccitvitManager.getInstance().addActivity(this);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setType(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_welcome);
		db = new DBManager(this, null);
		final ArrayList<LocationHistory> list=(ArrayList<LocationHistory>) db.queryCityList();
		//在开始欢迎动画时候就加载了省份数据
		try{			
			SetLocationActivity.provinces=WebServiceUtil.getProvinceList();
		}catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(this, "网络服务异常", 2000).show();
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if(list.size()>0){
					Intent intent = new Intent(WelcomeActivity.this, WeatherPaper.class);
					Bundle bundle = new Bundle();
					bundle.putParcelableArrayList("cityList", list);
					intent.putExtras(bundle);
					startActivity(intent);
					finish();
				}else{					
					Intent intent = new Intent(WelcomeActivity.this,SetLocationActivity.class);
					startActivity(intent);
					finish();
				}
			}
		}, DELAY_LENGTH);
	}

	@Override
	protected void onStart() {
		AccitvitManager.getInstance().addActivity(this);
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

}
