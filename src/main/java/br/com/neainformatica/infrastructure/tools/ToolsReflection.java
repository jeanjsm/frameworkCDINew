package br.com.neainformatica.infrastructure.tools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("rawtypes")
public class ToolsReflection {

	public static Class getTClass(Object object) {
		return getTClass(object.getClass());
	}

	public static Class getTClass(Class clazz) {
		try {
			Type[] types = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
			return (Class) types[0];
		} catch (Exception e) {
			return null;
		}
	}

	public static Object getTNewInstance(Class clazz) {
		try {
			return getNewInstance(getTClass(clazz));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getTNewInstance(Object object) {
		try {
			return getNewInstance(getTClass(object));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Object getNewInstance(Class clazz) {
		try {
			return clazz.newInstance();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Field[] getAllFields(Object object) {
		return getAllFields(object.getClass());
	}

	public static Field[] getAllFields(Class clazz) {
		Field[] fields = clazz.getDeclaredFields();
		return fields;
	}

	public static Map<String, Method> getAllMethods(Object bean) {
		Map<String, Method> methods = new HashMap<String, Method>();
		for (Class<?> atual = bean.getClass(); !atual.equals(Object.class); atual = atual.getSuperclass()) {
			for (Method m : atual.getMethods()) {
				methods.put(m.getName(), m);
			}
		}
		return methods;
	}

	public static Field getField(Object object, String fieldName) throws SecurityException, NoSuchFieldException {
		return getField(object.getClass(), fieldName);
	}

	public static Field getField(Class clazz, String fieldName) throws SecurityException, NoSuchFieldException {
		return clazz.getDeclaredField(fieldName);
	}

	public static Object getFieldValue(Object object, String fieldName) throws SecurityException, NoSuchFieldException, IllegalArgumentException,
			IllegalAccessException {
		Field field = getField(object.getClass(), fieldName);

		Boolean accessible = field.isAccessible();

		field.setAccessible(true);
		Object result = field.get(object);
		field.setAccessible(accessible);

		return result;
	}

	public static Integer getIdByReflection(Object bean) {
		try {
			return (Integer) getFieldValue(bean, "id");
		} catch (Exception ex) {
			throw new RuntimeException("Não foi possível obter a propriedade'id' do item", ex);
		}
	}

	public static Object getObjectIdByReflection(Object bean) {
		try {
			// Marcos Bispo, 22/01/2014: alterado para pegar o valor a partir do
			// método direto
			String nameMethod = getPrimaryKeyMethod(bean);
			Method method = bean.getClass().getMethod(nameMethod);
			Object value = method.invoke(bean);
			return value;
			// return getFieldValue(bean, "id");
		} catch (Exception ex) {
			throw new RuntimeException("Não foi possível obter o valor da propriedade'id' do item", ex);
		}
	}

	public static String getPrimaryKeyMethod(Object object) {
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (field.getDeclaredAnnotation(Id.class) != null || field.getDeclaredAnnotation(EmbeddedId.class) != null)
				return "get" + StringUtils.capitalize(field.getName());
		}
		//Leandro de Bortoli, 10/05/2017: alterado para procurar o metodo do primary key nas superclasses.
		Class superClass = object.getClass();
		while((superClass = superClass.getSuperclass()) != null) {
			fields = superClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.getDeclaredAnnotation(Id.class) != null || field.getDeclaredAnnotation(EmbeddedId.class) != null)
					return "get" + StringUtils.capitalize(field.getName());
			}
		}
		return "";
	}

	// public static Method findGetter(Object bean, String propertyName) {
	// Class<? extends Object> beanClass = bean.getClass();
	// return findGetter(beanClass, propertyName);
	// }

	public static Object copyProperties(Object toObject, Object fromObject) {

		BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
		beanUtilsBean.getConvertUtils().register(new org.apache.commons.beanutils.converters.DateConverter(null), Date.class);
		beanUtilsBean.getConvertUtils().register(new org.apache.commons.beanutils.converters.SqlTimestampConverter(null), Timestamp.class);

		try {

			beanUtilsBean.copyProperties(toObject, fromObject);

		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		return toObject;
	}

}
