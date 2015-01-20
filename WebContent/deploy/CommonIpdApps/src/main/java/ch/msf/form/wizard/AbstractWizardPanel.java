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
import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.model.Encounter;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.datavalidator.DataValidator;
import ch.msf.model.datavalidator.DefaultDataValidator;
import ch.msf.service.ServiceHelper;
import ch.msf.util.MiscelaneousUtils;

import com.nexes.wizard.WizardPanelDescriptor;

public abstract class AbstractWizardPanel extends JPanel implements ActionListener, TableModelListener {

	private static final long serialVersionUID = 1L;
	public static final int CELL_SPACING = 10;
	public static final int CELL_HEIGHT = 20;

	public static final int QUESTION_WIDTH = 300;
	public static final int QUESTION_HEIGHT = CELL_HEIGHT;
	public static final int ANSWER_WIDTH = 300;
	public static final int ANSWER_HEIGHT = QUESTION_HEIGHT;

	public static final int QUESTION_ANSWER_PANEL_WIDTH = QUESTION_WIDTH + ANSWER_WIDTH + CELL_SPACING;
//	public static final int QUESTION_ANSWER_PANEL_HEIGHT = 0; // CELL_HEIGHT *2

	public static final int PATIENT_INFO_PANEL_HEIGHT = CELL_HEIGHT * 2;
	public static final int PATIENT_INFO_PANEL_WIDTH = QUESTION_ANSWER_PANEL_WIDTH;

	public static final int EXERCISE_LIST_HEIGHT = 400; // TN104
	public static final int EXERCISE_LIST_WIDTH = QUESTION_ANSWER_PANEL_WIDTH;

	public static final int BUTTON_PANEL_HEIGHT = CELL_HEIGHT * 2;
	public static final int BUTTON_PANEL_WIDTH = QUESTION_ANSWER_PANEL_WIDTH;

	public static final int EXERCISE_PANEL_HEIGHT = PATIENT_INFO_PANEL_HEIGHT  + EXERCISE_LIST_HEIGHT + BUTTON_PANEL_HEIGHT + 5;
	public static final int EXERCISE_PANEL_WIDTH = QUESTION_ANSWER_PANEL_WIDTH + 5;

	public static final int STATEINFO_WIDTH = 180;

	public static final int FINDER_TEXT_WIDTH = 100; // TN9

	private SimpleDateFormat _Sdf = new SimpleDateFormat(CommonConstants.SIMPLE_DATE_FORMAT);

	private JPanel contentPanel;
	private JLabel iconLabel;
	private JSeparator separator;
	protected JLabel _CurrentWizardState;
	protected JLabel _CurrentContextInfo;
	private JPanel titlePanel;
	protected String _PannelState;

	protected WizardPanelDescriptor _Descriptor;

	protected JLabel _CurPatientInfo; // patient info

	protected EntryFormConfigurationManagerImpl _ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();

	public AbstractWizardPanel() {

		super();

		System.out.println("Starting initializing " + getClass().getName());
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
//		_CurrentContextInfo.setBackground(CommonIpdConstants.MAIN_PANEL_CONTEXT_BACK_GRND_COLOR);
		_CurrentContextInfo.setBackground(CommonIpdConstants.MAIN_PANEL_BACK_GRND_COLOR);//TN143
		
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

	public ConfigurationManagerImpl getConfigurationManager() {
		return _ConfigurationManager;
	}

	public EntryFormConfigurationManagerImpl getEntryFormConfigurationManager() {
		return _ConfigurationManager;
	}

	public void buildBannerInfo() {
		setPanelState(); // TN97
		_CurrentWizardState.setText(getPanelState());
		_CurrentContextInfo.setText(_ConfigurationManager.getProgramRunner().getSelectedParamsString());

	}

	public void buildCurPatientInfo(int level) {
		// String patientInfo =
		// _ConfigurationManager.getCurrentPatientModelData()
		// TN78 show changed info in red (patient or encounter)
		String patientInfo = "<html><font color='";
		if (!_ConfigurationManager.isPatientDataSaved() || !_ConfigurationManager.isPatientAttributesDataSaved()) /* TN79 */
			patientInfo += "red'>";
		else
			patientInfo += "black'>";

		if (_ConfigurationManager.getCurrentPatientModelData() == null)
			return;
		if (_ConfigurationManager.getCurrentPatientModelData().getPatientContextId() == null) {
			// TN107 this is a new patient
			String surname = _ConfigurationManager.getCurrentPatientModelData().getSurname();
			if (surname != null)
				patientInfo += (surname + " - ");
			String name = _ConfigurationManager.getCurrentPatientModelData().getName();
			if (name != null)
				patientInfo += (name + " - ");
			if (surname == null) // TN117 show id if patient name is null
				patientInfo += (_ConfigurationManager.getCurrentPatientModelData().getId() + " - ");
		} else {
			PatientContext currentPatientContext = _ConfigurationManager.getPatientContextCache().get(_ConfigurationManager.getCurrentPatientModelData().getPatientContextId());
			if (currentPatientContext == null)
				return;
			Patient currentPatient = currentPatientContext.getPatient();

			if (currentPatient.getCreationDate() != null) {
				String creationDateStr = _Sdf.format(currentPatient.getCreationDate());
				patientInfo += (creationDateStr + ": ");
			}
			// TN117 show id if patient name is null
			boolean showId = currentPatient.getFamilyName() == null || currentPatient.getFamilyName().equals("/");
			if (showId)
				patientInfo += (MiscelaneousUtils.getPatientId(currentPatient.getPatientIdentifiers().get(0).getIdentifier()) + " - ");
			else {
				if (currentPatient.getFamilyName() != null)
					patientInfo += (currentPatient.getFamilyName() + " - ");
				if (currentPatient.getFirstName() != null)
					patientInfo += (currentPatient.getFirstName() + " - ");
			}

			// if (currentPatient.getSex() != null)
			// patientInfo += (currentPatient.getSex() + " - ");
			patientInfo += "</font>";
			// TN81 if (DateUtil.getAge(currentPatient.getBirthDate()) != -1)
			// patientInfo += (DateUtil.getAge(currentPatient.getBirthDate()));
			if (level >= 2) { // use enum on panel states?
				Encounter currentEncounter = _ConfigurationManager.getCurrentEncounter();
				if (currentEncounter != null) {
					if (!_ConfigurationManager.isEncounterAttributesDataSaved() || !_ConfigurationManager.isEncounterDataSaved())/* TN79 */
						patientInfo += "<font color='red'>";
					else
						patientInfo += "<font color='black'>";

					patientInfo += (_Sdf.format(currentEncounter.getDate()));
					// patientInfo += (currentEncounter.getPlace() + " - ");
					// TN84
					String encounterLabel = _ConfigurationManager.getEncounterLabel(currentEncounter.getType());
					patientInfo += (" - " + encounterLabel);
					patientInfo += "</font>";
				}
			}
		}

		setCurPatientInfo(patientInfo);
	}

	public String getCurPatientInfo() {
		return _CurPatientInfo.getText();
	}

	public void setCurPatientInfo(String text) {
		this._CurPatientInfo.setText(text);
	}

	public void setDescriptor(WizardPanelDescriptor formDescriptor) {
		// _Descriptor = patientListFormDescriptor;
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
		int row = e.getFirstRow();
		int column = e.getColumn();
		if (column != TableModelEvent.ALL_COLUMNS) {

			// run action on dependencies
			Integer errorCode = runRowActions(row);

			if (errorCode != null) {
				String errMess = ServiceHelper.getMessageService().getMessage(errorCode);
				errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED) + ", " + errMess;
				System.out.println(errMess);
				JOptionPane.showMessageDialog(null, errMess, "Error", JOptionPane.OK_OPTION, null);
			}
		}
	}

	protected Integer runRowActions(int row) {

		// get concept id changed
		String conceptId = getConceptIdChanged(row);
		// get concept value changed
		String idValue = getConceptValueChanged(row);

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
	protected void initRowActions(int row) {

		// get concept id changed
		String conceptId = getConceptIdChanged(row);
		// get concept value changed
		// String idValue = getConceptValueChanged(row);

		DataValidator validator = new DefaultDataValidator(conceptId);
		Object[] runtimeParams = new Object[0];
		// runtimeParams[0] = conceptId; // id source
		// runtimeParams[1] = idValue; // id value
		validator.runAction(CommonIpdConstants.ACTION_RULES_REINIT, runtimeParams);
	}

	/**
	 * default implementation
	 */
	protected String getConceptValueChanged(int row) {
		return null;
	}

	/**
	 * default implementation
	 */
	protected String getConceptIdChanged(int row) {
		return null;
	}

}
