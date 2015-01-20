package ch.msf.form.wizard;

import javax.swing.JOptionPane;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.model.CareCenter;
import ch.msf.model.Project;
import ch.msf.model.SelectedCountry;
import ch.msf.model.SelectionContext;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public class ParamDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "CONTEXT_CHOICE_PANEL";

	ParamPanel _ParamPanel;

	EntryFormConfigurationManager _ConfigurationManager;

	public ParamDescriptor() {

		_ParamPanel = new ParamPanel();

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(_ParamPanel);

		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();

	}

	public Object getNextPanelDescriptor() {

		return AggregationListFormDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return null;// LoginDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		// show header info
		_ParamPanel.buildBannerInfo();

		if (_ConfigurationManager.getEntryFormConfig() != null && _ConfigurationManager.getEntryFormConfig().getSelectedCountries().size() == 0)

		{
			if (_ConfigurationManager.getSelectedCountriesManager().getDbSelectedCountries(true).size() == 0) {
				String errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED) + ", "
						+ ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ID_ADMIN_NEEDED);

				JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.OK_OPTION, null);
				getWizard().setNextFinishButtonEnabled(false);
			}
		}
	}

	/**
	 * Override this method to perform functionality just before the panel is to
	 * be hidden.
	 */
	public void aboutToHidePanel() {

		// country
		String curCountry = (String) _ParamPanel.getCountries().getSelectedItem();
		curCountry = (curCountry != null) ? curCountry : "No country!";
		_ConfigurationManager.getProgramRunner().getSelectedParams().put("country", curCountry);

		SelectedCountry selectedCountry = _ConfigurationManager.getSelectedCountriesManager().getDbSelectedCountryFromLabel(curCountry, false);
		// ((EntryFormConfigurationManager)_ConfigurationManager).setCurrentSelectedCountry(selectedCountry);

		// prj
		String curProject = (String) _ParamPanel.getProjects().getSelectedItem();
		curProject = (curProject != null) ? curProject : "No project!";
		_ConfigurationManager.getProgramRunner().getSelectedParams().put("project", curProject);

		Project selectedProject = _ConfigurationManager.getSelectedCountriesManager().getDbSelectedProjectFromName(curCountry, curProject, false);
		// ((EntryFormConfigurationManager)_ConfigurationManager).setCurrentProject(selectedProject);

		// carecenter
		String curCarecenter = (String) _ParamPanel.getCareCenters().getSelectedItem();
		curCarecenter = (curCarecenter != null) ? curCarecenter : "No care center!";
		_ConfigurationManager.getProgramRunner().getSelectedParams().put("carecenter", curCarecenter);

		CareCenter selectedCareCenter = _ConfigurationManager.getSelectedCountriesManager().getDbSelectedCareCenterFromName(curCountry, curProject, curCarecenter, false);
		// ((EntryFormConfigurationManager)_ConfigurationManager).setCurrentCareCenter(selectedCareCenter);

		// set the new selection context
		SelectionContext selectionContext = new SelectionContext();
		selectionContext.setSelectedCountry(selectedCountry);
		selectionContext.setSelectedProject(selectedProject);
		selectionContext.setSelectedCareCenter(selectedCareCenter);
		_ConfigurationManager.setCurrentSelectionContext(selectionContext);

	}

}
