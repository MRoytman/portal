package ch.msf.manager;

import java.util.List;

import ch.msf.form.ParamException;
import ch.msf.model.Encounter;
import ch.msf.model.Patient;

public interface EncounterManager {
	
	public Encounter getEncounter(Long EncounterId) ;

	public Encounter saveEncounter(Encounter encounter) throws ParamException ;

	public void removeEncounter(Encounter encounter);

	public List<Encounter> getAllPatientEncounters(Patient currentPatient, boolean retired);
	public List<Encounter> getAllEncounters();
	/**
	 * 
	 * @param currentRoleName
	 * @return the list of encounter id for the current role 
	 */
	List<String> getCurrentRoleEncounterIds();
}
