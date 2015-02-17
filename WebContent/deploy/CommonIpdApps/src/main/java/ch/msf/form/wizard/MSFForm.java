package ch.msf.form.wizard;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.ActionMapUIResource;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.config.ConfigFileException;
import ch.msf.form.FatalException;
import ch.msf.javadb.h2.CreateDB;
import ch.msf.javadb.h2.DbConnectionStrategy;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.manager.ResourceManagerImpl;
import ch.msf.model.Country;
import ch.msf.model.Project;
import ch.msf.model.SelectedCountry;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IOUtils;
import ch.msf.util.StackTraceUtil;

public class MSFForm implements Runnable, CheckBusinessRulesChecker {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;

	// resource file names
	protected String _FieldsIdTypeFileName = null;
	protected String _FieldsLabelFileName = null;

	// a map of parameters (key - value) that have been selected by the user
	private static LinkedHashMap<String, String> _SelectedParams = new LinkedHashMap<String, String>();

	@SuppressWarnings("unused")
	private MSFForm _Prog;

	// // define the configuration for this program
	// private Properties _Properties = new Properties();

	protected EntryFormConfigurationManagerImpl _ConfigurationManager;

	public MSFForm(String[] args) {

		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();

		if (args == null)
			return;

	}

	/**
	 * application init - read the configuration for this program - build the config file
	 * 
	 * @return
	 */
	public boolean init() {
		System.out.println(getClass().getName() + "::init............................");

		boolean ret = true;
		boolean modeJavaWebStart = true;

		getProperties().put("modeJavaWebStart", true);

		// app construction is done, save now the app reference to config for a
		// later use
		getConfigurationManager().setProg(this);

		// allow the tab to work on dialog boxes
		UIManager.put("Button.defaultButtonFollowsFocus", Boolean.TRUE);

		// change application background color
		UIManager.put("Panel.background", CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);

		// allow the arrows to work on dialog boxes
		configureOptionPane();

		// TN135 default language
		getConfigurationManager().setDefaultLanguage(System.getProperty("defaultLanguage"));
		if (getConfigurationManager().getDefaultLanguage() == null)
			getConfigurationManager().setDefaultLanguage((String) getProperties().get("defaultLanguage"));
		if (getConfigurationManager().getDefaultLanguage() == null)
			getConfigurationManager().setDefaultLanguage("en");

		// take care of application icon name parameter
		String appIconName = System.getProperty("applicationIconName");
		if (appIconName == null) {
			appIconName = "head_logo_msf.gif";
			modeJavaWebStart = false;
			System.out.println("NOT Java web start...............................");
		} else
			System.out.println("Java web start MODE...............................");
		getProperties().put("modeJavaWebStart", modeJavaWebStart);

		URL imgURL = IOUtils.getResource(appIconName, EntryFormConfigurationManagerImpl.class);
		if (imgURL != null) {
			getConfigurationManager().setApplicationIcon(new ImageIcon(imgURL, "MSF icon"));
		} else {
			System.out.println("Important...: file parameter for icon is not defined: ");
		}

		// load the localized messages
		ServiceHelper.getMessageService().loadMessages(CommonConstants.MESSAGES_FILENAME);

		// load the action and business rules
		if (this instanceof DataEntryFormI)
			ServiceHelper.getActionRuleService().loadActionRulesResources(CommonIpdConstants.ACTION_RULES_FILENAME);

		System.out.println(getClass().getName() + "::init2............................");

		// create msf structure directory if needed
		// boolean dirOk = createMsfStructureBaseDirectory();
		boolean dirOk = getConfigurationManager().createMsfStructureBaseDirectory();
		if (!dirOk) {
			throw new FatalException("Fatal: problem writing application structure directory: ");
		}

		// TN122 take care of mixedModeDB parameter
		if (this instanceof DataEntryFormI) {
			String mixedModeDB = System.getProperty("mixedModeDB");
			if (mixedModeDB == null) {
				// don't forget to set the value for local testing/devel
				// non webstart mode
				mixedModeDB = getConfigurationManager().getConfigField("mixedModeDB");
			}
			if (mixedModeDB != null)
				getProperties().put("mixedModeDB", mixedModeDB);
		}

		// TN133 check if test db
		String testModeDB = System.getProperty("testModeDB");
		if (testModeDB == null) {
			// don't forget to set the value for local testing/devel
			// non webstart mode
			testModeDB = getConfigurationManager().getConfigField("testModeDB");
		}
		if (testModeDB != null)
			getProperties().put("testModeDB", testModeDB);

		// TN129
		if (this instanceof DataEntryFormI) {
			String villageOriginConceptId = System.getProperty("villageOrigin.conceptId");
			if (villageOriginConceptId == null) {
				// don't forget to set the value for local testing/devel
				// non webstart mode
				villageOriginConceptId = getConfigurationManager().getConfigField("villageOrigin.conceptId");
			}
			if (villageOriginConceptId != null)
				getProperties().put("villageOrigin.conceptId", villageOriginConceptId);
		}

		// TN148
		// load the roles&permissions if enabled
		try {
			if (this instanceof DataEntryFormI) {
				String roleFile = getConfigurationManager().getConfigField("application.roles.filename");
				if (roleFile != null) {
					ServiceHelper.getPermissionManagerService().loadRoles();
					ServiceHelper.getPermissionManagerService().loadPermissions();
				}
			}
		} catch (Exception e) {
			throw new FatalException("Fatal: problem reading application roles & permissions: " + e);
		}

		// // take care of db file name liquibase.xml parameter
		// String statementFileName =
		// System.getProperty("sqlStatementFileName");
		// if (statementFileName == null) {
		// if (!modeJavaWebStart)
		// statementFileName =
		// "H:\\devel\\workspace64Bits\\SGBV\\src\\main\\resources\\liquibase.xml";
		// }
		// getProperties().put("sqlStatementFileName", statementFileName);
		// if (statementFileName == null)
		// throw new
		// FatalException("Fatal: Bad configuration. Could not find statementFileName parameter!");

		// take care of configuration file name parameter
		// we just have to read or write the configuration file in a well-known
		// place that is not necessarily an msf directory
		String configFileName = System.getProperty("configFileName");
		if (configFileName == null) {
			configFileName = "entryFormConfig.xml";
		}
		getProperties().put("configFileName", configFileName);

		if (!(this instanceof DataEntryFormI)) {//TN149 if admin
			// includes drive in path
			String overridingConfigFilePathName = getOverridingConfigFilePathName();
			getConfigurationManager().setEntryFormConfigFileName(overridingConfigFilePathName);
			System.out.println("configFileName is:" + overridingConfigFilePathName);
		} else{
			getConfigurationManager().setEntryFormConfigFileName(configFileName);
			System.out.println("configFileName is:" + configFileName);
		}

		// check if preconfig mode, then install config file automatically
		if (this instanceof DataEntryFormI) {// (only if it is an entryform)
			autoInstallConfig();// taivd copy file config and listcountry.csv to appdir
		}

		// TN147 check if file exists in the 'well known' directory
		// if so...move it to the project directory
		if (this instanceof DataEntryFormI) {// (only if it is an entryform)
			overrideConfigurationFile();
		}

		// take care of db creation strategy parameter
		String connectionStrategy = System.getProperty("connectionStrategy");
		if (connectionStrategy == null) {
			if (!modeJavaWebStart)
				connectionStrategy = "USE_OR_CREATE_DB";
		}
		getProperties().put("connectionStrategy", connectionStrategy);
		if (connectionStrategy == null)
			throw new FatalException("Fatal: Bad configuration. Could not find connectionStrategy parameter!");

		// take care of db creation strategy parameter
		if (getProperties().get("connectionStrategy") != null) {

			if (connectionStrategy.equals(DbConnectionStrategy.USE_OR_CREATE_DB.toString()) || connectionStrategy.equals(DbConnectionStrategy.FORCE_CREATE_DB.toString())) {
				// test if allcountries tables are there
				boolean tablesAlreadyCreated = false;
				try {
					List<Country> allCountries = ServiceHelper.getAllCountriesManagerService().getAllCountries();

					tablesAlreadyCreated = allCountries.size() > 0;
				} catch (Exception e) {
					// table does not exist!
				}

				if (!tablesAlreadyCreated || connectionStrategy.equals(DbConnectionStrategy.FORCE_CREATE_DB.toString()))
					ret = new CreateDB().createDb(getProperties());
			}

		}

		// TN142 if field defined in deploy configuration, propose to find patient on the value of this field
		if (this instanceof DataEntryFormI) {
			String searchConceptId = getConfigurationManager().getConfigField("searchConceptId");
			if (searchConceptId != null) {
				//
				getConfigurationManager().setPatientSearchConceptId(searchConceptId);
			}

		}

		// ...load config file
		// TN147 at this point the configFileName should point to the project directory
		try {
			getConfigurationManager().loadConfigurationFile(/* configFileName */);
		} catch (FileNotFoundException e) {
			if (this instanceof DataEntryFormI) {
				// throw an exception only if db does not exist
				List<SelectedCountry> allExistingSelectedCountries = getConfigurationManager().getSelectedCountriesManager().getDbSelectedCountries(true);
				if (allExistingSelectedCountries.size() == 0)
					throw new ConfigFileException("Missing configuration file! " + e);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new FatalException("Fatal: problem reading application configuration: " + e);
		}

		return ret;
	}

	/**
	 * TN147 set the config file path to the project one override project configuration file with configFilePathName
	 * 
	 * @param configFilePathName
	 */
	private void overrideConfigurationFile(/* String configFilePathName */) {
		// get potential overriding configuration file
		String overridingConfigFilePathName = getOverridingConfigFilePathName();

		boolean ret = false;
		// override the file
		File source = new File(overridingConfigFilePathName);

		// set project configuration path
		String projectConfigFilePathName = getConfigurationFilePathName();
		getConfigurationManager().setEntryFormConfigFileName(projectConfigFilePathName);

		File destination = new File(projectConfigFilePathName);
		if (source.exists()) {
			System.out.println(getClass().getName() + "overriding Configuration File::" + projectConfigFilePathName + " with " + overridingConfigFilePathName);

			try {

				IOUtils.copyAnyFile(source, destination);

				// delete original file
				ret = source.delete();

			} catch (IOException e1) {
				//
			}
		}
		if (ret)
			System.out.println(getClass().getName() + "overriding Configuration File OK");
		else
			System.out.println(getClass().getName() + "overrideConfigurationFile:: No change in configuration file...");
	}

	private String getOverridingConfigFilePathName() {
		String configFileName = (String) getProperties().get("configFileName");
		if (configFileName.contains(System.getProperty("file.separator")))
			return configFileName;
		String overridingConfigFilePathName = "D:" + System.getProperty("file.separator") + configFileName;
		return overridingConfigFilePathName;
	}

	/**
	 * //TN147
	 * 
	 * @return the path + name of the configuration file
	 */
	private String getConfigurationFilePathName() {
		String configFileName = (String) getProperties().get("configFileName");

		String applicationDirectory = getConfigurationManager().getApplicationDirectory();

		String newConfigFilePathName = applicationDirectory + System.getProperty("file.separator") + configFileName;
		return newConfigFilePathName;
	}

	/**
	 * TN109 if entryFormConfig.xml exists in deployed config, copy it to the right dir
	 */

	private void autoInstallConfig() {
		// read the file resources
		String resourceName = (String) getProperties().get("configFileName");
		String resourceCountry="list_countries.csv";//taivd add 
		// get project configuration path
		String projectConfigFilePathName = getConfigurationFilePathName();
		String strAppDir= getConfigurationManager().getBaseDirectory()+File.separator+resourceCountry;//taivd add copy list_countries.csv
		URL dbConfigFile = IOUtils.getResource(resourceName, EntryFormConfigurationManagerImpl.class);
		if (dbConfigFile != null) {
			System.out.println("Configuration file detected.............Attempting to install it......");
			try { // TN147
				boolean ret = ((ResourceManagerImpl) ServiceHelper.getConfigurationManagerService().getResourceManager()).exportResource(resourceName, projectConfigFilePathName);
				boolean ret2 = ((ResourceManagerImpl) ServiceHelper.getConfigurationManagerService().getResourceManager()).exportResource(resourceCountry, strAppDir);//taivd add copy list_countries.csv
			} catch (IOException e) {
				System.out.println("The configuration file could not be installed!");
				e.printStackTrace();
			}
		}

	}

	/**
	 * create projects from config
	 * 
	 * @param configurationManager
	 * @param configCountryProjets
	 */
	// TN79
	protected void createCountryProjectFromDeployConfig(ConfigurationManagerImpl configurationManager, HashMap<String, ArrayList<String>> configCountryProjets) {

		List<Project> newProjects = new ArrayList<Project>();

		// for each country...
		for (String countryCode : configCountryProjets.keySet()) {
			newProjects.clear(); // TN79
			ArrayList<String> projectCodeNames = configCountryProjets.get(countryCode);
			// for each project...
			for (String projectCodeName : projectCodeNames) {
				String[] parts = projectCodeName.split("\\|");
				String projectCode = parts[1];
				String projectName = parts[0];
				Project newProject = configurationManager.createProjectsFromCodeLabel(configurationManager, countryCode, projectCode, projectName);
				newProjects.add(newProject);
			}
			configurationManager.updateProjectSelectionConfiguration(countryCode, newProjects);
		}
	}

	public void run() {

		JOptionPane.showMessageDialog(null, "The run method should be overriden!", "Fatal error", JOptionPane.ERROR_MESSAGE, null);

	}

	//
	protected void configureOptionPane() {
		if (UIManager.getLookAndFeelDefaults().get("OptionPane.actionMap") == null) {
			UIManager.put("OptionPane.windowBindings", new Object[] { "ESCAPE", "close", "LEFT", "left", "KP_LEFT", "left", "RIGHT", "right", "KP_RIGHT", "right" });
			ActionMap map = new ActionMapUIResource();
			map.put("close", new OptionPaneCloseAction());
			map.put("left", new OptionPaneArrowAction(false));
			map.put("right", new OptionPaneArrowAction(true));
			UIManager.getLookAndFeelDefaults().put("OptionPane.actionMap", map);
		}
	}

	protected class OptionPaneCloseAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JOptionPane optionPane = (JOptionPane) e.getSource();
			optionPane.setValue(JOptionPane.CLOSED_OPTION);
		}
	}

	protected class OptionPaneArrowAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		private boolean myMoveRight;

		OptionPaneArrowAction(boolean moveRight) {
			myMoveRight = moveRight;
		}

		public void actionPerformed(ActionEvent e) {
			JOptionPane optionPane = (JOptionPane) e.getSource();
			EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
			eq.postEvent(new KeyEvent(optionPane, KeyEvent.KEY_PRESSED, e.getWhen(), (myMoveRight) ? 0 : InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_TAB, KeyEvent.CHAR_UNDEFINED,
					KeyEvent.KEY_LOCATION_UNKNOWN));
		}
	}

	/**
	 * @return the _SelectedParams a map of parameters (key - value) that have been selected by the user
	 */
	@SuppressWarnings("static-method")
	public LinkedHashMap<String, String> getSelectedParams() {
		return _SelectedParams;
	}

	/**
	 * @return
	 */
	public String getSelectedParamsString() {
		StringBuilder sb = new StringBuilder("<html>");
		String lastLine = "";
		// will iterate in the order of insertion
		for (String value : getSelectedParams().values()) {

			sb.append(" - " + value);
			lastLine += (" - " + value);
			if (lastLine.length() > 20) {
				// sb.append("\n");
				sb.append("<br>");
				lastLine = "";
			} else {

			}
		}
		// sb.append(" - "); //TN136
		return sb.toString();
	}

	public Properties getProperties() {
		// return _Properties;
		return getConfigurationManager().getGlobalProperties();
	}

	@Override
	public boolean checksOnClose() {
		//
		JOptionPane.showMessageDialog(null, "The run method should be overriden!", "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		return false;
	}

	public EntryFormConfigurationManagerImpl getConfigurationManager() {
		return _ConfigurationManager;
	}

	// TN145
	protected void manageException(Exception e) {
		e.printStackTrace();
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		e.printStackTrace(printWriter);

		if (stringWriter.toString().contains("Locked by another process") || (e.getMessage() != null && e.getMessage().contains("Unable to build EntityManagerFactory"))) {
			JOptionPane
					.showMessageDialog(
							null,
							"Error!\n"
									+ "Database is probably already in use by another process, please close any existing application using this database before restarting...\nLa base de datos probablemente ya está en uso por otro proceso; por favor, cierre cualquier aplicación existente utilizando esta base de datos antes de reiniciar...\nLa base de donnée est déjà utilisée, veuillez fermer les applications existantes avant de redémarrer...",
							"Fatal error", JOptionPane.ERROR_MESSAGE, null);
		} else {
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(e), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		}
	}

}
