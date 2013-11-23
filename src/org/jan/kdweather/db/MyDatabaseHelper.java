package org.jan.kdweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
	final String CREATE_TABLE_SQL = "create table locationHistory(_id integer primary key autoincrement ,cityName varchar)";
	final String CREATE_TABLE_SQL_WEATHERINFO = "create table weatherInfo(_id integer primary key autoincrement ,cityName varchar,weather_txt varchar,weather_temp varchar ,wencha,wind,date,tip,time,humidity,otherContent)";
	private static final int VERSION = 1;

	public MyDatabaseHelper(Context context, String name) {
		super(context, name, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_SQL);
		db.execSQL(CREATE_TABLE_SQL_WEATHERINFO);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("alter table locationHistory add column weather string");
	}

}
