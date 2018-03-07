package br.com.neainformatica.infrastructure.enumeration;

public enum EnumSituacaoAcesso {

	ATIVO('A', "Ativo"), BLOQUEADO('B', "Bloqueado"), SOMENTE_LEITURA('L', "Somente Leitura");

	private char id;
	private String descricao;

	private EnumSituacaoAcesso(char id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public char toChar() {
		return this.id;
	}
	
	public static EnumSituacaoAcesso valueOf(char c) {
		switch (c) {
		case 'A':
			return ATIVO;
		case 'B':
			return BLOQUEADO;
		case 'L':
			return SOMENTE_LEITURA;
		default:
			return null;
		}
	}

	public static EnumSituacaoAcesso getIdEnum(String value) {
		switch (value) {
		case "A":
			return ATIVO;
		case "B":
			return BLOQUEADO;
		case "L":
			return SOMENTE_LEITURA;

		default:
			return null;
		}
	}

	public char getId() {
		return id;
	}

	public String getDescricao() {
		return descricao;
	}

}
