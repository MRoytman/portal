package ch.msf.form.wizard;

import java.util.List;

import javax.swing.JOptionPane;

import ch.msf.CommonConstants;
import ch.msf.form.ParamException;
import ch.msf.form.config.PatientListTableModel.PatientModelData;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.model.PatientContext;
import ch.msf.model.SelectionContext;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public class PatientListFormDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "PATIENTLIST_FORM_PANEL";

	PatientListFormPanel _FormPanel;

	EntryFormConfigurationManager _ConfigurationManager;

	private SelectionContext _LastSelectionContext;
	private String _LastSelectionLanguage;

	public PatientListFormDescriptor() {
		_FormPanel = new PatientListFormPanel();
		_FormPanel.setDescriptor(this);

		setPanelComponent(_FormPanel);

		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
	}

	public Object getNextPanelDescriptor() {
		return PatientFormDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		// TN129 check if we have to include the village origin panel
		String villageOriginConceptId = _ConfigurationManager.getConfigField("villageOrigin.conceptId");
		if (villageOriginConceptId != null)
			return VillageListFormDescriptor.IDENTIFIER;

		return ParamDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		// show header info
		_FormPanel.buildBannerInfo();
		boolean contextChanged = false;
		
		if (_ConfigurationManager.isCurrentPatientCancelled()){//TN132 (roll back patient)
			_FormPanel.doCancelPatient(); // cancel new entered patient
			_ConfigurationManager.setCurrentPatientCancelled(false); // reset cancel 
			contextChanged = true;
		}

		// check if there is a change in the context
		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();

		// build the list of patients
		// in case context changed or previous search with filter
		if (contextChanged || !selectionContext.equals(_LastSelectionContext) || !_ConfigurationManager.getDefaultLanguage().equals(_LastSelectionLanguage)|| _FormPanel.isFilterResults()) {
			contextChanged = true;

			// update patient list
			updatePatientList();

			// save the Context and language
			_LastSelectionContext = selectionContext;
			_LastSelectionLanguage = _ConfigurationManager.getDefaultLanguage();

		}

		// // TN78 display current list choice
		// _FormPanel.buildCurPatientInfo(2);

		_FormPanel.configurePanelStartState(contextChanged);

	}

	/**
	 * read all within limit per page
	 */
	public void updatePatientList() {

		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();

		// get the list of patient contexts
		String date = null;
		String idOrName = null;
		boolean testOnId = true;
		List<PatientContext> patientContexts;
		try {
			patientContexts = getPatientContexts(selectionContext, date, idOrName, testOnId);

		} catch (Exception e) {
			// 
			JOptionPane.showMessageDialog(null, "ERROR! List could not be updated", "", JOptionPane.OK_OPTION, null);
			return;
		}
		// ...and update the view,
		_FormPanel.updateModelView(patientContexts);

	}

	/**
	 * search on patients
	 * 
	 * @param selectionContext
	 * @param date
	 * @param idOrName
	 * @param testOnId
	 * @return a list of patientcontext that matches with passed criteria
	 */
	private List<PatientContext> getPatientContexts(SelectionContext selectionContext, String date, String idOrName, boolean testOnId) {
		List<PatientContext> patientContexts = null;
		try {
			// get first page
			patientContexts = ServiceHelper.getPatientManagerService().getAllSelectedPatientContext(selectionContext, date, idOrName, testOnId, 1);
//			patientContexts = ServiceHelper.getPatientManagerService().getAllSelectedPatientContext(selectionContext, date, idOrName, testOnId);
		} catch (ParamException e) {
			// get the error message
			String errMessNumber = e.getMessage();
			int errorCode = -1;
			String errMess = null;
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

	// // fill the tablemodel with patient context data
	// private void updateModelView(List<PatientContext> patientContexts) {
	//
	// // manage combo value in list
	// _FormPanel.updateModelEditor();
	//
	// ArrayList<PatientListTableModel.PatientModelData> modelValues = new
	// ArrayList<PatientListTableModel.PatientModelData>();
	// if (patientContexts != null)
	// for (PatientContext patientContext : patientContexts) {
	// PatientListTableModel.PatientModelData modelValue = new
	// PatientListTableModel().new PatientModelData();
	// Patient patient = patientContext.getPatient();
	// // fill the model with patient data
	// _FormPanel.fillModelValue(modelValue, patient);
	// modelValues.add(modelValue);
	//
	// // add caching purposes info
	// modelValue.setPatientContextId(patientContext.getId());
	//
	// _ConfigurationManager.getPatientContextCache().put(patientContext.getId(),
	// patientContext);
	// }
	//
	// // update table titles
	// String[] tableTitles =
	// _ConfigurationManager.getTableTitleTranslation("patientList");
	// _FormPanel.setDataModel(modelValues, tableTitles);
	//
	// }

	public void aboutToHidePanel() {

		if (getWizard()._MyDirectionNext) // if we go next...
		{
			// save the patient selection
			// if (_ConfigurationManager.getCurrentPatientModelData() == null) {
			// if (_ConfigurationManager.getCurrentPatientIdentifier() ==
			// null) {
			if (_FormPanel.getTable().getSelectedRow() == -1) {
				JOptionPane.showMessageDialog(null, "The application is in an inconsistent state, please exit if moving back does not work", "Inconsistent state",
						JOptionPane.OK_OPTION, null);
				// reset flags
				// _ConfigurationManager.isPatientDataSaved();
				_ConfigurationManager.setNewPatientDataInsertion(false);
				getWizard().setCurrentPanel(ParamDescriptor.IDENTIFIER, false);
			}

			PatientModelData patientModelData = _FormPanel.getTableModel().getModelValues().get(_FormPanel.getTable().getSelectedRow());
			_ConfigurationManager.setCurrentPatientModelData(patientModelData);
		}

		// // mark the patient id
		// int currentCount = _MyTableModel.getRowCount();
		// String patientIdentifier =
		// (String)_MyTableModel.getValueAt(currentCount-1, 0);
		// if (patientIdentifier != null)
		// getEntryFormConfigurationManager().setCurrentPatientIdentifier(patientIdentifier);

	}

}
