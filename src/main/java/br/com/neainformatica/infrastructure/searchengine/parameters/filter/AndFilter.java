package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import java.util.List;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 *  <strong>Agrupador E</strong><br/>
 * Filtro agrupador usado para a operação do tipo E 
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see IFilter
 * */
public class AndFilter implements IFilter {

	/**método de filtragem que será utilizado*/
	private FilterEnum filterBy;

	/**Lista de Filtros que serão agrupados pelo operador E*/
	private List<IFilter> filters;

	/**
	 * construtor usado para informar os filtros que serão agrupados 
	 * 
	 * @param filters Lista de Filtros que serão agrupados pelo operador E
	 * */
	public AndFilter(List<IFilter> filters) {
		super();
		this.filterBy = FilterEnum.AND;
		this.filters = filters;
	}

	public FilterEnum getFilterBy() {
		return filterBy;
	}

	public String getAlias() {
		return null;
	}

	public List<IFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<IFilter> filters) {
		this.filters = filters;
	}

}
