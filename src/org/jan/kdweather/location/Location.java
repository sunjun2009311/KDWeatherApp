package org.jan.kdweather.location;

import android.app.Application;
import android.os.Process;
import android.os.Vibrator;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;

/**
 * ��λLocation
 *
 */
public class Location extends Application {


	public LocationClient mLocationClient = null;
	public GeofenceClient mGeofenceClient;
	public NotifyLister mNotifyer=null;
	public Vibrator mVibrator01;
	public static String TAG = "LocTestDemo";
	
	@Override
	public void onCreate() {
		mLocationClient = new LocationClient( this );
		/**������������������������������������������������������������������������������������������������������������������������������������
		 * �����AK��Ӧ��ǩ�������󶨣����ʹ�����Լ��Ĺ�������Ҫ�滻Ϊ�Լ������Key
		 * ������������������������������������������������������������������������������������������������������������������������������������
		 */
		mLocationClient.setAK("2ae11254a07ee8e6943fc315ab564630");
		mGeofenceClient = new GeofenceClient(this);
		//λ��������ش���
//		mNotifyer = new NotifyLister();
//		mNotifyer.SetNotifyLocation(40.047883,116.312564,3000,"gps");//4����������Ҫλ�����ѵĵ�����꣬���庬������Ϊ��γ�ȣ����ȣ����뷶Χ������ϵ����(gcj02,gps,bd09,bd09ll)
//		mLocationClient.registerNotify(mNotifyer);
		
		super.onCreate(); 
		Log.d(TAG, "... Application onCreate... pid=" + Process.myPid());
	}
	
	
	
	public class NotifyLister extends BDNotifyListener{
		public void onNotify(BDLocation mlocation, float distance){
			mVibrator01.vibrate(1000);
		}
	}

}
