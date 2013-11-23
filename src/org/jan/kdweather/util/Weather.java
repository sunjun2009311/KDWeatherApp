package org.jan.kdweather.util;

/**
 * 天气预报的未来5天趋势（包括今日）
 * 
 * @author jan
 * 
 */
public class Weather {
	private String date;
	private String weather;
	private String temperature;
	private String windDirection;
	private String icon_1;
	private String icon_2;

	public Weather() {

	}

	public Weather(String d, String w, String tem, String wind, String s1,
			String s2) {
		this.date = d;
		this.weather = w;
		this.temperature = tem;
		this.windDirection = wind;
		this.icon_1 = s1;
		this.icon_2 = s2;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}

	public String getIcon_1() {
		return icon_1;
	}

	public void setIcon_1(String icon_1) {
		this.icon_1 = icon_1;
	}

	public String getIcon_2() {
		return icon_2;
	}

	public void setIcon_2(String icon_2) {
		this.icon_2 = icon_2;
	}

	@Override
	public String toString() {
		return date + "|" + weather + "|" + temperature + "|" + windDirection
				+ "|" + icon_1 + "|" + icon_2 + "|";
	}

}
