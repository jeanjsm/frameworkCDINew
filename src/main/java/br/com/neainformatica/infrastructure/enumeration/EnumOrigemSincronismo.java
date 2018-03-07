package br.com.neainformatica.infrastructure.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumOrigemSincronismo {
	
	CLIENTE('C', "Cliente"),
	SERVER('S', "Server");
	
	private char sigla;
	
	private String descricao;

	private EnumOrigemSincronismo(char sigla, String descricao) {
	    this.sigla = sigla;
	    this.descricao = descricao;
    }
	

	@JsonValue
	public char toChar() {
		return this.sigla;
	}
	
	@JsonCreator
	public static EnumOrigemSincronismo valueOf(char c) {
		switch (c) {
		case 'C' : return CLIENTE;			
		case 'S' : return SERVER;			
			default: return null;
		}
	}
	
	@Override
	public String toString() {
	    return this.descricao;
	}

}
