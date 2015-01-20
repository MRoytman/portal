package ch.msf.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;

import ch.msf.model.Country;
import ch.msf.model.CountryName;


public class AllCountriesManagerImpl implements
		AllCountriesManager {

	private HashMap<String, Country> _AllCountries = new HashMap<String, Country>();

	// a map of <countryCodeLocale, CountryName>
	private HashMap<String, CountryName> _AllCountriesNames = new HashMap<String, CountryName>();

	@Override
	public List<Country> getAllCountries() {

		// TODO move to DAO
		if (_AllCountries.isEmpty()) {
			buildAllCountriesMap();
		}

		return new ArrayList(_AllCountries.values());
	}


	@Override
	public List<CountryName> getAllCountriesNames(String locale) {

		// TODO move to DAO
		List<CountryName> cn = new ArrayList<CountryName>();

		if (_AllCountriesNames.isEmpty()) {

			buildAllCountriesNamesMap();

		}
		for (String countryLocale : _AllCountriesNames.keySet()) {
			String[] country_Locale = countryLocale.split("\\|");
			if (country_Locale[1].equals(locale)) {
				// System.out.println(country_Locale + "");
				cn.add(_AllCountriesNames.get(countryLocale));
			}
		}

		return cn;
	}


	private void buildAllCountriesNamesMap() {
		EntityManager em = getDbManager().startTransaction();

		// READ ALL COUNTRIES NAMES...
		List<CountryName> countriesNames = em.createQuery(
		// "select cl from CountryName as cl where cl._Locale = :locale ")
		// .setParameter("locale", locale).getResultList();
				"select cl from CountryName as cl").getResultList();

		getDbManager().endTransaction(em);

		for (CountryName countryName : countriesNames) {
			_AllCountriesNames.put(
					countryName.getCode() + "|" + countryName.getLocale(),
					countryName);
		}

	}

	private void buildAllCountriesMap() {
		EntityManager em = getDbManager().startTransaction();

		// READ ALL COUNTRIES...
		List<Country> countries = em.createQuery("select c from Country c")
				.getResultList();

		getDbManager().endTransaction(em);

		for (Country country : countries) {
			_AllCountries.put(country.getCode(), country);
		}

	}

	@Override
	public Country getCountry(String countryCode) {

		if (_AllCountriesNames.isEmpty()) {
			buildAllCountriesMap();
		}
		return _AllCountries.get(countryCode);
	}

	@Override
	public String getCountryNameLabel(Country country, String locale) {

		for (CountryName countryName : country.getCountryNames()) {
			if (countryName.getCode().equals(country.getCode()) && countryName.getLocale().equals(locale))
				return countryName.getLabel();
		}
		return null;
	}

	@Override
	public void saveCountryConfig(ArrayList<Country> countries,
			ArrayList<CountryName> countriesNames)
			throws IOException {

		EntityManager em = getDbManager().startTransaction();

		// save the countries
		for (Country country : countries) {
			em.persist(country);
		}

		// save the countries localization
		for (CountryName countryName : countriesNames) {
			em.persist(countryName);
		}

		getDbManager().endTransaction(em);

	}

	// /**
	// * @return the countries
	// */
	// public static List<CountryName> getAllCountriesNames(String locale) {
	// ArrayList<CountryName> retList = new ArrayList<CountryName>();
	// List<CountryName> allCountriesNames = getAllCountries()
	// .getCountriesNames();
	// for (CountryName countryName : allCountriesNames) {
	// if (countryName.getLocale().equals(locale)) {
	// retList.add(countryName);
	// }
	// }
	// return retList;
	// }
	
	
	// ///////////////////////////
	// spring...

	
	
	private DbManager _DbManager;
	
	public DbManager getDbManager() {
		return _DbManager;
	}


	public void setDbManager(DbManager _DbManager) {
		this._DbManager = _DbManager;
	}	

}
