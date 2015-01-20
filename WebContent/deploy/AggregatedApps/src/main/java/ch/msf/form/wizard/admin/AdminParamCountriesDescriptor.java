package ch.msf.form.wizard.admin;

import java.util.Iterator;
import java.util.TreeSet;

import ch.msf.manager.ConfigurationManagerImpl;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public class AdminParamCountriesDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "COUNTRIES_CHOOSE_PANEL";

	AdminParamCountriesPanel _ParamPanel;

	public AdminParamCountriesDescriptor() {

		_ParamPanel = new AdminParamCountriesPanel();

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(_ParamPanel);

		_ParamPanel.setDescriptor(this);
	}

	public Object getNextPanelDescriptor() {
		return AdminParamDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return null;// LoginDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		// show header info
		_ParamPanel.buildBannerInfo();
		getWizard().setNextFinishButtonEnabled(_ParamPanel.isNextAutorized());
	}

	/**
	 * // update the model
	 */
	public void aboutToHidePanel() {

		if (_ParamPanel.getDualListBox() != null) {
			TreeSet<String> selectedCountriesLabels = getSelectedCountriesLabelFromList();

			ConfigurationManagerImpl configurationManager = ServiceHelper.getConfigurationManagerService();
			configurationManager.updateCountrySelectionConfigurationFromLabel(selectedCountriesLabels);
		}
	}



	private TreeSet<String> getSelectedCountriesLabelFromList() {
		if (_ParamPanel.getDualListBox() != null) {
			Iterator it = _ParamPanel.getDualListBox().destinationIterator();
			TreeSet<String> selectedCountriesLabels = new TreeSet<String>();
			while (it.hasNext()) {
				String countryLabel = (String) it.next();
				selectedCountriesLabels.add(countryLabel);
			}
			return selectedCountriesLabels;
		}
		return null;
	}

	// public void aboutToDisplayPanel() {
	// setNextButtonAccordingToCheckBox();
	// }
	//
	// public void actionPerformed(ActionEvent e) {
	// setNextButtonAccordingToCheckBox();
	// }
	//

	// private void setNextButtonAccordingToCheckBox() {
	// if (_ParamPanel.isCheckBoxSelected())
	// getWizard().setNextFinishButtonEnabled(true);
	// else
	// getWizard().setNextFinishButtonEnabled(false);
	//
	// }
}
