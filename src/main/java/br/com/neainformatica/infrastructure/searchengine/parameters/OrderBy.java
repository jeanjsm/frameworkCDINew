package br.com.neainformatica.infrastructure.searchengine.parameters;

/**
 * Classe utilizada na busca dinamica usada para ordenar os campos da busca
 * 
 * @author Pedro Sanchez
 * */
public class OrderBy {
	
	private String field;
	private String direction = "ASC";

	public OrderBy(){}

	public OrderBy(String field){
		this.field = field;
	}	

	public OrderBy(String field, String direction){
		this.field = field;
		this.direction = direction;
	}
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	
	
}
