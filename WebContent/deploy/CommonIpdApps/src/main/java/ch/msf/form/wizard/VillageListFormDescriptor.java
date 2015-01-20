package ch.msf.form.wizard;

import java.util.ArrayList;

import ch.msf.form.config.VillageListTableModel.VillageHealthAreaModelData;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;


/**
 * This is related to TN129 (presentation of villages of origin)
 * 
 * @author cmi
 *
 */
public class VillageListFormDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "VILLAGELIST_FORM_PANEL";

	VillageListFormPanel _FormPanel;

	EntryFormConfigurationManager _ConfigurationManager;

	// private SelectionContext _LastSelectionContext;
	// private String _LastSelectionLanguage;

	public VillageListFormDescriptor() {
		_FormPanel = new VillageListFormPanel();
		_FormPanel.setDescriptor(this);

		setPanelComponent(_FormPanel);

		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
	}

	public Object getNextPanelDescriptor() {
		return PatientListFormDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return ParamDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		
		// show header info
		_FormPanel.buildBannerInfo();

		// load existing village - area list
		loadAreaList();

		// load existing village - area list
		loadVillageAreaList();

		// ...and update the view,
		_FormPanel.updateModelView();

		_FormPanel.configurePanelStartState();

	}

	private void loadAreaList() {

		_ConfigurationManager.loadAllAreas();

	}

	/**
	 * 
	 */
	public void loadVillageAreaList() {

		_ConfigurationManager.loadAllVillageArea();

	}

	public void aboutToHidePanel() {

		// IF modified
		if (_ConfigurationManager.isNewVillageOriginData()) {

			// get the village list
			ArrayList<VillageHealthAreaModelData> villageAreas = _FormPanel.getVillageList();

			// and save it
			_ConfigurationManager.saveAllVillageArea(villageAreas);
		}

	}
}
