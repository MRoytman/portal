package ch.msf.form.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.form.MyDateFieldEditor3;
import ch.msf.form.ParamException;
import ch.msf.form.config.EncounterKeyValueTableModel;
import ch.msf.form.config.EncounterKeyValueTableModel.DicoData;
import ch.msf.model.ConceptIdValue;
import ch.msf.model.Encounter;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;
import ch.msf.util.MiscelaneousUtils;

public class EncounterFormPanel extends AbstractWizardPanel implements ActionListener, TableModelListener {

	private static final long serialVersionUID = 1L;

	private JButton _SaveButton; //
	private JButton _CancelButton; /* TN79 */
	private JButton _HelpButton; // TN106
	
	private JScrollPane _ScrollPane;

	protected EncounterKeyValueTableModel _MyTableModel;

	private JTable _Table;

	public EncounterFormPanel() {

		super();

	}

	public JPanel getContentPanel() {
		_MyTableModel = new EncounterKeyValueTableModel(null, null, null);

		JPanel contentPanelMaster = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));

		JPanel contentPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		contentPanelMaster.add(contentPanel1);

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
		// curPatientPanel.setLayout(new GridLayout(1, 1, 0, 15));//
		excercicePanel.add(curPatientPanel);
		_CurPatientInfo = new JLabel();
		curPatientPanel.add(_CurPatientInfo);
		_CurPatientInfo.setText("");
		// curPatientPanel.setBorder(BorderFactory.createLineBorder(Color.red));

		buildTable();

		// Create the scroll pane and add the table to it.
		_ScrollPane = new JScrollPane(_Table);

		Dimension scrollDim = new Dimension(EXERCISE_LIST_WIDTH, EXERCISE_LIST_HEIGHT);
		_ScrollPane.setMinimumSize(scrollDim);
		_ScrollPane.setMaximumSize(scrollDim);
		_ScrollPane.setPreferredSize(scrollDim);
		_ScrollPane.setSize(scrollDim);
		// _ScrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));

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
		final int NBR_BUTTONS = 2; //
		buttonPanel.setLayout(new GridLayout(1, NBR_BUTTONS, 0, 3));// nbr

		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_SAVECOMMAND);
		_SaveButton = new JButton(mess);
		_SaveButton.addActionListener(this);

		buttonPanel.add(_SaveButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelButton = new JButton(mess);/* TN79 */
		_CancelButton.addActionListener(this);
		_CancelButton.setEnabled(false);
		buttonPanel.add(_CancelButton);
		
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton = new JButton(mess);
		_HelpButton.addActionListener(this);
		buttonPanel.add(_HelpButton);

		return contentPanelMaster;
	}

	private void buildTable() {

		final DefaultTableCellRenderer titleLeftRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = null;
				if ((column == 0)) {
					if (hasFocus) // TN100
						table.changeSelection(row, column + 1, false, false);

					if (value != null) { // TN96
						JLabel parent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						parent.setFont(parent.getFont().deriveFont(Font.BOLD));
						parent.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);// TN115
						return parent;
					}
				}
				component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				return component;
			}

		};

		// let's make a renderer only for the left column
		// just to render the titles in bold...
		final DefaultTableCellRenderer leftColRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = null;
				if ((column == 0)) {

					if (hasFocus) // TN100
						table.changeSelection(row, column + 1, false, false);

					if (value != null) { // TN96
						JLabel parent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						parent.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);// TN116
						return parent;
					}

				}
				component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				return component;
			}

		};

		// let's make a renderer only for the right column
		// just to render the combobox and dates...
		final DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = null;

				// ...and align all cells to left
				setHorizontalAlignment(JLabel.LEFT);

				DicoData dicoData = _MyTableModel.getKeyValues().get(row);
				int intFieldType = _MyTableModel.getModelTypes().get(dicoData._Key);

				if ((column == 1)) {
					// get type

					if (intFieldType == CommonIpdConstants.BOOLEAN_TYPE) {
						// just to render the combobox
						if (value != null) {
							JCheckBox checkBox = new JCheckBox();
							checkBox.setSelected((Boolean) value);
							return checkBox;
						}
					} else if (intFieldType == CommonIpdConstants.DATE_TYPE) {
						if (value != null) {

							if (dicoData._CellEditor instanceof MyDateFieldEditor3) {
								DateFormatDateReEntrant dateFormatDateReEntrant = new DateFormatDateReEntrant();
								dateFormatDateReEntrant._Date = (Date) value;
								dateFormatDateReEntrant._Formatter = ((MyDateFieldEditor3) dicoData._CellEditor).getSdf(); // MyDateFieldEditor._Sdf;
								// getTableCellRendererComponent will call
								// setValue()
								component = super.getTableCellRendererComponent(table, dateFormatDateReEntrant, isSelected, hasFocus, row, column);
								return component;
							}
						}
					} else if ((column == 1) && intFieldType == CommonIpdConstants.TITLE_TYPE) {

					}
					Component parent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (!parent.isEnabled()) {
						parent.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);// TN115
						return parent;
					}
				}
				component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				return component;
			}

			/**
			 * special implementation for dates
			 */
			public void setValue(Object value) {
				if (value instanceof DateFormatDateReEntrant) {
					DateFormat formatter = ((DateFormatDateReEntrant) value)._Formatter;
					Date date = ((DateFormatDateReEntrant) value)._Date;
					// if (formatter == null) {
					// formatter = DateFormat.getDateInstance();
					// }
					setText(formatter.format(date));
				} else
					super.setValue(value);
			}

			/**
			 * special container to ensure formatter and date are grouped
			 * 
			 * @author cmi
			 * 
			 */
			class DateFormatDateReEntrant {
				DateFormat _Formatter;
				Date _Date;
			}

		};

		_Table = new JTable(_MyTableModel) {

			private static final long serialVersionUID = 1L;

			// return the editor according to the data type of the cell
			// (the column has not a fixed type!)
			public TableCellEditor getCellEditor(int row, int column) {
				return ((EncounterKeyValueTableModel) getModel()).getKeyValues().get(row)._CellEditor;
			}

			// write the date according same format their are entered
			public TableCellRenderer getCellRenderer(int row, int column) {
				// we save the current row which is in use, to return the
				// correct object class in the model
				_MyTableModel._ListIndexRenderer = row;
				DicoData dicoData = _MyTableModel.getKeyValues().get(row);
				int intFieldType = _MyTableModel.getModelTypes().get(dicoData._Key);
				if (column == 0) {
					if (intFieldType == CommonIpdConstants.TITLE_TYPE) {
						return titleLeftRenderer; // TN115
					}

					return leftColRenderer;// TN116
				} else if (column == 1) {
					return rightRenderer;
				}
				// else...
				return super.getCellRenderer(row, column);
			}

			@Override
			// TN114
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component c = super.prepareRenderer(renderer, row, col);
				if (!_MyTableModel.isCellEditable(row, col)) { // TN114
					c.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR); // TN115
					return c;
				}
				c.setBackground(Color.white);
				return c;
			}

		};

		_Table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		_Table.setFillsViewportHeight(true); // ?

		// column settings
		_Table.getColumnModel().getColumn(1).setMinWidth(50);
		_Table.getColumnModel().getColumn(1).setMaxWidth(AbstractWizardPanel.ANSWER_WIDTH);
		_Table.getColumnModel().getColumn(1).setPreferredWidth(AbstractWizardPanel.ANSWER_WIDTH);

		// register the change listener (useful business rules)
		_Table.getModel().addTableModelListener(this);

		// TN75 give focus to combobox component instead of F2
		_Table.setSurrendersFocusOnKeystroke(true);

		// TN108 trap focus to avoid user click save while text cell still in
		// edition
		_Table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	/**
	 * TN79 fill the keyValues map from currentEncounter info to update the
	 * model
	 * 
	 * @param currentPatient
	 */
	public void updateModelView(Encounter currentEncounter) {
		HashMap<String, String> keyValues = new HashMap<String, String>();

		// get the concept ids for this entity
		ArrayList<IdType> idTypes = _ConfigurationManager.getQuestionIdTypes(Encounter.class, currentEncounter.getType(), true);

		// get the labels per language for this entity
		String className = MiscelaneousUtils.getClassName(Encounter.class);
		HashMap<String, HashMap<String, String>> labels = _ConfigurationManager.getQuestionLabels(className, currentEncounter.getType());

		HashMap<String, ArrayList<KeyValue>> comboboxValueMap = _ConfigurationManager.getAllComboLabels(Encounter.class, currentEncounter.getType(), true);
		clearDataModel(idTypes, labels, comboboxValueMap);

		// ...and update the view, set all concept values
		// get the concept values...
		for (ConceptIdValue conceptIdValue : currentEncounter.getIdValues()) {

			keyValues.put(conceptIdValue.getConceptId(), conceptIdValue.getConceptValue());
		}

		setDataModel(idTypes, keyValues, labels, comboboxValueMap, currentEncounter);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _HelpButton) { // TN106
			String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_ENCOUNTERHELP);
			JOptionPane.showMessageDialog(null, tip, "", JOptionPane.PLAIN_MESSAGE, null);
		} else if (e.getSource() == _SaveButton) { // edit

			saveResults();
			getDescriptor().getWizard().setCurrentPanel(PatientListFormDescriptor.IDENTIFIER, true);
		} else if (e.getSource() == _CancelButton) {/* TN79 */
			// cancel only concept modifications (not entity values)
			cancelChanges();

		} else
			System.out.println(e.getSource());
	}

	/**
	 * 
	 * @return
	 */
	private int saveResults() {
		HashMap<String, String> results = _MyTableModel.getResults();
		try {
			getEntryFormConfigurationManager().saveEncounterAttributes(results, true);
			System.out.println("save Encounter Results ok");
			boolean encounterDataSaved = true;
			getEntryFormConfigurationManager().setEncounterAttributesDataSaved(encounterDataSaved);/* TN79 */
			getEntryFormConfigurationManager().setEncounterDataSaved(encounterDataSaved);
			getEntryFormConfigurationManager().setNewEncounterDataInsertion(false);

			// authorize next screen only if saved
			getDescriptor().getWizard().setNextFinishButtonEnabled(_ConfigurationManager.isEncounterDataSaved() && _ConfigurationManager.isEncounterAttributesDataSaved());/* TN79 */

			// TN78 show change in header
			buildCurPatientInfo(2);

		} catch (ParamException e) {
			e.printStackTrace();
			String errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_SAVE_FAILED) + ", " + e.getCause();

			if (e.getCause().toString().contains("ConstraintViolationException: Unique index or primary key violation"))
				errMess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ID_ALREADY_EXIST) + ", " + errMess;
			JOptionPane.showMessageDialog(null, errMess, "Error", JOptionPane.OK_OPTION, null);
			getDescriptor().getWizard().setNextFinishButtonEnabled(false);
		}

		return 0;
	}

	private int cancelChanges() {

		Encounter currentEncounter = _ConfigurationManager.getCurrentEncounter();
		updateModelView(currentEncounter);
		// redraw the table
		getTableModel().fireTableDataChanged();

		// flag data modification...
		boolean encounterDataSaved = true;
		getEntryFormConfigurationManager().setEncounterAttributesDataSaved(encounterDataSaved);
		// getEntryFormConfigurationManager().setEncounterDataSaved(encounterDataSaved);
		// // NO!
		// getEntryFormConfigurationManager().setNewEncounterDataInsertion(false);
		// // NO!

		// TN86 authorize back screen only if saved
		getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isEncounterDataSaved() && _ConfigurationManager.isEncounterAttributesDataSaved());/* TN86 */

		// TN78 show change in header
		buildCurPatientInfo(2);

		// disable cancel
		_CancelButton.setEnabled(false);

		return 0;
	}

	//
	// class DateRenderer extends DefaultTableCellRenderer {
	//
	// private static final long serialVersionUID = 1L;
	// private DateFormat formatter = new
	// SimpleDateFormat(CommonConstants.SIMPLE_DATE_FORMAT);
	//
	// // DateFormat formatter = MyDateFieldEditor._Sdf;
	//
	// public DateRenderer() {
	// super();
	// }
	//
	// public void setValue(Object value) {
	// if (formatter == null) {
	// formatter = DateFormat.getDateInstance();
	// }
	// setText((value == null) ? "" : formatter.format(value));
	// setHorizontalAlignment(SwingConstants.LEFT);
	// }
	// }
	//
	// class BooleanRenderer extends DefaultTableCellRenderer {
	//
	// private static final long serialVersionUID = 1L;
	//
	// public BooleanRenderer() {
	// super();
	// }
	//
	// public void setValue(Object value) {
	// System.out.println("setValue");
	// super.setValue(value);
	// setHorizontalAlignment(SwingConstants.LEFT);
	// }
	//
	// }

	// the model should be updated
	public void setDataModel(ArrayList<IdType> idTypes, HashMap<String, String> keyValuesNewData, HashMap<String, HashMap<String, String>> labels,
			HashMap<String, ArrayList<KeyValue>> comboboxValueMap, Encounter currentEncounter) {
		// clearTable();
		_MyTableModel.setIdTypes(idTypes);
		_MyTableModel.setLabels(labels);
		_MyTableModel.setComboboxValueMap(comboboxValueMap);
		_MyTableModel.reBuildModel(currentEncounter);
		_MyTableModel.setIdValues(keyValuesNewData);

		// refresh action rules
		refreshActionRules();
	}

	/**
	 * encounter has changed, refresh the action rules
	 */
	private void refreshActionRules() { // TN103
		int rowCount = getTableModel().getRowCount();
		// re-init the rules
		for (int row = 0; row < rowCount; row++) {
			initRowActions(row);
		}

		for (int row = 0; row < rowCount; row++) {
			Integer ret = runRowActions(row);
			if (ret != null) {
				// System.out.println("refreshActionRules: refresh action rule problem!");
			}
		}
	}

	// empty the model
	public void clearDataModel(ArrayList<IdType> idTypes, HashMap<String, HashMap<String, String>> labels, HashMap<String, ArrayList<KeyValue>> comboboxValueMap) {

		_MyTableModel.setIdTypes(idTypes);
		_MyTableModel.setLabels(labels);
		_MyTableModel.setComboboxValueMap(comboboxValueMap);
		_MyTableModel.reBuildModel(null);

		int rowCount = getTableModel().getRowCount();
		// re-init the rules
		for (int row = 0; row < rowCount; row++) {
			initRowActions(row);
		}

	}

	/**
	 * called by descriptor so that the panel can be initialized
	 */
	public void configurePanelStartState() {

		internationalize(); // TN97

		// do no selection
		if (getTable().getRowCount() > 0)
			getTable().removeRowSelectionInterval(0, getTable().getRowCount() - 1);

		// authorize next screen only if saved
		getDescriptor().getWizard().setNextFinishButtonEnabled(_ConfigurationManager.isPatientDataSaved());

		// authorize back screen only if saved
		getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isEncounterDataSaved());

		// TN51: scroll to the top of table
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_ScrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
			}
		});
	}

	/**
	 * TN97
	 */
	protected void internationalize() {
		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_SAVECOMMAND);
		_SaveButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton.setText(mess);
	}

	public EncounterKeyValueTableModel getTableModel() {
		return _MyTableModel;
	}

	public JTable getTable() {
		return _Table;
	}

	/**
	 * 
	 * @param row
	 * @return the concept associated value
	 */
	@Override
	protected String getConceptValueChanged(int row) {
		DicoData dicoData = getTableModel().getKeyValues().get(row);
		String idValue = null;
		// System.out.println("concept id = " + dicoData._Key);
		if (dicoData._Value != null) { // make sure there is a value
			int intFieldType = getTableModel().getModelTypes().get(dicoData._Key);
			//
			if (intFieldType == CommonIpdConstants.COMBO_TYPE) {
				ArrayList<KeyValue> values = getTableModel().getComboboxValueMap().get(dicoData._ComboName);
				if (values != null) {
					for (KeyValue keyValue : values) {
						if (keyValue._Value.equals(dicoData._Value.toString())) {
							idValue = keyValue._Key;
							// notFound = false;
							break;
						}
					}
				}
			} else
				idValue = dicoData._Value.toString();
		}
		return idValue;
	}

	@Override
	protected String getConceptIdChanged(int row) {
		return getTableModel().getKeyValues().get(row)._Key;
	}

	/**
	 * detect cell changes and mark the entity as changed
	 */
	@Override
	public void tableChanged(TableModelEvent e) {

		// do actions
		super.tableChanged(e);
		// flag data modification...
		boolean encounterDataSaved = false;
		getEntryFormConfigurationManager().setEncounterAttributesDataSaved(encounterDataSaved);/* TN79 */
		// TN86 authorize back screen only if saved
		getDescriptor().getWizard().setBackButtonEnabled(encounterDataSaved);

		/* TN79 */// enable cancel
		_CancelButton.setEnabled(true);

		// TN78 show change in header
		buildCurPatientInfo(2);

		_Table.repaint(); // TN114 (otherwise background cells not repainted
							// when business disable/enable cells)
	}

	@Override
	// TN97
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_ENCOUNTERDETAILSPANEL;
	}
}
