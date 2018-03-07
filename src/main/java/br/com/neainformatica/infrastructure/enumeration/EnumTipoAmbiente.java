package br.com.neainformatica.infrastructure.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnumTipoAmbiente {
	DESKTOP(1, "Desktop"), 
	WEB(2, "Web");
	
	private int id;
	private String descricao;
	
	private EnumTipoAmbiente(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}
	
	@JsonCreator
	public static EnumTipoAmbiente valueOf(int i) {
		switch (i) {
		case 1 : return DESKTOP;			
		case 2 : return WEB;		
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