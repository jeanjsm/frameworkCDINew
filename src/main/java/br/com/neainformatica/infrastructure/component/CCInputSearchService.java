package br.com.neainformatica.infrastructure.component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.ManyToOne;

import org.jfree.util.Log;

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.AndFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.BetweenFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.GreaterEqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.GreaterThanFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.InFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.IsNotNullFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.IsNullFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LessEqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LessThanFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LikeFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.NotFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.OrFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.tools.ToolsReflection;
import net.sf.ehcache.search.expression.Between;

public class CCInputSearchService implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int paramCount;

	@Inject
	private CCInputSearchRepository dao;

	public List<CCInputSearchFilterBean> buildSimpleFilters(Class<?> clazz, String superProperty, String superLabel) {
		return buildSimpleFilters(clazz, superProperty, superLabel, null);
	}

	public List<CCInputSearchFilterBean> buildSimpleFilters(Class<?> clazz, String superProperty, String superLabel, List<String> fieldList) {
		List<CCInputSearchFilterBean> filterBeans = new ArrayList<>();
		List<Field> fields = Arrays.asList(ToolsReflection.getAllFields(clazz));

		for (Field field : fields) {
			// System.out.println(field.getName());

			Filter filterAnotation = field.getAnnotation(Filter.class);

			if (filterAnotation == null)
				continue;

			if (filterAnotation.disabled())
				continue;

			String fieldName = field.getName();

			String fieldAlias = filterAnotation.name();
			if (fieldAlias == null || fieldAlias.trim().equals(""))
				fieldAlias = fieldName;

			ManyToOne manyToOneAnotation = field.getAnnotation(ManyToOne.class);
			
			//(superLabel != null) indica que estamos em uma recursão. Interrompe a recursão no segundo nivel.
			if (manyToOneAnotation != null && superLabel != null)
				continue;

			if (manyToOneAnotation != null) {
				filterBeans.addAll(buildSimpleFilters(field.getType(), fieldName, fieldAlias, fieldList));
				continue;
			}

			EnumFilterMethod filterMethod = filterAnotation.simpleFilterMethod();
			if (filterMethod == null)
				filterMethod = EnumFilterMethod.EQUALS;

			CCInputSearchFilterBean f = new CCInputSearchFilterBean();

			if (superProperty != null) {
				f.setFieldName(superProperty + "." + fieldName);
				f.setFieldAlias(superLabel + "-" + fieldAlias);
			} else {
				f.setFieldName(fieldName);
				f.setFieldAlias(fieldAlias);
			}
			f.setFilterParameter(filterMethod);
			f.setTypeObject(field.getType());

			if (fieldList == null || fieldList.contains(f.getFieldName())) {
				filterBeans.add(f);
			}
		}
		//Se o usuario não passou uma lista de fields, ordena pelo fieldAlias.
		if (fieldList == null)
			filterBeans.stream().sorted((f1, f2) -> f1.getFieldAlias().compareTo(f2.getFieldAlias()));
		else {
			// Se o usuario passou uma lista de fields e todos os filtros já foram criados, ordena pela ordem que o usuário declarou o a lista de fields.
			if (fieldList.size() == filterBeans.size()) {
				List<CCInputSearchFilterBean> listaAuxiliar = new ArrayList<>();
				for (String fieldName : fieldList) {
					CCInputSearchFilterBean filtro = filterBeans.stream().filter(item -> item.getFieldName().equals(fieldName)).findAny().orElse(null);
					listaAuxiliar.add(filtro);
				}
				filterBeans = listaAuxiliar;
			}
		}
		return filterBeans;
	}

	public List<?> pesquisar(Class<?> clazz, String valueFilter, CCInputSearchFilterBean selectedFilter, List<CCInputSearchFilterBean> simpleFilters,
			List<IFilter> beforeFilters, Integer qtdeRegistros) {
		HashMap<String, Object> parametros = new HashMap<>();
		try {
			String hql = montarConsultaHql(clazz, valueFilter, selectedFilter, simpleFilters, beforeFilters, parametros);
			return dao.executarConsulta(hql, parametros, qtdeRegistros);
		} catch (Exception e) {
			Log.debug(e.getMessage());
			return Collections.emptyList();
		}
	}

	private String montarConsultaHql(Class<?> clazz, String valueFilter, CCInputSearchFilterBean selectedFilter, List<CCInputSearchFilterBean> simpleFilters,
			List<IFilter> beforeFilters, HashMap<String, Object> parametros) {
		boolean pesquisarTudo = false;
		valueFilter = valueFilter.trim();
		if (valueFilter.equals("*") || valueFilter.equals(""))
			pesquisarTudo = true;

		StringBuilder sql = new StringBuilder();
		sql.append("select " + clazz.getSimpleName().toLowerCase() + " ");
		sql.append("from " + clazz.getSimpleName() + " " + clazz.getSimpleName().toLowerCase());
		sql.append(montarJoins(clazz, simpleFilters));
		sql.append(" where " + clazz.getSimpleName().toLowerCase() + ".id" + " > 0");
		if (beforeFilters != null && !beforeFilters.isEmpty()) {
			paramCount = 0;
			sql.append(" AND ");
			sql.append(parseBeforeFilter(clazz.getSimpleName().toLowerCase(), beforeFilters, parametros, false));
		}
		if (!pesquisarTudo) {
			Class<?> filterType = selectedFilter.getTypeObject();
			if (filterType.equals(String.class)) {
				sql.append(" AND upper(" + clazz.getSimpleName().toLowerCase() + "." + selectedFilter.getFieldName() + ")");
			} else {
				sql.append(" AND " + clazz.getSimpleName().toLowerCase() + "." + selectedFilter.getFieldName());
			}

			switch (selectedFilter.getFilterParameter()) {
			case DEFAULT:
			case LIKE:
				sql.append(" like ");
				break;
			case EQUALS:
				sql.append(" = ");
				break;
			case DIFERENTE:
				sql.append(" <> ");
				break;
			case GREATER_EQUAL:
				sql.append(" >= ");
				break;
			case GREATER_THAN:
				sql.append(" > ");
				break;
			case LESS_EQUAL:
				sql.append(" <= ");
				break;
			case LESS_THAN:
				sql.append(" < ");
				break;
			}

			if (filterType.equals(String.class)) {
				switch (selectedFilter.getFilterParameter()) {
				case DEFAULT:
				case LIKE:
					sql.append("'%" + valueFilter.toUpperCase() + "%'");
					break;
				default:
				case EQUALS:
					sql.append("'" + valueFilter.toUpperCase() + "'");
					break;
				}
			} else {
				sql.append(valueFilter);
			}
		}
		return sql.toString();
	}

	private String parseBeforeFilter(String alias, List<IFilter> beforeFilters, HashMap<String, Object> parametros, boolean orFilter) {
		StringBuilder builder = new StringBuilder();
		//Operação de concatenação dos filtros, OR ou AND.
		String concatOperation = orFilter ? "OR" : "AND";
		boolean first = true;
		for (IFilter filter : beforeFilters) {
			if(!first)
				builder.append(" " + concatOperation);
			first = false;
			String parameter = "parameter" + paramCount;
			paramCount++;

			switch (filter.getFilterBy()) {
			case AND:
				builder.append(" (" + parseBeforeFilter(alias, ((AndFilter) filter).getFilters(), parametros, false) + ")");
				break;
			case BETWEEN:
				BetweenFilter betweenFilter = (BetweenFilter) filter;
				paramCount++;
				String parameter2 = "parameter" + paramCount;
				builder.append(" " + alias + "." + betweenFilter.getField() + " between :" + parameter + " and :" + parameter2);
				parametros.put(parameter, betweenFilter.getInitialValue());
				parametros.put(parameter2, betweenFilter.getFinalValue());
				break;
			case ENDING:
				break;
			case EQUAL:
				EqualFilter equalFilter = (EqualFilter) filter;
				if (equalFilter.getInitialValue().getClass().equals(String.class)) {
					builder.append(" upper(" + alias + "." + equalFilter.getField() + ") = :" + parameter);
					parametros.put(parameter, ((String) equalFilter.getInitialValue()).toUpperCase());
				} else {
					builder.append(" " + alias + "." + equalFilter.getField() + " = :" + parameter);
					parametros.put(parameter, equalFilter.getInitialValue());
				}
				break;
			case GREATER_EQUAL:
				GreaterEqualFilter greaterEqualFilter = (GreaterEqualFilter) filter;
				builder.append(" " + alias + "." + greaterEqualFilter.getField() + " >= :" + parameter);
				parametros.put(parameter, greaterEqualFilter.getInitialValue());
				break;
			case GREATER_THAN:
				GreaterThanFilter greaterThanFilter = (GreaterThanFilter) filter;
				builder.append(" " + alias + "." + greaterThanFilter.getField() + " > :" + parameter);
				parametros.put(parameter, greaterThanFilter.getInitialValue());
				break;
			case IN:
				break;
			case IS_NOT_NULL:
				IsNotNullFilter isNotNullFilter = (IsNotNullFilter) filter;
				builder.append(" " + alias + "." + isNotNullFilter.getField() + " is not null");
				break;
			case ISNULL:
				IsNullFilter isNullFilter = (IsNullFilter) filter;
				builder.append(" " + alias + "." + isNullFilter.getField() + " is not null");
				break;
			case LESS_EQUALS:
				LessEqualFilter lessEqualFilter = (LessEqualFilter) filter;
				builder.append(" " + alias + "." + lessEqualFilter.getField() + " <= :" + parameter);
				parametros.put(parameter, lessEqualFilter.getInitialValue());
				break;
			case LESS_THAN:
				LessThanFilter lessThanFilter = (LessThanFilter) filter;
				builder.append(" " + alias + "." + lessThanFilter.getField() + " < :" + parameter);
				parametros.put(parameter, lessThanFilter.getInitialValue());
				break;
			case LIKE:
				LikeFilter likeFilter = (LikeFilter) filter;
				builder.append(" upper(" + alias + "." + likeFilter.getField() + ") like :" + parameter);
				parametros.put(parameter, "%" + ((String) likeFilter.getInitialValue()).toUpperCase() + "%");
				break;
			case NOT:
				builder.append(" NOT (" + parseBeforeFilter(alias, ((NotFilter) filter).getFilters(), parametros, false) + ")");
				break;
			case OR:
				builder.append(" (" + parseBeforeFilter(alias, ((OrFilter) filter).getFilters(), parametros, true) + ")");
				break;
			case STARTING:
				break;
			default:
				break;
			}
		}
		return builder.toString();
	}

	public Object pesquisarSingleResult(Class<?> clazz, String codigoValor, String fieldName, List<CCInputSearchFilterBean> simpleFilters,
			List<IFilter> beforeFilters) {
		if (codigoValor != null && !codigoValor.trim().equals("")) {
			HashMap<String, Object> parametros = new HashMap<>();
			String hql = montarConsultaHqlSingleResult(clazz, codigoValor, fieldName, simpleFilters, beforeFilters, parametros);
			List<?> result = dao.executarConsulta(hql, parametros, 100);
			if (result.size() != 1) {
				return null;
			}
			return result.get(0);
		}
		return null;
	}

	private String montarConsultaHqlSingleResult(Class<?> clazz, String valueFilter, String fieldName, List<CCInputSearchFilterBean> simpleFilters,
			List<IFilter> beforeFilters, HashMap<String, Object> parametros) {

		StringBuilder sql = new StringBuilder();
		sql.append("select " + clazz.getSimpleName().toLowerCase() + " ");
		sql.append("from " + clazz.getSimpleName() + " " + clazz.getSimpleName().toLowerCase());
		sql.append(montarJoins(clazz, simpleFilters));
		sql.append(" where " + clazz.getSimpleName().toLowerCase() + ".id > 0");
		if (beforeFilters != null && !beforeFilters.isEmpty()) {
			paramCount = 0;
			sql.append(" AND ");
			sql.append(parseBeforeFilter(clazz.getSimpleName().toLowerCase(), beforeFilters, parametros, false));
		}
		Class<?> filterType = getFieldType(clazz, fieldName);
		if (filterType.equals(String.class)) {
			sql.append(" AND upper(" + clazz.getSimpleName().toLowerCase() + "." + fieldName + ")");
		} else {
			sql.append(" AND " + clazz.getSimpleName().toLowerCase() + "." + fieldName);
		}
		sql.append(" = ");

		if (filterType.equals(String.class)) {
			sql.append("'" + valueFilter.toUpperCase() + "'");
		} else {
			sql.append(valueFilter);
		}
		return sql.toString();
	}

	private static StringBuilder montarJoins(Class<?> obj, List<CCInputSearchFilterBean> filtros) {

		List<String> joins = new ArrayList<>();

		for (CCInputSearchFilterBean f : filtros) {

			if (f.getFieldName().contains(".")) {
				String fieldJoin = f.getFieldName().substring(0, f.getFieldName().indexOf("."));
				String j = " left join fetch " + obj.getSimpleName().toLowerCase() + "." + fieldJoin + " " + fieldJoin + " ";

				if (!joins.contains(j))
					joins.add(j);

			}

		}

		StringBuilder retorno = new StringBuilder(" ");
		for (String string : joins) {
			retorno.append(string + " ");
		}

		return retorno;

	}

	private static Class<?> getFieldType(Class<?> obj, String fieldName) {
		try {
			if (fieldName.contains(".")) {
				String firstFieldString = fieldName.substring(0, fieldName.indexOf("."));
				Field firstField = ToolsReflection.getField(obj, firstFieldString);
				String secondFieldString = fieldName.substring(fieldName.indexOf(".") + 1);
				Field secondField = ToolsReflection.getField(firstField.getType(), secondFieldString);
				return secondField.getType();
			} else {
				return ToolsReflection.getField(obj, fieldName).getType();
			}
		} catch (Exception e) {
			return null;
		}
	}

	// private static StringBuilder montarJoin(Class<?> obj, String fieldName) {
	// List<String> joins = new ArrayList<>();
	// if (fieldName.contains(".")) {
	// String fieldJoin = fieldName.substring(0, fieldName.indexOf("."));
	// String j = " left join fetch " + obj.getSimpleName().toLowerCase() + "."
	// + fieldJoin + " " + fieldJoin + " ";
	//
	// if (!joins.contains(j))
	// joins.add(j);
	//
	// }
	//
	// StringBuilder retorno = new StringBuilder(" ");
	// for (String string : joins) {
	// retorno.append(string + " ");
	// }
	// return retorno;
	// }

}
