package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;


/**
 * <strong><em>Esta classe não pode ser usada apenas herdada</em></strong>
 * 
 * <br/>
 * Filtro generalizado para alguns filtros que possuem este comportamento identico * 
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see IFilter
 * */
class GenericFilter implements IFilter {
	
	/**campo que será filtrado*/
	private String field;
	
	/**método de filtragem que será utilizado*/
	private FilterEnum filterBy;
	
	/** alias para join */
	private String alias;
	
	/**
	 * valor que será filtrado
	 * */
	private Object initialValue;


	/**Contrutor Default vazio*/
	public GenericFilter() {
		
	}

	/**
	 * construtor usado nas classes que estendem esta onde é passado o valor 
	 * 
	 * @param field campo que será filtrado
	 * @param initialValue valor que será filtrado
	 * */
	public GenericFilter(String field, Object initialValue, String alias) {
		this.field = field;
		this.initialValue = initialValue;
		this.alias = alias;
	}

	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public FilterEnum getFilterBy() {
		return filterBy;
	}
	public void setFilterBy(FilterEnum filterBy) {
		this.filterBy = filterBy;
	}
 
	public String getAlias() {
		return alias;
	}

	public Object getInitialValue() {
		return initialValue;
	}
	public void setInitialValue(Object initialValue) {
		this.initialValue = initialValue;
	}
	
	
	

}
