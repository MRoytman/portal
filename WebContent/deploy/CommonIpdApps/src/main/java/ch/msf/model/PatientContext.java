package ch.msf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * @author cmi
 * this class is used to locate a patient in a context (country, project, carecenter)
 * there is a one to one relationship with a patient.
 * when an instance of this class is created, it must be associated with a new or existing Patient
 * The deletion of an instance normally implies the deletion of associated patient 
 */

@Entity
@Table(name="PatientContext")
public class PatientContext  implements Cloneable, Comparable<PatientContext> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PAC_id")
	private Long id;
	
	
	@Column(name = "PAC_NAME")
	// will/can be used to differentiate the contexts in an integrated system
	private String _ContextName;

	@OneToOne
	@JoinColumn(name="PAC_Patient", nullable = false)
	private Patient _Patient;
	
	@OneToOne
	@JoinColumn(name="PAC_SELCTY", nullable = false)
	private SelectedCountry _SelectedCountry;
	
	
	@OneToOne
	@JoinColumn(name="PAC_SELPRJ", nullable = false)
	private Project _SelectedProject;	
	
	
	@OneToOne
	@JoinColumn(name="PAC_SELCCAR", nullable = false)
	private CareCenter _SelectedCareCenter;	
	
//	@Column(name = "PAC_CREA_DATE")
//	private Date _CreationDate;	
//	
//	
//	@Column(name = "PAC_MODIF_DATE")
//	private Date _ModificationDate;
	
//	
//	@Column(name = "PAC_RETIRED")
//	private boolean _Retired;
//	
//	
//	@Column(name = "PAC_RETIRED_DATE")
//	private Date _RetiredDate;
	
	
	

	@Override
	public String toString() {
		return "PatientContext [_Patient=" + _Patient.getFamilyName() + ", "+ _Patient.getFirstName() + ", _SelectedCountry="
				+ _SelectedCountry.getCode() + ", _SelectedProject=" + _SelectedProject.getName()
				+ ", _SelectedCareCenter=" + _SelectedCareCenter.getName() + "]";
	}


	public Long getId() {
		return id;
	}


	public Patient getPatient() {
		return _Patient;
	}




	public void setPatient(Patient patient) {
		this._Patient = patient;
	}
	
	
	public SelectedCountry getSelectedCountry() {
		return _SelectedCountry;
	}


	public void setSelectedCountry(SelectedCountry _SelectedCountry) {
		this._SelectedCountry = _SelectedCountry;
	}


	public Project getSelectedProject() {
		return _SelectedProject;
	}


	public void setSelectedProject(Project _SelectedProject) {
		this._SelectedProject = _SelectedProject;
	}


	public CareCenter getSelectedCareCenter() {
		return _SelectedCareCenter;
	}


	public void setSelectedCareCenter(CareCenter _SelectedCareCenter) {
		this._SelectedCareCenter = _SelectedCareCenter;
	}
	
	/**
	 * @return the _ContextName
	 */
	public String getContextName() {
		return _ContextName;
	}


	/**
	 * @param _ContextName the _ContextName to set
	 */
	public void setContextName(String _ContextName) {
		this._ContextName = _ContextName;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PatientContext other = (PatientContext) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	@Override
	public int compareTo(PatientContext other) {
		// ok for current purposes...
		return getId().compareTo(other.getId());
	}


	
}
