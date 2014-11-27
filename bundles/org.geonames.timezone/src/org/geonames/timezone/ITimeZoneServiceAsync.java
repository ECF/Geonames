package org.geonames.timezone;

import java.util.concurrent.CompletableFuture;

public interface ITimeZoneServiceAsync {

	CompletableFuture<TimeZone> getTimeZoneAsync(double latitude, double longitude);
	
}
