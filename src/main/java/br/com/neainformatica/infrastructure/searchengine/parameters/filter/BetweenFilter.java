package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 *  <strong>Entre</strong><br/>
 * Filtro usado para trazer somente dados que o campo esteja entre os valores informados
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see GenericFilter
 * @see IFilter
 * */
public class BetweenFilter extends GenericFilter implements IFilter {

	private Object finalValue;

	public Object getFinalValue() {
		return finalValue;
	}

	public void setFinalValue(Object finalValue) {
		this.finalValue = finalValue;
	}

	/**
	 * construtor para informar o campo e valores
	 * <br/>
	 * onde o campo deverá estar entre os valores informados
	 * 
	 * @param field campo que será filtrado
	 * @param initialValue menor valor a ser comparado
	 * @param finalValue maior valor a ser comparado
	 * */
	public BetweenFilter(String field, Object initialValue, Object finalValue, String alias) {
		super(field, initialValue, alias);
		this.finalValue = finalValue;
		this.setFilterBy(FilterEnum.BETWEEN);
	}

}
