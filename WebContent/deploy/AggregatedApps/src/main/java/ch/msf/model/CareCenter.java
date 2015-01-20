package ch.msf.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 
 * @author cmi
 * 
 */
@Entity
@Table(name = "CareCenter")
public class CareCenter implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CCAR_id")
	private Long id;

	@Column(name = "CCAR_CODE")
	private String _Code;

	@Column(name = "CCAR_name")
	private String _Name;

	@Column(name = "CCAR_RETIRED")
	private boolean _Retired;

	@Column(name = "CCAR_RETIRED_DATE")
	private Date _RetiredDate;

	// @ManyToOne
	// @JoinColumn(name = "CCAR_PRJ_id")
	private transient Project _Project;

	public Project getProject() {
		return _Project;
	}

	public void setProject(Project project) {
		_Project = project;
	}

	public CareCenter() {
		_Name = "-";
	}

	public CareCenter(String name) {
		_Name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
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
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		_Name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
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

	/*
	 * (non-Javadoc)
	 * 
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
		CareCenter other = (CareCenter) obj;
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
			CareCenter sc = (CareCenter)o;
			sc.id = null;	// TN74 Admin

		} catch (CloneNotSupportedException cnse) {
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
