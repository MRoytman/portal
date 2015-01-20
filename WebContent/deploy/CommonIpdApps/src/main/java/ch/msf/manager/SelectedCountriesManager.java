package ch.msf.manager;

import java.util.List;
import java.util.TreeSet;

import ch.msf.model.CareCenter;
import ch.msf.model.Project;
import ch.msf.model.SelectedCountry;

public interface SelectedCountriesManager {

	public SelectedCountry getSelectedCountry(String code);

	public SelectedCountry getDbSelectedCountryFromLabel(
			String countryLabelToSearch, boolean notRetired);

	public Project getDbSelectedProjectFromName(String countryLabelToSearch,
			String projectNameToSearch, boolean notRetired);

	public CareCenter getDbSelectedCareCenterFromName(
			String countryLabelToSearch, String projectNameToSearch,
			String careCenterToSearch, boolean notRetired);

	public List<SelectedCountry> getSelectedCountriesFromLabel(
			List<String> countryNamesLabelsToFind, String locale);

	public List<SelectedCountry> createCountriesFromLabel(
			List<String> countryNamesLabelsToFind, String locale);

	public SelectedCountry saveSelectedCountry(SelectedCountry selectedCountry);

	public void removeSelectedCountry(SelectedCountry selectedCountry);

	public List<SelectedCountry> getDbSelectedCountries(Boolean notRetired);

	public List<String> getDbAllSelectedCountrieNameLabels(String locale,
			boolean notRetired);

	public List<String> getDbProjectNames(String countryName, boolean notRetired);

	public List<String> getDbCareCenters(String countryLabelToSearch,
			String projectNameToSearch, boolean notRetired);

	public List<SelectedCountry> createCountriesFromCode(TreeSet<String> countryCodes);


}
