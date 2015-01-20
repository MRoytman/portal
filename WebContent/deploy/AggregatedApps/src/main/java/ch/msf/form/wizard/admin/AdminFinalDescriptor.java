package ch.msf.form.wizard.admin;


import ch.msf.form.wizard.FinalPanel;

import com.nexes.wizard.WizardPanelDescriptor;

public class AdminFinalDescriptor extends WizardPanelDescriptor {

	public static final String IDENTIFIER = "FINAL_PANEL";

	FinalPanel _FinalPanel;

	public AdminFinalDescriptor() {

		_FinalPanel = new FinalPanel();
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(_FinalPanel);

	}

	public Object getNextPanelDescriptor() {
		return FINISH;
	}

	public Object getBackPanelDescriptor() {
		return AdminParamDescriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {

		// if (!AdminConfigLoader.isConfigurationSaved()){
		// int result = JOptionPane.showConfirmDialog(null,
		// "You have not saved your configuration! Are you sure you want to leave?",
		// "Title", JOptionPane.OK_CANCEL_OPTION);
		// if (result == JOptionPane.CANCEL_OPTION) {
		// getWizard().setNextFinishButtonEnabled(false);
		// getWizard().setBackButtonEnabled(true);
		// } else if (result == JOptionPane.NO_OPTION) {
		//
		// }
		//
		// }

	}

	// public void displayingPanel() {
	//
	// Thread t = new Thread() {
	//
	// public void run() {
	//
	// try {
	// Thread.sleep(2000);
	// _FinalPanel.setProgressValue(25);
	// _FinalPanel.setProgressText("Server Connection Established");
	// Thread.sleep(500);
	// _FinalPanel.setProgressValue(50);
	// _FinalPanel.setProgressText("Transmitting Data...");
	// Thread.sleep(3000);
	// _FinalPanel.setProgressValue(75);
	// _FinalPanel.setProgressText("Receiving Acknowledgement...");
	// Thread.sleep(1000);
	// _FinalPanel.setProgressValue(100);
	// _FinalPanel.setProgressText("Data Successfully Transmitted");
	//
	// getWizard().setNextFinishButtonEnabled(true);
	// getWizard().setBackButtonEnabled(true);
	//
	// } catch (InterruptedException e) {
	//
	// _FinalPanel.setProgressValue(0);
	// _FinalPanel.setProgressText("An Error Has Occurred");
	//
	// getWizard().setBackButtonEnabled(true);
	// }
	//
	// }
	// };
	//
	// t.start();
	// }

	public void aboutToHidePanel() {
		// Can do something here, but we've chosen not not.
	}

}
