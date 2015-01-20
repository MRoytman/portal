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

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.error.FatalException;
import ch.msf.form.ParamException;
import ch.msf.form.config.AggregationListTableModel.AggregatedModelData;
import ch.msf.form.config.SectionListTableModel.SectionModelData;
import ch.msf.model.Aggregation;
import ch.msf.model.AggregationContext;
import ch.msf.model.ConceptIdValue;
import ch.msf.model.Section;
import ch.msf.model.actionrunner.ComponentCellUpdator;
import ch.msf.model.actionrunner.ComponentEnabler;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;

public class EntryFormConfigurationManagerImpl extends ConfigurationManagerImpl implements EntryFormConfigurationManager {

	// output data file
	// private static String _ResultFileName;

	public static int USER_MAX_SIZE = 20; // TEMP
	public static int PWD_MAX_SIZE = 20; // TEMP

	private AggregationContext _AggregationContext;

	// identify country, project, carecenter

	private Section _CurrentSection;

	private AggregatedModelData _AggregatedModelData;

	private SectionModelData _SectionModelData;

	private boolean _AggregationDataSaved = true;

	private boolean _AggregationAttributesDataSaved = true;/* TN79 */

	private boolean _NewAggregationData = false;

	private boolean _SectionDataSaved = true;

	private boolean _SectionAttributesDataSaved = true;/* TN79 */

	private boolean _NewSectionData = false;

	// cache of Aggregation <AggregationContextid, AggregationContext>
	private HashMap<Long, AggregationContext> _AggregationContextCache = new HashMap<Long, AggregationContext>();

	// cache of Section <Sectionid, Section>
	private HashMap<Long, Section> _SectionCache = new HashMap<Long, Section>();

	// cache of uniq visual components <className, Object>
	private HashMap<String, ComponentEnabler> _ComponentCache = new HashMap<String, ComponentEnabler>();
	// cache of uniq visual components <className, Object>
	private HashMap<String, ComponentCellUpdator> _ComponentUpdatorCache = new HashMap<String, ComponentCellUpdator>();
	

	// HashMap<aggregationId, aggregationType>
	private HashMap<String, String> _AggregationTypes = new HashMap<String, String>();

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
	@Override
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

	@Override
	public ArrayList<String> getAggregationIds() {

		ArrayList<String> aggregationInfos = getConfigFields(CommonIpdConstants.AGGREGATION_TYPE_FILENAME_PREFIX);
		ArrayList<String> list = new ArrayList<String>(aggregationInfos.size()); //
		// and initiate array, otherwise get a IndexOutOfBoundsException
		for (int i = 0; i < aggregationInfos.size(); i++) {
			list.add("");
		}
		try {
			for (String aggregationInfo : aggregationInfos) {
				aggregationInfo = aggregationInfo.replace(CommonIpdConstants.AGGREGATION_TYPE_FILENAME_PREFIX, "");
				// get the value order
				String[] keyOrderType = aggregationInfo.split(CommonConstants.PROPS_STRING_SEPARATOR);
				String aggregationId = keyOrderType[1];

				list.add(aggregationId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConfigException("Configuration problem with " + CommonIpdConstants.AGGREGATION_TYPE_FILENAME_PREFIX, e);
		}

		return list;
	}

	@Override
	public void readAggregationTypes() {

		if (_AggregationTypes.isEmpty()) {
			ArrayList<String> aggregationInfos = getConfigFields(CommonIpdConstants.AGGREGATION_TYPE_FILENAME_PREFIX);

			try {
				for (String aggregationInfo : aggregationInfos) {
					aggregationInfo = aggregationInfo.replace(CommonIpdConstants.AGGREGATION_TYPE_FILENAME_PREFIX + CommonConstants.PROPS_STRING_SEPARATOR, "");
					aggregationInfo = aggregationInfo.replace(CommonConstants.PROPS_STRING_SEPARATOR, "\t");
					// get the value order
					String[] keyOrderType = aggregationInfo.split("\t");
					String aggregationId = keyOrderType[0];
					String aggregationType = keyOrderType[2];

					// SectionType =
					// SectionType.replace(CommonConstants.PROPS_STRING_SEPARATOR,
					// "");

					_AggregationTypes.put(aggregationId, aggregationType);
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new ConfigException("Configuration problem with " + CommonIpdConstants.AGGREGATION_TYPE_FILENAME_PREFIX, e);
			}
		}
	}

	@Override
	public HashMap<String, String> getAggregationTypes() {
		if (_AggregationTypes.isEmpty()) {
			readAggregationTypes();
		}
		return _AggregationTypes;
	}

	@Override
	public String getAggregationLabel(String aggregationConfigId) {
		String type = getResourceManager().getAggregationLabel(aggregationConfigId, getDefaultLanguage());
		if (type == null) {
			System.out.println(getClass().getName() + "::getAggregationLabel: translation not found for name " + aggregationConfigId + ", with language " + getDefaultLanguage());
			if (!getDefaultLanguage().equals("en")) {
				type = getResourceManager().getAggregationLabel(aggregationConfigId, "en");
			} else {
				throw new FatalException(getClass().getName() + "::getAggregationLabel: translation not found for name " + aggregationConfigId + ", with language "
						+ getDefaultLanguage());
			}
		}

		return type;
	}

	@Override
	public String getAggregationId(String aggregationLabel) {
		// HashMap<locale, HashMap<AggregationType, translated label>>
		HashMap<String, HashMap<String, String>> localeIdLabels = getResourceManager().getAggregationLabels();
		HashMap<String, String> idLabels = localeIdLabels.get(getDefaultLanguage());
		for (String id : idLabels.keySet()) {
			String label = idLabels.get(id);
			if (label.equals(aggregationLabel)) {
				return id;
			}
		}

		return null;
	}

	@Override
	public String getAggregationType(String aggregationLabel) {
		// GET THE AGGREG ID FIRST
		String aggregId = null;
		// HashMap<locale, HashMap<AggregationType, translated label>>
		HashMap<String, HashMap<String, String>> localeIdLabels = getResourceManager().getAggregationLabels();
		HashMap<String, String> idLabels = localeIdLabels.get(getDefaultLanguage());
		for (String id : idLabels.keySet()) {
			String label = idLabels.get(id);
			if (label.equals(aggregationLabel)) {
				aggregId = id;
				break;
			}
		}

		// GET THE TYPE from the aggregation id
		if (aggregId != null) {
			// HashMap<aggregationId, aggregationType>
			HashMap<String, String> aggregationTypes = getAggregationTypes();
			for (String id : aggregationTypes.keySet()) {
				if (id.equals(aggregId)) {
					return aggregationTypes.get(id);
				}
			}

		} else {
			throw new FatalException(getClass().getName() + "::getAggregationType: translation not found for name " + aggregationLabel + ", with language " + getDefaultLanguage());
		}

		return null;
	}

	@Override
	public ArrayList<String> getAllAggregationLabel() {
		ArrayList<String> aggregationLabels = getResourceManager().getAggregationLabels(getDefaultLanguage());
		if (aggregationLabels == null) {
			System.out.println(getClass().getName() + "::getAllAggregationLabel: translation not found for name " + ", with language " + getDefaultLanguage());
			if (!getDefaultLanguage().equals("en")) {
				aggregationLabels = getResourceManager().getAggregationLabels("en");
			} else {
				throw new FatalException(getClass().getName() + "::getAllAggregationLabel: translation not found for name " + ", with language " + getDefaultLanguage());
			}
		}

		return aggregationLabels;
	}

	@Override
	public ArrayList<String> getSectionLabels(String aggregationThemeType) {
		ArrayList<String> labels = getResourceManager().getSectionLabels(aggregationThemeType, getDefaultLanguage());
		if (labels == null) {
			System.out.println(getClass().getName() + "::getSectionLabels: translation not found for name " + aggregationThemeType + ", with language " + getDefaultLanguage());
			if (!getDefaultLanguage().equals("en")) {
				labels = getResourceManager().getSectionLabels(aggregationThemeType, "en");
			} else {
				throw new FatalException(getClass().getName() + "::getSectionLabels: translation not found for name " + aggregationThemeType + ", with language "
						+ getDefaultLanguage());
			}
		}

		return labels;
	}

	@Override
	public ArrayList<String> getSectionIds(String aggregationThemeType) {
		ArrayList<String> sectionIds = getResourceManager().getSectionIds(aggregationThemeType, getDefaultLanguage());
		return sectionIds;
	}

	@Override
	public ArrayList<Section> createNewSections(Aggregation aggregation) {
		ArrayList<Section> retSections = new ArrayList<Section>();

		ArrayList<String> sectionIds = getSectionIds(aggregation.getThemeType());
		for (String sectionId : sectionIds) {
			Section newSection = new Section();
			newSection.setCreationDate(new Date());
			newSection.setDone(false);
			newSection.setAggregation(aggregation);
			newSection.setThemeCode(sectionId);
			retSections.add(newSection);
			aggregation.addSection(newSection);
		}
		return retSections;
	}

	@Override
	public String getSectionContentLabels(String aggregationThemeType, String sectionType) {
		String label = getResourceManager().getSectionContentLabels(aggregationThemeType, sectionType, getDefaultLanguage());
		if (label == null) {
			System.out.println(getClass().getName() + "::getSectionContentLabels: translation not found for name " + aggregationThemeType + ", " + sectionType + ", with language "
					+ getDefaultLanguage());
			if (!getDefaultLanguage().equals("en")) {
				label = getResourceManager().getSectionContentLabels(aggregationThemeType, sectionType, "en");
			} else {
				throw new FatalException(getClass().getName() + "::getSectionContentLabels: translation not found for name " + aggregationThemeType + ", " + sectionType
						+ ", with language " + getDefaultLanguage());
			}
		}
		return label;
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

	/**
	 * load the aggregation labels to get all configured locales
	 * 
	 * @param className
	 */
	public void loadResourceLanguages(String className) {

		// HashMap<locale, HashMap<AggregationType, translated label>>
		HashMap<String, HashMap<String, String>> aggrLabels = getResourceManager().getAggregationLabels();
		setupLanguages(aggrLabels);
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
	public AggregationContext getCurrentAggregationContext() {

		return _AggregationContext;
	}

	@Override
	public AggregationContext createNewAggregationContext() {
		_AggregationContext = new AggregationContext();

		String applicationShortName = getConfigField("applicationShortName"); // TN95

		String countryDeployShortName = null;//
		countryDeployShortName = getConfigField("countryDeployShortName");

		_AggregationContext.setContextName(countryDeployShortName + "|" + applicationShortName);

		_AggregationContext.setSelectedCountry(getCurrentSelectionContext().getSelectedCountry());

		_AggregationContext.setSelectedProject(getCurrentSelectionContext().getSelectedProject());

		_AggregationContext.setSelectedCareCenter(getCurrentSelectionContext().getSelectedCareCenter());

		return _AggregationContext;
	}

	@Override
	public void setCurrentAggregationContext(AggregationContext aggregationContext) {
		if (aggregationContext == null)
			System.out.println("aggregationContext == null");
		_AggregationContext = aggregationContext;
	}

	@Override
	public Section getCurrentSection() {
		return _CurrentSection;
	}

	@Override
	public void setCurrentSection(Section Section) {
		_CurrentSection = Section;

	}


	@Override
	public Aggregation saveCurrentAggregation() throws ParamException {
		AggregationContext aggregationContext = getCurrentAggregationContext();

		// get the aggregation
		Aggregation aggregation = aggregationContext.getAggregation();
		
		//  update context id
		String idAggregationWithContext = buildAggregationContextId(aggregation.getThemeCode());
		aggregation.setThemeCode(idAggregationWithContext);
		
		aggregation.setModificationDate(new Date());

		aggregationContext = ServiceHelper.getAggregationDataManagerService().saveSelectedAggregationContext(aggregationContext);

		setAggregatedDataSaved(true);
		// if the currentAggregationContext has not been saved it will be
		// null
		// so replace it...
		setCurrentAggregationContext(aggregationContext);
		// necessary to update the view
		getCurrentAggregatedModelData().setAggregatedContextId(aggregationContext.getId());

		// update cache
		getAggregationContextCache().put(aggregationContext.getId(), aggregationContext);
		// get the aggregation
		aggregation = aggregationContext.getAggregation();
		return aggregation;

	}

	/**
	 * 
	 * @param results
	 *            : a map of conceptId values
	 * @param saveDb
	 *            : true if the current Section should be saved to db
	 * @throws ParamException
	 */
	@Override
	public Section saveSectionAttributes(HashMap<String, String> results, boolean saveDb) throws ParamException {
		Section currentSection = getCurrentSection();

		currentSection.setModificationDate(new Date());

		// get existing attributes
		List<ConceptIdValue> conceptIdValues = currentSection.getIdValues();
		Iterator<ConceptIdValue> it = conceptIdValues.iterator();
		while (it.hasNext()) {
			ConceptIdValue conceptIdValue = it.next();
			String newValue = results.get(conceptIdValue.getConceptId());
			if (newValue == null) {

				// this field is no more in the set
				// remove it from the aggregation list
				it.remove();
			} else {
				conceptIdValue.setConceptValue(newValue);
				// remove from hash to not be added again
				results.remove(conceptIdValue.getConceptId());
			}
		}

		// add all new concepts
		for (String key : results.keySet()) {
			String strVal = results.get(key);
			if (strVal != null) {
				ConceptIdValue civ = new ConceptIdValue();
				civ.setConceptId(key);
				civ.setConceptValue(results.get(key));
				civ.setSection(currentSection);
				currentSection.getIdValues().add(civ);
			}
		}

		if (saveDb) {
			currentSection = ServiceHelper.getSectionManagerService().saveSection(currentSection);

			// if the currentSection has not been saved it will be null
			// so replace it...
			setCurrentSection(currentSection);
			
			// then aggregation is updated too (?!), so attach it to aggregation context and save it
			AggregationContext aggregationContext = getCurrentAggregationContext();
			aggregationContext.setAggregation(currentSection.getAggregation());
			saveCurrentAggregation();

			// for caching purpose
			// if the currentSection has not been saved it will be null
			// so replace it...
			getCurrentSectionModelData().setSessionId(currentSection.getId());
		}

		return currentSection;
	}

	@Override
	public AggregatedModelData getCurrentAggregatedModelData() {
		return _AggregatedModelData;
	}

	@Override
	public void setCurrentAggregatedModelData(AggregatedModelData aggregationModelData) {
		_AggregatedModelData = aggregationModelData;

	}

	@Override
	public SectionModelData getCurrentSectionModelData() {
		return _SectionModelData;
	}

	@Override
	public void setCurrentSectionModelData(SectionModelData SectionModelData) {
		_SectionModelData = SectionModelData;

	}

	@Override
	public boolean isAggregatedDataSaved() {
		return _AggregationDataSaved;
	}

	@Override
	public void setAggregatedDataSaved(boolean _DataSaved) {
		this._AggregationDataSaved = _DataSaved;
	}

	@Override
	/* TN79 */
	public boolean isAggregatedAttributesDataSaved() {
		return _AggregationAttributesDataSaved;
	}

	@Override
	public boolean isSectionDataSaved() {
		return _SectionDataSaved;
	}

	@Override
	public void setSectionDataSaved(boolean _DataSaved) {
		this._SectionDataSaved = _DataSaved;
	}

	@Override
	/* TN79 */
	public boolean isSectionAttributesDataSaved() {
		return _SectionAttributesDataSaved;
	}

	@Override
	/* TN79 */
	public void setSectionAttributesDataSaved(boolean dataSaved) {
		_SectionAttributesDataSaved = dataSaved;
	}

	@Override
	public HashMap<Long, AggregationContext> getAggregationContextCache() {
		return _AggregationContextCache;
	}

	@Override
	public HashMap<Long, Section> getSectionCache() {
		return _SectionCache;
	}

	// cache of uniq visual components <className, Object>
	public HashMap<String, ComponentEnabler> getComponentCache() {
		return _ComponentCache;
	}

	// cache of uniq visual components <className, Object>
	public HashMap<String, ComponentCellUpdator> getComponentUpdatorCache() {
		return _ComponentUpdatorCache;
	}
	
	
	

	@Override
	// TN129
	public boolean isNewAggregatedDataInsertion() {
		return _NewAggregationData;
	}

	@Override
	public void setNewAggregatedDataInsertion(boolean newData) {
		_NewAggregationData = newData;
		if (!newData) {
			// we just saved a new inserted aggregation
			// clear the null value from the cache that was used for that new
			// aggregation
			if (getAggregationContextCache().get(null) != null) {
				getAggregationContextCache().remove(null);
			}

		}
	}

	@Override
	public boolean isNewSectionDataInsertion() {
		return _NewSectionData;
	}

	@Override
	public void setNewSectionDataInsertion(boolean newData) {
		_NewSectionData = newData;
		if (!newData) {
			// we just saved a new inserted Section
			// clear the null value from the cache that was used for that new
			// Section
			if (getSectionCache().get(null) != null) {
				getSectionCache().remove(null);
			}

		}
	}

	@Override
	public ArrayList<IdType[]> getSectionTableQuestionIdTypes(String sectionId) {
		// HashMap<sectionTableid, ArrayList<IdType[]>>
		HashMap<String, ArrayList<IdType[]>> sectionTableIdTypes = ServiceHelper.getEntryFormConfigurationManagerService().getResourceManager().getSectionTableIdTypes(sectionId);

		return sectionTableIdTypes.get(sectionId);
	}

	/**
	 * @return the QuestionLabels
	 *  HashMap<locale, HashMap<conceptId, conceptLabel>>>
	 */
	@Override
	public HashMap<String, HashMap<String, String>> getSectionTableQuestionLabels(String sectionId) {

		// HashMap<locale, HashMap<sectionTableConceptId, sectionTableConceptLabel>>
		return getResourceManager().getSectionTableLabels(sectionId, getDefaultLanguage());
	}

}
