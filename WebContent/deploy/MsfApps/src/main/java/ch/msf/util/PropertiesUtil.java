package ch.msf.util;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import ch.msf.error.ConfigException;

public class PropertiesUtil {

	public static Boolean getConfigFieldBoolean(String key,
			Properties properties) throws ConfigException {
		String token = getConfigField(key, properties);
		if (token == null)
			return null;
		try {
			return Boolean.parseBoolean(token);
		} catch (Exception e) {
			throw new ConfigException(e);
		}
	}

	/**
	 * 
	 * @param key
	 * @param properties
	 * @return the property referenced by key from the passed properties
	 */
	public static String getConfigField(String key, Properties properties) {
		String property = properties.getProperty(key);
		if (property == null)
			System.out
					.println("PropertiesUtil::getConfigField: NO VALUE DEFINED FOR "
							+ key);

		return property;
	}

	/**
	 * 
	 * @param keyPattern
	 * @return a list of string key separator values that match keyPattern
	 */
	public static ArrayList<String> getConfigFields(Properties properties,
			String keyPattern, String strSeparator) {
		ArrayList<String> list = new ArrayList<String>();

		for (Enumeration<Object> e = properties.keys(); e.hasMoreElements();) {
			String key, value;
			try {
				key = (String) e.nextElement();
				if (key.startsWith(keyPattern)) {
					value = properties.getProperty(key);
					list.add(key + strSeparator + value);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

		return list;
	}

}
