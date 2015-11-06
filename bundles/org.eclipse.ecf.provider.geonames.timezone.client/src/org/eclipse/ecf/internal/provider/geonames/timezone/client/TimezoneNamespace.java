package org.eclipse.ecf.internal.provider.geonames.timezone.client;

import java.net.URI;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.remoteservice.rest.identity.RestID;

public class TimezoneNamespace extends Namespace {

	public static final String NAME = "ecf.geonames.timezone.namespace";
	public static TimezoneNamespace INSTANCE;

	private static final long serialVersionUID = -428290464908414596L;

	public TimezoneNamespace() {
		super(NAME, "Geonames Timezone Namespace");
		INSTANCE = this;
	}

	@Override
	public ID createInstance(Object[] parameters) throws IDCreateException {
		try {
			return new RestID(TimezoneNamespace.INSTANCE,URI.create((String) parameters[0]));
		} catch (Exception e) {
			throw new IDCreateException("Could not create rest ID", e); //$NON-NLS-1$
		}
	}

	public static RestID createUUID() throws IDCreateException {
		return new RestID(INSTANCE,
				URI.create("uuid:" + java.util.UUID.randomUUID().toString()));
	}

	@Override
	public String getScheme() {
		return "ecf.geonames.timezone";
	}

}
