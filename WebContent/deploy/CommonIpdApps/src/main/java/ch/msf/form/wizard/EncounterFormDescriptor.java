package ch.msf.form.wizard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JOptionPane;

import ch.msf.CommonConstants;
import ch.msf.form.FatalException;
import ch.msf.form.config.EncounterListTableModel.EncounterModelData;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.model.ConceptIdValue;
import ch.msf.model.Encounter;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;
import ch.msf.util.MiscelaneousUtils;
import ch.msf.util.StackTraceUtil;

import com.nexes.wizard.WizardPanelDescriptor;

public class EncounterFormDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "ENCOUNTER_FORM_PANEL";

	private EncounterFormPanel _FormPanel;

	EntryFormConfigurationManager _ConfigurationManager;

	public EncounterFormDescriptor() {
		_FormPanel = new EncounterFormPanel();
		setPanelComponent(_FormPanel);
		_FormPanel.setDescriptor(this);

		_ConfigurationManager = ServiceHelper
				.getEntryFormConfigurationManagerService();
	}

	public Object getNextPanelDescriptor() {
		return FinalDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return EncounterListFormDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {

		// show header info
		_FormPanel.buildBannerInfo();

		Encounter currentEncounter = null;
		HashMap<String, String> keyValues = new HashMap<String, String>();

		// let's prepare the previous screen data
		// EncounterModelData encounterModelData = _ConfigurationManager
		// .getCurrentEncounterModelData();

		EncounterModelData currentEncounterModelData = _ConfigurationManager
				.getCurrentEncounterModelData();
		Long currentEncounterId = currentEncounterModelData.getEncounterId();

		try {
			if (_ConfigurationManager.getEncounterCache().get(
					currentEncounterId) == null) {
				// this Encounter has not been parsed yet

				// try {
				currentEncounter = ServiceHelper.getEncounterManagerService()
						.getEncounter(currentEncounterId);

				if (currentEncounter != null) {
					// get the concept values...
					for (ConceptIdValue conceptIdValue : currentEncounter
							.getIdValues()) {
						keyValues.put(conceptIdValue.getConceptId(),
								conceptIdValue.getConceptValue());
					}
					// ...and update the view, set all concept values

					// get the concept ids for this entity
					ArrayList<IdType> idTypes = _ConfigurationManager
							.getQuestionIdTypes(Encounter.class,
									currentEncounter.getType(), true);
					// get the labels per language for this entity
					String className = MiscelaneousUtils.getClassName(Encounter.class);
					// HashMap<locale, HashMap<fieldId, fieldLabel>>>
					HashMap<String, HashMap<String, String>> labels = _ConfigurationManager
							.getQuestionLabels(className,
									currentEncounter.getType());

					HashMap<String, ArrayList<KeyValue>> comboboxValueMap = _ConfigurationManager
							.getAllComboLabels(Encounter.class,
									currentEncounter.getType(), true);

					_FormPanel.setDataModel(idTypes, keyValues, labels,
							comboboxValueMap, currentEncounter);
				} else {
					// this happen when it's a new encounter

					currentEncounter = new Encounter();
					currentEncounter.setPatient(_ConfigurationManager
							.getCurrentPatientContext().getPatient());
					currentEncounter.setCreationDate(new Date());
					// set encounter type
					String encounterType = currentEncounterModelData
							.getEncounterType();
					if (encounterType != null
							&& !encounterType.equals("DefaultType")) {
						currentEncounter.setType(encounterType);
					}
					currentEncounter.setStatus(0);

					// clear the model
					// get the concept ids for this entity
					ArrayList<IdType> idTypes = _ConfigurationManager
							.getQuestionIdTypes(Encounter.class,
									currentEncounter.getType(), true);

					// get the labels per language for this entity
					String className = MiscelaneousUtils.getClassName(Encounter.class);
					HashMap<String, HashMap<String, String>> labels = _ConfigurationManager
							.getQuestionLabels(className,
									currentEncounter.getType());

					HashMap<String, ArrayList<KeyValue>> comboboxValueMap = _ConfigurationManager
							.getAllComboLabels(Encounter.class,
									currentEncounter.getType(), true);

					_FormPanel
							.clearDataModel(idTypes, labels, comboboxValueMap);

				}
				// currentEncounterId can be null if not saved (new encounter),
				// but
				// it works...(AS LONG THERE IS ONLY ONE LINE ALLOWED IN
				// EDITION)
				_ConfigurationManager.getEncounterCache().put(
						currentEncounterId, currentEncounter);

			} else { // this encounter is already in cache

				currentEncounter = _ConfigurationManager.getEncounterCache()
						.get(currentEncounterId);

				_FormPanel.updateModelView(currentEncounter);
			}
		} catch (FatalException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS
					+ StackTraceUtil.getCustomStackTrace(e1), "Fatal error",
					JOptionPane.OK_OPTION, null);

			// go back to source panel that caused the problem
			getWizard().setCurrentPanel(EncounterListFormDescriptor.IDENTIFIER,
					false);

		}

		// recopy the data anyway as they might have changed
		// try {
		// currentEncounter.setDate(_Sdf.parse(currentEncounterModelData.getDate()));
		// } catch (ParseException e) {
		// e.printStackTrace();
		// }
		currentEncounter.setDate(currentEncounterModelData.getDate());

		// currentEncounter.setPlace(currentEncounterModelData.getPlace());

		//
		_FormPanel.configurePanelStartState();

		// if the currentPatientContext has not been saved its id will be null
		_ConfigurationManager.setCurrentEncounter(currentEncounter);

		// display current list choice in header
		_FormPanel.buildCurPatientInfo(2);

		// forbid next screen
		getWizard().setNextFinishButtonEnabled(false);
	}

	public void aboutToHidePanel() {
		// if going back, save the changes for current Encounter
		// TN86 prevent from going back if not saved
//		if (!getWizard()._MyDirectionNext) {
//			HashMap<String, String> results = _FormPanel.getTableModel()
//					.getResults();
//			try {
//				_FormPanel._ConfigurationManager.saveEncounterAttributes(
//						results, false);
//			} catch (ParamException e) {
//				e.printStackTrace();
//			}
//		}
	}

}
