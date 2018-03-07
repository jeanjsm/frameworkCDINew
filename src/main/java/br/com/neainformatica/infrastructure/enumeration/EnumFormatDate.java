package br.com.neainformatica.infrastructure.enumeration;

public enum EnumFormatDate {

	RESUMIDO("dd/MM/yyyy"), 
	RESUMIDO_INTERNACIONAL("yyyy-MM-dd"),
	DATE_TIME("dd/MM/yyyy HH:mm"),
	DATE_TIME_HMS("dd/MM/yyyy HH:mm:ss"),
	EXTENSO("d 'de' MMMM 'de' yyyy"), 
	EXTENSO_COM_DIA_SEMANA("EEEE, d 'de' MMMM 'de' yyyy"), 
	DIA_SEMANA("dd"), 
	MES_EXTENSO("MM"), 
	ANO_EXTENSO("yyyy"), 
	HORA_DATA("HH:mm"), 
	HORA_DATA_DETALHADA("HH:mm:ss"),
	DIA_SEMANA_EXTENSO("EEEE"),
	DDMMYYYHHMMSS("ddMMyyyyHHmmss"),	
	RESUMIDO_INTERNACIONAL_SEM_MASCARA("yyyyMMdd");		

	private String mascara;

	private EnumFormatDate(String mascara) {
		this.mascara = mascara;
	}

	public String getMascara() {
		return mascara;
	}

	public void setMascara(String mascara) {
		this.mascara = mascara;
	}

}
