package ch.msf.form.wizard;

import java.awt.BorderLayout;
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
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import javax.persistence.NoResultException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import ch.msf.form.MyDateFieldEditor3;
import ch.msf.form.ParamException;
import ch.msf.form.config.PatientListTableModel;
import ch.msf.form.config.PatientListTableModel.PatientModelData;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.manager.PatientManagerImpl;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.PatientIdentifier;
import ch.msf.model.SelectionContext;
import ch.msf.service.ServiceHelper;
import ch.msf.util.KeyValue;
import ch.msf.util.StackTraceUtil;

public class PatientListFormPanel extends AbstractWizardPanel implements ActionListener, TableModelListener {

	private static final long serialVersionUID = 1L;

	private JButton _NewButton; //
	private JButton _DeleteButton; //
	private JButton _CancelEditButton; //
	private JButton _HelpButton; // TN106

	private JScrollPane _ScrollPane;

	protected PatientListTableModel _MyTableModel;

	private JTable _Table;

	// TN9
	private JLabel _FinderPatientLabel; // patient surname/name/id label
	private JTextField _FinderPatientSearch; // patient surname/name/id search
												// text
	// private JLabel _FinderPatientNameLabel; // patient name label
	// private JTextField _FinderPatientNameSearch; // patient name
	private JButton _FindButton; //
	private JButton _ClearSearchButton; //
	private JButton _NextPageSearchButton;
	private JButton _BackPageSearchButton;

	// private int _CurrentPatientLineNumberSelection; // the current selection

	// TN134
	private int _PageIndex = 1;

	private JPanel _FinderPanel;

	public PatientListFormPanel() {
		super();
	}

	public JPanel getContentPanel() {

		// pass a null model, the values will be passed by the descriptor
		_MyTableModel = new PatientListTableModel(null);

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

		// patient info
		JPanel curPatientPanel = new JPanel();
		curPatientPanel.setLayout(new BorderLayout());
		Dimension curPatientDim = new Dimension(PATIENT_INFO_PANEL_WIDTH, PATIENT_INFO_PANEL_HEIGHT);
		curPatientPanel.setMinimumSize(curPatientDim);
		curPatientPanel.setMaximumSize(curPatientDim);
		curPatientPanel.setPreferredSize(curPatientDim);
		curPatientPanel.setSize(curPatientDim);
		excercicePanel.add(curPatientPanel);
		// excercicePanel.setBorder(BorderFactory.createLineBorder(Color.green));

		// curPatientPanel.setBorder(BorderFactory.createLineBorder(Color.magenta));

		_FinderPanel = new JPanel();
		Dimension curFinderDim = new Dimension(PATIENT_INFO_PANEL_WIDTH, 2 * CELL_HEIGHT);
		_FinderPanel.setMinimumSize(curFinderDim);
		_FinderPanel.setMaximumSize(curFinderDim);
		_FinderPanel.setPreferredSize(curFinderDim);
		_FinderPanel.setSize(curFinderDim);
		curPatientPanel.add(_FinderPanel, BorderLayout.NORTH);
		_FinderPanel.setBorder(BorderFactory.createLineBorder(Color.gray));

		// add surname label
		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_FINDLABEL);
		_FinderPatientLabel = new JLabel(mess);
		_FinderPanel.add(_FinderPatientLabel);
		// add search surname
		Dimension inputDim = new Dimension(FINDER_TEXT_WIDTH, CELL_HEIGHT);
		_FinderPatientSearch = new JTextField();
		_FinderPanel.add(_FinderPatientSearch);
		_FinderPatientSearch.setSize(inputDim);
		_FinderPatientSearch.setMinimumSize(inputDim);
		_FinderPatientSearch.setMaximumSize(inputDim);
		_FinderPatientSearch.setPreferredSize(inputDim);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_FINDCOMMAND);
		_FindButton = new JButton(mess);
		_FindButton.addActionListener(this);
		_FinderPanel.add(_FindButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CLEARCOMMAND);
		_ClearSearchButton = new JButton(mess);
		_ClearSearchButton.addActionListener(this);
		_FinderPanel.add(_ClearSearchButton);

		// TN134
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_SEARCHBACKCOMMAND) + " " + PatientManagerImpl.SEARCH_ITEMS_MAX_SIZE;
		_BackPageSearchButton = new JButton(mess);
		_BackPageSearchButton.addActionListener(this);
		_FinderPanel.add(_BackPageSearchButton);
		_BackPageSearchButton.setEnabled(false);
		//
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_SEARCHNEXTCOMMAND) + " " + PatientManagerImpl.SEARCH_ITEMS_MAX_SIZE;
		_NextPageSearchButton = new JButton(mess);
		_NextPageSearchButton.addActionListener(this);
		_FinderPanel.add(_NextPageSearchButton);

		_CurPatientInfo = new JLabel(); // TN78
		curPatientPanel.add(_CurPatientInfo, BorderLayout.SOUTH);
		_CurPatientInfo.setText("");

		// Set up renderer
		final TableCellRenderer myDateRenderer = new DateRenderer();

		final DefaultCellEditor dateCellEditor = new MyDateFieldEditor3(null, null) {
			public boolean isCellEditable(EventObject anEvent) {//TN139 TN140
				return commonIsCellEditable(anEvent);
			}
		};

		final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new JTextField()) {
			public boolean isCellEditable(EventObject anEvent) {//TN139 TN140
				return commonIsCellEditable(anEvent);
			}
		};


		// manage combo value in list
		// String comboSex = "sex";
		// JComboBox comboBox = ComboEntityList.buildComboboxModelValue(
		// _ConfigurationManager, comboSex);
		// final DefaultCellEditor sexCellEditor = new
		// DefaultCellEditor(comboBox);

		// we should always define a cell editor and a cell renderer
		_Table = new JTable(_MyTableModel) {
			public void changeSelection(final int row, final int column, boolean toggle, boolean extend) {
				super.changeSelection(row, column, toggle, extend);

				// stay on same row...(tab at last new cell)
				if (_MyTableModel.getLineNumberEditable() != null && row != _MyTableModel.getLineNumberEditable()) {
					changeSelection(_MyTableModel.getLineNumberEditable(), column, toggle, extend);
				}

			}

			public TableCellEditor getCellEditor(int row, int column) {
				if ((column == PatientListTableModel.FIELDINDEX_BIRTHDATE)) {
					return dateCellEditor;
				}
				if (column == PatientListTableModel.FIELDINDEX_SEX)
					return ((PatientListTableModel) getModel()).getSexEditor();
				return defaultCellEditor;
			}

			// write the date according same format their are entered
			public TableCellRenderer getCellRenderer(int row, int column) {
				// Object obj = null;
				// we save the current row which is in use, to return the
				// correct object class in the model
				if ((column == PatientListTableModel.FIELDINDEX_BIRTHDATE)) {

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

					if (realColumnIndex == PatientListTableModel.FIELDINDEX_ID) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTID);
					} else if (realColumnIndex == PatientListTableModel.FIELDINDEX_SURNAME || realColumnIndex == PatientListTableModel.FIELDINDEX_NAME) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTNAME);
					} else if (realColumnIndex == PatientListTableModel.FIELDINDEX_SEX) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTSEX);
					} else if (realColumnIndex == PatientListTableModel.FIELDINDEX_BIRTHDATE) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTBIRTHDAY);
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
		String[] tableTitles = getEntryFormConfigurationManager().getTableTitleTranslation("patientList");
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

		// register the change listener (useful for the new patient)
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

		_ScrollPane.setBorder(BorderFactory.createLineBorder(Color.gray));

		// buttons
		JPanel buttonPanel = new JPanel();
		Dimension buttonDim = new Dimension(BUTTON_PANEL_WIDTH, BUTTON_PANEL_HEIGHT);
		buttonPanel.setMinimumSize(buttonDim);
		buttonPanel.setMaximumSize(buttonDim);
		buttonPanel.setPreferredSize(buttonDim);
		buttonPanel.setSize(buttonDim);
		excercicePanel.add(buttonPanel);

		// buttonPanel.setBorder(BorderFactory.createLineBorder(Color.red));
		final int NBR_BUTTONS = 4; //
		buttonPanel.setLayout(new GridLayout(1, NBR_BUTTONS, 0, 3));// nbr

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_NEWCOMMAND);
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
		String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTNEW);
		_NewButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTDELETE);
		_DeleteButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTCANCEL);
		_CancelEditButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTFIND);
		_FindButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTCLEAR);
		_ClearSearchButton.setToolTipText(tip);

		return contentPanelMaster;
	}
	
	/**
	 * cell editor common mouse management
	 * @param anEvent
	 * @return
	 *///TN139 TN140
	private boolean commonIsCellEditable(EventObject anEvent) {
		if (anEvent instanceof MouseEvent) {
			boolean inEdition = ((MouseEvent) anEvent).getClickCount() >= 2; //clickCountToStart;
			if (inEdition){
				changeComponentsModifState(false);
			}

			return inEdition;
		}
		return true;
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
		
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (_MyTableModel.getLineNumberEditable() != null && row == _MyTableModel.getLineNumberEditable() && column == PatientListTableModel.FIELDINDEX_BIRTHDATE)
				cell.setBackground(Color.pink);
			else if (!isSelected)
				cell.setBackground(null);

			return cell;
		}
	}

	/**
	 * a button is pressed
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == _HelpButton) { // TN106
			String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTLISTHELP);
			JOptionPane.showMessageDialog(null, tip, "", JOptionPane.PLAIN_MESSAGE, null);

		} else if (e.getSource() == _NewButton) { // edit
			System.out.println("actionPerformed NEW");

			// TN123 update the patient list
			((PatientListFormDescriptor) getDescriptor()).updatePatientList();

			PatientModelData patientModelData = new PatientListTableModel().new PatientModelData();
			// patientModelData.setId(Constants.NEW_PATIENT_ENTERED);
			// update the model
			_MyTableModel.addModelValue(patientModelData);
			_MyTableModel.fireTableRowsInserted(0, 0);

			selectNewInsertedLineAndFocus();

			// flag data modification...
			getEntryFormConfigurationManager().setPatientDataSaved(false);
			// ...and row number
			_MyTableModel.setLineNumberEditable(_MyTableModel.getRowCount() - 1);

			getDescriptor().getWizard().setNextFinishButtonEnabled(false);

			_ConfigurationManager.setNewPatientDataInsertion(true);
			// disable new
			_NewButton.setEnabled(false);
			// never disable cancel...
			_CancelEditButton.setEnabled(true);
			// disable delete if new enabled
			_DeleteButton.setEnabled(false);

		} else if (e.getSource() == _DeleteButton) { // edit
			System.out.println("actionPerformed DELETE");

			String errMessCancel = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_CANCEL);
			String messConfirmDelete = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CONFIRMDELETE);
			Object[] options = { "OK", errMessCancel };

			int retCode = JOptionPane.showOptionDialog(null, messConfirmDelete, "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[1]);

			if (retCode == 0) {

				PatientModelData patientModelData = getTableModel().getModelValues().get(getTable().getSelectedRow());
				int patientDeletedRetCode = deletePatient(patientModelData.getId());
				//
				if (patientDeletedRetCode >= 0) {// if new insertion deleted

				}
				// reset saved flag
				getEntryFormConfigurationManager().setPatientDataSaved(true);

				// remove the row from the model and the table
				_MyTableModel.removeModelValue(patientModelData);
				_MyTableModel.fireTableRowsDeleted(getTable().getSelectedRow(), getTable().getSelectedRow());
				configurePanelStartState(true);
			}

		} else if (e.getSource() == _CancelEditButton) { // cancel edit

			doCancelPatient();

		} else if (e.getSource() == _FindButton) { // TN9 search with criteria
			// get the list of patient contexts
			String date = null;
			String idOrName = _FinderPatientSearch.getText();
			// search on id and name
//			boolean testOnId = true;
			List<PatientContext> patientContexts = getPatientContexts(date, idOrName/*, testOnId*/);
			TreeSet<PatientContext> patientSet = new TreeSet<PatientContext>(patientContexts);
			String searchConceptId = _ConfigurationManager.getPatientSearchConceptId();
			if (searchConceptId != null){ //TN142 search on patient field too
			List<PatientContext> patientContext2s = getPatientContexts(date, idOrName, searchConceptId);
			patientSet.addAll(patientContext2s);
			}
			
			if (patientSet.size() > 0) { // invalid param, do nothing

				// if (patientContexts.size() > 0) {
				// ...and update the view,
				updateModelView(new ArrayList<PatientContext>(patientSet));
				// _MyTableModel.fireTableDataChanged();
				selectFirstLine();
				// }
			}

			// configurePanelStartState(true);
		} else if (e.getSource() == _ClearSearchButton) { // TN9 search all
			// clear the search text value
			_FinderPatientSearch.setText("");

			// get the list of patient contexts
			String date = null;
			String idOrName = null;
			boolean testOnId = true;
			_PageIndex = 1;
			_BackPageSearchButton.setEnabled(false);
			List<PatientContext> patientContexts = getPatientContexts(date, idOrName, testOnId, _PageIndex);

			// ...and update the view,
			updateModelView(patientContexts);
			selectFirstLine();

		} else if (e.getSource() == _NextPageSearchButton || e.getSource() == _BackPageSearchButton) { // TN134 search
			// get the list of patient contexts
			String date = null;
			String idOrName = (_FinderPatientSearch.getText().length() > 0) ? _FinderPatientSearch.getText() : null;
			boolean testOnId = true;
			if (e.getSource() == _NextPageSearchButton) {
				_PageIndex++;
			} else
				_PageIndex--;
			if (_PageIndex == 0)
				_PageIndex = 1;
			boolean buttonEnabled = _PageIndex > 1;
			_BackPageSearchButton.setEnabled(buttonEnabled);
			List<PatientContext> patientContexts = getPatientContexts(date, idOrName, testOnId, _PageIndex);

			buttonEnabled = !(patientContexts != null && patientContexts.size() == 0 && e.getSource() == _NextPageSearchButton);
			_NextPageSearchButton.setEnabled(buttonEnabled);
			if (!buttonEnabled)
				_PageIndex--;

			// ...and update the view,
			if (patientContexts != null && patientContexts.size() > 0)
				updateModelView(patientContexts);
			selectFirstLine();
		}

		else
			System.out.println(e.getSource());
	}

	public void doCancelPatient() {
		// restore original value
		PatientModelData patientModelData = getTableModel().getModelValues().get(getTable().getSelectedRow());

		Patient patient = readPatient(patientModelData.getPatientContextId());

		if (patient != null) {
			// update the list model
			fillModelValue(patientModelData, patient);
			_MyTableModel.fireTableRowsInserted(0, 0);

			// update the list of concepts
			// just need to remove it from cache, it will be reloaded next
			// screen
			_ConfigurationManager.getPatientContextCache().remove(patientModelData.getPatientContextId());

		} else {
			// patient is null, then we just inserted a new line normally

			// cancel the editor focus set when new line added
			// (editor.requestFocusInWindow())
			if (getTable().getCellEditor() != null) // TN112
				getTable().getCellEditor().cancelCellEditing();

			// update the model by removing the inserted line
			// remove the row from the model and the table
			_MyTableModel.removeModelValue(patientModelData);
			_MyTableModel.fireTableRowsDeleted(getTable().getSelectedRow(), getTable().getSelectedRow());

			if (getTable().getRowCount() > 0) {
				// select 1rst line
				selectFirstLine();
			}
		}

		// reset saved flag
		getEntryFormConfigurationManager().setPatientDataSaved(true);
		getEntryFormConfigurationManager().setNewPatientDataInsertion(false);

		// restore original panel situation
		configurePanelStartState(false);

		// change line to remove the red background modification
		if (getTable().getRowCount() > 0) {
			getTable().setRowSelectionInterval(0, 0);
		}

	}

	private void selectNewInsertedLineAndFocus() {
		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {
				getTable().setRowSelectionInterval(getTable().getRowCount() - 1, getTable().getRowCount() - 1);
				getTable().changeSelection(getTable().getRowCount() - 1, PatientListTableModel.FIELDINDEX_ID, false, false);
				boolean ret = getTable().editCellAt(getTable().getRowCount() - 1, PatientListTableModel.FIELDINDEX_ID);
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

	private void selectFirstLine() {
		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {
				if (getTable().getRowCount() > 0) {
					getTable().setRowSelectionInterval(getTable().getRowCount() - 1, getTable().getRowCount() - 1);
					getTable().changeSelection(0, PatientListTableModel.FIELDINDEX_ID, false, false);
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

	/**
	 * search on patients
	 * 
	 * @param selectionContext
	 * @param date
	 * @param idOrName
	 * @return a list of patientcontexts that matches with passed criteria or null if parameter problem
	 */
	private List<PatientContext> getPatientContexts(String date, String idOrName/*, boolean testOnId*/) {
		List<PatientContext> patientContexts = null;
		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();
		try {
			patientContexts = ServiceHelper.getPatientManagerService().getAllSelectedPatientContext(selectionContext, date, idOrName/*, testOnId*/);
		} catch (ParamException e) {
			// get the error message
			String errMessNumber = e.getMessage();
			int errorCode = -1;
			String errMess = "";
			try {
				errorCode = Integer.parseInt(errMessNumber);
				errMess = ServiceHelper.getMessageService().getMessage(errorCode);
			} catch (NumberFormatException n) {
				System.out.println("Programming problem? (no number passed to error!)");
			}
			errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED) + ", " + errMess;

			System.out.println(errMess);
			JOptionPane.showMessageDialog(null, errMess, "", JOptionPane.OK_OPTION, null);

		}
		return patientContexts;
	}
	
	/**
	 * find patient on one of their attribute
	 * @param date
	 * @param idOrName
	 * @param patientConcept
	 * @return a list of patientcontexts that matches with passed criteria or null if parameter problem
	 */
	private List<PatientContext> getPatientContexts(String date, String idOrName, String patientConcept) {
		List<PatientContext> patientContexts = null;
		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();
		try {
			patientContexts = ServiceHelper.getPatientManagerService().getAllSelectedPatientContext(selectionContext, date, idOrName, patientConcept);
		} catch (ParamException e) {
			// get the error message
			String errMessNumber = e.getMessage();
			int errorCode = -1;
			String errMess = "";
			try {
				errorCode = Integer.parseInt(errMessNumber);
				errMess = ServiceHelper.getMessageService().getMessage(errorCode);
			} catch (NumberFormatException n) {
				System.out.println("Programming problem? (no number passed to error!)");
			}
			errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED) + ", " + errMess;

			System.out.println(errMess);
			JOptionPane.showMessageDialog(null, errMess, "", JOptionPane.OK_OPTION, null);

		}
		return patientContexts;
	}
	
	
	

	private List<PatientContext> getPatientContexts(String date, String idOrName, boolean testOnId, int pageNumber) {
		List<PatientContext> patientContexts = null;
		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();
		try {
			patientContexts = ServiceHelper.getPatientManagerService().getAllSelectedPatientContext(selectionContext, date, idOrName, testOnId, pageNumber);
		} catch (ParamException e) {
			// get the error message
			String errMessNumber = e.getMessage();
			int errorCode = -1;
			String errMess = "";
			try {
				errorCode = Integer.parseInt(errMessNumber);
				errMess = ServiceHelper.getMessageService().getMessage(errorCode);
			} catch (NumberFormatException n) {
				System.out.println("Programming problem? (no number passed to error!)");
			}
			errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED) + ", " + errMess;

			System.out.println(errMess);
			JOptionPane.showMessageDialog(null, errMess, "", JOptionPane.OK_OPTION, null);

		}
		return patientContexts;
	}

	// fill the tablemodel with patient context data
	public void updateModelView(List<PatientContext> patientContexts) {

		// clear the patient cache
		_ConfigurationManager.getPatientContextCache().clear();

		// manage combo value in list
		updateModelEditor();

		ArrayList<PatientListTableModel.PatientModelData> modelValues = new ArrayList<PatientListTableModel.PatientModelData>();
		if (patientContexts != null)
			for (PatientContext patientContext : patientContexts) {
				PatientListTableModel.PatientModelData modelValue = new PatientListTableModel().new PatientModelData();
				Patient patient = patientContext.getPatient();
				// fill the model with patient data
				fillModelValue(modelValue, patient);
				modelValues.add(modelValue);

				// add caching purposes info
				modelValue.setPatientContextId(patientContext.getId());

				_ConfigurationManager.getPatientContextCache().put(patientContext.getId(), patientContext);
			}

		// update table titles
		String[] tableTitles = _ConfigurationManager.getTableTitleTranslation("patientList");
		setDataModel(modelValues, tableTitles);
		_MyTableModel.fireTableDataChanged();
	}

	private Patient readPatient(Long patientContextId) {

		// look for patient

		PatientContext patientContext = null;
		try {
			patientContext = ServiceHelper.getPatientManagerService().getPatientContext(patientContextId);
		} catch (NoResultException e) {
			// no entries for that id, return null
		}

		if (patientContext != null)
			return patientContext.getPatient();

		return null;
	}

	/**
	 * 
	 * @param id
	 * @return -1 if error, 0 if the patient didn't existed, 1 if patient deleted
	 */
	private int deletePatient(String id) {

		// look for patient
		List<PatientContext> patientContexts = null;
		PatientContext currentPatientContext = null;

		String idPatientWithContext = _ConfigurationManager.buildPatientContextId(id);

		PatientIdentifier patientIdentifier = new PatientIdentifier(idPatientWithContext, CommonIpdConstants.IDENTITY_TYPE_OTHER);
		try {
			patientContexts = ServiceHelper.getPatientManagerService().getSelectedPatient(patientIdentifier, false);

			if (patientContexts == null || patientContexts.size() > 1) {
				// this should never happen as there is a uniq db constraint
				// on
				// the id
				String errMess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ID_ALREADY_EXIST);
				JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + "There are several patients with this id, " + errMess, "Error!", JOptionPane.OK_OPTION, null);
				return -1;
			}

			currentPatientContext = patientContexts.get(0);
			// remove all related to patient
			ServiceHelper.getPatientManagerService().removeSelectedPatientContext(currentPatientContext);
			return 1;

		} catch (NoResultException e) {
			// the patient does not exist yet, it was created and deleted
			String errMess = "deletePatient: Patient not found with id " + id;
			System.out.println("deletePatient: Patient not found with id " + id);
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(e), errMess, JOptionPane.OK_OPTION, null);

			return 0;

		}
	}

	// the model should be updated
	public void setDataModel(ArrayList<PatientModelData> modelValues, String[] tableTitles) {

		// // manage combo value in list
		// String comboSex = "sex";
		// JComboBox comboBox =
		// ComboEntityList.buildComboboxModelValue(_ConfigurationManager,
		// comboSex);
		// ((PatientListTableModel)_MyTableModel).setSexEditor(comboBox);
		//
		_MyTableModel.setModelValues(modelValues, tableTitles);
		// // change col titles
		changeTableColumnTitles(tableTitles);
	}

	/**
	 * activated when new row added or when user change patient
	 * 
	 * @author cmi
	 * 
	 */
	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {

				if (_MyTableModel.getLineNumberEditable() != null) { // new line
																		// mode
					// save patient id

					DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();
					int rowImpacted = selectionModel.getMinSelectionIndex();
					if (rowImpacted == -1)
						rowImpacted = 0;

					// save the patient selection
					PatientModelData patientModelData = getTableModel().getModelValues().get(rowImpacted);
					_ConfigurationManager.setCurrentPatientModelData(patientModelData);

					// if user changed row, go back to the row
					if (rowImpacted != _MyTableModel.getLineNumberEditable()) {

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

			if (column == PatientListTableModel.FIELDINDEX_ID) {
				// id has changed
				// since id has changed the contxt id should be null
				if (_ConfigurationManager.getCurrentPatientModelData() != null) {
					_ConfigurationManager.getCurrentPatientModelData().setOldId(_ConfigurationManager.getCurrentPatientModelData().getId());
				}

				// check that id does not exist yet
				boolean alreadyExist = checkPatientAlreadyExist(model, row, column, CommonIpdConstants.IDENTITY_TYPE_OTHER);
				if (alreadyExist) {
					String idPatient = (String) model.getValueAt(row, column);
					showError(idPatient);
					return;
				}
			}

			// TN73 allow next button if ID and sex are filled
			boolean allowed = false;
			int columnIndex = PatientListTableModel.FIELDINDEX_ID;
			Object colData = model.getValueAt(row, columnIndex);
			if (colData != null && !colData.toString().equals(""))
				allowed = true;
			if (allowed) {
				allowed = false;
				columnIndex = PatientListTableModel.FIELDINDEX_SEX;
				colData = model.getValueAt(row, columnIndex);
				if (colData != null && !colData.toString().equals(""))
					allowed = true;
			}
			getDescriptor().getWizard().setNextFinishButtonEnabled(allowed);
			// always disable new patient when in modification
			// always disable back button when in modification
			// always hide research when in modification
			changeComponentsModifState(false);

			// enable cancel as we are in modification
			_ConfigurationManager.setNewPatientDataInsertion(true);
			_CancelEditButton.setEnabled(true);

			// flag data modification...
			getEntryFormConfigurationManager().setPatientDataSaved(false);
			// ...and row number
			_MyTableModel.setLineNumberEditable(row);
		}
	}

	// TN139 // TN140
	/**
	 * 
	 * @param state : false when in patient in modification, true otherwise
	 */
	private void changeComponentsModifState(boolean state) {
		_NewButton.setEnabled(state);
		// always hide research when in modification
		_FinderPanel.setVisible(state);
		// always disable back button when in modification
		getDescriptor().getWizard().setBackButtonEnabled(state);
		
		// enable cancel as we are in modification
		_ConfigurationManager.setNewPatientDataInsertion(!state);
		_CancelEditButton.setEnabled(!state);
	}

	private void showError(final String idPatient) {

		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {

				String errMess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ID_ALREADY_EXIST);
				JOptionPane.showMessageDialog(null, errMess + "! " + idPatient, "Error", JOptionPane.ERROR_MESSAGE, null);

				// set selection to the correct defect cell
				getTable().setRowSelectionInterval(getTable().getRowCount() - 1, getTable().getRowCount() - 1);
				getTable().changeSelection(getTable().getRowCount() - 1, PatientListTableModel.FIELDINDEX_ID, false, false);
				boolean ret = getTable().editCellAt(getTable().getRowCount() - 1, PatientListTableModel.FIELDINDEX_ID);
				Component editor = getTable().getEditorComponent();
				editor.requestFocusInWindow();
			}
		};
		// do not run it in the current stack
		try {

			SwingUtilities.invokeLater(selectNewInsertedLine);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(ex), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		}
	}

	/**
	 * 
	 * @param model
	 * @param row
	 * @param column
	 * @param identityTypeOther
	 * @return true if the patient identified by the content of the table in row, column already exists
	 */
	private boolean checkPatientAlreadyExist(TableModel model, int row, int column, int identityTypeOther) {
		// the patient id is on first col
		if (column != PatientListTableModel.FIELDINDEX_ID)
			return false;
		String idPatientToCheck = (String) model.getValueAt(row, column);

		if (idPatientToCheck == null || idPatientToCheck.toString().equals(""))
			return false;

		// first just check in the model
		for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
			String idPatient = (String) model.getValueAt(rowIndex, column);
			if (rowIndex != row && idPatient.equals(idPatientToCheck))
				return true;
		}

		// then check in db...
		// check if new insert
		if (_ConfigurationManager.isNewPatientDataInsertion()) {
			// check if it exists in db

			String idPatientWithContext = _ConfigurationManager.buildPatientContextId(idPatientToCheck);

			PatientIdentifier newPatientIdentifier = new PatientIdentifier(idPatientWithContext, identityTypeOther);
			List<PatientContext> patientContexts = null;
			try {
				patientContexts = ServiceHelper.getPatientManagerService().getSelectedPatient(newPatientIdentifier, null);
			} catch (NoResultException e) {
				// normal case
			}

			if (patientContexts != null && patientContexts.size() > 0) {
				return true;
			}
		}
		return false;
	}

	public boolean isSelectionEmpty() {
		return _Table.getSelectionModel().isSelectionEmpty();
	}

	public PatientListTableModel getTableModel() {
		return _MyTableModel;
	}

	public JTable getTable() {
		return _Table;
	}

	public class MyCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (_MyTableModel.getLineNumberEditable() != null && row == _MyTableModel.getLineNumberEditable() && column != PatientListTableModel.FIELDINDEX_INDEX)
				cell.setBackground(Color.pink);
			else if (!isSelected)
				cell.setBackground(null);

			return cell;
		}
	}

	/**
	 * called by descriptor so that the panel can be initialized
	 * 
	 * @param contextChanged
	 */
	public void configurePanelStartState(boolean contextChanged) {

		// configure the panel
		internationalize(); // TN97

		changeComponentsModifState(_ConfigurationManager.isPatientDataSaved());// TN139//TN140

		if (getTable().getRowCount() > 0) {
			// disable cancel if new disabled and conversely
			_CancelEditButton.setEnabled(_ConfigurationManager.isNewPatientDataInsertion() && !_ConfigurationManager.isPatientDataSaved());

			// allow to delete if not in insertion mode
			_DeleteButton.setEnabled(!_ConfigurationManager.isNewPatientDataInsertion());
		} else {
			_DeleteButton.setEnabled(false);
			_CancelEditButton.setEnabled(false);
		}

		if (_ConfigurationManager.isPatientDataSaved()) {
			getTableModel().setLineNumberEditable(null);
		}

		// allow any row selection
		if (_ConfigurationManager.isPatientDataSaved() && contextChanged) {
			// if several lines and none selected, select first one
			if (getTable().getRowCount() > 0 /*
											 * && getTable().getSelectedRow() == -1
											 */) {

				getTable().setRowSelectionInterval(0, 0);
				getTable().changeSelection(0, PatientListTableModel.FIELDINDEX_ID, false, false);
			}
		}

		getDescriptor().getWizard().setNextFinishButtonEnabled(getTable().getRowCount() > 0 && getTable().getSelectedRow() >= 0);

		// init back next search
		_PageIndex = 1;
		_BackPageSearchButton.setEnabled(false);
		_NextPageSearchButton.setEnabled(true);
		_FinderPatientSearch.setText("");
	}

	/**
	 * TN97
	 */
	protected void internationalize() {
		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_FINDCOMMAND);
		_FindButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_FINDLABEL);
		_FinderPatientLabel.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CLEARCOMMAND);
		_ClearSearchButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_NEWCOMMAND);
		_NewButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		_DeleteButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelEditButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_SEARCHBACKCOMMAND) + " " + PatientManagerImpl.SEARCH_ITEMS_MAX_SIZE;
		_BackPageSearchButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_SEARCHNEXTCOMMAND) + " " + PatientManagerImpl.SEARCH_ITEMS_MAX_SIZE;
		_NextPageSearchButton.setText(mess);

		String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTFIND);
		_FindButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTCLEAR);
		_ClearSearchButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTNEW);
		_NewButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTDELETE);
		_DeleteButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTCANCEL);
		_CancelEditButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_NEXTPAGEHELP);
		_BackPageSearchButton.setToolTipText(tip);
		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_BACKPAGEHELP);
		_NextPageSearchButton.setToolTipText(tip);

	}

	/**
	 * fill the model with patient data
	 * 
	 * @param modelValue
	 * @param patient
	 */
	public void fillModelValue(PatientModelData modelValue, Patient patient) {
		String patientId = "";
		for (PatientIdentifier patientIdentifier : patient.getPatientIdentifiers()) {
			patientId = patientIdentifier.getIdentifier();
		}
		modelValue.setIndex(patient.getIndex().toString());
		// get rid of context prefix
		int index = patientId.indexOf(CommonConstants.PATIENTID_STRING_SEPARATOR);
		if (index != -1) {
			patientId = patientId.substring(index + 1);
		}
		modelValue.setId(patientId);
		modelValue.setSurname(patient.getFamilyName());
		modelValue.setName(patient.getFirstName());

		// sex part
		String sexVal = patient.getSex();
		String comboSex = "sex";
		modelValue.setSex(ComboEntityList.getEntityForModel(comboSex, sexVal));

		// // set the age
		modelValue.setBirthDate(patient.getBirthDate());
		// // set the age from the concept value
		// ArrayList<KeyValue> keyValueAttributeMappings = _ConfigurationManager
		// .getQuestionIdToClassAttributes(Patient.class, null);
		//
		// String conceptIdAge = null;
		// for (KeyValue keyValueAttributeMapping : keyValueAttributeMappings) {
		// if (keyValueAttributeMapping._Value.equals("Age")) {
		// conceptIdAge = keyValueAttributeMapping._Key;
		// break;
		// }
		// }
		// boolean found = false;
		// if (conceptIdAge != null) {
		// for (PatientIdValue patientIdValue : patient.getIdValues()) {
		// if (patientIdValue.getConceptId().equals(conceptIdAge)){
		// // found !
		// modelValue.setBirthDate(Date.parse(s)(patientIdValue.getConceptValue()));
		// found = true;
		// break;
		// }
		// }
		// }
		// if (!found){
		// // we must add the concept to the list
		// PatientIdValue piv = new PatientIdValue();
		// piv.setConceptId(conceptIdAge);
		// piv.setConceptValue("0");
		// piv.setPatient(patient);
		// patient.getIdValues().add(piv);
		//
		// }

	}

	public static class ComboEntityList {
		private static HashMap<String, ArrayList<KeyValue>> _ComboboxValueMap = null;

		public static String getModelForEntity(String comboName, String modelValue) {// read
																						// from
																						// model

			// get the id of the combobox
			ArrayList<KeyValue> values = getComboboxValueMap().get(comboName);
			for (KeyValue keyValue : values) {
				if (keyValue._Value.equals(modelValue)) {
					return keyValue._Key;
				}
			}
			System.out.println("ComboEntityList.setModelToEntity::getResults:ERROR: combo id of " + comboName + " not found!!!!");
			return "notFound!";
		}

		public static String getEntityForModel(String comboName, String patientValue) {
			// write to model get the id of the combobox
			ArrayList<KeyValue> values = getComboboxValueMap().get(comboName);
			for (KeyValue keyValue : values) {
				if (keyValue._Key.equals(patientValue)) {
					return keyValue._Value;
				}
			}

			System.out.println("ComboEntityList.setModelToEntity::setIdValues: ERROR: combo id of " + comboName + " not found!!!!");
			return "notFound!";
		}

		public static JComboBox buildComboboxModelValue(EntryFormConfigurationManagerImpl configurationManager, String comboName) {
			ComboEntityList.setComboboxValueMap(configurationManager.getAllComboLabels(Patient.class, null, false));
			JComboBox comboBox = new JComboBox();
			ArrayList<KeyValue> values = getComboboxValueMap().get(comboName);
			for (KeyValue keyValue : values) {
				comboBox.addItem(keyValue._Value);
			}
			return comboBox;
		}

		public static HashMap<String, ArrayList<KeyValue>> getComboboxValueMap() {
			return _ComboboxValueMap;
		}

		public static void setComboboxValueMap(HashMap<String, ArrayList<KeyValue>> _ComboboxValueMap) {
			ComboEntityList._ComboboxValueMap = _ComboboxValueMap;
		}
	}

	public void updateModelEditor() {
		// update sex
		// see row in Patient-FieldsIdType
		// sex Combo-sex
		String comboSex = "sex"; // = Combo-sex
		JComboBox comboBox = ComboEntityList.buildComboboxModelValue(_ConfigurationManager, comboSex);
		((PatientListTableModel) _MyTableModel).setSexEditor(comboBox);
	}

	@Override
	// TN97
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_PATIENTLISTPANEL;
	}

	@Override
	public void setPanelState() { //
		super.setPanelState();
		_PannelState = "<html>" + _PannelState + "<br><font size='3'><font color='blue'><center>("
				+ ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_STATE_PATIENTLISTPANEL2) + " " + PatientManagerImpl.SEARCH_ITEMS_MAX_SIZE + ")";
	}

	/**
	 * TN141
	 * @return 
	 */
	public boolean isFilterResults() {
		return _FinderPatientSearch.getText() != null && !_FinderPatientSearch.getText().isEmpty() ;
	}

}
