package br.com.neainformatica.infrastructure.enumeration;


public enum EnumNaUsuarioOrigemAlteracao {
	WEB('W',"Web"),
	DESKTOP('D',"Desktop");
	
	private char origemAlteracao;
	
	private String descricao;
	
	private EnumNaUsuarioOrigemAlteracao(char simNao, String descricao) {
		this.origemAlteracao = simNao;
		this.descricao = descricao;
	}
	
	public char toChar() {
		return this.origemAlteracao;
	}
	
	public static EnumNaUsuarioOrigemAlteracao valueOf(char c) {
		switch (c) {
		case 'W' : return WEB;			
		case 'D' : return DESKTOP;			
			default: return null;
		}
	}
	

	@Override
	public String toString() {
		return descricao;
	}

	public String getValue() {
		return String.valueOf(this.origemAlteracao);
	}

	public String getDescricao() {
		return toString();
	}

}
