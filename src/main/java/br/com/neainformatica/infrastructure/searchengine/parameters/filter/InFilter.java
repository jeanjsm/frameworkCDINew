package br.com.neainformatica.infrastructure.searchengine.parameters.filter;

import java.util.List;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * <strong>Esteja em</strong><br/>
 * Filtro usado verficar se o campo contenha algum dos valores informados na lista 
 * <br/>
 * Implementa a interface IFilter
 * 
 * @author Pedro Sanchez
 * @see IFilter
 * */
public class InFilter implements IFilter {

	/**método de filtragem que sera utilizado*/
	private FilterEnum filterBy;

	/**campo que será filtrado*/
	private String field;
	
	/** alias para joins */
	private String alias;


	/**valores que serão comparados*/
	private List<?> values;

	/**
	 * Cria filtro de Esteja
	 * 
	 * @param field campo que será filtrado
	 * @param value valores que serão filtrados
	 * @param alias usado nos joins
	 * */
	public InFilter(String field, List<?> values, String alias) {
		super();
		this.filterBy = FilterEnum.IN;
		this.field = field;
		this.values = values;
		this.alias = alias;
	}

	public FilterEnum getFilterBy() {
		return filterBy;
	}

	public String getAlias() {
		return alias;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public List<?> getValues() {
		return values;
	}

	public void setValues(List<?> values) {
		this.values = values;
	}

}
