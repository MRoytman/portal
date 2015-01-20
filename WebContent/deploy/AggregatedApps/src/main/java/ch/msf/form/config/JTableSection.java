package ch.msf.form.config;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import ch.msf.model.SectionTable;

public class JTableSection extends JTable {

	protected SectionTable _SectionTable;

	public SectionTable getSectionTable() {
		return _SectionTable;
	}

	public void setSectionTable(SectionTable _SectionTable) {
		this._SectionTable = _SectionTable;
	}

	public JTableSection(TableModel dm, SectionTable sectionTable) {
		super(dm);
		_SectionTable = sectionTable;
	}
	
	
}
