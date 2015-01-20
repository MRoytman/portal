package ch.msf.form.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import ch.msf.CommonConstants;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

public class PatientListTableModel extends DefaultTableModel {

	public final static int FIELDINDEX_INDEX = 0;
	public final static int FIELDINDEX_ID = FIELDINDEX_INDEX + 1;
	public final static int FIELDINDEX_SURNAME = FIELDINDEX_ID + 1;
	public final static int FIELDINDEX_NAME = FIELDINDEX_SURNAME + 1;
	public final static int FIELDINDEX_SEX = FIELDINDEX_NAME + 1;
	public final static int FIELDINDEX_BIRTHDATE = FIELDINDEX_SEX + 1;
	public final static int TOTAL_FIELDS = FIELDINDEX_BIRTHDATE + 1;

	// when a new patient is created or data modified from existing row
	private Integer _LineNumberEditable;

	// String[] columnNames = { "index", "Id", "Surname", "Name", "Sex",
	// "BirthDate" };
	private String[] _ColumnNames = null;

	// the model
	private ArrayList<PatientModelData> _PatientModelDataValues = null;

	private TableCellEditor _SexTableCellEditor = null;

	protected ConfigurationManagerImpl _ConfigurationManager;

	SimpleDateFormat _Sdf = new SimpleDateFormat(CommonConstants.SIMPLE_DATE_FORMAT);

	private static final PatientModelData _DummyPatientModelData = new PatientListTableModel().new PatientModelData("index", "id", "surname", "name", "sex", new Date());

	public PatientListTableModel() {
	}

	public PatientListTableModel(ArrayList<PatientModelData> patientModelData) {
		_ConfigurationManager = ServiceHelper.getConfigurationManagerService();
	}

	private static final long serialVersionUID = 1L;

	public int getColumnCount() {
		return TOTAL_FIELDS;
	}

	public int getRowCount() {
		return getModelValues().size();
	}

	public String getColumnName(int col) {

		if (_ColumnNames == null)
			return null;
		return _ColumnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return getModelValues().get(row).getObject(col);
	}

	public Class getColumnClass(int c) {
		if (getValueAt(0, c) == null)
			return _DummyPatientModelData.getObject(c).getClass();
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {

		if (col == FIELDINDEX_INDEX)
			return false;

		if (getLineNumberEditable() == null)
			// all table is editable
			return true;

		if (row == getLineNumberEditable()) {
			return true;
		}
		return false;
	}

	/*

	 */
	public void setValueAt(Object value, int row, int col) {
		// System.out.println("setValueAt row" + row);
		// System.out.println("setValueAt getRowCount()" + getRowCount());

		if (row < getModelValues().size()) {
			getModelValues().get(row).setObject(col, value);
			fireTableCellUpdated(row, col);
		}
	}

	public class PatientModelData {

		public PatientModelData() {

		}

		public PatientModelData(String index, String id, String surname, String name, String sex, Date birthDate) {
			setIndex(index);
			setId(id);
			setSurname(surname);
			setName(name);
			setSex(sex);
			setBirthDate(birthDate);
		}

		Object[] _TheModel = new Object[TOTAL_FIELDS];
		private Long _PatientContextId; // technical PatientContext id

		private String _OldId; // used when business id change

		public Object getObject(int index) {
			return _TheModel[index];
		}

		public void setObject(int index, Object obj) {
			_TheModel[index] = obj;
		}

		public void setIndex(String id) {
			setObject(FIELDINDEX_INDEX, id);
		}

		public void setId(String id) {
			setObject(FIELDINDEX_ID, id);
		}

		public void setSurname(String surname) {
			setObject(FIELDINDEX_SURNAME, surname);
		}

		public void setName(String name) {
			setObject(FIELDINDEX_NAME, name);
		}

		public void setSex(String sex) {
			setObject(FIELDINDEX_SEX, sex);
		}

		public void setBirthDate(Date birthDate) {
			setObject(FIELDINDEX_BIRTHDATE, birthDate);
		}

		public String getIndex() {
			return (String) getObject(FIELDINDEX_INDEX);
		}

		public String getId() {
			return (String) getObject(FIELDINDEX_ID);
		}

		public String getSurname() {
			return (String) getObject(FIELDINDEX_SURNAME);
		}

		public String getName() {
			return (String) getObject(FIELDINDEX_NAME);
		}

		public String getSex() {
			return (String) getObject(FIELDINDEX_SEX);
		}

		public Date getBirthDate() {
			return (Date) getObject(FIELDINDEX_BIRTHDATE);
		}

		// used for caching
		public Long getPatientContextId() {
			return _PatientContextId;
		}

		public void setPatientContextId(Long _PatientContextId) {
			this._PatientContextId = _PatientContextId;
		}

		public String getOldId() {
			return _OldId;
		}

		public void setOldId(String _OldId) {
			this._OldId = _OldId;
		}

	}

	/**
	 * @return the model
	 */
	public ArrayList<PatientModelData> getModelValues() {
		if (_PatientModelDataValues == null)
			_PatientModelDataValues = new ArrayList<PatientListTableModel.PatientModelData>();
		return _PatientModelDataValues;
	}

	/**
	 * set the model
	 * 
	 * @param modelValues
	 */
	public void setModelValues(ArrayList<PatientModelData> modelValues, String[] tableTitles) {
		_PatientModelDataValues = modelValues;
		_ColumnNames = tableTitles;
	}

	public void addModelValue(PatientModelData patientModelData) {
		getModelValues().add(patientModelData);
	}

	public void removeModelValue(PatientModelData patientModelData) {
		getModelValues().remove(patientModelData);
	}

	// get the line number being edited
	public Integer getLineNumberEditable() {
		return _LineNumberEditable;
	}

	/**
	 * set the row number being edited
	 * 
	 * @param numLine
	 */
	public void setLineNumberEditable(Integer numLine) {
		this._LineNumberEditable = numLine;
	}

	/**
	 * multi-lingual editors
	 * 
	 * @return
	 */
	public TableCellEditor getSexEditor() {
		return _SexTableCellEditor;
	}

	/**
	 * 
	 * @param comboBox
	 *            : a combobox with new values
	 */
	public void setSexEditor(JComboBox comboBox) {
		// // manage combo value in list
		// String comboSex = "sex";
		// JComboBox comboBox = ComboEntityList.getComboboxModelValue(comboSex);
		_SexTableCellEditor = new DefaultCellEditor(comboBox);
	}
}
