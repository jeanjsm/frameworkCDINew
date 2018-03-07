package br.com.neainformatica.infrastructure.tools;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LazyInitializationException;
import org.hibernate.Session;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.entity.NaAuditoria;
import br.com.neainformatica.infrastructure.entity.NaAuditoriaItem;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumAuditoriaOperacao;
import br.com.neainformatica.infrastructure.enumeration.EnumFormatDate;

//import org.picketlink.Identity;
//import org.picketlink.idm.model.basic.User;

public class Auditoria implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(Auditoria.class);

	@Inject
	private EntityManager entityManager;

	// @Inject
	// private Identity identity;

	@Inject
	private InfrastructureController infrastructureController;

	@Inject
	private NaSessionController naSessionController;

	@SuppressWarnings("unused")
	private Session session;

	public Session getSession() {
		return (Session) entityManager.getDelegate();
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public NaAuditoria save(NaAuditoria entity) {
		entity = (NaAuditoria) getSession().merge(entity);
		getSession().flush();
		return entity;
	}

	public NaAuditoriaItem save(NaAuditoriaItem entity) {
		entity = (NaAuditoriaItem) getSession().merge(entity);
		getSession().flush();
		return entity;
	}

	public String obterUsuarioAtual() {

		try {
			if (naSessionController == null || naSessionController.getNaUsuario() == null) {
				naoExisteSessaoAtivaLog();
				return "SISTEMA";
			}
		} catch (ContextNotActiveException e) {
			naoExisteSessaoAtivaLog();
			return "SISTEMA";
		}

		return naSessionController.getNaUsuario().getNomeUsuarioAuditoria();

	}

	public NaCliente obterClienteAtual() {

		try {
			if (naSessionController == null || naSessionController.getNaUsuario() == null) {
				naoExisteSessaoAtivaLog();
				return null;
			}
		} catch (ContextNotActiveException e) {
			naoExisteSessaoAtivaLog();
			return null;
		}

		return naSessionController.getNaCliente();

	}

	private void naoExisteSessaoAtivaLog() {

		log.error("----------------------------------------------------------------------------------------------------");
		log.error("Sr(a) Desenvolvedor(a), Usuario de auditoria não identificado nesta transação, favor verificar URGENTE.");
		log.error("      Sistema: " + InfrastructureController.getNeaInfrastructureSistemaNome());

		// mostrando apenas os ultimos métodos do stackTrace
		Throwable throwable = new Throwable();
		throwable.fillInStackTrace();
		StackTraceElement[] stackTraceElements = throwable.getStackTrace();

		int max = 12;
		if (stackTraceElements == null || stackTraceElements.length == 0)
			max = 0;
		if (max == 12 && stackTraceElements.length < 12)
			max = stackTraceElements.length;

		for (int i = 0; i < max; i++) {
			String methodName = stackTraceElements[i].toString();
			log.error("   " + methodName);
		}

		log.error("----------------------------------------------------------------------------------------------------");
	}

	/*
	 * Marcos Bispo, 22/01/2014: Gera auditoria usando usuário atual retornado pelo
	 * método obterUsuarioAtual, que usa o identity do picketlink
	 */
	public void geraAuditoria(Object velho, Object novo) {
		geraAuditoria(velho, novo, obterUsuarioAtual(), infrastructureController.getNaSistema(), obterClienteAtual());
	}

	public void geraAuditoria(Object velho, Object novo, String usuario) {
		geraAuditoria(velho, novo, usuario, infrastructureController.getNaSistema(), obterClienteAtual());
	}

	public static String gerarDados(Object obj, Field fieldAudit) {
		if (obj == null)
			return null;

		StringBuilder retorno = new StringBuilder();

		try {
			String value = formataValue(fieldAudit, obj);
			retorno.append(value);
		} catch (IllegalAccessException | LazyInitializationException e) {

			e.printStackTrace();
		}

		// Field[] field = FieldUtils.getAllFields(obj.getClass());
		//
		// for (Field f : field) {
		// try {
		// if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
		// continue;
		//
		// String value;
		//
		// value = formataValue(f, obj);
		//
		// if (value != null && !value.trim().equals(""))
		// retorno.append(f.getName() + ": " + value + " |");
		//
		// } catch (IllegalAccessException e) {
		// e.printStackTrace();
		// }
		// }

		return retorno.toString();

	}

	private static String formataValue(Field field, Object obj) throws IllegalAccessException {

		Object valueField = FieldUtils.readField(obj, field.getName(), true);

		if (valueField == null)
			return "";

		if (field.getType().getSimpleName().equalsIgnoreCase("Integer"))
			return ((Integer) valueField).toString();
		if (field.getType().getSimpleName().equalsIgnoreCase("String")) {
			// Se campo String com mais de 1000 caracteres, corta na auditoria
			String value = (String) valueField;
			if (value.length() > 1000) {
				return value.substring(0, 999);
			} else {
				return value;
			}

		}
		if (field.getType().getSimpleName().equalsIgnoreCase("Date"))
			return NeaDate.formatarData((Date) valueField, EnumFormatDate.DATE_TIME_HMS);
		if (field.getType().getSimpleName().equalsIgnoreCase("List"))
			return "";

		else {
			Method method;
			try {
				try {
					valueField.toString();
				} catch (LazyInitializationException e) {
					return "";
				}
				method = valueField.getClass().getMethod("toAuditoria", new Class[] {});
				Object invoke = method.invoke(valueField, new Object[] {});
				return invoke.toString();
			} catch (NoSuchMethodException | SecurityException e1) {
				return valueField.toString();
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return "";
		}

	}

	public void geraAuditoria(Object velho, Object novo, String usuario, NaSistema sistema, NaCliente naCliente) {
		Map<String, Object[]> changeds = new HashMap<String, Object[]>();

		String nomeUsuario = usuario;
		EnumAuditoriaOperacao auditoriaOperacao;

		if (velho == null) {
			auditoriaOperacao = EnumAuditoriaOperacao.INCLUSAO;
		} else if (novo == null) {
			auditoriaOperacao = EnumAuditoriaOperacao.EXCLUSAO;
		} else {
			auditoriaOperacao = EnumAuditoriaOperacao.ALTERACAO;
		}

		try {

			Class<?> clazz = null;
			if (auditoriaOperacao == EnumAuditoriaOperacao.EXCLUSAO) {
				clazz = velho.getClass();
			} else {
				clazz = novo.getClass();
			}

			Method[] methods = clazz.getDeclaredMethods();

			// Field[] fields = clazz.getDeclaredFields();
			String fieldName;

			Field[] fields = FieldUtils.getAllFields(clazz);

			for (int i = 0; i < fields.length; i++) {

				String oldFieldValue = null;
				String newFieldValue = null;

				Field field = fields[i];

				if (java.lang.reflect.Modifier.isStatic(fields[i].getModifiers()))
					continue;

				if (field.getType().getSimpleName().equalsIgnoreCase("List"))
					continue;

				if (field.getType().getSimpleName().equalsIgnoreCase("byte[]"))
					continue;

				if (field.getType().getSimpleName().equalsIgnoreCase("Integer") || field.getType().getSimpleName().equalsIgnoreCase("String")
						|| field.getType().getSimpleName().equalsIgnoreCase("int") || field.getType().getSimpleName().equalsIgnoreCase("long")) {

					Object obj = velho != null ? FieldUtils.readField(velho, field.getName(), true) : null;
					if (obj != null)
						oldFieldValue = obj.toString();

					obj = novo != null ? FieldUtils.readField(novo, field.getName(), true) : null;
					if (obj != null)
						newFieldValue = obj.toString();

					if (field.getType().getSimpleName().equalsIgnoreCase("String")) {
						if (oldFieldValue != null && oldFieldValue.length() > 1000) {
							oldFieldValue = oldFieldValue.substring(0, 999);
						}
						if (newFieldValue != null && newFieldValue.length() > 1000) {
							newFieldValue = newFieldValue.substring(0, 999);
						}
					}

				} else if (field.getType().getSimpleName().equalsIgnoreCase("Date")) {

					Object obj = velho != null ? FieldUtils.readField(velho, field.getName(), true) : null;
					if (obj != null)
						oldFieldValue = obj.toString();

					obj = novo != null ? FieldUtils.readField(novo, field.getName(), true) : null;
					if (obj != null)
						newFieldValue = obj.toString();

				} else if (field.getType().getSimpleName().equalsIgnoreCase("Date")) {

					Date dateTmp = velho != null ? (Date) FieldUtils.readField(velho, field.getName(), true) : null;
					if (dateTmp != null)
						oldFieldValue = NeaDate.formatarData(dateTmp, EnumFormatDate.DATE_TIME_HMS);

					dateTmp = novo != null ? (Date) FieldUtils.readField(novo, field.getName(), true) : null;
					if (dateTmp != null)
						newFieldValue = NeaDate.formatarData(dateTmp, EnumFormatDate.DATE_TIME_HMS);
				} else if (field.getType().getSimpleName().equalsIgnoreCase("BigDecimal") || field.getType().getSimpleName().equalsIgnoreCase("Double")) {

					Object oldValue = velho != null ? (Object) FieldUtils.readField(velho, field.getName(), true) : null;

					oldFieldValue = oldValue != null ? oldValue.toString() : null;

					Object newvalue = novo != null ? (Object) FieldUtils.readField(novo, field.getName(), true) : null;
					newFieldValue = newvalue != null ? newvalue.toString() : null;
				} else if (field.getType().isEnum()) {
					Object obj = velho != null ? FieldUtils.readField(velho, field.getName(), true) : null;
					if (obj != null)
						oldFieldValue = obj.toString();

					obj = novo != null ? FieldUtils.readField(novo, field.getName(), true) : null;
					if (obj != null)
						newFieldValue = obj.toString();
				} else {
					oldFieldValue = gerarDados(velho, field);
					newFieldValue = gerarDados(novo, field);
				}

				if (auditoriaOperacao == EnumAuditoriaOperacao.ALTERACAO) {
					if (oldFieldValue != null && newFieldValue != null) {
						if (!oldFieldValue.equals(newFieldValue)) {
							changeds.put(field.getName(), new Object[] { oldFieldValue, newFieldValue });
						}
					}
				} else {
					changeds.put(field.getName(), new Object[] { oldFieldValue, newFieldValue });
				}

			}

			// for (int i = 0; i < methods.length; i++) {
			// String methodName = methods[i].getName();
			//
			// if (methods[i].getDeclaringClass().isAssignableFrom(clazz)) {
			//
			// if (methodName.startsWith("get")) {
			// // Method method = clazz.getMethod(methodName);
			//
			// Object oldFieldValue = null;
			// if (auditoriaOperacao != EnumAuditoriaOperacao.INCLUSAO) {
			// try {
			//
			// oldFieldValue =
			// velho.getClass().getMethod(methodName).invoke(velho);
			// } catch (java.lang.NoSuchMethodException ns) {
			// oldFieldValue = "N/D";
			// }
			// }
			//
			// Object newFieldValue = null;
			// if (auditoriaOperacao != EnumAuditoriaOperacao.EXCLUSAO) {
			// try {
			// newFieldValue =
			// novo.getClass().getMethod(methodName).invoke(novo);
			// } catch (java.lang.NoSuchMethodException ns) {
			// newFieldValue = "N/D";
			// }
			// }
			//
			// fieldName = methodName.substring(3);
			// fieldName = getFirstLower(fieldName);
			//
			// if (auditoriaOperacao == EnumAuditoriaOperacao.ALTERACAO) {
			// if (oldFieldValue != null && newFieldValue != null) {
			// if (!oldFieldValue.equals(newFieldValue)) {
			// changeds.put(fieldName, new Object[] { oldFieldValue,
			// newFieldValue });
			// }
			// }
			// } else {
			// changeds.put(fieldName, new Object[] { oldFieldValue,
			// newFieldValue });
			// }
			// }
			// }
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}

		NaAuditoria auditoria = null;

		if (auditoriaOperacao != EnumAuditoriaOperacao.EXCLUSAO) {
			auditoria = new NaAuditoria(new Date(), nomeUsuario, novo.getClass().getSimpleName(), ToolsReflection.getObjectIdByReflection(novo).toString(),
					auditoriaOperacao, 0, sistema, naCliente);
		} else {
			auditoria = new NaAuditoria(new Date(), nomeUsuario, velho.getClass().getSimpleName(), ToolsReflection.getObjectIdByReflection(velho).toString(),
					auditoriaOperacao, 0, sistema, naCliente);
		}

		auditoria = save(auditoria);

		for (Iterator<String> iter = changeds.keySet().iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Object[] values = (Object[]) changeds.get(key);

			// log.debug("Auditoria.geraAuditoria: propriedade = " + key +
			// ", velho: " + values[0] + ", novo: " + values[1]);

			NaAuditoriaItem auditoriaItem = null;

			if (auditoriaOperacao == EnumAuditoriaOperacao.INCLUSAO) {
				if (values[1] != null) {
					auditoriaItem = new NaAuditoriaItem(auditoria, key, "", values[1].toString());
				} else {
					auditoriaItem = new NaAuditoriaItem(auditoria, key, "", "");
				}

			} else if (auditoriaOperacao == EnumAuditoriaOperacao.EXCLUSAO) {

				if (values[0] != null) {
					auditoriaItem = new NaAuditoriaItem(auditoria, key, values[0].toString(), "");
				} else {
					auditoriaItem = new NaAuditoriaItem(auditoria, key, "", "");
				}

			} else {

				if (values[0] != null && values[1] != null) {
					auditoriaItem = new NaAuditoriaItem(auditoria, key, values[0].toString(), values[1].toString());

				}

			}

			if (auditoriaItem != null) {
				save(auditoriaItem);
			}

		}
	}

	public String getFirstUpper(String s) {
		return s.substring(0, 1).toUpperCase().concat(s.substring(1));
	}

	public String getFirstLower(String s) {
		return s.substring(0, 1).toLowerCase().concat(s.substring(1));
	}

	public NaSessionController getNaSessionController() {
		return naSessionController;
	}

}
