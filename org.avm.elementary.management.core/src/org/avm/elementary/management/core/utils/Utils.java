package org.avm.elementary.management.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.StringTokenizer;

public class Utils {
	private static final SimpleDateFormat DF = new SimpleDateFormat(
			"yyyy-MM-dd");
	private static final SimpleDateFormat HF = new SimpleDateFormat("HHMMss");

	/**
	 * replace une chaine de caractere par une autre ex: replace
	 * ("ftp://10.1.2.9/$u", "$u", "toto"); => ftp://10.1.2.9/toto
	 */
	public static String replace(String chaine, String tag, String tag2) {

		if (tag == null || tag2 == null || chaine == null) {
			System.err
					.println("Null !!!! " + chaine + ", " + tag + ", " + tag2);
			return chaine;
		}
		int idx = chaine.indexOf(tag);
		if (idx != -1) {
			StringBuffer buf = new StringBuffer();
			buf.append(chaine.substring(0, idx));
			buf.append(tag2);
			return buf.toString()
					+ replace(chaine.substring(idx + tag.length()), tag, tag2);
		} else {
			return chaine;
		}

	}

	public static boolean isSameDate(File f1, File f2) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String d1 = df.format(new Date(f1.lastModified()));
		
		
		String d2 = df.format(new Date(f2.lastModified()));
		boolean result =  d1.equals(d2);

		return result;


	}

	public static String formatURL(String url, boolean useDefault) {
		String terminalId = "default";
		String terminalOwner = Terminal.getInstance().getOwner();
		String plateformType = Terminal.getInstance().getPlateform();
		String terminalName = Terminal.getInstance().getName();

		if (useDefault == false) {
			terminalId = Terminal.getInstance().getId();
		}

		String temp = url;
		temp = Utils.replace(temp, "$i", terminalId);

		temp = Utils.replace(temp, "$u", terminalName);// deprecated
		temp = Utils.replace(temp, "$v", terminalName);// deprecated
		temp = Utils.replace(temp, "$n", terminalName);

		temp = Utils.replace(temp, "$e", terminalOwner);
		temp = Utils.replace(temp, "$o", terminalOwner);

		temp = Utils.replace(temp, "$p", plateformType);

		return temp;
	}

	public static String getRemoteFilename(File localfile, String remotefilename) {
		StringBuffer buf = new StringBuffer();
		buf.append(Terminal.getInstance().getOwner());
		buf.append("_");
		buf.append(Terminal.getInstance().getName());

		if (localfile != null) {
			Date modified = new Date(localfile.lastModified());
			buf.append("_");
			buf.append("M");
			buf.append(DF.format(modified));
			buf.append("-");
			buf.append(HF.format(modified));
			buf.append("R");
			buf.append(DF.format(new Date()));
			buf.append("_");
		}

		buf.append(remotefilename);

		return buf.toString();
	}

	public static Properties loadProperties(String filename) {
		Properties p;
		p = new Properties();
		try {
			p.load(new FileInputStream(filename));

			System.out.println("Loading properties : " + filename);
			System.out.println(p);

		} catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException (" + e.getMessage()
					+ "): " + filename);
		} catch (IOException e) {
			System.err.println("IOException (" + e.getMessage() + "): "
					+ filename);
		}
		return p;
	}

	public static void saveProperties(Properties p, String filename)
			throws FileNotFoundException, IOException {
		p.store(new FileOutputStream(filename), "Modified by Management Core");
	}

	public static String genMD5(File file) {
		MessageDigest md;
		StringBuffer output = null;
		try {
			md = MessageDigest.getInstance("MD5");
			InputStream inStream = new FileInputStream(file);
			byte[] buffer = new byte[8192];
			int read = 0;
			while ((read = inStream.read(buffer)) > 0) {
				md.update(buffer, 0, read);
			}
			byte[] md5sum = md.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			output = new StringBuffer(bigInt.toString(16));

			inStream.close();

			while (output.length() < 32) {
				output.insert(0, "0");
			}

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return output.toString();
	}

	public static void writeMD5(String md5, File file) throws IOException {
		BufferedWriter br = new BufferedWriter(new FileWriter(
				file.getAbsolutePath() + ".md5"));
		br.write(md5);
		br.write("  ");
		br.write(file.getName());
		br.write("\n");
		br.close();
	}

	public static boolean checkFile(File md5file) {
		boolean result = false;

		try {
			BufferedReader br = new BufferedReader(new FileReader(
					md5file.getAbsolutePath()));
			String line = br.readLine();
			int idx = line.indexOf(" ");
			String md5 = line.substring(0, idx).trim();
			String filename = line.substring(idx + 1).trim();

			String genmd5 = genMD5(new File(md5file.getParentFile()
					.getAbsoluteFile() + "/" + filename));

			result = (genmd5 != null && md5.equals(genmd5));
			System.out.println("genmd5=" + genmd5 + ", md5=" + md5 + "(check="
					+ result + ")");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * converti un numero de version au format 1.2.3.20141208 dans un format
	 * numérique 1.00200320141208 (pour pouvoir effectuer des comparaisons)
	 * 
	 * @param version
	 * @return
	 */
	public static String getVersion(String version) {
		if (version == null)
			return null;

		StringTokenizer t = new StringTokenizer(version, ".");
		long majeure = 0;
		long mineure = 0;

		long micro = 0;
		long other = 0;

		String v;

		// majeure
		if (t.hasMoreElements()) {
			majeure = Long.parseLong(t.nextToken());

			// --mineure
			if (t.hasMoreElements()) {
				v = t.nextToken();
				mineure = Long.parseLong(v);

				// micro
				if (t.hasMoreElements()) {
					v = t.nextToken();
					micro = Long.parseLong(v);

					// other
					if (t.hasMoreElements()) {
						StringBuffer b = new StringBuffer();
						while (t.hasMoreElements()) {
							v = t.nextToken();
							b.append(v);
						}
						try {
							other = Long.parseLong(b.toString());
						} catch (Exception e) {
							// on ne s'occupe pas de la version other si pas un
							// nombre
							// e.printStackTrace();
							other = 0;
						}
					}

				}

			}

		}

		// if (micro > 1000) {
		// long mod = micro % 1000;
		// mineure += (micro - mod) / 1000;
		// micro = mod;
		// }
		//
		// if (mineure > 1000) {
		//
		// long mod = mineure % 1000;
		// majeure += (mineure - mod) / 1000;
		// mineure = mod;
		//
		// // int unit = (int)(mineure/1000d);
		// // majeure +=unit;
		// // mineure = mineure / unit;
		// }

		StringBuffer buf = new StringBuffer();
		buf.append(majeure);
		buf.append(".");

		StringBuffer b;

		// --mineure
		b = new StringBuffer();
		b.append(mineure);
		while (b.length() < 3) {
			b.insert(0, "0");
		}
		buf.append(b.toString());

		// --micro
		b = new StringBuffer();
		b.append(micro);
		while (b.length() < 3) {
			b.insert(0, "0");
		}
		buf.append(b.toString());

		// --other
		buf.append(other);

		// double ver;
		// try {
		// ver = Double.parseDouble(buf.toString());
		// } catch (NumberFormatException e) {
		// ver = Double.MAX_VALUE;
		// }
		// return ver;
		return buf.toString();
	}

	public static int compareVersion(String v1, String v2) {
		String formattedV1 = getVersion(v1);
		String formattedV2 = getVersion(v2);

		return formattedV1.compareTo(formattedV2);
	}

	// /**
	// * converti un numero de version au format 1.0.0.10.2 dans un format
	// * numérique 1.00102 (pour pouvoir effectuer des comparaisons)
	// *
	// * @param version
	// * @return
	// */
	// public static double getVersion(String version) {
	// if (version == null)
	// return Double.NaN;
	// StringBuffer buf = new StringBuffer();
	// boolean dotfound = false;
	// for (int i = 0; i < version.length(); i++) {
	// char c = version.charAt(i);
	// if (Character.isDigit(c)) {
	// buf.append(c);
	// } else if (dotfound == false && c == '.') {
	// dotfound = true;
	// buf.append(c);
	// }
	// }
	// double ver;
	// try {
	// ver = Double.parseDouble(buf.toString());
	// } catch (NumberFormatException e) {
	// ver = Double.MAX_VALUE;
	// }
	// return ver;
	// }

}
