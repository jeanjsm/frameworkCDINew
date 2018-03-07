package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * <strong>Menor que</strong><br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Filtro usado para trazer somente dados onde o campo contenha valores menor que o informado no initialValue
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see GenericFilter
 * @see IFilter
 * */
public class LessThanFilter extends GenericFilter implements IFilter {

	/**
	 * Cria filtro de menor que
	 * 
	 * @param field campo que será filtrado
	 * @param initialValue valor que será filtrado
	 * */
	public LessThanFilter(String field, Object initialValue, String alias) {
		super(field, initialValue, alias);
		this.setFilterBy(FilterEnum.LESS_THAN);
	}

}
