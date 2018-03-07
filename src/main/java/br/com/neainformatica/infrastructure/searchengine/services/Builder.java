package br.com.neainformatica.infrastructure.searchengine.services;

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.bean.FilterBean;
import br.com.neainformatica.infrastructure.searchengine.converter.Converter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.*;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.tools.ToolsReflection;

import javax.persistence.MappedSuperclass;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe utilitária usada para construção e manipulação de filtros
 * 
 * @author Pedro sanchez
 * 
 * */
public class Builder {

	/**
	 * Método que cria filterBeans de uma entidade
	 * 
	 * <br/>
	 * <br/>
	 * <em><strong>Este Método é Recursivo</strong></em>
	 * 
	 * @param clazz classe da entidade de uma das composições da entidade utilizada
	 * @param superProperty string com nome dos joins necessarios para chegar nesta entidade
	 * 
	 * @return lista de filterBean
	 * 
	 * */
	public static List<FilterBean> buildSimpleFilters(Class<?> clazz, String superProperty){

		List<FilterBean> filterBeans = new ArrayList<>();
		
		List<Field> fields = Arrays.asList(ToolsReflection.getAllFields(clazz));		

		// TODO MODIFICAR PARA CLASSE COMPOSTA E CLASSE HERDADA E LISTA
		// TODO melhorar a verificação do @Filter nos fields de tipo composto ou lista
		for (Field field : fields) {
			
			Filter simplefilter = field.getAnnotation(Filter.class);
			
			if (simplefilter != null) {
			
				// verifica se na anotação @Filter foi setado o filterProperty
				String fieldName = (simplefilter.filterProperty() != null && !simplefilter.filterProperty().equals("")) ? simplefilter.filterProperty() : field.getName();
				String alias = (simplefilter.filterProperty() != null && !simplefilter.filterProperty().equals("")) ? field.getName() : "";
				
				if (fieldName.equals("*")) {
					// se filterProperty = "*", pega todos os fields do field atual (1 nível apenas), cria os filterbeans com alias para filtro com join
					List<Field> childFields = Arrays.asList(ToolsReflection.getAllFields(field.getType()));
					
					for (Field childField : childFields) {
						
						Filter childSimplefilter = childField.getAnnotation(Filter.class);
						
						// ignora os fields com @Filter.filterProperty != "", por conta dos joins e aliases, por enquanto
						if ((childSimplefilter != null)&&(childSimplefilter.filterProperty()==null||childSimplefilter.filterProperty().equals(""))) {
						
							FilterBean filterBean = Converter.convertField(childField, superProperty, alias+"."+childField.getName(), alias, simplefilter.name()+".");
							
							if(filterBean != null){
								filterBeans.add(filterBean);
							}	
						}
					}
					
					
				} else {
					// se filterProperty != "" então cria o filterbean com o alias e o filtro = filterproperty, senão cria filterbean normal
					FilterBean filterBean = Converter.convertField(field, superProperty, fieldName, alias, "");
					
					if(filterBean != null){
						filterBeans.add(filterBean);
					}
				}
			}
		}

		Class<?> superClass = clazz.getSuperclass();
		if(superClass.getAnnotation(MappedSuperclass.class) != null) {
			filterBeans.addAll(buildSimpleFilters(superClass, superProperty));
		}
		
		return filterBeans;
	}
	
	


	/**
	 * Método que cria filterBeans de uma entidade
	 * 
	 * <br/>
	 * <br/>
	 * <em><strong>Este Método é Recursivo</strong></em>
	 * 
	 * @param clazz classe da entidade de uma das composições da entidade utilizada
	 * @param superProperty string com nome dos joins necessarios para chegar nesta entidade
	 * 
	 * @return lista de filterBean
	 * 
	 * */
	public static List<FilterBean> buildAdvancedFilters(Class<?> clazz, String superProperty){

		List<FilterBean> filterBeans = new ArrayList<>();
		
		List<Field> fields = Arrays.asList(ToolsReflection.getAllFields(clazz));		

		// TODO MODIFICAR PARA CLASSE COMPOSTA E CLASSE HERDADA E LISTA
		// TODO melhorar a verificação do @Filter nos fields de tipo composto ou lista
		for (Field field : fields) {

			Filter simplefilter = field.getAnnotation(Filter.class);

			/*
			 * Adicionado esta validação pois quando a classe tiver atributos
			 * que não estão anotados com @Filter acontece erro nesta rotina
			 */
			if (simplefilter != null) {

				String fieldName = "";

				fieldName = superProperty;

				if (!fieldName.trim().isEmpty()) {
					fieldName += ".";
				}

				//fieldName += field.getName();
				
				fieldName += (simplefilter.filterProperty()!=null&&!simplefilter.filterProperty().equals(""))?simplefilter.filterProperty():field.getName();
				String alias = (simplefilter.filterProperty()!=null&&!simplefilter.filterProperty().equals(""))?field.getName():"";
				
				if (fieldName.equals("*")) {
					// se filterProperty = "*", pega todos os fields do field atual (1 nível apenas), cria os filterbeans com alias para filtro com join
					List<Field> childFields = Arrays.asList(ToolsReflection.getAllFields(field.getType()));
					
					for (Field childField : childFields) {
						
						Filter childSimplefilter = childField.getAnnotation(Filter.class);
						
						// ignora os fields com @Filter.filterProperty != "", por conta dos joins e aliases, por enquanto
						if ((childSimplefilter != null)&&(childSimplefilter.filterProperty()==null||childSimplefilter.filterProperty().equals(""))) {
							
							filterBeans.add(new FilterBean(simplefilter.name()+"."+childSimplefilter.name(), childField.getType(), new AdvancedFilter(alias+"."+childField.getName(), alias)));
							
						}
						
					}
					
					
				} else {
					
					// se filterProperty != "" então cria o filterbean com o alias e o filtro = filterproperty, senão cria filterbean normal
					
					filterBeans.add(new FilterBean(simplefilter.name(), field.getType(), new AdvancedFilter(fieldName, alias)));
					
				}				
				
			}
			
		}
		
		return filterBeans;
	}
	
	
	/**
	 * Método que localiza um filterBean em uma lista de filter bean através do nome escolhido pelo usuário na combo de campo
	 * 
	 * @param filterBeans lista de filtros em contexto
	 * @param fieldName  Nome amigavel do campo que será exibido para o usuário do sistema
	 * 
	 * @return filterbean correspondente ao fieldname informado
	 * 
	 * @see FilterBean
	 * */
	public static FilterBean getFilterBean(List<FilterBean> filterBeans, String fieldName){
		
		if(filterBeans != null && !filterBeans.isEmpty()){
			for (FilterBean filterBean : filterBeans) {
				if(filterBean.getFieldName().equalsIgnoreCase(fieldName)){
					return filterBean;
				}
			}
		}
		
		return null;
	}
	

	

	/**
	 * Método que localiza um Filtro em uma lista de filterBean através do nome escolhido pelo usuário na combo de campo
	 * e retorna o filtro com o valor a ser filtrado
	 * 
	 * @param filterBeans lista de filtros em contexto
	 * @param fieldName  Nome amigavel do campo que ser� exibido para o usu�rio do sistema
	 * @param fieldValue  string com o valor a ser <strong>convertido</strong> e filtrado
	 * 
	 * @return Filtro correspondente ao campo informado com valor a ser filtrado já atribuido	 
	 *  
	 * @see FilterBean
	 * */
	public static IFilter getFilter(List<FilterBean> filterBeans, String fieldName, String fieldValue){
		
		FilterBean filterBean = getFilterBean(filterBeans, fieldName);
		
		IFilter iFilter = filterBean.getFilterParameter();
		
		Object objectValue = Converter.convertFrom(fieldValue, filterBean.getFieldClass());

		return alterFilterValue(iFilter, objectValue);
	}
	

	/**
	 * Método que localiza um Filtro em uma lista de filterBean através do nome escolhido pelo usuário na combo de campo
	 * e retorna o filtro com o valor a ser filtrado
	 * 
	 * @param filterBeans lista de filtros em contexto
	 * @param fieldName  Nome amigavel do campo que será exibido para o usuário do sistema
	 * @param fieldValue  valor a ser filtrado
	 * 
	 * @return Filtro correspondente ao campo informado com valor a ser filtrado j� atribuido	 
	 *  
	 * @see FilterBean
	 * */
	
	public static IFilter getFilter(List<FilterBean> filterBeans, String fieldName, Object fieldValue){
		
		FilterBean filterBean = getFilterBean(filterBeans, fieldName);
		
		IFilter iFilter = filterBean.getFilterParameter();
		
		return alterFilterValue(iFilter, fieldValue);

	}
	
	
	/**
	 * Método que adiciona o valor que será ao filtro informado
	 * 
	 * @param iFilter filtro a ser modificado
	 * @param fieldValue valor a ser filtrado
	 * 
	 * @return Filtro modificado
	 * */
	public static IFilter alterFilterValue(IFilter iFilter,  Object fieldValue){

		if(FilterEnum.EQUAL.equals(iFilter.getFilterBy())){
			EqualFilter equalFilter = (EqualFilter) iFilter;				
			equalFilter.setInitialValue(fieldValue);
		}
		if(FilterEnum.LIKE.equals(iFilter.getFilterBy())){
			LikeFilter likeFilter = (LikeFilter) iFilter;				
			likeFilter.setInitialValue(fieldValue);
		}
		else if(FilterEnum.GREATER_EQUAL.equals(iFilter.getFilterBy())){
			GreaterEqualFilter greaterEqualFilter = (GreaterEqualFilter) iFilter;				
			greaterEqualFilter.setInitialValue(fieldValue);
		}
		else if(FilterEnum.GREATER_THAN.equals(iFilter.getFilterBy())){
			GreaterThanFilter greaterThanFilter = (GreaterThanFilter) iFilter;				
			greaterThanFilter.setInitialValue(fieldValue);
		}
		else if(FilterEnum.LESS_EQUALS.equals(iFilter.getFilterBy())){
			LessEqualFilter lessEqualFilter = (LessEqualFilter) iFilter;				
			lessEqualFilter.setInitialValue(fieldValue);
		}
		else if(FilterEnum.LESS_THAN.equals(iFilter.getFilterBy())){
			LessThanFilter lessThanFilter = (LessThanFilter) iFilter;				
			lessThanFilter.setInitialValue(fieldValue);
		}
		else if(FilterEnum.LESS_THAN.equals(iFilter.getFilterBy())){
			LessThanFilter lessThanFilter = (LessThanFilter) iFilter;				
			lessThanFilter.setInitialValue(fieldValue);
		}
		else if(FilterEnum.NOT.equals(iFilter.getFilterBy())){
			//NotFilter notFilter = (NotFilter) iFilter;				
			//((EqualFilter)notFilter.getFilter()).setInitialValue(fieldValue);
		}
		
		return iFilter;
	}
		
}
