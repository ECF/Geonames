package org.geonames.timezone;

public interface ITimezoneService {

	Timezone getTimezone(double latitude, double longitude);
	
}
