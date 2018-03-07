package br.com.neainformatica.infrastructure.enumeration;

public enum EnumRelatorioExportacao {
	PDF("pdf"), 
	XLS("xls"), 
	RTF("rtf"),
	HTML("html"),	
	CSV("csv"),
	XML("xml"),
	ODF("odt"),
	TXT("txt");
	
	private String descricao;
	
	private EnumRelatorioExportacao(String descricao) {
		this.descricao = descricao;
	}
	
	public static EnumRelatorioExportacao valueOf(int i) {
		switch (i) {
		case 0 : return PDF;			
		case 1 : return XLS;
		case 2 : return RTF;
		case 3 : return HTML;
		case 4 : return CSV;
		case 5 : return XML;
		case 6 : return ODF;
		case 7 : return TXT;
			default: return null;
		}
	}

	@Override
	public String toString() {
		return descricao;
	}

	public String getDescricao() {
		return toString();
	}

}
