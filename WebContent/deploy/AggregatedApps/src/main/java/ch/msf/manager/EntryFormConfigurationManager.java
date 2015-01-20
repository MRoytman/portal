package ch.msf.manager;

import java.util.ArrayList;
import java.util.HashMap;

import ch.msf.form.ParamException;
import ch.msf.form.config.AggregationListTableModel.AggregatedModelData;
import ch.msf.form.config.SectionListTableModel.SectionModelData;
import ch.msf.model.Aggregation;
import ch.msf.model.AggregationContext;
import ch.msf.model.Section;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;


public interface EntryFormConfigurationManager extends ConfigurationManager {
	
	void setupLanguages(
			HashMap<String, HashMap<String, String>> fieldsLabels);
	
	ArrayList<IdType> getQuestionIdTypes(Class theClass, String subType, Boolean noMapping);
	ArrayList<IdType[]> getSectionTableQuestionIdTypes(String sectionId);
	
	HashMap<String, HashMap<String, String>> getQuestionLabels(String className, String subType);

	// HashMap<locale, HashMap<sectionTableConceptId, sectionTableConceptLabel>>
	HashMap<String, HashMap<String, String>> getSectionTableQuestionLabels(String sectionId);
	/**
	 * 
	 * @param theClass: the class of entity
	 * @param subType: a string specifying the subtype of the class
	 * @param noMapping: if true, discard the questionid that are mapped to an entity
	 *            class
	 * @return a map of combolabels related to locale for theClass/subType
	 */
	HashMap<String, ArrayList<KeyValue>> getAllComboLabels(Class theClass, String subType, Boolean noMapping);
	
//	ArrayList<KeyValue> getQuestionIdToClassAttributes(Class theClass, String subType);

	
	AggregationContext getCurrentAggregationContext();
	
	AggregationContext createNewAggregationContext();

	void setCurrentAggregationContext(AggregationContext aggregationContext);

	Section getCurrentSection();
	
	void setCurrentSection(Section Section);	
	
	// CurrentAggregatedModelData is needed to update the view from models
	AggregatedModelData getCurrentAggregatedModelData();
	void setCurrentAggregatedModelData(AggregatedModelData aggregationModelData);
	
	
	void setCurrentSectionModelData(SectionModelData SectionModelData);
	SectionModelData getCurrentSectionModelData();
		
	// tells if a line is in edition
	boolean isAggregatedDataSaved();
	void setAggregatedDataSaved(boolean _DataSaved);
	
	/* TN79 */// tells if a concept panel is in edition
	boolean isAggregatedAttributesDataSaved();
//	void setAggregatedAttributesDataSaved(boolean dataSaved);
	

	
	// tells if current line is a new data
	boolean isNewAggregatedDataInsertion();
	void setNewAggregatedDataInsertion(boolean _NewData);

	HashMap<Long, AggregationContext> getAggregationContextCache();
	
	
	HashMap<Long, Section> getSectionCache();
	
	// tells if a line is in edition
	boolean isSectionDataSaved();
	void setSectionDataSaved(boolean _DataSaved);
	
	/* TN79 */// tells if concepts panel is in edition
	boolean isSectionAttributesDataSaved();
	void setSectionAttributesDataSaved(boolean dataSaved);

	
	// tells if current line is a new data
	boolean isNewSectionDataInsertion();
	void setNewSectionDataInsertion(boolean _NewData);
	
	String[] getTableTitleTranslation(String tableName);
	
	String getAggregationLabel(String aggregationType);
	String getAggregationType(String aggregationLabel);
	String getAggregationId(String aggregationLabel);

	ArrayList<String> getAllAggregationLabel();
	
	ArrayList<String> getSectionLabels(String aggregationThemeType);
	ArrayList<String> getSectionIds(String aggregationThemeType);
	
	String getSectionContentLabels(String aggregationThemeType, String sectionType);

	void readAggregationTypes();
	HashMap<String, String> getAggregationTypes();

	ArrayList<String> getAggregationIds();

	ArrayList<Section> createNewSections(Aggregation aggregation);

	Aggregation saveCurrentAggregation() throws ParamException;

	Section saveSectionAttributes(HashMap<String, String> results, boolean saveDb) throws ParamException;

	
}
