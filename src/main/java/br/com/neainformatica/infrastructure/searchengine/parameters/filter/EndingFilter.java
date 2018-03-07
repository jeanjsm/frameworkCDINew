package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * <strong>Contenha</strong><br/>
 * Filtro usado para trazer somente dados onde o campo contenha o valor informado no initialValue
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see GenericFilter
 * @see IFilter
 * */
public class EndingFilter extends GenericFilter implements IFilter {

	/**
	 * Cria filtro de Contenha
	 * 
	 * @param field campo que será filtrado
	 * @param initialValue valor que será filtrado
	 * */
	public EndingFilter(String field, Object initialValue, String alias) {
		super(field, initialValue, alias);
		this.setFilterBy(FilterEnum.ENDING);
	}

}
