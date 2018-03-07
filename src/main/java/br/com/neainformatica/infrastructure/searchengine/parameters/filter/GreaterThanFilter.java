package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * <strong>Maior que</strong><br/>
 * Filtro usado para trazer somente dados onde o campo contenha valores maior
 * que o informado no initialValue <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see GenericFilter
 * @see IFilter
 * */
public class GreaterThanFilter extends GenericFilter implements IFilter {

	/**
	 * Cria filtro de maio que
	 * 
	 * @param field campo que será filtrado
	 * @param initialValue valor que será filtrado
	 * */
	public GreaterThanFilter(String field, Object initialValue, String alias) {
		super(field, initialValue, alias);
		this.setFilterBy(FilterEnum.GREATER_THAN);
	}

}
