package br.com.neainformatica.infrastructure.enumeration;

public enum EnumTipoColumnBD {
	 CHAR(1, "Char"), 
	 VARCHAR(2, "Varchar"), 
	 INTEGER(3, "Integer"),
	 BIG_INTEGER(4, "BigInteger"),
	 DATE(5, "Date"), 
	 TIME(6, "Time"), 
	 TIMESTAMP(7, "Timestamp"), 
	 BLOB_BINARIO(8, "BlobBinario"), 
	 BLOB_TEXTO(9, "BlobTexto"), 
	 NUMERIC(10, "Numeric");
		
	private int id;
	private String descricao;
	
	private EnumTipoColumnBD(int id, String descricao){
		this.id = id;
		this.descricao = descricao;
	}
	
	public int toInt() {
		return this.id;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public static EnumTipoColumnBD valueOf(int id) {
		switch (id) {
		case 1:	return CHAR;
		case 2:	return VARCHAR;
		case 3:	return INTEGER;
		case 4:	return BIG_INTEGER;
		case 5:	return DATE;
		case 6:	return TIME;
		case 7:	return TIMESTAMP;
		case 8:	return BLOB_BINARIO;
		case 9:	return BLOB_TEXTO;
		case 10: return NUMERIC;		
		default: return null;
		}
	}
	
}
