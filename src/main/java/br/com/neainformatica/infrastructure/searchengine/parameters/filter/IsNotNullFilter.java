package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 *  <strong>Agrupador de Negação</strong><br/>
 * Filtro agrupador usado para a operação do tipo não 
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see IFilter
 * */
public class IsNotNullFilter implements IFilter {

	/**método de filtragem que será utilizado*/
	private FilterEnum filterBy;

	/**campo que será filtrado*/
	private String field;

	private String alias;

	/**
	 * construtor usado para informar o campo que deverá ser nulo 
	 * @param field campo que deverá ser nulo 
	 * */
	public IsNotNullFilter(String field, String alias) {
		super();
		this.filterBy = FilterEnum.IS_NOT_NULL;
		this.field = field;
		this.alias = alias;
	}

	public FilterEnum getFilterBy() {
		return filterBy;
	}

	public String getAlias() {
		return alias;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

}
