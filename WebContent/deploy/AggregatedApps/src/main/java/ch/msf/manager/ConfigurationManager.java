package ch.msf.manager;

import java.util.List;

import ch.msf.form.wizard.MSFForm;
import ch.msf.model.CareCenter;
import ch.msf.model.EntryFormConfig;
import ch.msf.model.Project;
import ch.msf.model.SelectedCountry;
import ch.msf.model.SelectionContext;


public interface ConfigurationManager extends ConfigurationManagerBase{
	
	
	public MSFForm getProgramRunner();
	
	public List<SelectedCountry> getConfigFileSelectedCountries();
	
	
	public List<Project> getAdminSelectedProjects(String countryNameLabel);
	
	
	public List<CareCenter> getConfigFileCareCenters(String countryName,
			String projectName);
	
	public List<String> getConfigFileCareCentersNames(String countryName,
			String projectName);
	
	
	public boolean countryConfigFileHasProjects(String countryNameLabel);
	
	
	public boolean projectConfigFileHasCareCenters(String countryNameLabel,
			String projectName);
	
	
	public SelectedCountry getSelectedCountryFromLabel(String countryNameLabel,
			String locale);
	
	public void addProject(String countryNameLabel, String projectName);
	
	public boolean deleteProject(String countryNameLabel, String projectName);
	
	public boolean addCareCenter(String countryNameLabel, String projectName,
			String careCenterName);
	
	public boolean deleteCareCenter(String countryNameLabel,
			String projectName, String careCenterName);
	
	//TN78 admin 
	public boolean renameCareCenter(String countryNameLabel, String projectName, String oldcareCenterName, String newcareCenterName);
	
	public int saveAdminContextConfig();
	
	public void loadConfigurationFile(/*String configFileName*/) throws Exception; //TN13
	

//	public void loadResources();
	
	
	public List<String> getConfigFileAllSelectedCountrieNameLabels(String locale);
	
	public List<String> getConfigFileProjectNames(String countryName);
	
	public EntryFormConfig getEntryFormConfig();

	public SelectedCountriesManagerImpl getSelectedCountriesManager();

	
//	public Properties getGlobalProperties();
	
	public boolean checkCountryComplete(/*String countryNameLabel*/);


//	public ArrayList<String> getConfigFields(String keyPattern);

//	public String getConfigField(String key);
	
//	public String getApplicationDirectory();
	
//	public boolean createMsfStructureBaseDirectory();
	
//	public boolean createMsfStructureDirectory();
	
	public boolean isConfigurationSaved();
//
//	String getMsfApplicationDir();
//
//	String getMsfRessourceDir();
//
//	String getMsfDriverDir();

	void setConfigurationSaved(boolean configurationSaved);

	SelectedCountry getSelectedCountryFromCode(String countryCode);

	void updateProjectSelectionConfiguration(String countryCode, List<Project> selectedProjects);

	
	public SelectionContext getCurrentSelectionContext();
	
	public void setCurrentSelectionContext(SelectionContext selectionContext);

}
