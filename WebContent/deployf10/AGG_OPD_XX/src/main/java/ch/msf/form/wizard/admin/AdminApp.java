package ch.msf.form.wizard.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ch.msf.CommonConstants;
import ch.msf.Constants;
import ch.msf.error.ConfigException;
import ch.msf.form.wizard.MSFForm;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.model.Project;
import ch.msf.service.ServiceHelper;
import ch.msf.util.StackTraceUtil;

import com.nexes.wizard.Wizard;
import com.nexes.wizard.WizardPanelDescriptor;

public class AdminApp extends MSFForm {

	private static final long serialVersionUID = 1L;

	public static final String COUNTRY_CODE_PATTERN = "CountryCode-";

	private static AdminApp _Prog;

	public AdminApp(String[] args) {
		super(args);

	}

	public boolean init() {

		_ConfigurationManager.getDbManager().setAdmin(true);

		// take care of applicationTitle parameter
		String applicationTitle = _ConfigurationManager.getConfigField("applicationTitleAdmin"); //TN76 
		
		if (System.getProperty("webstarttest") == null) {
			// modeJavaWebStart = false;
			System.out.println("DEVELOPMENT MODE = TRUE");
			_ConfigurationManager.setDevProjectPath(Constants.WORKSPACE_RES_DIR);
		}
		else
			System.out.println("DEVELOPMENT MODE = FALSE");
		
		_ConfigurationManager.setApplicationTitle(applicationTitle);

		if (_ConfigurationManager.getApplicationTitle() == null)
			throw new ConfigException("This key should be defined in jnlp resource or property file:" + "applicationTitleAdmin");
		// first thing, set the db
		ServiceHelper.getDbManagerService().setAdmin(true);

		boolean ret = super.init();

		if (ret) { // update configuration

			// read config file

			// read configuration
			ConfigurationManagerImpl configurationManager = ServiceHelper.getConfigurationManagerService();
			HashMap<String, ArrayList<String>> configCountryProjets = parseConfigFile(configurationManager);

			// create countries from config
			createCountryFromDeployConfig(configurationManager, configCountryProjets);

			// update selected projects
			createCountryProjectFromDeployConfig(configurationManager, configCountryProjets);

		}

		return ret;
	}

	/**
	 * create projects from config
	 * 
	 * @param configurationManager
	 * @param configCountryProjets
	 */
	private void createCountryFromDeployConfig(ConfigurationManagerImpl configurationManager, HashMap<String, ArrayList<String>> configCountryProjets) {
		// TreeSet<String> selectedCountriesCodes =
		// getSelectedCountriesCodes(configurationManager);
		TreeSet<String> configCountriesCodes = new TreeSet<String>(configCountryProjets.keySet());
		configurationManager.updateCountrySelectionConfigurationFromCode(configCountriesCodes);
	}

	/**
	 * create projects from config
	 * 
	 * @param configurationManager
	 * @param configCountryProjets
	 */
	private void createCountryProjectFromDeployConfig(ConfigurationManagerImpl configurationManager, HashMap<String, ArrayList<String>> configCountryProjets) {

		List<Project> newProjects = new ArrayList<Project>();

		// for each country...
		for (String countryCode : configCountryProjets.keySet()) {

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

	// private TreeSet<String>
	// getSelectedCountriesCodes(ConfigurationManagerImpl configurationManager)
	// {
	//
	// TreeSet<String> selectedCountriesLabels = new TreeSet<String>();
	//
	// // get config country - project codes
	// ArrayList<String> allConfigCountryCodes =
	// configurationManager.getConfigFields(COUNTRY_CODE_PATTERN);
	//
	// for (String configCountryCode : allConfigCountryCodes) {
	// // get rid of header
	// configCountryCode = configCountryCode.replace(COUNTRY_CODE_PATTERN, "");
	// // take first 2 letters
	// String countryCode = configCountryCode.substring(0, 2);
	// selectedCountriesLabels.add(countryCode);
	// }
	//
	// return selectedCountriesLabels;
	// }

	private HashMap<String, ArrayList<String>> parseConfigFile(ConfigurationManagerImpl configurationManager) {
		HashMap<String, ArrayList<String>> retCountryProjets = new HashMap<String, ArrayList<String>>();

		// get config country - project codes
		ArrayList<String> allConfigCountryCodes = configurationManager.getConfigFields(COUNTRY_CODE_PATTERN);

		for (String configCountryCode : allConfigCountryCodes) {
			// get rid of header
			configCountryCode = configCountryCode.replace(COUNTRY_CODE_PATTERN, "");
			configCountryCode = configCountryCode.replace(CommonConstants.PROPS_STRING_SEPARATOR, "");
			// -HT|Haiti Mission|HT101
			String[] parts = configCountryCode.split("\\|");
			if (parts.length != 3)
				throw new ConfigException(getClass().getName() + "::parseConfigFile():PROBLEM, bad country project configuration!");
			String countryCode = parts[0];
			ArrayList<String> projects = retCountryProjets.get(countryCode);
			if (projects == null) {
				projects = new ArrayList<String>();
				retCountryProjets.put(countryCode, projects);
			}
			String projectLabel = parts[1];
			String projectCode = parts[2];
			String projectLabelCode = projectLabel + "|" + projectCode;
			projects.add(projectLabelCode);
			// System.out.println();
		}

		return retCountryProjets;
	}

	private void addProject(ConfigurationManagerImpl configurationManager) {
		// configurationManager.addProjectFromCountryCode(String countryCode,
		// String projectName);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			_Prog = new AdminApp(args);

			SwingUtilities.invokeLater(_Prog);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(e), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		}

	}

	public void run() {
		try {
			boolean ok = init();
			Wizard wizard = new AdminWizard();
			if (!ok) {
				JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS, "Bad init", JOptionPane.ERROR_MESSAGE, null);

			} else {

				// wizard.addCheckBusinessRules(this);
				wizard.getDialog().setTitle(_ConfigurationManager.getApplicationTitle());

				WizardPanelDescriptor descriptorLogin = new LoginDescriptor();
				wizard.registerWizardPanel(LoginDescriptor.IDENTIFIER, descriptorLogin);

				boolean configUrgence = false;
				if (configUrgence) { // TN75
					WizardPanelDescriptor descriptorParamCountries = new AdminParamCountriesDescriptor();
					wizard.registerWizardPanel(AdminParamCountriesDescriptor.IDENTIFIER, descriptorParamCountries);
				}

				WizardPanelDescriptor descriptorParam = new AdminParamDescriptor();
				wizard.registerWizardPanel(AdminParamDescriptor.IDENTIFIER, descriptorParam);

				WizardPanelDescriptor descriptorFinal = new AdminFinalDescriptor();
				wizard.registerWizardPanel(AdminFinalDescriptor.IDENTIFIER, descriptorFinal);

				wizard.setCurrentPanel(LoginDescriptor.IDENTIFIER, true);
//				 wizard.setCurrentPanel(AdminParamDescriptor.IDENTIFIER,
//				 true);

				int ret = wizard.showModalDialog();

				System.out.println("Dialog return code is (0=Finish,1=Cancel,2=Error): " + ret);
			}

		} catch (Exception e) {
			if (e.getMessage().contains("Unable to build EntityManagerFactory")) {
				JOptionPane
						.showMessageDialog(
								null,
								"Error!\n"
										+ "Database is probably already in use by another process, please close any existing application using this database before restarting...\nLa base de datos probablemente ya está en uso por otro proceso; por favor, cierre cualquier aplicación existente utilizando esta base de datos antes de reiniciar...\nLa base de donnée est déjà utilisée, veuillez fermer les applications existantes avant de redémarrer...",
								"Fatal error", JOptionPane.ERROR_MESSAGE, null);
			} else
				JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(e), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		}
	}

	@Override
	public boolean checksOnClose() {
		// my checks...

		return true;
	}

}
