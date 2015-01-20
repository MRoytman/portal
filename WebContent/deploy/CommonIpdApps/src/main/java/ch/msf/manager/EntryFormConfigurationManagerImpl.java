package ch.msf.manager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import ch.msf.CommonConstants;
import ch.msf.basicservice.BasicServiceHelper;
import ch.msf.form.FatalException;
import ch.msf.form.ParamException;
import ch.msf.form.config.EncounterListTableModel.EncounterModelData;
import ch.msf.form.config.PatientListTableModel.PatientModelData;
import ch.msf.form.config.VillageListTableModel.VillageHealthAreaModelData;
import ch.msf.model.ConceptIdValue;
import ch.msf.model.Encounter;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.PatientIdValue;
import ch.msf.model.VillageArea;
import ch.msf.model.actionrunner.ComponentEnabler;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IOUtils;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;
import ch.msf.util.MiscelaneousUtils;

public class EntryFormConfigurationManagerImpl extends ConfigurationManagerImpl implements EntryFormConfigurationManager {

	// output data file
	// private static String _ResultFileName;

	public static int USER_MAX_SIZE = 20; // TEMP
	public static int PWD_MAX_SIZE = 20; // TEMP

	private PatientContext _PatientContext;

	// identify country, project, carecenter

	private Encounter _CurrentEncounter;

	private PatientModelData _PatientModelData;

	private EncounterModelData _EncounterModelData;

	private boolean _PatientDataSaved = true;

	private boolean _PatientAttributesDataSaved = true;/* TN79 */

	private boolean _NewPatientData = false;

	private boolean _EncounterDataSaved = true;

	private boolean _EncounterAttributesDataSaved = true;/* TN79 */

	private boolean _NewEncounterData = false;

	// cache of patient <PatientContextid, PatientContext>
	private HashMap<Long, PatientContext> _PatientContextCache = new HashMap<Long, PatientContext>();

	// cache of encounter <Encounterid, Encounter>
	private HashMap<Long, Encounter> _EncounterCache = new HashMap<Long, Encounter>();

	// the list of areas in the project
	private ArrayList<String> _AreaList; // TN129

	private ArrayList<VillageArea> _AllVillageArea; // TN129

	private boolean _NewVillageOriginData = false; // TN129
	
	private String _SearchConceptId; //TN142

	// cache of uniq visual components <className, Object>
	private HashMap<String, ComponentEnabler> _ComponentCache = new HashMap<String, ComponentEnabler>();


	public EntryFormConfigurationManagerImpl() {
		System.out.println("EntryFormConfigurationManagerImpl constructor");
	}

	/**
	 * @return the _QuestionIdTypes for an entity
	 */
	@Override
	public ArrayList<IdType> getQuestionIdTypes(Class theClass, String subType, Boolean noMapping) {

		return getResourceManager().getQuestionIdTypes(theClass, subType, noMapping);
	}

	/**
	 * @return the QuestionLabels
	 */
	@Override  // HashMap<locale, HashMap<fieldId, fieldLabel>>>
	public HashMap<String, HashMap<String, String>> getQuestionLabels(String className, String subType) {

		return getResourceManager().getQuestionLabels(className, subType);
	}

	/**
	 * @return the ComboLabels
	 */
	@Override
	public HashMap<String, ArrayList<KeyValue>> getAllComboLabels(Class theClass, String subType, Boolean noMapping) {
		return getResourceManager().getAllComboLabels(theClass, subType, getDefaultLanguage(), noMapping);
	}

	/**
	 * TN84 read encounter labels
	 */
	@Override
	public String getEncounterLabel(String encounterName) {
		String ret = getResourceManager().getEncounterLabel(encounterName, getDefaultLanguage());
		if (ret == null) {
			System.out.println(getClass().getName() + "::getEncounterLabel: translation not found for name " + encounterName + ", with language " + getDefaultLanguage());
			if (!getDefaultLanguage().equals("en")) {
				ret = getResourceManager().getEncounterLabel(encounterName, "en");
			} else {
				throw new FatalException(getClass().getName() + "::getEncounterLabel: translation not found for name " + encounterName + ", with language " + getDefaultLanguage());
			}
		}

		return ret;
	}

	/**
	 * @return the QuestionIdToClassAttributes
	 */
	@Override
	public ArrayList<KeyValue> getQuestionIdToClassAttributes(Class theClass, String subType) {

		return getResourceManager().getQuestionIdToClassAttributes(theClass, subType);
	}

	/**
	 * @return the ComboLabels
	 */
	@Override
	public String[] getTableTitleTranslation(String tableName) {
		String[] ret = getResourceManager().getTableTitleTranslation(tableName, getDefaultLanguage());
		if (ret == null) {
			System.out.println(getClass().getName() + "::getTableTitleTranslation: No titles found for table " + tableName + ", with language " + getDefaultLanguage());
			if (!getDefaultLanguage().equals("en")) {
				ret = getResourceManager().getTableTitleTranslation(tableName, "en");
			} else {
				throw new FatalException(getClass().getName() + "::getTableTitleTranslation: No titles found for table " + tableName + ", with language " + getDefaultLanguage());
			}
		}

		return ret;
	}

	// /////////////////////////////////////////TECHNICAL
	// UTILITIES/////////////////////////////////////////////////////////////////////////////
	// TODO use Resource manager and replace all this

	public static ArrayList<String> readFile(String inputFileName, URI uri, URL url, InputStream is) throws IOException {

		ArrayList<String> stringList = null;
		if (inputFileName != null)
			stringList = readFile(inputFileName);
		else if (uri != null)
			stringList = readFileUrl(uri);
		else if (url != null)
			stringList = readFileUrl(url);
		// else if (is != null)
		// stringList = readFileStream(is);

		return stringList;
	}

	public static ArrayList<String> readFileUrl(URI inputFileUri) throws IOException {

		java.net.URL inputFileUrl = inputFileUri.toURL();
		return readFileUrl(inputFileUrl);
	}

	public static ArrayList<String> readFileUrl(URL inputFileUrl) throws IOException {
		System.out.println("reading file from url : " + inputFileUrl);
		ArrayList<String> stringList = new ArrayList<String>();

		InputStream is = inputFileUrl.openStream();
		BufferedReader in = new BufferedReader(new InputStreamReader(is, "UTF8"));

		String thisLine = null;
		while ((thisLine = in.readLine()) != null) {
			stringList.add(thisLine);
		}

		in.close();

		return stringList;
	}

	public static ArrayList<String> readFile(String inputFileName) {
		System.out.println("reading file : " + inputFileName);

		ArrayList<String> stringList = new ArrayList<String>();

		try {
			FileInputStream fis = new FileInputStream(inputFileName);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis, "UTF8"));

			String thisLine = null;

			while ((thisLine = in.readLine()) != null) {
				// System.out.println(thisLine);
				stringList.add(thisLine);

			} // end while
			in.close();

		} catch (UnsupportedEncodingException ue) {
			System.out.println("Not supported : ");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return stringList;
	}

	@Override
	public PatientContext getCurrentPatientContext() {

		return _PatientContext;
	}

	@Override
	public PatientContext createNewPatientContext() {
		_PatientContext = new PatientContext();
		// if (_PatientContext != null) {

		String applicationShortName = getConfigField("applicationShortName"); // TN95

		String countryDeployShortName = null;// System.getProperty("countryDeployShortName");
		// if (countryDeployShortName == null)
		// don't forget to set the value for local testing/devel
		countryDeployShortName = getConfigField("countryDeployShortName");

		_PatientContext.setContextName(countryDeployShortName + "|" + applicationShortName);

		_PatientContext.setSelectedCountry(getCurrentSelectionContext().getSelectedCountry());

		_PatientContext.setSelectedProject(getCurrentSelectionContext().getSelectedProject());

		_PatientContext.setSelectedCareCenter(getCurrentSelectionContext().getSelectedCareCenter());
		// }

		return _PatientContext;
	}

	@Override
	public void setCurrentPatientContext(PatientContext patientContext) {
		if (patientContext == null)
			System.out.println("patientContext == null");
		_PatientContext = patientContext;
	}

	@Override
	public Encounter getCurrentEncounter() {
		return _CurrentEncounter;
	}

	@Override
	public void setCurrentEncounter(Encounter encounter) {
		_CurrentEncounter = encounter;

	}

	/**
	 * 
	 * @param results
	 *            : a map of conceptId values
	 * @param saveDb
	 *            : true if the current patient should be saved to db
	 * @return the saved patient if saved or null otherwise
	 * @throws ParamException
	 */
	public Patient savePatientAttributes(HashMap<String, String> results, boolean saveDb) throws ParamException {
		PatientContext currentPatientContext = getCurrentPatientContext();

		// get the patient
		Patient currentPatient = currentPatientContext.getPatient();
		currentPatient.setModificationDate(new Date());

		// get existing attributes
		List<PatientIdValue> patientIdValues = null;
		try {
			patientIdValues = currentPatient.getIdValues();
			for (PatientIdValue patientIdValue : patientIdValues) {
			}

		} catch (org.hibernate.LazyInitializationException e) {
			boolean all = true;
			currentPatient = ServiceHelper.getPatientManagerService().readDBPatientInfo(currentPatient, all);
			patientIdValues = currentPatient.getIdValues();
		}

		Iterator<PatientIdValue> it = patientIdValues.iterator();
		while (it.hasNext()) {
			PatientIdValue patientIdValue = it.next();
			String newValue = results.get(patientIdValue.getConceptId());
			if (newValue == null) {
				// check if the field is part from the (hiden) entity list
				// fields
				// check age
				// System.out.println(patientIdValue.getConceptId());

				// this field is no more in the set
				// remove it from the patient list
				it.remove();
			} else {
				patientIdValue.setConceptValue(newValue);
				// remove from hash
				results.remove(patientIdValue.getConceptId());
			}
		}

		// add all new concepts
		for (String key : results.keySet()) {
			String strVal = results.get(key);
			if (strVal != null) {
				PatientIdValue piv = new PatientIdValue();
				piv.setConceptId(key);
				piv.setConceptValue(results.get(key));
				piv.setPatient(currentPatient);
				currentPatient.getIdValues().add(piv);
			}
		}

		if (saveDb) {
			// save the patient
			try {
				currentPatientContext = ServiceHelper.getPatientManagerService().saveSelectedPatientContext(currentPatientContext);
				// if the currentPatientContext has not been saved it will be null
				// so replace it...
				setCurrentPatientContext(currentPatientContext);
				getCurrentPatientModelData().setPatientContextId(currentPatientContext.getId());

				// update cache
				getPatientContextCache().put(currentPatientContext.getId(), currentPatientContext);
				// get the patient
				currentPatient = currentPatientContext.getPatient();
			} catch (Exception e) {
				// TN132 roll back
				// remove from cache
				Object obj = getPatientContextCache().remove(currentPatientContext.getId());
				e.printStackTrace();
				throw new ParamException(e);
			}


			return currentPatient;
		}
		return null;

	}

	/**
	 * 
	 * @param results
	 *            : a map of conceptId values
	 * @param saveDb
	 *            : true if the current Encounter should be saved to db
	 * @throws ParamException
	 */
	public void saveEncounterAttributes(HashMap<String, String> results, boolean saveDb) throws ParamException {
		Encounter currentEncounter = getCurrentEncounter();

		currentEncounter.setModificationDate(new Date());

		// get existing attributes
		List<ConceptIdValue> conceptIdValues = currentEncounter.getIdValues();
		Iterator<ConceptIdValue> it = conceptIdValues.iterator();
		while (it.hasNext()) {
			ConceptIdValue conceptIdValue = it.next();
			String newValue = results.get(conceptIdValue.getConceptId());
			if (newValue == null) {
				// check if the field is part from the (hiden) entity list
				// fields
				// check age
				// System.out.println(conceptIdValue.getConceptId());

				// this field is no more in the set
				// remove it from the patient list
				it.remove();
			} else {
				conceptIdValue.setConceptValue(newValue);
				// remove from hash
				results.remove(conceptIdValue.getConceptId());
			}
		}

		// // get existing attributes
		// List<ConceptIdValue> conceptIdValues =
		// currentEncounter.getIdValues();
		// for (ConceptIdValue conceptIdValue : conceptIdValues) {
		// String newValue = results.get(conceptIdValue.getConceptId());
		// if (newValue == null) {
		// // this field is no more in the set
		// // do nothing for now
		// } else {
		// conceptIdValue.setConceptValue(newValue);
		// // remove from hash
		// results.remove(conceptIdValue.getConceptId());
		// }
		// }
		//

		// add all new concepts
		for (String key : results.keySet()) {
			String strVal = results.get(key);
			if (strVal != null) {
				ConceptIdValue civ = new ConceptIdValue();
				civ.setConceptId(key);
				civ.setConceptValue(results.get(key));
				civ.setEncounter(currentEncounter);
				currentEncounter.getIdValues().add(civ);
			}
		}

		if (saveDb) {
			currentEncounter = ServiceHelper.getEncounterManagerService().saveEncounter(currentEncounter);

			// if the currentEncounter has not been saved it will be null
			// so replace it...
			setCurrentEncounter(currentEncounter);

			// for caching purpose
			// if the currentEncounter has not been saved it will be null
			// so replace it...
			getCurrentEncounterModelData().setEncounterId(currentEncounter.getId());

			//
			// // update cache
			// getPatientContextCache().put(
			// currentPatientContext.getId(), currentPatientContext);
		}

	}

	@Override
	public PatientModelData getCurrentPatientModelData() {
		return _PatientModelData;
	}

	@Override
	public void setCurrentPatientModelData(PatientModelData patientModelData) {
		_PatientModelData = patientModelData;

	}

	@Override
	public EncounterModelData getCurrentEncounterModelData() {
		return _EncounterModelData;
	}

	@Override
	public void setCurrentEncounterModelData(EncounterModelData encounterModelData) {
		_EncounterModelData = encounterModelData;

	}

	@Override
	public boolean isPatientDataSaved() {
		return _PatientDataSaved;
	}

	@Override
	public void setPatientDataSaved(boolean _DataSaved) {
		this._PatientDataSaved = _DataSaved;
	}

	@Override
	/* TN79 */
	public boolean isPatientAttributesDataSaved() {
		return _PatientAttributesDataSaved;
	}

	@Override
	/* TN79 */
	public void setPatientAttributesDataSaved(boolean dataSaved) {
		_PatientAttributesDataSaved = dataSaved;
	}

	@Override
	public boolean isEncounterDataSaved() {
		return _EncounterDataSaved;
	}

	@Override
	public void setEncounterDataSaved(boolean _DataSaved) {
		this._EncounterDataSaved = _DataSaved;
	}

	@Override
	/* TN79 */
	public boolean isEncounterAttributesDataSaved() {
		return _EncounterAttributesDataSaved;
	}

	@Override
	/* TN79 */
	public void setEncounterAttributesDataSaved(boolean dataSaved) {
		_EncounterAttributesDataSaved = dataSaved;
	}

	@Override
	public HashMap<Long, PatientContext> getPatientContextCache() {
		return _PatientContextCache;
	}

	@Override
	public HashMap<Long, Encounter> getEncounterCache() {
		return _EncounterCache;
	}

	// cache of uniq visual components <className, Object>
	public HashMap<String, ComponentEnabler> getComponentCache() {
		return _ComponentCache;
	}

	@Override
	// TN129
	public boolean isNewVillageOriginData() {
		return _NewVillageOriginData;
	}

	// TN129
	public void setNewVillageOriginData(boolean _NewVillageOriginData) {
		this._NewVillageOriginData = _NewVillageOriginData;
		// clear the resourcemanager info
		if (_NewVillageOriginData) {
			String configField = "villageOrigin.conceptId";
			String villageOriginConcept = getConfigField(configField);

			String comboResourceToLoad = villageOriginConcept + CommonConstants.CODEDLIST_EXTENTION_CONCEPT_TYPE;
			getResourceManager().clearResourceKeyFromCache(Patient.class, null, comboResourceToLoad);
		}

	}

	@Override
	// TN129
	public boolean isNewPatientDataInsertion() {
		return _NewPatientData;
	}

	@Override
	public void setNewPatientDataInsertion(boolean newData) {
		_NewPatientData = newData;
		if (!newData) {
			// we just saved a new inserted patient
			// clear the null value from the cache that was used for that new
			// patient
			if (getPatientContextCache().get(null) != null) {
				getPatientContextCache().remove(null);
			}

		}
	}

	@Override
	public boolean isNewEncounterDataInsertion() {
		return _NewEncounterData;
	}

	@Override
	public void setNewEncounterDataInsertion(boolean newData) {
		_NewEncounterData = newData;
		if (!newData) {
			// we just saved a new inserted encounter
			// clear the null value from the cache that was used for that new
			// encounter
			if (getEncounterCache().get(null) != null) {
				getEncounterCache().remove(null);
			}

		}
	}

	// TN129
	public ArrayList<String> getAreaList() {
		return _AreaList;
	}

	/**
	 * return the list of entered village-origin -> association stored in a
	 * local file
	 */
	@Override
	// TN129
	public List<VillageArea> getAllVillageArea() {
		// if (_AllVillageArea == null)
		// loadAllVillageArea($);

		return _AllVillageArea;
	}

	@Override
	// TN129
	public void loadAllVillageArea() {

		String configField = "villageOrigin.conceptId";
		String villageOriginConcept = getConfigField(configField);

		String comboResourceToLoad = villageOriginConcept + CommonConstants.CODEDLIST_EXTENTION_CONCEPT_TYPE;

		ArrayList<KeyValue> comboValues = getResourceManager().getComboLabels(Patient.class, null, comboResourceToLoad, getDefaultLanguage());

		_AllVillageArea = new ArrayList<VillageArea>();
		if (comboValues != null)
			for (KeyValue keyValue : comboValues) {
				VillageArea villageArea = new VillageArea();
				// villageArea.setHealthArea("");
				villageArea.setVillageOrigin(keyValue._Value);//TN131
				_AllVillageArea.add(villageArea);
			}
	}

	@Override
	// TN129
	public void saveAllVillageArea(ArrayList<VillageHealthAreaModelData> villageAreas) {
		TreeSet<String> languages = getLanguages();

		ArrayList<String> lines = new ArrayList<String>();

		for (VillageHealthAreaModelData villageHealthAreaModelData : villageAreas) {
			String key = villageHealthAreaModelData.getVillageOrigin();// + "::"
																		// +
																		// villageHealthAreaModelData.getHealthArea();
			String value = key;
			for (String language : languages) {
				String line = key + "\t" + value + "\t" + language;
				lines.add(line);
			}

		}

		// save the collection
		String configField = "villageOrigin.conceptId";
		String villageOriginConcept = getConfigField(configField);

		String comboResourceToLoad = villageOriginConcept + CommonConstants.CODEDLIST_EXTENTION_CONCEPT_TYPE;

		String className = MiscelaneousUtils.getClassName(Patient.class);

		String resourceKey = className + "-" + comboResourceToLoad;

		String resourceFileComboValue = resourceKey + "-comboValue.txt";

		String workspaceresource = getApplicationDirectory();
		resourceFileComboValue = workspaceresource + System.getProperty("file.separator") + resourceFileComboValue;

		IOUtils.writeFileString(lines, resourceFileComboValue);

		// flag modification
		setNewVillageOriginData(false);
	}

	/**
	 * return the list of all known areas in the project's country
	 */
	@Override
	// TN129
	public ArrayList<String> loadAllAreas() {

		if (_AreaList == null) {
			String configField = "healthAreas.file.name";
			String resourceFileName = getConfigField(configField);

			_AreaList = BasicServiceHelper.getResourceService().loadResource(resourceFileName);
		}

		return _AreaList;

	}

	@Override //TN142
	public String getPatientSearchConceptId() {
		return _SearchConceptId;
		
	}
	
	@Override //TN142
	public void setPatientSearchConceptId(String searchConceptId){
		_SearchConceptId = searchConceptId;
	}

}
