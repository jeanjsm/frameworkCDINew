package br.com.neainformatica.infrastructure.enumeration;

public enum EnumTipoAtor {
	PERFIL('P', "Perfil"), 
	USUARIO('U', "Usuário");

	private char tipoAtor;
	private String descricao;

	private EnumTipoAtor(char tipoAtor, String descricao) {
		this.tipoAtor = tipoAtor;
		this.descricao = descricao;
	}

	public char toChar() {
		return this.tipoAtor;
	}

	public static EnumTipoAtor valueOf(char c) {
		switch (c) {
		case 'P': return PERFIL;
		case 'U': return USUARIO;
		default: return null;
		}
	}

	@Override
	public String toString() {
		return descricao;
	}

	public char getValue() {
		return tipoAtor;
	}

	public String getDescricao() {
		return toString();
	}

}
