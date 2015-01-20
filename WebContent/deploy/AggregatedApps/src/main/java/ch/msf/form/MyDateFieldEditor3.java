package ch.msf.form;

import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;

import ch.msf.CommonConstants;
import ch.msf.CommonIpdConstants;
import ch.msf.model.datavalidator.DateValidator;
import ch.msf.service.ServiceHelper;
import ch.msf.util.StackTraceUtil;

public class MyDateFieldEditor3 extends DefaultCellEditor {

	private JFormattedTextField _Ftf;
	private SimpleDateFormat _Sdf = null;

	private Date _RuntimeCheckParam;

	private DateValidator _DateValidator = null;

	public MyDateFieldEditor3(final String key, Date runtimeCheckParam) {

		super(new JFormattedTextField());

		// _ConceptId = key;
		_RuntimeCheckParam = runtimeCheckParam;
		_Ftf = (JFormattedTextField) getComponent();

		// this formatter allows to input an empty field value
		DateFormatter dateFormatter = new MyDateFormatter(CommonConstants.SIMPLE_DATE_FORMAT);

		// CM: add a factory to manage the checks when lost of focus on click
		// (calls to stopCellEditing)
		_Ftf.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
		_Ftf.setValue(new Date());
		_Sdf = ((MyDateFormatter) _Ftf.getFormatter()).getSdf();

		_Ftf.setHorizontalAlignment(JTextField.TRAILING);
		_Ftf.setFocusLostBehavior(JFormattedTextField.COMMIT);

		// React when the user presses Enter while the editor is
		// active. (Tab is handled as specified by
		// JFormattedTextField's focusLostBehavior property.)
		// CM:define an handler when ENTER key is pressed
		_Ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
		_Ftf.getActionMap().put("check", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!_Ftf.isEditValid()) { // The text is invalid.
					if (userSaysRevert()) { // reverted
						_Ftf.postActionEvent(); // inform the editor
					} else {
						doAction(false);
					}
				} else {
					try { // The text is valid,
						_Ftf.commitEdit(); // so use it.
						_Ftf.postActionEvent(); // will call stopCellEditing()
					} catch (java.text.ParseException exc) {
					}
				}
			}
		});

		_DateValidator = new DateValidator(key, _Sdf.toPattern());

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// //Override to invoke setValue on the formatted text field.
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JFormattedTextField ftf2 = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		ftf2.setValue(value);
		return ftf2;
	}

	// returns the cell's current value
	// Override to ensure that the value remains an Integer.
	public Object getCellEditorValue() {
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		Object o = ftf.getValue();
		if (o instanceof String) {
			// System.out.println("getCellEditorValue = " + o);
			return o;
		} else if (o instanceof Number) {
			return o;
		} else if (o instanceof Date) {
			return o;
		} else {
			// if (DEBUG)
//			System.out.println("getCellEditorValue: o isn't a string");
			return null;
		}
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
			// ok = doBasicChecks(_Ftf.getText());
			ok = _DateValidator.doFormatChecks(_Ftf.getText());
			if (ok) {
				// Integer errorCode = checkBusinessRules();
				Integer errorCode = _DateValidator.checkBusinessRules(_Ftf.getText(), _RuntimeCheckParam);
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
		String errMessBadDate = ServiceHelper.getMessageService().getMessage(102);
		Object[] options = { errMessEdit, errMessRevert };

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

	// /**
	// * perform checks on new entered data
	// *
	// * @return
	// */
	// protected Integer checkBusinessRules() {
	//
	// BusinessRuleCheckerI businessRuleChecker = doBRChecks();
	// if (businessRuleChecker == null) { // ok
	// return null;
	// }
	// Integer errCode = businessRuleChecker.getErrorCode();
	// return errCode;
	// }
	//
	// /**
	// * do Business Rules checks
	// *
	// * @return
	// */
	// // @Override
	// public BusinessRuleCheckerI doBRChecks() {
	// BusinessRuleCheckerI businessRuleChecker = null;
	//
	// if (_Ftf.getText() != null && !_Ftf.getText().equals("")) {
	// // load the business rules
	// Object[] runtimeParams = new Object[1];
	// runtimeParams[0] = _RuntimeCheckParam;
	// return businessRuleChecker =
	// ServiceHelper.getBusinesRuleService().check(_ConceptId, _Ftf.getText(),
	// runtimeParams);
	// }
	// // ok case
	// return businessRuleChecker;
	// }

	// /**
	// * perform basic checks on passed date text
	// *
	// * @param text
	// * @param sdf
	// * @return
	// */
	// protected boolean doBasicChecks(String text) {
	// // allow empty string
	// if (text == null || text.equals(""))
	// return true;
	// String pattern = _Sdf.toPattern();
	// if (text.length() != pattern.length())
	// return false;
	//
	// return true;
	// }

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

	public JFormattedTextField getFtf() {
		return _Ftf;
	}

	public SimpleDateFormat getSdf() {
		return _Sdf;
	}

}
