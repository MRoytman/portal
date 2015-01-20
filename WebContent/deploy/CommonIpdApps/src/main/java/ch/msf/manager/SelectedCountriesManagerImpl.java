package ch.msf.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ch.msf.error.ConfigException;
import ch.msf.model.CareCenter;
import ch.msf.model.Country;
import ch.msf.model.CountryName;
import ch.msf.model.Project;
import ch.msf.model.SelectedCountry;

public class SelectedCountriesManagerImpl implements SelectedCountriesManager {

	private HashMap<String, SelectedCountry> _AllSelectedCountries = new HashMap<String, SelectedCountry>();

	@Override
	public SelectedCountry getSelectedCountry(String countryCode) {

		if (_AllSelectedCountries.isEmpty()) {
			buildAllSelectedCountriesMap(false);
		}
		return _AllSelectedCountries.get(countryCode);
	}

	@Override
	public List<SelectedCountry> getDbSelectedCountries(Boolean notRetired) {

		// if (_AllSelectedCountries.isEmpty()) {
		buildAllSelectedCountriesMap(notRetired);
		// }
		return new ArrayList<SelectedCountry>(_AllSelectedCountries.values());
	}

	@Override
	/**
	 * 
	 * @param countryNamesLabelsToFind
	 * @param locale
	 * @return a created list of new SelectedCountry (not saved)
	 */
	public List<SelectedCountry> createCountriesFromLabel(List<String> countryNamesLabelsToFind, String locale) {
		ArrayList<SelectedCountry> retList = new ArrayList<SelectedCountry>();

		for (String countryNameLabelToFind : countryNamesLabelsToFind) {

			boolean found = false;
			for (CountryName countryName : getAllCountriesManager().getAllCountriesNames(locale)) {

				if (countryName.getLabel().equals(countryNameLabelToFind)
				// && countryName.getLocale().equals(locale)
				) {
					SelectedCountry selectCountry = new SelectedCountry(countryName.getCountry());
					retList.add(selectCountry);
					found = true;
					break;
				}
			}
			if (!found) {
				System.out.println(getClass().getName() + "::getCountriesFromLabel():PROBLEM, country not found! countryName=" + countryNameLabelToFind);
				// TODO exception est masquée et ne vient pas à l'utilisateur?
				throw new ConfigException(getClass().getName() + "::getCountriesFromLabel():PROBLEM, country not found! countryName=" + countryNameLabelToFind);
			}
		}

		return retList;
	}

	/**
	 * 
	 * @param countryNamesLabelsToFind
	 * @param locale
	 * @return a created list of new SelectedCountry (not saved)
	 */
	@Override
	public List<SelectedCountry> createCountriesFromCode(TreeSet<String> countryCodes) {
		ArrayList<SelectedCountry> retList = new ArrayList<SelectedCountry>();

		for (String countryCode : countryCodes) {
			Country country = getAllCountriesManager().getCountry(countryCode);
			if (country == null) {
				throw new ConfigException(getClass().getName() + "::createCountriesFromCode():PROBLEM, country not found! countryCode=" + countryCode);
			}
			SelectedCountry selectCountry = new SelectedCountry(country);
			retList.add(selectCountry);
		}
		return retList;
	}

	@Override
	/**
	 * 
	 */
	public List<SelectedCountry> getSelectedCountriesFromLabel(List<String> countryNamesLabelsToFind, String locale) {
		ArrayList<SelectedCountry> retList = new ArrayList<SelectedCountry>();

		for (String countryNameLabelToFind : countryNamesLabelsToFind) {
			SelectedCountry selectCountry = null;
			// for (CountryName countryName : getAllCountriesNames(locale)) {
			// boolean found = false;
			for (CountryName countryName : getAllCountriesManager().getAllCountriesNames(locale)) {
				// for (String countryNameLabelToFind :
				// countryNamesLabelsToFind) {
				if (countryName.getLabel().equals(countryNameLabelToFind)
				// && countryName.getLocale().equals(locale)
				) {
					selectCountry = getSelectedCountry(countryName.getCode());// new
					// SelectedCountry(countryName.getCountry());
					retList.add(selectCountry);
					// found = true;
					break;
				}
			}
			if (selectCountry != null) {
				System.out.println(getClass().getName() + "::getCountriesFromLabel():PROBLEM, country not found! countryName=" + countryNameLabelToFind);
				// TODO exception est masquée et ne vient pas à l'utilisateur
				throw new ConfigException(getClass().getName() + "::getCountriesFromLabel():PROBLEM, country not found! countryName=" + countryNameLabelToFind);
			}
		}

		return retList;
	}

	/**
	 * 
	 * @param selectedCountry
	 */
	@Override
	public SelectedCountry saveSelectedCountry(SelectedCountry selectedCountry) {
		// Create new SelectedCountry

		EntityManager em = getDbManager().startTransaction();
		selectedCountry = em.merge(selectedCountry);
		// TN73 (admin)/TN80 (entryform) add code to project&carecenter
		// detect if each project has a code...
		boolean changed = false;
		for (Project project : selectedCountry.getProjects()) {
			if (project.getCode() == null || project.getCode().equals("")) {
				// if here, the projects are not issued from the configuration
				changed = true;
				project.setCode(project.getId().toString());
			}
			for (CareCenter careCenter : project.getCareCenters()) {
				if (careCenter.getCode() == null || careCenter.getCode().equals("")) {
					changed = true;
					careCenter.setCode(careCenter.getId().toString());
				}
			}
		}
		if (changed) // save again
			selectedCountry = em.merge(selectedCountry);
		// end TN73 (admin)/TN80 (entryform)

		getDbManager().endTransaction(em);

		_AllSelectedCountries.put(selectedCountry.getCode(), selectedCountry);

		return selectedCountry;
	}

	/**
	 * 
	 * @param selectedCountry
	 */
	@Override
	public void removeSelectedCountry(SelectedCountry selectedCountry) {

		EntityManager em = getDbManager().startTransaction();
		SelectedCountry sc = em.merge(selectedCountry);

		// delete projects
		// for(Project project: sc.getProjects()){
		// for(CareCenter careCenter: project.getCareCenters()){
		// em.remove(careCenter);
		// }
		// em.remove(project);
		// }
		em.remove(sc);
		getDbManager().endTransaction(em);
		_AllSelectedCountries.remove(sc.getCode());
	}

	/**
	 * Called by Entryform
	 * 
	 * @param localeToSearch
	 * @param notRetired
	 *            : if true, return only countries names that are not retired
	 * @return a list of labels for the selected countries for the given locale
	 */
	@Override
	// TODO à optimiser avec getCountryNameLabel ?
	public List<String> getDbAllSelectedCountrieNameLabels(String localeToSearch, boolean notRetired) {//
		List<String> countrieNames = new ArrayList<String>();
		List<SelectedCountry> selCountries = getDbSelectedCountries(notRetired);
		for (SelectedCountry selectedCountry : selCountries) {
			if (!notRetired || !selectedCountry.isRetired()) {
				if (selectedCountry.getCountry() == null) {
					System.out.println("selectedCountry.getCountry() = NULL!!" + selectedCountry.getCode());
				}
				for (CountryName countryName : selectedCountry.getCountry().getCountryNames()) {
					if (localeToSearch.equals(countryName.getLocale())) {
						countrieNames.add(countryName.getLabel());
					}
				}
			}
		}
		return countrieNames;
	}

	@Override
	public List<String> getDbProjectNames(String countryLabelToSearch, boolean notRetired) {

		List<String> projectNames = new ArrayList<String>();
		SelectedCountry selectedCountry = getDbSelectedCountryFromLabel(countryLabelToSearch, notRetired);

		if (selectedCountry != null) {
			for (Project project : selectedCountry.getProjects()) {
				if (!notRetired || !project.isRetired())
					projectNames.add(project.toString());
			}
		} else
			System.out.println("getDbProjectNames() Error: projects = null!");

		return projectNames;
	}

	@Override
	public List<String> getDbCareCenters(String countryLabelToSearch, String projectNameToSearch, boolean notRetired) {
		System.out.println(getClass().getName() + "::getDbCareCenters()...");

		List<String> careCenterNames = new ArrayList<String>();
		Project project = getDbSelectedProjectFromName(countryLabelToSearch, projectNameToSearch, notRetired);

		if (project != null) {
			for (CareCenter careCenter : project.getCareCenters()) {
				if (!notRetired || !careCenter.isRetired())
					careCenterNames.add(careCenter.getName());
			}
		} else
			System.out.println("getDbCareCenters() Error: projects = null!");

		return careCenterNames;
	}

	/**
	 * called by entryform only
	 * 
	 * @param countryLabelToSearch
	 * @param notRetired
	 *            : if true, return only countries names that are not retired
	 * @return
	 */
	@Override
	public SelectedCountry getDbSelectedCountryFromLabel(String countryLabelToSearch, boolean notRetired) {

		List<SelectedCountry> selectedCountries = getDbSelectedCountries(notRetired);
		for (SelectedCountry selectedCountry : selectedCountries) {
			if (!notRetired || !selectedCountry.isRetired())
				for (CountryName countryName : selectedCountry.getCountry().getCountryNames()) {
					if (countryLabelToSearch.equals(countryName.getLabel())) {
						return selectedCountry;
					}
				}
		}
		return null;
	}

	/**
	 * called by entryform only
	 * 
	 * @param countryLabelToSearch
	 * @param projectNameToSearch
	 * @param notRetired
	 *            : if true, return only countries names that are not retired
	 * @return
	 */
	@Override
	public Project getDbSelectedProjectFromName(String countryLabelToSearch, String projectNameToSearch, boolean notRetired) {

		SelectedCountry selectedCountry = getDbSelectedCountryFromLabel(countryLabelToSearch, notRetired);

		if (selectedCountry != null) {
			for (Project project : selectedCountry.getActiveProjects()) {
				if (projectNameToSearch.equals(project.getName())) {
					return project;
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param countryLabelToSearch
	 * @param projectNameToSearch
	 * @param careCenterToSearch
	 * @param notRetired
	 *            : if true, return only countries names that are not retired
	 * @return
	 */
	@Override
	public CareCenter getDbSelectedCareCenterFromName(String countryLabelToSearch, String projectNameToSearch, String careCenterToSearch, boolean notRetired) {

		Project project = getDbSelectedProjectFromName(countryLabelToSearch, projectNameToSearch, notRetired);

		if (project != null) {
			for (CareCenter carecenter : project.getCareCenters()) {
				if (careCenterToSearch.equals(carecenter.getName()))
					return carecenter;

			}
		}
		return null;
	}

	private void buildAllSelectedCountriesMap(Boolean notRetired) {
		EntityManager em = getDbManager().startTransaction();

		// READ ALL COUNTRIES...
		String strQuery = "select c from SelectedCountry c ";
		if (notRetired != null && notRetired) {
			strQuery += " where c._Retired = :retired ";
		}
		Query qry = em.createQuery(strQuery);

		if (notRetired != null && notRetired) {
			qry.setParameter("retired", false);
		}
		List<SelectedCountry> countries = qry.getResultList();

		for (SelectedCountry selectedCountry : countries) {
			_AllSelectedCountries.put(selectedCountry.getCode(), selectedCountry);
			for (Project project : selectedCountry.getProjects()) {
				project.setSelectedCountry(selectedCountry);
				for (CareCenter carecenter : project.getCareCenters()) {
					carecenter.setProject(project);
				}
			}
		}
		getDbManager().endTransaction(em);

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

}
