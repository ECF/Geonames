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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		// Register an instance of TimezoneNamespace
		bundleContext.registerService(Namespace.class, new TimezoneNamespace(),
				null);
		// Register an instance of TimezoneContainerTypeDescription (see below)
		bundleContext.registerService(ContainerTypeDescription.class,
				new TimezoneContainerTypeDescription(), null);
	}

	class TimezoneContainerTypeDescription extends ContainerTypeDescription {
		public TimezoneContainerTypeDescription() {
			super(TimezoneClientContainer.NAME,
					new TimezoneClientContainer.Instantiator(),
					"Timezone Remote Service Container");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
