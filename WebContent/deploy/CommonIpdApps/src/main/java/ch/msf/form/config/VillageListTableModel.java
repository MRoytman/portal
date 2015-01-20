package ch.msf.form.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.swing.table.DefaultTableModel;

import ch.msf.CommonConstants;
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

/**
 * This is related to TN129 (presentation of villages of origin)
 * 
 * @author cmi
 * 
 */
public class VillageListTableModel extends DefaultTableModel {

	public final static int FIELDINDEX_VILLAGE = 0;
	// public final static int FIELDINDEX_HEALTH_AREA = FIELDINDEX_VILLAGE + 1;
	public final static int TOTAL_FIELDS = FIELDINDEX_VILLAGE + 1;

	private String[] _ColumnNames = null;

	// the model
	private ArrayList<VillageHealthAreaModelData> _VillageAreaModelDataValues = null;

	protected ConfigurationManagerImpl _ConfigurationManager;

	SimpleDateFormat _Sdf = new SimpleDateFormat(CommonConstants.SIMPLE_DATE_FORMAT);

	public VillageListTableModel() {
	}

	public VillageListTableModel(ArrayList<VillageHealthAreaModelData> patientModelData) {
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

		if (c == FIELDINDEX_VILLAGE)
			return String.class;

		// if (c == FIELDINDEX_HEALTH_AREA)
		// return JComboBox.class;

		return null;
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {

		return false;
	}

	/*

	 */
	// public void setValueAt(Object value, int row, int col) {
	// // System.out.println("setValueAt row" + row);
	// // System.out.println("setValueAt getRowCount()" + getRowCount());
	//
	// if (row < getModelValues().size()) {
	// getModelValues().get(row).setObject(col, value);
	// fireTableCellUpdated(row, col);
	// }
	// }

	public class VillageHealthAreaModelData implements Comparable<VillageHealthAreaModelData> {

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(_TheModel);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			VillageHealthAreaModelData other = (VillageHealthAreaModelData) obj;
//			if (!getOuterType().equals(other.getOuterType()))
//				return false;

			
//			if (!Arrays.equals(_TheModel, other._TheModel))
//				return false;
//			return true;
			String thisObj = this._TheModel[0];
			String otherObj = other._TheModel[0];
			return thisObj.equals(otherObj);
		}

		@Override
		public int compareTo(VillageHealthAreaModelData other) {
			String thisObj = this._TheModel[0];
			String otherObj = other._TheModel[0];

			return thisObj.compareTo(otherObj);
		}

		public VillageHealthAreaModelData() {

		}

		public VillageHealthAreaModelData(String villageOrigin, String healthArea) {
			setVillageOrigin(villageOrigin);
			// setHealthArea(healthArea);
		}

		String[] _TheModel = new String[TOTAL_FIELDS];

		public Object getObject(int index) {
			return _TheModel[index];
		}

		public void setObject(int index, String obj) {
			_TheModel[index] = obj;
		}

		public void setVillageOrigin(String id) {
			setObject(FIELDINDEX_VILLAGE, id);
		}

		// public void setHealthArea(String id) {
		// setObject(FIELDINDEX_HEALTH_AREA, id);
		// }
		//
		// public String getHealthArea() {
		// return (String) getObject(FIELDINDEX_HEALTH_AREA);
		// }

		public String getVillageOrigin() {
			return (String) getObject(FIELDINDEX_VILLAGE);
		}

		private VillageListTableModel getOuterType() {
			return VillageListTableModel.this;
		}

	}

	/**
	 * @return the model
	 */
	public ArrayList<VillageHealthAreaModelData> getModelValues() {
		if (_VillageAreaModelDataValues == null)
			_VillageAreaModelDataValues = new ArrayList<VillageListTableModel.VillageHealthAreaModelData>();
		return _VillageAreaModelDataValues;
	}

	/**
	 * set the model
	 * 
	 * @param modelValues
	 */
	public void setModelValues(ArrayList<VillageHealthAreaModelData> modelValues, String[] tableTitles) {
		_VillageAreaModelDataValues = modelValues;
		_ColumnNames = tableTitles;
	}

	public void addModelValue(VillageHealthAreaModelData patientModelData) {
		if (!getModelValues().contains(patientModelData)) {
			getModelValues().add(patientModelData);
			Collections.sort(getModelValues());
		}

	}

	public void removeModelValue(VillageHealthAreaModelData patientModelData) {
		getModelValues().remove(patientModelData);
	}

}
