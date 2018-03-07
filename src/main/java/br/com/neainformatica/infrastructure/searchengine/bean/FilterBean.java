package br.com.neainformatica.infrastructure.searchengine.bean;

import java.util.ArrayList;
import java.util.List;

import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * Bean utilizado para montar pesquisa dinamica
 * 
 * @author Pedro Sanchez
 * */
public class FilterBean {
	private String fieldName;
	private Class<?> fieldClass;
	private IFilter filterParameter;
	
	private List<FilterBean> filterBeans = new ArrayList<>();
	
	/**
	 * Contrutor da classe
	 * 
	 * @param fieldName Nome amigavel do campo que será exibido para o usuário do sistema
	 * @param fieldClass Tipo de dado que este campo aceita
	 * @param filterParameter interface do tipo de filtro que será utilizado na consulta para este campo
	 * 
	 * @see IFilter
	 * */
	public FilterBean(String fieldName, Class<?> fieldClass, IFilter filterParameter) {
		super();
		this.fieldName = fieldName;
		this.fieldClass = fieldClass;
		this.filterParameter = filterParameter;
	}
	

	 /** 
	  * @return Nome amigavel do campo que será exibido para o usuário do sistema
	  * 
	  * */

	public String getFieldName() {
		return fieldName;
	}

	 /** 
	  * @param fieldName Nome amigavel do campo que será exibido para o usuário do sistema
	  * 
	  * */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}

	public void setFieldClass(Class<?> fieldClass) {
		this.fieldClass = fieldClass;
	}

	public IFilter getFilterParameter() {
		return filterParameter;
	}

	public void setFilterParameter(IFilter filterParameter) {
		this.filterParameter = filterParameter;
	}


	public void addChildren(FilterBean filterBean) {
		
		if(filterBean != null){
			filterBeans.add(filterBean);
		}
		
	}
	public void removeChildren(FilterBean filterBean) {
		
		if(filterBean != null){
			filterBeans.remove(filterBean);
		}
		
	}

	public void cleanChildren() {
		
		filterBeans.clear();
			
	}


	public List<FilterBean> getFilterBeans() {
		return  this.filterBeans;
	}
	
	
	
	
	
}
