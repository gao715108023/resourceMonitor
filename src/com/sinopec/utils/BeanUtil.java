package com.sinopec.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA. User: gaochuanjun Date: 13-10-25 Time: 下午3:07 To
 * change this template use File | Settings | File Templates.
 */
public class BeanUtil {

	/**
	 * 把obj1的相同字段值（经过处理）复制到第二个对应的相同的属性值
	 * 
	 * @param obj1
	 * @param obj2
	 */
	public static void copyProperties(Object obj1, Object obj2) {
		Class classTypeF = obj1.getClass();
		Class classTypeS = obj2.getClass();
		Field fieldsF[] = classTypeF.getDeclaredFields();
		for (Field field : fieldsF) {
			String propertyName = field.getName();

			boolean isHas = true;
			try {
				classTypeS.getDeclaredField(propertyName);
			} catch (NoSuchFieldException e) {
				// e.printStackTrace(); //To change body of catch statement use
				// File | Settings | File Templates.
				isHas = false;
			}
			if (isHas) {
				String prefix = propertyName.substring(0, 1).toUpperCase();
				String getMethodName = "get" + prefix + propertyName.substring(1);
				String setMethodName = "set" + prefix + propertyName.substring(1);

				try {
					Method methodGet = classTypeF.getMethod(getMethodName, new Class[] {});
					Method methodSet = classTypeS.getMethod(setMethodName, new Class[] { field.getType() });

					Object objValue = methodGet.invoke(obj1, new Object[] {});
					// String methodGetValue = objValue == null ? "" :
					// objValue.toString();
					// methodSet.invoke(obj2, new
					// Object[]{methodGetValue.trim()});
					methodSet.invoke(obj2, objValue);
				} catch (NoSuchMethodException e) {
					e.printStackTrace(); // To change body of catch statement
											// use File | Settings | File
											// Templates.
				} catch (InvocationTargetException e) {
					e.printStackTrace(); // To change body of catch statement
											// use File | Settings | File
											// Templates.
				} catch (IllegalAccessException e) {
					e.printStackTrace(); // To change body of catch statement
											// use File | Settings | File
											// Templates.
				}
			}

		}
	}
}
