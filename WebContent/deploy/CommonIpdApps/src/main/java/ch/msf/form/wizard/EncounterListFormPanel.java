package ch.msf.form.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.form.MyDateFieldEditor3;
import ch.msf.form.config.EncounterListTableModel;
import ch.msf.form.config.EncounterListTableModel.EncounterModelData;
import ch.msf.model.Encounter;
import ch.msf.service.ServiceHelper;
import ch.msf.util.StackTraceUtil;

public class EncounterListFormPanel extends AbstractWizardPanel implements ActionListener, TableModelListener {

	private static final long serialVersionUID = 1L;

	private JButton _NewButton; //
	private JButton _DeleteButton; //
	private JButton _CancelEditButton; //
	private JButton _HelpButton; // TN106

	private JScrollPane _ScrollPane;

	protected EncounterListTableModel _MyTableModel;

	private JTable _Table;

	public EncounterListFormPanel() {
		super();
	}

	public JPanel getContentPanel() {

		// pass a null model, the values will be passed by the descriptor
		_MyTableModel = new EncounterListTableModel(null);

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
		JPanel curPatientPanel = new JPanel();
		Dimension curPatientDim = new Dimension(PATIENT_INFO_PANEL_WIDTH, PATIENT_INFO_PANEL_HEIGHT);
		curPatientPanel.setMinimumSize(curPatientDim);
		curPatientPanel.setMaximumSize(curPatientDim);
		curPatientPanel.setPreferredSize(curPatientDim);
		curPatientPanel.setSize(curPatientDim);
		excercicePanel.add(curPatientPanel);
		_CurPatientInfo = new JLabel();
		curPatientPanel.add(_CurPatientInfo);
		_CurPatientInfo.setText("");

		// Set up renderer
		final TableCellRenderer myDateRenderer = new DateRenderer();

		final DefaultCellEditor dateCellEditor = new MyDateFieldEditor3(null, null);

		final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField());
		_Table = new JTable(_MyTableModel) {
			public void changeSelection(final int row, final int column, boolean toggle, boolean extend) {
				super.changeSelection(row, column, toggle, extend);

				// if (editCellAt(row, column)) {
				// getEditorComponent().requestFocusInWindow();
				// }

				// stay on same row...(tab at last new cell)
				if (_MyTableModel.getLineNumberEditable() != null && row != _MyTableModel.getLineNumberEditable()) {
					changeSelection(_MyTableModel.getLineNumberEditable(), column, toggle, extend);
				}
			}

			// return the editor according to the data type of the cell
			// (the column has not a fixed type!)
			public TableCellEditor getCellEditor(int row, int column) {
				if ((column == EncounterListTableModel.FIELDINDEX_DATE)) {
					return dateCellEditor;
				}
				return defaultCellEditor;
			}

			// write the date according same format their are entered
			public TableCellRenderer getCellRenderer(int row, int column) {
				Object obj = null;
				// we save the current row which is in use, to return the
				// correct object class in the model
				if ((column == EncounterListTableModel.FIELDINDEX_DATE)) {
					// obj = _MyTableModel.getKeyValues().get(row)._Value;
					// if (obj instanceof Date) {
					return myDateRenderer;
					// }
					// else if (obj instanceof Boolean) {
					// return myBooleanRenderer;
					// }
				}
				// else...
				return super.getCellRenderer(row, column);
			}

			// TN66 Implement table cell tool tips.
			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);

				if (rowIndex >= 0 && rowIndex < getRowCount()) {
					int colIndex = columnAtPoint(p);
					int realColumnIndex = convertColumnIndexToModel(colIndex);

					if (realColumnIndex == EncounterListTableModel.FIELDINDEX_TYPE) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_ENCOUNTERTYPE);
					} else if (realColumnIndex == EncounterListTableModel.FIELDINDEX_DATE) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_ENCOUNTERDATE);
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
		String[] tableTitles = getEntryFormConfigurationManager().getTableTitleTranslation("encounterList");
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

		// register the change listener (useful for the new encounter)
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
		final int NBR_BUTTONS = 1; //
		buttonPanel.setLayout(new GridLayout(NBR_BUTTONS, 1, 0, 3));// nbr

		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_NEWCOMMAND);
		_NewButton = new JButton(mess);
		_NewButton.addActionListener(this);
		buttonPanel.add(_NewButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		_DeleteButton = new JButton(mess);
		_DeleteButton.addActionListener(this);
		buttonPanel.add(_DeleteButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelEditButton = new JButton(mess);
		_CancelEditButton.addActionListener(this);
		buttonPanel.add(_CancelEditButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton = new JButton(mess); // TN106
		_HelpButton.addActionListener(this);
		buttonPanel.add(_HelpButton);

		// TN66
		String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_ENCOUNTERNEW);
		_NewButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_ENCOUNTERDELETE);
		_DeleteButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_ENCOUNTERCANCEL);
		_CancelEditButton.setToolTipText(tip);

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

	/**
	 * a button is pressed
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == _HelpButton) { // TN106
			String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_ENCOUNTERLISTHELP);
			JOptionPane.showMessageDialog(null, tip, "", JOptionPane.PLAIN_MESSAGE, null);

		} else if (e.getSource() == _NewButton) { // edit
			System.out.println("actionPerformed NEW");

			EncounterModelData encounterModelData = new EncounterListTableModel().new EncounterModelData();

			// check if there are more than one encounter type
			ArrayList<String> encounterTypes = getEncounterTypes();
			if (encounterTypes != null && encounterTypes.size() > 0) {

				Object selectedValue = null;
				Object[] objEncounterTypes = encounterTypes.toArray();
				// TN84
				ArrayList<String> encounterLabels = new ArrayList<String>();
				for (Object encounterName : encounterTypes) {
					String encounterLabel = _ConfigurationManager.getEncounterLabel((String) encounterName);
					encounterLabels.add(encounterLabel);
				}
				Object[] encounterChoiceLabels = encounterLabels.toArray();

				// check if there is only one
				if (objEncounterTypes.length == 1) {
					// selectedValue = possibilities[0];
					selectedValue = encounterChoiceLabels[0];
				} else {
					// show choice dialog
					selectedValue = JOptionPane.showInputDialog(null, "Chose the encounter type", "", JOptionPane.INFORMATION_MESSAGE, null, encounterChoiceLabels,
							encounterChoiceLabels[0]);
				}
				int index = 0;
				if (selectedValue != null) {
					// get the index
					for (Object o : encounterChoiceLabels) {
						if (selectedValue == o)
							break;
						index++;
					}
					encounterModelData.setEncounterType((String) objEncounterTypes[index]);
					encounterModelData.setTypeLabel((String) encounterChoiceLabels[index]);
				} else {
					encounterModelData.setEncounterType((String) objEncounterTypes[index]);
					encounterModelData.setTypeLabel((String) encounterChoiceLabels[index]);
				}

				// update the model
				_MyTableModel.addModelValue(encounterModelData);
				_MyTableModel.fireTableRowsInserted(0, 0);

				selectNewInsertedLineAndFocus();

				// flag data modification...
				getEntryFormConfigurationManager().setEncounterDataSaved(false);
				// ...and row number
				_MyTableModel.setLineNumberEditable(_MyTableModel.getRowCount() - 1);

				getEntryFormConfigurationManager().setNewEncounterDataInsertion(true);
				getDescriptor().getWizard().setBackButtonEnabled(false);
				// never disable cancel...
				_CancelEditButton.setEnabled(true); // TN113

			} else {
				// warn user
				String errMess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_NO_ENCOUNTER_PERMISSIONS);
				JOptionPane.showMessageDialog(null, errMess, "", JOptionPane.PLAIN_MESSAGE, null);

				getEntryFormConfigurationManager().setNewEncounterDataInsertion(false);
				getDescriptor().getWizard().setBackButtonEnabled(true);

				_CancelEditButton.setEnabled(false);
			}

			getDescriptor().getWizard().setNextFinishButtonEnabled(false);

			// disable new
			_NewButton.setEnabled(false);

			// disable delete if new enabled
			_DeleteButton.setEnabled(false);

		} else if (e.getSource() == _DeleteButton) { // edit
			System.out.println("actionPerformed DELETE");

			String errMessCancel = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_CANCEL);
			String messConfirmDelete = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CONFIRMDELETE);
			Object[] options = { "OK", errMessCancel };

			int retCode = JOptionPane.showOptionDialog(null, messConfirmDelete, "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

			if (retCode == 0) {
				EncounterModelData encounterModelData = getTableModel().getModelValues().get(getTable().getSelectedRow());
				int encounterDeletedRetCode = deleteEncounter(encounterModelData.getEncounterId());
				//
				if (encounterDeletedRetCode >= 0) {// if new insertion deleted

				}
				// reset saved flag
				getEntryFormConfigurationManager().setEncounterDataSaved(true);

				// remove the row from the model and the table
				_MyTableModel.removeModelValue(encounterModelData);
				_MyTableModel.fireTableRowsDeleted(getTable().getSelectedRow(), getTable().getSelectedRow());

				configurePanelStartState(true);
			}

		} else if (e.getSource() == _CancelEditButton) { // cancel edit

			// restore original value
			EncounterModelData encounterModelData = getTableModel().getModelValues().get(getTable().getSelectedRow());

			Encounter encounter = ServiceHelper.getEncounterManagerService().getEncounter(encounterModelData.getEncounterId());
			if (encounter != null) {
				// update the list model
				fillModelValue(encounterModelData, encounter);
				_MyTableModel.fireTableRowsInserted(0, 0);

				// update the list of concepts
				// just need to remove it from cache, it will be reloaded next
				// screen
				_ConfigurationManager.getEncounterCache().remove(encounterModelData.getEncounterId());

			} else {

				// Encounter is null, then we just inserted a new line normally

				// cancel the editor focus set when new line added
				// (editor.requestFocusInWindow())
				if (getTable().getCellEditor() != null) // TN112
					getTable().getCellEditor().cancelCellEditing();
				// update the model by removing the inserted line
				// remove the row from the model and the table
				_MyTableModel.removeModelValue(encounterModelData);
				_MyTableModel.fireTableRowsDeleted(getTable().getSelectedRow(), getTable().getSelectedRow());

				if (getTable().getRowCount() > 0) {
					// select 1rst line
					selectFirstLine();
				}
			}

			// reset saved flag
			getEntryFormConfigurationManager().setEncounterDataSaved(true);
			getEntryFormConfigurationManager().setNewEncounterDataInsertion(false);

			// restore original panel situation
			configurePanelStartState(false);

			// change line to remove the red background modification
			if (getTable().getRowCount() > 0) {
				// getTable().addRowSelectionInterval(0, 0);
				getTable().setRowSelectionInterval(0, 0);
				// do no selection
				// getTable().removeRowSelectionInterval(0,
				// getTable().getRowCount() - 1);
			}

		} else
			System.out.println(e.getSource());
	}

	private ArrayList<String> getEncounterTypes() {

		ArrayList<String> allEncountersTypes = _ConfigurationManager.getConfigFields(CommonIpdConstants.ENCOUNTER_TYPE_FILENAME_PREFIX);
		List<String> roleAllowedEncountersTypes = null;
		if (_ConfigurationManager.isUserPermissionsActive())
			roleAllowedEncountersTypes = ServiceHelper.getEncounterManagerService().getCurrentRoleEncounterIds();
		else
			roleAllowedEncountersTypes = allEncountersTypes;
		if (roleAllowedEncountersTypes != null) {
			ArrayList<String> encounterTypes = new ArrayList<String>(allEncountersTypes.size()); // TN119
			// allocate just what is necessary
			// and initiate array, otherwise get a IndexOutOfBoundsException
			for (int i = 0; i < allEncountersTypes.size(); i++) {
				encounterTypes.add("");
			}
			try {
				// TN119 sort encounters according to order
				for (String encounterType : allEncountersTypes) {
					encounterType = encounterType.replace(CommonIpdConstants.ENCOUNTER_TYPE_FILENAME_PREFIX, "");
					// get the value order
					String[] keyOrder = encounterType.split(CommonConstants.PROPS_STRING_SEPARATOR);
					String encounter = keyOrder[1];
					int order = Integer.parseInt(keyOrder[2]) - 1;
					encounterTypes.set(order, encounter);
				}
				// TN148 allowing encounters according to userrole
				if (!_ConfigurationManager.isUserPermissionsActive()) 
					return encounterTypes;
					Iterator<String> it = encounterTypes.iterator();
					while (it.hasNext()) {
						String encounterType = it.next();
						if (!roleAllowedEncountersTypes.contains(encounterType)) {
							it.remove();
						}
					}

				return encounterTypes;
			} catch (Exception e) {
				e.printStackTrace();
				throw new ConfigException("Configuration problem with " + CommonIpdConstants.ENCOUNTER_TYPE_FILENAME_PREFIX, e);
			}
		}
		return null;
	}

	private void selectFirstLine() {
		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {
				if (getTable().getRowCount() > 0) {
					getTable().setRowSelectionInterval(getTable().getRowCount() - 1, getTable().getRowCount() - 1);
					getTable().changeSelection(0, EncounterListTableModel.FIELDINDEX_DATE, false, false);
					if (getTable().getCellEditor() != null)
						getTable().getCellEditor().cancelCellEditing();
				}
				getDescriptor().getWizard().setNextFinishButtonEnabled(getTable().getRowCount() > 0 && getTable().getSelectedRow() >= 0);

			}
		};
		// run it not in the current stack
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
				getTable().changeSelection(getTable().getRowCount() - 1, EncounterListTableModel.FIELDINDEX_DATE, false, false);
				getTable().editCellAt(getTable().getRowCount() - 1, EncounterListTableModel.FIELDINDEX_DATE);
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

	/**
	 * 
	 * @param id
	 * @return -1 if error, 0 if the encounter didn't existed, 1 if encounter deleted
	 */
	private int deleteEncounter(Long id) {

		try {
			Encounter encounter = ServiceHelper.getEncounterManagerService().getEncounter(id);

			if (encounter != null) {
				// remove all related to Encounter
				ServiceHelper.getEncounterManagerService().removeEncounter(encounter);
				return 1;
			}

		} catch (Exception e) {
			// e.printStackTrace();

		}
		return -1;
	}

	// the model should be updated
	public void setDataModel(ArrayList<EncounterModelData> modelValues, String[] tableTitles) {

		_MyTableModel.setModelValues(modelValues, tableTitles);
		// change col titles
		changeTableColumnTitles(tableTitles);
	}

	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {

				DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();

				int rowImpacted = selectionModel.getMinSelectionIndex();
				// save the Encounter selection
				EncounterModelData encounterModelData = getTableModel().getModelValues().get(rowImpacted);
				_ConfigurationManager.setCurrentEncounterModelData(encounterModelData);

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

	/*
	 * the user just made a modification on a cell
	 */
	@Override
	// TableModelListener
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();
		if (column != TableModelEvent.ALL_COLUMNS) {
			TableModel model = (TableModel) e.getSource();

			// TN111
			boolean allowed = false;
			int columnIndex = EncounterListTableModel.FIELDINDEX_DATE;
			Object colData = model.getValueAt(row, columnIndex);
			if (colData != null && !colData.toString().equals(""))
				allowed = true;
			getDescriptor().getWizard().setNextFinishButtonEnabled(allowed);

			// alway disable new Encounter when in modification
			_NewButton.setEnabled(false);

			// enable anyway as there is now a modification just made
			_ConfigurationManager.setNewEncounterDataInsertion(true);
			_CancelEditButton.setEnabled(true);

			// flag data modification...
			getEntryFormConfigurationManager().setEncounterDataSaved(false);
			// ...and row number
			_MyTableModel.setLineNumberEditable(row);

		}

	}

	public void setDescriptor(EncounterListFormDescriptor encounterConsultationListFormDescriptor) {
		_Descriptor = encounterConsultationListFormDescriptor;

	}

	public boolean isSelectionEmpty() {
		return _Table.getSelectionModel().isSelectionEmpty();
	}

	public EncounterListTableModel getTableModel() {
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
	public void configurePanelStartState(boolean patientChanged) {

		// configure the panel

		internationalize(); // TN97

		_NewButton.setEnabled(_ConfigurationManager.isEncounterDataSaved());

		if (getTable().getRowCount() > 0) {
			// enable cancel if in insertion and not saved
			_CancelEditButton.setEnabled(_ConfigurationManager.isNewEncounterDataInsertion() && !_ConfigurationManager.isEncounterDataSaved());

			// allow to delete if not in insertion mode
			_DeleteButton.setEnabled(!_ConfigurationManager.isNewEncounterDataInsertion());
		} else {
			_DeleteButton.setEnabled(false);
			_CancelEditButton.setEnabled(false);
		}

		if (_ConfigurationManager.isEncounterDataSaved()) {
			getTableModel().setLineNumberEditable(null);
		}
		// allow any row selection
		if (_ConfigurationManager.isEncounterDataSaved() && patientChanged) {
			// if several lines and none selected, select first one
			if (getTable().getRowCount() > 0) {
				getTable().setRowSelectionInterval(0, 0);
				getTable().changeSelection(0, 0, false, false);
			}
		}

		getDescriptor().getWizard().setNextFinishButtonEnabled(getTable().getRowCount() > 0 && getTable().getSelectedRow() >= 0);
		getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isEncounterDataSaved());

	}

	/**
	 * TN97
	 */
	protected void internationalize() {

		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_NEWCOMMAND);
		_NewButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		_DeleteButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelEditButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton.setText(mess);
	}

	public void fillModelValue(EncounterModelData modelValue, Encounter encounter) {

		Date date = encounter.getDate();
		modelValue.setDate(date);
		// modelValue.setDate(_Sdf.format(date));
		// modelValue.setPlace(encounter.getPlace());
		modelValue.setEncounterType(encounter.getType());
		String encounterLabel = _ConfigurationManager.getEncounterLabel(encounter.getType());
		// modelValue.setType(encounter.getType());
		modelValue.setTypeLabel(encounterLabel);

	}

	@Override
	// TN97
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_ENCOUNTERLISTPANEL;
	}
}
