package ch.msf.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

// see also http://www.odi.ch/prog/design/datetime.php

public class DateUtil {

	public static int getAge(Date dateOfBirth) {
		if (dateOfBirth == null)
			return -1;
		Calendar now = Calendar.getInstance();
		Calendar dob = Calendar.getInstance();

		dob.setTime(dateOfBirth);

		if (dob.after(now)) {
			throw new IllegalArgumentException("Can't be born in the future");
		}

		int age = now.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

		if (now.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
			age--;
		}

		return age;
	}

	/**
	 * Calculates midnight of the day in which date lies with respect to a time
	 * zone.
	 **/
	public static Date midnight(Date date /* , TimeZone tz */) {
		// Calendar cal = new GregorianCalendar(tz);

		// Constructs a default GregorianCalendar using the current time in the
		// default time zone with the default locale.
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * Adds a number of days to a date. DST change dates are handled according
	 * to the time zone. That's necessary as these days don't have 24 hours.
	 */
	public static Date addDays(Date date, int days /* , TimeZone tz */) {
		
		if (days == 0)
			return date;
		// Calendar cal = new GregorianCalendar(tz);

		// Constructs a default GregorianCalendar using the current time in the
		// default time zone with the default locale.
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	
	/**
	 * !! SimpleDateFormat is not thread safe !!
	 */
	public class ThreadSafeSimpleDateFormat {

		private DateFormat df;

		public ThreadSafeSimpleDateFormat(String format) {
			this.df = new SimpleDateFormat(format);
		}

		public synchronized String format(Date date) {
			return df.format(date);
		}

		public synchronized Date parse(String string) throws ParseException {
			return df.parse(string);
		}
	}

}
