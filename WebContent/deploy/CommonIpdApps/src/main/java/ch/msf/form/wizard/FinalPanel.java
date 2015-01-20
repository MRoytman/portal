package ch.msf.form.wizard;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import ch.msf.CommonIpdConstants;


public class FinalPanel extends AbstractWizardPanel {
 
	private static final long serialVersionUID = 1L;


	public FinalPanel() {
        
        super();
        
    }  
    

    
    public JPanel getContentPanel() {            
        
        JPanel contentPanel1 = new JPanel();
        
//        connectorGroup = new ButtonGroup();
//        welcomeTitle = new JLabel();
//        jPanel1 = new JPanel();
//        blankSpace = new JLabel();
////        progressSent = new JProgressBar();
////        progressDescription = new JLabel();
//        anotherBlankSpace = new JLabel();
//        yetAnotherBlankSpace1 = new JLabel();
//        jLabel1 = new JLabel();
//
//        contentPanel1.setLayout(new java.awt.BorderLayout());
//
//        welcomeTitle.setText("Now we will pretend to send this data somewhere for approval...");
//        contentPanel1.add(welcomeTitle, java.awt.BorderLayout.NORTH);
//
//        jPanel1.setLayout(new java.awt.GridLayout(0, 1));
//
//        jPanel1.add(blankSpace);
//
//        jPanel1.add(anotherBlankSpace);
//
//        jPanel1.add(yetAnotherBlankSpace1);
//
//        contentPanel1.add(jPanel1, java.awt.BorderLayout.CENTER);
//
//        jLabel1.setText("After the sending is completed, the Back and Finish buttons will enable below.");
//        contentPanel1.add(jLabel1, java.awt.BorderLayout.SOUTH);
        
        return contentPanel1;
    }
    
	/**
	 * TN97
	 */
	protected void internationalize() {

	}

	@Override
	public JTable getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DefaultTableModel getTableModel() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override //TN97
	public int getPannelStateCode() {
		return CommonIpdConstants.MESSAGE_PANNEL_STATE_FINALPANEL;
	}
}
