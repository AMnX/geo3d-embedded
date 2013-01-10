package org.avm.device.knet.io;

import java.util.BitSet;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.avm.device.io.DigitalIODevice;
import org.avm.device.io.DigitalIODriver;
import org.avm.device.knet.sensor.Sensor;
import org.avm.device.knet.sensor.SensorInjector;
import org.avm.elementary.common.PropertyChangeListener;
import org.avm.elementary.common.PropertyChangeSupport;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.util.tracker.ServiceTracker;

public class KnetDigitalOutputService extends ServiceTracker implements
		DigitalIODriver, SensorInjector {

	private Logger _log = Logger.getInstance(this.getClass());

	private DigitalIODevice _device;

	private BitSet _data = new BitSet();

	private ServiceRegistration _sr;

	private ComponentContext _context;

	private PropertyChangeSupport _listeners;

	private Sensor _knetSensor;

	public KnetDigitalOutputService(ComponentContext context,
			ServiceReference device) {
		super(context.getBundleContext(), device, null);
		_context = context;
		for (int i = 0; i < _data.size(); i++) {
			_data.clear(i);
		}
		_listeners = new PropertyChangeSupport(this);
		open();
		// _log.setPriority(Priority.DEBUG);
	}

	public int getCapability() {
		return _data.size();
	}

	public boolean getValue(int index) {
		if (index >= _data.size())
			throw new IndexOutOfBoundsException();
		boolean value = _data.get(index);
		_log.debug("I/O[" + index + "] = " + value);
		return value;
	}

	public void setValue(int index, boolean value) {
		// throw new UnsupportedOperationException();

		if (index >= _data.size())
			throw new IndexOutOfBoundsException();

		boolean oldValue = _data.get(index);

		if (value)
			_data.set(index);
		else
			_data.clear(index);

		_knetSensor.beep(1);

		_log.debug("I/O[" + index + "] = " + _data.get(index));

		_listeners.fireIndexedPropertyChange(null, index, oldValue, value);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		_listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		_listeners.removePropertyChangeListener(listener);
	}

	public Object addingService(ServiceReference reference) {
		Object o = super.addingService(reference);
		if (o instanceof DigitalIODevice) {
			_device = (DigitalIODevice) o;
			_device.setDriver(this);

			_log.debug("registerService(" + this.getClass().getName() + "...)");
			_sr = context.registerService(this.getClass().getName(), this,
					new Properties());
		}

		return o;
	}

	public void removedService(ServiceReference reference, Object service) {
		super.removedService(reference, service);
		if (service instanceof DigitalIODevice) {
			// stop tache d'aquisition

			_device.setDriver(null);
			_sr.unregister();
			_device = null;
		}
	}

	public void setSensor(Sensor sensor) {
		_log.debug("setSensor(" + sensor + ")");
		_knetSensor = sensor;
	}
}
