package ch.msf.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import ch.msf.CommonConstants;
import ch.msf.error.FatalException;
import ch.msf.util.IOUtils;
import ch.msf.util.PropertiesUtil;

public class ConfigurationManagerBaseImpl implements ConfigurationManagerBase {

	
//	private static final Logger _sLogger = Logger.getRootLogger();
	private static final Logger _sLogger = Logger.getLogger("ch.msf");
	
	// choosen application language
	protected String _DefaultLanguage;

	//
	protected String _ApplicationTitle = null;

	protected ImageIcon _ApplicationIcon = null;

	// a list of usable languages
	// determined from the fields's label locales
	private TreeSet<String> _Languages;

	private TreeSet<String> _FtpFileNames;

	//
	private String _CurrentFtpTransfertFileName = null;

	//
	protected String _EntryFormConfigFileName = null;

	protected boolean _ConfigurationSaved = true;

	protected Properties _Properties;

	// export prog
	private ExportOptions _ExportOptions = ExportOptions.NONE;

	// only for devel purposes
	protected String _DevProjectPath;

	protected String _MsfApplicationDir;
	protected String _MsfRessourceDir;
	protected String _MsfDriverDir;

	@Override
	/**
	 * @return the _DefaultLanguage
	 */
	public String getDefaultLanguage() {
		return _DefaultLanguage;
	}

	@Override
	/**
	 * @param _DefaultLanguage
	 *            the _DefaultLanguage to set
	 */
	public void setDefaultLanguage(String defaultLanguage) {
		_DefaultLanguage = defaultLanguage;
	}

	/**
	 * load all needed resource files
	 * 
	 * @param className
	 */
	@Override
	public void loadResourceLanguages(String className) {
		HashMap<String, HashMap<String, String>> classLabels = getResourceManager().getQuestionLabels(className, null);
		// determine the number of languages allowed from the
		// _FieldsLabels map
		// temp, determine the default language
		setupLanguages(classLabels);

	}

	/**
	 * get the list of all languages set the default language
	 * 
	 * @param _FieldsLabels
	 *            , the collection that contains all locales
	 */
	public void setupLanguages(HashMap<String, HashMap<String, String>> fieldsLabels) {
		setLanguages(new TreeSet<String>());
		for (String language : fieldsLabels.keySet()) {
			getLanguages().add(language);
		}
		// temp, take the first defined
		// setDefaultLanguage(System.getProperty("defaultLanguage"));
		if (getDefaultLanguage() == null)
			setDefaultLanguage("en");

	}

	@Override
	public Properties getGlobalProperties() {
		if (_Properties == null) {
			_Properties = getResourceManager().loadGlobalProperties(getDevProjectPath());
		}
		return _Properties;

	}

	@Override
	public ArrayList<String> getConfigFields(String keyPattern) {
		return PropertiesUtil.getConfigFields(getGlobalProperties(), keyPattern, CommonConstants.PROPS_STRING_SEPARATOR);
	}

	@Override
	public String getConfigField(String key) {
		return PropertiesUtil.getConfigField(key, getGlobalProperties());
	}

	public boolean isConfigurationSaved() {
		return _ConfigurationSaved;
	}

	public void setConfigurationSaved(boolean configurationSaved) {
		_ConfigurationSaved = configurationSaved;
	}

	/**
	 * @return the _ApplicationTitle
	 */
	public String getApplicationTitle() {
		return _ApplicationTitle;
	}

	/**
	 * @param _ApplicationTitle
	 *            the _ApplicationTitle to set
	 */
	public void setApplicationTitle(String applicationTitle) {
		_ApplicationTitle = applicationTitle;
	}

	public void setFtpFileNames(TreeSet<String> ftpFileNames) {
		_FtpFileNames = ftpFileNames;
	}

	public TreeSet<String> getFtpFileNames() {
		return _FtpFileNames;
	}

	/**
	 * @return the _ApplicationIconName
	 */
	public ImageIcon getApplicationIcon() {
		return _ApplicationIcon;
	}

	/**
	 * @param _ApplicationIcon
	 *            the _ApplicationIconName to set
	 */
	public void setApplicationIcon(ImageIcon applicationIcon) {
		_ApplicationIcon = applicationIcon;
	}

	/**
	 * @return the _Languages
	 */
	public TreeSet<String> getLanguages() {
		return _Languages;
	}

	/**
	 * @param _Languages
	 *            the _Languages to set
	 */
	public void setLanguages(TreeSet<String> languages) {
		_Languages = languages;
	}

	public String getEntryFormConfigFileName() {
		return _EntryFormConfigFileName;
	}

	public void setEntryFormConfigFileName(String entryFormConfigFileName) {
		_EntryFormConfigFileName = entryFormConfigFileName;
	}

	@Override
	public String getBaseDirectory() {

		String msfApplicationDir = System.getProperty("msfApplicationDir");
		if (msfApplicationDir == null)
			msfApplicationDir = getConfigField("msfApplicationDir");
		if (msfApplicationDir == null)
			msfApplicationDir = CommonConstants.MSF_BASE_DIR; // "C:\\MSFMedAppData";

		return msfApplicationDir;
	}

	@Override
	public String getApplicationDirectory() {
		String appDir = "";

		String msfApplicationDir = getBaseDirectory();
		String applicationShortName = System.getProperty("applicationShortName");
		if (applicationShortName == null)
			// applicationShortName = Constants.LOCAL_APP_SHORTNAME;
			// don't forget to set the value for local testing/devel
			applicationShortName = getConfigField("applicationShortName");

		appDir = msfApplicationDir + System.getProperty("file.separator") + applicationShortName;

		return appDir;
	}

	@Override
	// create msf structure directory if needed
	public boolean createMsfStructureBaseDirectory() {

		String msfApplicationDir = getBaseDirectory();
		System.out.println("msfApplicationDir is :"+msfApplicationDir);

		boolean dirOk = IOUtils.createDirIfNotExists(msfApplicationDir);
		if (!dirOk)
			return false;
		String applicationShortName = System.getProperty("applicationShortName");
		if (applicationShortName == null)
			// applicationShortName = "C:\\MSFMedAppData\\SGBV";
			// don't forget to set the value for local testing/devel
			applicationShortName = getConfigField("applicationShortName");

		_MsfApplicationDir = msfApplicationDir + System.getProperty("file.separator") + applicationShortName;
		
		System.out.println("MsfStructureBaseDirectory is :"+_MsfApplicationDir);

		dirOk = IOUtils.createDirIfNotExists(_MsfApplicationDir);
		return dirOk;
	}

	@Override
	// create msf structure directory if needed
	public boolean createMsfStructureDirectory() {

		String dirPath = null;
		String fileSeparator = System.getProperty("file.separator");

		// // /////// create msf root dir
		// _MsfApplicationDir = System.getProperty("msfApplicationDir");
		// if (_MsfApplicationDir == null) {
		// String errMess = "msfApplicationDir is not defined!!";
		// JOptionPane.showMessageDialog(null, errMess, "Fatal error",
		// JOptionPane.ERROR_MESSAGE, null);
		// throw new FatalException(errMess);
		// } else
		// dirPath = _MsfApplicationDir;
		//
		// boolean dirOk = IOUtils.createDirIfNotExists(_MsfApplicationDir);
		boolean dirOk = createMsfStructureBaseDirectory();
		if (!dirOk) {
			String errMess = "Could not create application directory! " + dirPath;
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		}

		// /////// create msf driver dir
		String driverDir = "Driver";

		dirPath = _MsfApplicationDir + fileSeparator + driverDir;

		dirOk = IOUtils.createDirIfNotExists(dirPath);
		if (!dirOk) {
			String errMess = "Could not create application directory! " + dirPath;
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		}
		_MsfDriverDir = dirPath;
		System.out.println("MsfDriverDir = " + _MsfDriverDir);

		// /////// create msf application dir
		String applicationShortName = System.getProperty("applicationShortName");
		if (applicationShortName == null) {
			String errMess = "applicationShortName is not defined!!";
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		} else {
			dirPath = _MsfApplicationDir + fileSeparator + applicationShortName;
		}
		dirOk = IOUtils.createDirIfNotExists(dirPath);
		if (!dirOk) {
			String errMess = "Could not create application directory! " + dirPath;
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		}

		// /////// create msf application subdir for versioned elements
		String applicationVersion = System.getProperty("applicationVersion");
		if (applicationVersion == null) {
			String errMess = "applicationVersion is not defined!!";
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		} else {
			dirPath = dirPath + fileSeparator + applicationShortName + "_" + applicationVersion;
		}
		dirOk = IOUtils.createDirIfNotExists(dirPath);
		if (!dirOk) {
			String errMess = "Could not create application directory! " + dirPath;
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		}

		// check backup dir exists
		String backupDir = dirPath + fileSeparator + "backup";
		System.out.println("Backup dir=" + backupDir);
		boolean exists = IOUtils.checkIfDirNotExists(backupDir);
		if (exists) {
			// if yes create backup
			System.out.println("Backup dir already exists...");

			String backupReplaceAll = System.getProperty("backupReplaceAll");
			System.out.println("backupReplaceAll = " + backupReplaceAll);
			if (backupReplaceAll != null && backupReplaceAll.equals("true")) {
				System.out.println("Replacing all contents from backup directory...");
				// delete dir contents
				IOUtils.deleteDirectoryContents(new File(backupDir));
			}
		} else {
			dirOk = IOUtils.createDirIfNotExists(backupDir);
			if (!dirOk) {
				String errMess = "Could not create backup directory! " + dirPath;
				JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
				throw new FatalException(errMess);
			}
		}

		// move actual contents to backup dir
		try {
			IOUtils.copyDirectory(new File(dirPath), new File(backupDir));
		} catch (IOException e) {
			e.printStackTrace();
			String errMess = "Problem copying backup directory!!";
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		}

		dirOk = IOUtils.createDirIfNotExists(dirPath);
		if (!dirOk) {
			String errMess = "Could not create application directory! " + dirPath;
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			throw new FatalException(errMess);
		}

		_MsfRessourceDir = dirPath;

		return dirOk;
	}

	@Override
	public String getMsfApplicationDir() {
		return _MsfApplicationDir;
	}

	@Override
	public String getMsfRessourceDir() {
		return _MsfRessourceDir;
	}

	@Override
	public String getMsfDriverDir() {
		return _MsfDriverDir;
	}

	public String getDevProjectPath() {
		return _DevProjectPath;
	}

	public void setDevProjectPath(String _DevProjectPath) {
		this._DevProjectPath = _DevProjectPath;
	}

	public String getCurrentFtpTransfertFileName() {
		return _CurrentFtpTransfertFileName;
	}

	public void setCurrentFtpTransfertFileName(String _CurrentFtpTransfertFileName) {
		this._CurrentFtpTransfertFileName = _CurrentFtpTransfertFileName;
	}
	
	

	@Override
	public Logger getLogger() {
		return _sLogger;
	}


	// ///////////////////

	public enum ExportOptions {
		NONE, FTP_HQ, DRIVE;
	}

	/**
	 * @return the _ExportOptions
	 */
	public ExportOptions getExportOptions() {
		return _ExportOptions;
	}

	/**
	 * @param _ExportOptions
	 *            the _ExportOptions to set
	 */
	public void setExportOptions(ExportOptions _ExportOptions) {
		this._ExportOptions = _ExportOptions;
	}

	// ///////////////////////////

	private ResourceManager _ResourceManager;

	@Override
	public ResourceManager getResourceManager() {
		return _ResourceManager;
	}

	@Override
	public void setResourceManager(ResourceManager resourceManager) {
		this._ResourceManager = resourceManager;
	}

}
