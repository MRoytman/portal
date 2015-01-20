package ch.msf.form.config;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.error.FatalException;
import ch.msf.form.MyDecimalFieldEditor;
import ch.msf.form.MyNumericFieldEditor2;
import ch.msf.form.MyTimeFieldEditor;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.model.actionrunner.ComponentCellUpdator;
import ch.msf.model.actionrunner.ComponentEnabler;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;

public class SectionTableKeyValueTableModel extends AbstractTableModel implements ComponentCellUpdator, ComponentEnabler {

	// due to different types in the same column
	// used to get the correct renderer for each cell
	public int _ListIndexRenderer = 0;

	// String[] columnNames = { "", "" };
	public String[] _ColumnNames = null;
	// the model <conceptid, value>
	private ArrayList<DicoData[]> _KeyValues = null;

	// holds the structure of the section table
	// which is a list
	// of concept id arrays
	private ArrayList<IdType[]> _IdTypes;

	// all the language labels
	// HashMap<locale, HashMap<id, label>>
	private HashMap<String, HashMap<String, String>> _Labels;

	// the concepts types
	private HashMap<String, Integer> _ModelTypes = new HashMap<String, Integer>();

	// a map of <"combo name", combo values lists of localized<id, label>>
	private HashMap<String, ArrayList<KeyValue>> _ComboboxValueMap = new HashMap<String, ArrayList<KeyValue>>();

	protected EntryFormConfigurationManagerImpl _ConfigurationManager;

	SimpleDateFormat _Sdf = new SimpleDateFormat(CommonConstants.SIMPLE_DATE_FORMAT);
	SimpleDateFormat _Sdft = new SimpleDateFormat(CommonConstants.SIMPLE_TIME_FORMAT);

	// black list of concepts that should not be displayed (disabled)
	public HashSet<Integer> _ConceptsDisabled = new HashSet<Integer>();
	
	// used to retreive the jtable component when action rules
	private String _SectionTableId;

	public SectionTableKeyValueTableModel(String sectionTableId, ArrayList<IdType[]> idTypes, HashMap<String, HashMap<String, String>> labels, HashMap<String, ArrayList<KeyValue>> comboboxValueMap) {

		_SectionTableId = sectionTableId;
		setIdTypes(idTypes);
		setLabels(labels);
		setComboboxValueMap(comboboxValueMap);

		reBuildModel();
	}

	/**
	 * clear and rebuild the model
	 */
	public void reBuildModel(/* SectionTable currentSectionTable */) {
		_KeyValues = new ArrayList<DicoData[]>();

		if (_IdTypes == null /* || currentSectionTable == null */)
			return; // usual if types not known yet

		for (IdType[] idType : _IdTypes) {
			// int colNumber = currentSectionTable.getColumnNumber();
			int colNumber = idType.length;
			// assert colNumber == colNumber2;

			DicoData[] dicoDataArray = new DicoData[colNumber];
			for (int colCount = 0; colCount < colNumber; colCount++) {
				// set key and fieldType
				DicoData conceptIdType = new DicoData(idType[colCount]._Key, idType[colCount]._Value);
				dicoDataArray[colCount] = conceptIdType;
			}
			getKeyValues().add(dicoDataArray);
		}

		// reinit the disable list
		_ConceptsDisabled.clear();

		// register our component for action rules
		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
		String className = getClass().getName();
		className = className.substring(className.lastIndexOf('.') + 1);
		// record the component in view to be notified by action rules
		_ConfigurationManager.getComponentUpdatorCache().put(getSectionTableId(), this);
	}

	private static final long serialVersionUID = 1L;

	public int getColumnCount() {
//		return _ColumnNames.length;
//		return getKeyValues().get(0).length;
		return getIdTypes().get(0).length;
	}

	public int getRowCount() {
//		return getKeyValues().size();
		return getIdTypes().size();
	}

	public String getColumnName(int col) {
		return _ColumnNames[col];
	}

	public Object getValueAt(int row, int col) {
		try {
			// if (col == 0) {
			// String fieldId = getKeyValues().get(row)[col]._Key;
			// // HashMap<String, String> labels =
			// //
			// ((EntryFormConfigurationManager)_ConfigurationManager).getQuestionLabels(Patient.class)
			// // .get(_ConfigurationManager.getDefaultLanguage());
			// String label = null;
			// if (_Labels != null) {
			// label =
			// _Labels.get(_ConfigurationManager.getDefaultLanguage()).get(fieldId);
			// } else
			// System.out.println("label null");
			//
			// return (label != null) ? label : fieldId;
			// }

//			if (getKeyValues() == null || getKeyValues().get(row)[col]._Value == null) {
//				String conceptId = getKeyValues().get(row)[col]._Key;
//				int conceptType = _ModelTypes.get(conceptId);
//				switch (conceptType) {
//				case CommonIpdConstants.NUMERIC_TYPE:
//				case CommonIpdConstants.DECIMAL_TYPE:
//					return 0;
//				default:
//					return "";
//				}
//			}
//			if (col >= 4){
//				System.out.println("col == 4 !!");
//				return "";
//			}
			return getKeyValues().get(row)[col]._Value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * if used...c is only for first col
	 */
	public Class getColumnClass(int c) {

		return getValueAt(_ListIndexRenderer, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {

		// disable all row included in _ConceptsDisabled
		for (int conceptRow : _ConceptsDisabled) {
			if (conceptRow == row) {
				return false;
			}
		}
		
//		if (col >= 4){
//			System.out.println("col == 4 !!");
//			return false;
//		}

		if (getKeyValues().get(row)[col]._CellEditor == null)
			return false; // CommonIpdConstants.TITLE_TYPE

		String conceptId = getKeyValues().get(row)[col]._Key;
		int intFieldType = _ModelTypes.get(conceptId);

		switch (intFieldType) {

		case CommonIpdConstants.CALCULATED_TYPE:
			return false;
		}

		return true;
	}

	/*

	 */
	public void setValueAt(Object value, int row, int col) {
		if (row <= getKeyValues().size()) {
			getKeyValues().get(row)[col]._Value = value;
			fireTableCellUpdated(row, col);
		} else {
			System.out.println(getClass().getName() + "::setValueAt ?! trying to update row " + row + " and current row number is " + getKeyValues().size());
		}
	}

	// Numeric --> Numeric value, including integer or float (e.g., creatinine,
	// weight)
	// Coded --> Value determined by term dictionary lookup (i.e., term
	// identifier)
	// Text --> Free text
	// N/A --> Not associated with a datatype (e.g., term answers, sets)
	// Document --> Pointer to a binary or text-based document (e.g., clinical
	// document, RTF, XML, EKG, image, etc.) stored in complex_obs table
	// Date --> Absolute date
	// Time --> Absolute time of day
	// Datetime --> Absolute date and time
	// Boolean --> Boolean value (yes/no, true/false)
	// Rule --> Value derived from other data
	// Structured --> Numeric Complex numeric values possible (ie, <5, 1-10,
	// etc.)
	// Complex --> Complex value. Analogous to HL7 Embedded Datatype
	// Title : new personal type, does not exist in openmrs
	public class DicoData {

		public String _Key;
		public Object _Value;
		// field uniquely used for combobox
		// to make the exchange between the label and the combo id value
		public String _ComboName;

		// precision for decimal field
		public int _Scale;
		public DefaultCellEditor _CellEditor;

		public DicoData(String key, String fieldType/* , Object entity */) {
			_Key = key;
			// String comboName = null;
			int intFieldType = 0;
			fieldType = fieldType.trim();
			if (fieldType.equals("N/A") || fieldType.equals("Text") || fieldType.equals("Coded"))
				intFieldType = CommonIpdConstants.STRING_TYPE;

			else if (fieldType.equals("Date") || fieldType.equals("Datetime"))
				intFieldType = CommonIpdConstants.DATE_TYPE;

			else if (fieldType.equals("Time") || fieldType.equals("Time"))
				intFieldType = CommonIpdConstants.TIME_TYPE;

			else if (fieldType.equals("Boolean"))
				intFieldType = CommonIpdConstants.BOOLEAN_TYPE;

			else if (fieldType.equals("Numeric"))
				intFieldType = CommonIpdConstants.NUMERIC_TYPE;

			else if (fieldType.equals("Calculated"))
				intFieldType = CommonIpdConstants.CALCULATED_TYPE;

			else if (fieldType.startsWith("Decimal")) { // ex: Decimal-3
				intFieldType = CommonIpdConstants.DECIMAL_TYPE;
				String[] parts = fieldType.split("-");
				boolean badFormat = false;
				if (parts.length == 2) {
					try {
						_Scale = Integer.parseInt(parts[1]);
					} catch (NumberFormatException e) {
						badFormat = true;
					}
				} else
					badFormat = true;
				// else
				// _Scale = "0";
				if (badFormat) // do not allow without scale, use numeric
								// instead
					throw new FatalException(getClass().getName() + "DicoData: Decimal with wrong format.");
			}

			// else if (fieldType.startsWith("Combo-")) {
			// intFieldType = CommonIpdConstants.COMBO_TYPE;
			// String[] parts = fieldType.split("-");
			// if (parts.length == 2) {
			// _ComboName = parts[1];
			// // System.out.println(getClass().getName()
			// // + "DicoData: resourceToLoad=" + comboName);
			//
			// } else
			// throw new FatalException(getClass().getName() +
			// "DicoData: Combo with wrong format.");
			// }
			else if (fieldType.equals("Title"))
				intFieldType = CommonIpdConstants.TITLE_TYPE;
			else if (fieldType.equals("Title2"))
				intFieldType = CommonIpdConstants.TITLE_TYPE;
			else if (fieldType.equals("Label1"))
				intFieldType = CommonIpdConstants.LABEL_TYPE;
			_CellEditor = null;

			// save the id, type
			_ModelTypes.put(key, intFieldType);

			switch (intFieldType) {
			case CommonIpdConstants.NUMERIC_TYPE:
			case CommonIpdConstants.CALCULATED_TYPE:
				_CellEditor = new MyNumericFieldEditor2(key);
				break;
			case CommonIpdConstants.DECIMAL_TYPE:
				_CellEditor = new MyDecimalFieldEditor(key, _Scale);
				break;

			// case CommonIpdConstants.DATE_TYPE:
			// Section currentEncounter = (Section) entity;
			// Date creationDate = null;
			// if (currentEncounter != null)
			// creationDate = currentEncounter.getCreationDate();
			// if (creationDate == null) // encounter not yet created
			// creationDate = new Date();
			// _CellEditor = new MyDateFieldEditor3(key, creationDate);
			// break;

			case CommonIpdConstants.TIME_TYPE:
				_CellEditor = new MyTimeFieldEditor(key);
				break;

			case CommonIpdConstants.BOOLEAN_TYPE:
				// _CellEditor = new MyBooleanFieldEditor(); //
				JCheckBox checkBox = new JCheckBox();
				_CellEditor = new DefaultCellEditor(checkBox);
				break;

			// case CommonIpdConstants.COMBO_TYPE:
			// JComboBox comboBox = new JComboBox(new MyComboBoxModel());
			//
			// ArrayList<KeyValue> values = _ComboboxValueMap.get(_ComboName);
			//
			// for (KeyValue keyValue : values) {
			// comboBox.addItem(keyValue._Value);
			// }
			//
			// _CellEditor = new DefaultCellEditor(comboBox);
			// break;

			case CommonIpdConstants.STRING_TYPE:
				_CellEditor = new DefaultCellEditor(new JTextField());
				break;

			case CommonIpdConstants.TITLE_TYPE:// TN96
			case CommonIpdConstants.LABEL_TYPE:// TN96
				_CellEditor = null;
				break;

			default:
				System.out.println("No type defined for value " + fieldType);
				_CellEditor = new DefaultCellEditor(new JTextField());
				break;
			}
		}
	}

	/**
	 * 
	 * @return an hashtable<conceptId, conceptValue>
	 */

	public HashMap<String, String> getResults() {
		HashMap<String, String> results = new HashMap<String, String>();

		int rowNumber = 0;
		for (DicoData[] dicoDataArray : getKeyValues()) {

			int colNumber = dicoDataArray.length;
			for (int colCount = 0; colCount < colNumber; colCount++) {
				// set key and fieldType
				DicoData dicoData = dicoDataArray[colCount];

				String strValue = null;

				if (dicoData._Value != null) {

					int intFieldType = _ModelTypes.get(dicoData._Key);

					switch (intFieldType) {
					case CommonIpdConstants.NUMERIC_TYPE:
					case CommonIpdConstants.DECIMAL_TYPE:
						strValue = dicoData._Value.toString();
						break;
						
					case CommonIpdConstants.CALCULATED_TYPE:
						strValue = null;
						break;


					case CommonIpdConstants.DATE_TYPE:
						strValue = _Sdf.format(dicoData._Value);
						break;

					case CommonIpdConstants.TIME_TYPE:
						strValue = (String) dicoData._Value;
						break;

					case CommonIpdConstants.BOOLEAN_TYPE:
						strValue = dicoData._Value.toString();
						break;

					case CommonIpdConstants.COMBO_TYPE:
						// strValue = dicoData._Value.toString();
						boolean notFound = true;
						// get the id of the combobox
						ArrayList<KeyValue> values = _ComboboxValueMap.get(dicoData._ComboName);
						for (KeyValue keyValue : values) {
							if (keyValue._Value.equals(dicoData._Value.toString())) {
								strValue = keyValue._Key;
								notFound = false;
								break;
							}
						}
						if (notFound)
							System.out.println(getClass().getName() + "::getResults:ERROR: combo id of " + dicoData._ComboName + " not found!!!!");

						break;

					case CommonIpdConstants.STRING_TYPE:
						strValue = dicoData._Value.toString();
						break;

					case CommonIpdConstants.TITLE_TYPE:
						strValue = null; // 
						break;

					default:
						strValue = dicoData._Value.toString();
						break;
					}
					if (strValue != null)
					results.put(dicoData._Key, strValue);
				}

			}
			rowNumber++;
		}
		return results;
	}

	/**
	 * @return the getList()
	 */
	public ArrayList<DicoData[]> getKeyValues() {
		return _KeyValues;
	}

	/*
	 * fill the existing structure with new values
	 */
	public void setIdValues(HashMap<String, String> newKeyValues) {
		if (newKeyValues != null && newKeyValues.size() > 0) {
			int lineCount = 0;
			for (DicoData[] dicoDataArray : getKeyValues()) {
				lineCount++;
				int colNumber = dicoDataArray.length;
				for (int colCount = 0; colCount < colNumber; colCount++) {
					// set key and fieldType
					DicoData dicoData = dicoDataArray[colCount];
					int intFieldType = _ModelTypes.get(dicoData._Key);
					String conceptId = dicoData._Key;
					String conceptValue = newKeyValues.get(conceptId);

					if (conceptValue != null) {

						switch (intFieldType) {
						case CommonIpdConstants.NUMERIC_TYPE:
						case CommonIpdConstants.CALCULATED_TYPE:
							dicoData._Value = new Integer(conceptValue);
							break;

						case CommonIpdConstants.DECIMAL_TYPE:
							dicoData._Value = new BigDecimal(conceptValue);
							break;

						case CommonIpdConstants.DATE_TYPE:
							Date date;
							try {
								date = _Sdf.parse(conceptValue);
							} catch (ParseException e) {
								e.printStackTrace();
								date = CommonConstants.INVALID_DATE;
							}
							dicoData._Value = date;
							break;

						case CommonIpdConstants.TIME_TYPE:

							try {
								_Sdft.parse(conceptValue);
							} catch (ParseException e) {
								// cannot append normally
								e.printStackTrace();
							}
							dicoData._Value = conceptValue;
							break;

						case CommonIpdConstants.BOOLEAN_TYPE:
							dicoData._Value = new Boolean(conceptValue);
							break;

						case CommonIpdConstants.COMBO_TYPE:

							boolean notFound = true;
							// get the id of the combobox
							ArrayList<KeyValue> values = _ComboboxValueMap.get(dicoData._ComboName);
							for (KeyValue keyValue : values) {
								if (keyValue._Key.equals(conceptValue)) {
									dicoData._Value = keyValue._Value;
									notFound = false;
									break;
								}
							}
							if (notFound)
								System.out.println(getClass().getName() + "::setIdValues: ERROR: combo id of " + dicoData._ComboName + " not found!!!!");

							break;

						case CommonIpdConstants.STRING_TYPE:
							dicoData._Value = conceptValue;
							break;

						case CommonIpdConstants.TITLE_TYPE:
							String label = conceptValue; 
							dicoData._Value = label; //
							break;

						default:
							dicoData._Value = conceptValue;
							break;
						}
					}
				}
			}
		}
//		if (labels != null){
//			for(String conceptId: labels.keySet()){
//				
//			}
//		}
	}

	public void setIdTypes(ArrayList<IdType[]> _IdTypes) {
		this._IdTypes = _IdTypes;
	}

	public ArrayList<IdType[]>  getIdTypes() {
		return _IdTypes;
	}

	public HashMap<String, HashMap<String, String>> getLabels() {
		return _Labels;
	}

	public void setLabels(HashMap<String, HashMap<String, String>> _Labels) {
		this._Labels = _Labels;
	}

	public HashMap<String, ArrayList<KeyValue>> getComboboxValueMap() {
		return _ComboboxValueMap;
	}

	public void setComboboxValueMap(HashMap<String, ArrayList<KeyValue>> _ComboboxValueMap) {
		this._ComboboxValueMap = _ComboboxValueMap;
	}

	/**
	 * 
	 * @param conceptDependency
	 * @return the row that correspond to conceptDependency
	 */
	private Integer getRowCellDependency(String conceptDependency) {
		Integer rowNumber = 0;

		for (DicoData[] dicoDataArray : getKeyValues()) {

			int colNumber = dicoDataArray.length;
			for (int colCount = 0; colCount < colNumber; colCount++) {
				// set key and fieldType
				DicoData dicoData = dicoDataArray[colCount];

				if (conceptDependency.equals(dicoData._Key)) {
					return rowNumber;
				}
			}
			rowNumber++;
		}
		return -1;
	}

	/**
	 * enableConcept the concept when allCriteriaOk
	 * 
	 * @param conceptId
	 * @param allCriteriaOk
	 */
	@Override
	public void enableConcept(String conceptId, boolean allCriteriaOk) {
		// Integer row = getRowCellDependency(conceptId);
		// if (allCriteriaOk) {
		// _ConceptsDisabled.remove(row);
		// } else {
		// _ConceptsDisabled.add(row);
		// if (getValueAt(row, COL_VALUES) != null)
		// // put line to blank
		// setValueAt(null, row, COL_VALUES);
		// }

	}

	/**
	 * disableConcept the concept when allCriteriaOk
	 * 
	 * @param conceptId
	 * @param allCriteriaOk
	 */
	@Override
	public void disableConcept(String conceptId, boolean allCriteriaOk) {
		// Integer row = getRowCellDependency(conceptId);
		// if (!allCriteriaOk) {
		// _ConceptsDisabled.remove(row);
		// } else {
		// _ConceptsDisabled.add(row);
		// // put line to blank
		// setValueAt(null, row, COL_VALUES);
		// }

	}

	/**
	 * @return the _ModelTypes
	 */
	public HashMap<String, Integer> getModelTypes() {
		return _ModelTypes;
	}
	

	public String getSectionTableId() {
		return _SectionTableId;
	}

	@Override
	/**
	 * change the cell identified by conceptId with newValue
	 * @param conceptId
	 * @param newValue
	 */
	public void updateCell(String conceptId, Object newValue) {
		int row = 0;
		for (DicoData[] dicoDataArray : getKeyValues()) {

			int colNumber = dicoDataArray.length;
			for (int colCount = 0; colCount < colNumber; colCount++) {
				// set key and fieldType
				DicoData dicoData = dicoDataArray[colCount];

				if (conceptId.equals(dicoData._Key)) {
					setValueAt(newValue, row, colCount);
					return;
				}
			}
			row++;
		}
		
	}


}
