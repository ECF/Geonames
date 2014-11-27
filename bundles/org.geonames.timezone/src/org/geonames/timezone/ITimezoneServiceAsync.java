package org.geonames.timezone;

import java.util.concurrent.CompletableFuture;

public interface ITimezoneServiceAsync {

	CompletableFuture<Timezone> getTimezoneAsync(double latitude, double longitude);
	
}
