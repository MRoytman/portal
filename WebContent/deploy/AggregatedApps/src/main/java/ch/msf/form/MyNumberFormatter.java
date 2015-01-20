package ch.msf.form;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

/**
 * ALLOW BLANK VALUE TO BE INSERTED IN NUMERIC FIELD
 * 
 * @author cmi
 * 
 */
public class MyNumberFormatter extends NumberFormatter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyNumberFormatter(NumberFormat numberFormat) {
		super(numberFormat);
	}

	public Object stringToValue(String text) throws ParseException {
		if (text.length() == 0)
			return null;
		return super.stringToValue(text);
	}

	public String valueToString(Object value) throws ParseException {
		if (value == null)
			return "";
		return super.valueToString(value);
	}

}
