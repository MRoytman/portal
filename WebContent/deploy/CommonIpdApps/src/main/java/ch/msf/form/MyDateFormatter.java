package ch.msf.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.text.DateFormatter;


/**
 * ALLOW BLANK VALUE TO BE INSERTED IN DATE FIELD
 * @author cmi
 *
 */
public class MyDateFormatter extends DateFormatter /*JFormattedTextField.AbstractFormatter*/ {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat _Sdf = null;

	public MyDateFormatter(String sdfFormat) {
		_Sdf = new SimpleDateFormat(sdfFormat);
	}

	public Object stringToValue(String text) throws ParseException {
		if (text.length() == 0)
			return null;
		return _Sdf.parse(text);
	}

	public String valueToString(Object value) throws ParseException {
		if (value == null)
			return "";
		return _Sdf.format(value);
	}

	/**
	 * @return the _Sdf
	 */
	public SimpleDateFormat getSdf() {
		return _Sdf;
	}

}
