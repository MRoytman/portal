package ch.msf.form.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.error.FatalException;
import ch.msf.form.MyDateFieldEditor3;
import ch.msf.form.config.SectionListTableModel;
import ch.msf.form.config.SectionListTableModel.SectionModelData;
import ch.msf.model.Section;
import ch.msf.service.ServiceHelper;
import ch.msf.util.StackTraceUtil;

public class SectionListFormPanel extends AbstractWizardPanel {

	private static final long serialVersionUID = 1L;

	// private JButton _NewButton; //
	// private JButton _DeleteButton; //
	// private JButton _CancelEditButton; //
	// private JButton _HelpButton; // 

	private JScrollPane _ScrollPane;

	protected SectionListTableModel _MyTableModel;

	// sectionListFormDescriptor _Descriptor;

	private JTable _Table;

	// private ArrayList<String> _sectionsTypes;

	public SectionListFormPanel() {
		super(null);
	}

	public JPanel getContentPanel() {

		// pass a null model, the values will be passed by the descriptor
		_MyTableModel = new SectionListTableModel(null);

		JPanel contentPanelMaster = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));

		JPanel contentPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		contentPanelMaster.add(contentPanel1);

		// contentPanel1.setBorder(BorderFactory.createLineBorder(Color.red));

		JPanel excercicePanel = new JPanel();
		contentPanel1.add(excercicePanel);

		excercicePanel.setLayout(new BoxLayout(excercicePanel, BoxLayout.Y_AXIS));
		excercicePanel.setMinimumSize(new Dimension(EXERCISE_PANEL_WIDTH, EXERCISE_PANEL_HEIGHT));

		excercicePanel.setPreferredSize(new Dimension(EXERCISE_PANEL_WIDTH, EXERCISE_PANEL_HEIGHT));
		excercicePanel.setSize(new Dimension(EXERCISE_PANEL_WIDTH, EXERCISE_PANEL_HEIGHT));

		// current patient info
		JPanel curSectionListPanel = new JPanel();
		Dimension curPatientDim = new Dimension(AGGREG_INFO_PANEL_WIDTH, AGGREG_INFO_PANEL_HEIGHT);
		curSectionListPanel.setMinimumSize(curPatientDim);
		curSectionListPanel.setMaximumSize(curPatientDim);
		curSectionListPanel.setPreferredSize(curPatientDim);
		curSectionListPanel.setSize(curPatientDim);
		excercicePanel.add(curSectionListPanel);
		_CurAggregationInfo = new JLabel();
		curSectionListPanel.add(_CurAggregationInfo);
		_CurAggregationInfo.setText("");

		// Set up renderer
		// final TableCellRenderer myDateRenderer = new DateRenderer();

		final DefaultCellEditor dateCellEditor = new MyDateFieldEditor3(null, null);

		final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField());
		_Table = new JTable(_MyTableModel) {
			public void changeSelection(final int row, final int column, boolean toggle, boolean extend) {
				super.changeSelection(row, column, toggle, extend);



				// stay on same row...(tab at last new cell)
				if (_MyTableModel.getLineNumberEditable() != null && row != _MyTableModel.getLineNumberEditable()) {
					changeSelection(_MyTableModel.getLineNumberEditable(), column, toggle, extend);
				}
			}

			// return the editor according to the data type of the cell
			// (the column has not a fixed type!)
			public TableCellEditor getCellEditor(int row, int column) {
				if ((column == SectionListTableModel.FIELDINDEX_SESSION_MODIFCOMPLETED)) {
					return dateCellEditor;
				}
				return defaultCellEditor;
			}

			// write the date according same format their are entered
			public TableCellRenderer getCellRenderer(int row, int column) {
				Object obj = null;
				// we save the current row which is in use, to return the
				// correct object class in the model
				if ((column == SectionListTableModel.FIELDINDEX_SESSION_MODIFCOMPLETED)) {
					// obj = _MyTableModel.getKeyValues().get(row)._Value;
					// if (obj instanceof Date) {
					// return myDateRenderer;
					// }
					// else if (obj instanceof Boolean) {
					// return myBooleanRenderer;
					// }
				}
				// else...
				return super.getCellRenderer(row, column);
			}

			// Implement table cell tool tips.
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);

				if (rowIndex >= 0 && rowIndex < getRowCount()) {
					int colIndex = columnAtPoint(p);
					int realColumnIndex = convertColumnIndexToModel(colIndex);

					if (realColumnIndex == SectionListTableModel.FIELDINDEX_SESSIONTYPE) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_SECTIONTYPE);
					} else if (realColumnIndex == SectionListTableModel.FIELDINDEX_SESSION_MODIFCOMPLETED) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_SECTIONCHECKBOX);
					} else {
						// You can omit this part if you know you don't
						// have any renderers that supply their own tool
						// tips.
						tip = super.getToolTipText(e);
					}
				}
				return tip;
			}
		};

		// change col titles
		String[] tableTitles = getEntryFormConfigurationManager().getTableTitleTranslation("sectionList");
		changeTableColumnTitles(tableTitles);

		// detect any country selection change
		_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_Table.getSelectionModel().addListSelectionListener(new RowListener());

		_Table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		_Table.setFillsViewportHeight(true); // ?

		// column settings
		_Table.getColumnModel().getColumn(1).setMinWidth(50);
		_Table.getColumnModel().getColumn(1).setMaxWidth(200);

		_Table.setDefaultRenderer(Object.class, new MyCellRenderer());

		// register the change listener (useful for the new section)
		_Table.getModel().addTableModelListener(this);

		// Create the scroll pane and add the table to it.
		_ScrollPane = new JScrollPane(_Table);

		Dimension scrollDim = new Dimension(EXERCISE_LIST_WIDTH, EXERCISE_LIST_HEIGHT);
		_ScrollPane.setMinimumSize(scrollDim);
		_ScrollPane.setMaximumSize(scrollDim);
		_ScrollPane.setPreferredSize(scrollDim);
		_ScrollPane.setSize(scrollDim);

		_ScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		_ScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		excercicePanel.add(_ScrollPane);

		// buttons
		JPanel buttonPanel = new JPanel();
		Dimension buttonDim = new Dimension(BUTTON_PANEL_WIDTH, BUTTON_PANEL_HEIGHT);
		buttonPanel.setMinimumSize(buttonDim);
		buttonPanel.setMaximumSize(buttonDim);
		buttonPanel.setPreferredSize(buttonDim);
		buttonPanel.setSize(buttonDim);
		excercicePanel.add(buttonPanel);

		// buttonPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		// final int NBR_BUTTONS = 1; //
		// buttonPanel.setLayout(new GridLayout(NBR_BUTTONS, 1, 0, 3));// nbr
		//
		// String mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_NEWCOMMAND);
		// _NewButton = new JButton(mess);
		// _NewButton.addActionListener(this);
		// buttonPanel.add(_NewButton);
		//
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		// _DeleteButton = new JButton(mess);
		// _DeleteButton.addActionListener(this);
		// buttonPanel.add(_DeleteButton);
		//
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		// _CancelEditButton = new JButton(mess);
		// _CancelEditButton.addActionListener(this);
		// buttonPanel.add(_CancelEditButton);
		//
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		// _HelpButton = new JButton(mess); // 
		// _HelpButton.addActionListener(this);
		// buttonPanel.add(_HelpButton);
		//
		// // 
		// String tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_sectionNEW);
		// _NewButton.setToolTipText(tip);
		// tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_sectionDELETE);
		// _DeleteButton.setToolTipText(tip);
		// tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_sectionCANCEL);
		// _CancelEditButton.setToolTipText(tip);

		return contentPanelMaster;
	}

	class DateRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;
		private DateFormat formatter = new SimpleDateFormat(CommonConstants.SIMPLE_DATE_FORMAT);

		public DateRenderer() {
			super();
		}

		public void setValue(Object value) {
			if (formatter == null) {
				formatter = DateFormat.getDateInstance();
			}
			setText((value == null) ? "" : formatter.format(value));
			setHorizontalAlignment(SwingConstants.RIGHT);
		}
	}



	private void selectFirstLine() {
		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {
				if (getTable().getRowCount() > 0) {
					getTable().setRowSelectionInterval(getTable().getRowCount() - 1, getTable().getRowCount() - 1);
					getTable().changeSelection(0, SectionListTableModel.FIELDINDEX_SESSION_MODIFCOMPLETED, false, false);
					if (getTable().getCellEditor() != null)
						getTable().getCellEditor().cancelCellEditing();
				}
				getDescriptor().getWizard().setNextFinishButtonEnabled(getTable().getRowCount() > 0 && getTable().getSelectedRow() >= 0);

			}
		};
		//  run it not in the current stack
		try {

			SwingUtilities.invokeLater(selectNewInsertedLine);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(ex), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		}
	}

	private void selectNewInsertedLineAndFocus() {
		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {
				getTable().setRowSelectionInterval(getTable().getRowCount() - 1, getTable().getRowCount() - 1);
				getTable().changeSelection(getTable().getRowCount() - 1, SectionListTableModel.FIELDINDEX_SESSION_MODIFCOMPLETED, false, false);
				getTable().editCellAt(getTable().getRowCount() - 1, SectionListTableModel.FIELDINDEX_SESSION_MODIFCOMPLETED);
				Component editor = getTable().getEditorComponent();
				editor.requestFocusInWindow();
			}
		};
		try {

			SwingUtilities.invokeLater(selectNewInsertedLine);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(ex), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		}

	}


	// the model should be updated
	public void setDataModel(ArrayList<SectionModelData> modelValues, String[] tableTitles) {

		_MyTableModel.setModelValues(modelValues, tableTitles);
		// change col titles
		changeTableColumnTitles(tableTitles);
	}

	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {

				DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();

				int rowImpacted = selectionModel.getMinSelectionIndex();
				// save the section selection
				SectionModelData sectionModelData = getTableModel().getModelValues().get(rowImpacted);

				_ConfigurationManager.setCurrentSectionModelData(sectionModelData);

				if (_MyTableModel.getLineNumberEditable() != null) {

					// if user changed row, go back to the row
					if (rowImpacted != _MyTableModel.getLineNumberEditable()) {
						// getTable().addRowSelectionInterval(_MyTableModel.getLineNumberEditable(),
						// _MyTableModel.getLineNumberEditable());
						getTable().setRowSelectionInterval(_MyTableModel.getLineNumberEditable(), _MyTableModel.getLineNumberEditable());
					}
				}

				return;
			}
		}
	}


	public void setDescriptor(SectionListFormDescriptor sectionConsultationListFormDescriptor) {
		_Descriptor = sectionConsultationListFormDescriptor;

	}

	public boolean isSelectionEmpty() {
		return _Table.getSelectionModel().isSelectionEmpty();
	}

	public SectionListTableModel getTableModel() {
		return _MyTableModel;
	}

	public JTable getTable() {
		return _Table;
	}

	public class MyCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (_MyTableModel.getLineNumberEditable() != null && row == _MyTableModel.getLineNumberEditable())
				cell.setBackground(Color.pink);
			else if (!isSelected)
				cell.setBackground(null);

			return cell;
		}
	}

	/**
	 * called by descriptor so that the panel can be initialized
	 * 
	 * @param patientChanged
	 */
	public void configurePanelStartState() {

		// configure the panel

		internationalize(); //

		if (_ConfigurationManager.isSectionDataSaved()) {
			getTableModel().setLineNumberEditable(null);
		}
		// // allow any row selection
		// if (_ConfigurationManager.isSectionDataSaved() && patientChanged) {
		// // if several lines and none selected, select first one
		// if (getTable().getRowCount() > 0) {
		// getTable().setRowSelectionInterval(0, 0);
		// getTable().changeSelection(0, 0, false, false);
		// }
		// }
//		if (_ConfigurationManager.isSectionDataSaved() && contextChanged) {
			// if several lines and none selected, select first one
			if (getTable().getRowCount() > 0 && getTable().getSelectedRow() == -1) {

				getTable().setRowSelectionInterval(0, 0);
				getTable().changeSelection(0, 0, false, false);
			}
//		}

		getDescriptor().getWizard().setNextFinishButtonEnabled(getTable().getRowCount() > 0 && getTable().getSelectedRow() >= 0);
		getDescriptor().getWizard().setBackButtonEnabled(true /*
															 * _ConfigurationManager
															 * .
															 * isSectionDataSaved
															 * ()
															 */);

	}

	/**
	 * 
	 */
	protected void internationalize() {

		// String mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_NEWCOMMAND);
		// _NewButton.setText(mess);
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		// _DeleteButton.setText(mess);
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		// _CancelEditButton.setText(mess);
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		// _HelpButton.setText(mess);
	}

	public void fillModelValue(SectionModelData modelValue, Section section) {

		String aggregationLabel = "/";
		try {
			aggregationLabel = _ConfigurationManager.getSectionContentLabels(modelValue.getAggregationThemeType(), section.getThemeCode());
		} catch (FatalException e) {
			System.out.println(getClass().getName() + "::fillModelValue: Problem getting the label :" + e.getMessage());
			e.printStackTrace();
		}
		// modelValue.setType(section.getType());
		modelValue.setTypeLabel(aggregationLabel);
		modelValue.setCompleted(section.isDone());

	}

	@Override
	// 
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_SECTIONLISTPANEL;
	}
}
