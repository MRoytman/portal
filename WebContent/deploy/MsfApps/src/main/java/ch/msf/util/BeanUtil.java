package ch.msf.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

public class BeanUtil {

	public static <S, T> void copyFields(S source, T target) throws Exception {

		BeanInfo beanInfoSource = Introspector.getBeanInfo(source.getClass());
		BeanInfo beanInfoDest = Introspector.getBeanInfo(target.getClass());
		for (PropertyDescriptor propertyDescSource : beanInfoSource.getPropertyDescriptors()) {
			if (propertyDescSource.getPropertyType().equals(java.util.Collection.class))
				continue;
			if (propertyDescSource.getPropertyType().equals(java.util.Map.class))
				continue;
			String propertyNameSource = propertyDescSource.getName();
			if (propertyDescSource.getReadMethod() == null)
				continue;

			if (!propertyDescSource.getReadMethod().getName().startsWith("get"))
				continue;

			if (propertyNameSource.equals("class"))
				continue;
			for (PropertyDescriptor propertyDestDest : beanInfoDest.getPropertyDescriptors()) {
				// propertyDestDest.
				String propertyNameDest = propertyDestDest.getName();
				if (propertyNameSource.equals(propertyNameDest)) {
					Object value = propertyDescSource.getReadMethod().invoke(source);
					try {
						propertyDestDest.getWriteMethod().invoke(target, value); // java.lang.IllegalArgumentException:
																					// argument
																					// type
																					// mismatch
					} catch (IllegalArgumentException e) {
						// tolerated as it happen frequently in this application
					} catch (NullPointerException e) {
						// tolerated as it happen frequently in this application
					} catch (Exception e) {
						//
						e.printStackTrace();
					}
					break;
				}
			}

		}

		// Class<?> clazz = source.getClass();
		// clazz.getDeclaredMethods();
		// clazz.getDeclaredFields();
		// for (Field field : clazz.getFields()) {
		// Object value = field.get(source);
		// field.set(target, value);
		// }
	}

}
