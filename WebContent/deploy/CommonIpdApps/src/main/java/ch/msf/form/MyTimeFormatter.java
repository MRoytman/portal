package ch.msf.form;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.text.DateFormatter;

/**
 * DOES THE CHECKING OF THE TIME FIELDS ALLOW BLANK VALUE TO BE INSERTED IN DATE
 * FIELD
 * 
 * @author cmi
 * 
 */
public class MyTimeFormatter extends DateFormatter /*
													 * JFormattedTextField.
													 * AbstractFormatter
													 */{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SimpleDateFormat _Sdf = null;
	private Character _HourMinuteSeparator;

	public MyTimeFormatter(String sdfFormat) {
		_Sdf = new SimpleDateFormat(sdfFormat);
		// try to detect the pattern separator
		if (sdfFormat != null) {
			for (int index = 0; index < sdfFormat.length(); index++) {
				Character c = sdfFormat.charAt(index);
				if (!Character.isLetterOrDigit(c)) {
					_HourMinuteSeparator = c;
					break;
				}
			}
		}
	}

	/**
	 * check the format of a string time value throw a parseException if not
	 * correct
	 */
	public Object stringToValue(String text) throws ParseException {
		if (text.length() == 0)
			return null;
		try {
			_Sdf.parse(text);
		} catch (Exception e) {
			throw new java.text.ParseException(text, 0);
		}
		// but not sufficient, let's check hh and mm individually !
		if (text.length() != _Sdf.toPattern().length())
			throw new java.text.ParseException(text, 0);
		if (_HourMinuteSeparator != null) {
			String[] hourMinute = text.split("" + _HourMinuteSeparator);
			if (hourMinute.length == 2) {
				try {
					String hourStr = hourMinute[0];
					int hour = Integer.parseInt(hourStr);
					if (hour > 23) {
						throw new java.text.ParseException(text, 0);
					}
					String minuteStr = hourMinute[1];
					int minute = Integer.parseInt(minuteStr);
					if (minute > 59) {
						throw new java.text.ParseException(text, 0);
					}
				} catch (NumberFormatException e) {
					throw new java.text.ParseException(text, 0);
				}
			}
		}
		return text;
	}

	public String valueToString(Object value) throws ParseException {
		if (value == null)
			return "";

		return (String) value;
	}

	/**
	 * @return the _Sdf
	 */
	public SimpleDateFormat getSdf() {
		return _Sdf;
	}

}
