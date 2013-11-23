package org.jan.kdweather.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 数据库管理
 * @author jan
 *
 */
public class DBManager {
	private MyDatabaseHelper dbHelper;
	private SQLiteDatabase sqliteDB;
	private static final String DB_NAME = "locationHisotry.db3";

	public DBManager(Context context, String name) {
		if (name == null) {
			dbHelper = new MyDatabaseHelper(context, DB_NAME);
		} else {
			dbHelper = new MyDatabaseHelper(context, name);
		}
		sqliteDB = dbHelper.getWritableDatabase();
	}

	public void add(LocationHistory history) {
		// sqliteDB.beginTransaction();
		// try {
		sqliteDB.execSQL("insert into locationHistory values(null,?)",
				new Object[] { history.getCityName() });
		// sqliteDB.endTransaction();
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// sqliteDB.close();
		// }
	}

	public void delete(String cityName) {
		sqliteDB.delete("locationHistory", "cityName like ?",
				new String[] { cityName });
	}

	/**
	 * 查询是否已经存在城市内容
	 * 
	 * @param cityname
	 * @return
	 */
	public boolean queryCitylistByCityName(String cityname) {
		Cursor c = sqliteDB.rawQuery(
				"select count(*) from locationHistory where cityName=?",
				new String[] { cityname });
		int sum=0;
		if(c.moveToFirst()){			
			sum = c.getInt(c.getColumnIndex("count(*)"));
		}
		Log.d("debug", "cursor=" + c + ",\n sum=" + sum);
		if (sum > 0) {
			return true;
		}
		return false; // 不存在
	}

	public List<LocationHistory> queryCityList() {
		ArrayList<LocationHistory> list = new ArrayList<LocationHistory>();
		// sqliteDB.query("locationHistory", new String[]{"_id,cityName"}, null,
		// null, null, null, null);
		Cursor cursor = sqliteDB
				.rawQuery("SELECT * FROM locationHistory", null);
		while (cursor.moveToNext()) {
			LocationHistory lh = new LocationHistory();
			lh.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			lh.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
			list.add(lh);
		}
		return list;

	}

	public void addWeatherInfo(WeatherInfo info) {
		sqliteDB.execSQL(
				"insert into weatherInfo(cityName,weather_txt,weather_temp,wencha,wind,date,tip,time,humidity,otherContent) values(?,?,?,?,?,?,?,?,?,?)",
				new Object[] { info.getCityName(), info.getWeather_txt(),
						info.getWeather_temp(), info.getWencha(),
						info.getWind(), info.getDate(), info.getTip(),
						info.getTime(), info.getHumidity(),
						info.getOtherContent() });
	}

	public boolean queryWeatherInfoIsExist(String cityName) {
		Cursor c = sqliteDB.rawQuery(
				"select count(*) from weatherInfo where cityName=?",
				new String[] { cityName });
		int sum =0;
		if(c.moveToFirst()){			
			sum = c.getInt(c.getColumnIndex("count(*)"));
		}
		Log.d("debug[queryWeatherInfoIsExist]", "cursor=" + c.toString() + ",\n sum="
				+ sum);
		if (sum > 0) {
			return true;
		}
		return false;
	}

	public void updateWeatherInfo(WeatherInfo info) {
		ContentValues values = new ContentValues();
		values.put("weather_txt", info.getWeather_txt());
		values.put("weather_temp", info.getWeather_temp());
		values.put("wencha", info.getWencha());
		values.put("wind", info.getWind());
		values.put("date", info.getDate());
		values.put("tip", info.getTip());
		values.put("time", info.getTime());
		values.put("humidity", info.getHumidity());
		values.put("otherContent", info.getOtherContent());
		sqliteDB.update("weatherInfo", values, "cityName=?",
				new String[] { info.getCityName() });
	}

	public WeatherInfo queryWeatherInfo(String cityName) {
		Cursor cursor = sqliteDB
				.rawQuery("SELECT * FROM weatherInfo WHERE cityName=?", new String[]{cityName});
		WeatherInfo info = new WeatherInfo();
		if(cursor.moveToFirst()){			
			info.setId(cursor.getInt(cursor.getColumnIndex("_id")));
			info.setCityName(cursor.getString(cursor.getColumnIndex("cityName")));
			info.setWeather_txt(cursor.getString(cursor.getColumnIndex("weather_txt")));
			info.setWeather_temp(cursor.getString(cursor.getColumnIndex("weather_temp")));
			info.setWencha(cursor.getString(cursor.getColumnIndex("wencha")));
			info.setWind(cursor.getString(cursor.getColumnIndex("wind")));
			info.setDate(cursor.getString(cursor.getColumnIndex("date")));
			info.setTip(cursor.getString(cursor.getColumnIndex("tip")));
			info.setTime(cursor.getString(cursor.getColumnIndex("time")));
			info.setHumidity(cursor.getString(cursor.getColumnIndex("humidity")));
		}
		return info;
	}

	public void closeDB() {
		sqliteDB.close();
	}
}
