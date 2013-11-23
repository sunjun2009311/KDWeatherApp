package org.jan.kdweather.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
/**
 * ≥« –¿‡
 * @author jan
 *
 */
public class LocationHistory implements Parcelable {
	private int id;
	private String cityName;
	public LocationHistory(){}
	public LocationHistory(int id,String cityname){
		this.id=id;
		this.cityName=cityname;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Log.d("LocationHistory.debug", "writeToParcel");
		dest.writeInt(id);
		dest.writeString(cityName);
	};
	public static final Parcelable.Creator<LocationHistory> CREATOR = new Creator<LocationHistory>() {
		
		@Override
		public LocationHistory[] newArray(int size) {
			return new LocationHistory[size];
		}
		
		@Override
		public LocationHistory createFromParcel(Parcel source) {
			return new LocationHistory(source.readInt(), source.readString());
		}
	};
}
