package br.com.neainformatica.infrastructure.enumeration;

public enum EnumTipoNaPermissao {
	CADASTRO('C', "Cadastro"), 
	RELATORIO('R', "Relatório"), 
	PROCESSAMENTO('P', "Processamento"),
	RESTRITIVA('X', "Restritiva");

	private char tipoPermissao;
	private String descricao;

	private EnumTipoNaPermissao(char tipoPermissao, String descricao) {
		this.tipoPermissao = tipoPermissao;
		this.descricao = descricao;
	}

	public char toChar() {
		return this.tipoPermissao;
	}

	public static EnumTipoNaPermissao valueOf(char c) {
		switch (c) {
		case 'C': return CADASTRO;
		case 'R': return RELATORIO;
		case 'P': return PROCESSAMENTO;
		case 'X': return RESTRITIVA;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return descricao;
	}

	public char getValue() {
		return tipoPermissao;
	}

	public String getDescricao() {
		return toString();
	}

}
