package org.jan.kdweather.db;

/**
 * 城市对应的天气类
 * @author jan
 *
 */
public class WeatherInfo {
	private int id;
	private String cityName;
	private String weather_txt;
	private String weather_temp;
	private String wencha;
	private String wind;
	private String date;
	private String tip;
	private String time;
	private String humidity;
	private String otherContent;
	
	public WeatherInfo(){
		
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
	public String getWeather_txt() {
		return weather_txt;
	}
	public void setWeather_txt(String weather_txt) {
		this.weather_txt = weather_txt;
	}
	public String getWeather_temp() {
		return weather_temp;
	}
	public void setWeather_temp(String weather_temp) {
		this.weather_temp = weather_temp;
	}
	public String getWencha() {
		return wencha;
	}
	public void setWencha(String wencha) {
		this.wencha = wencha;
	}
	public String getWind() {
		return wind;
	}
	public void setWind(String wind) {
		this.wind = wind;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTip() {
		return tip;
	}
	public void setTip(String tip) {
		this.tip = tip;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getOtherContent() {
		return otherContent;
	}
	public void setOtherContent(String otherContent) {
		this.otherContent = otherContent;
	}
	
}
