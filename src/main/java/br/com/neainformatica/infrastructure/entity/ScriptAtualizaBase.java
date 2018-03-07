package br.com.neainformatica.infrastructure.entity;

public class ScriptAtualizaBase {

	private Integer numeroVersao;
	private String sql;
	
	public ScriptAtualizaBase() {
		super();
	}

	public ScriptAtualizaBase(Integer numeroVersao, String sql) {
		super();
		this.numeroVersao = numeroVersao;
		this.sql = sql;
	}

	public Integer getNumeroVersao() {
		return numeroVersao;
	}

	public void setNumeroVersao(Integer numeroVersao) {
		this.numeroVersao = numeroVersao;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((numeroVersao == null) ? 0 : numeroVersao.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof ScriptAtualizaBase))
			return false;
		ScriptAtualizaBase other = (ScriptAtualizaBase) obj;
		if (numeroVersao == null) {
			if (other.numeroVersao != null)
				return false;
		} else if (!numeroVersao.equals(other.numeroVersao))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Versão: " + numeroVersao;
	}

}