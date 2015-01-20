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
@Table(name="Patient")
public class Patient  implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PAT_id")
	private Long id;

//	@Column(name = "PAT_IDENTIFIER", unique=true, nullable = false)
//	private String _Identifier;
//	
//	@Column(name = "PAT_IDENT_TYPE", nullable = false)
//	private int _IdentifierType;
	
	@Column(name = "PAT_NAME", nullable = false)
	private String _FamilyName;
	
	@Column(name = "PAT_FIRSTNAME", nullable = false)
	private String _FirstName;
	
	@Column(name = "PAT_SEX")
	private String _Sex;
	
	@Column(name = "PAT_BIRTH_DATE")
	private Date _BirthDate;
	
	@OneToMany(mappedBy = "_Patient", /*fetch=FetchType.EAGER,*/ cascade=CascadeType.ALL, orphanRemoval=true)	// 
	private List<PatientIdValue> _PatientIdValues;
	
	
	@OneToMany(mappedBy = "_Patient", /*fetch=FetchType.EAGER,*/ cascade=CascadeType.ALL, orphanRemoval=true)	// 
	private List<PatientIdentifier> _PatientIdentifiers;
	
	
	@Column(name = "PAT_INDEX", unique=true, nullable = false)
	private Long _Index;
	
	
	/////////////// technical fields

	@Column(name = "PAT_CREA_DATE")
	private Date _CreationDate;	
	
	
	@Column(name = "PAT_MODIF_DATE")
	private Date _ModificationDate;
	
	
	@Column(name = "PAT_RETIRED")
	private boolean _Retired;
	
	
	@Column(name = "PAT_RETIRED_DATE")
	private Date _RetiredDate;


	
	
	
	// TODO DEFINE PATIENT ID
//	private transient String _PatientId; 


	// a list patient configured properties
	// defined at configuration time
	// loaded at object construction
//	private static transient List<PropertyName> _PropertyNames;
	
	
    
	public Patient() {
//		System.out.println("::This empty constructor is used...");
	}



	public List<PatientIdValue> getIdValues() {
		if (_PatientIdValues == null)
			_PatientIdValues = new ArrayList<PatientIdValue>();
		return _PatientIdValues;
	}

	
	public void setPatientIdValues(List<PatientIdValue> patientIdValues) {
		_PatientIdValues = patientIdValues;
	}
	

	public Long getId() {
		return id;
	}


	public String getFamilyName() {
		return _FamilyName;
	}



	public void setFamilyName(String _Name) {
		this._FamilyName = _Name;
	}

	public String getFirstName() {
		return _FirstName;
	}

	public void setFirstName(String _FirstName) {
		this._FirstName = _FirstName;
	}



	public String getSex() {
		return _Sex;
	}

	public void setSex(String sex) {
		this._Sex = sex;
	}
	

	public Date getBirthDate() {
		return _BirthDate;
	}

	public void setBirthDate(Date date) {
		this._BirthDate = date;
	}
	
	
	public Long getIndex() {
		return _Index;
	}

	public void setIndex(Long _Index) {
		this._Index = _Index;
	}	
////////////////////
	
	
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
	
	
//	public String getPatientId() {
//		return _PatientId;
//	}
//
//
//
//	public void setPatientId(String patientId) {
//		this._PatientId = patientId;
//	}



	public List<PatientIdentifier> getPatientIdentifiers() {
		if (_PatientIdentifiers == null)
			_PatientIdentifiers = new ArrayList<PatientIdentifier>();
		return _PatientIdentifiers;
	}

	public void addPatientIdentifier(PatientIdentifier patientIdentifier) {
		getPatientIdentifiers().add(patientIdentifier);
	}



	@Override
	public String toString() {
		return "Patient [_FamilyName=" + _FamilyName + ", _FirstName="
				+ _FirstName + ", _Sex=" + _Sex + ", _BirthDate=" + _BirthDate
				+ ", _PatientIdentifiers=" + _PatientIdentifiers + "]";
	}
	

}

//class PropertyName{
//	
//	public String _MethodeName;
//	public String _ResourceId;
//	
//}
