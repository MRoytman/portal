package ch.msf.form.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
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
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.form.config.VillageListTableModel;
import ch.msf.form.config.VillageListTableModel.VillageHealthAreaModelData;
import ch.msf.model.VillageArea;
import ch.msf.service.ServiceHelper;

/**
 * This is related to TN129 (presentation of villages of origin)
 * 
 * @author cmi
 * 
 */
public class VillageListFormPanel extends AbstractWizardPanel implements ActionListener, TableModelListener {

	private static final long serialVersionUID = 1L;

	// public static final int VILLAGE_INFO_PANEL_HEIGHT = CELL_HEIGHT * 2;
	public static final int VILLAGE_PANEL_WIDTH = QUESTION_ANSWER_PANEL_WIDTH;
	// private static final int VILLAGE_PANEL_HEIGHT = VILLAGE_LIST_HEIGHT +
	// NEW_VILLAGE_HEIGHT + BUTTON_PANEL_HEIGHT;
	private static final int VILLAGE_PANEL_HEIGHT = EXERCISE_PANEL_HEIGHT - BUTTON_PANEL_HEIGHT;
	private static final int NEW_VILLAGE_HEIGHT = CELL_HEIGHT * 4; // TN129
	private static final int VILLAGE_LIST_HEIGHT = VILLAGE_PANEL_HEIGHT - NEW_VILLAGE_HEIGHT - 5;// 370;
																									// //
																									// TN129

	private JButton _NewButton; //
	private JButton _DeleteButton; //
	// private JButton _CancelEditButton; //
	private JButton _HelpButton; //

	private JScrollPane _ScrollPane;

	protected VillageListTableModel _MyTableModel;

	private JTable _Table;

	private JTextField _NewVillageOrigin; //
	private JComboBox _NewArea; //

	private JLabel _VillageLabel;

	private JLabel _AreaLabel;

	// private int _CurrentVillageLineNumberSelection; // the current selection

	public VillageListFormPanel() {
		super();
	}

	public JPanel getContentPanel() {

		// pass a null model, the values will be passed by the descriptor
		_MyTableModel = new VillageListTableModel(null);

		JPanel contentPanelMaster = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));

		JPanel contentPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		contentPanelMaster.add(contentPanel1);

		// contentPanel1.setBorder(BorderFactory.createLineBorder(Color.red));

		JPanel excercicePanel = new JPanel();
		contentPanel1.add(excercicePanel);

		excercicePanel.setLayout(new BoxLayout(excercicePanel, BoxLayout.Y_AXIS));
		Dimension exerciceDim = new Dimension(EXERCISE_PANEL_WIDTH, EXERCISE_PANEL_HEIGHT);
		excercicePanel.setMinimumSize(exerciceDim);
		excercicePanel.setPreferredSize(exerciceDim);
		excercicePanel.setSize(exerciceDim);
		// excercicePanel.setBorder(BorderFactory.createLineBorder(Color.green));

		// patient info
		JPanel curVillagePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		Dimension curVillageDim = new Dimension(VILLAGE_PANEL_WIDTH, VILLAGE_PANEL_HEIGHT);
		curVillagePanel.setMinimumSize(curVillageDim);
		curVillagePanel.setMaximumSize(curVillageDim);
		curVillagePanel.setPreferredSize(curVillageDim);
		curVillagePanel.setSize(curVillageDim);
		excercicePanel.add(curVillagePanel);

		// Form entry panel
		// Dimension questionAnswerPanelDim = new
		// Dimension(QUESTION_ANSWER_PANEL_WIDTH, QUESTION_ANSWER_PANEL_HEIGHT);
		// FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 3, 3);
		// JPanel questionAnswerPanel = new JPanel(flowLayout);
		// questionAnswerPanel.setMinimumSize(questionAnswerPanelDim);
		// // questionAnswerPanel.setMaximumSize(questionAnswerPanelDim);
		// questionAnswerPanel.setSize(questionAnswerPanelDim);
		// questionAnswerPanel.setPreferredSize(questionAnswerPanelDim);

		// curVillagePanel.setBorder(BorderFactory.createLineBorder(Color.magenta));

		// // Set up renderer
		// final TableCellRenderer myDateRenderer = new DateRenderer();
		//
		// final DefaultCellEditor dateCellEditor = new MyDateFieldEditor3(null,
		// null);
		//
		// final DefaultCellEditor defaultCellEditor = new DefaultCellEditor(new
		// JTextField());

		// we should always define a cell editor and a cell renderer
		_Table = new JTable(_MyTableModel) {

		};

		// change col titles
		String[] tableTitles = getEntryFormConfigurationManager().getTableTitleTranslation("villageAreaList");
		changeTableColumnTitles(tableTitles);

		// detect any country selection change
		_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// _Table.getSelectionModel().addListSelectionListener(new
		// RowListener());

		// _Table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		// _Table.setFillsViewportHeight(true); // ?

		// column settings
		// _Table.getColumnModel().getColumn(1).setMinWidth(50);
		// _Table.getColumnModel().getColumn(1).setMaxWidth(200);

		_Table.setDefaultRenderer(Object.class, new MyCellRenderer());

		// register the change listener (useful for the new patient)
		_Table.getModel().addTableModelListener(this);

		// Create the scroll pane and add the table to it.
		_ScrollPane = new JScrollPane(_Table);

		Dimension scrollDim = new Dimension(VILLAGE_PANEL_WIDTH, VILLAGE_LIST_HEIGHT);
		_ScrollPane.setMinimumSize(scrollDim);
		_ScrollPane.setMaximumSize(scrollDim);
		_ScrollPane.setPreferredSize(scrollDim);
		_ScrollPane.setSize(scrollDim);

		_ScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		_ScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		curVillagePanel.add(_ScrollPane);

		_ScrollPane.setBorder(BorderFactory.createLineBorder(Color.gray));

		// add search surname
		JPanel newVillageAreaPanel = new JPanel();
		Dimension inputDim = new Dimension(VILLAGE_PANEL_WIDTH, NEW_VILLAGE_HEIGHT);
		newVillageAreaPanel.setMinimumSize(inputDim);
		newVillageAreaPanel.setMaximumSize(inputDim);
		newVillageAreaPanel.setPreferredSize(inputDim);
		newVillageAreaPanel.setSize(inputDim);
		newVillageAreaPanel.setLayout(new GridLayout(2, 2, 2, 2));

		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_VILLAGELABEL);
		_VillageLabel = new JLabel(mess);
		newVillageAreaPanel.add(_VillageLabel);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_AREALABEL);
		_AreaLabel = new JLabel(mess);
		newVillageAreaPanel.add(_AreaLabel);

		_NewVillageOrigin = new JTextField();
		newVillageAreaPanel.add(_NewVillageOrigin);
		_NewVillageOrigin.setAlignmentX(CENTER_ALIGNMENT);
		// _NewVillageOrigin.setSize(inputDim);
		// _NewVillageOrigin.setMinimumSize(inputDim);
		// _NewVillageOrigin.setMaximumSize(inputDim);
		// _NewVillageOrigin.setPreferredSize(inputDim);

		_NewArea = new JComboBox();
		newVillageAreaPanel.add(_NewArea);
		_NewArea.setAlignmentX(CENTER_ALIGNMENT);
		// _NewArea.setSize(inputDim);
		// _NewArea.setMinimumSize(inputDim);
		// _NewArea.setMaximumSize(inputDim);
		// _NewArea.setPreferredSize(inputDim);

		newVillageAreaPanel.setBorder(BorderFactory.createLineBorder(Color.gray));

		curVillagePanel.add(newVillageAreaPanel);

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
		buttonPanel.setLayout(new GridLayout(1, NBR_BUTTONS, 0, 1));// nbr

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_ADDCOMMAND);
		_NewButton = new JButton(mess);
		_NewButton.addActionListener(this);
		buttonPanel.add(_NewButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		_DeleteButton = new JButton(mess);
		_DeleteButton.addActionListener(this);
		buttonPanel.add(_DeleteButton);
		_DeleteButton.setVisible(false);

		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		// _CancelEditButton = new JButton(mess);
		// _CancelEditButton.addActionListener(this);
		// buttonPanel.add(_CancelEditButton);
		// _CancelEditButton.setVisible(false);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton = new JButton(mess); //
		_HelpButton.addActionListener(this);
		buttonPanel.add(_HelpButton);

		//
		String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_VILLAGENEW);
		_NewButton.setToolTipText(tip);
		// tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_VILLAGEDELETE);
		// _DeleteButton.setToolTipText(tip);
		// tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_VILLAGECANCEL);
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

	/**
	 * a button is pressed
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == _HelpButton) { //
			String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_VILLAGELISTHELP);
			JOptionPane.showMessageDialog(null, tip, "", JOptionPane.PLAIN_MESSAGE, null);

		} else if (e.getSource() == _NewButton) { // edit
			System.out.println("actionPerformed NEW");

			VillageHealthAreaModelData villageHealthAreaData = new VillageListTableModel().new VillageHealthAreaModelData();

			String newVillage = _NewVillageOrigin.getText();
			if (newVillage != null && !newVillage.equals("")) {
				newVillage = newVillage.toLowerCase();
				newVillage = Character.toString(newVillage.charAt(0)).toUpperCase()+newVillage.substring(1);
				String area = _NewArea.getSelectedItem().toString();
				// villageHealthAreaData.setHealthArea(area);
				villageHealthAreaData.setVillageOrigin(newVillage + "::" + area);

				// update the model
				insertNewVillageHealthArea(villageHealthAreaData);

				// clear view
				_NewVillageOrigin.setText("");

				// flag modification
				_ConfigurationManager.setNewVillageOriginData(true);
			}

			// // flag data modification...
			// getEntryFormConfigurationManager().setVillageDataSaved(false);
			// // ...and row number
			// _MyTableModel.setLineNumberEditable(_MyTableModel.getRowCount() -
			// 1);
			//
			// getDescriptor().getWizard().setNextFinishButtonEnabled(false);
			//
			// _ConfigurationManager.setNewVillageDataInsertion(true);
			// // disable new
			// _NewButton.setEnabled(false);
			// // never disable cancel...
			// _CancelEditButton.setEnabled(true);
			// // disable delete if new enabled
			// _DeleteButton.setEnabled(false);

		}
		// else if (e.getSource() == _DeleteButton) { // edit
		// System.out.println("actionPerformed DELETE");
		//
		// String errMessCancel =
		// ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_CANCEL);
		// String messConfirmDelete =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CONFIRMDELETE);
		// Object[] options = { "OK", errMessCancel };
		//
		// int retCode = JOptionPane.showOptionDialog(null, messConfirmDelete,
		// "Warning", JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
		// null, options, options[1]);
		//
		// if (retCode == 0) {
		//
		// VillageModelData patientModelData =
		// getTableModel().getModelValues().get(getTable().getSelectedRow());
		// int patientDeletedRetCode = deleteVillage(patientModelData.getId());
		// //
		// if (patientDeletedRetCode >= 0) {// if new insertion deleted
		//
		// }
		// // reset saved flag
		// getEntryFormConfigurationManager().setVillageDataSaved(true);
		//
		// // remove the row from the model and the table
		// _MyTableModel.removeModelValue(patientModelData);
		// _MyTableModel.fireTableRowsDeleted(getTable().getSelectedRow(),
		// getTable().getSelectedRow());
		// configurePanelStartState(true);
		// }
		//
		// }
		// else if (e.getSource() == _CancelEditButton) { // cancel edit
		//
		// // restore original value
		// VillageModelData patientModelData =
		// getTableModel().getModelValues().get(getTable().getSelectedRow());
		//
		// Village patient =
		// readVillage(patientModelData.getVillageContextId());
		//
		// if (patient != null) {
		// // update the list model
		// fillModelValue(patientModelData, patient);
		// _MyTableModel.fireTableRowsInserted(0, 0);
		//
		// // update the list of concepts
		// // just need to remove it from cache, it will be reloaded next
		// // screen
		// _ConfigurationManager.getVillageContextCache().remove(patientModelData.getVillageContextId());
		//
		// } else {
		// // patient is null, then we just inserted a new line normally
		//
		// // cancel the editor focus set when new line added
		// // (editor.requestFocusInWindow())
		// if (getTable().getCellEditor() != null) //
		// getTable().getCellEditor().cancelCellEditing();
		//
		// // update the model by removing the inserted line
		// // remove the row from the model and the table
		// _MyTableModel.removeModelValue(patientModelData);
		// _MyTableModel.fireTableRowsDeleted(getTable().getSelectedRow(),
		// getTable().getSelectedRow());
		//
		// if (getTable().getRowCount() > 0) {
		// // select 1rst line
		// selectFirstLine();
		// }
		// }
		//
		// // reset saved flag
		// getEntryFormConfigurationManager().setVillageDataSaved(true);
		// getEntryFormConfigurationManager().setNewVillageDataInsertion(false);
		//
		// // restore original panel situation
		// configurePanelStartState(false);
		//
		// // change line to remove the red background modification
		// if (getTable().getRowCount() > 0) {
		// getTable().setRowSelectionInterval(0, 0);
		// }
		//
		// }
		else
			System.out.println(e.getSource());
	}

	// private void selectNewInsertedLineAndFocus() {
	// Runnable selectNewInsertedLine = new Runnable() {
	// public void run() {
	// getTable().setRowSelectionInterval(getTable().getRowCount() - 1,
	// getTable().getRowCount() - 1);
	// getTable().changeSelection(getTable().getRowCount() - 1,
	// VillageListTableModel.FIELDINDEX_ID, false, false);
	// boolean ret = getTable().editCellAt(getTable().getRowCount() - 1,
	// VillageListTableModel.FIELDINDEX_ID);
	// Component editor = getTable().getEditorComponent();
	// editor.requestFocusInWindow();
	// }
	// };
	// try {
	//
	// SwingUtilities.invokeLater(selectNewInsertedLine);
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS +
	// StackTraceUtil.getCustomStackTrace(ex), "Fatal error",
	// JOptionPane.ERROR_MESSAGE, null);
	// }
	//
	// }

	private void insertNewVillageHealthArea(VillageHealthAreaModelData villageHealthAreaData) {
		_MyTableModel.addModelValue(villageHealthAreaData);
		_MyTableModel.fireTableRowsInserted(0, 0);

		// selectNewInsertedLineAndFocus();

	}

	// private void selectFirstLine() {
	// Runnable selectNewInsertedLine = new Runnable() {
	// public void run() {
	// if (getTable().getRowCount() > 0) {
	// getTable().setRowSelectionInterval(getTable().getRowCount() - 1,
	// getTable().getRowCount() - 1);
	// getTable().changeSelection(0, VillageListTableModel.FIELDINDEX_ID, false,
	// false);
	// if (getTable().getCellEditor() != null)
	// getTable().getCellEditor().cancelCellEditing();
	// }
	// getDescriptor().getWizard().setNextFinishButtonEnabled(getTable().getRowCount()
	// > 0 && getTable().getSelectedRow() >= 0);
	//
	// }
	// };
	// //  run it not in the current stack
	// try {
	//
	// SwingUtilities.invokeLater(selectNewInsertedLine);
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS +
	// StackTraceUtil.getCustomStackTrace(ex), "Fatal error",
	// JOptionPane.ERROR_MESSAGE, null);
	// }
	// }


	/**
	 * update the view
	 */
	public void updateModelView() {

		// init combo area list (only once as the area list is fixed)
		ArrayList<String> areas = _ConfigurationManager.getAreaList();

		if (areas != null && _NewArea.getModel().getSize() == 0) {
//			_NewArea.removeAllItems();
			
			_NewArea.addItem("  ??  "); // unknown area
			for (String str : areas) {
				_NewArea.addItem(str);
			}
		}

		// fill the list of village - area
		List<VillageArea> villageAreas = _ConfigurationManager.getAllVillageArea();
		ArrayList<VillageListTableModel.VillageHealthAreaModelData> modelValues = new ArrayList<VillageListTableModel.VillageHealthAreaModelData>();
		if (villageAreas != null)
			for (VillageArea villageArea : villageAreas) {
				VillageListTableModel.VillageHealthAreaModelData modelValue = new VillageListTableModel().new VillageHealthAreaModelData();
				// fill the model with patient data
				fillModelValue(modelValue, villageArea);
				modelValues.add(modelValue);
			}

		// update table titles
		String[] tableTitles = _ConfigurationManager.getTableTitleTranslation("villageAreaList");
		setDataModel(modelValues, tableTitles);
		_MyTableModel.fireTableDataChanged();
	}

	// /**
	// *
	// * @param id
	// * @return -1 if error, 0 if the patient didn't existed, 1 if patient
	// * deleted
	// */
	// private int deleteVillage(String id) {
	//
	// // look for patient
	// List<VillageContext> patientContexts = null;
	// VillageContext currentVillageContext = null;
	//
	// String idVillageWithContext =
	// _ConfigurationManager.buildVillageContextId(id);
	//
	// VillageIdentifier patientIdentifier = new
	// VillageIdentifier(idVillageWithContext,
	// CommonIpdConstants.IDENTITY_TYPE_OTHER);
	// try {
	// patientContexts =
	// ServiceHelper.getVillageManagerService().getSelectedVillage(patientIdentifier,
	// false);
	//
	// if (patientContexts == null || patientContexts.size() > 1) {
	// // this should never happen as there is a uniq db constraint
	// // on
	// // the id
	// String errMess =
	// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ID_ALREADY_EXIST);
	// JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS +
	// "There are several patients with this id, " + errMess, "Error!",
	// JOptionPane.OK_OPTION, null);
	// return -1;
	// }
	//
	// currentVillageContext = patientContexts.get(0);
	// // remove all related to patient
	// ServiceHelper.getVillageManagerService().removeSelectedVillageContext(currentVillageContext);
	// return 1;
	//
	// } catch (NoResultException e) {
	// // the patient does not exist yet, it was created and deleted
	// String errMess = "deleteVillage: Village not found with id " + id;
	// System.out.println("deleteVillage: Village not found with id " + id);
	// JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS +
	// StackTraceUtil.getCustomStackTrace(e), errMess, JOptionPane.OK_OPTION,
	// null);
	//
	// return 0;
	//
	// }
	// }

	// the model should be updated
	public void setDataModel(ArrayList<VillageHealthAreaModelData> modelValues, String[] tableTitles) {

		// // manage combo value in list
		// String comboSex = "sex";
		// JComboBox comboBox =
		// ComboEntityList.buildComboboxModelValue(_ConfigurationManager,
		// comboSex);
		// ((VillageListTableModel)_MyTableModel).setSexEditor(comboBox);
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
	// private class RowListener implements ListSelectionListener {
	// public void valueChanged(ListSelectionEvent e) {
	// if (e.getValueIsAdjusting()) {
	//
	// if (_MyTableModel.getLineNumberEditable() != null) { // new line
	// // mode
	// // save patient id
	//
	// DefaultListSelectionModel selectionModel = (DefaultListSelectionModel)
	// e.getSource();
	// int rowImpacted = selectionModel.getMinSelectionIndex();
	// if (rowImpacted == -1)
	// rowImpacted = 0;
	//
	// // save the patient selection
	// VillageHealthAreaModelData patientModelData =
	// getTableModel().getModelValues().get(rowImpacted);
	// _ConfigurationManager.setCurrentVillageHealthAreaModelData(patientModelData);
	//
	// // if user changed row, go back to the row
	// if (rowImpacted != _MyTableModel.getLineNumberEditable()) {
	//
	// getTable().setRowSelectionInterval(_MyTableModel.getLineNumberEditable(),
	// _MyTableModel.getLineNumberEditable());
	// }
	//
	// }
	// // else {
	// // // keep track of the row selection
	// // // check if there is a change in the context
	// // SelectionContext selectionContext =
	// // _ConfigurationManager.getCurrentSelectionContext();
	// // if (!selectionContext.equals(((VillageListFormDescriptor)
	// // getDescriptor())._LastSelectionContext))
	// // _CurrentVillageLineNumberSelection = rowImpacted;
	// // }
	//
	// return;
	// }
	// }
	// }

	/*
	 * the user just made a modification on a cell
	 */
	// @Override
	// // TableModelListener
	// public void tableChanged(TableModelEvent e) {
	// int row = e.getFirstRow();
	// int column = e.getColumn();
	// if (column != TableModelEvent.ALL_COLUMNS) {
	// TableModel model = (TableModel) e.getSource();
	//
	// if (column == VillageListTableModel.FIELDINDEX_ID) {
	// // id has changed
	// // since id has changed the contxt id should be null
	// if (_ConfigurationManager.getCurrentVillageModelData() != null) {
	// _ConfigurationManager.getCurrentVillageModelData().setOldId(_ConfigurationManager.getCurrentVillageModelData().getId());
	// }
	//
	// // check that id does not exist yet
	// boolean alreadyExist = checkVillageAlreadyExist(model, row, column,
	// CommonIpdConstants.IDENTITY_TYPE_OTHER);
	// if (alreadyExist) {
	// String idVillage = (String) model.getValueAt(row, column);
	// showError(idVillage);
	// return;
	// }
	// }
	//
	// // allow next button if ID and sex are filled
	// boolean allowed = false;
	// int columnIndex = VillageListTableModel.FIELDINDEX_ID;
	// Object colData = model.getValueAt(row, columnIndex);
	// if (colData != null && !colData.toString().equals(""))
	// allowed = true;
	// if (allowed) {
	// allowed = false;
	// columnIndex = VillageListTableModel.FIELDINDEX_SEX;
	// colData = model.getValueAt(row, columnIndex);
	// if (colData != null && !colData.toString().equals(""))
	// allowed = true;
	// }
	// getDescriptor().getWizard().setNextFinishButtonEnabled(allowed);
	//
	// // alway disable new patient when in modification
	// _NewButton.setEnabled(false);
	//
	// // enable cancel as we are in modification
	// _ConfigurationManager.setNewVillageDataInsertion(true);
	// _CancelEditButton.setEnabled(true);
	//
	// // flag data modification...
	// getEntryFormConfigurationManager().setVillageDataSaved(false);
	// // ...and row number
	// _MyTableModel.setLineNumberEditable(row);
	// }
	// }

	// private void showError(final String idVillage) {
	//
	// Runnable selectNewInsertedLine = new Runnable() {
	// public void run() {
	//
	// String errMess =
	// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ID_ALREADY_EXIST);
	// JOptionPane.showMessageDialog(null, errMess + "! " + idVillage, "Error",
	// JOptionPane.ERROR_MESSAGE, null);
	//
	// // set selection to the correct defect cell
	// getTable().setRowSelectionInterval(getTable().getRowCount() - 1,
	// getTable().getRowCount() - 1);
	// getTable().changeSelection(getTable().getRowCount() - 1,
	// VillageListTableModel.FIELDINDEX_ID, false, false);
	// boolean ret = getTable().editCellAt(getTable().getRowCount() - 1,
	// VillageListTableModel.FIELDINDEX_ID);
	// Component editor = getTable().getEditorComponent();
	// editor.requestFocusInWindow();
	// }
	// };
	// // do not run it in the current stack
	// try {
	//
	// SwingUtilities.invokeLater(selectNewInsertedLine);
	// } catch (Exception ex) {
	// ex.printStackTrace();
	// JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS +
	// StackTraceUtil.getCustomStackTrace(ex), "Fatal error",
	// JOptionPane.ERROR_MESSAGE, null);
	// }
	// }

	/**
	 * 
	 * @param model
	 * @param row
	 * @param column
	 * @param identityTypeOther
	 * @return true if the patient identified by the content of the table in
	 *         row, column already exists
	 */
	// private boolean checkVillageAlreadyExist(TableModel model, int row, int
	// column, int identityTypeOther) {
	// // the patient id is on first col
	// if (column != VillageListTableModel.FIELDINDEX_ID)
	// return false;
	// String idVillageToCheck = (String) model.getValueAt(row, column);
	//
	// if (idVillageToCheck == null || idVillageToCheck.toString().equals(""))
	// return false;
	//
	// // first just check in the model
	// for (int rowIndex = 0; rowIndex < model.getRowCount(); rowIndex++) {
	// String idVillage = (String) model.getValueAt(rowIndex, column);
	// if (rowIndex != row && idVillage.equals(idVillageToCheck))
	// return true;
	// }
	//
	// // then check in db...
	// // check if new insert
	// if (_ConfigurationManager.isNewVillageDataInsertion()) {
	// // check if it exists in db
	//
	// String idVillageWithContext =
	// _ConfigurationManager.buildVillageContextId(idVillageToCheck);
	//
	// VillageIdentifier newVillageIdentifier = new
	// VillageIdentifier(idVillageWithContext, identityTypeOther);
	// List<VillageContext> patientContexts = null;
	// try {
	// patientContexts =
	// ServiceHelper.getVillageManagerService().getSelectedVillage(newVillageIdentifier,
	// null);
	// } catch (NoResultException e) {
	// // normal case
	// }
	//
	// if (patientContexts != null && patientContexts.size() > 0) {
	// return true;
	// }
	// }
	// return false;
	// }

	public boolean isSelectionEmpty() {
		return _Table.getSelectionModel().isSelectionEmpty();
	}

	public VillageListTableModel getTableModel() {
		return _MyTableModel;
	}

	public JTable getTable() {
		return _Table;
	}

	public class MyCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			// if (_MyTableModel.getLineNumberEditable() != null && row ==
			// _MyTableModel.getLineNumberEditable() && column !=
			// VillageListTableModel.FIELDINDEX_INDEX)
			// cell.setBackground(Color.pink);
			// else if (!isSelected)
			// cell.setBackground(null);

			return cell;
		}
	}

	/**
	 * called by descriptor so that the panel can be initialized
	 * 
	 * @param contextChanged
	 */
	public void configurePanelStartState() {

		// configure the panel
		internationalize(); //

		// _NewButton.setEnabled(_ConfigurationManager.isVillageDataSaved());

		// if (getTable().getRowCount() > 0) {
		// // disable cancel if new disabled and conversely
		// _CancelEditButton.setEnabled(_ConfigurationManager.isNewVillageDataInsertion()
		// && !_ConfigurationManager.isVillageDataSaved());
		//
		// // allow to delete if not in insertion mode
		// _DeleteButton.setEnabled(!_ConfigurationManager.isNewVillageDataInsertion());
		// } else {
		// _DeleteButton.setEnabled(false);
		// _CancelEditButton.setEnabled(false);
		// }
		//
		// if (_ConfigurationManager.isVillageDataSaved()) {
		// getTableModel().setLineNumberEditable(null);
		// }
		//
		// // allow any row selection
		// if (_ConfigurationManager.isVillageDataSaved() && contextChanged) {
		// // if several lines and none selected, select first one
		// if (getTable().getRowCount() > 0 /*
		// * && getTable().getSelectedRow() ==
		// * -1
		// */) {
		//
		// getTable().setRowSelectionInterval(0, 0);
		// getTable().changeSelection(0, VillageListTableModel.FIELDINDEX_ID,
		// false, false);
		// }
		// }
		//
		// getDescriptor().getWizard().setNextFinishButtonEnabled(getTable().getRowCount()
		// > 0 && getTable().getSelectedRow() >= 0);

	}

	/**
	 * 
	 */
	protected void internationalize() {

		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_ADDCOMMAND);
		_NewButton.setText(mess);
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		// _DeleteButton.setText(mess);
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		// _CancelEditButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton.setText(mess);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_VILLAGELABEL);
		_VillageLabel.setText(mess);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_AREALABEL);
		_AreaLabel.setText(mess);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_AREALABEL);

		String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_VILLAGENEW);
		_NewButton.setToolTipText(tip);
	}

	/**
	 * fill the model with patient data
	 * 
	 * @param modelValue
	 * @param patient
	 */
	public void fillModelValue(VillageHealthAreaModelData modelValue, VillageArea villageArea) {

		modelValue.setVillageOrigin(villageArea.getVillageOrigin());
		// modelValue.setHealthArea(villageArea.getHealthArea());

	}

	// public static class ComboEntityList {
	// private static HashMap<String, ArrayList<KeyValue>> _ComboboxValueMap =
	// null;
	//
	// public static String getModelForEntity(String comboName, String
	// modelValue) {// read
	// // from
	// // model
	//
	// // get the id of the combobox
	// ArrayList<KeyValue> values = getComboboxValueMap().get(comboName);
	// for (KeyValue keyValue : values) {
	// if (keyValue._Value.equals(modelValue)) {
	// return keyValue._Key;
	// }
	// }
	// System.out.println("ComboEntityList.setModelToEntity::getResults:ERROR: combo id of "
	// + comboName + " not found!!!!");
	// return "notFound!";
	// }
	//
	// public static String getEntityForModel(String comboName, String
	// patientValue) {
	// // write to model get the id of the combobox
	// ArrayList<KeyValue> values = getComboboxValueMap().get(comboName);
	// for (KeyValue keyValue : values) {
	// if (keyValue._Key.equals(patientValue)) {
	// return keyValue._Value;
	// }
	// }
	//
	// System.out.println("ComboEntityList.setModelToEntity::setIdValues: ERROR: combo id of "
	// + comboName + " not found!!!!");
	// return "notFound!";
	// }
	//
	// public static JComboBox
	// buildComboboxModelValue(EntryFormConfigurationManagerImpl
	// configurationManager, String comboName) {
	// ComboEntityList.setComboboxValueMap(configurationManager.getAllComboLabels(Village.class,
	// null, false));
	// JComboBox comboBox = new JComboBox();
	// ArrayList<KeyValue> values = getComboboxValueMap().get(comboName);
	// for (KeyValue keyValue : values) {
	// comboBox.addItem(keyValue._Value);
	// }
	// return comboBox;
	// }
	//
	// public static HashMap<String, ArrayList<KeyValue>> getComboboxValueMap()
	// {
	// return _ComboboxValueMap;
	// }
	//
	// public static void setComboboxValueMap(HashMap<String,
	// ArrayList<KeyValue>> _ComboboxValueMap) {
	// ComboEntityList._ComboboxValueMap = _ComboboxValueMap;
	// }
	// }

	@Override
	//
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_VILLAGELISTPANEL;
	}

	public ArrayList<VillageHealthAreaModelData> getVillageList() {
		return ((VillageListTableModel) (getTable().getModel())).getModelValues();
	}

}
