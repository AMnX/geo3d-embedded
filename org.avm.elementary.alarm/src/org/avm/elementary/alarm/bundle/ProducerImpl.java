package org.avm.elementary.alarm.bundle;

import org.avm.elementary.alarm.Alarm;
import org.avm.elementary.common.AbstractProducer;
import org.osgi.service.component.ComponentContext;


public class ProducerImpl extends AbstractProducer {

	public ProducerImpl(ComponentContext context) {
		super(context);
	}

	protected String getProducerPID() {
		return Activator.getDefault().getPid();
	}

	protected Class[] getProducerFlavors() {
		Class[] result = new Class[] {  Alarm.class };
		return result;
	}
}
