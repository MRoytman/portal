package ch.msf.manager;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ch.msf.model.ConceptIdValue;
import ch.msf.model.Encounter;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.PatientIdValue;
import ch.msf.model.PatientIdentifier;
import ch.msf.service.ServiceHelper;

public class ExportCSV {
	ConfigurationManagerBaseImpl config = ServiceHelper
			.getConfigurationManagerService();

	public String getPath(String strFileName) {
		return config.getMsfApplicationDir() + "\\" + strFileName;
	}

	public void ExportContext() {

		String strFileName1 = "PatientContext.csv";
		String strPath1 = getPath(strFileName1);
		PatientContext patientC = new PatientContext();
		try {
			FileWriter writer1 = new FileWriter(strPath1);

			writer1.append("ID Context");
			writer1.append(',');
			writer1.append("ID Patient");
			writer1.append(',');
			writer1.append("Context name");
			writer1.append(',');
			writer1.append("Selected Country");
			writer1.append(',');
			writer1.append("Selected Care Center");
			writer1.append(',');
			writer1.append("Selected Project");
			writer1.append('\n');
			List<PatientContext> lstPatientContext = null;
			lstPatientContext = ServiceHelper.getPatientManagerService()
					.getAllSelectedPatientContext();
			for (int i = 0; i < lstPatientContext.size(); i++) {
				System.out.println(lstPatientContext.get(i).toString());
				patientC = lstPatientContext.get(i);

				writer1.append(patientC.getId().toString());
				writer1.append(',');
				writer1.append(patientC.getPatient().getId().toString());
				writer1.append(',');
				writer1.append(patientC.getContextName().toString());
				writer1.append(',');
				writer1.append(patientC.getSelectedCountry().toString());
				writer1.append(',');
				writer1.append(patientC.getSelectedCareCenter().toString());
				writer1.append(',');
				writer1.append(patientC.getSelectedProject().toString());
				writer1.append('\n');
			}
			writer1.flush();
			writer1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * "Patient [_FamilyName=" + _FamilyName + ", _FirstName=" + _FirstName +
	 * ", _Sex=" + _Sex + ", _BirthDate=" + _BirthDate +
	 * ", _PatientIdentifiers=" + _PatientIdentifiers + "]";
	 */
	// Export Patient,PatientIdValue,PatientIdentifier
	public void ExportPatient() {

		String strFileName1 = "Patient.csv";
		String strFileName2 = "PatientIdValue.csv";
		String strFileName3 = "PatientIdentifier.csv";

		String strPath1 = getPath(strFileName1);
		String strPath2 = getPath(strFileName2);
		String strPath3 = getPath(strFileName3);

		Patient pat = new Patient();
		PatientIdValue patientIdVal = new PatientIdValue();
		PatientIdentifier patientIdent = new PatientIdentifier();

		try {
			FileWriter writer1 = new FileWriter(strPath1);
			FileWriter writer2 = new FileWriter(strPath2);
			FileWriter writer3 = new FileWriter(strPath3);

			writer1.append("ID");
			writer1.append(',');
			writer1.append("Family Name");
			writer1.append(',');
			writer1.append("First Name");
			writer1.append(',');
			writer1.append("Sex");
			writer1.append(',');
			writer1.append("Birth Date");
			writer1.append('\n');

			writer2.append("Patient ID");
			writer2.append(',');
			writer2.append("ID Concept");
			writer2.append(',');
			writer2.append("Value Concept");
			writer2.append('\n');

			writer3.append("Patient ID");
			writer3.append(',');
			writer3.append("ID");
			writer3.append(',');
			writer3.append("Identifier");
			writer3.append('\n');

			List<Patient> lstPatient = null;
			lstPatient = ServiceHelper.getPatientManagerService()
					.getAllPatient();
			for (int i = 0; i < lstPatient.size(); i++) {
				System.out.println(lstPatient.get(i).toString());
				pat = lstPatient.get(i);

				writer1.append(pat.getId().toString());
				writer1.append(',');
				writer1.append(pat.getFamilyName());
				writer1.append(',');
				writer1.append(pat.getFirstName());
				writer1.append(',');
				writer1.append(pat.getSex());
				writer1.append(',');
				writer1.append(pat.getBirthDate().toString());
				writer1.append('\n');

				for (int j = 0; j < pat.getIdValues().size(); j++) {
					patientIdVal = pat.getIdValues().get(j);
					writer2.append(pat.getId().toString());// patient id
					writer2.append(',');
					writer2.append(patientIdVal.getConceptId().toString());
					writer2.append(',');
					writer2.append(patientIdVal.getConceptValue().toString());
					writer2.append('\n');
				}

				for (int k = 0; k < pat.getPatientIdentifiers().size(); k++) {
					patientIdent = pat.getPatientIdentifiers().get(k);
					writer3.append(pat.getId().toString());// patient id
					writer3.append(',');
					writer3.append(patientIdent.getId().toString());
					writer3.append(',');
					writer3.append(patientIdent.getIdentifier());
					writer3.append('\n');
				}
			}
			writer1.flush();
			writer1.close();
			writer2.flush();
			writer2.close();
			writer3.flush();
			writer3.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// export Encouter,ConceptIDValue
	public void ExportEncounter() {

		String strFileName1 = "Encounter.csv";
		String strFileName2 = "EncounterConceptIDValue.csv";

		String strPath1 = getPath(strFileName1);
		String strPath2 = getPath(strFileName2);

		ConceptIdValue conceptId = new ConceptIdValue();
		try {
			FileWriter writer1 = new FileWriter(strPath1);
			FileWriter writer2 = new FileWriter(strPath2);

			writer1.append("ID Encounter");
			writer1.append(',');
			writer1.append("ID Patient");
			writer1.append(',');
			writer1.append("Date");
			writer1.append(',');
			writer1.append("Type");
			writer1.append(',');
			writer1.append("Status");
			writer1.append('\n');

			writer2.append("ID Encounter");
			writer2.append(',');
			writer2.append("ID Patient");
			writer2.append(',');
			writer2.append("ID Concept");
			writer2.append(',');
			writer2.append("Value Concept");
			writer2.append('\n');

			List<Encounter> lstEnCounter = null;
			lstEnCounter = ServiceHelper.getEncounterManagerService()
					.getAllEncounters();
			Encounter enc = new Encounter();
			for (int i = 0; i < lstEnCounter.size(); i++) {
				System.out.println(lstEnCounter.get(i).toString());
				enc = lstEnCounter.get(i);

				writer1.append(enc.getId().toString());
				writer1.append(',');
				writer1.append(enc.getPatient().getId().toString());
				writer1.append(',');
				writer1.append(enc.getDate().toString());
				writer1.append(',');
				writer1.append(enc.getType());
				writer1.append(',');
				writer1.append(enc.getStatus().toString());
				writer1.append('\n');

				for (int j = 0; j < enc.getIdValues().size(); j++) {
					conceptId = enc.getIdValues().get(j);

					writer2.append(enc.getId().toString());
					writer2.append(',');
					writer2.append(enc.getPatient().getId().toString());
					writer2.append(',');
					writer2.append(conceptId.getConceptId().toString());
					writer2.append(',');
					writer2.append(conceptId.getConceptValue().toString());
					writer2.append('\n');
				}
			}
			writer1.flush();
			writer1.close();
			writer2.flush();
			writer2.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
