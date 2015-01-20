package ch.msf.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import ch.msf.util.IdType;
import ch.msf.util.KeyValue;

public interface ResourceManager {

	/**
	 * 
	 * @param theClass
	 *            : the class of entity
	 * @param subType
	 *            : a string specifying the subtype of the class
	 * @param noMapping
	 *            : if true, discard the questionid that are mapped to an entity
	 *            class
	 * @return a list of id/types related to locale for theClass/subType
	 */
	ArrayList<IdType> getQuestionIdTypes(Class theClass, String subType, Boolean noMapping);

	/**
	 * 
	 * @param theClass
	 *            : the class of entity
	 * @param subType
	 *            : a string specifying the subtype of the class
	 * @return a map of labels related to locale for theClass/subType
	 */
	// HashMap<locale, HashMap<fieldId, fieldLabel>>>
	HashMap<String, HashMap<String, String>> getQuestionLabels(String className, String subType);

	// HashMap<locale, HashMap<sectionTableConceptId, label>>
	HashMap<String, HashMap<String, String>> getSectionTableLabels(String sectionTableId, String locale);

	/**
	 * 
	 * @param theClass
	 *            : the class of entity
	 * @param subType
	 *            : a string specifying the subtype of the class
	 * @param locale
	 *            :
	 * @param noMapping
	 *            : if true, discard the questionid that are mapped to an entity
	 *            class
	 * @return a map of combolabels related to locale for theClass/subType
	 */
	HashMap<String, ArrayList<KeyValue>> getAllComboLabels(Class theClass, String subType, String locale, Boolean noMapping);

	ArrayList<KeyValue> getQuestionIdToClassAttributes(Class theClass, String subType);

	Properties loadGlobalProperties(String string);

	String[] getTableTitleTranslation(String tableName, String locale);

	/**
	 * 
	 * @param resourceName
	 * @return a list of lines representing the content of the resourceName
	 */
	ArrayList<String> readResourceFile(String resourceName);

	ConfigurationManagerBase getConfigurationManager();

	void setConfigurationManager(ConfigurationManagerBase _ConfigurationManager);

	// TN84 read encounter labels
	String getEncounterLabel(String encounterName, String defaultLanguage); // TN84

	String getAggregationLabel(String thematicPeriodType, String defaultLanguage); //

	ArrayList<String> getAggregationLabels(String defaultLanguage);

	// HashMap<locale, HashMap<AggregationType, translated label>>
	HashMap<String, HashMap<String, String>> getAggregationLabels();

	// linked with TN128 ...load a resource whether it is on JWS or local dev
	ArrayList<String> loadResource(String resourceNameFile);

	// TN129
	ArrayList<KeyValue> getComboLabels(Class theClass, String subType, String comboName, String locale);

	// TN129
	void clearResourceKeyFromCache(Class theClass, String subType, String comboName);

	ArrayList<String> getSectionLabels(String aggregationThemeType, String locale);
	ArrayList<String> getSectionIds(String aggregationThemeType, String defaultLanguage);

	String getSectionContentLabels(String aggregationThemeType, String sectionType, String locale);

	/**
	 * 
	 * @return a list of sectiontable ids linked with sectionId
	 * read the section file configuration if not already done
	 * @param sectionId
	 */
	ArrayList<String> getSectionTableIds(String sectionId);

	/**
	 * 
	 * @return a map of sectiontableids types, linked with sectionId
	 * read the section file configuration if not already done
	 * @param sectionId
	 */
	HashMap<String, ArrayList<IdType[]>> getSectionTableIdTypes(String sectionId);


}
