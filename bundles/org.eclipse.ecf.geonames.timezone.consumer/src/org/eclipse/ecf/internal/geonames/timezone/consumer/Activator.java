package org.eclipse.ecf.internal.geonames.timezone.consumer;

import org.geonames.timezone.ITimezoneService;
import org.geonames.timezone.Timezone;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class Activator implements BundleActivator, ServiceTrackerCustomizer<ITimezoneService, ITimezoneService> {

	private static BundleContext context;

	private ServiceTracker<ITimezoneService,ITimezoneService> tracker;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		tracker = new ServiceTracker<ITimezoneService, ITimezoneService>(context,ITimezoneService.class,this);
		tracker.open();
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	@Override
	public ITimezoneService addingService(
			ServiceReference<ITimezoneService> reference) {
		ITimezoneService service = getContext().getService(reference);
		System.out.println("Got ITimezoneService");
		Timezone timezone = service.getTimezone(47.01, 10.2);
		System.out.println("Got timezone="+timezone);
		return service;
	}

	@Override
	public void modifiedService(ServiceReference<ITimezoneService> reference,
			ITimezoneService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removedService(ServiceReference<ITimezoneService> reference,
			ITimezoneService service) {
		// TODO Auto-generated method stub
		
	}

}
