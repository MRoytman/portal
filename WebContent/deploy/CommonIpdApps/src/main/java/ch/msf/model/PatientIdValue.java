package ch.msf.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PatientIdValue")
public class PatientIdValue  implements Cloneable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="PIV_id")
	private Long id;
    

	@ManyToOne
	@JoinColumn(name="PIV_Patient", nullable = false)
	private Patient _Patient;
	
	
	@Column(name="PIV_CONCEPT_ID", nullable = false)
	private String _ConceptId;
	
	
	@Column(name="PIV_CONCEPT_VAL")
	private String _ConceptValue;

    
	public PatientIdValue() {
//		System.out.println("::This empty constructor is used...");
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




	public String getConceptId() {
		return _ConceptId;
	}




	public void setConceptId(String conceptId) {
		this._ConceptId = conceptId;
	}




	public String getConceptValue() {
		return _ConceptValue;
	}




	public void setConceptValue(String conceptValue) {
		this._ConceptValue = conceptValue;
	}
}
