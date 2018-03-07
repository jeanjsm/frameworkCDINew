package br.com.neainformatica.infrastructure.enumeration;


public enum EnumNaParametroTipoUpload {

	IMAGEM(1, "Imagem"),
	OUTROS(99, "Outros");	

	private Integer tipo;
	private String descricao;
	
	private EnumNaParametroTipoUpload(Integer tipo, String descricao) {
		this.tipo = tipo;
		this.descricao = descricao;
	}
	
	public static EnumNaParametroTipoUpload valueOf(Integer tipo) {
		switch (tipo) {
			case 1: return IMAGEM;
			case 99: return OUTROS;		
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
