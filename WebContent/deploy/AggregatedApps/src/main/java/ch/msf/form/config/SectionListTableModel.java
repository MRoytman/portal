package ch.msf.form.config;

import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

public class SectionListTableModel extends DefaultTableModel {

	public final static int FIELDINDEX_SESSIONTYPE = 0;
	public final static int FIELDINDEX_SESSION_MODIFCOMPLETED = FIELDINDEX_SESSIONTYPE + 1;
	public final static int TOTAL_FIELDS = FIELDINDEX_SESSION_MODIFCOMPLETED + 1;

	// due to different types in the same column
	// used to get the correct renderer for each cell
	// public int _ListIndexRenderer = 0;

	// when a new encounter is created or data modified from existing row
	private Integer _LineNumberEditable;

	String[] _ColumnNames = null;

	// the model
	private ArrayList<SectionModelData> _SessionModelDataValues = null;

	protected ConfigurationManagerImpl _ConfigurationManager;

	public SectionListTableModel() {

	}

	public SectionListTableModel(ArrayList<SectionModelData> encounterModelData) {
		_ConfigurationManager = ServiceHelper.getConfigurationManagerService();

		if (encounterModelData != null)
			buildModel(encounterModelData);
	}

	private void buildModel(ArrayList<SectionModelData> patientModelData) {
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
			return null; // ?
							// _DummyAggregatedModelData.getObject(c).getClass();

		if (c == FIELDINDEX_SESSION_MODIFCOMPLETED)
			return Boolean.class; // just to show the combobox

		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
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

	public class SectionModelData {

		public SectionModelData() {
		}

		public SectionModelData(String type, boolean completed) {
			setTypeLabel(type);
			setCompleted(completed);
		}

		Object[] _TheModel = new Object[TOTAL_FIELDS];

		private Long _SectionId; // technical Encounter id
		
		private String _AggregationThemeType;

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

		// the visible encounter type label in the table
		public void setTypeLabel(String type) {
			setObject(FIELDINDEX_SESSIONTYPE, type);
		}
		
		public String getTypeLabel() {
			return (String) getObject(FIELDINDEX_SESSIONTYPE);
		}
		
		public void setCompleted(boolean completed) {
			setObject(FIELDINDEX_SESSION_MODIFCOMPLETED, completed);
		}
		
		public Boolean getCompleted() {
			return (Boolean) getObject(FIELDINDEX_SESSION_MODIFCOMPLETED);
		}

		// used for caching
		public Long getSectionId() {
			return _SectionId;
		}

		public void setSessionId(Long _SessionId) {
			this._SectionId = _SessionId;
		}

		public void setAggregationThemeType(String aggregationThemeType) {
			_AggregationThemeType = aggregationThemeType;			
		}
		
		public String getAggregationThemeType() {
			return _AggregationThemeType;
		}

	}

	/**
	 * @return the model
	 */
	public ArrayList<SectionModelData> getModelValues() {
		if (_SessionModelDataValues == null)
			_SessionModelDataValues = new ArrayList<SectionListTableModel.SectionModelData>();
		return _SessionModelDataValues;
	}

	/**
	 * set the model
	 * 
	 * @param modelValues
	 */
	public void setModelValues(ArrayList<SectionModelData> modelValues, String[] tableTitles) {
		_SessionModelDataValues = modelValues;
		_ColumnNames = tableTitles;
	}

	public void addModelValue(SectionModelData patientModelData) {
		getModelValues().add(patientModelData);
	}

	public void removeModelValue(SectionModelData patientModelData) {
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
