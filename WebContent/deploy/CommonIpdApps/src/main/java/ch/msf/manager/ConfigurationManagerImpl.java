package ch.msf.manager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;

import ch.msf.form.FatalException;
import ch.msf.form.wizard.MSFForm;
import ch.msf.model.CareCenter;
import ch.msf.model.Country;
import ch.msf.model.CountryName;
import ch.msf.model.EntryFormConfig;
import ch.msf.model.Project;
import ch.msf.model.SelectedCountry;
import ch.msf.model.SelectionContext;
import ch.msf.service.ServiceHelper;
import ch.msf.util.MiscelaneousUtils;

public class ConfigurationManagerImpl extends ConfigurationManagerBaseImpl implements ConfigurationManager {

	protected MSFForm _Prog;

	// admin configuration (projects, countries, etc...)
	private EntryFormConfig _EntryFormConfig;

	private SelectionContext _SelectionContext;
	
	private boolean _CancelPatient;// TN132

	public ConfigurationManagerImpl() {
		System.out.println("ConfigurationManagerImpl constructor");
	}

	@Override
	public List<SelectedCountry> getConfigFileSelectedCountries() {

		if (getEntryFormConfig() == null)// no config file
			return null;
		return getEntryFormConfig().getSelectedCountries();
	}

	@Override
	public List<Project> getAdminSelectedProjects(String countryNameLabel) {
		// List<Project> retList = new ArrayList<Project>();
		int index = countryNameLabel.indexOf('-');
		if (index != -1)
			countryNameLabel = countryNameLabel.substring(0, index);
		// System.out.println();
		SelectedCountry country = getSelectedCountryFromLabel(countryNameLabel, getDefaultLanguage());
		if (country != null) {
			return country.getProjects();
		}
		return null; // retList;
	}

	@Override
	public List<String> getConfigFileProjectNames(String countryName) {

		List<String> projectNames = new ArrayList<String>();
		List<Project> projects = getAdminSelectedProjects(countryName);

		if (projects != null && projects.size() > 0) {
			for (Project project : projects) {
				projectNames.add(project.toString());
			}
		} else
			System.out.println("getProjectNames() : projects = null on " + countryName);

		return projectNames;
	}

	@Override
	public List<CareCenter> getConfigFileCareCenters(String countryName, String projectName) {

		// List<CareCenter> careCenters = new ArrayList<CareCenter>();

		List<Project> projects = getAdminSelectedProjects(countryName);

		if (projects != null && projects.size() > 0) {
			for (Project project : projects) {
				if (project.toString().equals(projectName)) {

					return project.getCareCenters();
				}
			}
		} else
			System.out.println("getProjectNames() Error: projects = null!");
		return null;
	}

	public List<String> getConfigFileCareCentersNames(String countryName, String projectName) {

		List<String> careCentersNames = new ArrayList<String>();
		List<CareCenter> careCenters = getConfigFileCareCenters(countryName, projectName);

		if (careCenters != null) {
			for (CareCenter careCenter : careCenters) {
				careCentersNames.add(careCenter.toString());
			}
			// return careCentersNames;
		}

		return careCentersNames;
	}

	@Override
	public boolean countryConfigFileHasProjects(String countryNameLabel) {

		SelectedCountry country = getSelectedCountryFromLabel(countryNameLabel, getDefaultLanguage()); // TODO
																										// change
																										// default
																										// language
		if (country != null) {
			return country.getProjects().size() > 0;
		}

		return false;
	}

	@Override
	public boolean projectConfigFileHasCareCenters(String countryNameLabel, String projectName) {
		boolean ret = false;
		SelectedCountry country = getSelectedCountryFromLabel(countryNameLabel, getDefaultLanguage()); // TODO
																										// change
																										// default
																										// language
		if (country != null) {
			for (Project project : country.getProjects()) {
				if (project.toString().equals(projectName)) {
					return project.getCareCenters().size() > 0;
				}
			}
		}

		if (!ret)
			System.out.println("Problem! No carecenter was added. Country=" + countryNameLabel + " , project = " + projectName);
		return false;
	}

	/**
	 * 
	 * @param locale
	 * @return a list of labels for the selected countries
	 */
	@Override
	public List<String> getConfigFileAllSelectedCountrieNameLabels(String locale) {//
		List<String> countrieNames = new ArrayList<String>();

		for (SelectedCountry selectedCountry : getConfigFileSelectedCountries()) {
			// countrieNames.add(country.toString()); A CHANGER

			countrieNames.add(getAllCountriesManager().getCountryNameLabel(selectedCountry.getCountry(), locale));
		}
		return countrieNames;
	}

	@Override
	/**
	 * look in the whole table to find the right label
	 * @param countryNameLabel
	 * @param locale
	 * @return the selectedcountry from the passed label, locale
	 */
	// TODO à optimiser...
	public SelectedCountry getSelectedCountryFromLabel(String countryNameLabel, String locale) {

		// get the country
		ArrayList<String> countryNamesLabel = new ArrayList<String>();
		countryNamesLabel.add(countryNameLabel);

		List<SelectedCountry> selectedCountries = getConfigFileSelectedCountries();
		for (SelectedCountry selectedCountry : selectedCountries) {
			List<CountryName> countryNames = selectedCountry.getCountry().getCountryNames();
			for (CountryName countryName : countryNames) {
				if (countryName.getLocale().equals(getDefaultLanguage()) && countryName.getLabel().equals(countryNameLabel))
					return selectedCountry;
			}
		}
		return null;
	}

	/**
	 * look in the whole table to find the right label
	 * 
	 * @param countryNameLabel
	 * @param locale
	 * @return the selectedcountry from the passed label, locale
	 */
	@Override
	public SelectedCountry getSelectedCountryFromCode(String countryCode) {

		// get the country
		List<SelectedCountry> selectedCountries = getConfigFileSelectedCountries();
		for (SelectedCountry selectedCountry : selectedCountries) {
			Country country = selectedCountry.getCountry();
			if (country.getCode().equals(countryCode))
				return selectedCountry;
		}
		return null;
	}

	@Override
	public boolean deleteProject(String countryNameLabel, String projectName) {

		SelectedCountry selectedCountry = getSelectedCountryFromLabel(countryNameLabel, getDefaultLanguage()); // TODO
																												// change
																												// default
																												// language
		if (selectedCountry != null) {

			Project project = new Project(projectName);
			return selectedCountry.getProjects().remove(project);
		}
		System.out.println("Problem! No project was deleted. Country=" + countryNameLabel + " , project = " + projectName);
		return false;
	}

	@Override
	public boolean deleteCareCenter(String countryNameLabel, String projectName, String careCenterName) {

		SelectedCountry selectedCountry = getSelectedCountryFromLabel(countryNameLabel, getDefaultLanguage());
		if (selectedCountry != null) {
			for (Project project : selectedCountry.getProjects()) {
				if (project.toString().equals(projectName)) {
					return project.getCareCenters().remove(new CareCenter(careCenterName));
				}
			}
		} else
			System.out.println("Problem! No carecenter was deleted. Country=" + countryNameLabel + " , project = " + projectName + " , carecenter = " + careCenterName);
		return false;

	}

	@Override
	// TN78 admin
	public boolean renameCareCenter(String countryNameLabel, String projectName, String oldcareCenterName, String newcareCenterName) {
		SelectedCountry selectedCountry = getSelectedCountryFromLabel(countryNameLabel, getDefaultLanguage());
		if (selectedCountry != null) {
			for (Project project : selectedCountry.getProjects()) {
				if (project.toString().equals(projectName)) {
					for (CareCenter existingCareCenter : project.getCareCenters()) {
						if (existingCareCenter.toString().equals(oldcareCenterName)) {
							existingCareCenter.setName(newcareCenterName);
							return true;
						}
					}
				}
			}
		}
		System.out.println("Problem! No carecenter was renamed. Country=" + countryNameLabel + " , project = " + projectName + " , carecenter = " + oldcareCenterName);
		return false;
	}

	@Override
	public void addProject(String countryNameLabel, String projectName) {

		SelectedCountry selectedCountry = getSelectedCountryFromLabel(countryNameLabel, getDefaultLanguage()); // TODO
																												// change
																												// default
																												// language
		if (selectedCountry != null) {
			Project project = new Project(projectName);

			selectedCountry.getProjects().add(project);
			project.setSelectedCountry(selectedCountry);
			return;
		}
		System.out.println("Problem! No project was added. Country=" + countryNameLabel + " , project = " + projectName);
	}

	@Override
	public boolean addCareCenter(String countryNameLabel, String projectName, String careCenterName) {

		SelectedCountry selectedCountry = getSelectedCountryFromLabel(countryNameLabel, getDefaultLanguage());
		if (selectedCountry != null) {
			for (Project project : selectedCountry.getActiveProjects()) {
				if (project.toString().equals(projectName)) {
					CareCenter cc = new CareCenter(careCenterName);
					cc.setProject(project);
					project.getCareCenters().add(cc);
					return true;
				}
			}

		} else
			System.out.println("Problem! No carecenter was added. Country=" + countryNameLabel + " , project = " + projectName + " , carecenter = " + careCenterName);
		return false;
	}

	@Override
	public boolean checkCountryComplete(/* String countryNameLabel */) {

		List<SelectedCountry> selectedCountries = getConfigFileSelectedCountries();
		for (SelectedCountry selectedCountry : selectedCountries) {
			if (selectedCountry != null) {
				if (selectedCountry.getProjects() == null || selectedCountry.getProjects().size() == 0)
					return false;
				for (Project project : selectedCountry.getProjects()) {
					if (project.getCareCenters() == null || project.getCareCenters().size() == 0)
						return false;
				}

			} else
				return false;
		}
		return true;
	}

	/**
	 * Entry form reads the xml configuration setup by admin process.
	 * 
	 * @param configFileName
	 * @throws Exception
	 */
	public void loadConfigurationFile(/*String configFileName*/) throws Exception {

		boolean admin = getDbManager().isAdmin();

		if (!admin) {

			// read configuration file...
			// _EntryFormConfig is loaded
			readConfigurationFile(/*configFileName*/);

		} else {
			// admin part
			// we create a container configuration object
			// base on the actual db contents
			setEntryFormConfig(new EntryFormConfig());
			List<SelectedCountry> allSelectedCountries = getSelectedCountriesManager().getDbSelectedCountries(null);
			getEntryFormConfig().setSelectedCountries(allSelectedCountries);

		}

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

	/**
	 * setup the db with selected countries, projects, carecenters
	 * 
	 * @param isAdmin
	 */
	public void configureEntryFormDbFromConfigFile() {

		// do the changes only if config file exists
		if (getConfigFileSelectedCountries() != null) {

			String updateMessage = "";

			// read all existing selected countries (including retired)
			List<SelectedCountry> allExistingSelectedCountries = getSelectedCountriesManager().getDbSelectedCountries(null);
			HashMap<String, SelectedCountry> allExistingSelectedCountriesMap = new HashMap<String, SelectedCountry>();
			for (SelectedCountry selectedCountry : allExistingSelectedCountries) {
				allExistingSelectedCountriesMap.put(selectedCountry.getCode(), selectedCountry);
			}

			// read all new countries
			HashMap<String, SelectedCountry> newSelectedCountriesMap = new HashMap<String, SelectedCountry>();
			for (SelectedCountry selectedCountry : getConfigFileSelectedCountries()) {
				newSelectedCountriesMap.put(selectedCountry.getCode(), selectedCountry);
			}

			// parse the new selected
			for (String selectedCountryStr : newSelectedCountriesMap.keySet()) {
				// is this country code in previous configuration
				SelectedCountry selectedExistingCountry = allExistingSelectedCountriesMap.get(selectedCountryStr);
				if (selectedExistingCountry != null) {
					// it already exists
					if (selectedExistingCountry.isRetired())
						// TN82 it was retired but become active again
						selectedExistingCountry.setRetired(false);
					SelectedCountry newSelectedCountry = newSelectedCountriesMap.get(selectedCountryStr);
					// parse the newSelectedCountry projects
					for (Project newproject : newSelectedCountry.getProjects()) {
						// check each project
						boolean projectFound = false;
						for (Project existingproject : selectedExistingCountry.getProjects()) {
							// compare with code if present
							if (newproject.getCode() != null && existingproject.getCode() != null) {
								if (newproject.getCode().equals(existingproject.getCode())) {
									// do nothing
									projectFound = true;
								}
							}
							if (projectFound || (newproject.getName().equals(existingproject.getName()))) {
								if (existingproject.isRetired()) {
									// TN82 it was retired but become active
									// again
									existingproject.setRetired(false);
									updateMessage += "\n Activating (retired = false)  project " + existingproject.getName() + " to country "
											+ newSelectedCountry.getCountry().getCode();
								}
								projectFound = true;
								// parse the new carecenters
								for (CareCenter newCareCenter : newproject.getCareCenters()) {
									boolean careCenterFound = false;
									for (CareCenter existingCareCenter : existingproject.getCareCenters()) {
										// compare with code if present
										if (newCareCenter.getCode() != null && existingCareCenter.getCode() != null) {
											if (newCareCenter.getCode().equals(existingCareCenter.getCode())) {
												// do nothing
												careCenterFound = true;
											}
										}

										if (careCenterFound) { // TN130 get new
																// care center
																// names
											existingCareCenter.setName(newCareCenter.getName());
										}
										if (careCenterFound /*
															 * ||
															 * (newCareCenter.
															 * getName().equals(
															 * existingCareCenter
															 * .getName()))
															 */) {
											// do nothing
											careCenterFound = true;
											if (existingCareCenter.isRetired()) {
												// TN82 it was retired but
												// become
												// active
												// again
												existingCareCenter.setRetired(false);
												updateMessage += "\n Activating (retired = false) care center " + newCareCenter.getName() + " to project "
														+ existingproject.getName() + " to country " + newSelectedCountry.getCountry().getCode();
											}
											break;
										}
									}
									if (!careCenterFound) {
										// add carecenter
										existingproject.getCareCenters().add(newCareCenter);
										updateMessage += "\n Addition of care center " + newCareCenter.getName() + " to project " + existingproject.getName() + " to country "
												+ newSelectedCountry.getCountry().getCode();
									}
								}

								// parse the old carecenters
								for (CareCenter existingCareCenter : existingproject.getCareCenters()) {
									boolean careCenterFound = false;
									for (CareCenter newCareCenter : newproject.getCareCenters()) {

										// compare with code if present // ADDED
										// 07.05.13
										if (newCareCenter.getCode() != null && existingCareCenter.getCode() != null) {
											if (newCareCenter.getCode().equals(existingCareCenter.getCode())) {
												// do nothing
												careCenterFound = true;
											}
										}
										if (careCenterFound || newCareCenter.getName().equals(existingCareCenter.getName())) {// END
																																// ADDED
																																// 07.05.13
											// do nothing
											careCenterFound = true;
											break;
										}
									}
									if (!careCenterFound) {
										// remove carecenter
										existingCareCenter.setRetired(true);
										updateMessage += "\n Deletion of care center " + existingCareCenter.getName() + " to project " + existingproject.getName() + " to country "
												+ newSelectedCountry.getCountry().getCode();

									}
								}
								break;
							}
						}
						if (!projectFound) {
							// new project so add it to existing country
							selectedExistingCountry.getProjects().add(newproject);
							updateMessage += "\n Addition of project " + newproject.getName() + " to country " + newSelectedCountry.getCountry().getCode();
						}
					}

					// parse the selectedExistingCountry projects
					for (Project existingproject : selectedExistingCountry.getActiveProjects()) {
						// check each project
						boolean projectFound = false;
						for (Project newproject : newSelectedCountry.getProjects()) {

							// compare with code if present // ADDED 07.05.13
							if (newproject.getCode() != null && existingproject.getCode() != null) {
								if (newproject.getCode().equals(existingproject.getCode())) {
									// do nothing
									projectFound = true;
								}
							}
							if (projectFound || newproject.getName().equals(existingproject.getName())) {// ADDED
																											// 07.05.13
								projectFound = true;
								break;
							}
						}
						if (!projectFound) {
							// project and carecenters should be deleted
							existingproject.setRetired(true);
							updateMessage += "\n Deletion of project " + existingproject.getName() + " to country " + selectedExistingCountry.getCountry().getCode();

							for (CareCenter existingCareCenter : existingproject.getCareCenters()) {
								existingCareCenter.setRetired(true);
								updateMessage += "\n Deletion of care center " + existingCareCenter.getName() + " to project " + existingproject.getName() + " to country "
										+ selectedExistingCountry.getCountry().getCode();
							}
						}
					}

					// save the country anyway
					getSelectedCountriesManager().saveSelectedCountry(selectedExistingCountry);

				} else {
					// a new country has been defined in the configuration
					SelectedCountry newSelectedCountry = newSelectedCountriesMap.get(selectedCountryStr);
					// save the new country
					getSelectedCountriesManager().saveSelectedCountry(newSelectedCountry);
					updateMessage += "\n Addition of country " + newSelectedCountry.getCountry().getCode();

				}
			}

			// delete the countries that are no more in the model
			List<SelectedCountry> selectedCountriesToDelete = new ArrayList<SelectedCountry>();
			// parse the existing countries
			for (String existingCountryStr : allExistingSelectedCountriesMap.keySet()) {
				// is this country code in new configuration
				SelectedCountry newSelectedCountry = newSelectedCountriesMap.get(existingCountryStr);
				if (newSelectedCountry != null) {
					// it already exists
					// we do nothing
				} else {
					// SelectedCountry should be deleted
					SelectedCountry selectedExistingCountry = allExistingSelectedCountriesMap.get(existingCountryStr);
					selectedExistingCountry.setRetired(true);
					selectedCountriesToDelete.add(selectedExistingCountry);
					updateMessage += "\n Deletion of country " + existingCountryStr;

					// project and carecenters should be deleted
					for (Project existingproject : selectedExistingCountry.getProjects()) {

						existingproject.setRetired(true);
						updateMessage += "\n Deletion of project " + existingproject.getName() + " to country " + existingCountryStr;

						for (CareCenter existingCareCenter : existingproject.getCareCenters()) {
							existingCareCenter.setRetired(true);
							updateMessage += "\n Deletion of care center " + existingCareCenter.getName() + " to project " + existingproject.getName() + " to country "
									+ existingCountryStr;
						}
					}
				}
			}
			System.out.println(updateMessage);
			// ...and setup db accordingly, remove the deleted countries
			for (SelectedCountry selectedCountry : selectedCountriesToDelete) {
				// getSelectedCountriesManager().removeSelectedCountry(selectedCountry);
				getSelectedCountriesManager().saveSelectedCountry(selectedCountry);
			}

		}

	}

	private void readConfigurationFile(/*String configFileName*/) throws Exception {

		JAXBContext jc = JAXBContext.newInstance(EntryFormConfig.class);

		Unmarshaller unmarshaller = jc.createUnmarshaller();
		InputStream inputStream = null;
		try {
			String configFileName = getEntryFormConfigFileName(); //TN147
			inputStream = new FileInputStream(configFileName);
			_EntryFormConfig = (EntryFormConfig) unmarshaller.unmarshal(inputStream);

			// complete selectedCountries configuration (skiped when saved)
			for (SelectedCountry selectedCountry : _EntryFormConfig.getSelectedCountries()) {
				Country country = getAllCountriesManager().getCountry(selectedCountry.getCode());
				if (country == null) {
					throw new FatalException(getClass().getName() + "::readConfigurationFile:Country not found!! Code=" + selectedCountry.getCode());
				}
				selectedCountry.setCountry(country);
				// set the project entity (skiped when saved)
				for (Project project : selectedCountry.getProjects()) {
					project.setSelectedCountry(selectedCountry);
					// set the carecenter entity (skiped when saved)
					for (CareCenter carecenter : project.getCareCenters()) {
						carecenter.setProject(project);
					}
				}
			}

		} catch (FileNotFoundException e) {
			// _EntryFormConfig = new EntryFormConfig();
			throw e;
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

	}

	/**
	 * save to the db and build the configuration file
	 * 
	 * @return 0 if ok
	 */
	public int saveAdminContextConfig() {

		// update the admin db
		boolean ret = updateAdminDb();

		if (ret) {
			String configFileName = getEntryFormConfigFileName();
			System.out.println("configFileName = " + configFileName);

			EntryFormConfig ac = getEntryFormConfig();

			// make a copy of the object...
			// System.out.println("ac2 = ");
			EntryFormConfig ac2 = (EntryFormConfig) ac.clone();

			// ... and clear the link with the country that make a circular
			// reference
			List<SelectedCountry> selCountries = ac2.getSelectedCountries();
			// List<String> _SelectedCountryCodes = new ArrayList<String>();

			for (SelectedCountry selCountry : selCountries) {
				// _SelectedCountryCodes.add(selCountry.getCode());
				selCountry.setCountry(null);
				for (Project project : selCountry.getProjects()) {
					project.setSelectedCountry(null);
					for (CareCenter careCenter : project.getCareCenters()) {
						careCenter.setProject(null);
					}
				}
			}

			// save the results to xml
			JAXBContext jc;
			try {
				jc = JAXBContext.newInstance(EntryFormConfig.class);
				Marshaller marshaller = jc.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				JAXBElement<EntryFormConfig> jaxbElement = new JAXBElement<EntryFormConfig>(new QName("entryFormConfig"), EntryFormConfig.class, ac2);

				// String fileName = "C:\\devel\\run\\adminConfig.xml";
				OutputStream out = new FileOutputStream(configFileName);
				marshaller.marshal(jaxbElement, out);

				setConfigurationSaved(true);
			} catch (Exception e) {
				e.printStackTrace();

				JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
			}
			return 0; // ok
		}
		return 1; // problem
	}

	/**
	 * update the db admin based on the configuration file
	 * 
	 * @return
	 */
	private boolean updateAdminDb() {

		try {
			EntryFormConfig ac = getEntryFormConfig();
			EntryFormConfig ac2 = new EntryFormConfig();
			for (SelectedCountry selectedCountry : ac.getSelectedCountries()) {

				// the selectedCountry reference is modified by the saving
				selectedCountry = getSelectedCountriesManager().saveSelectedCountry(selectedCountry);
				// add the modified selectedCOuntry to a new configuration
				ac2.getSelectedCountries().add(selectedCountry);
			}
			// update the configuration file with info updated from persistence
			setEntryFormConfig(ac2);

			// remove the deleted countries
			HashSet<SelectedCountry> selectCountryToRemove = new HashSet<SelectedCountry>(getSelectedCountriesManager().getDbSelectedCountries(true));
			for (SelectedCountry selectedCountry : ac2.getSelectedCountries()) {
				selectCountryToRemove.remove(selectedCountry);
			}
			// remove from db what's left
			for (SelectedCountry selectedCountry : selectCountryToRemove) {
				getSelectedCountriesManager().removeSelectedCountry(selectedCountry);
			}

			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @return the
	 */
	public EntryFormConfig getEntryFormConfig() {
		return _EntryFormConfig;
	}

	private void setEntryFormConfig(EntryFormConfig entryFormConfig) {
		_EntryFormConfig = entryFormConfig;

	}

	/**
	 * @return the _Prog
	 */
	public MSFForm getProgramRunner() {
		return _Prog;
	}

	/**
	 * @param _Prog
	 *            the _Prog to set
	 */
	public void setProg(MSFForm prog) {
		_Prog = prog;
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

	// /**
	// * @return the _Languages
	// */
	// public TreeSet<String> getLanguages() {
	// return _Languages;
	// }
	//
	// /**
	// * @param _Languages
	// * the _Languages to set
	// */
	// public void setLanguages(TreeSet<String> languages) {
	// _Languages = languages;
	// }

	public String getEntryFormConfigFileName() {
		return _EntryFormConfigFileName;
	}

	public void setEntryFormConfigFileName(String entryFormConfigFileName) {
		_EntryFormConfigFileName = entryFormConfigFileName;
	}

	@Override
	public SelectionContext getCurrentSelectionContext() {

		return _SelectionContext;
	}

	@Override
	public void setCurrentSelectionContext(SelectionContext selectionContext) {
		_SelectionContext = selectionContext;
	}

	// ///////////////////////////
	// spring...

	private DbManager _DbManager;

	public DbManager getDbManager() {
		return _DbManager;
	}

	public void setDbManager(DbManager _DbManager) {
		this._DbManager = _DbManager;
	}

	protected AllCountriesManagerImpl _AllCountriesManager;

	public void setAllCountriesManager(AllCountriesManagerImpl allCountriesManagerImpl) {
		_AllCountriesManager = allCountriesManagerImpl;
	}

	public AllCountriesManagerImpl getAllCountriesManager() {
		return _AllCountriesManager;
	}

	protected SelectedCountriesManagerImpl _SelectedCountriesManager;

	public void setSelectedCountriesManager(SelectedCountriesManagerImpl SelectedCountriesManagerImpl) {
		_SelectedCountriesManager = SelectedCountriesManagerImpl;
	}

	public SelectedCountriesManagerImpl getSelectedCountriesManager() {
		return _SelectedCountriesManager;
	}

	public void updateCountrySelectionConfigurationFromLabel(TreeSet<String> selectedCountriesLabels) {
		// get all the possible available countries
		// get the allcountries service
		// TODO use configurationManager for getDefaultLanguage
		// ConfigurationManagerImpl configurationManager =
		// ServiceHelper.getConfigurationManagerService();

		// get the collection of new selected countries from the country
		// manager
		List<SelectedCountry> selectedCountries = getSelectedCountriesManager().createCountriesFromLabel(new ArrayList<String>(selectedCountriesLabels), getDefaultLanguage());

		updateCountrySelectionConfiguration(selectedCountries);
	}

	public void updateCountrySelectionConfigurationFromCode(TreeSet<String> configCountriesCodes) {
		// get all the possible available countries
		// get the allcountries service
		// TODO use configurationManager for getDefaultLanguage
		// ConfigurationManagerImpl configurationManager =
		// ServiceHelper.getConfigurationManagerService();

		// get the collection of new selected countries from the country
		// manager
		List<SelectedCountry> configSelectedCountries = getSelectedCountriesManager().createCountriesFromCode(configCountriesCodes);

		updateCountrySelectionConfiguration(configSelectedCountries);
	}

	public void updateCountrySelectionConfiguration(List<SelectedCountry> configSelectedCountries) {

		List<SelectedCountry> previousSelectedCountries = getConfigFileSelectedCountries();

		HashSet<SelectedCountry> previousSelectedCountrieSet = new HashSet<SelectedCountry>(previousSelectedCountries);
		HashSet<SelectedCountry> newSelectedCountriesSet = new HashSet<SelectedCountry>();

		// remove the selectedCountries from the previousSelectedCountrieSet
		for (SelectedCountry country : configSelectedCountries) {
			boolean removed = previousSelectedCountrieSet.remove(country);
			if (!removed) {
				// country is new in the selection
				newSelectedCountriesSet.add(country);
			}
		}

		// remove the new selection from the selected countries configuration
		if (previousSelectedCountrieSet.size() > 0) {
			// those countries should be deleted from the model
			setConfigurationSaved(false);
			previousSelectedCountries.removeAll(previousSelectedCountrieSet);
		}

		// add the new selection to the selected countries configuration
		if (newSelectedCountriesSet.size() > 0) {
			// those countries should be added to the model
			setConfigurationSaved(false);
			previousSelectedCountries.addAll(newSelectedCountriesSet);
		}

	}

	@Override
	public void updateProjectSelectionConfiguration(String countryCode, List<Project> selectedProjects) {

		List<Project> previousSelectedProjects = getConfigFileSelectedProjects(countryCode);

		HashSet<Project> previousSelectedProjectSet = new HashSet<Project>(previousSelectedProjects);
		HashSet<Project> newSelectedProjectSet = new HashSet<Project>();

		// remove the selectedCountries from the previousSelectedCountrieSet
		for (Project project : selectedProjects) {
			boolean removed = previousSelectedProjectSet.remove(project);
			if (!removed) {
				// project is new in the selection
				newSelectedProjectSet.add(project);
			}
		}

		// remove the new selection from the selected projects configuration
		if (previousSelectedProjectSet.size() > 0) {
			// those countries should be deleted from the model
			setConfigurationSaved(false);
			previousSelectedProjects.removeAll(previousSelectedProjectSet);
		}

		// add the new selection to the selected projects configuration
		if (newSelectedProjectSet.size() > 0) {
			// those countries should be added to the model
			setConfigurationSaved(false);
			// add projects to already selected project
			previousSelectedProjects.addAll(newSelectedProjectSet);
		}
	}

	public Project createProjectsFromCodeLabel(ConfigurationManagerImpl configurationManager, String countryCode, String projectCode, String label) {
		SelectedCountry selectedCountry = configurationManager.getSelectedCountryFromCode(countryCode);

		if (selectedCountry != null) {
			Project project = new Project();
			project.setCode(projectCode);
			project.setName(label);
			// selectedCountry.getProjects().add(project);
			project.setSelectedCountry(selectedCountry);
			return project;
		}
		return null;
	}

	/**
	 * 
	 * @param countryCode
	 * @return the list of config file selected projects for the country
	 *         identified by countryCode
	 */
	private List<Project> getConfigFileSelectedProjects(String countryCode) {

		if (getEntryFormConfig() == null)// no config file
			return null;

		for (SelectedCountry selectedCountry : getEntryFormConfig().getSelectedCountries()) {

			if (selectedCountry.getCode().equals(countryCode)) {
				return selectedCountry.getProjects();
			}
		}
		return null;
	}

	public String buildPatientContextId(String idPatientToCheck) {
		// TN94 transparently add context to patient's id.
		SelectionContext selectionContext = getCurrentSelectionContext();
		String contextId = MiscelaneousUtils.getContextKey(selectionContext.getSelectedProject().getCode(), selectionContext.getSelectedCareCenter().getCode());

		if (!idPatientToCheck.startsWith(contextId))
			return contextId + idPatientToCheck;

		return idPatientToCheck;
	}

	@Override
	public boolean isCurrentPatientCancelled() {
		return _CancelPatient;
	}

	@Override // TN132
	public void setCurrentPatientCancelled(boolean cancelled) {
		_CancelPatient = cancelled;		
	}
	
	@Override //TN148
	public boolean isUserPermissionsActive(){
		return ServiceHelper.getPermissionManagerService().isUserPermissionsActive();
	}
	
	@Override //TN148
	public List<String> getRoles() {
		ArrayList<String> ret = null;
		Map<String, String> map = ServiceHelper.getPermissionManagerService().getRolePwds();
		if (map != null)
			return new ArrayList<String>(map.keySet());
		return ret;
	}
	

}
