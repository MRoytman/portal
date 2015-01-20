package ch.msf.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.model.datavalidator.NumericValidator;
import ch.msf.service.ServiceHelper;
import ch.msf.util.StackTraceUtil;

public class MyDecimalFieldEditor extends DefaultCellEditor {

	JFormattedTextField _Ftf;

	private NumericValidator _NumericValidator = null;
	private static final long serialVersionUID = 1L;

	private int _Scale;

	public MyDecimalFieldEditor(final String key, int scale) {

		super(new JFormattedTextField());
		_Ftf = (JFormattedTextField) getComponent();

		// //Set up the editor for the integer cells.
		// _DecimalFormat = DecimalFormat.getNumberInstance();
		_Scale = scale;
		DecimalFormat decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(_Scale);
		
		 DecimalFormatSymbols dfs = decimalFormat.getDecimalFormatSymbols();
		 
			System.out.println("MyDecimalFieldEditor, dfs.getDecimalSeparator() = " + dfs.getDecimalSeparator());
			if (dfs.getDecimalSeparator() == ','){ //TN121
				System.out.println("Changing DecimalSeparator to '.'");
				dfs.setDecimalSeparator('.');
				decimalFormat.setDecimalFormatSymbols(dfs);
			}

//			System.out.println("MyDecimalFieldEditor, dfs.getPatternSeparator() = " + dfs.getPatternSeparator());
//			System.out.println("MyDecimalFieldEditor, dfs.getGroupingSeparator() = " + dfs.getGroupingSeparator());
//			System.out.println("MyDecimalFieldEditor, dfs.getPerMill() = " + dfs.getPerMill());
//			System.out.println("MyDecimalFieldEditor, dfs.getZeroDigit() = " + dfs.getZeroDigit());
//			System.out.println("MyDecimalFieldEditor, dfs.getDigit() = " + dfs.getDigit());
//			System.out.println("MyDecimalFieldEditor, dfs.getMonetaryDecimalSeparator() = " + dfs.getMonetaryDecimalSeparator());
			System.out.println("MyDecimalFieldEditor, scale = " + _Scale);
		NumberFormatter intFormatter = new MyDecimalFormatter(decimalFormat);
		intFormatter.setFormat(decimalFormat);
		// intFormatter.setMinimum(0); // ok but use business rule instead
		// intFormatter.setMaximum(100);// ok but use business rule instead

		_Ftf.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
		_Ftf.setValue(1234.1);

		_Ftf.setHorizontalAlignment(JTextField.TRAILING);
		_Ftf.setFocusLostBehavior(JFormattedTextField.COMMIT);
		//
		// React when the user presses Enter while the editor is
		// active. (Tab is handled as specified by
		// JFormattedTextField's focusLostBehavior property.)
		_Ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
		_Ftf.getActionMap().put("check", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!_Ftf.isEditValid()) { // The text is invalid.
					if (userSaysRevert()) { // reverted
						_Ftf.postActionEvent(); // inform the editor
					} else {
						doAction(false);
					}
				} else
					try { // The text is valid,
						_Ftf.commitEdit(); // so use it.
						_Ftf.postActionEvent(); // stop editing
					} catch (java.text.ParseException exc) {
					}
			}
		});

		_NumericValidator = new NumericValidator(key);

	}

	/**
	 * called when the cell is edited
	 */
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		String text = null;
		if (value != null) {
			DecimalFormat numberFormat = new DecimalFormat();
			numberFormat.setMaximumFractionDigits(_Scale);
			text = numberFormat.format(Double.parseDouble(value.toString()));

//			 System.out.println("getTableCellEditorComponent text=" + text);
		} else
			text = "";

		ftf.setText(text);

		return ftf;
	}

	// returns the cell's current value
	// Override to ensure that the value remains a decimal.
	public Object getCellEditorValue() {
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		Object o = ftf.getValue();
		if (o instanceof String) {
			System.out.println("getCellEditorValue = " + o);
			return o;
		}
//		if (o != null)
//			System.out.println("getCellEditorValue3  o.getClass():" + o.getClass());
		if (o instanceof Number) {
			String text = null; // ftf.getText();

			try {
				// DecimalFormat df = new DecimalFormat("#0.00");
				DecimalFormat numberFormat = new DecimalFormat();
				numberFormat.setMaximumFractionDigits(_Scale);
				text = numberFormat.format(Double.parseDouble(o.toString()));
				Number n = numberFormat.parse(text);
//				System.out.println("getCellEditorValue3 text:" + text);
//				System.out.println("getCellEditorValue3 ftf.getText():" + ftf.getText());
//				System.out.println("getCellEditorValue3:" + n);
				return n;
			} catch (ParseException e) {
				e.printStackTrace();
				return o;
			}
		}

		// System.out.println("getCellEditorValue: o isn't a string");
		return null;

	}

	// Override to check whether the edit isvalid, setting the value if it is
	// and complaining if it isn't. If it's OK for the editor to go away, we
	// need to invoke the superclass'sversion of this method so that everything
	// gets cleaned up.
	// CM: called when mouse click on another cell
	public boolean stopCellEditing() {

		JFormattedTextField ftf = (JFormattedTextField) getComponent();

		boolean ok = ftf.isEditValid();
		boolean forceAccept = true;
		if (ok) {
			// perform checks on new value
			// replace first any ',' by '.'
			_Ftf.setText(_Ftf.getText().replace(',', '.'));//TN121
			ok = _NumericValidator.doFormatChecks(_Ftf.getText());
			if (ok) {
				// Integer errorCode = checkBusinessRules();
				Integer errorCode = _NumericValidator.checkBusinessRules(_Ftf.getText()/* _RuntimeCheckParam */);
				if (errorCode != null) { // error case
					// show error code message and get user instruction
					// to force or not
					String errMess = ServiceHelper.getMessageService().getMessage(errorCode);
					forceAccept = userSaysForceAccept(errMess);
				}
				if ((ok && errorCode == null) || forceAccept) {

					try { // The text is valid,
						_Ftf.commitEdit(); // so use it.
						doAction(true);
					} catch (java.text.ParseException exc) {
					}
					return super.stopCellEditing();
				}
			}
		}
		if (ok && !forceAccept) { // user said 'edit'
			doAction(false);
			return false;
		}
		if (!ok) {
			doAction(false);
			if (!userSaysRevert()) { // reverted
				return false;// don't let the editor go away
			}
		}

		return super.stopCellEditing();
	}

	/**
	 * Lets the user know that the text they entered is bad. Returns true if the
	 * user elects to revert to the last good value. Otherwise, returns false,
	 * indicating that the user wants to continue editing.
	 */
	protected boolean userSaysRevert() {
		Toolkit.getDefaultToolkit().beep();
		_Ftf.selectAll();

		String errMessEdit = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_EDIT);
		String errMessRevert = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_REVERT);
		String errMessBadParam = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_BAD_PARAM);
		String errMessBadDate = ServiceHelper.getMessageService().getMessage(103);
		Object[] options = { errMessEdit, errMessRevert };
		// Object[] options = { "Edit", "Revert" };
		int answer = JOptionPane.showOptionDialog(null, errMessBadDate, errMessBadParam, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

		if (answer == 1) { // Revert!
			_Ftf.setValue(_Ftf.getValue());
			return true;
		}
		return false;
	}

	protected boolean userSaysForceAccept(String message) {
		Toolkit.getDefaultToolkit().beep();
		_Ftf.selectAll();

		String errMessEdit = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_EDIT);
		String errMessAccept = ServiceHelper.getMessageService().getMessage(CommonIpdConstants.MESSAGE_ACCEPT);
		String errMessBadParam = ServiceHelper.getMessageService().getMessage(CommonConstants.MESSAGE_BAD_PARAM);
		Object[] options = { errMessEdit, errMessAccept };
		int answer = JOptionPane.showOptionDialog(null, message, errMessBadParam, JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);

		if (answer == 1) { // Accept!
			return true;
		}
		return false;
	}

	private void doAction(final boolean ok) {

		Runnable selectNewInsertedLine = new Runnable() {
			public void run() {
				if (!ok) {
					getComponent().setForeground(Color.red);
					getComponent().setBackground(Color.pink);
				} else {
					// ok case
					getComponent().setForeground(Color.black);
					getComponent().setBackground(Color.white);
				}

			}
		};
		// my feeling is not to run it in the current stack
		try {
			SwingUtilities.invokeLater(selectNewInsertedLine);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(null, CommonConstants.ERR_MESS + StackTraceUtil.getCustomStackTrace(ex), "Fatal error", JOptionPane.ERROR_MESSAGE, null);
		}

	}

}
