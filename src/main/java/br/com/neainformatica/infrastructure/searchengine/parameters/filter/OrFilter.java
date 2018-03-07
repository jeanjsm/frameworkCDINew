package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import java.util.List;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * <strong>Agrupador Ou</strong><br/>
 *     Filtro agrupador usado para a operação do tipo OU 
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see IFilter
 * */
public class OrFilter implements IFilter {

	/**método de filtragem que será utilizado*/
	private FilterEnum filterBy;

	/**Lista de Filtros que serão agrupados pelo operador E*/
	private List<IFilter> filters;
	
	private String alias;

	/**
	 * construtor usado para informar os filtros que serão agrupados 
	 * 
	 * @param filters Lista de Filtros que serão agrupados pelo operador Ou
	 * */
	public OrFilter(List<IFilter> filters, String alias) {
		super();
		this.filterBy = FilterEnum.OR;
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
