package br.com.neainformatica.infrastructure.enumeration;

import java.util.ArrayList;

public enum EnumSexo {
	MASCULINO('M',"Masculino"),
	FEMININO('F',"Feminino");
	
	private char enumSexo;
	
	private String descricao;
	
	private EnumSexo(char enumSexo, String descricao) {
		this.enumSexo = enumSexo;
		this.descricao = descricao;
	}
	
	public char toChar() {
		return this.enumSexo;
	}
	
	public static EnumSexo valueOf(char c) {
		switch (c) {
		case 'M' : return MASCULINO;			
		case 'F' : return FEMININO;			
			default: return null;
		}
	}

	public static ArrayList<EnumSexo> find(String data){
		ArrayList<EnumSexo> list = new ArrayList<EnumSexo>();
		ArrayList<EnumSexo> result = new ArrayList<EnumSexo>();
		
		list.add(MASCULINO);
		list.add(FEMININO);
		
		for (EnumSexo enumSexo : list) {
			if(enumSexo.getValue().toLowerCase().contains(data.toLowerCase())){
				result.add(enumSexo);
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

}
