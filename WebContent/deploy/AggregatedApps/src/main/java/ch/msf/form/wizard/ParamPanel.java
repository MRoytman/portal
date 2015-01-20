package ch.msf.form.wizard;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import ch.msf.CommonIpdConstants;
import ch.msf.error.FatalException;
import ch.msf.util.swing.GridLayout2;

public class ParamPanel extends AbstractWizardPanel implements ActionListener {

	private static final long serialVersionUID = 1L;
	private JComboBox _Languages;
	private JComboBox _Countries;
	private JComboBox _Projects;
	private JComboBox _CareCenters;

	// boolean _IsAdmin = false;

	public ParamPanel() {

		super(null);
	}

	public JPanel getContentPanel() {

		Dimension standardComboDim = new Dimension(220, 50);

		JPanel contentPanelMaster = new JPanel();

		JPanel contentPanel1 = new JPanel(new BorderLayout());
		contentPanelMaster.add(contentPanel1);

		JPanel defaultParamPanel = new JPanel();
		defaultParamPanel.setLayout(new GridLayout2(2, 2, 30, 30)); //

		contentPanel1.add(Box.createVerticalStrut(100), java.awt.BorderLayout.NORTH);

		try {
			// languages
			_Languages = new JComboBox(_ConfigurationManager.getLanguages().toArray());
			if (_ConfigurationManager.getLanguages().size() > 0)
				_Languages.setSelectedIndex(0);
			_Languages.addActionListener(this);
			_Languages.setPreferredSize(standardComboDim);
			_Languages.setBorder(BorderFactory.createTitledBorder("Languages"));
			defaultParamPanel.add(_Languages);

			// countries
			// get the allcountries service
			List<String> currentCountries = _ConfigurationManager.getSelectedCountriesManager()
					.getDbAllSelectedCountrieNameLabels(_ConfigurationManager.getDefaultLanguage(), true);

			_Countries = new JComboBox(currentCountries.toArray());
			if (currentCountries.size() > 0)
				_Countries.setSelectedIndex(0);
			_Countries.addActionListener(this);
			_Countries.setPreferredSize(standardComboDim);
			_Countries.setBorder(BorderFactory.createTitledBorder("Countries"));
			defaultParamPanel.add(_Countries);
			String currentCountrySelected = (String) _Countries.getSelectedItem();

			// projects
			List<String> currentProjects = _ConfigurationManager.getSelectedCountriesManager().getDbProjectNames(currentCountrySelected, true);

			_Projects = new JComboBox(currentProjects.toArray());
			if (currentProjects.size() > 0)
				_Projects.setSelectedIndex(0);
			_Projects.addActionListener(this);
			_Projects.setPreferredSize(standardComboDim);
			_Projects.setBorder(BorderFactory.createTitledBorder("Projects"));
			defaultParamPanel.add(_Projects);
			String currentProjectSelected = (String) _Projects.getSelectedItem();

			// care centers
			List<String> currentCareCenters = _ConfigurationManager.getSelectedCountriesManager().getDbCareCenters(currentCountrySelected, currentProjectSelected, true);

			_CareCenters = new JComboBox(currentCareCenters.toArray());
			if (currentCareCenters.size() > 0)
				_CareCenters.setSelectedIndex(0);
			_CareCenters.addActionListener(this);
			_CareCenters.setPreferredSize(standardComboDim);
			_CareCenters.setBorder(BorderFactory.createTitledBorder("CareCenters"));
			defaultParamPanel.add(_CareCenters);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FatalException("Got exception! ", e);
		}

		// not needed anymore since standardComboDim is enlarged
		// defaultParamPanel.add(Box.createHorizontalStrut(10),
		// java.awt.BorderLayout.CENTER);
		defaultParamPanel.setBorder(BorderFactory.createTitledBorder("Change default values if needed"));

		contentPanel1.add(defaultParamPanel, java.awt.BorderLayout.SOUTH);

		return contentPanelMaster;
	}

	/**
	 * TN97
	 */
	protected void internationalize() {

	}

	/**
	 * a new selection has been made
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == _Languages) {
			_ConfigurationManager.setDefaultLanguage((String) ((JComboBox) e.getSource()).getSelectedItem());
			((JComboBox) e.getSource()).getParent().getParent().repaint();
			System.out.println("_DefaultLanguage =" + _ConfigurationManager.getDefaultLanguage());
		} else if (e.getSource() == _Countries) {
			// Config.getSelectedParams().add(
			// (String) ((JComboBox) e.getSource()).getSelectedItem());

			// change the projects combobox
			String currentCountrySelected = (String) _Countries.getSelectedItem();
			List<String> currentProjects = _ConfigurationManager.getSelectedCountriesManager().getDbProjectNames(currentCountrySelected, true);

			// clear the current project list
			// and update the list+model
			_Projects.removeAllItems();
			for (String project : currentProjects) {
				_Projects.addItem(project);
			}
			if (currentProjects.size() > 0)
				_Projects.setSelectedIndex(0);

			String currentProjectSelected = (String) _Projects.getSelectedItem();
			List<String> currentCareCenters = _ConfigurationManager.getSelectedCountriesManager().getDbCareCenters(currentCountrySelected, currentProjectSelected, true);
			// clear the current carecenter list
			// and update the list+model
			_CareCenters.removeAllItems();
			for (String project : currentCareCenters) {
				_CareCenters.addItem(project);
			}
			if (currentCareCenters.size() > 0)
				_CareCenters.setSelectedIndex(0);

			// _Projects.getParent().repaint();
			// (_Projects).repaint();
		} else if (e.getSource() == _Projects) {
			// change the carecenter combobox
			String currentCountrySelected = (String) _Countries.getSelectedItem();
			String currentProjectSelected = (String) _Projects.getSelectedItem();
			if (currentCountrySelected != null && currentProjectSelected != null) {
				List<String> currentCareCenters = _ConfigurationManager.getSelectedCountriesManager().getDbCareCenters(currentCountrySelected, currentProjectSelected, true);

				// clear the current carecenter list
				// and update the list+model
				_CareCenters.removeAllItems();
				for (String project : currentCareCenters) {
					_CareCenters.addItem(project);
				}
				if (currentCareCenters.size() > 0)
					_CareCenters.setSelectedIndex(0);
			}

		} else if (e.getSource() == _CareCenters) {

		} else
			System.out.println(e.getSource());
	}

	/**
	 * @return the _Projects
	 */
	public JComboBox getProjects() {
		return _Projects;
	}

	/**
	 * @return the _Countries
	 */
	public JComboBox getCountries() {
		return _Countries;
	}

	/**
	 * @return the _CareCenters
	 */
	public JComboBox getCareCenters() {
		return _CareCenters;
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
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_PARAMPANEL;
	}

}
