package br.com.neainformatica.infrastructure.enumeration;

import br.com.neainformatica.infrastructure.interfaces.EnumRelatorioInterface;

public enum EnumRelatorio implements EnumRelatorioInterface {
	PAGE_HEADER_RETRATO(0,"_pageHeaderRetrato", "_pageHeaderRetrato"),
	PAGE_FOOTER_RETRATO(1,"_pageFooterRetrato", "_pageFooterRetrato"),
	PAGE_HEADER_PAISAGEM(2,"_pageHeaderPaisagem", "_pageHeaderPaisagem"),
	PAGE_FOOTER_PAISAGEM(3,"_pageFooterPaisagem", "_pageFooterPaisagem"),
	AUDITORIA(4,"Auditoria", "Relatório de Auditoria");
	
	private Integer codigo;
	private String arquivo;
	private String titulo;
	
	private EnumRelatorio(Integer codigo,String arquivo, String titulo) {
		this.codigo = codigo;
		this.arquivo = arquivo;
		this.titulo = titulo;
	}
	
	public static EnumRelatorio valueOf(int i) {
		switch (i) {
			case 0 : return PAGE_HEADER_RETRATO;
			case 1 : return PAGE_FOOTER_RETRATO;
			case 2 : return PAGE_HEADER_PAISAGEM;
			case 3 : return PAGE_FOOTER_PAISAGEM;
			case 4 : return AUDITORIA;
			default: return null;
		}
	}

	public String getArquivo() {
		return arquivo;
	}
	
	public String getTitulo() {
		return titulo;
	}

	public Integer getCodigo() {
		return this.codigo;
	}
	
}