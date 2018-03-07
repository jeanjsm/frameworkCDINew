package br.com.neainformatica.infrastructure.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumTipoBancoDados {
	FIREBIRD(0, "Firebird"),
	FIREBIRD_2(1, "Firebird 2"),
	FIREBIRD_21(2, "Firebird 2.1"),
	FIREBIRD_25(3, "Firebird 2.5"),
	FIREBIRD_3(4, "Firebird 3"),
	POSTGRESQL(20, "PostgreSql");
			
	private int id;
	private String descricao;
	
	private EnumTipoBancoDados(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}
	
	@JsonCreator
	public static EnumTipoBancoDados valueOf(int i) {
		switch (i) {
		case 0 : return FIREBIRD;			
		case 1 : return FIREBIRD_2;		
		case 2 : return FIREBIRD_21;		
		case 3 : return FIREBIRD_25;		
		case 4 : return FIREBIRD_3;		
		case 20 : return POSTGRESQL;		
			default: return null;
		}
	}

	@JsonValue
	public int toInt() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return descricao;
	}

	public String getDescricao() {
		return toString();
	}

}