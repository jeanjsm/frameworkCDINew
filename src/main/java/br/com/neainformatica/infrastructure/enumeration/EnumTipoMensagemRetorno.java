package br.com.neainformatica.infrastructure.enumeration;


public enum EnumTipoMensagemRetorno {

	SUCESSO('S', "Sucesso"), ERRO('E', "Erro"), ALERTA('A', "Alerta");

	private char id;
	private String descricao;

	private EnumTipoMensagemRetorno(char id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public char toChar() {
		return this.id;
	}

	public static EnumTipoMensagemRetorno valueOf(char c) {
		switch (c) {
		case 'S':
			return SUCESSO;
		case 'E':
			return ERRO;
		case 'A':
			return ALERTA;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return descricao;
	}

	public char getValue() {
		return id;
	}

	public String getDescricao() {
		return toString();
	}
}
