package org.eclipse.ecf.internal.geonames.timezone.consumer;

import org.geonames.timezone.ITimezoneServiceAsync;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator,
		ServiceTrackerCustomizer<ITimezoneServiceAsync, ITimezoneServiceAsync> {

	private static BundleContext context;

	private ServiceTracker<ITimezoneServiceAsync, ITimezoneServiceAsync> tracker;

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
		tracker = new ServiceTracker<ITimezoneServiceAsync, ITimezoneServiceAsync>(
				context, ITimezoneServiceAsync.class, this);
		tracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		if (tracker != null) {
			tracker.close();
			tracker = null;
		}
		Activator.context = null;
	}

	@Override
	public ITimezoneServiceAsync addingService(
			ServiceReference<ITimezoneServiceAsync> reference) {
		ITimezoneServiceAsync service = getContext().getService(reference);
		System.out.println("Got ITimezoneServiceAsync");
		// Get completable future and when complete
		service.getTimezoneAsync(47.01, 10.2).whenComplete(
				(result, exception) -> {
					// Check for exception and print out
					if (exception != null) {
						System.out.println(exception.getMessage());
						exception.printStackTrace();
					} else
						// Success!
						System.out.println("Received response:  timezone="
								+ result);
				});
		// Report
		System.out.println("Returning ITimezoneServiceAsync");
		return service;
	}

	@Override
	public void modifiedService(
			ServiceReference<ITimezoneServiceAsync> reference,
			ITimezoneServiceAsync service) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removedService(
			ServiceReference<ITimezoneServiceAsync> reference,
			ITimezoneServiceAsync service) {
		// TODO Auto-generated method stub

	}

}
