package ch.msf.form.wizard.admin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import ch.msf.form.wizard.FinalDescriptor;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public class AdminParamDescriptor extends WizardPanelDescriptor implements ActionListener {

	public static final String IDENTIFIER = "CONNECTOR_CHOOSE_PANEL";

	AdminParamPanel _ParamPanel;

	public AdminParamDescriptor() {

		_ParamPanel = new AdminParamPanel();

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(_ParamPanel);
		_ParamPanel.addUserActionListener(this);
	}

	public Object getNextPanelDescriptor() {
		return FinalDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		boolean configUrgence = false;
		if (configUrgence) { // TN75
			return AdminParamCountriesDescriptor.IDENTIFIER;
		}
		return null;
	}

	public void aboutToDisplayPanel() {

		// show header info
		_ParamPanel.buildBannerInfo();

		// get the allcountries service
		// AllCountriesManager allCountriesManager =
		// ServiceHelper.getAllCountriesManagerService();

		ConfigurationManagerImpl configurationManager = ServiceHelper.getConfigurationManagerService();

		// display selected countries
		List<String> list = configurationManager.getConfigFileAllSelectedCountrieNameLabels(configurationManager.getDefaultLanguage());
		TreeSet<String> selectedCountriesLabels = new TreeSet<String>(list);

		_ParamPanel.getSelectedCountries().setEntities(new ArrayList<String>(selectedCountriesLabels));
		_ParamPanel.getSelectedCountries().setSelectedIndex(0);

		// display the projects from the selected country
		List<String> currentProjects = null;
		if (selectedCountriesLabels != null && selectedCountriesLabels.size() > 0) {
			_ParamPanel.setCurrentCountrySelected(selectedCountriesLabels.first());
			currentProjects = configurationManager.getConfigFileProjectNames(_ParamPanel.getCurrentCountrySelected());
		}
		// load all projects from the first country
		_ParamPanel.getProjects().setEntities(currentProjects);
		_ParamPanel.getProjects().setSelectedIndex(0);

		// show the carecenters from the selected project
		List<String> currentCareCenters = null;
		if (currentProjects != null && currentProjects.size() > 0) {
			_ParamPanel.setCurrentProjectSelected(currentProjects.get(0));
			currentCareCenters = configurationManager.getConfigFileCareCentersNames(_ParamPanel.getCurrentCountrySelected(), _ParamPanel.getCurrentProjectSelected());
		}

		_ParamPanel.getCareCenters().setEntities(currentCareCenters);
		_ParamPanel.getCareCenters().setSelectedIndex(0);

		_ParamPanel.configurePanelStartState();

		// if modif in countries screen
		// if (!configurationManager.isConfigurationSaved())
		// a save should be made to go to the next screen
		getWizard().setNextFinishButtonEnabled(false);
	}

	// public void aboutToDisplayPanel() {
	//
	// // show header info
	// _ParamPanel.buildBannerInfo();
	//
	// ConfigurationManager configurationManager =
	// ServiceHelper.getConfigurationManagerService();
	//
	// // display selected countries
	// List<String> countries =
	// configurationManager.getConfigFileAllSelectedCountrieNameLabels(configurationManager.getDefaultLanguage());
	// // List<String> countries =
	// getCountryLabelProjectCode(configurationManager);
	// TreeSet<String> selectedCountriesLabels = new TreeSet<String>(countries);
	//
	// _ParamPanel.getSelectedCountries().setEntities(new
	// ArrayList<String>(selectedCountriesLabels));
	// _ParamPanel.getSelectedCountries().setSelectedIndex(0);
	//
	// // display the projects from the selected country
	// List<String> currentProjectLabels = new ArrayList<String>();
	// if (selectedCountriesLabels != null && selectedCountriesLabels.size() >
	// 0) {
	// _ParamPanel.setCurrentCountrySelected(selectedCountriesLabels.first());
	// List<String> currentProjects =
	// configurationManager.getConfigFileProjectNames(_ParamPanel.getCurrentCountrySelected());
	// // get the project label
	// // String projectLabel = getProjectLabel(configurationManager,
	// selectedCountriesLabels.first());
	// currentProjectLabels.add(projectLabel);
	// }
	// // load all projects from the first country
	// _ParamPanel.getProjects().setEntities(currentProjectLabels);
	// _ParamPanel.getProjects().setSelectedIndex(0);
	//
	// // show the carecenters from the selected project
	// List<String> currentCareCenters = null;
	// if (currentProjectLabels != null && currentProjectLabels.size() > 0) {
	// _ParamPanel.setCurrentProjectSelected(currentProjectLabels.get(0));
	// currentCareCenters =
	// configurationManager.getConfigFileCareCentersNames(_ParamPanel.getCurrentCountrySelected(),
	// _ParamPanel.getCurrentProjectSelected());
	// }
	//
	// _ParamPanel.getCareCenters().setEntities(currentCareCenters);
	// _ParamPanel.getCareCenters().setSelectedIndex(0);
	//
	// _ParamPanel.configurePanelStartState();
	//
	// // if modif in countries screen
	// // if (!configurationManager.isConfigurationSaved())
	// // a save should be made to go to the next screen
	// getWizard().setNextFinishButtonEnabled(false);
	//
	// }

	// private ArrayList<String> getCountryLabelProjectCode(ConfigurationManager
	// configurationManager) {
	//
	// List<String> countries =
	// configurationManager.getConfigFileAllSelectedCountrieNameLabels(configurationManager.getDefaultLanguage());
	// ICI
	//
	//
	//
	//
	// AllCountriesManager allCountriesManager =
	// ServiceHelper.getAllCountriesManagerService();
	// ArrayList<String> countryLabelCodes = new ArrayList<String>();
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
	// Country country = allCountriesManager.getCountry(countryCode);
	// if (country == null){
	// String errMess =
	// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGEADMIN_BAD_CONFIG);
	// throw new ConfigException(errMess);
	// }
	// String countryNameLabel =
	// allCountriesManager.getCountryNameLabel(country,
	// configurationManager.getDefaultLanguage());
	// // get rid of label
	// int index = configCountryCode.indexOf('|');
	// if (index == -1){
	// String errMess =
	// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGEADMIN_BAD_CONFIG);
	// throw new ConfigException(errMess);
	// }
	// String projectCode = configCountryCode.substring(0, index);
	//
	// countryNameLabel += ("-"+projectCode);
	// countryLabelCodes.add(countryNameLabel);
	// }
	//
	// return countryLabelCodes;
	// }

	// private String getProjectLabel(ConfigurationManager configurationManager,
	// String countryLabelProjectCode) {
	//
	// // get the project code
	// int index = countryLabelProjectCode.indexOf('-');
	// if (index == -1){
	// String errMess =
	// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGEADMIN_BAD_CONFIG);
	// throw new ConfigException(errMess);
	// }
	// String projectCode = countryLabelProjectCode.substring(index+1);
	//
	// // get config country - project codes
	// ArrayList<String> allConfigCountryCodes =
	// configurationManager.getConfigFields(COUNTRY_CODE_PATTERN);
	// for (String configCountryCode : allConfigCountryCodes) {
	// // get rid of header
	// configCountryCode = configCountryCode.replace(COUNTRY_CODE_PATTERN, "");
	// // get rid of label
	// index = configCountryCode.indexOf('|');
	// if (index == -1){
	// String errMess =
	// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGEADMIN_BAD_CONFIG);
	// throw new ConfigException(errMess);
	// }
	// String projectCode2 = configCountryCode.substring(0, index);
	// if (projectCode2.equals(projectCode))
	// return configCountryCode.substring(index+1);
	// }
	// String errMess =
	// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGEADMIN_BAD_CONFIG);
	// throw new ConfigException(errMess);
	// }

	// /**
	// *
	// * @param configurationManager
	// * @param country
	// * @return the default countryProject label if not defined in db
	// */
	// private ArrayList<String> getCountryProjectCode(ConfigurationManager
	// configurationManager, String country) {
	// ArrayList<String> allConfigCountryCodes =
	// configurationManager.getConfigFields(COUNTRY_CODE_PATTERN);
	//
	// ArrayList<String> countryCodes = new ArrayList<String>();
	// for (String configCountryCode : allConfigCountryCodes) {
	// if (configCountryCode.startsWith(country)) {
	// countryCodes.add(configCountryCode);
	// }
	// }
	// return allConfigCountryCodes;
	// }

	public void aboutToHidePanel() {

		// AdminConfigLoader.setConfigurationSaved(false);

	}

	public void actionPerformed(ActionEvent e) {

		// configuration saved
		// getWizard().setNextFinishButtonEnabled(true);
	}

}
