package ch.msf.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.msf.form.config.EncounterListTableModel.EncounterModelData;
import ch.msf.form.config.PatientListTableModel.PatientModelData;
import ch.msf.form.config.VillageListTableModel.VillageHealthAreaModelData;
import ch.msf.model.Encounter;
import ch.msf.model.PatientContext;
import ch.msf.model.VillageArea;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;


public interface EntryFormConfigurationManager extends ConfigurationManager {
	
	public void setupLanguages(
			HashMap<String, HashMap<String, String>> fieldsLabels);
	
	public ArrayList<IdType> getQuestionIdTypes(Class theClass, String subType, Boolean noMapping);
	// HashMap<locale, HashMap<fieldId, fieldLabel>>>
	public HashMap<String, HashMap<String, String>> getQuestionLabels(String className, String subType);
	
	/**
	 * 
	 * @param theClass: the class of entity
	 * @param subType: a string specifying the subtype of the class
	 * @param noMapping: if true, discard the questionid that are mapped to an entity
	 *            class
	 * @return a map of combolabels related to locale for theClass/subType
	 */
	public HashMap<String, ArrayList<KeyValue>> getAllComboLabels(Class theClass, String subType, Boolean noMapping);
	
	public ArrayList<KeyValue> getQuestionIdToClassAttributes(Class theClass, String subType);

	
	public PatientContext getCurrentPatientContext();
	
	public PatientContext createNewPatientContext();

	public void setCurrentPatientContext(PatientContext patientContext);

	public Encounter getCurrentEncounter();
	
	public void setCurrentEncounter(Encounter encounter);	
	
	public PatientModelData getCurrentPatientModelData();
	public void setCurrentPatientModelData(PatientModelData patientModelData);
	
	
	public void setCurrentEncounterModelData(EncounterModelData encounterModelData);
	public EncounterModelData getCurrentEncounterModelData();
		
	// tells if a line is in edition
	public boolean isPatientDataSaved();
	void setPatientDataSaved(boolean _DataSaved);
	
	/* TN79 */// tells if a concept panel is in edition
	public boolean isPatientAttributesDataSaved();
	void setPatientAttributesDataSaved(boolean dataSaved);
	

	
	// tells if current line is a new data
	public boolean isNewPatientDataInsertion();
	void setNewPatientDataInsertion(boolean _NewData);

	HashMap<Long, PatientContext> getPatientContextCache();

//	void setPatientContextCache(
//			HashMap<Long, PatientContext> _PatientContextCache);
	
	
	HashMap<Long, Encounter> getEncounterCache();
	
	// tells if a line is in edition
	public boolean isEncounterDataSaved();
	void setEncounterDataSaved(boolean _DataSaved);
	
	/* TN79 */// tells if concepts panel is in edition
	public boolean isEncounterAttributesDataSaved();
	void setEncounterAttributesDataSaved(boolean dataSaved);

	
	// tells if current line is a new data
	public boolean isNewEncounterDataInsertion();
	void setNewEncounterDataInsertion(boolean _NewData);
	
	public String[] getTableTitleTranslation(String tableName);
	
//	TN84 read encounter labels
	String getEncounterLabel(String encounterName);
	
	//TN129
	public List<VillageArea> getAllVillageArea();
	public void loadAllVillageArea();

	//TN129
	public ArrayList<String> loadAllAreas();

	//TN129
	public void saveAllVillageArea(ArrayList<VillageHealthAreaModelData> villageAreas);

	//TN129
	public boolean isNewVillageOriginData();

	//TN142
	String getPatientSearchConceptId();

	//TN142
	void setPatientSearchConceptId(String searchConceptId);
	
}
