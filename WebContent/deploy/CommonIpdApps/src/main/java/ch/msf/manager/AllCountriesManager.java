package ch.msf.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import ch.msf.model.Country;
import ch.msf.model.CountryName;
import ch.msf.model.SelectedCountry;


public interface AllCountriesManager {

	public List<Country> getAllCountries(/*boolean isAdmin*/);
	
	public List<CountryName> getAllCountriesNames(String locale);

	public String getCountryNameLabel(Country country, String locale);
	
	public Country getCountry(String countryCode);	

	public void saveCountryConfig(ArrayList<Country> countries, ArrayList<CountryName> countriesNames) throws IOException;


	
}
