package ch.msf.form;

import java.awt.event.ActionEvent;


/**
 * allow a return code that can be used by the issuer to the listener
 * @author cmi
 *
 */
public class ActionEventRetCode  extends ActionEvent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActionEventRetCode(ActionEvent e) {
		super(e.getSource(), e.getID(), e.getActionCommand());

	}

	private int _RetCode;

	public int getRetCode() {
		return _RetCode;
	}

	public void setRetCode(int _RetCode) {
		this._RetCode = _RetCode;
	}

}
