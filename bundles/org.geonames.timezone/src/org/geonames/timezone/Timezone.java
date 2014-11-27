package org.geonames.timezone;

import java.util.Date;

public class Timezone {

	private String countryCode;
	private String countryName;
	private double latitude;
	private double longitude;
	private String timezoneId;
	private double dstOffset;
	private double gmtOffset;
	private double rawOffset;
	private Date time;
	private Date sunriseTime;
	private Date sunsetTime;
	
	public Timezone(String countryCode, String countryName, double latitude,
			double longitude, String timezoneId, double dstOffset,
			double gmtOffset, double rawOffset, Date time, Date sunriseTime,
			Date sunsetTime) {
		super();
		this.countryCode = countryCode;
		this.countryName = countryName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.timezoneId = timezoneId;
		this.dstOffset = dstOffset;
		this.gmtOffset = gmtOffset;
		this.rawOffset = rawOffset;
		this.time = time;
		this.sunriseTime = sunriseTime;
		this.sunsetTime = sunsetTime;
	}
	
	public String getCountryCode() {
		return countryCode;
	}
	public String getCountryName() {
		return countryName;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public String getTimezoneId() {
		return timezoneId;
	}
	public double getDstOffset() {
		return dstOffset;
	}
	public double getGmtOffset() {
		return gmtOffset;
	}
	public double getRawOffset() {
		return rawOffset;
	}
	public Date getTime() {
		return time;
	}
	public Date getSunriseTime() {
		return sunriseTime;
	}
	public Date getSunsetTime() {
		return sunsetTime;
	}

	@Override
	public String toString() {
		return "Timezone [countryCode=" + countryCode + ", countryName="
				+ countryName + ", latitude=" + latitude + ", longitude="
				+ longitude + ", timezoneId=" + timezoneId + ", dstOffset="
				+ dstOffset + ", gmtOffset=" + gmtOffset + ", rawOffset="
				+ rawOffset + ", time=" + time + ", sunriseTime=" + sunriseTime
				+ ", sunsetTime=" + sunsetTime + "]";
	}
	
}
