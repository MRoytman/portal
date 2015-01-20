package ch.msf.form.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

public class EncounterListTableModel extends DefaultTableModel {

	public final static int FIELDINDEX_TYPE = 0;
	public final static int FIELDINDEX_DATE = FIELDINDEX_TYPE + 1;
	public final static int TOTAL_FIELDS = FIELDINDEX_DATE + 1;

	// due to different types in the same column
	// used to get the correct renderer for each cell
	// public int _ListIndexRenderer = 0;

	// when a new encounter is created or data modified from existing row
	private Integer _LineNumberEditable;

	// String[] columnNames = { "Type", "Date" };
	String[] _ColumnNames = null;

	// the model
	private ArrayList<EncounterModelData> _EncounterModelDataValues = null;

	protected ConfigurationManagerImpl _ConfigurationManager;

	private SimpleDateFormat _Sdf = new SimpleDateFormat(CommonConstants.SIMPLE_DATE_FORMAT);

	private static final EncounterModelData _DummyEncounterModelData = new EncounterListTableModel().new EncounterModelData(new Date(), /*
																																		 * "Place"
																																		 * ,
																																		 */"Type");

	public EncounterListTableModel() {

	}

	public EncounterListTableModel(ArrayList<EncounterModelData> encounterModelData) {
		_ConfigurationManager = ServiceHelper.getConfigurationManagerService();

		if (encounterModelData != null)
			buildModel(encounterModelData);
	}

	private void buildModel(ArrayList<EncounterModelData> patientModelData) {
		setModelValues(patientModelData, null);

	}

	private static final long serialVersionUID = 1L;

	public int getColumnCount() {
		// return columnNames.length;
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
			return _DummyEncounterModelData.getObject(c).getClass();
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {

		// return false if col is encounter type
		if (col == FIELDINDEX_TYPE) {
			return false;
		}

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

		boolean businessRulesOk = getModelValues().get(row).setObject(col, value);
		if (!businessRulesOk) {
			String errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED);
			errMess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_FORMAT_NOT_COMPATIBLE) + ", " + errMess;
			JOptionPane.showMessageDialog(null, errMess, "Error", JOptionPane.OK_OPTION, null);
		} else
			fireTableCellUpdated(row, col);
	}

	public class EncounterModelData {

		public EncounterModelData() {
		}

		public EncounterModelData(Date date/* , String place */, String type) {

			setDate(date);
			// setPlace(place);
			setTypeLabel(type);
		}

		// Object[] _TheModel = new Object[columnNames.length];
		Object[] _TheModel = new Object[TOTAL_FIELDS];

		private Long _EncounterId; // technical Encounter id

		private String _EncounterType;

		public Object getObject(int index) {
			return _TheModel[index];
		}

		/**
		 * 
		 * @param index
		 * @param obj
		 * @return true if business rules are ok
		 */
		private boolean setObject(int index, Object obj) {

			_TheModel[index] = obj;
			return true;
		}

		public void setDate(Date date) {

			// try {
			// _Sdf.parse(date);
			// } catch (ParseException e) {
			// e.printStackTrace();
			// }
			setObject(FIELDINDEX_DATE, date);
		}

		public Date getDate() {
			return (Date) getObject(FIELDINDEX_DATE);
		}

		// public String getPlace() {
		// return (String) getObject(FIELDINDEX_PLACE);
		// }
		//
		// public void setPlace(String place) {
		// setObject(FIELDINDEX_PLACE, place);
		// }

		// public String getType() {
		// return (String) getObject(FIELDINDEX_TYPE);
		// }

		// the visible encounter type label in the table
		public void setTypeLabel(String type) {
			setObject(FIELDINDEX_TYPE, type);
		}

		// used for caching
		public Long getEncounterId() {
			return _EncounterId;
		}

		public void setEncounterId(Long _EncounterId) {
			this._EncounterId = _EncounterId;
		}

		public String getEncounterType() {
			return _EncounterType;
		}

		public void setEncounterType(String _EncounterType) {
			this._EncounterType = _EncounterType;
		}

	}

	/**
	 * @return the model
	 */
	public ArrayList<EncounterModelData> getModelValues() {
		if (_EncounterModelDataValues == null)
			_EncounterModelDataValues = new ArrayList<EncounterListTableModel.EncounterModelData>();
		return _EncounterModelDataValues;
	}

	/**
	 * set the model
	 * 
	 * @param modelValues
	 */
	public void setModelValues(ArrayList<EncounterModelData> modelValues, String[] tableTitles) {
		_EncounterModelDataValues = modelValues;
		_ColumnNames = tableTitles;
	}

	public void addModelValue(EncounterModelData patientModelData) {
		getModelValues().add(patientModelData);
	}

	public void removeModelValue(EncounterModelData patientModelData) {
		getModelValues().remove(patientModelData);
	}

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
}
