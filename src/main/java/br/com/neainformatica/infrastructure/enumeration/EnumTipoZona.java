package br.com.neainformatica.infrastructure.enumeration;

import java.util.ArrayList;
import java.util.List;

public enum EnumTipoZona {
	RURAL('R',"Rural"),
	URBANA('U',"Urbana");
	
	private char zona;
	
	private String descricao;
	
	
	private static List<EnumTipoZona> list = new ArrayList<EnumTipoZona>();
	
	static{
		list.add(RURAL);
		list.add(URBANA);
	}
	
	private EnumTipoZona(char zona, String descricao) {
		this.zona = zona;
		this.descricao = descricao;
	}
	
	public char toChar() {
		return this.zona;
	}
	
	public static EnumTipoZona valueOf(char c) {
		switch (c) {
		case 'R' : return RURAL;			
		case 'U' : return URBANA;			
			default: return null;
		}
	}

	public static ArrayList<EnumTipoZona> find(String data){
		ArrayList<EnumTipoZona> list = new ArrayList<EnumTipoZona>();
		ArrayList<EnumTipoZona> result = new ArrayList<EnumTipoZona>();
		
		list.add(RURAL);
		list.add(URBANA);
		
		for (EnumTipoZona enumSimNao : list) {
			if(enumSimNao.getValue().toLowerCase().startsWith(data.toLowerCase())){
				result.add(enumSimNao);
			}
		}
		
		return result;
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

	public static List<EnumTipoZona> getList(){
		return list;
	}
	
}
