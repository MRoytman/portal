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
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="Project")
public class Project implements Cloneable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PRJ_id")
	private Long id;

	@Column(name = "PRJ_CODE")
	private String _Code;
	
	@Column(name = "PRJ_name")
	private String _Name;
	
	
	@Column(name = "PRJ_RETIRED")
	private boolean _Retired;
	
	
	@Column(name = "PRJ_RETIRED_DATE")
	private Date _RetiredDate;
    
	@OneToMany(/*mappedBy = "_Project", */cascade=CascadeType.ALL)// use CascadeType.DELETE_ORPHAN?
    private List<CareCenter> _CareCenters;
    
//	@ManyToOne
//	@JoinColumn(name = "PRJ_SelCountry_id")
    private transient SelectedCountry _SelectedCountry;


	public Project(){
    	_Name = "-";
    }
    
    public Project(String name){
    	_Name = name;
    }
    

    
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
//		return _Code+":"+_Name;	// pb with SimpleEntityList
		return _Name;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	
	public String getCode() {
		return _Code;
	}

	public void setCode(String _Code) {
		this._Code = _Code;
	}

	
	/**
	 * @return the name
	 */
	public String getName() {
		return _Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		_Name = name;
	}
	
    
    public SelectedCountry getSelectedCountry() {
		return _SelectedCountry;
	}

	public void setSelectedCountry(SelectedCountry selectedCountry) {
		_SelectedCountry = selectedCountry;
	}
	
	
	/**
	 * @return the _CareCenters
	 */
	public List<CareCenter> getCareCenters() {
		if (_CareCenters == null)
			_CareCenters = new ArrayList<CareCenter>();
		return _CareCenters;
	}
	/**
	 * @param careCenters the _CareCenters to set
	 */
	public void setCareCenters(List<CareCenter> careCenters) {
		_CareCenters = careCenters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if (_Code != null)
			result = prime * result + ((_Code == null) ? 0 : _Code.hashCode());
		else
			result = prime * result + ((_Name == null) ? 0 : _Name.hashCode());
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
		Project other = (Project) obj;
		if (_Code != null) {
			if (_Code == null) {
				if (other._Code != null)
					return false;
			} else if (!_Code.equals(other._Code))
				return false;
		} else {
			if (_Name == null) {
				if (other._Name != null)
					return false;
			} else if (!_Name.equals(other._Name))
				return false;
		}
		return true;
	}

	
	public Object clone() {
		Object o = null;
		try {
			// On récupère l'instance à renvoyer par l'appel de la 
			// méthode super.clone()
			o = super.clone();
			Project sc = (Project)o;
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
