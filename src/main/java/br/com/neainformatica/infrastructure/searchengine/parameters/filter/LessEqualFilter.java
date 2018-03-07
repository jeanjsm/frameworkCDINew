package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * <strong>Menor ou Igual a</strong><br/>
 *    Filtro usado para trazer somente dados onde o campo contenha valores menor ou igual ao informado no initialValue
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see GenericFilter
 * @see IFilter
 * */
public class LessEqualFilter extends GenericFilter implements IFilter {

	/**
	 * Cria filtro de menor ou igual
	 * 
	 * @param field campo que será filtrado
	 * @param initialValue valor que será filtrado
	 * */
	public LessEqualFilter(String field, Object initialValue, String alias) {
		super(field, initialValue, alias);
		this.setFilterBy(FilterEnum.LESS_EQUALS);
	}

}
