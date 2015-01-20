package ch.msf.form.wizard;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.util.swing.GridLayout2;
import ch.msf.util.swing.SwingUtil;

public class LoginPanel extends AbstractWizardPanel implements KeyListener {

	private static final long serialVersionUID = 1L;

	private JTextField _User;
	private JComboBox _Users;
	private JPasswordField _PasswordField;
	private JButton enterButton;

	public LoginPanel() {
		super();
	}

	public JPanel getContentPanel() {

		JPanel contentPanelMaster = new JPanel();

		JPanel contentPanel1 = new JPanel(new BorderLayout());
		contentPanelMaster.add(contentPanel1);

		contentPanel1.add(Box.createVerticalStrut(100), java.awt.BorderLayout.NORTH);

		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridLayout2(2, 2, 3, 3)); //
		JLabel name = new JLabel("Name:");
		loginPanel.add(name);


		if (_ConfigurationManager.isUserPermissionsActive()){//TN148 (new format)
			List<String> roles = _ConfigurationManager.getRoles();

			_Users = new JComboBox();
			for (String role:roles) {
				_Users.addItem(role);
			}
			loginPanel.add(_Users);
			_Users.addKeyListener(this);
		}
		else{ // old way
			_User = new JTextField(EntryFormConfigurationManagerImpl.USER_MAX_SIZE);
			loginPanel.add(_User);
			_User.setText(_ConfigurationManager.getConfigField("msfUser"));
			_User.addKeyListener(this);
		}

		_PasswordField = new JPasswordField(EntryFormConfigurationManagerImpl.PWD_MAX_SIZE);
		JLabel pwd = new JLabel("Password:");
		loginPanel.add(pwd);
		loginPanel.add(_PasswordField);
		loginPanel.setBorder(BorderFactory.createTitledBorder("Please enter your connection information:"));

		enterButton = new JButton(CommonConstants.ENTER);

		contentPanel1.add(loginPanel, java.awt.BorderLayout.CENTER);
		contentPanel1.add(enterButton, java.awt.BorderLayout.SOUTH);

		// set the focus on password input
		SwingUtil.setFieldFocus(_PasswordField);//
		_PasswordField.addKeyListener(this);

		return contentPanelMaster;
	}

	/**
	 * TN97
	 */
	protected void internationalize() {

	}

	public String getUserName() {
		if (_User != null)
		return _User.getText();
		if (_Users != null){//TN148
			return (String)_Users.getSelectedItem();
		}
		return null;
	}

	public String getPassword() {
		return new String(_PasswordField.getPassword()); //
	}

	public void addUserActionListener(LoginDescriptor loginDescriptor) {
		// _User.addKeyListener(loginDescriptor);
		enterButton.addActionListener(loginDescriptor);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
			((LoginDescriptor) getDescriptor()).handleLogin();
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	@Override
	public JTable getTable() {
		return null;
	}

	@Override
	public DefaultTableModel getTableModel() {
		return null;
	}

	@Override
	// TN97
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_LOGINPANEL;
	}

}
