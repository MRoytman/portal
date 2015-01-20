package ch.msf.form;

import java.text.DecimalFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

/**
 * ALLOW BLANK VALUE TO BE INSERTED IN NUMERIC FIELD
 * 
 * @author cmi
 * 
 */
public class MyDecimalFormatter extends NumberFormatter {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyDecimalFormatter(DecimalFormat numberFormat) {
		super(numberFormat);
		setValueClass(Float.class);//TN121 utile?
//		System.out.println("setValueClass(Float.class)");
		// _IntegerFormat = numberFormat;
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
