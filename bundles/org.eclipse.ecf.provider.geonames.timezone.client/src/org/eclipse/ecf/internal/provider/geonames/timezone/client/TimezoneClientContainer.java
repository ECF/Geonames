package org.eclipse.ecf.internal.provider.geonames.timezone.client;

import java.io.NotSerializableException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.security.IConnectContext;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.IRemoteResponseDeserializer;
import org.eclipse.ecf.remoteservice.client.RemoteCallParameter;
import org.eclipse.ecf.remoteservice.client.RemoteCallable;
import org.eclipse.ecf.remoteservice.rest.client.HttpGetRequestType;
import org.eclipse.ecf.remoteservice.rest.client.RestClientContainer;
import org.eclipse.ecf.remoteservice.rest.client.RestClientContainerInstantiator;
import org.geonames.timezone.ITimezoneService;
import org.geonames.timezone.Timezone;
import org.json.JSONException;
import org.json.JSONObject;

public class TimezoneClientContainer extends RestClientContainer {

	public static final String NAME = "ecf.container.client.geonames.timezone";

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm");
	private static final String USERNAME = System.getProperty(
			"org.eclipse.ecf.provider.geonames.timezone.client.username",
			"demo");

	private IRemoteServiceRegistration tzServiceregistration;

	public TimezoneClientContainer() {
		// Create a random uuid for this container
		super(TimezoneNamespace.createUUID());
		// we will use default parameters
		setAlwaysSendDefaultParameters(true);
		// Setup response remote response deserializer to parse JSON response
		// and return new
		// Timezone instance. The remote response deserializer is called
		// automatically
		// when a http response is received and it's responsible for converting
		// the response
		// to an instance of appropriate type. In this case the return type
		// for the ITimezoneService.getTimeZone() is of type Timezone
		// The JSON returned by the geonames timezone service is specified here
		// http://www.geonames.org/export/web-services.html#timezone
		setResponseDeserializer(new IRemoteResponseDeserializer() {
			@Override
			public Object deserializeResponse(String endpoint,
					IRemoteCall call, IRemoteCallable callable,
					@SuppressWarnings("rawtypes") Map responseHeaders,
					byte[] responseBody) throws NotSerializableException {
				// Convert responseBody to String and parse using JSON lib
				try {
					JSONObject jo = new JSONObject(new String(responseBody));
					// Check status for failure. Throws exception if
					// error status
					if (jo.has("status")) {
						JSONObject status = jo.getJSONObject("status");
						throw new JSONException(status.getString("message")
								+ ";code=" + status.getInt("value"));
					}
					// Get the field
					String countryCode = jo.getString("countryCode");
					String countryName = jo.getString("countryName");
					double lat = jo.getDouble("lat");
					double lng = jo.getDouble("lng");
					String timezoneId = jo.getString("timezoneId");
					double dstOffset = jo.getDouble("dstOffset");
					double gmtOffset = jo.getDouble("gmtOffset");
					double rawOffset = jo.getDouble("rawOffset");
					String time = jo.getString("time");
					String sunrise = jo.getString("sunrise");
					String sunset = jo.getString("sunset");

					return new Timezone(countryCode, countryName, lat, lng,
							timezoneId, dstOffset, gmtOffset, rawOffset,
							dateFormat.parse(time), dateFormat.parse(sunrise),
							dateFormat.parse(sunset));

				} catch (Exception e) {
					NotSerializableException ex = new NotSerializableException(
							"Problem in response from timezone service endpoint="
									+ endpoint + " status message: "
									+ e.getMessage());
					ex.setStackTrace(e.getStackTrace());
					throw ex;
				}
			}
		});
	}

	@Override
	public Namespace getConnectNamespace() {
		return TimezoneNamespace.INSTANCE;
	}

	@Override
	public void connect(ID targetID, IConnectContext connectContext1)
			throws ContainerConnectException {
		super.connect(targetID, connectContext1);
		// The parameter names and path specified by the Geonames Timezone
		// service
		// documentation at
		// http://www.geonames.org/export/web-services.html#timezone
		RemoteCallParameter.Builder parameterBuilder = new RemoteCallParameter.Builder();
		// setup the parameter names as specified by the Geonames Timezone
		// service documentation at
		// http://www.geonames.org/export/web-services.html#timezone
		parameterBuilder.addParameter("lat").addParameter("lng")
				.addParameter("username", USERNAME);
		// setup the association between the method name and the
		// service path as specified by the Geonames Timezone
		// service documentation
		RemoteCallable.Builder callableBuilder = new RemoteCallable.Builder(
				"getTimezone", "/timezoneJSON");
		// Set default parameters from parameterBuilder
		callableBuilder.setDefaultParameters(parameterBuilder.build());
		// Set request type to http get
		callableBuilder.setRequestType(new HttpGetRequestType());
		// This registration associated the getTimezone method in the
		// ITimezoneService proxy
		// with the get call to the /timezoneJSON service, along with the
		// callParams
		// created above
		tzServiceregistration = registerCallables(ITimezoneService.class,
				new IRemoteCallable[] { callableBuilder.build() },
				null);
	}

	@Override
	public void disconnect() {
		super.disconnect();
		if (tzServiceregistration != null) {
			tzServiceregistration.unregister();
			tzServiceregistration = null;
		}
	}

	public static class Instantiator extends RestClientContainerInstantiator {

		@Override
		public IContainer createInstance(ContainerTypeDescription description,
				Object[] parameters) throws ContainerCreateException {
			// Return new TimezoneClientContainer
			return new TimezoneClientContainer();
		}

		@Override
		public String[] getImportedConfigs(
				ContainerTypeDescription description,
				String[] exporterSupportedConfigs) {
			// If the exporterSupportedConfigs contains NAME, then
			// return that NAME to trigger RSA usage of this container
			// instantiator
			if (Arrays.asList(exporterSupportedConfigs).contains(NAME))
				return new String[] { NAME };
			return null;
		}
	}
}
