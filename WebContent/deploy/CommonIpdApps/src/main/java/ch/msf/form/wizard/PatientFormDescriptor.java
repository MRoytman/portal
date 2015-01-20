package ch.msf.form.wizard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.persistence.NoResultException;
import javax.swing.JOptionPane;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.form.config.PatientListTableModel.PatientModelData;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.model.Patient;
import ch.msf.model.PatientContext;
import ch.msf.model.PatientIdentifier;
import ch.msf.model.SelectionContext;
import ch.msf.service.ServiceHelper;
import ch.msf.util.IdType;
import ch.msf.util.KeyValue;
import ch.msf.util.MiscelaneousUtils;

import com.nexes.wizard.WizardPanelDescriptor;

public class PatientFormDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "PATIENT_FORM_PANEL";

	private PatientFormPanel _FormPanel;

	EntryFormConfigurationManager _ConfigurationManager;

	public PatientFormDescriptor() {
		_FormPanel = new PatientFormPanel();
		setPanelComponent(_FormPanel);
		_FormPanel.setDescriptor(this);

		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
	}

	public Object getNextPanelDescriptor() {
		return EncounterListFormDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return PatientListFormDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {

		// show header info
		_FormPanel.buildBannerInfo();

		// TN94 transparently add context to patient's id.
		SelectionContext selectionContext = _ConfigurationManager.getCurrentSelectionContext();
		String contextId = MiscelaneousUtils.getContextKey(selectionContext.getSelectedProject().getCode(), selectionContext.getSelectedCareCenter().getCode());
		PatientIdentifier patientIdentifier = new PatientIdentifier(contextId+_ConfigurationManager.getCurrentPatientModelData().getId(), CommonIpdConstants.IDENTITY_TYPE_OTHER);

		List<PatientContext> patientContexts = null;
		PatientContext currentPatientContext = null;
		Patient currentPatient = null;

		// let's prepare the previous screen data
		PatientModelData patientModelData = _ConfigurationManager.getCurrentPatientModelData();

		Long currentPatientContextId = _ConfigurationManager.getCurrentPatientModelData().getPatientContextId();

		if (_ConfigurationManager.getPatientContextCache().get(currentPatientContextId) == null) {
			// this patient has not been parsed yet
			try {
				patientContexts = ServiceHelper.getPatientManagerService().getSelectedPatient(patientIdentifier, false);

				if (patientContexts == null || patientContexts.size() > 1) {
					// this should never happen as there is a uniq db constraint
					// on
					// the id
					JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + "There are several patients with this id, please contact the application administator.",
							"Error!", JOptionPane.OK_OPTION, null);
					getWizard().setNextFinishButtonEnabled(false);
					return;
				}
				currentPatientContext = patientContexts.get(0);
				currentPatient = patientContexts.get(0).getPatient();
				// List<PatientContext> patientContexts = ServiceHelper
				// .getPatientManagerService()
				// .getAllSelectedPatientContext(currentPatient);
				// if (patientContexts != null && patientContexts.size() > 0) {
				// currentPatientContext = patientContexts.get(0);
				// // ! important to replace the patient...otherwise changes on
				// // the list are not taken
				// currentPatient = currentPatientContext.getPatient();
				// }
				// ...and update the view,
				_FormPanel.updateModelView(currentPatient);

			} catch (NoResultException e) {
				// this happen when it's a new patient
				currentPatientContext = _ConfigurationManager.createNewPatientContext();

				currentPatient = new Patient();
////				 recreate patient identifier with context info
//				selectionContext = _ConfigurationManager.getCurrentSelectionContext();
//				String contextId = MiscelaneousUtils.getContextKey(selectionContext.getSelectedProject().getCode(), selectionContext.getSelectedCareCenter().getCode());

//				patientIdentifier = new PatientIdentifier(contextId + _ConfigurationManager.getCurrentPatientModelData().getId(), CommonIpdConstants.IDENTITY_TYPE_OTHER);
				patientIdentifier.setPatient(currentPatient);
				currentPatient.addPatientIdentifier(patientIdentifier);

				currentPatient.setCreationDate(new Date());

				currentPatientContext.setPatient(currentPatient);

				// clear the model
				// get the concept ids for this entity
				ArrayList<IdType> idTypes = _ConfigurationManager.getQuestionIdTypes(Patient.class, null, true);
				// get the labels per language for this entity
				String className = MiscelaneousUtils.getClassName(Patient.class);
				HashMap<String, HashMap<String, String>> labels = _ConfigurationManager.getQuestionLabels(className, null);

				HashMap<String, ArrayList<KeyValue>> comboboxValueMap = _ConfigurationManager.getAllComboLabels(Patient.class, null, true);

				_FormPanel.clearDataModel(idTypes, labels, comboboxValueMap);

			}
			// patientContextId can be null if not saved (new patient), but it
			// works...(AS
			// LONG THERE IS ONLY ONE LINE ALLOWED IN EDITION)
			_ConfigurationManager.getPatientContextCache().put(currentPatientContextId, currentPatientContext);

		} else { // this patient is already in cache
		// Long patientContextId = _ConfigurationManager
		// .getCurrentPatientModelData().getPatientContextId();

			currentPatientContext = _ConfigurationManager.getPatientContextCache().get(currentPatientContextId);
			currentPatient = currentPatientContext.getPatient();

			// ...and update the view,
			// TN83 entryform:...update the patient first!...
			boolean all = true;
			currentPatient = ServiceHelper.getPatientManagerService().readDBPatientInfo(currentPatient, all);
			currentPatientContext.setPatient(currentPatient);

			_FormPanel.updateModelView(currentPatient);
		}

		// update patient data
		// recopy the data anyway as they might have changed
		// check id
		for (PatientIdentifier oldPatientIdentifier : currentPatient.getPatientIdentifiers()) {
			// TN94 transparently add context to patient's id.
			oldPatientIdentifier.setIdentifier(contextId+patientModelData.getId());
			// TODO
			// NOK
			// if
			// multi
			// patient
			// Ids
		}
		currentPatient.setFamilyName(patientModelData.getSurname());
		currentPatient.setFirstName(patientModelData.getName());

		// get sex from
		// get all combo
		// HashMap<String, ArrayList<KeyValue>> allComboboxValueMap =
		// _ConfigurationManager.getAllComboLabels(Patient.class, null, false
		// /*get all combo*/) ;
		String comboSex = "sex";
		currentPatient.setSex(PatientListFormPanel.ComboEntityList.getModelForEntity(comboSex, patientModelData.getSex()));

		// don't update the patient since it does not have a field 'age'
		// currentPatient.setAge(patientModelData.getAge());
		currentPatient.setBirthDate(patientModelData.getBirthDate());

		// // set the patient age
		// // set the age concept value
		// ArrayList<KeyValue> keyValueAttributeMappings = _ConfigurationManager
		// .getQuestionIdToClassAttributes(Patient.class, null);
		// // first find the concept
		// String conceptIdAge = null;
		// for (KeyValue keyValueAttributeMapping : keyValueAttributeMappings) {
		// if (keyValueAttributeMapping._Value.equals("Age")) {
		// conceptIdAge = keyValueAttributeMapping._Key;
		// break;
		// }
		// }
		// boolean found = false;
		// if (conceptIdAge != null) {
		// for (PatientIdValue patientIdValue : currentPatient.getIdValues()) {
		// if (patientIdValue.getConceptId().equals(conceptIdAge)){
		// // found !
		// patientIdValue.setConceptValue(patientModelData.getBirthDate().toString());
		// found = true;
		// break;
		// }
		// }
		// }
		// // the age is not in the patient concepts yet, we must add it
		// if (!found) {
		// PatientIdValue piv = new PatientIdValue();
		// piv.setConceptId(conceptIdAge);
		// piv.setConceptValue(patientModelData.getBirthDate().toString());
		// piv.setPatient(currentPatient);
		// currentPatient.getIdValues().add(piv);
		// }

		//
		_FormPanel.configurePanelStartState();

		// if the currentPatientContext has not been saved its id will be null
		_ConfigurationManager.setCurrentPatientContext(currentPatientContext);

		// display current list choice
		_FormPanel.buildCurPatientInfo(1);//TN118
	}

	// /**
	// * fill the keyValues map from currentPatient info to update the model
	// * @param currentPatient
	// */
	// public void updateModelView(Patient currentPatient) {
	//
	// // clear the model...
	// // get the concept ids for this entity
	// ArrayList<IdType> idTypes = _ConfigurationManager
	// .getQuestionIdTypes(Patient.class, null, true);
	// // get the labels per language for this entity
	// HashMap<String, HashMap<String, String>> labels = _ConfigurationManager
	// .getQuestionLabels(Patient.class, null);
	//
	// HashMap<String, ArrayList<KeyValue>> comboboxValueMap =
	// _ConfigurationManager.getAllComboLabels(Patient.class, null, true) ;
	// _FormPanel.clearDataModel(idTypes, labels,
	// comboboxValueMap);
	//
	// // ...and update the view, set all concept values
	//
	// // get the attribute mapping for this entity
	// ArrayList<KeyValue> keyValueAttributeMappings = _ConfigurationManager
	// .getQuestionIdToClassAttributes(Patient.class, null);
	//
	//
	// // keyValues: a map to update the table model
	// HashMap<String, String> keyValues = new HashMap<String, String>();
	// // get the concept values...
	// for (PatientIdValue patientIdValue : currentPatient.getIdValues()) {
	//
	// String conceptId = (String) patientIdValue.getConceptId();
	// // if (s.equals("456"))
	// // System.out.println();
	// boolean found = false;
	// for (KeyValue keyValueAttributeMapping : keyValueAttributeMappings) {
	// if (keyValueAttributeMapping._Key.equals(conceptId)) {
	// found = true;
	// break;
	// }
	// }
	// // do not update the model with info that are displayed in the list
	// if (!found)
	// keyValues.put(conceptId,
	// patientIdValue.getConceptValue());
	// }
	// // update the model
	// _FormPanel.setDataModel(idTypes, keyValues, labels,
	// comboboxValueMap, currentPatient);
	//
	// }

	public void aboutToHidePanel() {
		// if going back, save the changes for current patient
		// TN86 prevent from going back if not saved
		// if (!getWizard()._MyDirectionNext) {
		// HashMap<String, String> results = _FormPanel.getTableModel()
		// .getResults();
		// try {
		// _FormPanel._ConfigurationManager.savePatientAttributes(results,
		// false);
		// } catch (ParamException e) {
		// e.printStackTrace();
		// }
		// }

	}

}
