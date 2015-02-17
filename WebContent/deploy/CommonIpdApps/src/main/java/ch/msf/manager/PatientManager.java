package ch.msf.manager;

import java.util.List;

import ch.msf.form.ParamException;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.PatientIdentifier;
import ch.msf.model.SelectionContext;

public interface PatientManager {

	Patient getSelectedPatient(Long patientId);

	List<PatientContext> getSelectedPatient(PatientIdentifier patientIdentifier, Boolean patientRetired);

	List<PatientContext> getAllSelectedPatientContext();// not in use yet

	List<PatientContext> getAllSelectedPatientContext(int yearDate) throws ParamException;// not in use yet

	List<PatientContext> getAllSelectedPatientContext(Patient patient);

	List<PatientContext> getAllPatientContext(Patient patient);
	
	PatientContext getPatientContext(Long patientContextId);

//	List<PatientContext> getAllSelectedPatientContext(SelectionContext patientContext, String date, String idOrName, boolean testOnId) throws ParamException;
	// TN141 search on both id and names
	List<PatientContext> getAllSelectedPatientContext(SelectionContext patientContext, String date, String idOrName) throws ParamException;

	// TN134 entryform
	List<PatientContext> getAllSelectedPatientContext(SelectionContext selectionContext, String yearDate, String idOrName, boolean testOnId, int pageNumber) throws ParamException;
	// TN142 entryform
	List<PatientContext> getAllSelectedPatientContext(SelectionContext selectionContext, String yearDate, String searchToken, String patientConcept) throws ParamException;

	PatientContext saveSelectedPatientContext(PatientContext patientContext) throws ParamException;

	void removeSelectedPatientContext(PatientContext patientContext);

	// TN83 entryform:
	Patient readDBPatientInfo(Patient patient, boolean all);

	//taivd add
	List<Patient> getAllPatient();

}
