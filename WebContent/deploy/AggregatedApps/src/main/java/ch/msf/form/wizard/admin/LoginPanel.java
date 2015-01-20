package ch.msf.form.wizard.admin;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.form.wizard.AbstractWizardPanel;
import ch.msf.manager.EntryFormConfigurationManagerImpl;
import ch.msf.util.swing.GridLayout2;
import ch.msf.util.swing.SwingUtil;

public class LoginPanel extends AbstractWizardPanel implements KeyListener {

	private static final long serialVersionUID = 1L;

	// private javax.swing.JLabel welcomeTitle;

	private JTextField _User;
	private JPasswordField _PasswordField;
	private JButton enterButton;

	public LoginPanel() {
		super(null);

	}


	public JPanel getContentPanel() {

		JPanel contentPanelMaster = new JPanel();

		JPanel contentPanel1 = new JPanel(new BorderLayout());
		contentPanelMaster.add(contentPanel1);

		contentPanel1.add(Box.createVerticalStrut(100),
				java.awt.BorderLayout.NORTH);

		JPanel loginPanel = new JPanel();
		loginPanel.setLayout(new GridLayout2(2, 2, 5, 5));
		JLabel name = new JLabel("Name:");
		loginPanel.add(name);
		_User = new JTextField(EntryFormConfigurationManagerImpl.USER_MAX_SIZE);
		loginPanel.add(_User);

		_User.setText(_ConfigurationManager.getConfigField("adminUser"));

		_PasswordField = new JPasswordField(EntryFormConfigurationManagerImpl.PWD_MAX_SIZE);
		JLabel pwd = new JLabel("Password:");

		loginPanel.add(pwd);
		loginPanel.add(_PasswordField);
		loginPanel
				.setBorder(BorderFactory
						.createTitledBorder("Please enter your connection information:"));

		enterButton = new JButton(CommonConstants.ENTER);

		contentPanel1.add(loginPanel, java.awt.BorderLayout.CENTER);
		contentPanel1.add(enterButton, java.awt.BorderLayout.SOUTH);

		_User.addKeyListener(this);
		_PasswordField.addKeyListener(this);

		// set the focus on password input
		SwingUtil.setFieldFocus(_PasswordField);

		return contentPanelMaster;
	}
	
	/**
	 * TN97 entryform
	 */
	protected void internationalize() {

	}

	public String getUserName() {
		return _User.getText();
	}

	public String getPassword() {
		return new String(_PasswordField.getPassword()); //
	}

	public void addUserActionListener(LoginDescriptor loginDescriptor) {
		// _User.addKeyListener(loginDescriptor);
		enterButton.addActionListener(loginDescriptor);
	}

	public void keyTyped(KeyEvent e) {
		// int key = e.getKeyCode();
		// if (key == KeyEvent.VK_ENTER) {
		// System.out.println("ENTER pressed");
		// // setNextButtonAccordingToUserInput();
		// }

	}

	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {
//			System.out.println("ENTER pressed");
			((LoginDescriptor) getDescriptor()).handleLogin();
		}

	}

	public void keyReleased(KeyEvent e) {

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
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_LOGINPANEL;
	}
}
