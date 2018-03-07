package br.com.neainformatica.infrastructure.enumeration;


public enum EnumFisicaJuridica {
	FISICA('F',"Física"),
	JURIDICA('J',"Jurídica");
	
	private char fisicaJuridica;
	
	private String descricao;
	
	private EnumFisicaJuridica(char fisicaJuridica, String descricao) {
		this.fisicaJuridica = fisicaJuridica;
		this.descricao = descricao;
	}
	
	public char toChar() {
		return this.fisicaJuridica;
	}
	
	public static EnumFisicaJuridica valueOf(char c) {
		switch (c) {
		case 'F' : return FISICA;			
		case 'J' : return JURIDICA;			
			default: return null;
		}
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
