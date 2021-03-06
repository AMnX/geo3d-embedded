package org.avm.elementary.management.addons.wifi;

import java.io.PrintWriter;

import org.apache.log4j.Logger;
import org.avm.elementary.management.addons.ManagementService;
import org.osgi.util.measurement.State;

/**
 * @author MERCUR : Didier LALLEMAND
 * 
 *         To change this generated comment edit the template variable
 *         "typecomment": Window>Preferences>Java>Templates. To enable and
 *         disable the creation of type comments go to
 *         Window>Preferences>Java>Code Generation.
 * 
 */
public class WifiManagementImpl implements WifiManagement {

	private Logger _logger;

	private ManagementService _management;

	private org.avm.elementary.log4j.Logger _syslog;

	public WifiManagementImpl(ManagementService management) {
		_logger = Logger.getInstance(this.getClass());
		_management = management;
	}

	public void notify(Object o) {
		State state = (State) o;
		boolean isconnected = state.getValue() > 0;
		_logger.info("WifiStatut : "
				+ ((isconnected) ? "connected" : "disconnected"));

		try {
			_management.setPrivateMode(isconnected);
			if (isconnected) {
				_management.synchronize(new PrintWriter(System.out), true);
			}
		} catch (Exception e) {
			_logger.error("Wifi/management synchronization failed.", e);
		}
		if (!isconnected) {
			if (_syslog != null) {
				_syslog.disableSyslog();
			}
		}
	}

	public void setSyslog(org.avm.elementary.log4j.Logger syslog) {
		_syslog = syslog;
	}
}