package ch.msf.model;

import javax.swing.JTable;

import ch.msf.form.config.SectionTableKeyValueTableModel;

public class SectionTable extends JTable {

	private static final long serialVersionUID = 1L;
	
	private String _TableId;
	private int _ColumnNumber;
	private int[] _ColumnSize;
	private SectionTableKeyValueTableModel _SectionKeyValueTableModel;
	
	public SectionTable(String tableId, int columnNumber, SectionTableKeyValueTableModel sectionKeyValueTableModel) {
		super();
		this._TableId = tableId;
		this._ColumnNumber = columnNumber;
		this._SectionKeyValueTableModel = sectionKeyValueTableModel;
	}
	
	public String getTableId() {
		return _TableId;
	}
	
//	
	public int getColumnNumber() {
		return _ColumnNumber;
	}
	
//	public int[] getColumnSize() {
//		return _ColumnSize;
//	}
//	
//	public void setColumnSize(int[] _ColumnSize) {
//		this._ColumnSize = _ColumnSize;
//	}

	public SectionTableKeyValueTableModel getSectionKeyValueTableModel() {
		return _SectionKeyValueTableModel;
	}

}
