package br.com.neainformatica.infrastructure.enumeration;

public enum EnumTipoTelefone {

	RESIDENCIAL(1, "Residencial"), 
	COMERCIAL(2, "Comercial"), 
	CELULAR(3, "Celular"), 
	FAX(4, "Fax"), 
	T0800(5, "0800");	

	private int id;
	private String descricao;
	
	public static EnumTipoTelefone valueOf(int id) {
		switch (id) {
		case 1:	return RESIDENCIAL;
		case 2:	return COMERCIAL;
		case 3:	return CELULAR;
		case 4:	return FAX;
		case 5:	return T0800;
		default: return null;
		}
	}

	public int toInt() {
		return this.id;
	}

	private EnumTipoTelefone(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}		

}
