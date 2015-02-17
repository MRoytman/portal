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
	public List<Encounter> getAllEncountersByPatient(Patient currentPatient);
	public List<Encounter> getAllEncounters();
	List<String> getCurrentRoleEncounterIds();
}
