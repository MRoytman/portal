package ch.msf.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name="SelectCountries")
public class SelectedCountry  implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="SELCTY_id")
	private Long id;
    
	@Column(name="SELCTY_Code")
	private String _Code;
    

	@OneToOne
	@JoinColumn(name="SELCTY_FK_CTY", nullable = false)
	private Country _CountryRef;


	@OneToMany(/*mappedBy = "_SelectedCountry", fetch=FetchType.EAGER,*/ cascade=CascadeType.ALL)	// use CascadeType.DELETE_ORPHAN?
	private List<Project> _Projects;
	
	
	@Column(name = "SELCTY_RETIRED")
	private boolean _Retired;
	
	
	@Column(name = "SELCTY_RETIRED_DATE")
	private Date _RetiredDate;
	
	
    
	public SelectedCountry() {
//		System.out.println("SelectedCountry::This empty constructor is used...");
	}

	
    
	public SelectedCountry( Country ref) {
//		this.id = id;
		_Code = ref.getCode();
		_CountryRef = ref;
	}



	/**
	 * @return the _Projects
	 */
	public List<Project> getProjects() {
		if (_Projects == null)
			_Projects = new ArrayList<Project>();
		return _Projects;
	}
	
	/**
	 * @return the _Projects
	 */
	public List<Project> getActiveProjects() {
		List<Project> activeProjects = new ArrayList<Project>();
		for(Project project: getProjects())
		{
			if (!project.isRetired())
				activeProjects.add(project);
		}
		return activeProjects;
	}
	
	/**
	 * @param _Projects the _Projects to set
	 */
	public void setProjects(List<Project> projects) {
		_Projects = projects;
	}
	

	public String getCode() {
		return _Code;
	}

	public void setCode(String code) {
		_Code = code;
	}
	
	

	public Country getCountry() {
		return _CountryRef;
	}

	public void setCountry(Country countryRef) {
		_CountryRef = countryRef;
	}
	

	public Long getId() {
		return id;
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
		SelectedCountry other = (SelectedCountry) obj;
		if (_Code == null) {
			if (other._Code != null)
				return false;
		} else if (!_Code.equals(other._Code))
			return false;
		return true;
	}
	
	public Object clone() {
		Object o = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			o = super.clone();
			SelectedCountry sc = (SelectedCountry)o;
			sc.id = null;	// TN74 Admin
			
		} catch(CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implémentons 
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}
		// on renvoie le clone
		return o;
	}
	

	public boolean isRetired() {
		return _Retired;
	}



	public void setRetired(boolean retired) {
		this._Retired = retired;
	}



	public Date getRetiredDate() {
		return _RetiredDate;
	}



	public void setRetiredDate(Date retiredDate) {
		this._RetiredDate = retiredDate;
	}
	

}
