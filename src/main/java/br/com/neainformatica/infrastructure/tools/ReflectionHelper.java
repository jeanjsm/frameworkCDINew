package br.com.neainformatica.infrastructure.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/*
 * http://code.google.com/p/wryel/source/browse/springmvc-tiles-jpa/springmvc-tiles-jpa-core/src/main/java/br/com/wryel/helper/ReflectionHelper.java
 * */

public class ReflectionHelper {

	public static List<Method> getMethodsStartedWith(Object obj, String start) {
		List<Method> methods = new ArrayList<Method>();
		for (Class<?> klass = obj.getClass(); !klass.equals(Object.class); klass = klass.getSuperclass()) {
			for (Method method : klass.getMethods()) {
				if (method.getName().startsWith(start)) {
					methods.add(method);
				}
			}
		}
		return methods;
	}

	public static Map<Field, Method> getGetersByField(Object obj) throws Exception {
		Map<Field, Method> geters = new HashMap<Field, Method>();
		for (Class<?> klass = obj.getClass(); !klass.equals(Object.class); klass = klass.getSuperclass()) {
			for (Field field : klass.getDeclaredFields()) {
				try {
					Method method = klass.getMethod("get" + StringUtils.capitalize(field.getName()));
					geters.put(field, method);
				} catch (NoSuchMethodException e) {

				} catch (Exception e) {
					throw new Exception(e);
				}
			}

		}
		return geters;
	}

	public static Map<Field, Method> getSetersByField(Object obj) throws Exception {
		Map<Field, Method> geters = new HashMap<Field, Method>();
		for (Class<?> klass = obj.getClass(); !klass.equals(Object.class); klass = klass.getSuperclass()) {
			for (Field field : klass.getDeclaredFields()) {
				try {
					Method method = klass.getMethod("set" + StringUtils.capitalize(field.getName()), field.getType());
					geters.put(field, method);
				} catch (NoSuchMethodException e) {

				} catch (Exception e) {
					throw new Exception(e);
				}
			}
		}
		return geters;
	}

	public static Object getObjectProprety(Object bean, String propertyName) throws Exception {
		try {
			Method method = findGetter(bean, propertyName);
			return method != null ? method.invoke(bean) : null;
		} catch (Exception e) {
			throw new Exception("Não achou a propriedade: " + propertyName, e);
		}
	}

	public static Method findGetter(Object bean, String propertyName) {
		Class<? extends Object> beanClass = bean.getClass();
		return findGetter(beanClass, propertyName);
	}

	public static Method findGetter(Class<? extends Object> beanClass, String propertyName) {
		Method method = null;
		String capitalized = StringUtils.capitalize(propertyName);
		for (Class<?> current = beanClass; !current.equals(Object.class) && method == null; current = current.getSuperclass()) {
			try {
				method = current.getMethod("get" + capitalized);
			} catch (Exception e) {

			}
		}
		return method;
	}

	public static Method findSetter(Object bean, String propertyName) {
		Class<? extends Object> beanClass = bean.getClass();
		return findSetter(beanClass, propertyName);
	}

	public static Method findSetter(Class<? extends Object> beanClass, String propertyName) {
		Method method = null;
		String capitalized = StringUtils.capitalize(propertyName);
		for (Class<?> current = beanClass; !current.equals(Object.class) && method == null; current = current.getSuperclass()) {
			try {
				method = current.getMethod("set" + capitalized);
			} catch (Exception e) {

			}
		}
		return method;
	}

	public static Map<String, Method> getAllSetters(Object bean) {
		Map<String, Method> methods = new HashMap<String, Method>();
		for (Class<?> atual = bean.getClass(); !atual.equals(Object.class); atual = atual.getSuperclass()) {
			for (Method method : atual.getMethods()) {
				if (method.getName().startsWith("set")) {
					methods.put(method.getName(), method);
				}
			}
		}
		return methods;
	}

	public static Map<String, Method> getAllGetters(Object bean) {
		Map<String, Method> methods = new HashMap<String, Method>();
		for (Class<?> atual = bean.getClass(); !atual.equals(Object.class); atual = atual.getSuperclass()) {
			for (Method method : atual.getMethods()) {
				if (method.getName().startsWith("get")) {
					methods.put(method.getName(), method);
				}
			}
		}
		return methods;
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

	public static Field getField(Object bean, String name) throws Exception {
		Field field = null;
		for (Class<?> klass = bean.getClass(); !klass.equals(Object.class) && field == null; klass = klass.getSuperclass()) {
			try {
				field = klass.getDeclaredField(name);
			} catch (SecurityException e) {
				throw new Exception("Nao foi possivel acessar o campo escolhido.", e);
			} catch (NoSuchFieldException e) {

			}
		}
		return field;
	}

	public static List<Field> getField(Object bean, Class<? extends Annotation> annotation) throws Exception {
		List<Field> fields = new ArrayList<Field>();
		for (Class<?> klass = bean.getClass(); !klass.equals(Object.class); klass = klass.getSuperclass()) {
			try {
				for (Field current : klass.getDeclaredFields()) {
					if (current.getAnnotation(annotation) != null) {
						fields.add(current);
					}
				}
			} catch (SecurityException e) {
				throw new Exception("Nao foi possivel acessar o campo escolhido.", e);
			}
		}
		return fields;
	}

	public static Map<String, Field> getAllFields(Object bean) {
		Field[] fields;
		Map<String, Field> mappedFields = new HashMap<String, Field>();
		for (Class<?> klass = bean.getClass(); !klass.equals(Object.class); klass = klass.getSuperclass()) {
			fields = klass.getDeclaredFields();
			for (Field field : fields) {
				mappedFields.put(field.getName(), field);
			}
		}
		return mappedFields;
	}
}