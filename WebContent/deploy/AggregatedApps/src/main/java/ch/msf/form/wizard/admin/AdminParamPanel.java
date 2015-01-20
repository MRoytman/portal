package ch.msf.form.wizard.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import ch.msf.CommonIpdConstants;
import ch.msf.form.wizard.AbstractWizardPanel;
import ch.msf.manager.AllCountriesManager;
import ch.msf.service.ServiceHelper;
import ch.msf.util.swing.GridLayout2;
import ch.msf.util.swing.SimpleEntityList;
import ch.msf.util.swing.SimpleEntityList.AddListener;
import ch.msf.util.swing.SimpleEntityList.DeleteListener;
import ch.msf.util.swing.SimpleEntityList.ListAddListener;
import ch.msf.util.swing.SimpleEntityList.ListDeleteListener;
import ch.msf.util.swing.SimpleEntityList.ListRenameListener;
import ch.msf.util.swing.SimpleEntityList.RenameListener;

public class AdminParamPanel extends AbstractWizardPanel implements  ListSelectionListener, ListDeleteListener, ListRenameListener, ListAddListener {

	private static final long serialVersionUID = 1L;
	public static int PROJECT_NAME_MAX_LENGTH = 15;
	public static int COUNTRY_NAME_MAX_LENGTH = 15;
	public static int CARECENTER_NAME_MAX_LENGTH = 15;

	// the jlist showing the countries
	private SimpleEntityList _SelectedCountries;

	// the jlist showing the projects
	private SimpleEntityList _Projects;

	// the jlist showing the careCenters
	private SimpleEntityList _CareCenters;

	private String _CurrentCountryLabelSelected;
	private String _CurrentProjectSelected;

	private static final String SAVE_CMD = "Save";
	private static final String COUNTRIES_EMPTY = "Country list must not be empty";
	private static final String PROJECTS_EMPTY = "Project list must not be empty";
	private static final String COUNTRYPROJECT_NOTEMPTY = "Before deleting this country, delete its components.";

	private JButton _SaveQuitButton; //

	AllCountriesManager _AllCountriesManager;

	public AdminParamPanel() {

		super(null);

		// get the allcountries service
		_AllCountriesManager = ServiceHelper.getAllCountriesManagerService();

	}


	public JPanel getContentPanel() {

		JPanel contentPanelMaster = new JPanel();

		JPanel contentPanel1 = new JPanel(new BorderLayout());
		contentPanelMaster.add(contentPanel1);

		JPanel defaultParamPanel = new JPanel();
		defaultParamPanel.setLayout(new GridLayout2(1, 1, 2, 5)); //

		contentPanel1.add(Box.createVerticalStrut(20), java.awt.BorderLayout.NORTH);

		// // show the selected countries

		// create an empty list of countries that we be filled once the panel is
		// aboutToDisplayPanel()
		boolean showListOnly = true;
		setSelectedCountries(new SimpleEntityList("Countries", COUNTRY_NAME_MAX_LENGTH, null, showListOnly, 200));
		// detect any country selection change
		getSelectedCountries().addListListener(this);

		defaultParamPanel.add(getSelectedCountries());
		defaultParamPanel.add(Box.createHorizontalStrut(3));

		// show the projects from the selected country
		showListOnly = true; // TN75
		_Projects = new SimpleEntityList("Projects", PROJECT_NAME_MAX_LENGTH, null, showListOnly, -1);

		// detect any country selection change
		_Projects.addListListener(this);
		_Projects.setSelectedIndex(0);
		// detect any project list change
		_Projects.getAddListener().setListener(this, showListOnly);
		_Projects.getDeleteListener().setListener(this, showListOnly);

		defaultParamPanel.add(_Projects);

		defaultParamPanel.add(Box.createHorizontalStrut(3));

		// show the carecenters from the selected project
		showListOnly = false;
		_CareCenters = new SimpleEntityList("CareCenters", CARECENTER_NAME_MAX_LENGTH, null, showListOnly, -1);

		// detect any country selection change
		getCareCenters().addListListener(this);
		getCareCenters().setSelectedIndex(0);
		// detect any careCenters list change
		getCareCenters().getAddListener().setListener(this, showListOnly);
		getCareCenters().getDeleteListener().setListener(this, showListOnly);
		getCareCenters().getRenameListener().setListener(this, showListOnly);//TN78

		defaultParamPanel.add(getCareCenters());

		defaultParamPanel.setBorder(BorderFactory.createTitledBorder("Add or delete following entities"));
		contentPanel1.add(defaultParamPanel, java.awt.BorderLayout.CENTER);

		// buttons
		JPanel buttonPanel = new JPanel();
		Dimension buttonDim = new Dimension(BUTTON_PANEL_WIDTH, BUTTON_PANEL_HEIGHT);
		buttonPanel.setMinimumSize(buttonDim);
		buttonPanel.setMaximumSize(buttonDim);
		buttonPanel.setPreferredSize(buttonDim);
		buttonPanel.setSize(buttonDim);

		final int NBR_BUTTONS = 1; //
		buttonPanel.setLayout(new GridLayout(NBR_BUTTONS, 1, 0, 15));// nbr

		_SaveQuitButton = new JButton(SAVE_CMD);
		_SaveQuitButton.addActionListener(this);
		_SaveQuitButton.setForeground(Color.blue);
		buttonPanel.add(_SaveQuitButton);
		contentPanel1.add(buttonPanel, java.awt.BorderLayout.SOUTH);

		// defaultParamPanel.setBorder(BorderFactory.createLineBorder(Color.green));

		return contentPanelMaster;
	}
	
	/**
	 * TN97 entryform
	 */
	protected void internationalize() {

	}

	public void addUserActionListener(AdminParamDescriptor descriptor) {
		_SaveQuitButton.addActionListener(descriptor);
		_Descriptor = descriptor;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _SaveQuitButton) { //

			saveContextConfig();

		} else
			System.out.println(e.getSource());
	}

	/**
	 * 
	 * @return
	 */
	private int saveContextConfig() {

		return _ConfigurationManager.saveAdminContextConfig();
	}

	/**
	 * let's be notified when a list selection change and update the model of
	 * the projects
	 * 
	 * @param e
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting() == false) {
			String selectedValue = null;
			String source = ((JList) e.getSource()).getName();
			selectedValue = (String) ((JList) e.getSource()).getSelectedValue();
			if (source.equals("Countries")) {

				if (selectedValue != null) {
					_CurrentCountryLabelSelected = selectedValue;
					// update view with country's projects list
					// load the corresponding projects's country
					List<String> currentProjects = _ConfigurationManager.getConfigFileProjectNames(_CurrentCountryLabelSelected);
					// clear the current project list
					// and update the list+model
					// _Projects.getEntityListModel().clear();
					// if (currentProjects != null) {
					// for (String project : currentProjects) {
					// _Projects.getEntityListModel().addElement(project);
					// }

					// load all projects from the first country
					getProjects().setEntities(currentProjects);
					getProjects().setSelectedIndex(0);
					if (currentProjects == null) {
						// clear carecenter list
						System.out.println("CLR CENTER LIST");
					}

				}
			} else if (source.equals("Projects")) {
				// if (selectedValue != null)
				_CurrentProjectSelected = selectedValue;
				if (_CurrentProjectSelected != null) {

					// System.out
					// .println(" valueChanged _CurrentProjectSelected ="
					// + _CurrentProjectSelected);
					// update view with projects's carecenters list
					// load the corresponding projects's country
					List<String> currentCareCenters = _ConfigurationManager.getConfigFileCareCentersNames(getCurrentCountrySelected(), getCurrentProjectSelected());
					// clear the current project list
					// and update the list+model
					getCareCenters().getEntityListModel().clear();
					// if (currentCareCenters != null){
					// for (String carecenter : currentCareCenters) {
					// getCareCenters().getEntityListModel().addElement(
					// carecenter);
					// }

					// show the carecenters from the selected project
					getCareCenters().setEntities(currentCareCenters);
					getCareCenters().setSelectedIndex(0);

				} else {
					getCareCenters().getEntityListModel().clear();
				}

			} else if (source.equals("CareCenters")) {

			} else
				System.out.println(getClass().getName() + "::valueChanged: Error: unknown source!");

		}
	}

	/**
	 * an item is going to be added to the list check if allowed
	 */
	public boolean notify(AddListener source, Object objAdded) {

		getDescriptor().getWizard().setNextFinishButtonEnabled(false);
		// System.out.println("AddListener source="+source);
		// System.out.println("objAdded="+objAdded);
		boolean ret = false;

		// if (source.getEntityName().equals("Countries")) {
		// ret = true; // always true
		//
		// Country country = new Country((String) objAdded);
		// AdminConfigLoader.getEntryFormConfig().getCountries().add(country);
		// } else

		if (source.getEntityName().equals("Projects")) {
			// must check if a country is selected
			ret = _CurrentCountryLabelSelected != null;
			if (ret) {
				_ConfigurationManager.addProject(_CurrentCountryLabelSelected, (String) objAdded);
				_ConfigurationManager.setConfigurationSaved(false);
			} else
				JOptionPane.showMessageDialog(null, COUNTRIES_EMPTY, "Error", JOptionPane.ERROR_MESSAGE, null);
		} else if (source.getEntityName().equals("CareCenters")) {
			// must check if a project is selected
			ret = _CurrentProjectSelected != null;
			if (ret) {
				_ConfigurationManager.addCareCenter(_CurrentCountryLabelSelected, _CurrentProjectSelected, (String) objAdded);
				_ConfigurationManager.setConfigurationSaved(false);

			} else
				JOptionPane.showMessageDialog(null, PROJECTS_EMPTY, "Error", JOptionPane.ERROR_MESSAGE, null);
		}

		//
		_SaveQuitButton.setEnabled(_ConfigurationManager.checkCountryComplete(/* _CurrentCountryLabelSelected */));

		return ret;
	}

	/**
	 * an item is going to be deleted from the list check if allowed
	 */
	public boolean notify(DeleteListener source, Object objDeleted) {
		// AdminConfigLoader.setConfigurationSaved(false);
		getDescriptor().getWizard().setNextFinishButtonEnabled(false);
		// System.out.println("DeleteListener source="+source);
		// System.out.println("objAdded="+objAdded);
		boolean proceed = false;

		// if (source.getEntityName().equals("Countries")) {
		// // check if project list is empty
		// proceed = !ConfigLoaderBase.countryHasProjects((String) objDeleted);
		// if (proceed) {
		// Country country = new Country((String) objDeleted);
		// ConfigLoaderBase.getSelectedCountries().remove(country);
		// } else
		// JOptionPane.showMessageDialog(null, COUNTRYPROJECT_NOTEMPTY,
		// "Error", JOptionPane.ERROR_MESSAGE, null);
		// } else

		if (source.getEntityName().equals("Projects")) {

			// check if project list is empty
			proceed = !_ConfigurationManager.projectConfigFileHasCareCenters(_CurrentCountryLabelSelected, (String) objDeleted);
			if (proceed) {
				// Project project = new Project((String) objDeleted);
				// AdminConfigLoader.getEntryFormConfig().getProjects(_CurrentCountryLabelSelected)
				// .remove(project);
				_ConfigurationManager.deleteProject(getCurrentCountrySelected(), (String) objDeleted);
				_ConfigurationManager.setConfigurationSaved(false);
			} else
				JOptionPane.showMessageDialog(null, COUNTRYPROJECT_NOTEMPTY, "Error", JOptionPane.ERROR_MESSAGE, null);

		} else if (source.getEntityName().equals("CareCenters")) {
			proceed = true; // always true
			// AdminConfigLoader.deleteProject(getCurrentProjectSelected(),
			// (String) objDeleted);
			_ConfigurationManager.deleteCareCenter(getCurrentCountrySelected(), getCurrentProjectSelected(), (String) objDeleted);
			_ConfigurationManager.setConfigurationSaved(false);
		}

		//
		_SaveQuitButton.setEnabled(_ConfigurationManager.checkCountryComplete(/* _CurrentCountryLabelSelected */));

		return proceed;
	}
	

	/**
	 * an item is going to be renamed from the list check if allowed
	 *///TN78
	public boolean notify(RenameListener source, Object objRenamed, Object newValue) {
		getDescriptor().getWizard().setNextFinishButtonEnabled(false);
		boolean proceed = false;

		if (source.getEntityName().equals("Projects")) {

				// not needed ...

		} else if (source.getEntityName().equals("CareCenters")) {
			proceed = true; // always true
			_ConfigurationManager.renameCareCenter(getCurrentCountrySelected(), getCurrentProjectSelected(), (String)objRenamed, (String)newValue);
			_ConfigurationManager.setConfigurationSaved(false);
		}

		//
		_SaveQuitButton.setEnabled(_ConfigurationManager.checkCountryComplete(/* _CurrentCountryLabelSelected */));

		return proceed;
	}
	

	public SimpleEntityList getSelectedCountries() {
		return _SelectedCountries;
	}

	public void setSelectedCountries(SimpleEntityList selectedCountries) {
		_SelectedCountries = selectedCountries;
	}

	public SimpleEntityList getProjects() {
		return _Projects;
	}

	public SimpleEntityList getCareCenters() {
		return _CareCenters;
	}

	public void setCareCenters(SimpleEntityList careCenters) {
		_CareCenters = careCenters;
	}

	public String getCurrentCountrySelected() {
		return _CurrentCountryLabelSelected;
	}

	public void setCurrentCountrySelected(String currentCountrySelected) {
		_CurrentCountryLabelSelected = currentCountrySelected;
	}

	public String getCurrentProjectSelected() {
		return _CurrentProjectSelected;
	}

	public void setCurrentProjectSelected(String currentProjectSelected) {
		_CurrentProjectSelected = currentProjectSelected;
	}

	/**
	 * called by descriptor so that the panel can be initialized
	 */
	public void configurePanelStartState() {
		//
		_SaveQuitButton.setEnabled(_ConfigurationManager.checkCountryComplete(/*
																			 * getCurrentCountrySelected
																			 * (
																			 * )
																			 */));

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

	@Override
	// TN97
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_ADMINPANEL;
	}
}
