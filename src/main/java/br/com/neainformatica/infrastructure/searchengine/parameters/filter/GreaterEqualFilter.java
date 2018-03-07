package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * <strong>Maior ou Igual a</strong><br/>
 * Filtro usado para trazer somente dados onde o campo contenha valores maior ou igual ao informado no initialValue
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see GenericFilter
 * @see IFilter
 * */
public class GreaterEqualFilter extends GenericFilter implements IFilter {

	/**
	 * Cria filtro de maior ou igual
	 * 
	 * @param field campo que será filtrado
	 * @param initialValue valor que seá� filtrado
	 * */
	public GreaterEqualFilter(String field, Object initialValue, String alias) {
		super(field, initialValue, alias);
		this.setFilterBy(FilterEnum.GREATER_EQUAL);
	}

}
