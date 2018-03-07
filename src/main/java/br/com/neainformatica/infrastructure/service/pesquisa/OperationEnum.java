package br.com.neainformatica.infrastructure.service.pesquisa;

public enum OperationEnum {
	EQUALS, 
	LIKE, 
	STARTING, 
	ENDING, 
	GREATER_THAN, 
	LESS_THAN, 
	BETWEEN, 
	ISNULL,
	DIFERENTE, 
	ISNOTNULL, 
	IN,
	NOT_IN,
	NOT_EXIST,
	EXIST
}