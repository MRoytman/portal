package ch.msf.form.config;

import java.util.ArrayList;

import javax.swing.table.DefaultTableModel;

import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

public class AggregationListTableModel extends DefaultTableModel {

	public final static int FIELDINDEX_YEAR = 0;
	public final static int FIELDINDEX_WEEK = FIELDINDEX_YEAR + 1;
	public final static int FIELDINDEX_CONFIG_LABEL = FIELDINDEX_WEEK + 1;
	public final static int FIELDINDEX_STARTDATE = FIELDINDEX_CONFIG_LABEL + 1;
	public final static int FIELDINDEX_ENDDATE = FIELDINDEX_STARTDATE + 1;

	public final static int TOTAL_FIELDS = FIELDINDEX_ENDDATE + 1;

	// when a new aggregated is created or data modified from existing row
	private Integer _LineNumberEditable;

	private String[] _ColumnNames = null;

	// the model
	private ArrayList<AggregatedModelData> _AggregatedModelDataValues = null;

	protected ConfigurationManagerImpl _ConfigurationManager;

	private static final AggregatedModelData _DummyAggregatedModelData = new AggregationListTableModel().new AggregatedModelData("year", "week", "theme", "start", "end");

	public AggregationListTableModel() {
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
			return _DummyAggregatedModelData.getObject(c).getClass();
		return getValueAt(0, c).getClass();
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {

		if (col == FIELDINDEX_YEAR || col == FIELDINDEX_WEEK || col == FIELDINDEX_CONFIG_LABEL) {

			if (getLineNumberEditable() == null)
				return false;

			if (row == getLineNumberEditable()) {
				return true;
			}
		}
		return false;
	}

	/*

	 */
	public void setValueAt(Object value, int row, int col) {

		if (row < getModelValues().size()) {
			getModelValues().get(row).setObject(col, value);
			fireTableCellUpdated(row, col);
		}
	}

	public class AggregatedModelData {

		public AggregatedModelData() {
		}

		public AggregatedModelData(/* String id, */String year, String week, String startDate, String endDate, String themeTypeLabel) {
			// setId(id);
			setYear(year);
			setWeek(week);
			setStartDate(startDate);
			setEndDate(endDate);
			setAggregatedLabel(themeTypeLabel);
		}

		// technical AggregatedContext id
		private Long _AggregatedContextId;
		// private String _AggregatedType;// see PeriodType

		Object[] _TheModel = new Object[TOTAL_FIELDS];

		public Object getObject(int index) {
			return _TheModel[index];
		}

		protected void setObject(int index, Object obj) {
			_TheModel[index] = obj;
		}

		public void setYear(String year) {
			setObject(FIELDINDEX_YEAR, year);
		}

		public String getYear() {
			return (String) getObject(FIELDINDEX_YEAR);
		}

		public void setWeek(String week) {
			setObject(FIELDINDEX_WEEK, week);
		}

		public String getWeek() {
			return (String) getObject(FIELDINDEX_WEEK);
		}

		public void setStartDate(String startDate) {
			setObject(FIELDINDEX_STARTDATE, startDate);
		}

		public String getStartDate() {
			return (String) getObject(FIELDINDEX_STARTDATE);
		}

		public void setEndDate(String name) {
			setObject(FIELDINDEX_ENDDATE, name);
		}

		public String getEndDate() {
			return (String) getObject(FIELDINDEX_ENDDATE);
		}

		public void setAggregatedLabel(String type) {
			setObject(FIELDINDEX_CONFIG_LABEL, type);
		}

		public String getAggregatedLabel() {
			return (String) getObject(FIELDINDEX_CONFIG_LABEL);
		}

		// used for caching
		public Long getAggregatedContextId() {
			return _AggregatedContextId;
		}

		public void setAggregatedContextId(Long _AggregatedContextId) {
			this._AggregatedContextId = _AggregatedContextId;
		}

	}

	/**
	 * @return the model
	 */
	public ArrayList<AggregatedModelData> getModelValues() {
		if (_AggregatedModelDataValues == null)
			_AggregatedModelDataValues = new ArrayList<AggregationListTableModel.AggregatedModelData>();
		return _AggregatedModelDataValues;
	}

	/**
	 * set the model
	 * 
	 * @param modelValues
	 */
	public void setModelValues(ArrayList<AggregatedModelData> modelValues, String[] tableTitles) {
		_AggregatedModelDataValues = modelValues;
		_ColumnNames = tableTitles;
	}

	public void addModelValue(AggregatedModelData aggregatedModelData) {
		getModelValues().add(aggregatedModelData);
	}

	public void removeModelValue(AggregatedModelData aggregatedModelData) {
		getModelValues().remove(aggregatedModelData);
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

}
