package ch.msf.util;

import java.lang.reflect.Method;

/**
 * @author cmiville
 * 
 */
public class Reflex {

	public static Class<? extends Object> getClassInstance(String className) {

		try {
			// get class representation
			Class<? extends Object> cls = Class.forName(className);
			return cls;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoClassDefFoundError e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Method getPrivateMethode(String className, String methodName) {
		Method method = null;
		try {
			// get class representation
			Class<? extends Object> cls = Class.forName(className);

			// check if object is instance of IErrorMessage
			// and invoke toJSON or toString method
			method = cls.getDeclaredMethod(methodName);

			// enable private method access
			method.setAccessible(true);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return method;
	}
}
