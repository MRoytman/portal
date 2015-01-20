package ch.msf.form.wizard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import ch.msf.CommonConstants;
import ch.msf.manager.ConfigurationManager;
import ch.msf.service.ServiceHelper;

import com.nexes.wizard.WizardPanelDescriptor;

public class LoginDescriptor extends WizardPanelDescriptor implements ActionListener {

	protected LoginPanel _LoginPanel;

	public static final String IDENTIFIER = "LOGIN_PANEL";

	protected ConfigurationManager _ConfigurationManager;

	private String _MsfUser;
	private String _MsfUserPwd;

	public LoginDescriptor() {
		// super(IDENTIFIER, new LoginPanel());
		_LoginPanel = new LoginPanel();
		_LoginPanel.addUserActionListener(this);

		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(_LoginPanel);

		_ConfigurationManager = ServiceHelper.getConfigurationManagerService();

		_MsfUser = _ConfigurationManager.getConfigField("msfUser");
		_MsfUserPwd = _ConfigurationManager.getConfigField("msfUserPwd");

		System.out.println("---------" + _MsfUserPwd + "---------");

		_LoginPanel.setDescriptor(this);

	}

	public Object getNextPanelDescriptor() {
		return null;// ParamDescriptor.IDENTIFIER;
	}

	public Object getBackPanelDescriptor() {
		return null;
	}

	public void aboutToDisplayPanel() {
		// show header info
		_LoginPanel.buildBannerInfo();
		// setNextButtonAccordingToUserInput();
		getWizard().setNextFinishButtonEnabled(false);
	}

	/**
	 * Override this method to perform functionality just before the panel is to
	 * be hidden.
	 */
	public void aboutToHidePanel() {

		_ConfigurationManager.getProgramRunner().getSelectedParams().put("user", _LoginPanel.getUserName());
	}

	public void actionPerformed(ActionEvent e) {
		handleLogin();
	}

	public void handleLogin() {
		// check if user+pwd ok and if so pass to the next screen
		if (checkUserInput())
			getWizard().setCurrentPanel(ParamDescriptor.IDENTIFIER, true);
		else {
			String errMess = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_OPERATION_FAILED);
			JOptionPane.showMessageDialog(null, errMess, "Fatal error", JOptionPane.OK_OPTION, null);
		}

	}

	/**
	 * check user & pwd
	 * 
	 * @return
	 */
	private boolean checkUserInput() {
		if (_LoginPanel.getUserName() == null || _LoginPanel.getUserName().length() == 0)
			return false;
		if (_LoginPanel.getPassword() == null || _LoginPanel.getPassword().length() == 0)
			return false;

		// check user & pwd
		// if (_LoginPanel.getUserName().equals(_LoginPanel.getPassword()))
		if (_LoginPanel.getUserName().equals(_MsfUser) && _LoginPanel.getPassword().equals(_MsfUserPwd))

			return true;

		return false;
	}

	// private void setNextButtonAccordingToUserInput() {
	// if (_LoginPanel.getUserName() == null ||
	// _LoginPanel.getUserName().length() == 0)
	// getWizard().setNextFinishButtonEnabled(false);
	// else
	// getWizard().setNextFinishButtonEnabled(true);

	// }

}
