package ch.msf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * allows to identify the patient.
 * the patient can have a list of PatientIdentifier
 * @author cmi
 *
 */

@Entity
@Table(name="PatientIdentifier", uniqueConstraints=@UniqueConstraint(columnNames = {"PID_IDENTIFIER", "PID_IDENT_TYPE"}))
public class PatientIdentifier  implements Cloneable {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PID_id")
	private Long id;

//	@Column(name = "PID_IDENTIFIER", unique=true, nullable = false)
	@Column(name = "PID_IDENTIFIER", nullable = false)
	private String _Identifier;
	
	@Column(name = "PID_IDENT_TYPE", nullable = false)
	private int _IdentifierType;
	

	@ManyToOne
	@JoinColumn(name="PID_Patient")
	private Patient _Patient;
	
    
	public PatientIdentifier() {
//		System.out.println("::This empty constructor is used...");
	} 
	
	
	public PatientIdentifier(String patientIdentifier, Integer identifierType) {
		_Identifier = patientIdentifier;
		_IdentifierType = identifierType;
	}


	public Long getId() {
		return id;
	}
	
	
	public String getIdentifier() {
		return _Identifier;
	}



	public void setIdentifier(String identifier) {
		_Identifier = identifier;
	}



	public int getIdentifierType() {
		return _IdentifierType;
	}


	public void setIdentifierType(int identifierType) {
		_IdentifierType = identifierType;
	}



	public Patient getPatient() {
		return _Patient;
	}

	public void setPatient(Patient patient) {
		this._Patient = patient;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_Identifier == null) ? 0 : _Identifier.hashCode());
		result = prime * result + _IdentifierType;
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
		PatientIdentifier other = (PatientIdentifier) obj;
		
		if (_IdentifierType != other._IdentifierType)
			return false;
		
		if (_Identifier == null) {
			if (other._Identifier != null)
				return false;
		} else if (!_Identifier.equals(other._Identifier))
			return false;

		return true;
	}


}

