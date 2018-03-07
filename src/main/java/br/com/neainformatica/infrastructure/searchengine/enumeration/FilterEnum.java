package br.com.neainformatica.infrastructure.searchengine.enumeration;

import java.util.ArrayList;


/**
 * Tipos de Filtros
 * @author Pedro Sanchez
 * 
 * */
public enum FilterEnum {

	
	/**Igual a*/
	EQUAL(1, "Igual a"), 
	
	/**Contenha*/
	LIKE(2, "Contenha"), 

	/**Inicia com*/
	STARTING(3, "Inicia com"),

	/**Termina com*/
	ENDING(4, "Termina com"), 
	
	/**Maior que*/
	GREATER_THAN(5, "Maior que"), 
	
	/**Maior ou Igual*/
	GREATER_EQUAL(6, "Maior ou Igual"), 

	/**Menor que*/
	LESS_THAN(7, "Menor que"), 
	
	/**Menor ou Igual*/
	LESS_EQUALS(8, "Menor ou Igual"),
	
	/**Entre*/ 
	BETWEEN(9, "Entre"), 
	
	/**Esteja em*/ 	
	IN(10, "Esteja em"), 
	
	/**Nulo*/ 	
	ISNULL(11, "Nulo"), 
	
	/**ou*/ 	
	OR(12, "Ou"), 
	
	/**e*/ 	
	AND(13, "E"), 
	
	/**Negar*/ 	
	NOT(14, "Negar"),
	
	/**Não Nulo*/

	IS_NOT_NULL(15,"Não Nulo");
	
	private int id;

	private String descricao;

	private FilterEnum(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int toInt() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return descricao;
	}

	public String getValue() {
		return toString();
	}

	public String getDescricao() {
		return toString();
	}
	
	/**
	 * M�todo que serve para obter todos os tipo de filtro
	 * 
	 *  @return lista de Tipos de Filtros
	 * */
	public static ArrayList<FilterEnum> getAll() {

		ArrayList<FilterEnum> list = new ArrayList<FilterEnum>();
		
		list.add(EQUAL);
		list.add(LIKE);
		list.add(STARTING);
		list.add(ENDING);
		list.add(GREATER_THAN);
		list.add(GREATER_EQUAL);
		list.add(LESS_THAN);
		list.add(LESS_EQUALS);
		list.add(BETWEEN);
		list.add(IN);
		list.add(ISNULL);
		list.add(OR);
		list.add(AND);
		list.add(NOT);
		list.add(IS_NOT_NULL);
		
		return list;
	}


	/**
	 * Busca Tipo de Filtro pelo seu id
	 * 
	 * @param id Corresponde ao id do tipo de filtro a ser encontrado
	 * @return Retorna o tipo de filtro correspondente ao id informado ou nulo caso não seja encontrado nada
	 * */
	public static FilterEnum valueOf(int id) {

		ArrayList<FilterEnum> list = getAll();
		
		for (FilterEnum filterEnum : list) {
			if(filterEnum.toInt() == id){
				return filterEnum;
			}
		}
		
		return null;
		
	}
	


	/**
	 * Busca Tipo de Filtro pela sua descrição
	 * método requerido nos enuns do sistema para que seja possivel fazer a busca dinamica.
	 * 
	 * @param data Corresponde a descrição do tipo de filtro a ser encontrado
	 * @return Retorna uma lista de Tipos de Filtros correspondente a descrição informada ou uma lista vazia caso nada seja encontrado.
	 * */
	public static ArrayList<FilterEnum> find(String data) {
		
		ArrayList<FilterEnum> list = getAll();
		ArrayList<FilterEnum> result = new ArrayList<FilterEnum>();

		for (FilterEnum filterEnum : list) {
			if (filterEnum.getValue().toLowerCase()
					.contains(data.toLowerCase())) {
				result.add(filterEnum);
			}
		}

		return result;

	}
	
	public String getStringFilter(){
		if(this.equals(FilterEnum.EQUAL)){
			return "=";
		}
		if(this.equals(FilterEnum.GREATER_EQUAL)){
			return ">=";
		}
		if(this.equals(FilterEnum.GREATER_THAN)){
			return ">";
		}
		if(this.equals(FilterEnum.LESS_EQUALS)){
			return "<=";
		}
		if(this.equals(FilterEnum.LESS_THAN)){
			return "<=";
		}
		


		if(this.equals(FilterEnum.LIKE)){
			return "Contenha";
		}
		if(this.equals(FilterEnum.STARTING)){
			return "Comece com";
		}
		if(this.equals(FilterEnum.ENDING)){
			return "Terminando com";
		}
		

		if(this.equals(FilterEnum.NOT)){
			return "não";
		}
		
		if(this.equals(FilterEnum.IN)){
			return "Esteja em ";
		}
		if(this.equals(FilterEnum.ISNULL)){
			return "Seja vazio ou nulo";
		}
		
		if(this.equals(FilterEnum.IS_NOT_NULL)){
			return "Não nulo";
		}
		

		if(this.equals(FilterEnum.BETWEEN)){
			return "Esteja entre";
		}
		
		return "";
		
	}

	
}
