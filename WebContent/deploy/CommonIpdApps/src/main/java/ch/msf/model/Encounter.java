
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


/**
 * This class is normally similar to openmrs encounter.
 * @author cmi
 *
 */
@Entity
@Table(name="Encounter")
public class Encounter  implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ENC_id")
	private Long id;
	
	@Column(name = "ENC_DATE")
	// business creation date (that can be different from when the program is run)
	// this should be modifiable
	private Date _Date;
	
	
//	@Column(name = "ENC_PLACE")
//	private String _Place;

	@OneToOne
	@JoinColumn(name="ENC_Patient", nullable = false)
	private Patient _Patient;
	
	@Column(name = "ENC_TYPE")
	private String _Type;
	

	@OneToMany(mappedBy = "_Encounter", /*fetch=FetchType.EAGER,*/ cascade=CascadeType.ALL, orphanRemoval=true)	// 
	private List<ConceptIdValue> _ConceptIdValues;
	
	
	// 0 = not validated, 1 = validation pending, 2 = validated 
	@Column(name = "ENC_STATUS")
	private Integer _Status;
	
	
	/////////////// technical fields
	
	@Column(name = "ENC_CREA_DATE")
	private Date _CreationDate;	
	
	
	@Column(name = "ENC_MODIF_DATE")
	private Date _ModificationDate;
	
	
	@Column(name = "ENC_RETIRED")
	private boolean _Retired;
	
	
	@Column(name = "ENC_RETIRED_DATE")
	private Date _RetiredDate;

	
    
	public Encounter() {
//		System.out.println("::This empty constructor is used...");
	}



	public List<ConceptIdValue> getIdValues() {
		if (_ConceptIdValues == null)
			_ConceptIdValues = new ArrayList<ConceptIdValue>();
		return _ConceptIdValues;
	}

	
	public void setIdValues(List<ConceptIdValue> idValues) {
		_ConceptIdValues = idValues;
	}
	

	public Long getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return "Encounter [_Date=" + _Date +/* ", _Place=" + _Place +*/ ", _Type="
				+ _Type + "]";
	}



	public Date getDate() {
		return _Date;
	}

	public void setDate(Date _Date) {
		this._Date = _Date;
	}

//	public String getPlace() {
//		return _Place;
//	}
//
//	public void setPlace(String _Place) {
//		this._Place = _Place;
//	}
	

	public Patient getPatient() {
		return _Patient;
	}

	public void setPatient(Patient patient) {
		this._Patient = patient;
	}

	public String getType() {
		return _Type;
	}


	public void setType(String type) {
		_Type = type;
	}
	
	

	public Integer getStatus() {
		return _Status;
	}


	public void setStatus(Integer status) {
		_Status = status;
	}

	
	
	public Date getCreationDate() {
		return _CreationDate;
	}



	public void setCreationDate(Date creationDate) {
		_CreationDate = creationDate;
	}



	public Date getModificationDate() {
		return _ModificationDate;
	}



	public void setModificationDate(Date modificationDate) {
		_ModificationDate = modificationDate;
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

