package org.avm.business.parser.management.bundle;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.avm.business.parser.management.ParserImpl;
import org.avm.elementary.parser.Parser;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;

public class Activator implements Parser {

	static final String PID = ParserImpl.class.getName();

	private Logger _log;

	private ComponentContext _context;

	private ParserImpl _peer;

	public Activator() {
		_log = Logger.getInstance(this.getClass());
	}

	private void initializeParser() {
		try {
			_log.info("initialize parser...");
			BundleContext bc = _context.getBundleContext();
			Bundle b = bc.getBundle();
			Enumeration it = b
					.findEntries("/", "protocol-management.jar", true);
			URL url = null;
			while (it.hasMoreElements()) {
				url = (URL) it.nextElement();
			}
			_peer = new ParserImpl(url);
			_log.info("parser initialized.");
		} catch (Throwable t) {
			_log.error(t);
		}

	}

	protected void activate(ComponentContext context) {
		_log.debug("Components activated");
		_context = context;
		initializeParser();
	}

	protected void deactivate(ComponentContext context) {
		_log.debug("Component deactivated");
	}

	public String getProtocolName() {
		if (_peer == null)
			return null;
		return _peer.getProtocolName();
	}

	public int getProtocolId() {
		if (_peer == null)
			return -1;
		return _peer.getProtocolId();
	}

	public Object get(InputStream in) throws Exception {
		if (_peer == null)
			throw new Exception();
		return _peer.get(in);
	}

	public void put(Object n, OutputStream out) throws Exception {
		if (_peer == null)
			throw new Exception();
		_peer.put(n, out);
	}

	public void marshal(Object n, OutputStream out) throws Exception {
		if (_peer == null)
			throw new Exception();
		_peer.marshal(n, out);

	}

	public Object unmarshal(InputStream in) throws Exception {
		if (_peer == null)
			throw new Exception();
		return _peer.unmarshal(in);
	}
}
