package br.com.neainformatica.infrastructure.enumeration;


public enum EnumNaParametroTipo {

	BINARIO(1, "Valor Binário"),
	DATA(2, "Data"),
	HORA(3, "Hora"),	
	DATA_HORA(4, "Data Hora"),
	SENHA(5, "Senha"),
	TEXTO(6, "Texto"),
	VALOR_FRACIONARIO(7, "Valor Fracionário"),
	VALOR_INTEIRO(8, "Valor Inteiro"),
	OPCOES(9, "Lista de Opções"),
	//OPCOES_BINARIO(10, "Lista de Opções de Binários"),
	SQL(11, "Consulta SQL");

	private Integer tipo;
	private String descricao;
	
	private EnumNaParametroTipo(Integer tipo, String descricao) {
		this.tipo = tipo;
		this.descricao = descricao;
	}
	
	public static EnumNaParametroTipo valueOf(Integer tipo) {
		switch (tipo) {
			case 1: return BINARIO;
			case 2: return DATA;
			case 3: return HORA;
			case 4: return DATA_HORA;
			case 5: return SENHA;
			case 6: return TEXTO;
			case 7: return VALOR_FRACIONARIO;
			case 8: return VALOR_INTEIRO;
			case 9: return OPCOES;
			//case 10: return OPCOES_BINARIO;
			case 11: return SQL;
			default: return null;
		}
	}

	public Integer toInt() {
		return this.tipo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	@Override
	public String toString() {
		return this.descricao;
	}
	
}
