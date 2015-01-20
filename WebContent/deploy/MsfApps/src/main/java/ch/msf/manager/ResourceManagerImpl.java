package ch.msf.manager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

import ch.msf.CommonConstants;
import ch.msf.error.ConfigException;
import ch.msf.error.FatalException;
import ch.msf.util.IOUtils;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;
import ch.msf.util.MiscelaneousUtils;
import ch.msf.util.StackTraceUtil;

public class ResourceManagerImpl implements ResourceManager {

	// dictionary fields id and types
	// HashMap<entityclassname, ArrayList<IdType>>
	private HashMap<String, ArrayList<IdType>> _QuestionIdTypes = null;

	// dictionary fields labels per locale HashMap<classTypeToCheck,
	// HashMap<locale, HashMap<fieldId, fieldLabel>>>
	private HashMap<String, HashMap<String, HashMap<String, String>>> _QuestionLabels = null;

	// dictionary combo values per locale
	// HashMap<application, HashMap<locale, ArrayList<idCombo, label>>>
	private HashMap<String, HashMap<String, ArrayList<KeyValue>>> _ComboValueLocales = new HashMap<String, HashMap<String, ArrayList<KeyValue>>>();

	// mapping with fields id and entity class attributes
	// HashMap<entityclassname, ArrayList<IdType>>
	private HashMap<String, ArrayList<KeyValue>> _QuestionIdToClassAttributes = null;

	// HashMap<tableName, HashMap<locale, String[table titles...]>>
	private HashMap<String, HashMap<String, String[]>> _TableTitleTranslations = new HashMap<String, HashMap<String, String[]>>();

	// TN84 HashMap<locale, HashMap<encounterName, translated label>>
	private HashMap<String, HashMap<String, String>> _EncounterLabels = new HashMap<String, HashMap<String, String>>();

	// HashMap<locale, HashMap<AggregationType, translated label>>
	private HashMap<String, HashMap<String, String>> _AggregationLabels = new HashMap<String, HashMap<String, String>>();

	// HashMap<aggreg type, ArrayList<sectionId layoutOrder>>
	private HashMap<String, ArrayList<IdType>> _SectionsIdOrders = null;

	// HashMap<aggreg type, HashMap<locale, HashMap<sectionId, label>>>
	private HashMap<String, HashMap<String, HashMap<String, String>>> _SectionLabels = null;

	// HashMap<sectionTypeId, ArrayList<sectionTableId>>
	private HashMap<String, ArrayList<String>> _SectionsTableIds = null;

	// HashMap<section typeId, HashMap<locale, HashMap<sectionTableConceptId,
	// label>>>
	private HashMap<String, HashMap<String, HashMap<String, String>>> _SectionTableLabels = null;

	// HashMap<sectionTableid, ArrayList<conceptId-conceptType>>
	private HashMap<String, ArrayList<IdType[]>> _SectionTableIdTypes = null;

	protected ConfigurationManagerBase _ConfigurationManager;

	public ResourceManagerImpl() {
		System.out.println("ResourceManagerImpl constructor");
	}

	/**
	 * load the model from the resource files
	 * 
	 * @param
	 */
	private void buildModel(String classTypeToCheck) {

		String resourceFileFieldsIdType = classTypeToCheck + "-FieldsIdType.txt";
		String resourceFileFieldsLabel = classTypeToCheck + "-FieldsLabel.txt";

		try {
			System.out.println("buildModel resourceFile=" + resourceFileFieldsIdType);
			ArrayList<String> fieldsIdType = null;
			ArrayList<String> fieldsLabel = null;

			// read the file resources
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(resourceFileFieldsIdType, classList);
			System.out.println("Resource url = " + url);
			if (url != null) {

				fieldsIdType = readFile(null, null, url, null);
				// url = Config.class.getResource(resourceFileFieldsLabel);
				url = IOUtils.getResource(resourceFileFieldsLabel, classList);
				fieldsLabel = readFile(null, null, url, null);
			} else {
				// local (NON JWS)
				String workspaceresource = getConfigurationManager().getConfigField("workspaceresource");

				//
				// resourceFileFieldsIdType =
				// "H:\\devel\\workspace64Bits\\SGBV\\src\\main\\resources\\"
				// + resourceFileFieldsIdType;
				// resourceFileFieldsLabel =
				// "H:\\devel\\workspace64Bits\\SGBV\\src\\main\\resources\\"
				// + resourceFileFieldsLabel;
				resourceFileFieldsIdType = workspaceresource + resourceFileFieldsIdType;
				resourceFileFieldsLabel = workspaceresource + resourceFileFieldsLabel;

				fieldsIdType = readFile(resourceFileFieldsIdType, null, null, null);
				fieldsLabel = readFile(resourceFileFieldsLabel, null, null, null);
			}
			// build the dictionary field list
			if (fieldsIdType != null && !fieldsIdType.isEmpty() && fieldsLabel != null && !fieldsLabel.isEmpty()) {
				// allocate the list of fields
				ArrayList<IdType> questionIdTypes = new ArrayList<IdType>();
				// allocate the list of fields attribute
				ArrayList<KeyValue> questionIdToClassAttributess = new ArrayList<KeyValue>();

				// determine the separator
				String separator = IOUtils.findSeparator(fieldsIdType.get(0), ";\t ");
				if (separator == null)
					throw new FatalException("Fatal: No field separator was found in " + resourceFileFieldsIdType);

				// parse all dictionary ids

				// set used for checking
				HashSet<String> fieldsRead = new HashSet<String>();

				int lineCount = 0;
				try {
					for (String field : fieldsIdType) {
						// if (field.contains("symptom_petechia_purpura"))
						// System.out.println("found");
						lineCount++;

						if (!field.equals("") && !field.startsWith("//")) { // TN127
							String[] parts = field.split(separator);
							String fieldId = parts[0].trim();
							boolean b1Ok = fieldId != null && !fieldId.isEmpty();
							String fieldType = parts[1].trim();
							boolean b2Ok = fieldType != null && !fieldType.isEmpty();

							if (b1Ok && b2Ok) {
								IdType idType = new IdType(fieldId, fieldType);
								questionIdTypes.add(idType);
								fieldsRead.add(fieldId);

								if (parts.length > 2) {
									// mapping id - class attribute
									String attributeMapping = parts[2].trim();
									boolean b3Ok = attributeMapping != null && !attributeMapping.isEmpty();

									if (b3Ok) {
										KeyValue keyValue = new KeyValue(fieldId, attributeMapping);
										questionIdToClassAttributess.add(keyValue);
									}
								}

							} else {
								System.out.println("label skipped(1) " + fieldId);
							}
						} else {
							System.out.println("field skipped(1) " + field);
						}
					}
				} catch (Exception e) {
					throw new FatalException("Problem reading " + resourceFileFieldsIdType + " file on line " + lineCount, e);
				}
				if (_QuestionIdTypes == null)
					_QuestionIdTypes = new HashMap<String, ArrayList<IdType>>();
				_QuestionIdTypes.put(classTypeToCheck, questionIdTypes);

				if (_QuestionIdToClassAttributes == null)
					_QuestionIdToClassAttributes = new HashMap<String, ArrayList<KeyValue>>();
				_QuestionIdToClassAttributes.put(classTypeToCheck, questionIdToClassAttributess);

				System.out.println("Dictionary Ids list size =" + questionIdTypes.size());

				// build the label list
				// allocate the list of fields labels
				HashMap<String, HashMap<String, String>> questionLabels = new HashMap<String, HashMap<String, String>>();

				// determine the separator
				separator = IOUtils.findSeparator(fieldsLabel.get(0), ";\t ");
				if (separator == null)
					throw new FatalException("Fatal: No field separator was found in " + resourceFileFieldsLabel);

				// parse all dictionary labels
				try {
					lineCount = 0;
					for (String field : fieldsLabel) {
						lineCount++;
						if (!field.equals("")) {
							String[] parts = field.split(separator);
							String fieldId = parts[0].trim();
							boolean b1Ok = fieldId != null && !fieldId.isEmpty();
							// String fieldLabel = parts[1].trim();
							String fieldLabel = parts[1]; // TN96 entryform: do
															// not trim on
															// labels
							boolean b2Ok = fieldLabel != null && !fieldLabel.isEmpty();
							// if
							// (fieldId.equals("hiv_hospitalisation_end_start"))
							// System.out.println("hiv_hospitalisation_end_start");
							String fieldLocale = parts[2].trim();
							boolean b3Ok = fieldLocale != null && !fieldLocale.isEmpty();

							// insert label only if the field exist in our field
							// list
							if (b1Ok && b2Ok && b3Ok && fieldsRead.contains(fieldId)) {
								// check if locale already exists in our map
								HashMap<String, String> labels = null;
								if ((labels = questionLabels.get(fieldLocale)) == null) {
									labels = new HashMap<String, String>();
									questionLabels.put(fieldLocale, labels);
								}
								labels.put(fieldId, fieldLabel);
							} else {
								System.out.println("label skipped(2) " + fieldId);
							}
						}
					}
				} catch (Exception e) {
					throw new FatalException("Problem reading " + resourceFileFieldsLabel + " file on line " + lineCount, e);
				}
				if (_QuestionLabels == null)
					_QuestionLabels = new HashMap<String, HashMap<String, HashMap<String, String>>>();
				_QuestionLabels.put(classTypeToCheck, questionLabels);

			} else {
				if (fieldsIdType == null) {
					throw new FatalException("Fatal: Could not read anything from file " + resourceFileFieldsIdType);
				}
				throw new FatalException("Fatal: Could not read anything from file " + resourceFileFieldsLabel);
			}
		} catch (Exception e) {
			throw new FatalException(StackTraceUtil.getCustomStackTrace(e));
		}

	}

	/**
	 * 
	 * @param className
	 *            : entity (ex:patient) to which combobox is linked
	 * @param comboName
	 *            : the combo name
	 */
	private void buildComboModel(String className, String comboName) {
		String resourceKey = className + "-" + comboName;
		String resourceFileComboValue = resourceKey + "-comboValue.txt";

		// TN129 check for special combo local resource
		boolean comboLocalResource = false;
		String codedListType = CommonConstants.CODEDLIST_EXTENTION_CONCEPT_TYPE;
		int index = resourceKey.indexOf(codedListType);
		if (index != -1) {// load file from a special location ()
			comboLocalResource = true;
			// resourceKey = resourceKey.substring(0, index);
		}

		int lineCount = 0;
		try {
			System.out.println("buildComboModel resourceFile=" + resourceFileComboValue);
			ArrayList<String> comboValues = null;

			// read the file resources
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(resourceFileComboValue, classList);
			System.out.println("Resource url = " + url);
			if (url != null) {

				comboValues = readFile(null, null, url, null);
			} else {
				// local (NON JWS)

				// TN129 check if special case
				if (comboLocalResource) {
					String workspaceresource = getConfigurationManager().getApplicationDirectory();
					resourceFileComboValue = workspaceresource + System.getProperty("file.separator") + resourceFileComboValue;
					System.out.println("Reading special comboLocalResource file at " + resourceFileComboValue);
				} else {

					String workspaceresource = getConfigurationManager().getConfigField("workspaceresource");
					resourceFileComboValue = workspaceresource + resourceFileComboValue;
				}

				comboValues = readFile(resourceFileComboValue, null, null, null);

			}

			// build the dictionary field list
			if (comboValues != null && (!comboValues.isEmpty() || (comboValues.isEmpty() && comboLocalResource))) {

				// determine the separator
				String separator = null;
				if (!comboValues.isEmpty())
					separator = IOUtils.findSeparator(comboValues.get(0), "\t ");
				if (separator == null && !comboLocalResource)
					throw new FatalException("Fatal: No field separator was found in " + resourceFileComboValue);

				// allocate the list of value locales
				HashMap<String, ArrayList<KeyValue>> valueLocales = new HashMap<String, ArrayList<KeyValue>>();

				// parse all values locales
				for (String fields : comboValues) {
					lineCount++;
					String[] valueLanguage = fields.split(separator);
					String id = valueLanguage[0].trim();
					boolean b0Ok = id != null && !id.isEmpty();
					String value = valueLanguage[1].trim();
					boolean b1Ok = value != null && !value.isEmpty();
					String fieldLocale = valueLanguage[2].trim();
					boolean b2Ok = fieldLocale != null && !fieldLocale.isEmpty();

					// insert label only if the field exist in our field
					// list
					if (b0Ok && b1Ok && b2Ok) {
						// check if locale already exists in our map
						ArrayList<KeyValue> values = null;
						if ((values = valueLocales.get(fieldLocale)) == null) {
							values = new ArrayList<KeyValue>();
							valueLocales.put(fieldLocale, values);
						}
						// put the new value in queue as it is read
						values.add(new KeyValue(id, value));
					} else {
						System.out.println("value skipped(2) " + value);
					}
				}
				_ComboValueLocales.put(resourceKey, valueLocales);

			} else {
				if (comboValues == null) {
					throw new FatalException("Fatal: Could not read anything from file " + resourceFileComboValue);
				}
				if (!comboLocalResource) // local file can be empty
					throw new FatalException("Fatal: Could not read anything from file " + resourceFileComboValue);

			}
		} catch (Exception e) {
			throw new FatalException("Problem reading " + resourceFileComboValue + " file on line " + lineCount, e);
		}
	}

	/**
	 * 
	 * @param theClass
	 *            : the class of entity
	 * @param subType
	 *            : a string specifying the subtype of the class
	 * @param noMapping
	 *            : if true, discard the questionid that are mapped to an entity class
	 * @return a list of id/types related to locale for theClass/subType
	 */
	@Override
	public ArrayList<IdType> getQuestionIdTypes(Class theClass, String subType, Boolean noMapping) {
		String className = MiscelaneousUtils.getClassName(theClass);
		String classTypeToCheck = className;
		if (subType != null)
			classTypeToCheck += subType;

		if (_QuestionIdTypes == null || _QuestionIdTypes.get(classTypeToCheck) == null) {
			buildModel(classTypeToCheck);
		}
		ArrayList<IdType> idTypes = _QuestionIdTypes.get(classTypeToCheck);
		if (noMapping == null || !noMapping)
			return idTypes;

		// return the collection without the conceptids that are mapped to class
		// fields
		ArrayList<IdType> retIdTypes = new ArrayList<IdType>();

		// get the attribute mapping for this entity
		ArrayList<KeyValue> keyValueAttributeMappings = getQuestionIdToClassAttributes(theClass, subType);

		// ...and update the view, set all concept values
		// get the concept values...
		for (IdType idType : idTypes) {

			String conceptId = idType._Key;
			boolean found = false;
			for (KeyValue keyValueAttributeMapping : keyValueAttributeMappings) {
				if (keyValueAttributeMapping._Key.equals(conceptId)) {
					found = true;
					break;
				}
			}
			if (!found)
				retIdTypes.add(idType);
		}
		return retIdTypes;
	}

	/**
	 * 
	 * @param theClass
	 *            : the class of entity
	 * @param subType
	 *            : a string specifying the subtype of the class
	 * @return a map of labels related to locale for theClass/subType
	 */
	@Override
	public HashMap<String, HashMap<String, String>> getQuestionLabels(String className, String subType) {
		// String className = MiscelaneousUtils.getClassName(theClass);
		String classTypeToCheck = className;
		if (subType != null)
			classTypeToCheck += subType;

		if (_QuestionLabels == null || _QuestionLabels.get(classTypeToCheck) == null) {
			// not built yet
			buildModel(classTypeToCheck);
		}
		HashMap<String, HashMap<String, String>> labelMaps = _QuestionLabels.get(classTypeToCheck);

		return labelMaps;
	}

	/**
	 * 
	 * @param theClass
	 *            : the class of entity
	 * @param subType
	 *            : a string specifying the subtype of the class
	 * @param noMapping
	 *            : if true, discard the questionid that are mapped to an entity class
	 * @return a map of combolabels related to locale for theClass/subType
	 */
	@Override
	// TN129
	public ArrayList<KeyValue> getComboLabels(Class theClass, String subType, String comboName, String locale) {

		String className = MiscelaneousUtils.getClassName(theClass);

		String resourceKey = className + "-" + comboName;

		if (_ComboValueLocales.get(resourceKey) != null) {
			HashMap<String, ArrayList<KeyValue>> applicMap = _ComboValueLocales.get(resourceKey);
			if (applicMap.get(locale) != null) {
				return applicMap.get(locale);
			}
		}

		// not built yet
		buildComboModel(className, comboName);

		if (_ComboValueLocales.get(resourceKey) != null) {
			HashMap<String, ArrayList<KeyValue>> applicMap = _ComboValueLocales.get(resourceKey);
			if (applicMap.get(locale) != null) {
				return applicMap.get(locale);
			}
		}

		return null;
	}

	// TN129
	@Override
	public void clearResourceKeyFromCache(Class theClass, String subType, String comboName) {
		String className = MiscelaneousUtils.getClassName(theClass);
		String resourceKey = className + "-" + comboName;
		_ComboValueLocales.remove(resourceKey);
	}

	/**
	 * 
	 * @param theClass
	 *            : the class of entity
	 * @param subType
	 *            : a string specifying the subtype of the class
	 * @param locale
	 *            :
	 * @param noMapping
	 *            : if true, discard the questionid that are mapped to an entity class
	 * @return a map of combolabels related to locale for theClass/subType
	 */
	@Override
	public HashMap<String, ArrayList<KeyValue>> getAllComboLabels(Class theClass, String subType, String locale, Boolean noMapping) {

		// String className = MiscelaneousUtils.getClassName(theClass);
		// a map of <"combo name", combo values lists>
		HashMap<String, ArrayList<KeyValue>> comboboxValueMap = new HashMap<String, ArrayList<KeyValue>>();

		ArrayList<IdType> idTypes = getQuestionIdTypes(theClass, subType, noMapping);
		// load all combobox values
		for (IdType idType : idTypes) {
			String fieldType = idType._Value.trim();
			String comboResourceToLoad = null;
			if (fieldType.startsWith("Combo-")) {
				String[] parts = fieldType.split("-");
				if (parts.length == 2) {
					comboResourceToLoad = parts[1];
					// System.out.println(getClass().getName()
					// + "DicoData: resourceToLoad = "
					// + comboResourceToLoad);
					ArrayList<KeyValue> comboValues = getComboLabels(theClass, subType, comboResourceToLoad, locale);
					comboboxValueMap.put(comboResourceToLoad, comboValues);

				} else
					throw new FatalException(getClass().getName() + "DicoData: Combo with wrong format on " + theClass + subType);
			}
		}

		return comboboxValueMap;
	}

	/**
	 * 
	 */
	@Override
	public ArrayList<KeyValue> getQuestionIdToClassAttributes(Class theClass, String subType) {
		String className = MiscelaneousUtils.getClassName(theClass);
		String classTypeToCheck = className;
		if (subType != null)
			classTypeToCheck += subType;

		if (_QuestionIdToClassAttributes == null || _QuestionIdToClassAttributes.get(classTypeToCheck) == null) {
			buildModel(classTypeToCheck);
		}

		ArrayList<KeyValue> keyValue = _QuestionIdToClassAttributes.get(classTypeToCheck);
		return keyValue;

	}

	/**
	 * 
	 */
	private void readTableTitleModels() {

		String resourceFileTableTitle = "tableTitles.txt";

		int lineCount = 0;
		try {
			System.out.println("readTableTitleModels resourceFile=" + resourceFileTableTitle);
			ArrayList<String> tablesTitles = null;

			// read the file resources
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(resourceFileTableTitle, classList);
			System.out.println("Resource url = " + url);
			if (url != null) {

				tablesTitles = readFile(null, null, url, null);
			} else {
				// local (NON JWS)
				// resourceFileTableTitle =
				// "H:\\devel\\workspace64Bits\\SGBV\\src\\main\\resources\\"
				// + resourceFileTableTitle;
				String workspaceresource = getConfigurationManager().getConfigField("workspaceresource");
				resourceFileTableTitle = workspaceresource + resourceFileTableTitle;

				tablesTitles = readFile(resourceFileTableTitle, null, null, null);

			}
			// build the dictionary field list
			if (tablesTitles != null && !tablesTitles.isEmpty()) {

				// determine the separator
				String separator = IOUtils.findSeparator(tablesTitles.get(0), ";\t ");
				if (separator == null)
					throw new FatalException("Fatal: No field separator was found in " + resourceFileTableTitle);

				// allocate the list of value locales
				// HashMap<String, ArrayList<KeyValue>> valueLocales = new
				// HashMap<String, ArrayList<KeyValue>>();
				HashMap<String, String[]> titlesLocales = null; //
				String tableName = null;
				// parse all values locales
				for (String tableTitlesStr : tablesTitles) {
					lineCount++;
					if (!tableTitlesStr.equals("")) {
						String[] tableFields = tableTitlesStr.split(separator);
						tableName = tableFields[0].trim();
						boolean b0Ok = tableName != null && !tableName.isEmpty();
						String locale = tableFields[1].trim();
						boolean b1Ok = locale != null && !locale.isEmpty();
						boolean b2Ok = tableFields.length > 2; // table titles

						if (b0Ok && b1Ok && b2Ok) {
							titlesLocales = _TableTitleTranslations.get(tableName);
							if (titlesLocales == null)
								titlesLocales = new HashMap<String, String[]>();
							// copy the remaining array into new array
							String[] tableTitles = Arrays.copyOfRange(tableFields, 2, tableFields.length);
							titlesLocales.put(locale, tableTitles);
							_TableTitleTranslations.put(tableName, titlesLocales);
						} else {
							System.out.println("value skipped(2) " + locale);
						}
					}
				}

			} else {
				if (tablesTitles == null) {
					throw new FatalException("Fatal: Could not read anything from file " + resourceFileTableTitle);
				}
				throw new FatalException("Fatal: Could not read anything from file " + resourceFileTableTitle);

			}
		} catch (Exception e) {
			throw new FatalException("Problem reading " + resourceFileTableTitle + " file on line " + lineCount, e);
		}
	}

	/**
	 * TN84
	 */
	private void readAllEncounterLabels() {

		String resourceFile = CommonConstants.ENCOUNTER_LABELS_FILENAME;

		int lineCount = 0;
		try {
			System.out.println("readAllEncounterLabels resourceFile=" + resourceFile);
			ArrayList<String> encounterLabels = null;

			// read the file resources
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(resourceFile, classList);
			System.out.println("Resource url = " + url);
			if (url != null) {

				encounterLabels = readFile(null, null, url, null);
			} else {
				// local (NON JWS)
				String workspaceresource = getConfigurationManager().getConfigField("workspaceresource");
				resourceFile = workspaceresource + resourceFile;
				encounterLabels = readFile(resourceFile, null, null, null);
			}
			// build the dictionary field list
			if (encounterLabels != null && !encounterLabels.isEmpty()) {

				// determine the separator
				String separator = IOUtils.findSeparator(encounterLabels.get(0), ";\t ");
				if (separator == null)
					throw new FatalException("Fatal: No field separator was found in " + resourceFile);

				// allocate the list of value locales
				HashMap<String, String> encounterLocales = null; //
				String encounterName = null;
				// parse all values locales
				for (String encounterLabel : encounterLabels) {
					lineCount++;
					if (!encounterLabel.equals("")) {
						String[] parts = encounterLabel.split(separator);
						encounterName = parts[0].trim();
						boolean b1Ok = encounterName != null && !encounterName.isEmpty();
						String fieldLabel = parts[1].trim();
						boolean b2Ok = fieldLabel != null && !fieldLabel.isEmpty();
						String fieldLocale = parts[2].trim();
						boolean b3Ok = fieldLocale != null && !fieldLocale.isEmpty();

						if (b1Ok && b1Ok && b2Ok) {
							encounterLocales = _EncounterLabels.get(fieldLocale);
							if (encounterLocales == null) {
								encounterLocales = new HashMap<String, String>();
								_EncounterLabels.put(fieldLocale, encounterLocales);
							}

							encounterLocales.put(encounterName, fieldLabel);
						} else {
							throw new FatalException("Problem reading " + resourceFile + " file on line " + lineCount);
						}
					}
				}

			} else {
				if (encounterLabels == null) {
					throw new FatalException("Fatal: Could not read anything from file " + resourceFile);
				}
				throw new FatalException("Fatal: Could not read anything from file " + resourceFile);

			}
		} catch (Exception e) {
			throw new FatalException("Problem reading " + resourceFile + " file on line " + lineCount, e);
		}
	}

	/**
	 * 
	 */
	private void readAllAggregationLabels() {

		String resourceFile = CommonConstants.AGGREGATION_LABELS_FILENAME;

		int lineCount = 0;
		try {
			System.out.println("readAllThematicPeriodLabels resourceFile=" + resourceFile);
			ArrayList<String> aggregatedLabels = null;

			// read the file resources
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(resourceFile, classList);
			System.out.println("Resource url = " + url);
			if (url != null) {

				aggregatedLabels = readFile(null, null, url, null);
			} else {
				// local (NON JWS)
				String workspaceresource = getConfigurationManager().getConfigField("workspaceresource");
				resourceFile = workspaceresource + resourceFile;
				aggregatedLabels = readFile(resourceFile, null, null, null);
			}
			// build the dictionary field list
			if (aggregatedLabels != null && !aggregatedLabels.isEmpty()) {

				// determine the separator
				String separator = IOUtils.findSeparator(aggregatedLabels.get(0), ";\t ");
				if (separator == null)
					throw new FatalException("Fatal: No field separator was found in " + resourceFile);

				// allocate the list of value locales
				HashMap<String, String> aggregatedLocales = null; //
				String aggregatedConfigId = null;
				// parse all values locales
				for (String aggregatedLabel : aggregatedLabels) {
					lineCount++;
					if (!aggregatedLabel.equals("")) {
						String[] parts = aggregatedLabel.split(separator);
						aggregatedConfigId = parts[0].trim();
						boolean b1Ok = aggregatedConfigId != null && !aggregatedConfigId.isEmpty();
						String fieldLabel = parts[1].trim();
						boolean b2Ok = fieldLabel != null && !fieldLabel.isEmpty();
						String fieldLocale = parts[2].trim();
						boolean b3Ok = fieldLocale != null && !fieldLocale.isEmpty();

						if (b1Ok && b1Ok && b2Ok) {
							aggregatedLocales = _AggregationLabels.get(fieldLocale);
							if (aggregatedLocales == null) {
								aggregatedLocales = new HashMap<String, String>();
								_AggregationLabels.put(fieldLocale, aggregatedLocales);
							}

							aggregatedLocales.put(aggregatedConfigId, fieldLabel);
						} else {
							throw new FatalException("Problem reading " + resourceFile + " file on line " + lineCount);
						}
					}
				}

			} else {
				if (aggregatedLabels == null) {
					throw new FatalException("Fatal: Could not read anything from file " + resourceFile);
				}
				throw new FatalException("Fatal: Could not read anything from file " + resourceFile);

			}
		} catch (Exception e) {
			throw new FatalException("Problem reading " + resourceFile + " file on line " + lineCount, e);
		}
	}

	/**
	 * load the model from the resource files
	 * 
	 * @param
	 */
	private void readAggregationSectionsInfo(String aggregationType) {

		// get all aggregation types
		String codedListType = CommonConstants.AGGREGATION_SECTION_FILENAME_PREFIX;
		String resourceFileSectionIdType = codedListType + aggregationType + "-SectionIds.txt";
		String resourceFileSectionLabel = codedListType + aggregationType + "-SectionLabels.txt";

		try {
			System.out.println("buildModel resourceFile=" + resourceFileSectionIdType);
			ArrayList<String> sectionsIdType = null;
			ArrayList<String> sectionsLabel = null;

			// read the file resources
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(resourceFileSectionIdType, classList);
			System.out.println("Resource url = " + url);
			if (url != null) {

				sectionsIdType = readFile(null, null, url, null);
				// url = Config.class.getResource(resourceFileFieldsLabel);
				url = IOUtils.getResource(resourceFileSectionLabel, classList);
				sectionsLabel = readFile(null, null, url, null);
			} else {
				// local (NON JWS)
				String workspaceresource = getConfigurationManager().getConfigField("workspaceresource");

				resourceFileSectionIdType = workspaceresource + resourceFileSectionIdType;
				resourceFileSectionLabel = workspaceresource + resourceFileSectionLabel;

				sectionsIdType = readFile(resourceFileSectionIdType, null, null, null);
				sectionsLabel = readFile(resourceFileSectionLabel, null, null, null);
			}
			// build the dictionary field list
			if (sectionsIdType != null && !sectionsIdType.isEmpty() && sectionsLabel != null && !sectionsLabel.isEmpty()) {
				// allocate the list of fields
				ArrayList<IdType> sectionIdOrders = new ArrayList<IdType>();
				// allocate the list of fields attribute
				// ArrayList<KeyValue> sectionIdToClassAttributess = new
				// ArrayList<KeyValue>();

				// determine the separator
				String separator = IOUtils.findSeparator(sectionsIdType.get(0), ";\t ");
				if (separator == null)
					throw new FatalException("Fatal: No field separator was found in " + resourceFileSectionIdType);

				// parse all dictionary ids

				// set used for checking
				HashSet<String> fieldsRead = new HashSet<String>();

				int lineCount = 0;
				try {
					for (String field : sectionsIdType) {
						// if (field.contains("symptom_petechia_purpura"))
						// System.out.println("found");
						lineCount++;

						if (!field.equals("") && !field.startsWith("//")) { // TN127
							String[] parts = field.split(separator);
							String fieldId = parts[0].trim();
							boolean b1Ok = fieldId != null && !fieldId.isEmpty();
							String fieldOrder = parts[1].trim();
							boolean b2Ok = fieldOrder != null && !fieldOrder.isEmpty();

							if (b1Ok && b2Ok) {
								IdType idType = new IdType(fieldId, fieldOrder);
								sectionIdOrders.add(idType);
								fieldsRead.add(fieldId);

							} else {
								System.out.println("label skipped(1) " + fieldId);
							}
						} else {
							System.out.println("field skipped(1) " + field);
						}
					}
				} catch (Exception e) {
					throw new FatalException("Problem reading " + resourceFileSectionIdType + " file on line " + lineCount, e);
				}
				if (_SectionsIdOrders == null)
					_SectionsIdOrders = new HashMap<String, ArrayList<IdType>>();
				_SectionsIdOrders.put(aggregationType, sectionIdOrders);

				System.out.println("Dictionary Ids list size =" + sectionIdOrders.size());

				// build the label list
				// allocate the list of fields labels
				HashMap<String, HashMap<String, String>> sectionsLabels = new HashMap<String, HashMap<String, String>>();

				// determine the separator
				separator = IOUtils.findSeparator(sectionsLabel.get(0), ";\t ");
				if (separator == null)
					throw new FatalException("Fatal: No field separator was found in " + resourceFileSectionLabel);

				// parse all dictionary labels
				try {
					lineCount = 0;
					for (String field : sectionsLabel) {
						lineCount++;
						if (!field.equals("")) {
							String[] parts = field.split(separator);
							String fieldId = parts[0].trim();
							boolean b1Ok = fieldId != null && !fieldId.isEmpty();
							// String fieldLabel = parts[1].trim();
							String fieldLabel = parts[1]; //
							boolean b2Ok = fieldLabel != null && !fieldLabel.isEmpty();

							String fieldLocale = parts[2].trim();
							boolean b3Ok = fieldLocale != null && !fieldLocale.isEmpty();

							// insert label only if the field exist in our field
							// list
							if (b1Ok && b2Ok && b3Ok && fieldsRead.contains(fieldId)) {
								// check if locale already exists in our map
								HashMap<String, String> labels = null;
								if ((labels = sectionsLabels.get(fieldLocale)) == null) {
									labels = new HashMap<String, String>();
									sectionsLabels.put(fieldLocale, labels);
								}
								labels.put(fieldId, fieldLabel);
							} else {
								System.out.println("label skipped(2) " + fieldId);
							}
						}
					}
				} catch (Exception e) {
					throw new FatalException("Problem reading " + resourceFileSectionLabel + " file on line " + lineCount, e);
				}
				if (_SectionLabels == null)
					_SectionLabels = new HashMap<String, HashMap<String, HashMap<String, String>>>();
				_SectionLabels.put(aggregationType, sectionsLabels);

			} else {
				if (sectionsIdType == null) {
					throw new FatalException("Fatal: Could not read anything from file " + resourceFileSectionIdType);
				}
				throw new FatalException("Fatal: Could not read anything from file " + resourceFileSectionLabel);
			}
		} catch (Exception e) {
			throw new FatalException(StackTraceUtil.getCustomStackTrace(e));
		}

	}

	@Override
	public String[] getTableTitleTranslation(String tableName, String locale) {

		if (_TableTitleTranslations.get(tableName) != null) {
			HashMap<String, String[]> tableTitleTranslation = _TableTitleTranslations.get(tableName);
			if (tableTitleTranslation.get(locale) != null) {
				return tableTitleTranslation.get(locale);
			}
		}

		// not built yet
		readTableTitleModels();

		if (_TableTitleTranslations.get(tableName) != null) {
			HashMap<String, String[]> tableTitleTranslation = _TableTitleTranslations.get(tableName);
			if (tableTitleTranslation.get(locale) != null) {
				return tableTitleTranslation.get(locale);
			}
		}
		return null;
	}

	/**
	 * TN84 read encounter labels
	 */
	@Override
	public String getEncounterLabel(String encounterName, String locale) {

		if (_EncounterLabels.get(locale) != null) {
			HashMap<String, String> encounterLabels = _EncounterLabels.get(locale);
			if (encounterLabels.get(encounterName) != null) {
				return encounterLabels.get(encounterName);
			}
		}

		// not built yet
		readAllEncounterLabels();

		if (_EncounterLabels.get(locale) != null) {
			HashMap<String, String> encounterLabels = _EncounterLabels.get(locale);

			return encounterLabels.get(encounterName);
		}
		return null;
	}

	@Override
	public String getAggregationLabel(String aggregationConfigId, String locale) {

		if (_AggregationLabels.get(locale) != null) {
			HashMap<String, String> encounterLabels = _AggregationLabels.get(locale);
			if (encounterLabels.get(aggregationConfigId) != null) {
				return encounterLabels.get(aggregationConfigId);
			}
		}

		// not built yet...
		readAllAggregationLabels();

		if (_AggregationLabels.get(locale) != null) {
			HashMap<String, String> encounterLabels = _AggregationLabels.get(locale);

			return encounterLabels.get(aggregationConfigId);
		}
		return null;
	}

	@Override
	public ArrayList<String> getAggregationLabels(String locale) {
		if (_AggregationLabels.isEmpty()) {
			// not built yet...
			readAllAggregationLabels();
		}

		// HashMap<locale, HashMap<AggregationType, translated label>>
		HashMap<String, String> aggregLabels = _AggregationLabels.get(locale);

		// labels are in the values

		return new ArrayList<String>(aggregLabels.values());
	}

	@Override
	// HashMap<locale, HashMap<AggregationType, translated label>>
	public HashMap<String, HashMap<String, String>> getAggregationLabels() {

		if (_AggregationLabels.isEmpty()) {
			// not built yet...
			readAllAggregationLabels();
		}
		return _AggregationLabels;
	}

	private HashMap<String, String> getSectionIdLabels(String aggregationThemeType, String locale) {

		// ArrayList<KeyValue> retList = new ArrayList<KeyValue>();
		boolean sectionsRead = false;
		do {
			if (_SectionLabels != null && _SectionLabels.get(aggregationThemeType) != null) {
				// HashMap<locale, HashMap<sectionId, label>>
				HashMap<String, HashMap<String, String>> sectionLabelLocales = _SectionLabels.get(aggregationThemeType);
				if (sectionLabelLocales.get(locale) != null) {

					HashMap<String, String> idLabels = sectionLabelLocales.get(locale);
					// for (String id : idLabels.keySet()) {
					// String label = idLabels.get(id);
					// KeyValue keyValue = new KeyValue(id, label);
					// retList.add(keyValue);
					// }
					return idLabels;
				}
			}
			if (sectionsRead)
				break;
			// not built yet...
			readAggregationSectionsInfo(aggregationThemeType);
			sectionsRead = true;

		} while (true);

		return null; // just to satisfy compiler
	}

	/**
	 * build a list of labels according to the order associated with the section ids
	 */
	@Override
	public ArrayList<String> getSectionLabels(String aggregationThemeType, String locale) {

		HashMap<String, String> aggIdLabels = getSectionIdLabels(aggregationThemeType, locale);

		// read the id layout orders first
		// HashMap<aggreg type, ArrayList<sectionId layoutOrder>>
		ArrayList<IdType> idOrders = _SectionsIdOrders.get(aggregationThemeType);
		// // create the returned list with the right size
		ArrayList<String> sectionLabels = new ArrayList<String>(idOrders.size());
		// initiate the list, to avoid a IndexOutOfBoundsException
		for (int i = 0; i < idOrders.size(); i++) {
			sectionLabels.add("");
		}

		for (IdType idOrder : idOrders) {
			String sectionId = idOrder._Key;
			int order = Integer.parseInt(idOrder._Value) - 1;
			// get the label
			String label = aggIdLabels.get(sectionId);
			sectionLabels.set(order, label);
		}
		return sectionLabels;
	}

	@Override
	public ArrayList<String> getSectionIds(String aggregationThemeType, String locale) {

		if (_SectionsIdOrders == null)
			getSectionIdLabels(aggregationThemeType, locale);
		// read the id layout orders first
		// HashMap<aggreg type, ArrayList<sectionId layoutOrder>>
		ArrayList<IdType> idOrders = _SectionsIdOrders.get(aggregationThemeType);
		// create the returned list with the right size
		ArrayList<String> sectionIds = new ArrayList<String>(idOrders.size());
		// initiate the list, to avoid a IndexOutOfBoundsException
		for (int i = 0; i < idOrders.size(); i++) {
			sectionIds.add("");
		}

		for (IdType idOrder : idOrders) {
			String sectionId = idOrder._Key;
			int order = Integer.parseInt(idOrder._Value) - 1;
			sectionIds.set(order, sectionId);
		}
		return sectionIds;
	}

	/**
	 * 
	 * @return a map of sectiontableids types, linked with sectionId read the section file configuration if not already
	 *         done
	 * @param sectionId
	 */
	@Override
	// HashMap<sectionTableid, ArrayList<IdType[]>>
	public HashMap<String, ArrayList<IdType[]>> getSectionTableIdTypes(String sectionId) {
		if (_SectionTableIdTypes == null || _SectionTableIdTypes.get(sectionId) == null)
			readSectionTablesInfo(sectionId);
		return _SectionTableIdTypes;
	}

	@Override
	// HashMap<locale, HashMap<sectionTableConceptId, sectionTableConceptLabel>>
	public HashMap<String, HashMap<String, String>> getSectionTableLabels(String sectionTableId, String locale) {
		// HashMap<locale, HashMap<sectionTableConceptId, label>>
		HashMap<String, HashMap<String, String>> sectionTableLabels = _SectionTableLabels.get(sectionTableId);
		return sectionTableLabels;
	}

	@Override
	public String getSectionContentLabels(String aggregationThemeType, String sectionType, String locale) {

		HashMap<String, String> aggIdLabels = getSectionIdLabels(aggregationThemeType, locale);
		// go through each id...

		for (String sectionId : aggIdLabels.keySet()) {
			if (sectionType.equals(sectionId)) {
				return aggIdLabels.get(sectionId);
			}
		}
		return null;
	}

	/**
	 * 
	 * @return a list of sectiontable ids linked with sectionId read the section file configuration if not already done
	 * @param sectionId
	 */
	@Override
	public ArrayList<String> getSectionTableIds(String sectionId) {

		// if configuration not totally built
		if (_SectionsTableIds == null || _SectionsTableIds.get(sectionId) == null) {
			// read the section tables configuration for this section
			readSectionTablesInfo(sectionId);
		}
		ArrayList<String> sectionTableIds = _SectionsTableIds.get(sectionId);

		return sectionTableIds;

	}

	/**
	 * 
	 * read the section table file configuration which is linked with a section id
	 * 
	 * @param sectionId
	 */
	private void readSectionTablesInfo(String sectionId) {

		String prefixFileName = CommonConstants.SECTION_FILENAME_PREFIX;
		String resourceFileSectionIdType = prefixFileName + sectionId + "-TablesIds.txt";
		String resourceFileSectionLabel = prefixFileName + sectionId + "-TablesLabels.txt";

		try {
			System.out.println("buildModel resourceFile=" + resourceFileSectionIdType);
			ArrayList<String> sectionTableIdLines = null;
			ArrayList<String> sectionsTableLabels = null;

			// read the file resources
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(resourceFileSectionIdType, classList);
			System.out.println("Resource url = " + url);
			if (url != null) {

				sectionTableIdLines = readFile(null, null, url, null);
				url = IOUtils.getResource(resourceFileSectionLabel, classList);
				sectionsTableLabels = readFile(null, null, url, null);
			} else {
				// local (NON JWS)
				String workspaceresource = getConfigurationManager().getConfigField("workspaceresource");

				resourceFileSectionIdType = workspaceresource + resourceFileSectionIdType;
				resourceFileSectionLabel = workspaceresource + resourceFileSectionLabel;

				sectionTableIdLines = readFile(resourceFileSectionIdType, null, null, null);
				sectionsTableLabels = readFile(resourceFileSectionLabel, null, null, null);
			}
			// build the dictionary field list
			if (sectionTableIdLines != null && !sectionTableIdLines.isEmpty() && sectionsTableLabels != null && !sectionsTableLabels.isEmpty()) {
				// allocate the list of fields
				ArrayList<String> sectionTableIds = new ArrayList<String>();

				String separator = CommonConstants.RESOURCE_FIELD_SEPARATOR;

				// parse all sectionTables ids

				// set used later for checking the ids and labels
				HashSet<String> fieldsRead = new HashSet<String>();
				boolean insideTable = false;
				String currentSectionTableId = null;
				int sectionTableCols = 0;
				int sectionTableRows = 0;
				// current sectiontable row count
				int currentSectionTableRows = 0;
				ArrayList<IdType[]> currentSectionTableList = null;

				int lineCount = 0;
				try { // parse file, read line by line
					for (String field : sectionTableIdLines) {
						lineCount++;
						// skip comments
						if (!field.equals("") && !field.startsWith("//")) {

							if (field.startsWith(CommonConstants.SECTIONTABLE_SEPARATOR)) {
								if (insideTable) {
									insideTable = completeTableInfo(currentSectionTableId, currentSectionTableList);

									// check lines & row number
									if (sectionTableRows != currentSectionTableRows)
										throw new ConfigException("readSectionTablesInfo: Fatal: Bad col/row number near line " + lineCount + " of " + resourceFileSectionIdType);
									currentSectionTableRows = 0;
								}
							} else if (field.startsWith(CommonConstants.SECTIONTABLE_PREFIX)) {
								// check if already in a table
								if (insideTable) {
									insideTable = completeTableInfo(currentSectionTableId, currentSectionTableList);
									currentSectionTableRows = 0;
									// check lines number
									if (sectionTableRows != currentSectionTableRows)
										throw new ConfigException("readSectionTablesInfo: Fatal: Bad col/row number near line " + lineCount + " of " + resourceFileSectionIdType);

								}
								currentSectionTableList = new ArrayList<IdType[]>();
								insideTable = true;
								String[] parts = field.split(separator);
								if (parts.length < 4)
									throw new ConfigException("readSectionTablesInfo: Fatal: Bad field number on line " + lineCount + " of " + resourceFileSectionIdType);

								currentSectionTableId = parts[1].trim();
//								currentSectionTableId = currentSectionTableId.replace(CommonConstants.SECTIONTABLE_PREFIX, "");
								try {
									String strSectionTableCols = parts[2].trim();
									sectionTableCols = Integer.parseInt(strSectionTableCols);

									String strSectionTableRows = parts[3].trim();
									sectionTableRows = Integer.parseInt(strSectionTableRows);
								} catch (NumberFormatException e) {

									throw new ConfigException("readSectionTablesInfo: Fatal (NumberFormatException): Bad col/row number near line " + lineCount + " of "
											+ resourceFileSectionIdType);
								}

								boolean b2Ok = currentSectionTableId != null && !currentSectionTableId.isEmpty();

								if (b2Ok) {// add the new sectiontableid to the
											// map
									sectionTableIds.add(currentSectionTableId);

								} else {
									System.out.println("sectionTableId " + field + " skipped from " + resourceFileSectionIdType);
								}
							} else {
								if (insideTable) {
									currentSectionTableRows++;
									//
									String[] columns = field.split(CommonConstants.SECTIONTABLE_COL_SEPARATOR);
									if (columns.length != sectionTableCols)
										throw new ConfigException("readSectionTablesInfo: Fatal: Bad col number near line " + lineCount + " of " + resourceFileSectionIdType);

									IdType[] idTypeArray = new IdType[sectionTableCols];
									int colCount = 0;
									// read conceptid and type for each column
									for (String colInfo : columns) {
										String[] id_Type = colInfo.split(separator);
										String sectionTableConceptId = id_Type[0].trim();
										fieldsRead.add(sectionTableConceptId);
										String conceptType = id_Type[1].trim();
										IdType idType = new IdType(sectionTableConceptId, conceptType);
										idTypeArray[colCount] = idType;

										colCount++;
									}
									currentSectionTableList.add(idTypeArray);
								}
							}
						} else {
							if (insideTable) {
								// no comments inside section table info
								insideTable = completeTableInfo(currentSectionTableId, currentSectionTableList);
								currentSectionTableRows = 0;
							}
						}
					}
					if (insideTable) {
						// check lines & row number
						if (sectionTableRows != currentSectionTableRows)
							throw new ConfigException("readSectionTablesInfo: Fatal: Bad col/row number near line " + lineCount + " of " + resourceFileSectionIdType);
						completeTableInfo(currentSectionTableId, currentSectionTableList);
					}
				} catch (Exception e) {
					if (e instanceof ConfigException)
						throw e;
					throw new ConfigException("Problem reading " + resourceFileSectionIdType + " file on line " + lineCount, e);
				}
				if (_SectionsTableIds == null)
					_SectionsTableIds = new HashMap<String, ArrayList<String>>();
				_SectionsTableIds.put(sectionId, sectionTableIds);

				System.out.println(sectionId + " sectionTable Ids list size =" + sectionTableIds.size());

				// build the label list
				// HashMap<locale, HashMap<sectionTableId, label>>
				HashMap<String, HashMap<String, String>> sectionTablesLabels = new HashMap<String, HashMap<String, String>>();

				// parse all sectiontables labels
				try {
					lineCount = 0;
					for (String field : sectionsTableLabels) {
						lineCount++;
						if (!field.equals("") && !field.startsWith("//")) { // skip
																			// comments
							String[] parts = field.split(separator);
							String sectionTableId = parts[0].trim();
							boolean b1Ok = sectionTableId != null && !sectionTableId.isEmpty();
							// String fieldLabel = parts[1].trim();
							String fieldLabel = parts[1]; //
							boolean b2Ok = fieldLabel != null && !fieldLabel.isEmpty();

							String fieldLocale = parts[2].trim();
							boolean b3Ok = fieldLocale != null && !fieldLocale.isEmpty();

							// insert label only if the field exist in our field
							// list
							if (b1Ok && b2Ok && b3Ok && fieldsRead.contains(sectionTableId)) {
								// check if locale already exists in our map
								HashMap<String, String> labels = null;
								if ((labels = sectionTablesLabels.get(fieldLocale)) == null) {
									labels = new HashMap<String, String>();
									sectionTablesLabels.put(fieldLocale, labels);
								}
								labels.put(sectionTableId, fieldLabel);
							} else {
								System.out.println("label " + sectionTableId + " skipped from " + resourceFileSectionLabel);
							}
						}
					}
				} catch (Exception e) {
					throw new ConfigException("Problem reading " + resourceFileSectionLabel + " file on line " + lineCount, e);
				}

				if (_SectionTableLabels == null)
					// HashMap<section typeId, HashMap<locale,
					// HashMap<sectionTableId, label>>>
					_SectionTableLabels = new HashMap<String, HashMap<String, HashMap<String, String>>>();
				_SectionTableLabels.put(sectionId, sectionTablesLabels);

			} else {
				if (sectionTableIdLines == null) {
					throw new ConfigException("Fatal: Could not read anything from file " + resourceFileSectionIdType);
				}
				throw new ConfigException("Fatal: Could not read anything from file " + resourceFileSectionLabel);
			}
		} catch (Exception e) {
			throw new ConfigException(StackTraceUtil.getCustomStackTrace(e));
		}

	}

	/**
	 * only used in SectionTablesInfo() for files parsing
	 * 
	 * @param currentSectionTableId
	 * @param currentSectionTableList
	 * @return always false for convenience purposes
	 */
	private boolean completeTableInfo(String currentSectionTableId, ArrayList<IdType[]> currentSectionTableList) {
		// HashMap<sectionTable id, ArrayList<IdType[]>>
		if (_SectionTableIdTypes == null)
			_SectionTableIdTypes = new HashMap<String, ArrayList<IdType[]>>();
		_SectionTableIdTypes.put(currentSectionTableId, currentSectionTableList);
		return false;
	}

	// /////////////////////////////////////////TECHNICAL
	// UTILITIES/////////////////////////////////////////////////////////////////////////////

	/**
	 * load the parameters from the resource global property file
	 * 
	 */
	@Override
	public Properties loadGlobalProperties(String projectPath) {

		String defaultProperties = CommonConstants.PROPERTY_FILE_NAME;
		Properties defaultProps = new Properties();

		try {
			System.out.println("loadGlobalProperties resourceFile=" + defaultProperties);

			// read the file resources
			// URL url = Config.class.getResource(resourceFileFieldsIdType);
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(defaultProperties, classList);
			InputStream is = null;
			System.out.println("Resource url = " + url);
			// FileInputStream in = null;
			if (url != null) {

				is = url.openStream();
				defaultProps.load(new InputStreamReader(is, "UTF8"));
			} else {
				// local (NON JWS)......-->>!!?? Does not go anymore that path!?
				// defaultProperties =
				// "H:\\devel\\workspace64Bits\\IPD\\src\\main\\resources\\"
				// + defaultProperties;
				// defaultProperties = Constants.WORKSPACE_RES_DIR;
				if (projectPath != null)
					defaultProperties = projectPath + defaultProperties;

				is = new FileInputStream(defaultProperties);
				defaultProps.load(new InputStreamReader(is, "UTF8"));
			}
//			defaultProps.load(is);
			is.close();

		} catch (Exception e) {
			throw new FatalException(StackTraceUtil.getCustomStackTrace(e));
		}

		return defaultProps;

	}
	

	public Properties loadGlobalProperties(String projectPath, Object caller) {

		String defaultProperties = CommonConstants.PROPERTY_FILE_NAME;
		Properties defaultProps = new Properties();

		try {
			System.out.println("loadGlobalProperties resourceFile=" + defaultProperties);

			// read the file resources
			// URL url = Config.class.getResource(resourceFileFieldsIdType);
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());
			classList.add(caller.getClass());

			URL url = IOUtils.getResource(defaultProperties, classList);
			InputStream is = null;
			System.out.println("Resource url = " + url);
			// FileInputStream in = null;
			if (url != null) {

				is = url.openStream();

			} else {
				// local (NON JWS)......-->>!!?? Does not go anymore that path!?
				// defaultProperties =
				// "H:\\devel\\workspace64Bits\\IPD\\src\\main\\resources\\"
				// + defaultProperties;
				// defaultProperties = Constants.WORKSPACE_RES_DIR;
				if (projectPath != null)
					defaultProperties = projectPath + defaultProperties;

				is = new FileInputStream(defaultProperties);
			}
			defaultProps.load(is);
			is.close();

		} catch (Exception e) {
			throw new FatalException(StackTraceUtil.getCustomStackTrace(e));
		}

		return defaultProps;

	}

	@Override
	/**
	 * load a resource whether it is on JWS or local dev
	 * linked with TN128 
	 */
	public ArrayList<String> loadResource(String resourceFileName) {
		ArrayList<String> resourceContents = null;

		try {
			// String resourceFileName = System.getProperty(resourceName);
			// System.out.println(getClass().getName()+"::loadResource: resourceFileName = "+resourceFileName);
			// if (resourceFileName != null) {

			// read the file resources
			ArrayList<Class> classList = new ArrayList<Class>();
			classList.add(this.getClass());

			URL url = IOUtils.getResource(resourceFileName, classList);
			System.out.println("Resource url = " + url);
			if (url != null) {

				resourceContents = readFile(null, null, url, null);

			} else {
				// throw new ConfigException("Bad resource name (not found)! :"
				// + resourceFileName);
				// local (NON JWS)
				String workspaceresource = getConfigurationManager().getConfigField("workspaceresource");
				String resourceNameFile = getConfigurationManager().getConfigField(resourceFileName);

				resourceFileName = workspaceresource + resourceNameFile;

				resourceContents = readFile(resourceFileName, null, null, null);
			}

			// } else {
			//
			// }

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resourceContents;
	}

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

	// ///////////////////////////////////////////////////////////////////////

	public boolean exportAllResources(String intputPathName, String outputPathName) throws IOException {
		// read the file resources

		// get all resource file names
		ArrayList<String> resourceFileNames = readResourceFile(intputPathName);

		if (resourceFileNames == null) {
			return false;
		}

		// go through all resource names
		for (String resourceFileName : resourceFileNames) {
			boolean ret = exportResource(resourceFileName, outputPathName + "/" + resourceFileName);
			if (!ret)
				return false;
		}

		return true;
	}

	/**
	 * Read a resource file and save it to outputPathName
	 * 
	 * @param inputResourceName
	 * @param outputPathName
	 * @return
	 * @throws IOException
	 */
	public boolean exportResource(String inputResourceName, String outputPathName) throws IOException {

		// read the file resources
		ArrayList<Class> classList = new ArrayList<Class>();
		classList.add(ResourceManagerImpl.class);

		try {
			URL url = IOUtils.getResource(inputResourceName, classList);
			System.out.println("Resource url = " + url);
			if (url == null) {
				return false;
			}

			System.out.println("exportResource:: exporting from  : " + inputResourceName + " to " + outputPathName);

			InputStream is = url.openStream();
			OutputStream out = new FileOutputStream(outputPathName);

			// do the copy
			byte[] buffer = new byte[4096];
			int len;
			while ((len = is.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
			is.close();
			out.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	/**
	 * 
	 * @param resourceName
	 * @return a list of lines representing the content of the resourceName
	 */
	public ArrayList<String> readResourceFile(String resourceName) {
		ArrayList<String> lines = null;

		// read the file resources
		ArrayList<Class> classList = new ArrayList<Class>();
		classList.add(ResourceManagerImpl.class);

		URL url = IOUtils.getResource(resourceName, classList);
		System.out.println("Resource url = " + url);
		if (url != null) {

			try {
				lines = readFile(null, null, url, null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return lines;
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
	public ConfigurationManagerBase getConfigurationManager() {
		return _ConfigurationManager;
	}

	@Override
	public void setConfigurationManager(ConfigurationManagerBase _ConfigurationManager) {
		this._ConfigurationManager = _ConfigurationManager;
	}

}
