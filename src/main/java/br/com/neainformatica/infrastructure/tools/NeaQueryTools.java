package br.com.neainformatica.infrastructure.tools;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

public class NeaQueryTools {

	/**
	 * Este Método deve ser utilizado para gerar uma SQL de consulta nativa Ele
	 * coloca todos os fields mapeados no select
	 * 
	 * @param cl
	 * @param schema
	 * @param alias
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String montarSqlQueryNativa(Class cl, String schema, String alias) {

		if (schema != null && !schema.trim().equals(""))
			schema = schema.trim() + ".";

		String retorno = "select " + buscarListaCamposFormatada(cl, alias) + " from " + schema + bucarNomeTabela(cl) + " " + alias;

		return retorno;

	}

	@SuppressWarnings("rawtypes")
	private static String bucarNomeTabela(Class cl) {

		String nomeTabela = null;

		Annotation[] annotations = cl.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation instanceof Table) {
				if (((Table) annotation).name() != null && !((Table) annotation).name().trim().equals(""))
					nomeTabela = ((Table) annotation).name().trim();
			}
		}
		annotations = null;
		if (nomeTabela == null)
			nomeTabela = cl.getSimpleName().toLowerCase();

		return nomeTabela;

	}

	@SuppressWarnings("rawtypes")
	private static String buscarListaCamposFormatada(Class cl, String alias) {

		List<String> campos = buscarListCampos(cl);

		StringBuilder retorno = new StringBuilder();

		for (String string : campos) {

			retorno.append(alias + "." + string + ", ");
		}

		return retorno.toString().substring(0, retorno.toString().length() - 2);

	}

	@SuppressWarnings("rawtypes")
	private static List<String> buscarListCampos(Class cl) {

		List<String> retorno = new ArrayList<>();

		Field[] fields = cl.getDeclaredFields();

		for (int i = 0; i < fields.length; i++) {

			if (java.lang.reflect.Modifier.isStatic(fields[i].getModifiers()))
				continue;

			Annotation[] annotations = fields[i].getAnnotations();

			String nomeField = null;

			for (Annotation annotation : annotations) {
				if (annotation instanceof Column) {
					if (((Column) annotation).name() != null && !((Column) annotation).name().trim().equals(""))
						nomeField = ((Column) annotation).name().trim();
				}
			}

			if (nomeField == null)
				nomeField = fields[i].getName();

			retorno.add(nomeField.toLowerCase());

		}

		return retorno;

	}

}
