package ch.msf.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


/**
 * A country definition
 * This class is just used as a reference
 * (see SelectedCountry for entities that are scoping a data collection)
 * @author cmi
 *
 */
@Entity
@Table(name="AllCountries")
public class Country {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="CTY_id")
	private Long id;
    
	@Column(name="CTY_Code")
	private String _Code;
	
	@Column(name="CTY_Continent")
	private String _Continent;
	
	@Column(name="CTY_Region")
	private String _Region;
	
	@OneToMany(mappedBy = "_Country" , fetch=FetchType.EAGER) 
	private List<CountryName> _CountryNames = new ArrayList<CountryName>();
	
	
    
	public Country() {
	}


    public List<CountryName> getCountryNames() {
		return _CountryNames;
	}
    

	public void setCountryNames(List<CountryName> countryNames) {
		_CountryNames = countryNames;
	}
    
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _Code +"-"+ _Continent +"-"+ _Region;
	}   
	
	public String getContinent() {
		return _Continent;
	}

	public void setContinent(String continent) {
		_Continent = continent;
	}

	public String getRegion() {
		return _Region;
	}

	public void setRegion(String region) {
		_Region = region;
	}

	public String getCode() {
		return _Code;
	}

	public void setCode(String code) {
		_Code = code;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_Code == null) ? 0 : _Code.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Country other = (Country) obj;
		if (_Code == null) {
			if (other._Code != null)
				return false;
		} else if (!_Code.equals(other._Code))
			return false;
		return true;
	}

	public Long getId() {
		return id;
	}

}
