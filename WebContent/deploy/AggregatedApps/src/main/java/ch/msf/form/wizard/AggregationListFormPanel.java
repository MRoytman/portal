package ch.msf.form.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.NoResultException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.form.config.AggregationListTableModel;
import ch.msf.form.config.AggregationListTableModel.AggregatedModelData;
import ch.msf.model.Aggregation;
import ch.msf.model.AggregationContext;
import ch.msf.model.datavalidator.NumericValidator;
import ch.msf.service.ServiceHelper;
import ch.msf.util.MiscelaneousUtils;
import ch.msf.util.StackTraceUtil;

public class AggregationListFormPanel extends AbstractWizardPanel {

	private static final long serialVersionUID = 1L;

	private JButton _NewButton; //
	// private JButton _DeleteButton; //
	private JButton _CancelEditButton; //
	private JButton _HelpButton; //

	private JScrollPane _ScrollPane;

	protected AggregationListTableModel _MyTableModel;

	private JTable _Table;

	public AggregationListFormPanel() {
		super(null);
	}

	public JPanel getContentPanel() {

		// pass a null model, the values will be passed by the descriptor
		_MyTableModel = new AggregationListTableModel();

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

		// aggr info
		JPanel curAggregPanel = new JPanel();
		curAggregPanel.setLayout(new BorderLayout());
		Dimension curAggregDim = new Dimension(AGGREG_INFO_PANEL_WIDTH, AGGREG_INFO_PANEL_HEIGHT);
		curAggregPanel.setMinimumSize(curAggregDim);
		curAggregPanel.setMaximumSize(curAggregDim);
		curAggregPanel.setPreferredSize(curAggregDim);
		curAggregPanel.setSize(curAggregDim);
		excercicePanel.add(curAggregPanel);

		// final DefaultCellEditor dateYearCellEditor = new MyDateFieldEditor3(null, null);

		// we should always define a cell editor and a cell renderer
		_Table = new JTable(_MyTableModel) {
			public void changeSelection(final int row, final int column, boolean toggle, boolean extend) {
				super.changeSelection(row, column, toggle, extend);

				// stay on same row...(tab at last new cell)
				if (_MyTableModel.getLineNumberEditable() != null && row != _MyTableModel.getLineNumberEditable()) {
					changeSelection(_MyTableModel.getLineNumberEditable(), column, toggle, extend);
				}
			}

			//
			// // write the date according same format their are entered
			public TableCellRenderer getCellRenderer(int row, int column) {
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

					if (realColumnIndex == AggregationListTableModel.FIELDINDEX_YEAR) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGATIONYEAR);
					} else if (realColumnIndex == AggregationListTableModel.FIELDINDEX_WEEK) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGATIONWEEK);
					} else if (realColumnIndex == AggregationListTableModel.FIELDINDEX_CONFIG_LABEL) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGATIONTYPE);
					} else if (realColumnIndex == AggregationListTableModel.FIELDINDEX_STARTDATE || realColumnIndex == AggregationListTableModel.FIELDINDEX_ENDDATE) {
						tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGATIONDATES);
					}

					else {
						// You can omit this part if you know you don't
						// have any renderers that supply their own tool
						// tips.
						tip = super.getToolTipText(e);
					}
				}
				return tip;
			}

			// public TableCellEditor getCellEditor(int row, int column) {
			// int realColumnIndex = convertColumnIndexToModel(column);
			//
			// if (realColumnIndex == AggregationListTableModel.FIELDINDEX_YEAR) {
			// return dateYearCellEditor;
			// }
			//
			// return super.getCellEditor(row, column);
			// }
		};

		// change col titles
		String[] tableTitles = getEntryFormConfigurationManager().getTableTitleTranslation("aggregationList");
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

		// register the change listener (useful for the new aggreg)
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

		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_NEWCOMMAND);
		_NewButton = new JButton(mess);
		_NewButton.addActionListener(this);
		buttonPanel.add(_NewButton);

		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		// _DeleteButton = new JButton(mess);
		// _DeleteButton.addActionListener(this);
		// buttonPanel.add(_DeleteButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelEditButton = new JButton(mess);
		_CancelEditButton.addActionListener(this);
		buttonPanel.add(_CancelEditButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton = new JButton(mess); // TN106
		_HelpButton.addActionListener(this);
		buttonPanel.add(_HelpButton);

		// // TN66
		// String tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGNEW);
		// _NewButton.setToolTipText(tip);
		// // tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGDELETE);
		// // _DeleteButton.setToolTipText(tip);
		// tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGCANCEL);
		// _CancelEditButton.setToolTipText(tip);
		// tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGFIND);
		// _FindButton.setToolTipText(tip);
		// tip =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGCLEAR);
		// _ClearSearchButton.setToolTipText(tip);

		setUpWeekColumn(_Table.getColumnModel().getColumn(AggregationListTableModel.FIELDINDEX_WEEK));
		setUpThemeColumn(_Table.getColumnModel().getColumn(AggregationListTableModel.FIELDINDEX_CONFIG_LABEL));

		return contentPanelMaster;
	}

	private void setUpWeekColumn(TableColumn weekColumn) {
		// Set up the editor for the sport cells.
		JComboBox comboBox = new JComboBox();
		for (int i = 1; i <= 52; i++) {
			comboBox.addItem("" + i);
		}
		weekColumn.setCellEditor(new DefaultCellEditor(comboBox));
	}

	private void setUpThemeColumn(TableColumn themeColumn) {
		// Set up the editor for the sport cells.
		JComboBox comboBox = new JComboBox();

		ArrayList<String> aggregLabels = _ConfigurationManager.getAllAggregationLabel();
		for (String aggregLabel : aggregLabels) {
			comboBox.addItem(aggregLabel);
		}

		themeColumn.setCellEditor(new DefaultCellEditor(comboBox));
	}

	/**
	 * a button is pressed
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == _HelpButton) { // TN106
			String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGATIONLISTHELP);
			JOptionPane.showMessageDialog(null, tip, "", JOptionPane.PLAIN_MESSAGE, null);

		} else if (e.getSource() == _NewButton) { // edit
			System.out.println("actionPerformed NEW");

			// update the aggreg list
			((AggregationListFormDescriptor) getDescriptor()).updateAggregationList();
			AggregatedModelData aggregModelData = new AggregationListTableModel().new AggregatedModelData();

			// update the model
			_MyTableModel.addModelValue(aggregModelData);
			_MyTableModel.fireTableRowsInserted(0, 0);

			selectNewInsertedLineAndFocus();

			// flag data modification...
			// getEntryFormConfigurationManager().setAggregatedAttributesDataSaved(false);
			// ...and row number
			_MyTableModel.setLineNumberEditable(_MyTableModel.getRowCount() - 1);

			getDescriptor().getWizard().setNextFinishButtonEnabled(false);
			// disable back button while editing
			getDescriptor().getWizard().setBackButtonEnabled(false);

			_ConfigurationManager.setNewAggregatedDataInsertion(true);
			// disable new
			_NewButton.setEnabled(false);
			// never disable cancel...
			_CancelEditButton.setEnabled(true);

		} else if (e.getSource() == _CancelEditButton) { // cancel edit

			// restore original value
			AggregatedModelData modelData = getTableModel().getModelValues().get(getTable().getSelectedRow());

			Aggregation aggreg = readAggregation(modelData.getAggregatedContextId());

			if (aggreg != null) {
				// update the list model
				fillModelValue(modelData, aggreg);
				_MyTableModel.fireTableRowsInserted(0, 0);

				// update the list of concepts
				// just need to remove it from cache, it will be reloaded next
				// screen
				_ConfigurationManager.getAggregationContextCache().remove(modelData.getAggregatedContextId());

			} else {
				// aggreg is null, then we just inserted a new line normally

				// cancel the editor focus set when new line added
				// (editor.requestFocusInWindow())
				if (getTable().getCellEditor() != null) // TN112
					getTable().getCellEditor().cancelCellEditing();

				// update the model by removing the inserted line
				// remove the row from the model and the table
				_MyTableModel.removeModelValue(modelData);
				_MyTableModel.fireTableRowsDeleted(getTable().getSelectedRow(), getTable().getSelectedRow());

				if (getTable().getRowCount() > 0) {
					// select 1rst line
					selectFirstLine();
				}
			}

			// reset saved flag
			getEntryFormConfigurationManager().setAggregatedDataSaved(true);
			getEntryFormConfigurationManager().setNewAggregatedDataInsertion(false);

			// restore original panel situation
			configurePanelStartState(false);

			// change line to remove the red background modification
			if (getTable().getRowCount() > 0) {
				getTable().setRowSelectionInterval(0, 0);
			}

			// enable back button while editing
			getDescriptor().getWizard().setBackButtonEnabled(true);
		} else
			System.out.println(e.getSource());
	}

	/**
	 * insert a new line and set the focus on it
	 */
	private void selectNewInsertedLineAndFocus() {
		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {
				getTable().setRowSelectionInterval(getTable().getRowCount() - 1, getTable().getRowCount() - 1);
				getTable().changeSelection(getTable().getRowCount() - 1, AggregationListTableModel.FIELDINDEX_YEAR, false, false);
				boolean ret = getTable().editCellAt(getTable().getRowCount() - 1, AggregationListTableModel.FIELDINDEX_YEAR);
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
					getTable().changeSelection(0, AggregationListTableModel.FIELDINDEX_WEEK, false, false);
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

	private Aggregation readAggregation(Long aggregContextId) {

		// look for aggreg

		AggregationContext aggregContext = null;
		try {
			aggregContext = ServiceHelper.getAggregationDataManagerService().getAggregatedContext(aggregContextId);
		} catch (NoResultException e) {
			// no entries for that id, return null
		}

		if (aggregContext != null)
			return aggregContext.getAggregation();

		return null;
	}

	// fill the tablemodel with aggreg context data
	public void updateModelView(List<AggregationContext> aggregationContexts) {

		// clear the aggreg cache
		_ConfigurationManager.getAggregationContextCache().clear();

		ArrayList<AggregationListTableModel.AggregatedModelData> modelValues = new ArrayList<AggregationListTableModel.AggregatedModelData>();
		if (aggregationContexts != null)
			for (AggregationContext aggregationContext : aggregationContexts) {
				AggregationListTableModel.AggregatedModelData modelValue = new AggregationListTableModel().new AggregatedModelData();
				Aggregation aggregation = aggregationContext.getAggregation();
				// fill the model with aggreg data
				fillModelValue(modelValue, aggregation);
				modelValues.add(modelValue);

				// add caching purposes info
				modelValue.setAggregatedContextId(aggregationContext.getId());

				_ConfigurationManager.getAggregationContextCache().put(aggregationContext.getId(), aggregationContext);
			}

		// update table titles
		String[] tableTitles = _ConfigurationManager.getTableTitleTranslation("aggregationList");
		setDataModel(modelValues, tableTitles);
		_MyTableModel.fireTableDataChanged();

		// update language dependant items
		setUpThemeColumn(_Table.getColumnModel().getColumn(AggregationListTableModel.FIELDINDEX_CONFIG_LABEL));
	}

	// the model should be updated
	public void setDataModel(ArrayList<AggregatedModelData> modelValues, String[] tableTitles) {

		_MyTableModel.setModelValues(modelValues, tableTitles);
		// // change col titles
		changeTableColumnTitles(tableTitles);
	}

	/**
	 * activated when new row added or when user change aggreg
	 * 
	 * @author cmi
	 * 
	 */
	private class RowListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {

				if (_MyTableModel.getLineNumberEditable() != null) { // new line
																		// mode
					// save aggreg id

					DefaultListSelectionModel selectionModel = (DefaultListSelectionModel) e.getSource();
					int rowImpacted = selectionModel.getMinSelectionIndex();
					if (rowImpacted == -1)
						rowImpacted = 0;

					// save the aggreg selection
					AggregatedModelData aggregatedModelData = getTableModel().getModelValues().get(rowImpacted);
					Long currentAggregationContextId = aggregatedModelData.getAggregatedContextId();
					AggregationContext currentAggregationContext = _ConfigurationManager.getAggregationContextCache().get(currentAggregationContextId);
					_ConfigurationManager.setCurrentAggregationContext(currentAggregationContext);

					// if user changed row, go back to the row
					if (rowImpacted != _MyTableModel.getLineNumberEditable()) {

						getTable().setRowSelectionInterval(_MyTableModel.getLineNumberEditable(), _MyTableModel.getLineNumberEditable());
					}

				}
				return;
			}
		}
	}

	private NumericValidator _NumericValidator = new NumericValidator("aggregation_year");

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

			boolean nextButtonAllowed = true;
			Integer errorCode = null;
			if (column == AggregationListTableModel.FIELDINDEX_YEAR || column == AggregationListTableModel.FIELDINDEX_WEEK) {

				String colDataYear = (String) model.getValueAt(row, AggregationListTableModel.FIELDINDEX_YEAR);
				if (column == AggregationListTableModel.FIELDINDEX_YEAR) {

					// year has changed
					// check if format is correct
					// get the year
					nextButtonAllowed = _NumericValidator.doFormatChecks(colDataYear);
					if (nextButtonAllowed) {
						Calendar today = new GregorianCalendar();
						today.setTime(new Date());
						// Integer errorCode = checkBusinessRules();
						errorCode = _NumericValidator.checkBusinessRules(colDataYear, new BigDecimal(today.get(Calendar.YEAR)));

					}
					else{
						errorCode = CommonConstants.MESSAGE_BAD_PARAM;
					}
					if (errorCode != null) { // error case
						showYearError(row, column, errorCode);
						nextButtonAllowed = false;
					}
				}

				// get the week
				String colDataWeek = (String) model.getValueAt(row, AggregationListTableModel.FIELDINDEX_WEEK);
				if (nextButtonAllowed && colDataYear != null && colDataWeek != null && !colDataYear.equals("") && !colDataWeek.equals("")) {
					// and compute dates
					Date[] startEndDate = computeDates(Integer.parseInt(colDataYear), Integer.parseInt(colDataWeek));

					model.setValueAt(_Sdf.format(startEndDate[0]), row, AggregationListTableModel.FIELDINDEX_STARTDATE);
					model.setValueAt(_Sdf.format(startEndDate[1]), row, AggregationListTableModel.FIELDINDEX_ENDDATE);
				}
			}

			// check if next button allowed

			int columnIndex = AggregationListTableModel.FIELDINDEX_YEAR;
			if (nextButtonAllowed) {
				do {
					Object colData = model.getValueAt(row, columnIndex);
					if (colData == null || colData.toString().equals("")) {
						nextButtonAllowed = false;
						break;
					}
					columnIndex++;

				} while (nextButtonAllowed && columnIndex < model.getColumnCount());
			}
			getDescriptor().getWizard().setNextFinishButtonEnabled(nextButtonAllowed);

			// alway disable new aggreg when in modification
			_NewButton.setEnabled(false);

			// enable cancel as we are in modification
			_ConfigurationManager.setNewAggregatedDataInsertion(true);
			_CancelEditButton.setEnabled(true);

			// flag data modification...
			getEntryFormConfigurationManager().setAggregatedDataSaved(false);
			// ...and row number
			_MyTableModel.setLineNumberEditable(row);
		}
	}
	
	private void showYearError(final int row, final int column, final Integer errorCode) {

		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {
				
				// show error code message and get user instruction
				// to force or not
				String errMess = ServiceHelper.getMessageService().getMessage(errorCode);
				JOptionPane.showMessageDialog(null, errMess , "Error", JOptionPane.ERROR_MESSAGE, null);

				// set selection to the correct defect cell
//				getTable().setRowSelectionInterval(getTable().getRowCount() - 1, getTable().getRowCount() - 1);
				getTable().changeSelection(row, column, false, false);
				boolean ret = getTable().editCellAt(row, column);
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

	private Date[] computeDates(int year, int week) {

		Calendar calendar = new GregorianCalendar();
		calendar.clear();
		calendar.set(Calendar.WEEK_OF_YEAR, week);
		calendar.set(Calendar.YEAR, year);

		// Now get the first day of week.
		Date startDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		Date endDate = calendar.getTime();

		Date[] dateArray = new Date[2];
		dateArray[0] = startDate;
		dateArray[1] = endDate;

		return dateArray;
	}

	public boolean isSelectionEmpty() {
		return _Table.getSelectionModel().isSelectionEmpty();
	}

	public AggregationListTableModel getTableModel() {
		return _MyTableModel;
	}

	public JTable getTable() {
		return _Table;
	}

	public class MyCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			if (_MyTableModel.isCellEditable(row, column))
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

		_NewButton.setEnabled(_ConfigurationManager.isAggregatedAttributesDataSaved());

		if (getTable().getRowCount() > 0) {
			// disable cancel if new disabled and conversely
			_CancelEditButton.setEnabled(_ConfigurationManager.isNewAggregatedDataInsertion() && !_ConfigurationManager.isAggregatedDataSaved());

			// allow to delete if not in insertion mode
			// _DeleteButton.setEnabled(!_ConfigurationManager.isNewAggregDataInsertion());
		} else {
			// _DeleteButton.setEnabled(false);
			_CancelEditButton.setEnabled(false);
		}

		if (_ConfigurationManager.isAggregatedDataSaved()) {
			getTableModel().setLineNumberEditable(null);
		}

		// allow any row selection
		if (_ConfigurationManager.isAggregatedDataSaved() && contextChanged) {
			// if several lines and none selected, select first one
			if (getTable().getRowCount() > 0 /*
											 * && getTable().getSelectedRow() == -1
											 */) {

				getTable().setRowSelectionInterval(0, 0);
				getTable().changeSelection(0, AggregationListTableModel.FIELDINDEX_YEAR, false, false);
			}
		}

	}

	/**
	 * 
	 */
	protected void internationalize() {

		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_NEWCOMMAND);
		_NewButton.setText(mess);
		// mess =
		// ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_DELETECOMMAND);
		// _DeleteButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelEditButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton.setText(mess);

		String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGNEW);
		_NewButton.setToolTipText(tip);

		tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_AGGREGCANCEL);
		_CancelEditButton.setToolTipText(tip);
	}

	/**
	 * fill the model with aggreg data
	 * 
	 * @param modelValue
	 * @param aggregation
	 */
	public void fillModelValue(AggregatedModelData modelValue, Aggregation aggregation) {

		modelValue.setAggregatedContextId(aggregation.getId());
		// modelValue.setAggregatedType(aggregation.getThemeType());
		Calendar cal = new GregorianCalendar();
		cal.setTime(aggregation.getEndDate());
		int endYearNumber = cal.get(Calendar.YEAR);

		cal.setTime(aggregation.getStartDate());
		int startYearNumber = cal.get(Calendar.YEAR);

		//TN15 get week of day
		int weekNumber = getWeekDay(cal);

		modelValue.setWeek(Integer.toString(weekNumber));

		if (startYearNumber != endYearNumber) { // TN11
			// years are different across the period of time
			// should take the right year to print
			if (weekNumber == 1)
				modelValue.setYear(Integer.toString(endYearNumber));
			else if (weekNumber >= 52)
				modelValue.setYear(Integer.toString(startYearNumber));

		} else
			modelValue.setYear(Integer.toString(startYearNumber));

		modelValue.setStartDate(_Sdf.format(aggregation.getStartDate()));
		if (aggregation.getEndDate() != null) {
			modelValue.setEndDate(_Sdf.format(aggregation.getEndDate()));
		}

		// set theme label
		// get rid of context id
		String themeNoContextId = MiscelaneousUtils.getIdFromContext(aggregation.getThemeCode());
		String aggregationLabel = _ConfigurationManager.getAggregationLabel(themeNoContextId);
		// modelValue.setType(encounter.getType());
		modelValue.setAggregatedLabel(aggregationLabel);
	}

	/**
	 * TN15 (take the Wednesday to compute the week)
	 * @param cal: is set to an existing date
	 * @return a number that correspond to a week day
	 */
	private int getWeekDay(Calendar cal) {
		// 
		Calendar calCopy = new GregorianCalendar();
		calCopy.setTime(cal.getTime());
		int dayWeek = calCopy.get(Calendar.DAY_OF_WEEK);
		int offset = 0;
		switch(dayWeek){
		case Calendar.SUNDAY:
			offset += 1;
			//$FALL-THROUGH$
		case Calendar.MONDAY:
			offset += 1;
			//$FALL-THROUGH$
		case Calendar.TUESDAY:
			offset += 1;
		}
		calCopy.add(Calendar.DAY_OF_WEEK, offset);
		
		return calCopy.get(Calendar.WEEK_OF_YEAR);
	}

	@Override
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_AGGREGATIONLISTPANEL;
	}

}
