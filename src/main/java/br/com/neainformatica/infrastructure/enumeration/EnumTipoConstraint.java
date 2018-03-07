package br.com.neainformatica.infrastructure.enumeration;

public enum EnumTipoConstraint {

	PRIMARY_KEY("p", "PRIMARY KEY"), FOREIGN_KEY("f", "FOREIGN KEY"), UNIQUE("u", "UNIQUE"), CHECK("c", "CHECK");

	private String tipo;
	private String descricao;

	private EnumTipoConstraint(String tipo, String descricao) {
		this.tipo = tipo;
		this.descricao = descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
