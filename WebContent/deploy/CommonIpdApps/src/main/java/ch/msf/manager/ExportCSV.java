package ch.msf.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.w3c.dom.ls.LSInput;

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
		return config.getMsfApplicationDir() +File.separator+ strFileName;
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
	public void ExportAllDB() {

		String strFileName = config.getApplicationTitle()+".csv";
		String strPath = getPath(strFileName);
		try {
			FileWriter writer = new FileWriter(strPath);
			//Patient
			writer.append("ID Patient");
			writer.append(',');
			writer.append("Family Name");
			writer.append(',');
			writer.append("First Name");
			writer.append(',');
			writer.append("Sex");
			writer.append(',');
			writer.append("Birth Date");
			writer.append(',');
			//Context
			writer.append("ID Context");
			writer.append(',');
			writer.append("Context name");
			writer.append(',');
			writer.append("Selected Country");
			writer.append(',');
			writer.append("Selected Care Center");
			writer.append(',');
			writer.append("Selected Project");
			writer.append(',');
			//Patient Concept
			writer.append("ID Patient Concept");
			writer.append(',');
			writer.append("Value Patient Concept");
			writer.append(',');
			//Encounter
			writer.append("ID Encounter");
			writer.append(',');
			writer.append("Date");
			writer.append(',');
			writer.append("Type");
			writer.append(',');
			writer.append("Status");
			writer.append(',');
			//Encounter Concept
			writer.append("ID Encounter Concept");
			writer.append(',');
			writer.append("Value Encounter Concept");
			writer.append('\n');

			List<Patient> lstPatient = null;
			List<PatientContext> lstPatientContext = null;
			List<PatientIdValue> lstPatientConcept = null;
			List<Encounter> lstEncounter =null;
			List<ConceptIdValue> lstEncounterConcept =null;
			Patient pat = new Patient();
			Encounter encount = new Encounter();
			PatientContext patContext= new PatientContext();
			PatientIdValue patConcept= new PatientIdValue();
			ConceptIdValue enConcept = new ConceptIdValue();
			
			lstPatient = ServiceHelper.getPatientManagerService()
					.getAllPatient();

			
			for (int i = 0; i < lstPatient.size(); i++){
				pat=lstPatient.get(i);
				//Patient - Context
				lstPatientContext = ServiceHelper.getPatientManagerService().getAllPatientContext(pat);
				for(int j = 0; j < lstPatientContext.size(); j++){
					printPatient(writer, pat);
					patContext = lstPatientContext.get(j);
					printContext(writer, patContext);
					printPatConceptNull(writer);
					printEncouterNull(writer);
					printEnConceptNull(writer);
				}
				//Patient - Concept
				lstPatientConcept = pat.getIdValues();
				for(int k=0;k<lstPatientConcept.size();k++){
					patConcept=lstPatientConcept.get(k);
					printPatient(writer, pat);
					printContextNull(writer);
					printPatConcept(writer,patConcept);
					printEncouterNull(writer);
					printEnConceptNull(writer);
				}
				//Patient - Encounter - Concept
				lstEncounter = ServiceHelper.getEncounterManagerService().getAllEncountersByPatient(pat);
				for(int l=0;l<lstEncounter.size();l++){
					encount = lstEncounter.get(l);
					lstEncounterConcept = encount.getIdValues();
					for(int m= 0;m<lstEncounterConcept.size();m++)
					{
						enConcept = lstEncounterConcept.get(m);
						printPatient(writer, pat);
						printContextNull(writer);
						printPatConceptNull(writer);
						printEncouter(writer, encount);
						printEnConcept(writer, enConcept);
						
					}
				}
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printPatConceptNull(FileWriter writer) throws IOException {
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append(',');
	}
	private void printEnConceptNull(FileWriter writer) throws IOException {
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append('\n');
	}
	private void printEncouterNull(FileWriter writer) throws IOException {
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append(',');
	}

	private void printContextNull(FileWriter writer) throws IOException {
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append(',');
		writer.append("");
		writer.append(',');
	}
	private void printEncouter(FileWriter writer,Encounter encount) throws IOException {
		writer.append(encount.getId().toString());
		writer.append(',');
		writer.append(encount.getDate().toString());
		writer.append(',');
		writer.append(encount.getType().toString());
		writer.append(',');
		writer.append(String.valueOf(encount.getStatus()));
		writer.append(',');
	}
	
	private void printPatConcept(FileWriter writer, PatientIdValue patConcept) throws IOException {
		writer.append(patConcept.getConceptId());
		writer.append(',');
		writer.append(patConcept.getConceptValue());
		writer.append(',');
	}
	
	private void printEnConcept(FileWriter writer, ConceptIdValue enConcept) throws IOException {
		writer.append(enConcept.getConceptId());
		writer.append(',');
		writer.append(enConcept.getConceptValue());
		writer.append('\n');
	}
	private void printContext(FileWriter writer, PatientContext patContext)
			throws IOException {
		writer.append(patContext.getId().toString());
		writer.append(',');
		writer.append(patContext.getContextName());
		writer.append(',');
		writer.append(patContext.getSelectedCountry().getCode());
		writer.append(',');
		writer.append(patContext.getSelectedCareCenter().getName());
		writer.append(',');
		writer.append(patContext.getSelectedProject().getName());
		writer.append(',');
	}

	private void printPatient(FileWriter writer, Patient pat)
			throws IOException {
		writer.append(pat.getId().toString());
		writer.append(',');
		writer.append(pat.getFamilyName().toString());
		writer.append(',');
		writer.append(pat.getFirstName().toString());
		writer.append(',');
		writer.append(pat.getSex().toString());
		writer.append(',');
		writer.append(pat.getBirthDate().toString());
		writer.append(',');
	}

}
