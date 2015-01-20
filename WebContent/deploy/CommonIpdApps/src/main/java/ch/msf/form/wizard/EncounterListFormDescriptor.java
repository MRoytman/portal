package ch.msf.form.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ch.msf.form.config.EncounterListTableModel;
import ch.msf.form.config.EncounterListTableModel.EncounterModelData;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.model.Encounter;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public class EncounterListFormDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "ENCOUNTER_LIST_FORM_PANEL";

	private EncounterListFormPanel _FormPanel;

	private Patient _CurrentPatient;

	EntryFormConfigurationManager _ConfigurationManager;

	public EncounterListFormDescriptor() {
		_FormPanel = new EncounterListFormPanel();
		_FormPanel.setDescriptor(this);

		setPanelComponent(_FormPanel);

		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
	}

	public Object getNextPanelDescriptor() {
		return EncounterFormDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return PatientFormDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {

		boolean newPatient = false;
		// if (getWizard()._MyDirectionNext) // if we go next...
		{
			// show header info
			_FormPanel.buildBannerInfo();

			// get the list of encounters

			// the patient has been saved at this stage and is in our cache
			PatientContext currentPatientContext = _ConfigurationManager.getCurrentPatientContext();
			Patient currentPatient = currentPatientContext.getPatient();

			if (_CurrentPatient != currentPatient) {
				newPatient = true;
				List<Encounter> encounters = null;
				boolean retired = false;
				encounters = ServiceHelper.getEncounterManagerService().getAllPatientEncounters(currentPatient, retired);
				encounters = filterEncounterWithPermissions(encounters);
				// fill the tablemodel with patient context data
				ArrayList<EncounterListTableModel.EncounterModelData> modelValues = new ArrayList<EncounterListTableModel.EncounterModelData>();
				if (encounters != null) {
					for (Encounter encounter : encounters) {
						EncounterListTableModel.EncounterModelData modelValue = new EncounterListTableModel().new EncounterModelData();
						_FormPanel.fillModelValue(modelValue, encounter);
						modelValues.add(modelValue);

						// add caching purposes info
						modelValue.setEncounterId(encounter.getId());
					}
					String[] tableTitles = _ConfigurationManager.getTableTitleTranslation("encounterList");
					_FormPanel.setDataModel(modelValues, tableTitles);
				}

				// save current patient
				_CurrentPatient = currentPatient;
			}

			// TN78 display current list choice
			_FormPanel.buildCurPatientInfo(1); // TN118
		}

		_FormPanel.configurePanelStartState(newPatient);

	}

	//TN149
	private List<Encounter> filterEncounterWithPermissions(List<Encounter> encounters) {
		if (_ConfigurationManager.isUserPermissionsActive()) {

			List<String> roleAllowedEncountersTypes = ServiceHelper.getEncounterManagerService().getCurrentRoleEncounterIds();
			Iterator<Encounter> it = encounters.iterator();
			while (it.hasNext()) {
				Encounter encounter = it.next();
				if (!roleAllowedEncountersTypes.contains(encounter.getType())) {
					it.remove();
				}
			}
		}
		return encounters;
	}

	public void aboutToHidePanel() {

		if (getWizard()._MyDirectionNext) // if we go next...
		{
			// save the Encounter selection

			EncounterModelData encounterModelData = _FormPanel.getTableModel().getModelValues().get(_FormPanel.getTable().getSelectedRow());
			_ConfigurationManager.setCurrentEncounterModelData(encounterModelData);
		}

	}

}
