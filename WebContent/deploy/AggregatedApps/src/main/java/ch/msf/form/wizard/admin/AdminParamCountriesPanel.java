package ch.msf.form.wizard.admin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import ch.msf.CommonIpdConstants;
import ch.msf.form.ActionEventRetCode;
import ch.msf.form.DualListBox;
import ch.msf.form.wizard.AbstractWizardPanel;
import ch.msf.manager.AllCountriesManager;
import ch.msf.model.CountryName;
import ch.msf.service.ServiceHelper;
import ch.msf.util.swing.GridLayout2;

public class AdminParamCountriesPanel extends AbstractWizardPanel {

	private static final long serialVersionUID = 1L;

	private DualListBox _DualListBox;
	
	// do not set less !
	private static int COUNTRY_PANEL_WIDTH_MIN_LENGTH = 650;
	private static int COUNTRY_PANEL_HEIGTH_MIN_LENGTH = 200;

//	AdminParamCountriesDescriptor _Descriptor;

	public AdminParamCountriesPanel() {

		super(null);//

	}

	public void setDescriptor(AdminParamCountriesDescriptor descriptor) {
		_Descriptor = descriptor;

	}

//	// the named state of the application wizard
//	public String getState() {
//		return "Countries";
//	}

	public JPanel getContentPanel() {

		JPanel contentPanelMaster = new JPanel();

		JPanel contentPanel1 = new JPanel(new BorderLayout());
		contentPanelMaster.add(contentPanel1);

		JPanel defaultParamPanel = new JPanel();
		defaultParamPanel.setLayout(new GridLayout2(1, 1, 5, 5)); //

		contentPanel1.add(Box.createVerticalStrut(100),
				java.awt.BorderLayout.NORTH);

		// get all the possible available countries
		// get the allcountries service
		AllCountriesManager allCountriesManager = ServiceHelper
				.getAllCountriesManagerService();
//System.out.println("BEFORE allCountriesManager.getAllCountriesNames"+new Date());
//		ConfigurationManagerBaseImpl configurationManager = ServiceHelper
//				.getConfigurationManagerService();
//System.out.println("AFTER allCountriesManager.getAllCountriesNames"+new Date());
		
		List<CountryName> allCountriesNames = allCountriesManager
				.getAllCountriesNames(_ConfigurationManager.getDefaultLanguage());
		// the whole list
		HashSet<String> countriesNamesLabels = new HashSet<String>();
		for (CountryName countryName : allCountriesNames) {
			countriesNamesLabels.add(countryName.getLabel());
		}

		// get the countries selected from the config file
		HashSet<String> selectedCountrieNames = new HashSet<String>(
				_ConfigurationManager
						.getConfigFileAllSelectedCountrieNameLabels(_ConfigurationManager
								.getDefaultLanguage()));
		// remove the selected countries from the whole list
		countriesNamesLabels.removeAll(selectedCountrieNames);

		_DualListBox = new DualListBox();
		// feed the left list with all available countries
		_DualListBox.addSourceElements(countriesNamesLabels.toArray());
		// feed the right list with selected countries
		_DualListBox.addDestinationElements(selectedCountrieNames.toArray());
		
		Dimension countryDim = new Dimension(COUNTRY_PANEL_WIDTH_MIN_LENGTH, COUNTRY_PANEL_HEIGTH_MIN_LENGTH);
		defaultParamPanel.setMinimumSize(countryDim);
		defaultParamPanel.setMaximumSize(countryDim);
		defaultParamPanel.setPreferredSize(countryDim);

		defaultParamPanel.add(_DualListBox);

		_DualListBox.addActionListener(this);

		defaultParamPanel.setBorder(BorderFactory
				.createTitledBorder("Select the countries"));
		contentPanel1.add(defaultParamPanel, java.awt.BorderLayout.CENTER);
		
		return contentPanelMaster;
	}
	
	/**
	 * TN97 entryform
	 */
	protected void internationalize() {

	}

	public DualListBox getDualListBox() {
		return _DualListBox;
	}

	public boolean isNextAutorized() {
		boolean allow = false;
		if (getDualListBox() != null) {
			Iterator it = getDualListBox().destinationIterator();
			allow = it.hasNext();
		}
		return allow;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
//		System.out.println(e.getActionCommand());
		if (e.getActionCommand() != null && e.getActionCommand().contains("Remove")){
			Object[] options = { "OK", "CANCEL" };
			int retCode = JOptionPane.showOptionDialog(null, "Are you sure you want to delete this country?", "Warning",
			        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
			        null, options, options[0]);
			if (retCode == 0)
			{
				getDescriptor().getWizard().setNextFinishButtonEnabled(isNextAutorized());
				_ConfigurationManager.setConfigurationSaved(false);
			}
			else{
				// forbid the action!
				if (e instanceof ActionEventRetCode){
					ActionEventRetCode actionEventRetCode = (ActionEventRetCode)e;
					actionEventRetCode.setRetCode(CommonIpdConstants.EVENT_STOP_ACTION);
				}
			}
		}
		else{
			getDescriptor().getWizard().setNextFinishButtonEnabled(isNextAutorized());
			_ConfigurationManager.setConfigurationSaved(false);
		}

	}

	@Override
	public JTable getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractTableModel getTableModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override //TN97
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_ADMINCOUNTRIES;
	}

}
