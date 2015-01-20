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
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.form.FatalException;
import ch.msf.form.MyComboBoxModel;
import ch.msf.form.MyDateFieldEditor3;
import ch.msf.form.MyDecimalFieldEditor;
import ch.msf.form.MyNumericFieldEditor2;
import ch.msf.form.MyTimeFieldEditor;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.model.Encounter;
import ch.msf.model.actionrunner.ComponentEnabler;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;

public class EncounterKeyValueTableModel extends AbstractTableModel implements ComponentEnabler {

	// dictionary fields types

	public static final int COL_KEYS = 1;
	public static final int COL_VALUES = 1;

	// due to different types in the same column
	// used to get the correct renderer for each cell
	public int _ListIndexRenderer = 0;

	// String[] columnNames = { "Key", "Value" };
	String[] columnNames = { "", "" };
	// the model <conceptid, value>
	private ArrayList<DicoData> _KeyValues = null;

	private ArrayList<IdType> _IdTypes;

	// all the language labels
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

	public EncounterKeyValueTableModel(ArrayList<IdType> idTypes, HashMap<String, HashMap<String, String>> labels, HashMap<String, ArrayList<KeyValue>> comboboxValueMap) {

		setIdTypes(idTypes);
		setLabels(labels);
		setComboboxValueMap(comboboxValueMap);

		reBuildModel(null);
	}

	/**
	 * clear and rebuild the model
	 */
	public void reBuildModel(Encounter currentEncounter) {
		_KeyValues = new ArrayList<DicoData>();

		if (_IdTypes == null)
			return; // usual if types not known yet
		for (IdType idType : _IdTypes) {
			// set key and fieldType
			DicoData keyValue = new DicoData(idType._Key, idType._Value, currentEncounter);

			getKeyValues().add(keyValue);
		}

		// reinit the disable list
		_ConceptsDisabled.clear();

		// register our component for action rules
		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
		String className = getClass().getName();
		className = className.substring(className.lastIndexOf('.') + 1);
		_ConfigurationManager.getComponentCache().put(className, this);
	}

	private static final long serialVersionUID = 1L;

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return getKeyValues().size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		try {
			if (col == 0) {
				String fieldId = getKeyValues().get(row)._Key;
				// HashMap<String, String> labels =
				// ((EntryFormConfigurationManager)_ConfigurationManager).getQuestionLabels(Patient.class)
				// .get(_ConfigurationManager.getDefaultLanguage());
				String label = null;
				if (_Labels != null) {
					label = _Labels.get(_ConfigurationManager.getDefaultLanguage()).get(fieldId);
				} else
					System.out.println("label null");

				return (label != null) ? label : fieldId;
			}
			return getKeyValues().get(row)._Value;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * if used...c is only for first col
	 */
	public Class getColumnClass(int c) {
		// return getValueAt(0, c).getClass();
		// System.out.println("c:"+c +
		// "_ListIndexRenderer: "+_ListIndexRenderer+" "+getValueAt(_ListIndexRenderer,
		// c).getClass());
		// if (c == 1) {
		// DicoData dicoData = (DicoData) getValueAt(_ListIndexRenderer, c);
		// int intFieldType = _ModelTypes.get(dicoData._Key);
		// if (intFieldType == CommonIpdConstants.BOOLEAN_TYPE)
		// return Boolean.class; // just to show the combobox
		// }

		if (c == 0)
			return String.class;
		// never comes here normally...
		return getValueAt(_ListIndexRenderer, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		if (col == 0) {
			return false;
		}
		// disable all row included in _ConceptsDisabled
		for (int conceptRow : _ConceptsDisabled) {
			if (conceptRow == row) {
				return false;
			}
		}
		
		if (getKeyValues().get(row)._CellEditor == null)
			return false; // TN115 //TN114
		
		return true;
	}

	/*

	 */
	public void setValueAt(Object value, int row, int col) {
		if (row <= getKeyValues().size()) {
			getKeyValues().get(row)._Value = value;
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

		public DicoData(String key, String fieldType, Object entity) {
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

			else if (fieldType.startsWith("Combo-")) {
				intFieldType = CommonIpdConstants.COMBO_TYPE;
				String[] parts = fieldType.split("-");
				if (parts.length == 2) {
					_ComboName = parts[1];
					// System.out.println(getClass().getName()
					// + "DicoData: resourceToLoad=" + comboName);

				} else
					throw new FatalException(getClass().getName() + "DicoData: Combo with wrong format.");
			} else if (fieldType.equals("Title"))
				intFieldType = CommonIpdConstants.TITLE_TYPE;// TN96

			// save the id, type
			_ModelTypes.put(key, intFieldType);

			switch (intFieldType) {
			case CommonIpdConstants.NUMERIC_TYPE:
				_CellEditor = new MyNumericFieldEditor2(key);
				break;

			case CommonIpdConstants.DECIMAL_TYPE:
				_CellEditor = new MyDecimalFieldEditor(key, _Scale);
				break;

			case CommonIpdConstants.DATE_TYPE:
				Encounter currentEncounter = (Encounter) entity;
				Date creationDate = null;
				if (currentEncounter != null)
					creationDate = currentEncounter.getCreationDate();
				if (creationDate == null) // encounter not yet created
					creationDate = new Date();
				_CellEditor = new MyDateFieldEditor3(key, creationDate);
				break;

			case CommonIpdConstants.TIME_TYPE:
				_CellEditor = new MyTimeFieldEditor(key);
				break;

			case CommonIpdConstants.BOOLEAN_TYPE:
				// _CellEditor = new MyBooleanFieldEditor(); //
				JCheckBox checkBox = new JCheckBox();
				_CellEditor = new DefaultCellEditor(checkBox);
				break;

			case CommonIpdConstants.COMBO_TYPE:
				JComboBox comboBox = new JComboBox(new MyComboBoxModel());

				ArrayList<KeyValue> values = _ComboboxValueMap.get(_ComboName);

				for (KeyValue keyValue : values) {
					comboBox.addItem(keyValue._Value);
				}

				_CellEditor = new DefaultCellEditor(comboBox);
				break;

			case CommonIpdConstants.STRING_TYPE:
				_CellEditor = new DefaultCellEditor(new JTextField());
				break;

			case CommonIpdConstants.TITLE_TYPE:// TN96
				_CellEditor = null;
				break;

			default:
				System.out.println("No type defined for value " + fieldType);
				_CellEditor = new DefaultCellEditor(new JTextField());
				break;
			}
		}
	}

	public ArrayList<String> getStringResults() {
		ArrayList<String> results = new ArrayList<String>();

		for (DicoData keyValue : getKeyValues()) {
			String line = keyValue._Key + "|" + keyValue._Value;
			results.add(line);
		}

		return results;
	}

	public HashMap<String, String> getResults() {
		HashMap<String, String> results = new HashMap<String, String>();

		for (DicoData dicoData : getKeyValues()) {
			String strValue = null;

			if (dicoData._Value != null) {

				int intFieldType = _ModelTypes.get(dicoData._Key);

				switch (intFieldType) {
				case CommonIpdConstants.NUMERIC_TYPE:
				case CommonIpdConstants.DECIMAL_TYPE:
					strValue = dicoData._Value.toString();
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
					strValue = null; // TN96
					break;

				default:
					strValue = dicoData._Value.toString();
					break;
				}
			}
			results.put(dicoData._Key, strValue);
		}
		return results;
	}

	/**
	 * @return the getList()
	 */
	public ArrayList<DicoData> getKeyValues() {
		return _KeyValues;
	}

	/*
	 * 
	 */
	public void setIdValues(HashMap<String, String> newKeyValues) {
		if (newKeyValues != null && newKeyValues.size() > 0)
			for (DicoData dicoData : getKeyValues()) {
				String strNewVal = newKeyValues.get(dicoData._Key);
				int intFieldType = _ModelTypes.get(dicoData._Key);

				if (strNewVal != null)
					switch (intFieldType) {
					case CommonIpdConstants.NUMERIC_TYPE:
						dicoData._Value = new Integer(strNewVal);
						break;

					case CommonIpdConstants.DECIMAL_TYPE:
						dicoData._Value = new BigDecimal(strNewVal);
						break;

					case CommonIpdConstants.DATE_TYPE:
						Date date;
						try {
							date = _Sdf.parse(strNewVal);
						} catch (ParseException e) {
							e.printStackTrace();
							date = CommonConstants.INVALID_DATE;
						}
						dicoData._Value = date;
						break;

					case CommonIpdConstants.TIME_TYPE:

						try {
							_Sdft.parse(strNewVal);
						} catch (ParseException e) {
							// cannot append normally
							e.printStackTrace();
						}
						dicoData._Value = strNewVal;
						break;

					case CommonIpdConstants.BOOLEAN_TYPE:
						dicoData._Value = new Boolean(strNewVal);
						break;

					case CommonIpdConstants.COMBO_TYPE:

						boolean notFound = true;
						// get the id of the combobox
						ArrayList<KeyValue> values = _ComboboxValueMap.get(dicoData._ComboName);
						for (KeyValue keyValue : values) {
							if (keyValue._Key.equals(strNewVal)) {
								dicoData._Value = keyValue._Value;
								notFound = false;
								break;
							}
						}
						if (notFound)
							System.out.println(getClass().getName() + "::setIdValues: ERROR: combo id of " + dicoData._ComboName + " not found!!!!");

						break;

					case CommonIpdConstants.STRING_TYPE:
						dicoData._Value = strNewVal;
						break;

					case CommonIpdConstants.TITLE_TYPE:
						dicoData._Value = null; // TN96
						break;

					default:
						dicoData._Value = strNewVal;
						break;
					}
			}
	}

	public ArrayList<IdType> getIdTypes() {
		return _IdTypes;
	}

	public void setIdTypes(ArrayList<IdType> _IdTypes) {
		this._IdTypes = _IdTypes;
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

		for (DicoData dicoData : getKeyValues()) {

			if (conceptDependency.equals(dicoData._Key)) {
				return rowNumber;
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
		Integer row = getRowCellDependency(conceptId);
		if (allCriteriaOk) {
			_ConceptsDisabled.remove(row);
		} else {
			_ConceptsDisabled.add(row);
			if (getValueAt(row, COL_VALUES) != null)
				// put line to blank
				setValueAt(null, row, COL_VALUES);
		}

	}

	/**
	 * disableConcept the concept when allCriteriaOk
	 * 
	 * @param conceptId
	 * @param allCriteriaOk
	 */
	@Override
	public void disableConcept(String conceptId, boolean allCriteriaOk) {
		Integer row = getRowCellDependency(conceptId);
		if (!allCriteriaOk) {
			_ConceptsDisabled.remove(row);
		} else {
			_ConceptsDisabled.add(row);
			// put line to blank
			setValueAt(null, row, COL_VALUES);
		}

	}

	/**
	 * @return the _ModelTypes
	 */
	public HashMap<String, Integer> getModelTypes() {
		return _ModelTypes;
	}

}
