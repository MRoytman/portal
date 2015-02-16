package ch.msf.config;

import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import ch.msf.error.FatalException;
import ch.msf.util.NetUtil;

public class DirStructure {

	/**
	 * 
	 * @return the existing msf structure directory
	 */
	public static String getMsfStructureDirectory(String level) {

		String dirPath = null;
		String fileSeparator = System.getProperty("file.separator");

		// /////// get msf root dir
		String msfApplicationDir = System.getProperty("msfApplicationDir");
		if (msfApplicationDir == null) {
			String errMess = "msfApplicationDir is not defined!!";
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
			// msfApplicationDir = "C:\\MSFMedAppData";
			// dirPath = msfApplicationDir;
		}
		dirPath = msfApplicationDir;

		if (level != null && level.equals("msfApplicationDir"))
			return dirPath;

		// /////// get msf application dir
		String applicationShortName = System.getProperty("applicationShortName");
		if (applicationShortName == null) {
			String errMess = "applicationShortName is not defined!!";
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
			// applicationShortName = "YF";
			// dirPath = msfApplicationDir + CommonConstants.FILE_SEPARATOR
			// + applicationShortName;
		}

		dirPath = msfApplicationDir + fileSeparator + applicationShortName;

		if (level != null && level.equals("applicationShortName"))
			return dirPath;

		// /////// get msf application subdir for versioned elements
		String applicationVersion = System.getProperty("applicationVersion");
		if (applicationVersion == null) {
			String errMess = "applicationVersion is not defined!!";
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
			// applicationVersion = "1.5";
		}
		dirPath = dirPath + fileSeparator + applicationShortName + "_" + applicationVersion;

		return dirPath;
	}

	public static String getApplicationDirectory() {
		return getMsfStructureDirectory("applicationShortName");
	}

//	public static String getDBUrl(Boolean isAdmin) {
//		// configOverrides.put("javax.persistence.jdbc.url",
//		// "jdbc:h2:C:\\H2DBAdmin;MVCC=TRUE");
//
//		String url = "jdbc:h2:";
//
//		String appDir = getApplicationDirectory();
//		url += appDir;
//
//		url += "\\H2DB";
//		if (isAdmin)
//			url += "Admin";
//
//		String applicationShortName = System.getProperty("applicationShortName");
//		if (applicationShortName == null)
//			applicationShortName = "SGBV";
//
//		url += applicationShortName;
//		String dbVersion = System.getProperty("dbVersion");
//		if (dbVersion == null)
//			dbVersion = "1.0";
//		url += dbVersion;
//		url += "_";
//		// get machine network name
//		// TODO
//
//		url += ";MVCC=TRUE";
//
//		return url;
//	}

	public static String getDBFile(Boolean isAdmin) {
		// configOverrides.put("javax.persistence.jdbc.url",
		// "jdbc:h2:C:\\H2DBAdmin;MVCC=TRUE");

		String url = "";

		String appDir = getApplicationDirectory();
		url += appDir;

		url += "\\H2DB";
		if (isAdmin)
			url += "Admin";

		String applicationShortName = System.getProperty("applicationShortName");
		if (applicationShortName == null) {
			String errMess = "applicationShortName is not defined!! Add this field to VM args: -DapplicationShortName=\"IPD\"";
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		}

		url += applicationShortName;
		String dbVersion = System.getProperty("dbVersion");
		if (dbVersion == null)
			dbVersion = "1.0";
		url += dbVersion;
		url += "_";
		// get machine network name
		// TODO

		url += ".h2.db";

		return url;
	}

	/**
	 * 
	 * @return the db filename to be exported
	 * @throws UnknownHostException
	 */
	public static String buildExpFileName() throws UnknownHostException {
		// country
		String expFileName = System.getProperty("countryDeployShortName");

		// app
		expFileName += ("_" + System.getProperty("applicationShortName"));

		// machine name
		expFileName += ("_" + NetUtil.getMachineName());

		expFileName += ".h2.db";

		return expFileName;
	}

}
