package ch.msf.form.wizard;

import java.util.ArrayList;

import javax.swing.JOptionPane;

import ch.msf.CommonConstants;
import ch.msf.error.ConfigException;
import ch.msf.error.FatalException;
import ch.msf.form.config.SectionListTableModel.SectionModelData;
import ch.msf.manager.EntryFormConfigurationManager;
import ch.msf.model.Section;
import ch.msf.model.SectionTable;
import ch.msf.service.ServiceHelper;
import ch.msf.util.StackTraceUtil;

import com.nexes.wizard.WizardPanelDescriptor;

public class SectionFormDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "Section_FORM_PANEL";

	private SectionFormPanel _FormPanel;

	EntryFormConfigurationManager _ConfigurationManager;

	public SectionFormDescriptor() {
		_ConfigurationManager = ServiceHelper.getEntryFormConfigurationManagerService();
		_FormPanel = new SectionFormPanel(this);
		setPanelComponent(_FormPanel);
	}

	public Object getNextPanelDescriptor() {
		return FinalDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return SectionListFormDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {

		// show header info
		_FormPanel.buildBannerInfo();

		Section currentSection = null;


		// let's get the section elements

		SectionModelData currentSectionModelData = _ConfigurationManager.getCurrentSectionModelData();
		Long currentSectionId = currentSectionModelData.getSectionId();

		try {
			if (_ConfigurationManager.getSectionCache().get(currentSectionId) == null) {
				// this Section has not been parsed yet
				// read it from db
				currentSection = ServiceHelper.getSectionManagerService().getSection(currentSectionId);

				if (currentSection == null)
					throw new FatalException(getClass().getName() + "::aboutToDisplayPanel: Section is null...(should never happen)");

				_ConfigurationManager.getSectionCache().put(currentSectionId, currentSection);

			} else { // this Section is already in cache

				currentSection = _ConfigurationManager.getSectionCache().get(currentSectionId);

			}
		} catch (FatalException e1) {
			e1.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(e1), "Fatal error", JOptionPane.OK_OPTION, null);

			// go back to source panel that caused the problem
			getWizard().setCurrentPanel(SectionListFormDescriptor.IDENTIFIER, false);

		}
		// if the currentSectionContext has not been saved its id will be null
		_ConfigurationManager.setCurrentSection(currentSection);

		// read section tables configuration part of this section
		try {
			attachSectionTables(currentSection);
		} catch (ConfigException e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Fatal error", JOptionPane.OK_OPTION, null);
			// go back to source panel that caused the problem
			getWizard().setCurrentPanel(SectionListFormDescriptor.IDENTIFIER, false);
		}
		//
		_FormPanel.updateModelView(currentSection);

		//
		_FormPanel.configurePanelStartState();

		// display current list choice in header
		_FormPanel.buildCurAggregationInfo(2);

		// forbid next screen
		getWizard().setNextFinishButtonEnabled(false);
	}

	/**
	 * if not already done, Read the file configuration of the current (selected) section return the
	 * created section tables
	 */
	public ArrayList<SectionTable> attachSectionTables(Section currentSection) {

		ArrayList<SectionTable> sectionTables = null;
		if (currentSection != null && currentSection.getSectionTables().size() == 0) {

			sectionTables = ServiceHelper.getSectionManagerService().buildSectionTablesFromConfig(currentSection);
			for (SectionTable sectionTable : sectionTables) {
				System.out.println("Adding sectionTable "+sectionTable.getTableId()+" to section "+currentSection.getThemeCode());
				currentSection.addSectionTable(sectionTable);
			}
		}
		return sectionTables;
	}

	public void aboutToHidePanel() {

	}

}
