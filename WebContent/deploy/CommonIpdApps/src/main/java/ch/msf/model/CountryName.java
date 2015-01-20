package ch.msf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


/**
 * A country translated label 
 * This class is just used as a reference
 * @author cmi
 *
 */

@Entity
@Table(name = "AllCountries_loc")
public class CountryName {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CTY_LC_id")
	private Long id;

	@Column(name = "CTY_Code")
	private String _Code;

	@Column(name = "CTY_LC_locale_code")
	private String _Locale;

	@Column(name = "CTY_LC_localized_data")
	private String _Label;

	@ManyToOne
	@JoinColumn(name = "CTY_LC_Country_id")
	private Country _Country;

	public Country getCountry() {
		return _Country;
	}

	public void setCountry(Country country) {
		_Country = country;
	}

	public String getCode() {
		return _Code;
	}

	public void setCode(String code) {
		_Code = code;
	}

	public String getLocale() {
		return _Locale;
	}

	public void setLocale(String locale) {
		_Locale = locale;
	}

	public CountryName(String name) {
		_Label = name;
	}

	public CountryName() {
		_Label = "-";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return _Label;
	}

	/**
	 * @return the name
	 */
	public String getLabel() {
		return _Label;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setLabel(String name) {
		_Label = name;
	}

}
