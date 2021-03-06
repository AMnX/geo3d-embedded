package org.avm.elementary.management.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.GZIPOutputStream;

import org.avm.elementary.management.core.utils.DataUploadClient;
import org.avm.elementary.management.core.utils.Terminal;
import org.avm.elementary.management.core.utils.Utils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.service.startlevel.StartLevel;

/**
 * 
 * @author didier.lallemand@cityway.fr
 * 
 */
public class ManagementImpl implements Management {
	private static final String REPORT_FILENAME = "update.log.gz";

	private static final int DELAY_BETWEEN_UPDATE = 5;

	private static int MAX_RETRY;

	private static final String AUTOUPDATE_TAG = Management.class.getPackage()
			.getName() + ".autoupdate";

	private int _retry = 1;

	private BundleContext _context;

	private SynchronizeBundleThread _synchronizeBundlesThread;

	private Management _instance;

	private StartLevel _startLevelService = null;

	private PackageAdmin _packageAdminService = null;

	private URL _downloadURL;

	private URL _uploadURL;

	private Timer _shutdownTimer;

	private ServiceRegistration _sr;

	private int currentMode;

	/**
	 * @param context
	 * 
	 */
	public ManagementImpl() {
		_instance = this;
	}

	public void debug(String trace) {
		if (DEBUG) {
			System.out.println("[ManagementCore] " + trace);
		}
	}

	public void error(String trace) {
		System.err.println("[ManagementCore] " + trace);
	}

	public void start() {
		boolean update = System.getProperty(AUTOUPDATE_TAG, "true").equals(
				"true");
		init();
		debug("registering management core service...");
		_sr = _context.registerService(Management.class.getName(), this, null);
		debug("management core service " + _sr + " registered.");
		debug("starting synchronize...");
		try {
			synchronize(update, new PrintWriter(System.out), false);
			debug("synchronize done.");
		} catch (Exception e) {
			error("synchronize failed!");
			e.printStackTrace();
		}
	}

	public void stop() {
		unsynchronize();
		debug("deactivating...");
		if (_sr != null) {
			_sr.unregister();
			_sr = null;
		}
	}

	public void setBundleContext(BundleContext context) {
		_context = context;
		updateVersion();
	}

	private long getBundleVersionDate(String version) {
		long result = -1;
		StringBuffer buf = new StringBuffer();
		int idx = version.lastIndexOf(".");
		if (idx != -1) {
			buf.append(version.substring(idx + 1));
			while (buf.length() < 14) {
				buf.append("0");
			}
			try {
				result = Long.parseLong(buf.toString());
			} catch (Throwable t) {

			}
		}

		return result;
	}

	private void updateVersion() {
		long ver = 0;
		String version = "";

		for (int i = 0; i < _context.getBundles().length; i++) {
			Bundle bundle = _context.getBundles()[i];
			String vendor = (String) bundle.getHeaders().get("Bundle-Vendor");
			if (vendor != null && vendor.toLowerCase().indexOf("cityway") != -1) {
				String sver = (String) bundle.getHeaders()
						.get("Bundle-Version");
				long v = getBundleVersionDate(sver);
				if ((bundle.getSymbolicName().indexOf("org.angolight") != -1 || bundle
						.getSymbolicName().indexOf("org.avm") != -1)
						&& !bundle.getSymbolicName().endsWith((".data"))) {
					if (v > ver) {
						ver = v;
						version = Long.toString(v);
					}

				}
			}
		}
		if (version != null) {
			System.setProperty(AVMVERSION_TAG, version);
		}

	}

	private void updateManagementProperties(String filename) {
		String filepath = System.getProperty(AVMHOME_TAG)
				+ System.getProperty("file.separator") + "bin"
				+ System.getProperty("file.separator") + filename;
		Properties p = Utils.loadProperties(filepath);
		boolean changed = checkAvmProperties(p);
		changed = updateWithSystemProperties(p) || changed;
		if (changed) {
			debug("Properties for " + filename + " changed.");
			try {
				Utils.saveProperties(p, filepath);
				Terminal.getInstance().save();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void init() {
		debug("Init management...");

		// -- load property files
		String filename = "avm.properties";
		updateManagementProperties(filename);

		filename = "management.properties";
		updateManagementProperties(filename);

		debug("Terminal Info: " + Terminal.getInstance());

		// -- set default download & upload URL
		String tmp = System.getProperty(UPDATE_MODE_TAG);
		if (tmp == null) {
			currentMode = MODE_PRIVATE;
			tmp = "private";
			debug("default mode not set : using private");
		} else {
			currentMode = tmp.equals("public") ? MODE_PUBLIC : MODE_PRIVATE;
		}
		debug("default mode is : " + tmp);

		debug("Check packages...");

		// -- check package and refresh if necessairy
		PrintWriter out = new PrintWriter(System.out);
		if (checkBundlePackages(_context, out, null) == false) {
			out.println("!!!!!!!!!!!!!!!!!!!!    WARNING    !!!!!!!!!!!!!!!!!!!!!!!!");
			out.println("!              Packages failure detected                  !");
			out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
			debug("Packages failure detected!");
			Bundle[] bundles = new Bundle[_context.getBundles().length - 1];
			int j = 0;
			for (int i = 0; i < _context.getBundles().length; i++) {
				Bundle bundle = _context.getBundles()[i];
				// -- refresh all bundles, except management
				if (!bundle.getSymbolicName().equals(
						Management.class.getPackage().getName())) {
					bundles[j] = bundle;
					j++;
				}
			}
			refresh(bundles, out);
		} else {
			out.println("Packages seem ok.");
		}
		debug("Init finished...");
	}

	// private void checkMode() {
	// boolean lastModePrivate = System.getProperty(
	// LAST_UPDATE_IN_PRIVATE_ZONE_TAG, "false").equals("true");
	// if (lastModePrivate == true) {
	// // -- derniere mise a jour effectuee par WiFi (private) ; donc on
	// // tente a nouveau...
	// currentMode = MODE_PRIVATE;
	// debug("Last update in private zone, retry (mode=private)");
	// } else {
	// // -- recuperation du mode de mise a jour par defaut. si rien est
	// // defini alors 'private'
	// String tmp = System.getProperty(UPDATE_MODE_TAG);
	// if (tmp == null) {
	// currentMode = MODE_PRIVATE;
	// debug("default mode not set : using private");
	// } else {
	// currentMode = tmp.equals("public") ? MODE_PUBLIC : MODE_PRIVATE;
	//
	// debug("default mode is : " + currentMode);
	// }
	// }
	//
	// }

	public String getUploadUrl(int mode) {
		String url = null;
		switch (mode) {

		case MODE_PUBLIC: {
			url = System.getProperty(PUBLIC_UPLOAD_URL_TAG,
					DEFAULT_PUBLIC_UPLOAD_URL);
		}
			break;
		case MODE_PRIVATE: {
			url = System.getProperty(PRIVATE_UPLOAD_URL_TAG,
					DEFAULT_PRIVATE_UPLOAD_URL);

		}
			break;
		case MODE_BYUSER: {
			if (_uploadURL == null) {
				url = getUploadUrl(MODE_DEFAULT);
			} else {
				url = _uploadURL.toString();
			}
		}
			break;
		default: {

			String tmp = System.getProperty(UPDATE_MODE_TAG);
			if (tmp == null) {
				url = getUploadUrl(MODE_PRIVATE);
				debug("default mode not set : using private");
			} else {
				int m = tmp.equals("public") ? MODE_PUBLIC : MODE_PRIVATE;
				if (!tmp.equals("public")) {
					tmp = "private";
				}
				url = getUploadUrl(m);
				debug("default mode is : " + tmp);
			}
		}
			break;
		}
		return url;
	}

	// private String getDefaultUploadUrl() {
	// checkMode();
	// String url = getUploadUrl(currentMode);
	// return url;
	// }

	public String getDownloadUrl(int mode) {

		String url = null;
		switch (mode) {
		case MODE_PUBLIC: {
			url = System.getProperty(PUBLIC_DOWNLOAD_URL_TAG,
					DEFAULT_PUBLIC_DOWNLOAD_URL);
		}
			break;
		case MODE_PRIVATE: {
			url = System.getProperty(PRIVATE_DOWNLOAD_URL_TAG,
					DEFAULT_PRIVATE_DOWNLOAD_URL);
		}
			break;
		case MODE_BYUSER: {
			if (_downloadURL == null) {
				url = getDownloadUrl(MODE_DEFAULT);
			} else {
				url = _downloadURL.toString();
			}
		}
			break;
		default: {
			String tmp = System.getProperty(UPDATE_MODE_TAG);
			if (tmp == null) {
				url = getDownloadUrl(MODE_PRIVATE);
				debug("default mode not set : using private");
			} else {
				int m = tmp.equals("public") ? MODE_PUBLIC : MODE_PRIVATE;
				url = getDownloadUrl(m);
				debug("default mode is : " + m);
			}
		}
		}
		return url;
	}

	// private String getDefaultDownloadUrl() {
	//
	// checkMode();
	// String url = getDownloadUrl(currentMode);
	//
	// return url;
	// }

	public void setUploadUrl(URL url) throws MalformedURLException {
		currentMode = MODE_BYUSER;
		_uploadURL = url;
	}

	public void setDownloadUrl(URL url) throws MalformedURLException {

		currentMode = MODE_BYUSER;
		_downloadURL = url;
	}

	public void synchronize(PrintWriter out, boolean force) throws Exception {
		synchronize(true, out, force);
	}

	public void synchronize(PrintWriter out) throws Exception {
		synchronize(true, out, false);
	}

	public void synchronize(boolean update, PrintWriter out, boolean force)
			throws Exception {
		_retry = 1;
		MAX_RETRY = Integer.parseInt(System.getProperty(Management.class
				.getPackage().getName() + ".autoupdate.maxtry", "5"));
		if (_synchronizeBundlesThread == null
				|| _synchronizeBundlesThread.isAlive() == false) {

			_synchronizeBundlesThread = new SynchronizeBundleThread(out);
			_synchronizeBundlesThread.start();
		} else {

			if (force == false) {
				throw new Exception(
						"Management synchronisation already in progress...");
			} else {
				out.print("Try to restart synchronisation process...");
				_synchronizeBundlesThread.stop();
				if (_synchronizeBundlesThread == null
						|| _synchronizeBundlesThread.isAlive() == false) {

					_synchronizeBundlesThread = new SynchronizeBundleThread(out);
					_synchronizeBundlesThread.start();
				}
			}
		}
	}

	public void unsynchronize() {
		if (_synchronizeBundlesThread != null) {
			_synchronizeBundlesThread.stop();
			_synchronizeBundlesThread = null;
		}
	}

	/**
	 * Demarre tous les bundles
	 * 
	 * @param out
	 */
	public void startAllBundles(PrintWriter out) {
		Bundle[] bundles = _context.getBundles();

		for (int i = 0; i < bundles.length; i++) {
			Bundle b = bundles[i];
			try {
				boolean fragment = (b.getHeaders().get("Fragment-Host") != null);
				if (fragment) {
					out.println("[WARNING] Bundle " + b.getSymbolicName()
							+ " is a fragment (will not be started)");
				} else if (b.getState() != Bundle.STARTING
						&& b.getState() != Bundle.ACTIVE) {
					b.start();
				}
			} catch (Throwable t) {
				out.println("failed (" + t.getLocalizedMessage() + ")");
			}
		}

	}

	class SynchronizeBundleThread implements Runnable {

		private Thread _thread = null;

		private PrintWriter _out = null;

		private boolean interrupted = false;

		public SynchronizeBundleThread(PrintWriter out) {
			_out = out;
		}

		public void start() {
			if (_thread == null) {
				_thread = new Thread(this);
				_thread.setName("[AVM] management core");
				interrupted = false;
				_thread.start();
			}
		}

		public void stop() {
			if (_thread != null) {
				_thread.interrupt();
				interrupted = true;
				_thread = null;
			}
		}

		public boolean isAlive() {
			if (_thread != null) {
				return _thread.isAlive();
			} else {
				return false;
			}
		}

		private void debug(String trace) {
			if (DEBUG) {
				_out.println("[ManagementCore] " + trace);
			}
		}

		public void run() {

			if (getCurrentDownloadUrl() == null) {
				_out.println("Download URL is null : update operation is cancelled.");
				return;
			}

			String m = "";
			if (currentMode == Management.MODE_BYUSER) {
				m = "*user*";
			} else {
				m = (currentMode == Management.MODE_PRIVATE ? "private"
						: "public");
			}

			System.out.println("Update mode  : " + m + " ("
					+ getCurrentDownloadUrl() + ")");
			_out.println("Update mode  : " + m);
			_out.println("Download url : " + getCurrentDownloadUrl());
			_out.println("Upload url   : " + getCurrentUploadUrl());
			_out.flush();

			boolean success = true;
			while (_retry <= MAX_RETRY && _thread.isInterrupted() == false) {
				debug("********************** " + _retry + "/" + MAX_RETRY
						+ " **********************");
				try {
					long t0 = System.currentTimeMillis();
					SynchronizeBundlesCommand cmd = new SynchronizeBundlesCommand(
							_context, _instance, _out);
					cmd.exec();
					long time = System.currentTimeMillis() - t0;
					debug("Update [done] (" + time + " ms.)");
					success = true;
					break;
				} catch (IOException ioe) {
					_out.println("[ERROR] Update IOException : "
							+ ioe.getMessage());
					try {
						debug("Wait " + DELAY_BETWEEN_UPDATE + "s...");
						Thread.sleep(DELAY_BETWEEN_UPDATE * 1000);
					} catch (InterruptedException e) {
						break;
					}
					success = false;
					_retry++;
				}

			}

			if (success == false) {
				_out.println("Update failure => ignore last update in private zone");
				System.setProperty(LAST_UPDATE_IN_PRIVATE_ZONE_TAG, "false");
				String filename = "management.properties";
				updateManagementProperties(filename);
			}

			if (interrupted == false) {
				updateVersion();
				startAllBundles(_out);
			}
			_out.flush();

			_synchronizeBundlesThread = null;
		}
	}

	private boolean updateWithSystemProperties(Properties p) {
		Enumeration e = p.keys();
		boolean changed = false;
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = p.getProperty(key);
			String currentValue = System.getProperty(key);
			if (currentValue != null && !currentValue.equals(value)) {
				changed = true;
				p.setProperty(key, currentValue);
			} else {
				System.setProperty(key, value);
			}
		}
		return changed;
	}

	private boolean checkAvmProperties(Properties p) {
		boolean mustBeSaved = false;

		String vehiculeId = p.getProperty(Terminal.VEHICULE_PROPERTY); // -- old
																		// version
		if (vehiculeId != null) {
			System.setProperty(Terminal.TERMINAL_NAME_PROPERTY, vehiculeId);
			p.remove(Terminal.VEHICULE_PROPERTY);
			mustBeSaved = true;
		}
		String exploitantId = p.getProperty(Terminal.EXPLOITATION_PROPERTY);
		if (exploitantId != null) {
			System.setProperty(Terminal.TERMINAL_OWNER_PROPERTY, exploitantId);
			p.remove(Terminal.EXPLOITATION_PROPERTY);
			mustBeSaved = true;
		}
		return mustBeSaved;
	}

	public StartLevel getStartLevelService() {
		ServiceReference sr = _context.getServiceReference(StartLevel.class
				.getName());
		_startLevelService = (StartLevel) _context.getService(sr);
		return _startLevelService;
	}

	public PackageAdmin getPackageAdminService() {
		ServiceReference sr = _context.getServiceReference(PackageAdmin.class
				.getName());
		_packageAdminService = (PackageAdmin) _context.getService(sr);
		return _packageAdminService;
	}

	protected void refresh(Bundle[] bundles, PrintWriter out) {
		try {
			getPackageAdminService().refreshPackages(bundles);
			getPackageAdminService().resolveBundles(bundles);
		} catch (Throwable t) {
			out.println("[ManagementCore] Error on refresh : " + t.getMessage());
		}
	}

	protected boolean checkBundlePackages(BundleContext context,
			PrintWriter out, Bundle bundle) {
		org.osgi.framework.ServiceReference packageAdminRef = context
				.getServiceReference("org.osgi.service.packageadmin.PackageAdmin"); //$NON-NLS-1$
		boolean result = true;
		long t0 = System.currentTimeMillis();
		if (packageAdminRef != null) {
			org.osgi.service.packageadmin.PackageAdmin packageAdmin = (org.osgi.service.packageadmin.PackageAdmin) context
					.getService(packageAdminRef);
			if (packageAdmin != null) {
				try {
					org.osgi.service.packageadmin.ExportedPackage exportedpkgs[] = packageAdmin
							.getExportedPackages(bundle);
					if (exportedpkgs != null) {
						for (int i = 0; i < exportedpkgs.length; i++) {
							org.osgi.service.packageadmin.ExportedPackage exportedpkg = exportedpkgs[i];
							exportedpkg.getImportingBundles(); // Exception
							// lorsque pb
							// package !!!
						}
					}
				} catch (Throwable t) {
					result = false;
				}
			}
		}
		out.println("Time to check packages : "
				+ (System.currentTimeMillis() - t0) + "ms.");

		return result;
	}

	public void shutdown(PrintWriter out, int waittime, int exitCode) {
		try {
			if (_shutdownTimer == null) {
				out.println("Shutdown in " + waittime + " secondes.");
				_shutdownTimer = new Timer();
				_shutdownTimer.schedule(new ShutdownTimerTask(exitCode, out),
						waittime * 1000);
			}

			if (waittime == -1 && _shutdownTimer != null) {
				out.println("Cancel shutdown secondes.");
				_shutdownTimer.cancel();
				_shutdownTimer = null;
			}
		} catch (Exception e) {
			out.println("ERROR: " + e.getMessage());
		}
	}

	class ShutdownTimerTask extends TimerTask {
		PrintWriter _out;
		private int _exitCode;

		public ShutdownTimerTask(int exitCode, PrintWriter out) {
			_out = out;
			_exitCode = exitCode;
		}

		public void run() {
			_out.println("Shutdown...");
			halt(_exitCode);
		}

		private void halt(final int exitCode) {
			BundleListener listener = new BundleListener() {
				public void bundleChanged(BundleEvent event) {
					Bundle[] bundles = _context.getBundles();
					int active = 0;
					for (int i = 0; i < bundles.length; i++) {
						if (bundles[i].getState() == Bundle.ACTIVE) {
							active++;
						}
					}
					ManagementImpl.this
							.debug("[ManagementCore] Active bundles : "
									+ active);
					if (active == 2) { // Bundle SystemOsgi + Bundle Management
						_out.println("bye bye...");
						System.exit(exitCode);
					}

					try {
						Thread.sleep(5 * 1000);
						_out.println("force exit...");
						System.exit(exitCode);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			};
			_context.addBundleListener(listener);
			int managementStartLevel = getManagementStartLevel();
			getStartLevelService().setStartLevel(managementStartLevel);
		}
	}

	private int getManagementStartLevel() {
		Bundle bundle = _context.getBundle();
		return getStartLevelService().getBundleStartLevel(bundle);
	}

	class PropertyFileFilter implements FilenameFilter {
		public boolean accept(File dir, String filename) {
			return filename.toLowerCase().endsWith(".properties");
		}
	}

	private BundleList createFromFwk() {
		BundleList bundleList = new BundleList();

		Bundle[] bundles = _context.getBundles();
		for (int i = 1; i < bundles.length; i++) {
			BundleProperties bp = new BundleProperties();
			String bundleName = bundles[i].getSymbolicName();
			String bundleVersion = (String) bundles[i].getHeaders().get(
					"Bundle-Version");
			String packName = (String) bundles[i].getHeaders().get("TAB-Pack");
			bundleList.put(bundleName, bp);
			bp.setSymbolicName(bundleName);
			bp.setVersion(bundleVersion);
			bp.setPack(packName);

			bp.setStartlevel(getStartLevelService().getBundleStartLevel(
					bundles[i]));

		}
		return bundleList;
	}

	public void sendBundleList(int mode) throws Exception {

		BundleList bundleList = createFromFwk();
		Enumeration enumeration = bundleList.elements();
		String filepattern = "$e_$v_$i_" + REPORT_FILENAME;

		String path = getUploadUrl(mode);

		String surl = Utils.formatURL(path, false);
		String filename = Utils.formatURL(filepattern, false);

		File file = new File(System.getProperty("java.io.tmpdir") + "/"
				+ filename);
		GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(
				file.getAbsoluteFile()));
		// Transfer bytes from the input file to the GZIP output stream
		while (enumeration.hasMoreElements()) {
			BundleProperties element = (BundleProperties) enumeration
					.nextElement();
			String line = element.toString()
					+ System.getProperty("line.separator");
			byte[] data = line.getBytes();
			out.write(data, 0, data.length);
		}

		// Complete the GZIP file
		out.finish();
		out.close();

		debug("Sending update report (" + file.getName() + ")");
		DataUploadClient client = new DataUploadClient(new URL(surl));
		client.put(file, filename);
		debug("OK Sent " + file.getName() + "");

	}

	public void setCurrentMode(int mode) throws MalformedURLException {
		currentMode = mode;
	}

	public int getCurrentMode() {
		return currentMode;
	}

	public URL getCurrentUploadUrl() {
		try {
			return new URL(getUploadUrl(currentMode));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public URL getCurrentDownloadUrl() {
		try {
			return new URL(getDownloadUrl(currentMode));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
