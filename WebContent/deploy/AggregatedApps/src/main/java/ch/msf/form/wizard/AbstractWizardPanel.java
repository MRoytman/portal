package ch.msf.form.wizard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.form.config.SectionTableKeyValueTableModel;
import ch.msf.manager.ConfigurationManager;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.model.Section;
import ch.msf.model.datavalidator.DataValidator;
import ch.msf.model.datavalidator.DefaultDataValidator;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public abstract class AbstractWizardPanel extends JPanel implements ActionListener, TableModelListener {

	private static final long serialVersionUID = 1L;
	public static final int CELL_SPACING = 10;
	public static final int CELL_HEIGHT = 20;

//	public static final int QUESTION_WIDTH = 300;
	public static final int QUESTION_HEIGHT = CELL_HEIGHT;
//	public static final int ANSWER_WIDTH = 300;
	public static final int ANSWER_HEIGHT = QUESTION_HEIGHT;

	public static final int QUESTION_ANSWER_PANEL_WIDTH = 800;
	// public static final int QUESTION_ANSWER_PANEL_HEIGHT = 0; // CELL_HEIGHT
	// *2

	public static final int AGGREG_INFO_PANEL_HEIGHT = CELL_HEIGHT * 2;
	public static final int AGGREG_INFO_PANEL_WIDTH = QUESTION_ANSWER_PANEL_WIDTH - 15;

	public static final int EXERCISE_LIST_HEIGHT = 400; // TN104
	public static final int EXERCISE_LIST_WIDTH = QUESTION_ANSWER_PANEL_WIDTH;

	public static final int BUTTON_PANEL_HEIGHT = CELL_HEIGHT * 2;
	public static final int BUTTON_PANEL_WIDTH = QUESTION_ANSWER_PANEL_WIDTH;

	public static final int EXERCISE_PANEL_HEIGHT = AGGREG_INFO_PANEL_HEIGHT + EXERCISE_LIST_HEIGHT + BUTTON_PANEL_HEIGHT + 5;
	public static final int EXERCISE_PANEL_WIDTH = QUESTION_ANSWER_PANEL_WIDTH + 5;

	public static final int STATEINFO_WIDTH = 180;

	public static final int FINDER_TEXT_WIDTH = 100; // TN9

	protected SimpleDateFormat _Sdf = new SimpleDateFormat(CommonConstants.SIMPLE_DATE_FORMAT);

	private JPanel contentPanel;
	private JLabel iconLabel;
	private JSeparator separator;
	protected JLabel _CurrentWizardState;
	protected JLabel _CurrentContextInfo;
	private JPanel titlePanel;
	private String _PannelState;

	protected WizardPanelDescriptor _Descriptor;

	protected JLabel _CurAggregationInfo; // Aggregated info

	protected EntryFormConfigurationManager _ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();

	public AbstractWizardPanel(WizardPanelDescriptor descriptor) {

		super();

		System.out.println("Starting initializing " + getClass().getName());
		setDescriptor(descriptor);
		contentPanel = getContentPanel();
		contentPanel.setBorder(new EmptyBorder(new Insets(1, 1, 1, 1)));
		// // change background color
		// contentPanel.setOpaque(true);
		// contentPanel.setBackground(Constants.MAIN_PANEL_BACK_GRND_COLOR);

		ImageIcon icon = getImageIcon();

		titlePanel = new javax.swing.JPanel();
		_CurrentWizardState = new javax.swing.JLabel();
		iconLabel = new javax.swing.JLabel();
		separator = new javax.swing.JSeparator();
		separator.setBackground(Color.gray);

		setLayout(new java.awt.BorderLayout(0, 0));

		titlePanel.setLayout(new java.awt.BorderLayout(0, 0));

		_CurrentWizardState.setFont(new Font("MS Sans Serif", Font.BOLD, 14));

		_CurrentWizardState.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
		// change background color
		_CurrentWizardState.setOpaque(true);
		_CurrentWizardState.setBackground(Color.white);
		Dimension infoStateDim = new Dimension(STATEINFO_WIDTH, 50);
		_CurrentWizardState.setMinimumSize(infoStateDim);
		_CurrentWizardState.setMaximumSize(infoStateDim);
		_CurrentWizardState.setPreferredSize(infoStateDim);

		_CurrentContextInfo = new javax.swing.JLabel();
		_CurrentContextInfo.setFont(new Font("MS Sans Serif", Font.BOLD, 12));

		_CurrentContextInfo.setBorder(new EmptyBorder(new Insets(3, 3, 3, 3)));
		_CurrentContextInfo.setHorizontalAlignment(SwingConstants.CENTER);
		// change background color
		_CurrentContextInfo.setOpaque(true);
		_CurrentContextInfo.setBackground(CommonIpdConstants.MAIN_PANEL_CONTEXT_BACK_GRND_COLOR);
		// _CurrentContextInfo.setBorder(BorderFactory.createLineBorder(Color.blue));

		if (icon != null)
			iconLabel.setIcon(icon);

		titlePanel.add(_CurrentWizardState, BorderLayout.WEST);
		titlePanel.add(_CurrentContextInfo, BorderLayout.CENTER);
		titlePanel.add(iconLabel, BorderLayout.EAST);
		titlePanel.add(separator, BorderLayout.SOUTH);

		add(titlePanel, BorderLayout.NORTH);
		JPanel secondaryPanel = new JPanel();
		secondaryPanel.add(contentPanel, BorderLayout.CENTER);
		// // change background color
		// secondaryPanel.setOpaque(true);
		// secondaryPanel.setBackground(Constants.MAIN_PANEL_BACK_GRND_COLOR);
		add(secondaryPanel, BorderLayout.CENTER);

		setBorder(BorderFactory.createLineBorder(Color.gray));
		System.out.println("Ended initializing " + getClass().getName());
	}

	// the main content of the panel
	public abstract JPanel getContentPanel();

	protected abstract void internationalize(); // TN97

	public abstract int getPannelStateCode();

	// the named state of the application wizard
	protected String getPanelState() {
		return _PannelState;
	}

	/**
	 * get the localized message corresponding to the panel name
	 */
	public void setPanelState() { // TN97
		_PannelState = ServiceHelper.getMessageService().getMessage(getPannelStateCode());// TN97
	}

	public abstract JTable getTable();

	public abstract AbstractTableModel getTableModel();

	// Icon to be placed in the upper right corner.
	public ImageIcon getImageIcon() {
		return getConfigurationManager().getApplicationIcon();
		// return null;
	}

	public ConfigurationManager getConfigurationManager() {
		return _ConfigurationManager;
	}

	public EntryFormConfigurationManager getEntryFormConfigurationManager() {
		return _ConfigurationManager;
	}

	public void buildBannerInfo() {
		setPanelState(); // TN97
		_CurrentWizardState.setText(getPanelState());
		_CurrentContextInfo.setText(_ConfigurationManager.getProgramRunner().getSelectedParamsString());

	}

	public void buildCurAggregationInfo(int level) {
		// String AggregatedInfo =
		// _ConfigurationManager.getCurrentAggregatedModelData()
		// show changed info in red (Aggregated or Section)
		String aggregatedInfo = "<html><font color='";
		if (!_ConfigurationManager.isAggregatedDataSaved() || !_ConfigurationManager.isAggregatedAttributesDataSaved())
			aggregatedInfo += "red'>";
		else
			aggregatedInfo += "black'>";

		if (_ConfigurationManager.getCurrentAggregatedModelData() == null)
			return;
		// if
		// (_ConfigurationManager.getCurrentAggregatedModelData().getAggregatedContextId()
		// == null) {
		// this is a new Aggregated
		String info = _ConfigurationManager.getCurrentAggregatedModelData().getAggregatedLabel();
		if (info != null)
			aggregatedInfo += (info);

		info = _ConfigurationManager.getCurrentAggregatedModelData().getYear();
//		info = _ConfigurationManager.getCurrentAggregatedModelData().getStartDate();
		aggregatedInfo += (" - " + info);
		info = _ConfigurationManager.getCurrentAggregatedModelData().getWeek();
//		info = _ConfigurationManager.getCurrentAggregatedModelData().getEndDate();
		aggregatedInfo += (" - " + info);
		// }
		// else {
		// AggregationContext currentAggregatedContext =
		// _ConfigurationManager.getAggregationContextCache().get(_ConfigurationManager.getCurrentAggregatedModelData().getAggregatedContextId());
		// if (currentAggregatedContext == null)
		// return;
		// Aggregation currentAggregated =
		// currentAggregatedContext.getAggregation();
		//
		// if (currentAggregated.getCreationDate() != null) {
		// String creationDateStr =
		// _Sdf.format(currentAggregated.getCreationDate());
		// AggregatedInfo += (creationDateStr + ": ");
		// }

		aggregatedInfo += "</font>";

		if (level >= 2) { // use enum on panel states?
			Section currentSection = _ConfigurationManager.getCurrentSection();
			if (currentSection != null) {
				if (!_ConfigurationManager.isSectionAttributesDataSaved() || !_ConfigurationManager.isSectionDataSaved())
					aggregatedInfo += "<font color='red'>";
				else
					aggregatedInfo += "<font color='black'>";

				// AggregatedInfo += (_Sdf.format(currentSection.getDate()));
				//
				String sectionLabel = _ConfigurationManager.getCurrentSectionModelData().getTypeLabel();
				aggregatedInfo += (" - " + sectionLabel);
				aggregatedInfo += "</font>";
			}
		}
		// }

		setCurAggregatedInfo(aggregatedInfo);
	}

	public String getCurAggregatedInfo() {
		return _CurAggregationInfo.getText();
	}

	public void setCurAggregatedInfo(String text) {
		this._CurAggregationInfo.setText(text);
	}

	public void setDescriptor(WizardPanelDescriptor formDescriptor) {
		// _Descriptor = AggregatedListFormDescriptor;
		_Descriptor = formDescriptor;

	}

	public WizardPanelDescriptor getDescriptor() {
		return _Descriptor;
	}

	public void actionPerformed(ActionEvent e) {
		// to be overriden
	}

	protected void changeTableColumnTitles(String[] tableTitles) {
		for (int i = 0; i < getTableModel().getColumnCount(); i++) {
			getTable().getColumnModel().getColumn(i).setHeaderValue(tableTitles[i]);
		}
	}

	/**
	 * used for action runners
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		Object tableModel = e.getSource();
		if (tableModel instanceof SectionTableKeyValueTableModel) {
			SectionTableKeyValueTableModel sectionTableKeyValueTableModel = (SectionTableKeyValueTableModel) tableModel;
			int row = e.getFirstRow();
			int column = e.getColumn();
//			System.out.println(row);
//			System.out.println(column);
			if (column != TableModelEvent.ALL_COLUMNS) {

				// run action on dependencies
				Integer errorCode = runRowActions(row, column, sectionTableKeyValueTableModel);

				if (errorCode != null) {
					String errMess = ServiceHelper.getMessageService().getMessage(errorCode);
					errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED) + ", " + errMess;
					System.out.println(errMess);
					JOptionPane.showMessageDialog(null, errMess, "Error", JOptionPane.OK_OPTION, null);
				}
			}
		}
	}

	protected Integer runRowActions(int row, int column, SectionTableKeyValueTableModel sectionTableKeyValueTableModel) {

		// get concept id changed
		String conceptId = getConceptIdChanged(row, column, sectionTableKeyValueTableModel);
		// get concept value changed
		String idValue = getConceptValueChanged(row, column, sectionTableKeyValueTableModel);

		DataValidator validator = new DefaultDataValidator(conceptId);
		Object[] runtimeParams = new Object[2];
		runtimeParams[0] = conceptId; // id source
		runtimeParams[1] = idValue; // id value
		Integer errorCode = validator.runAction(CommonIpdConstants.ACTION_RULES_VALUECHANGED, runtimeParams);
		//
		return errorCode;
	}

	/**
	 * entity has changed, refresh the action rules
	 */
	protected void initRowActions(int row, int column, SectionTableKeyValueTableModel sectionTableKeyValueTableModel) {

		// get concept id changed
		String conceptId = getConceptIdChanged(row, column, sectionTableKeyValueTableModel);
		// get concept value changed
		// String idValue = getConceptValueChanged(row);

		DataValidator validator = new DefaultDataValidator(conceptId);
		Object[] runtimeParams = new Object[0];
		// runtimeParams[0] = conceptId; // id source
		// runtimeParams[1] = idValue; // id value
		validator.runAction(CommonIpdConstants.ACTION_RULES_REINIT, runtimeParams);
	}

	// /**
	// * default implementation
	// */
	// protected String getConceptValueChanged(int row) {
	// return null;
	// }

	/**
	 * default implementation
	 */
	protected String getConceptValueChanged(int row, int column, SectionTableKeyValueTableModel sectionTableKeyValueTableModel) {
		// TODO Auto-generated method stub
		return null;
	}

	// protected String getConceptIdChanged(int row, int column, JTableSection
	// tableSource) {
	protected String getConceptIdChanged(int row, int column, SectionTableKeyValueTableModel sectionTableKeyValueTableModel) {
		// TODO Auto-generated method stub
		return null;
	}

}
