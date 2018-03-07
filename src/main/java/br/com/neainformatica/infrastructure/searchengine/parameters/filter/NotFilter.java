package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import java.util.List;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 *  <strong>Agrupador de Negação</strong><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Filtro agrupador usado para a opera��o do tipo n�o 
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see IFilter
 * */
public class NotFilter implements IFilter {

	/**método de filtragem que será utilizado*/
	private FilterEnum filterBy;

	/**Lista de Filtros que serão agrupados pelo operador Negação*/
	private List<IFilter> filters;
	
	private String alias;

	/**
	 * construtor usado para informar os filtros que serão agrupados  
	 * @param filters Lista de Filtros que serão agrupados pelo operador Negação
	 * */
	public NotFilter(List<IFilter> filters, String alias) {
		super();
		this.filterBy = FilterEnum.AND;
		this.filters = filters;
		this.alias = alias;
	}

	public FilterEnum getFilterBy() {
		return filterBy;
	}
	
	public String getAlias() {
		return alias;
	}

	public List<IFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<IFilter> filters) {
		this.filters = filters;
	}

}
