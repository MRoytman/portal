package ch.msf.manager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import ch.msf.model.ConceptIdValue;
import ch.msf.model.Encounter;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.PatientIdValue;
import ch.msf.service.ServiceHelper;

public class ExportCSVf2 {
	
	String atFolder="/ch/msf/fields/f2/";
	private String strFilePat = "Patient-FieldsIdType.txt";
	private String strFileEn1 = "Encountermental_health_main_assessment-FieldsIdType.txt";
	private String strFileEn2 = "Encountermental_health_fup-FieldsIdType.txt";


	ConfigurationManagerBaseImpl config = ServiceHelper
			.getConfigurationManagerService();

	ReadFileds rf = new ReadFileds();

	List<String> strCodeFullPat = rf.readFile(strFilePat,atFolder);
	List<String> strCodeFullEn1 = rf.readFile(strFileEn1,atFolder);
	List<String> strCodeFullEn2 = rf.readFile(strFileEn2,atFolder);


	List<String> valPrintGeneral = null;
	List<String> valPrintPatDetail = null;
	List<String> valPrintEn1Detail = null;
	List<String> valPrintEn2Detail = null;


	
	List<String> lstIDsDB = new ArrayList<String>();
	List<String> lstValsDB =new ArrayList<String>();
	
	public void printCol(FileWriter writer, List<String> valX)
			throws IOException {
		for (int i = 0; i < valX.size(); i++) {
			writer.append(valX.get(i));
			writer.append(',');
		}
	}

	public List<String> sortValueDependOn(List<String> lstIDsFull)
	{
		List<String> lstOut = new ArrayList<String>();
		for(int i=0;i<lstIDsFull.size();i++)
			lstOut.add("");
		
		for(int i=0;i<lstIDsDB.size();i++){
			for(int j=0;j<lstIDsFull.size();j++){
				if(lstIDsDB.get(i).compareTo(lstIDsFull.get(j))==0){
					lstOut.set(j, lstValsDB.get(i));
				}
					
			}
		}
		lstIDsDB=lstValsDB=null;
		return lstOut;
	}
	
	public String getPrefix(String strFile) {
		return strFile.replace("-FieldsIdType.txt", "")
				.replace("Encounter", "") + "__";
	}

	public String getCodeDB(String strFile) {
		return strFile.replace("-FieldsIdType.txt", "")
				.replace("Encounter", "");
	}

	public String getPath(String strFileName) {
		return config.getMsfApplicationDir() + File.separator + strFileName;
	}

	public void ExportAllDB() {
		String strFileName = config.getApplicationTitle() + ".csv";
		String strPath = getPath(strFileName);
		List<Patient> lstPatientDB = null;
		List<PatientContext> lstContextDB = null;
		List<PatientIdValue> lstPatientDetailDB = null;
		List<Encounter> lstEncounterDB = null;
		List<ConceptIdValue> lstEncounterDetailDB = null;
		Patient pat = new Patient();
		Encounter enc = new Encounter();
		PatientContext patContext = new PatientContext();
		PatientIdValue patDetail = new PatientIdValue();
		ConceptIdValue enDetail = new ConceptIdValue();

		try {
			FileWriter writer = new FileWriter(strPath);
			printHeader(writer);

			lstPatientDB = ServiceHelper.getPatientManagerService()
					.getAllPatient();
			// Patient
			for (int i = 0; i < lstPatientDB.size(); i++) {
				pat = lstPatientDB.get(i);
				// Patient - Context
				lstContextDB = ServiceHelper.getPatientManagerService()
						.getAllPatientContext(pat);
				for (int j = 0; j < lstContextDB.size(); j++) {
					// Print General info = Patient + context
					patContext = lstContextDB.get(j);
					valPrintGeneral=setGeneral(pat,patContext);
					printCol(writer, valPrintGeneral);

					// Patient detail
					lstPatientDetailDB = pat.getIdValues();
					initLstDB();
					for(int p=0;p<lstPatientDetailDB.size();p++){
						patDetail =lstPatientDetailDB.get(p);
						lstIDsDB.add(patDetail.getConceptId());
						lstValsDB.add(patDetail.getConceptValue());
					}
					valPrintPatDetail = sortValueDependOn(strCodeFullPat);//mapping col and val in DB with header
					printCol(writer, valPrintPatDetail );
					// Encounter detail
					lstEncounterDB = ServiceHelper.getEncounterManagerService()
							.getAllEncountersByPatient(pat);
					for (int k = 0; k < lstEncounterDB.size(); k++) {
						enc = lstEncounterDB.get(k);
						String enType = enc.getType();
						lstEncounterDetailDB = enc.getIdValues();
						if (enType.compareToIgnoreCase(getCodeDB(strFileEn1))==0) {
							initLstDB();
							for (int l = 0; l < lstEncounterDetailDB.size(); l++) {
								
								enDetail = lstEncounterDetailDB.get(l);
								lstIDsDB.add (enDetail.getConceptId());
								lstValsDB.add(enDetail.getConceptValue());
							}
							valPrintEn1Detail = sortValueDependOn(strCodeFullEn1);//mapping col and val in DB with header
						}
						if  (enType.compareToIgnoreCase(getCodeDB(strFileEn2))==0) {
							initLstDB();
							for (int l = 0; l < lstEncounterDetailDB.size(); l++) {
								
								enDetail = lstEncounterDetailDB.get(l);
								lstIDsDB.add (enDetail.getConceptId());
								lstValsDB.add(enDetail.getConceptValue());
							}
							valPrintEn2Detail = sortValueDependOn(strCodeFullEn2);//mapping col and val in DB with header
						}
						

					}
					if(valPrintEn1Detail!=null)
						printCol(writer, valPrintEn1Detail);
					else
						printNull(writer,strCodeFullEn1.size());
					
					if(valPrintEn2Detail!=null)
						printCol(writer, valPrintEn2Detail);
					else
						printNull(writer,strCodeFullEn2.size());
					
					
					
					valPrintEn1Detail=valPrintEn2Detail=null;
					writer.append('\n');
				}
			}

			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	private void initLstDB() {
		lstIDsDB = new ArrayList<String>();
		lstValsDB = new ArrayList<String>();
	}

	private List<String> setGeneral(Patient pat, PatientContext patContext) {
		List<String> strOut = new ArrayList<String>();
		strOut.add(pat.getId().toString());
		strOut.add(pat.getFamilyName());
		strOut.add(pat.getFirstName());
		strOut.add(pat.getSex());
		strOut.add(pat.getBirthDate().toString());
		strOut.add(patContext.getId().toString());
		strOut.add(patContext.getContextName());
		strOut.add(patContext.getSelectedCountry().getCountry().getCode());
		strOut.add(patContext.getSelectedCareCenter().getName());
		strOut.add(patContext.getSelectedProject().getName());
		return strOut;
	}
	private void printNull (FileWriter writer,int isize)throws IOException{
		for(int i=0;i<isize;i++)
		{
			writer.append("");
			writer.append(',');
		}
	}
	private void printHeader(FileWriter writer) throws IOException {
		// Patient
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
		// Context
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
		// Pat-Info
		for (String string : strCodeFullPat) {
			writer.append(getPrefix(strFilePat) + string);
			writer.append(',');
		}
		// En-Info
		for (String string : strCodeFullEn1) {
			writer.append(getPrefix(strFileEn1) + string);
			writer.append(',');
		}
		for (String string : strCodeFullEn2) {
			writer.append(getPrefix(strFileEn2) + string);
			writer.append(',');
		}
		
		writer.append('\n');
	}

}
