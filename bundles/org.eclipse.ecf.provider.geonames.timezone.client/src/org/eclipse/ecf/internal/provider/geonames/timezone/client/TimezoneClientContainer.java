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

	public static final String CONTAINER_TYPE_NAME = "ecf.container.client.geonames.timezone";

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm");
	private static final String USERNAME = System.getProperty(
			"org.eclipse.ecf.provider.geonames.timezone.client.username",
			"demo");

	private IRemoteServiceRegistration tzServiceRegistration;

	public static class Instantiator extends RestClientContainerInstantiator {

		/**
		 * 1. This method is called by the ECF RSA implementation when a remote
		 * service is to be imported. The exporterSupportedConfigs parameter
		 * contains the exported config types associated with the remote
		 * service. The implementation of this method decides whether we are
		 * interested in this remote service config type. If we are
		 * (exporterSupportedConfigs contains CONTAINER_TYPE_NAME, then we
		 * return an array of strings containing our CONTAINER_TYPE_NAME
		 */
		@Override
		public String[] getImportedConfigs(
				ContainerTypeDescription description,
				String[] exporterSupportedConfigs) {
			/**
			 * If the exporterSupportedConfigs contains CONTAINER_TYPE_NAME,
			 * then return that CONTAINER_TYPE_NAME to trigger RSA usage of this
			 * container instantiator
			 */
			if (Arrays.asList(exporterSupportedConfigs).contains(
					CONTAINER_TYPE_NAME))
				return new String[] { CONTAINER_TYPE_NAME };
			return null;
		}

		@Override
		/**
		 * 2. This method is called by the ECF RSA to create a new instance of
		 * the appropriate
		 * container type (aka OSGi config type)
		 */
		public IContainer createInstance(ContainerTypeDescription description,
				Object[] parameters) throws ContainerCreateException {
			return new TimezoneClientContainer();
		}
	}

	/**
	 * 3. This method is called by the ECF RSA implementation to 'connect' to
	 * the targetID. targetID (of appropriate type, in this case TimezoneID) is
	 * created by the RSA implementation and then passed to this method. For the
	 * geonames service, this targetID has value: http://api.geonames.org/ as
	 * given in the ecf.endpoint.id remote service property. See the <a href=
	 * "https://github.com/ECF/Geonames/blob/master/bundles/org.eclipse.ecf.geonames.timezone.consumer.edef/timezoneserviceendpointdescription.xml"
	 * >example EDEF</a>
	 */
	@Override
	public void connect(ID targetID, IConnectContext connectContext1)
			throws ContainerConnectException {
		// Set the connectTargetID in the RestClientContainer super class
		super.connect(targetID, connectContext1);

		// we will use default parameters (for username see parameters below)
		setAlwaysSendDefaultParameters(true);

		/**
		 * Setup the association between the ITimezoneService class and the
		 * Geonames REST remote service. Here is the specification of the <a
		 * href="http://www.geonames.org/export/web-services.html#timezone">
		 * Geonames Timezone service</a>.
		 * <p>
		 * The association is setup by creating and then registering an instance
		 * of IRemoteCallable. The IRemoteCallable specifies both the
		 * association between the automatically constructed ITimezoneService
		 * proxy's method name. In this case: getTimezone -> /timzoneJSON, as
		 * well as the association between the ITimezoneService.getTimezone
		 * proxy's method parameters (i.e. lat and lng in that order) and the
		 * remote timezone service's required URL parameters (lat, lng, and
		 * username).
		 * <p>
		 * We first define the required parameters using a RemoteCallParameter
		 * builder
		 * 
		 */
		RemoteCallParameter.Builder parameterBuilder = new RemoteCallParameter.Builder()
				.addParameter("lat").addParameter("lng")
				.addParameter("username", USERNAME);
		/**
		 * Then we create a callableBuilder instance to associate the
		 * getTimezone method to the path for this service. We also set the
		 * default parameters to the ones we've specified via the
		 * parameterBuilder above, and we define the http request type as 'GET'.
		 */
		RemoteCallable.Builder callableBuilder = new RemoteCallable.Builder(
				"getTimezone", "/timezoneJSON").setDefaultParameters(
				parameterBuilder.build()).setRequestType(
				new HttpGetRequestType());
		/**
		 * Now we register the remote callable. This associates the
		 * ITimezoneService proxy with a correct dynamically constructed URL.
		 * When RSA requests the remote service proxy from this container, the
		 * proxy will have all the necessary code to construct the appropriate
		 * URL and make the appropriate REST request.
		 */
		tzServiceRegistration = registerCallables(ITimezoneService.class,
				new IRemoteCallable[] { callableBuilder.build() }, null);

		/**
		 * In order for the proxy to handle the response from the Geonames
		 * Timezone service, it's necessary to define a response deserializer to
		 * convert the data from the JSON response (or failure/exception
		 * information) to an instance of Timezone for the proxy to return to
		 * the remote service consumer. This is done by defining an
		 * IRemoteResponseDeserializer. When the http response is received, it
		 * is passed to the remote response deserializer to convert from the
		 * response JSON to a Timezone instance that will be returned by the
		 * proxy.
		 * 
		 */
		setResponseDeserializer(new IRemoteResponseDeserializer() {
			@Override
			public Object deserializeResponse(String endpoint,
					IRemoteCall call, IRemoteCallable callable,
					@SuppressWarnings("rawtypes") Map responseHeaders,
					byte[] responseBody) throws NotSerializableException {
				try {
					// Convert responseBody to String and parse using org.json
					// lib
					JSONObject jo = new JSONObject(new String(responseBody));
					// Check status for failure. Throws exception if
					// error status
					if (jo.has("status")) {
						JSONObject status = jo.getJSONObject("status");
						throw new JSONException(status.getString("message")
								+ ";code=" + status.getInt("value"));
					}
					// No exception, so get each of the fields from the
					// json object
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
					// Now create and return Timezone instance with all the
					// appropriate
					// values of the fields
					return new Timezone(countryCode, countryName, lat, lng,
							timezoneId, dstOffset, gmtOffset, rawOffset,
							dateFormat.parse(time), dateFormat.parse(sunrise),
							dateFormat.parse(sunset));
					// If some json parsing exception (badly formatted json and
					// so on,
					// throw an appropriate exception
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
	public void disconnect() {
		super.disconnect();
		// Unregister the tzServiceRegistration upon 'disconnect'
		if (tzServiceRegistration != null) {
			tzServiceRegistration.unregister();
			tzServiceRegistration = null;
		}
	}

	TimezoneClientContainer() {
		// Set this container's ID to a ranmdom UUID
		super(TimezoneNamespace.createUUID());
	}

	@Override
	public Namespace getConnectNamespace() {
		// The required Namespace for this container
		// is the TimezoneNamespace
		return TimezoneNamespace.INSTANCE;
	}

}
