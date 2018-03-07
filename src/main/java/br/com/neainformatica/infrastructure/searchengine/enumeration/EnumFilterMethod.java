package br.com.neainformatica.infrastructure.searchengine.enumeration;

/**
 * Enum Método de Filtragem usado durante as anotações da entidades para informar qual é o modo de filtragem padrão desta
 * 
 *  @author Pedro Sanchez
 * */
public enum EnumFilterMethod {

	/**Igual a*/
	EQUALS, 
	/**Diferente de*/
	DIFERENTE, 
	/**Contenha*/
	LIKE, 
	/**Menor que*/
	LESS_THAN, 
	/**Menor ou Igual*/
	LESS_EQUAL, 
	/**Maior que*/
	GREATER_THAN,	
	/**Maior ou Igual*/
	GREATER_EQUAL, 
	/**Usado para quando não se deseja que o campo seja um filtro simples da grid/EditSearch*/
	DEFAULT
}
