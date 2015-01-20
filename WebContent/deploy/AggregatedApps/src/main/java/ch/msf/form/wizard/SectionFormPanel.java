package ch.msf.form.wizard;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.error.ConfigException;
import ch.msf.form.MyDateFieldEditor3;
import ch.msf.form.ParamException;
import ch.msf.form.config.JTableSection;
import ch.msf.form.config.SectionTableKeyValueTableModel;
import ch.msf.form.config.SectionTableKeyValueTableModel.DicoData;
import ch.msf.model.ConceptIdValue;
import ch.msf.model.Section;
import ch.msf.model.SectionTable;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;

import com.nexes.wizard.WizardPanelDescriptor;

public class SectionFormPanel extends AbstractWizardPanel {

	private static final long serialVersionUID = 1L;

	private JButton _SaveButton; //
	private JButton _CancelButton; /*  */
	private JButton _HelpButton; //

	private JScrollPane _ScrollPane;

	private JPanel _TableSectionsPanel;

	public SectionFormPanel(WizardPanelDescriptor descriptor) {

		super(descriptor);

	}

	public JPanel getContentPanel() {

		JPanel contentPanelMaster = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));

		JPanel contentPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 2));
		contentPanelMaster.add(contentPanel1);

		JPanel excercicePanel = new JPanel();
		contentPanel1.add(excercicePanel);

		excercicePanel.setLayout(new BoxLayout(excercicePanel, BoxLayout.Y_AXIS));

		Dimension exerciceDim = new Dimension(EXERCISE_PANEL_WIDTH, EXERCISE_PANEL_HEIGHT);
		excercicePanel.setMinimumSize(exerciceDim);
		excercicePanel.setMaximumSize(exerciceDim);
		excercicePanel.setPreferredSize(exerciceDim);

		// current section info
		JPanel curSectionPanel = new JPanel();
		Dimension curSectionDim = new Dimension(AGGREG_INFO_PANEL_WIDTH, AGGREG_INFO_PANEL_HEIGHT);
		curSectionPanel.setMinimumSize(curSectionDim);
		curSectionPanel.setMaximumSize(curSectionDim);
		curSectionPanel.setPreferredSize(curSectionDim);
		curSectionPanel.setSize(curSectionDim);

		excercicePanel.add(curSectionPanel);
		_CurAggregationInfo = new JLabel();
		curSectionPanel.add(_CurAggregationInfo);
		_CurAggregationInfo.setText("");


		_TableSectionsPanel = new JPanel();
		_TableSectionsPanel.setLayout(new BoxLayout(_TableSectionsPanel, BoxLayout.Y_AXIS));

		_ScrollPane = new JScrollPane(_TableSectionsPanel);
		_ScrollPane.setMaximumSize(exerciceDim);

		excercicePanel.add(_ScrollPane);

		// buttons
		JPanel buttonPanel = new JPanel();
		Dimension buttonDim = new Dimension(BUTTON_PANEL_WIDTH, BUTTON_PANEL_HEIGHT);
		buttonPanel.setMinimumSize(buttonDim);
		buttonPanel.setMaximumSize(buttonDim);
		buttonPanel.setPreferredSize(buttonDim);
		buttonPanel.setSize(buttonDim);
		excercicePanel.add(buttonPanel);


		final int NBR_BUTTONS = 2; //
		buttonPanel.setLayout(new GridLayout(1, NBR_BUTTONS, 0, 3));// nbr

		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_SAVECOMMAND);
		_SaveButton = new JButton(mess);
		_SaveButton.addActionListener(this);

		buttonPanel.add(_SaveButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelButton = new JButton(mess);/*  */
		_CancelButton.addActionListener(this);
		_CancelButton.setEnabled(false);
		buttonPanel.add(_CancelButton);

		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton = new JButton(mess);
		_HelpButton.addActionListener(this);
		buttonPanel.add(_HelpButton);

		return contentPanelMaster;
	}

	private void buildSectionTable(SectionTable currentSectionTable) {

		// TODO ........OPTIMIZE....

		final DefaultTableCellRenderer labelLeftRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = null;
				if ((column == 0)) {
					if (hasFocus) // 
						table.changeSelection(row, column + 1, false, false);

					if (value != null) { // 
						JLabel parent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						// parent.setFont(parent.getFont().deriveFont(Font.BOLD));
						parent.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);//

						return parent;
					}
				}
				component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				return component;
			}

		};

		final DefaultTableCellRenderer titleRenderer = new DefaultTableCellRenderer() {
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component component = null;
//				if ((column == 0)) {
					if (hasFocus) // 
						table.changeSelection(row, column + 1, false, false);

					if (value != null) { // 
						JLabel parent = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
						parent.setFont(parent.getFont().deriveFont(Font.BOLD));
						parent.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);//

						return parent;
					}
//				}
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

				JTableSection jTablesection = (JTableSection) table;
				SectionTableKeyValueTableModel tableModel = jTablesection.getSectionTable().getSectionKeyValueTableModel();
				DicoData[] dicoData = tableModel.getKeyValues().get(row);

				int intFieldType = tableModel.getModelTypes().get(dicoData[column]._Key);

				if ((column >= 1)) {
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

							if (dicoData[row]._CellEditor instanceof MyDateFieldEditor3) {
								DateFormatDateReEntrant dateFormatDateReEntrant = new DateFormatDateReEntrant();
								dateFormatDateReEntrant._Date = (Date) value;
								dateFormatDateReEntrant._Formatter = ((MyDateFieldEditor3) dicoData[row]._CellEditor).getSdf(); //
								// getTableCellRendererComponent will call
								// setValue()
								component = super.getTableCellRendererComponent(table, dateFormatDateReEntrant, isSelected, hasFocus, row, column);
								return component;
							}
						}
					} else if (intFieldType == CommonIpdConstants.TITLE_TYPE) {

					}
					Component parent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
					if (!parent.isEnabled()) {
						parent.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);//
						//
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

		// create table

		// set table header
		String[] tableTitles = _ConfigurationManager.getTableTitleTranslation(currentSectionTable.getTableId());
		// System.out.println("tableTitles="+tableTitles.length);
		currentSectionTable.getSectionKeyValueTableModel()._ColumnNames = tableTitles;

		// check table header coherence
		if (tableTitles.length != currentSectionTable.getSectionKeyValueTableModel().getColumnCount()) {
			throw new ConfigException("Bad configuration (number of declared column is not identical with number of titles) for section table id:"
					+ currentSectionTable.getTableId());
		}

		JTableSection jTable = new JTableSection(currentSectionTable.getSectionKeyValueTableModel(), currentSectionTable) {

			private static final long serialVersionUID = 1L;

			// return the editor according to the data type of the cell
			// (the column has not a fixed type!)
			public TableCellEditor getCellEditor(int row, int column) {
				return ((SectionTableKeyValueTableModel) getModel()).getKeyValues().get(row)[column]._CellEditor;
			}

			// write the date according same format their are entered
			public TableCellRenderer getCellRenderer(int row, int column) {
				// we save the current row which is in use, to return the
				// correct object class in the model
				getTableModel(getSectionTable())._ListIndexRenderer = row;
				DicoData[] dicoData = getTableModel(getSectionTable()).getKeyValues().get(row);
				// try {
				int intFieldType = getTableModel(getSectionTable()).getModelTypes().get(dicoData[column]._Key);
				if (intFieldType == CommonIpdConstants.LABEL_TYPE) {
					return labelLeftRenderer; //
				}
				if (intFieldType == CommonIpdConstants.TITLE_TYPE) {
					return titleRenderer; //
				}

				return rightRenderer;
			}

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component c = super.prepareRenderer(renderer, row, col);
				if (!getTableModel(getSectionTable()).isCellEditable(row, col)) { //
					c.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);
					//
					return c;
				}
				c.setBackground(Color.white);
				return c;
			}

		};

		// // register the change listener (useful business rules)
		jTable.getModel().addTableModelListener(this);
		//
		// give focus to combobox component instead of F2
		jTable.setSurrendersFocusOnKeystroke(true);

		// trap focus to avoid user click save while text cell
		// still in edition
		jTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		// add the table
		JScrollPane scrollPane = new JScrollPane(jTable);
		// _TableSectionsPanel.getComponentCount();
		_TableSectionsPanel.add(scrollPane);

		int rowCount = currentSectionTable.getSectionKeyValueTableModel().getRowCount();
		int tableHeight = rowCount * CELL_HEIGHT + CELL_HEIGHT /* +header size */;
		Dimension curTableDim = new Dimension(AGGREG_INFO_PANEL_WIDTH, tableHeight + CELL_HEIGHT);
		scrollPane.setMinimumSize(curTableDim);
		scrollPane.setMaximumSize(curTableDim);
		scrollPane.setPreferredSize(curTableDim);
		jTable.setRowHeight(CELL_HEIGHT);
		
//		TN16
		Font f = jTable.getTableHeader().getFont();
		Font boldFont = f.deriveFont(Font.BOLD);
		jTable.getTableHeader().setFont(boldFont);

		_TableSectionsPanel.setBorder(BorderFactory.createLineBorder(Color.gray));
		// jTable.setBorder(BorderFactory.createLineBorder(Color.blue));
		scrollPane.setBorder(BorderFactory.createLineBorder(Color.black));

		// TN7 set 1rst col width with max value size
		int width = 0;
		// get header width
		DefaultTableColumnModel colModel = (DefaultTableColumnModel) jTable.getColumnModel();
		TableColumn col = colModel.getColumn(0);
		java.awt.Component comp = labelLeftRenderer.getTableCellRendererComponent(jTable, col.getHeaderValue(), false, false, 0, 0);
		width = comp.getPreferredSize().width;
		// get max row from 1rst col width
		for (int r = 0; r < jTable.getRowCount(); r++) {
			comp = labelLeftRenderer.getTableCellRendererComponent(jTable, jTable.getValueAt(r, 0), false, false, r, 0);
			width = Math.max(width, comp.getPreferredSize().width);
		}
		col.setPreferredWidth(width);
	}

	/**
	 * fill the keyValues map from currentSection info to update the model
	 * 
	 * @param currentSection
	 */
	public void updateModelView(Section currentSection) {
		// clear all the section tables
		_TableSectionsPanel.removeAll();

		// for each section table
		// first clear data model...  ! TN9
		for (SectionTable currentSectionTable : currentSection.getSectionTables()) {
			try {
				HashMap<String, String> keyValues = new HashMap<String, String>();
				//
				// get the concept ids for this entity
				ArrayList<IdType[]> idTypes = _ConfigurationManager.getSectionTableQuestionIdTypes(currentSectionTable.getTableId());
				// HashMap<locale, HashMap<conceptId, conceptLabel>>>
				HashMap<String, HashMap<String, String>> allLabels = _ConfigurationManager.getSectionTableQuestionLabels(currentSection.getThemeCode());

				// HashMap<String, ArrayList<KeyValue>> comboboxValueMap =
				// _ConfigurationManager.getAllComboLabels(Section.class,currentSection.getThemeId(),
				// true);
				clearDataModel(idTypes, allLabels, null,/* comboboxValueMap, */currentSectionTable.getSectionKeyValueTableModel());

			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal error on " + currentSectionTable.getTableId(), JOptionPane.OK_OPTION, null);
				return;
			}
		}
		
		// for each section table
		// ...then set data model...  ! TN9
		for (SectionTable currentSectionTable : currentSection.getSectionTables()) {
			try {
				HashMap<String, String> keyValues = new HashMap<String, String>();
				//
				// get the concept ids for this entity
				ArrayList<IdType[]> idTypes = _ConfigurationManager.getSectionTableQuestionIdTypes(currentSectionTable.getTableId());
				// HashMap<locale, HashMap<conceptId, conceptLabel>>>
				HashMap<String, HashMap<String, String>> allLabels = _ConfigurationManager.getSectionTableQuestionLabels(currentSection.getThemeCode());

				// ...and update the table values,
				// get the concept values...only those for this section
				ArrayList<DicoData[]> arrayList = currentSectionTable.getSectionKeyValueTableModel().getKeyValues();
				// only add the concepts that belong to this section
				for (ConceptIdValue conceptIdValue : currentSection.getIdValues()) { //
					for (DicoData[] dicoDataArray : arrayList) {
						int colSize = dicoDataArray.length;
						for (int colIndex = 0; colIndex < colSize; colIndex++) {
							String conceptId = dicoDataArray[colIndex]._Key;
							if (conceptId.equals(conceptIdValue.getConceptId())) {
								keyValues.put(conceptId, conceptIdValue.getConceptValue());
							}
						}
					}
				}
				// fill the table with labels as they are values
				HashMap<String, String> labels = allLabels.get(_ConfigurationManager.getDefaultLanguage());
				for (String conceptIdLabel : labels.keySet()) { //
					for (DicoData[] dicoDataArray : arrayList) {
						int colSize = dicoDataArray.length;
						for (int colIndex = 0; colIndex < colSize; colIndex++) {
							String conceptId = dicoDataArray[colIndex]._Key;
							if (conceptId.equals(conceptIdLabel)) {
								keyValues.put(conceptId, labels.get(conceptIdLabel));
							}
						}
					}

				}
				// fill the tables
				setDataModel(idTypes, keyValues, /* comboboxValueMap */null, currentSectionTable);
				buildSectionTable(currentSectionTable);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal error on " + currentSectionTable.getTableId(), JOptionPane.OK_OPTION, null);
				return;
			}
		}

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _HelpButton) { //
			String tip = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_TOOLTIP_SECTIONHELP);
			JOptionPane.showMessageDialog(null, tip, "", JOptionPane.PLAIN_MESSAGE, null);
		} else if (e.getSource() == _SaveButton) { // edit

			saveResults();
			// getDescriptor().getWizard().setCurrentPanel(AggregationListFormDescriptor.IDENTIFIER,
			// true);
		} else if (e.getSource() == _CancelButton) {/*  */
			// cancel only concept modifications (not entity values)
			cancelChanges();

		} else
			System.out.println(e.getSource());
	}

	/**
	 * save each section tables
	 * 
	 * @return
	 */
	private int saveResults() {

		HashMap<String, String> allResults = new HashMap<String, String>();

		Section currentSection = _ConfigurationManager.getCurrentSection();
		// for each section table
		for (SectionTable currentSectionTable : currentSection.getSectionTables()) {
			HashMap<String, String> results = currentSectionTable.getSectionKeyValueTableModel().getResults();
			allResults.putAll(results);
		}
		try {
			currentSection.setDone(true);
			currentSection = getEntryFormConfigurationManager().saveSectionAttributes(allResults, true);
			System.out.println("save Section Results ok...");
			boolean sectionDataSaved = true;
			getEntryFormConfigurationManager().setSectionAttributesDataSaved(sectionDataSaved);
			getEntryFormConfigurationManager().setSectionDataSaved(sectionDataSaved);
			getEntryFormConfigurationManager().setNewSectionDataInsertion(false);

			// authorize next screen only if saved
			// getDescriptor().getWizard().setNextFinishButtonEnabled(_ConfigurationManager.isSectionDataSaved()
			// && _ConfigurationManager.isSectionAttributesDataSaved());/* */
			// never authorize next screen
			getDescriptor().getWizard().setNextFinishButtonEnabled(false);
			// authorize back screen only if saved
			getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isSectionDataSaved() && _ConfigurationManager.isSectionAttributesDataSaved());/*  */

			// show change in header
			buildCurAggregationInfo(2);

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

		Section currentSection = _ConfigurationManager.getCurrentSection();
		updateModelView(currentSection);
		// redraw the tables
		// for each section table
		for (SectionTable currentSectionTable : currentSection.getSectionTables()) {

			currentSectionTable.getSectionKeyValueTableModel().fireTableDataChanged();
		}

		// flag data modification...
		boolean sectionDataSaved = true;
		getEntryFormConfigurationManager().setSectionAttributesDataSaved(sectionDataSaved);
		//
		getEntryFormConfigurationManager().setSectionDataSaved(sectionDataSaved);

		//
		getEntryFormConfigurationManager().setNewSectionDataInsertion(false);

		// authorize back screen only if saved
		getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isSectionDataSaved() && _ConfigurationManager.isSectionAttributesDataSaved());/*  */

		// show change in header
		buildCurAggregationInfo(2);

		// disable cancel
		_CancelButton.setEnabled(false);

		return 0;
	}

	// the model should be updated
	public void setDataModel(ArrayList<IdType[]> idTypes, HashMap<String, String> keyValuesNewData, HashMap<String, ArrayList<KeyValue>> comboboxValueMap,
			SectionTable currentSectionTable) {
		// clearTable();
		currentSectionTable.getSectionKeyValueTableModel().setIdTypes(idTypes);
		// currentSectionTable.getSectionKeyValueTableModel().setLabels(allLabels);
		currentSectionTable.getSectionKeyValueTableModel().setComboboxValueMap(comboboxValueMap);
		currentSectionTable.getSectionKeyValueTableModel().reBuildModel(/* currentSectionTable */);

		currentSectionTable.getSectionKeyValueTableModel().setIdValues(keyValuesNewData);

		// refresh action rules
		refreshActionRules(currentSectionTable.getSectionKeyValueTableModel());
	}

	/**
	 * section has changed, refresh the action rules
	 */
	private void refreshActionRules(SectionTableKeyValueTableModel currentSectionTableModel) { //
		int rowCount = currentSectionTableModel.getRowCount();
		int colCount = currentSectionTableModel.getColumnCount();

		// re-init the rules UTILE??
		// for (int row = 0; row < rowCount; row++) {
		// for (int col = 0; col < colCount; col++)
		// initRowActions(row, col, currentSectionTableModel);
		// }

		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++) {
				Integer ret = runRowActions(row, col, currentSectionTableModel);
				if (ret != null) {
					//
					System.out.println("refreshActionRules: refresh action rule problem!");
				}
			}
		}
	}

	// empty the model
	public void clearDataModel(ArrayList<IdType[]> idTypes, HashMap<String, HashMap<String, String>> labels, HashMap<String, ArrayList<KeyValue>> comboboxValueMap,
			SectionTableKeyValueTableModel currentSectionTableModel) {

		currentSectionTableModel.setIdTypes(idTypes);
		currentSectionTableModel.setLabels(labels);
		currentSectionTableModel.setComboboxValueMap(comboboxValueMap);
		currentSectionTableModel.reBuildModel();

		int rowCount = currentSectionTableModel.getRowCount();
		int colCount = currentSectionTableModel.getColumnCount();
		// re-init the rules
		for (int row = 0; row < rowCount; row++) {
			for (int col = 0; col < colCount; col++)
				initRowActions(row, col, currentSectionTableModel);
		}

	}

	/**
	 * called by descriptor so that the panel can be initialized
	 */
	public void configurePanelStartState() {

		internationalize(); //

		// do no selection
		// if (getTable().getRowCount() > 0)
		// getTable().removeRowSelectionInterval(0, getTable().getRowCount() -
		// 1);

		// // authorize next screen only if saved
		// getDescriptor().getWizard().setNextFinishButtonEnabled(_ConfigurationManager.isAggregatedDataSaved());
		// never authorize next screen
		getDescriptor().getWizard().setNextFinishButtonEnabled(false);

		// authorize back screen only if saved
		getDescriptor().getWizard().setBackButtonEnabled(_ConfigurationManager.isSectionDataSaved());

		// scroll to the top of table
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				_ScrollPane.getViewport().setViewPosition(new java.awt.Point(0, 0));
			}
		});
	}

	/**
	 * 
	 */
	protected void internationalize() {
		String mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_SAVECOMMAND);
		_SaveButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_CANCELCOMMAND);
		_CancelButton.setText(mess);
		mess = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_PANNEL_HELPCOMMAND);
		_HelpButton.setText(mess);
	}

	public SectionTableKeyValueTableModel getTableModel(SectionTable sectionTable) {
		return sectionTable.getSectionKeyValueTableModel();
	}

	@Override
	public DefaultTableModel getTableModel() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void changeTableColumnTitles(String[] tableTitles, JTable sectionTable) {
		int colCount = sectionTable.getModel().getColumnCount();
		for (int i = 0; i < colCount; i++) {
			sectionTable.getColumnModel().getColumn(i).setHeaderValue(tableTitles[i]);
		}
	}

	public JTable getTable() {
		return null;// _Table;
	}

	/**
	 * 
	 * @param row
	 * @return the concept associated value
	 */
	@Override
	protected String getConceptValueChanged(int row, int column, SectionTableKeyValueTableModel sectionTableKeyValueTableModel) {
		DicoData[] dicoData = sectionTableKeyValueTableModel.getKeyValues().get(row);
		String idValue = null;
		// System.out.println("concept id = " + dicoData._Key);
		if (dicoData[column]._Value != null) { // make sure there is a value
			int intFieldType = sectionTableKeyValueTableModel.getModelTypes().get(dicoData[column]._Key);
			//
			if (intFieldType == CommonIpdConstants.COMBO_TYPE) {
				ArrayList<KeyValue> values = sectionTableKeyValueTableModel.getComboboxValueMap().get(dicoData[column]._ComboName);
				if (values != null) {
					for (KeyValue keyValue : values) {
						if (keyValue._Value.equals(dicoData[column]._Value.toString())) {
							idValue = keyValue._Key;
							// notFound = false;
							break;
						}
					}
				}
			} else
				idValue = dicoData[column]._Value.toString();
		}
		return idValue;
	}

	@Override
	protected String getConceptIdChanged(int row, int column, SectionTableKeyValueTableModel sectionTableKeyValueTableModel) {
		return sectionTableKeyValueTableModel.getKeyValues().get(row)[column]._Key;
	}

	/**
	 * detect cell changes and mark the entity as changed
	 */
	@Override
	public void tableChanged(TableModelEvent e) {

		// do actions
		super.tableChanged(e);
		// runBusinessActions(e);

		// flag data modification...
		boolean sectionDataSaved = false;
		getEntryFormConfigurationManager().setSectionAttributesDataSaved(sectionDataSaved);/*  */
		//  authorize back screen only if saved
		getDescriptor().getWizard().setBackButtonEnabled(sectionDataSaved);

		/*  */// enable cancel
		_CancelButton.setEnabled(true);

		// show change in header
		buildCurAggregationInfo(2);

	}

	@Override
	// 
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_SECTIONDETAILSPANEL;
	}
}
