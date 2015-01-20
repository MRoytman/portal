package ch.msf.form.wizard;

import java.util.ArrayList;
import java.util.List;

import ch.msf.form.config.SectionListTableModel;
import ch.msf.form.config.SectionListTableModel.SectionModelData;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.model.Aggregation;
import ch.msf.model.AggregationContext;
import ch.msf.model.Section;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public class SectionListFormDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "SESSION_LIST_FORM_PANEL";

	private SectionListFormPanel _FormPanel;

//	private Aggregation _CurrentAggregation;

	EntryFormConfigurationManagerImpl _ConfigurationManager;

	public SectionListFormDescriptor() {
		_FormPanel = new SectionListFormPanel();
		_FormPanel.setDescriptor(this);

		setPanelComponent(_FormPanel);

		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
	}

	public Object getNextPanelDescriptor() {
		return SectionFormDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return AggregationListFormDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		
		// show header info
		_FormPanel.buildBannerInfo();

//		if (getWizard()._MyDirectionNext) // if we go next...
		{
			// get the list of sections
			// the aggregation has been saved at this stage and is in our cache
			AggregationContext currentAggregationContext = _ConfigurationManager.getCurrentAggregationContext();
			Aggregation currentAggregation = currentAggregationContext.getAggregation();

//			if (_CurrentAggregation != currentAggregation) 
			{
				List<Section> sections = null;
				sections = currentAggregation.getSections();

				// fill the tablemodel with section context data
				ArrayList<SectionListTableModel.SectionModelData> modelValues = new ArrayList<SectionListTableModel.SectionModelData>();
				
				if (sections != null) {
					for (Section section : sections) {
						SectionListTableModel.SectionModelData modelValue = new SectionListTableModel().new SectionModelData();
						modelValue.setAggregationThemeType(currentAggregation.getThemeType());
						_FormPanel.fillModelValue(modelValue, section);
						modelValues.add(modelValue);

						// add caching purposes info
						modelValue.setSessionId(section.getId());
					}
					String[] tableTitles = _ConfigurationManager.getTableTitleTranslation("sectionList");
					_FormPanel.setDataModel(modelValues, tableTitles);
				}


				// save current Aggregation
//				_CurrentAggregation = currentAggregation;
			}

			//  display current list choice
			_FormPanel.buildCurAggregationInfo(1); //
		}

		_FormPanel.configurePanelStartState();

	}

	public void aboutToHidePanel() {

		if (getWizard()._MyDirectionNext) // if we go next...
		{
			// save the Encounter selection

			SectionModelData sectionModelData = _FormPanel.getTableModel().getModelValues().get(_FormPanel.getTable().getSelectedRow());
			_ConfigurationManager.setCurrentSectionModelData(sectionModelData);
		}

	}

}
