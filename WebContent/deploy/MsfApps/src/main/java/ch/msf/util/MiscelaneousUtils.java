package ch.msf.util;

import ch.msf.CommonConstants;


public class MiscelaneousUtils {

	public static String getClassName(Class theClass) {
		String classPathName = theClass.getName();
		String[] classPathNames = classPathName.split("\\.");
		String className = classPathNames[classPathNames.length - 1];
		return className;
	}

	/**
	 * 
	 * @param countryProjectCode
	 * @param careCenterCode
	 * @return a string that can be used as a context id
	 */
	public static  String getContextKey(String countryProjectCode, String careCenterCode) {
		StringBuilder sb = new StringBuilder();

		sb.append(countryProjectCode);
		sb.append(":");
		sb.append(careCenterCode);
		sb.append(CommonConstants.PATIENTID_STRING_SEPARATOR);

		return sb.toString();
	}
	
	
	/**
	 * 
	 * @param contextKey
	 * @return the patient id from a context id
	 */
	@Deprecated  // use getIdFromContext instead
	public static String getPatientId(String contextKey)
	{
		// get rid of context prefix
		String patientId = contextKey;
		int index = contextKey.indexOf(CommonConstants.PATIENTID_STRING_SEPARATOR);
		if (index != -1) {
			patientId = patientId.substring(index + 1);
		}
		return patientId;
	}
	
	
	/**
	 * 
	 * @param contextKey
	 * @return the id from a context id
	 */
	public static String getIdFromContext(String contextKey)
	{
		// get rid of context prefix
		String id = contextKey;
		int index = contextKey.indexOf(CommonConstants.PATIENTID_STRING_SEPARATOR);
		if (index != -1) {
			id = id.substring(index + 1);
		}
		return id;
	}
	
	
	/**
	 * remove cascaded carriage return caracters
	 * @param inputString
	 * @return
	 */
	public static String replaceDuplicateReturn(String inputString)
	{

		return inputString.replaceAll("[\n]+", "\n");
	}


}
