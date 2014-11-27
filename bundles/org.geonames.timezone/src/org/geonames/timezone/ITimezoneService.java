package org.geonames.timezone;

/**
 * Service interface to represent consumer access to the Geonames timezone service. 
 * This web-service is documented here:
 * http://www.geonames.org/export/web-services.html#timezone
 * @author slewis
 *
 */
public interface ITimezoneService {

	/**
	 * Get a timezone instance, given latitude and longitude values.
	 * 
	 * @param latitude the latitude of the location to get the timezone for
	 * @param longitude the longitude of the location to get the timezone for
	 * @return the Timezone information for the given latitude and longitude.  Should
	 * return <code>null</code> if the latitude or longitude are nonsensical (e.g. negative values)
	 */
	Timezone getTimezone(double latitude, double longitude);
	
}
