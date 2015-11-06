package org.eclipse.ecf.internal.provider.geonames.timezone.client;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.identity.Namespace;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// Register an instance of TimezoneNamespace
		bundleContext.registerService(Namespace.class, new TimezoneNamespace(),
				null);
		// Register an instance of ContainerTypeDescription
		bundleContext.registerService(ContainerTypeDescription.class,
				new ContainerTypeDescription(TimezoneClientContainer.CONTAINER_TYPE_NAME,
						new TimezoneClientContainer.Instantiator(),
						"Geonames Timezone Remote Service Client Container"), null);
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
