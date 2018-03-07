package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 *  <strong>Igual a</strong><br/>
 * Filtro usado para comparação de Igualdade 
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see GenericFilter
 * @see IFilter
 * */
public class EqualFilter extends GenericFilter implements IFilter {

	/**
	 * construtor usado para informar o campo e valor que será usado para o filtro de igualdade
	 * 
	 * @param field campo que será filtrado
	 * @param initialValue valor que será filtrado
	 * */
	public EqualFilter(String field, Object initialValue, String alias) {
		super(field, initialValue, alias);
		this.setFilterBy(FilterEnum.EQUAL);
	}

}
