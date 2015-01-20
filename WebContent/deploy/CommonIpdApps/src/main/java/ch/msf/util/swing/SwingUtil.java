package ch.msf.util.swing;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ch.msf.CommonConstants;
import ch.msf.util.StackTraceUtil;

public class SwingUtil {
	
	/**
	 * utility to set the focus to a text field
	 * @param textField
	 */
	public static void setFieldFocus(final JTextField textField) {
		Runnable setPasswordFocus = new Runnable() {
			public void run() {
				textField.requestFocusInWindow();
			}
		};
		//  run it not in the current stack
		try {

			SwingUtilities.invokeLater(setPasswordFocus);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(ex), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		}
	}

}
