package br.com.neainformatica.infrastructure.searchengine.enumeration;

import java.util.ArrayList;

/**
 * <strong>Tipos de Operação</strong><br/>
 * Enum Utilizado para informar o tipo de operação a ser usada pelo usuário na tela de pesquisa avançada
 * 
 * @author Pedro Sanchez
 * */
public enum OperatorEnum {

	/**Campo utilizado para adicionar um metodo de busca só exibido na primeira vez*/
	FILTER(1, "Filtrar"), 
	/**Abre parentes para agrupar dados da pesquisa*/
	OPEN_AGRUPATOR(2, "Abrir Agrupador - ("), 
	/**fecha parentes para agrupar dados da pesquisa*/
	CLOSE_AGRUPATOR(3, "Fechar Agrupagor - )"), 	
	/**Adiciona o filtro com um ou antes*/
	OR(4, "Ou"), 
	/**Adiciona o filtro com um e antes*/
	AND(5, "E"), 
	/**nega a operação a seguir*/
	NOT(6, "Negar");

	private int id;

	private String descricao;

	private OperatorEnum(int id, String descricao) {
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
	 * Metodo que serve para obter todos os tipos de operação
	 * 
	 *  @return lista de Tipos de Operação
	 *  
	 * */

	public static ArrayList<OperatorEnum> getAll() {

		ArrayList<OperatorEnum> list = new ArrayList<OperatorEnum>();
		
		list.add(FILTER);
		list.add(OPEN_AGRUPATOR);
		list.add(CLOSE_AGRUPATOR);
		list.add(OR);
		list.add(AND);
		list.add(NOT);
		
		return list;
	}


	/**
	 * Busca Tipo de Operação pelo seu id
	 * 
	 * @param id Corresponde ao id do Tipo de Operação a ser encontrado
	 * @return Retorna o Tipo de Operação correspondente ao id informado ou nulo caso não seja encontrado nada
	 * */
	public static OperatorEnum valueOf(int id) {

		ArrayList<OperatorEnum> list = getAll();
		
		for (OperatorEnum operatorEnum : list) {
			if(operatorEnum.toInt() == id){
				return operatorEnum;
			}
		}
		
		return null;
		
	}
	

	/**
	 * Busca Tipo de Operação pela sua descrição
	 * metodo requerido nos enuns do sistema para que seja possivel fazer a busca dinamica.
	 * 
	 * @param data Corresponde a descrição do Tipo de Operação a ser encontrado
	 * @return Retorna uma lista de Tipos de Operação correspondente a descrição informada ou uma lista vazia caso nada seja encontrado.
	 * */
	public static ArrayList<OperatorEnum> find(String data) {
		ArrayList<OperatorEnum> list = getAll();
		ArrayList<OperatorEnum> result = new ArrayList<OperatorEnum>();

		for (OperatorEnum operatorEnum : list) {
			if (operatorEnum.getValue().toLowerCase()
					.contains(data.toLowerCase())) {
				result.add(operatorEnum);
			}
		}

		return result;

	}


}
