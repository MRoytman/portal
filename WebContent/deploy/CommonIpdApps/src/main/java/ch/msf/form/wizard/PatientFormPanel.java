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
import java.util.List;

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
import ch.msf.form.config.PatientKeyValueTableModel;
import ch.msf.form.config.PatientKeyValueTableModel.DicoData;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.PatientIdValue;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;
import ch.msf.util.MiscelaneousUtils;

public class PatientFormPanel extends AbstractWizardPanel implements ActionListener, TableModelListener {

	private static final long serialVersionUID = 1L;

	private JButton _SaveButton; //
	private JButton _CancelButton; /* TN79 */
	private JButton _HelpButton; // TN106
	
	private JScrollPane _ScrollPane;

	protected PatientKeyValueTableModel _MyTableModel;

	private JTable _Table;

	public PatientFormPanel() {

		super();

	}

	public JPanel getContentPanel() {

		// get the concept ids for this entity
		ArrayList<IdType> idTypes = _ConfigurationManager.getQuestionIdTypes(Patient.class, null, true);
		// get the labels per language for this entity
		String className = MiscelaneousUtils.getClassName(Patient.class);
		HashMap<String, HashMap<String, String>> labels = _ConfigurationManager.getQuestionLabels(className, null);

		HashMap<String, ArrayList<KeyValue>> comboboxValueMap = _ConfigurationManager.getAllComboLabels(Patient.class, null, true);

		_MyTableModel = new PatientKeyValueTableModel(idTypes, labels, comboboxValueMap);

		JPanel contentPanelMaster = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		JPanel contentPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		contentPanelMaster.add(contentPanel1);

		JPanel excercicePanel = new JPanel();
		contentPanel1.add(excercicePanel);

		// contentPanel1.setBorder(BorderFactory.createLineBorder(Color.green));

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

		buildTable();

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
		// excercicePanel.setBorder(BorderFactory.createLineBorder(Color.red));

		// buttons
		JPanel buttonPanel = new JPanel();
		Dimension buttonDim = new Dimension(BUTTON_PANEL_WIDTH, BUTTON_PANEL_HEIGHT);
		buttonPanel.setMinimumSize(buttonDim);
		buttonPanel.setMaximumSize(buttonDim);
		buttonPanel.setPreferredSize(buttonDim);
		buttonPanel.setSize(buttonDim);
		excercicePanel.add(buttonPanel);

		final int NBR_BUTTONS = 3; //
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

					// // get type
					// DicoData dicoData =
					// _MyTableModel.getKeyValues().get(row);
					// int intFieldType =
					// _MyTableModel.getModelTypes().get(dicoData._Key);
					//
					// if (intFieldType == CommonIpdConstants.TITLE_TYPE) {
					// // just to render the combobox
					// if (value != null) { // TN96
					// JLabel parent = (JLabel)
					// super.getTableCellRendererComponent(table, value,
					// isSelected, hasFocus, row, column);
					// parent.setFont(parent.getFont().deriveFont(Font.BOLD));
					// // parent.setBackground(parent.getBackground().cyan);
					// return parent;
					// }
					// }
				}
				component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				return component;
			}

		};

		// final DefaultTableCellRenderer titleRightRenderer = new
		// DefaultTableCellRenderer() {
		// public Component getTableCellRendererComponent(JTable table, Object
		// value, boolean isSelected, boolean hasFocus, int row, int column) {
		// Component component = null;
		// if ((column == 1)) {
		//
		// JLabel parent = (JLabel) super.getTableCellRendererComponent(table,
		// value, isSelected, hasFocus, row, column);
		// parent.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);
		// //TN115
		// return parent;
		// }
		// component = super.getTableCellRendererComponent(table, value,
		// isSelected, hasFocus, row, column);
		// return component;
		// }
		//
		// };

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
				return ((PatientKeyValueTableModel) getModel()).getKeyValues().get(row)._CellEditor;
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
					// if (intFieldType == CommonIpdConstants.TITLE_TYPE) {
					// return titleRightRenderer;
					// }
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

		// let's make a renderer only for the right column
		// just to render the combobox and dates...
		// DefaultTableCellRenderer rightColRenderer = new
		// DefaultTableCellRenderer() {
		//
		// public Component getTableCellRendererComponent(JTable table, Object
		// value, boolean isSelected, boolean hasFocus, int row, int column) {
		// Component component = null;
		// if ((column == 1)) {
		// // get type
		// DicoData dicoData = _MyTableModel.getKeyValues().get(row);
		// int intFieldType = _MyTableModel.getModelTypes().get(dicoData._Key);
		//
		// if (intFieldType == CommonIpdConstants.BOOLEAN_TYPE) {
		// // just to render the combobox
		// if (value != null) {
		// JCheckBox checkBox = new JCheckBox();
		// checkBox.setSelected((Boolean) value);
		// return checkBox;
		// }
		// } else if (intFieldType == CommonIpdConstants.DATE_TYPE) {
		// if (value != null) {
		//
		// if (dicoData._CellEditor instanceof MyDateFieldEditor3) {
		// DateFormatDateReEntrant dateFormatDateReEntrant = new
		// DateFormatDateReEntrant();
		// dateFormatDateReEntrant._Date = (Date) value;
		// dateFormatDateReEntrant._Formatter = ((MyDateFieldEditor3)
		// dicoData._CellEditor).getSdf();
		// // getTableCellRendererComponent will call
		// // setValue()
		// component = super.getTableCellRendererComponent(table,
		// dateFormatDateReEntrant, isSelected, hasFocus, row, column);
		// return component;
		// }
		// }
		// }
		// }
		// component = super.getTableCellRendererComponent(table, value,
		// isSelected, hasFocus, row, column);
		// return component;
		// }
		//
		// /**
		// * special implementation for dates
		// */
		// public void setValue(Object value) {
		// if (value instanceof DateFormatDateReEntrant) {
		// DateFormat formatter = ((DateFormatDateReEntrant) value)._Formatter;
		// Date date = ((DateFormatDateReEntrant) value)._Date;
		//
		// setText(formatter.format(date));
		// } else
		// super.setValue(value);
		// }
		//
		// /**
		// * special container to ensure formatter and date are grouped
		// *
		// * @author cmi
		// *
		// */
		// class DateFormatDateReEntrant {
		// DateFormat _Formatter;
		// Date _Date;
		// }
		//
		// };
		// // ...and align all cells to left
		// rightColRenderer.setHorizontalAlignment(JLabel.LEFT);
		// _Table.getColumnModel().getColumn(1).setCellRenderer(rightColRenderer);

		// let's make a renderer only for the left column
		// just to render the titles in bold...
		// DefaultTableCellRenderer leftColRenderer = new
		// DefaultTableCellRenderer() {
		// public Component getTableCellRendererComponent(JTable table, Object
		// value, boolean isSelected, boolean hasFocus, int row, int column) {
		// Component component = null;
		// if ((column == 0)) {
		//
		// if (hasFocus) // TN100
		// table.changeSelection(row, column + 1, false, false);
		//
		// // get type
		// DicoData dicoData = _MyTableModel.getKeyValues().get(row);
		// int intFieldType = _MyTableModel.getModelTypes().get(dicoData._Key);
		//
		// if (intFieldType == CommonIpdConstants.TITLE_TYPE) {
		// // just to render the combobox
		// if (value != null) { //TN96
		// JLabel parent = (JLabel) super.getTableCellRendererComponent(table,
		// value, isSelected, hasFocus, row, column);
		// parent.setFont(parent.getFont().deriveFont(Font.BOLD));
		// return parent;
		// }
		// }
		// }
		// component = super.getTableCellRendererComponent(table, value,
		// isSelected, hasFocus, row, column);
		// return component;
		// }
		// };
		// leftColRenderer.setBackground(CommonIpdConstants.LIST_LABEL_COL_BACK_GRND_COLOR);
		//
		// _Table.getColumnModel().getColumn(0).setCellRenderer(leftColRenderer);

		// register the change listener (useful business rules)
		_Table.getModel().addTableModelListener(this);

		// TN75 give focus to combobox component instead of F2
		_Table.setSurrendersFocusOnKeystroke(true);

		// TN108 trap focus to avoid user click save while text cell still in
		// edition
		_Table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	/**
	 * fill the keyValues map from currentPatient info to update the model
	 * 
	 * @param currentPatient
	 */
	public void updateModelView(Patient currentPatient) {

		// clear the model...
		// get the concept ids for this entity
		ArrayList<IdType> idTypes = _ConfigurationManager.getQuestionIdTypes(Patient.class, null, true);
		// get the labels per language for this entity
		String className = MiscelaneousUtils.getClassName(Patient.class);
		HashMap<String, HashMap<String, String>> labels = _ConfigurationManager.getQuestionLabels(className, null);

		HashMap<String, ArrayList<KeyValue>> comboboxValueMap = _ConfigurationManager.getAllComboLabels(Patient.class, null, true);
		clearDataModel(idTypes, labels, comboboxValueMap);

		// ...and update the view, set all concept values

		// get the attribute mapping for this entity
		ArrayList<KeyValue> keyValueAttributeMappings = _ConfigurationManager.getQuestionIdToClassAttributes(Patient.class, null);

		// keyValues: a map to update the table model
		HashMap<String, String> keyValues = new HashMap<String, String>();
		// get the concept values...

		List<PatientIdValue> patientIdValues = null;
		try {
			patientIdValues = currentPatient.getIdValues();
			for (PatientIdValue patientIdValue : patientIdValues) {
			}
			;
		} catch (org.hibernate.LazyInitializationException e) {
			boolean all = true;
			currentPatient = ServiceHelper.getPatientManagerService().readDBPatientInfo(currentPatient, all);
			patientIdValues = currentPatient.getIdValues();
		}

		for (PatientIdValue patientIdValue : patientIdValues) {

			String conceptId = patientIdValue.getConceptId();
			// if (s.equals("456"))
			// System.out.println();
			boolean found = false;
			for (KeyValue keyValueAttributeMapping : keyValueAttributeMappings) {
				if (keyValueAttributeMapping._Key.equals(conceptId)) {
					found = true;
					break;
				}
			}
			// do not update the model with info that are displayed in the list
			if (!found)
				keyValues.put(conceptId, patientIdValue.getConceptValue());
		}
		// update the model
		setDataModel(idTypes, keyValues, labels, comboboxValueMap, currentPatient);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _HelpButton) { // TN106
			String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_PATIENTHELP);
			JOptionPane.showMessageDialog(null, tip, "", JOptionPane.PLAIN_MESSAGE, null);
		} else if (e.getSource() == _SaveButton) { // edit

			saveResults();
		} else if (e.getSource() == _CancelButton) { /* TN79 */
			// // cancel only concept modifications (not entity values)
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
			Patient currentPatient = getEntryFormConfigurationManager().savePatientAttributes(results, true);
			System.out.println("saveResults ok");
			boolean patientDataSaved = true;
			getEntryFormConfigurationManager().setPatientDataSaved(patientDataSaved);
			getEntryFormConfigurationManager().setPatientAttributesDataSaved(patientDataSaved);/* TN79 */
			getEntryFormConfigurationManager().setNewPatientDataInsertion(false);

			// update the patient index
			getEntryFormConfigurationManager().getCurrentPatientModelData().setIndex(currentPatient.getIndex().toString());

			// autorize next screen only if saved
			getDescriptor().getWizard().setNextFinishButtonEnabled(_ConfigurationManager.isPatientDataSaved() && _ConfigurationManager.isPatientAttributesDataSaved());/* TN79 */
			// TN86 autorize back screen only if saved
			getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isPatientDataSaved() && _ConfigurationManager.isPatientAttributesDataSaved());/* TN86 */

			// TN78 show change in header
			buildCurPatientInfo(1);// TN118

		} catch (Exception e) {
			String errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_SAVE_FAILED) + ", " + e.getCause();

			if (e.getCause().toString().contains("ConstraintViolationException: Unique index or primary key violation"))
				errMess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ID_ALREADY_EXIST) + ".\n " + errMess;
			JOptionPane.showMessageDialog(null, errMess, "Error", JOptionPane.OK_OPTION, null);
//			getDescriptor().getWizard().setNextFinishButtonEnabled(false);
//			getDescriptor().getWizard().setBackButtonEnabled(true); // allow to go back
			
			_ConfigurationManager.setCurrentPatientCancelled(true); // cancel patient 
			// go back to previous screen
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					getDescriptor().getWizard().setCurrentPanel(PatientListFormDescriptor.IDENTIFIER, true);
				}
			});
		}
		return 0;
	}

	private int cancelChanges() {

		// cancel only concept modifications (not entity values)
		PatientContext currentPatientContext = _ConfigurationManager.getCurrentPatientContext();

		// flag data modification...
		boolean patientDataSaved = true;

		// disable cancel
		_CancelButton.setEnabled(false);
		updateModelView(currentPatientContext.getPatient());
		// redraw the table after updating the model
		getTableModel().fireTableDataChanged();

		getEntryFormConfigurationManager().setPatientDataSaved(patientDataSaved);
		getEntryFormConfigurationManager().setPatientAttributesDataSaved(patientDataSaved);/* TN79 */
		getEntryFormConfigurationManager().setNewPatientDataInsertion(false);

		// authorize next screen only if saved
		getDescriptor().getWizard().setNextFinishButtonEnabled(_ConfigurationManager.isPatientDataSaved() && _ConfigurationManager.isPatientAttributesDataSaved());/* TN79 */
		// TN86 authorize back screen only if saved
		getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isPatientDataSaved() && _ConfigurationManager.isPatientAttributesDataSaved());/* TN86 */

		// TN78 show change in header
		buildCurPatientInfo(1);// TN118

		return 0;
	}

	// the model should be updated
	public void setDataModel(ArrayList<IdType> idTypes, HashMap<String, String> keyValuesNewData, HashMap<String, HashMap<String, String>> labels,
			HashMap<String, ArrayList<KeyValue>> comboboxValueMap, Patient currentPatient) {

		// clearTable();
		_MyTableModel.setIdTypes(idTypes);
		_MyTableModel.setLabels(labels);
		_MyTableModel.setComboboxValueMap(comboboxValueMap);
		_MyTableModel.reBuildModel(currentPatient);
		_MyTableModel.setIdValues(keyValuesNewData);

		// refresh action rules
		refreshActionRules();
	}

	/**
	 * patient has changed, refresh the action rules
	 */
	private void refreshActionRules() { // TN103
		int rowCount = getTableModel().getRowCount();
		// // re-init the rules
		// for (int row = 0; row < rowCount; row++) {
		// initRowActions(row);
		// }

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
		getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isPatientDataSaved());

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

	public PatientKeyValueTableModel getTableModel() {
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
		boolean patientDataSaved = false;
		getEntryFormConfigurationManager().setPatientAttributesDataSaved(patientDataSaved);/* TN79 */
		// authorize next screen only if saved
		getDescriptor().getWizard().setNextFinishButtonEnabled(patientDataSaved);
		// TN86 authorize back screen only if saved
		getDescriptor().getWizard().setBackButtonEnabled(patientDataSaved);

		/* TN79 */// enable cancel
		_CancelButton.setEnabled(true);

		// TN78 show change in header
		buildCurPatientInfo(1);// TN118

		_Table.repaint(); // TN114 (otherwise background cells not repainted
							// when business disable/enable cells)
	}

	@Override
	// TN97
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_PATIENTDETAILSPANEL;
	}
}
