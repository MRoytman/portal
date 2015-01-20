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
 * This class represents a pair (key, value) and is similar to openmrs concept.
 * 
 * @author cmi
 * 
 */
@Entity
@Table(name = "ConceptIdValue")
public class ConceptIdValue implements Cloneable /*, CheckableI*/ {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CIV_id")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "CIV_Section")
	private Section _Section;

	@Column(name = "CIV_CONCEPT_ID")
	private String _ConceptId;

	@Column(name = "CIV_CONCEPT_VAL")
	private String _ConceptValue;

	public ConceptIdValue() {
		// System.out.println("::This empty constructor is used...");
	}

	public Long getId() {
		return id;
	}

	public Section getSection() {
		return _Section;
	}

	public void setSection(Section Encounter) {
		this._Section = Encounter;
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

	
	/*
	@Override
	public String getKey() {
		return getConceptId();
	}

	@Override
	public String getValue() {
		return getConceptValue();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_ConceptId == null) ? 0 : _ConceptId.hashCode());
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
		ConceptIdValue other = (ConceptIdValue) obj;
		if (_ConceptId == null) {
			if (other._ConceptId != null)
				return false;
		} else if (!_ConceptId.equals(other._ConceptId))
			return false;
		return true;
	}

	*/
}
