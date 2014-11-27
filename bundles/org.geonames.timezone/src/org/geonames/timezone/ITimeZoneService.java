package org.geonames.timezone;

public interface ITimeZoneService {

	TimeZone getTimeZone(double latitude, double longitude);
	
}
