package br.com.neainformatica.infrastructure.enumeration;

public enum EnumAuditoriaOperacao {
	INCLUSAO(1, "Inclusão"), 
	ALTERACAO(2, "Alteração"), 
	EXCLUSAO(3, "Exclusão");
	
	private int id;
	private String descricao;
	
	private EnumAuditoriaOperacao(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}
	
	public static EnumAuditoriaOperacao valueOf(int i) {
		switch (i) {
		case 1 : return INCLUSAO;			
		case 2 : return ALTERACAO;
		case 3 : return EXCLUSAO;
			default: return null;
		}
	}

	public int toInt() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return descricao;
	}

	public String getDescricao() {
		return toString();
	}

}