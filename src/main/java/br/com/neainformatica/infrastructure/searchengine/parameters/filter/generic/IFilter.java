package br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;

/**Interface para que seja possivel adicionar nova consulta sem grandes efeitos colaterais
 * @author Pedro Sanchez
 * */
public interface IFilter {
	
	/**
	 * Obtém o critério que será utilizado na busca
	 * */
	public FilterEnum getFilterBy();
	public String getAlias();
}
