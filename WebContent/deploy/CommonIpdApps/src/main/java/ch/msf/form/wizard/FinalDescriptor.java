package ch.msf.form.wizard;

import com.nexes.wizard.WizardPanelDescriptor;




public class FinalDescriptor extends WizardPanelDescriptor {
    
    public static final String IDENTIFIER = "FINAL_PANEL";
    
    FinalPanel _FinalPanel;
    
    public FinalDescriptor() {
        
        _FinalPanel = new FinalPanel();
        setPanelDescriptorIdentifier(IDENTIFIER);
        setPanelComponent(_FinalPanel);
        
    }

    public Object getNextPanelDescriptor() {
        return FINISH;
    }
    
    public Object getBackPanelDescriptor() {
        return EncounterFormDescriptor.IDENTIFIER;
    }
    
    
//    public void aboutToDisplayPanel() {
//        
////        _FinalPanel.setProgressValue(0);
////        _FinalPanel.setProgressText("Connecting to Server...");
//
//        getWizard().setNextFinishButtonEnabled(false);
//        getWizard().setBackButtonEnabled(false);
//        
//    }
    
//    public void displayingPanel() {
//
//            Thread t = new Thread() {
//
//            public void run() {
//
//                try {
//                    Thread.sleep(2000);
//                    _FinalPanel.setProgressValue(25);
//                    _FinalPanel.setProgressText("Server Connection Established");
//                    Thread.sleep(500);
//                    _FinalPanel.setProgressValue(50);
//                    _FinalPanel.setProgressText("Transmitting Data...");
//                    Thread.sleep(3000);
//                    _FinalPanel.setProgressValue(75);
//                    _FinalPanel.setProgressText("Receiving Acknowledgement...");
//                    Thread.sleep(1000);
//                    _FinalPanel.setProgressValue(100);
//                    _FinalPanel.setProgressText("Data Successfully Transmitted");
//
//                    getWizard().setNextFinishButtonEnabled(true);
//                    getWizard().setBackButtonEnabled(true);
//
//                } catch (InterruptedException e) {
//                    
//                    _FinalPanel.setProgressValue(0);
//                    _FinalPanel.setProgressText("An Error Has Occurred");
//                    
//                    getWizard().setBackButtonEnabled(true);
//                }
//
//            }
//        };
//
//        t.start();
//    }
    
    public void aboutToHidePanel() {
        //  Can do something here, but we've chosen not not.
    }    
            
}
