package ch.msf.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * THIS CLASS IS A CONTAINER OF ALL PATIENT ENCOUNTERS INFORMATION
 * 
 * @author cmi
 * 
 */
public class PatientEncounterInfo {

	private String _PatientTechId;

	private List<Encounter> _PatientEncounters;

	public String getPatientTechId() {
		return _PatientTechId;
	}

	public void setPatientTechId(String _PatientTechId) {
		this._PatientTechId = _PatientTechId;
	}

	public List<Encounter> getPatientEncounters() {
		return _PatientEncounters;
	}

	public void setPatientEncounters(List<Encounter> _PatientEncounters) {
		this._PatientEncounters = _PatientEncounters;
	}

	/**
	 * parse all encounters for conceptId. The encountes are supposed to be sorted by date ascending
	 * 
	 * @param conceptId
	 *            : the id to look for
	 * @param startDate
	 * @param endDate
	 * @return a list of value - date that match conceptid
	 */
	public List<ConceptDate> buildConceptSerie(String conceptId, Date startDate, Date endDate) {
		List<ConceptDate> ret = new ArrayList<ConceptDate>();

		//
		for (Encounter encounter : _PatientEncounters) {
			if (!encounter.getDate().before(startDate) && !encounter.getDate().after(endDate)) {
				List<ConceptIdValue> idValues = encounter.getIdValues();
				for (ConceptIdValue conceptIdValue : idValues) {
					if (conceptIdValue.getConceptId().equals(conceptId)) {
						ConceptDate conceptDate = new ConceptDate(encounter.getDate(), conceptIdValue.getConceptValue());
						ret.add(conceptDate);
					}
				}
			}
		}

		return ret;
	}

	// private String _CountryCode;
	// private String _ProjectCode;
	// private String _EncounterCode;
	// public String getCountryCode() {
	// return _CountryCode;
	// }
	// public void setCountryCode(String _CountryCode) {
	// this._CountryCode = _CountryCode;
	// }
	// public String getProjectCode() {
	// return _ProjectCode;
	// }
	// public void setProjectCode(String _ProjectCode) {
	// this._ProjectCode = _ProjectCode;
	// }
	// public String getEncounterCode() {
	// return _EncounterCode;
	// }
	// public void setEncounterCode(String _EncounterCode) {
	// this._EncounterCode = _EncounterCode;
	// }

}

class ConceptDate {
	public Date _Date;
	public String _ConceptValue;

	public ConceptDate(Date date, String conceptValue) {
		_Date = date;
		_ConceptValue = conceptValue;
	}
}
