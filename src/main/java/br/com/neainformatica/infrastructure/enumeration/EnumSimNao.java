package br.com.neainformatica.infrastructure.enumeration;

public enum EnumSimNao {
	SIM('S',"Sim"),
	NAO('N',"Não");
	
	private char simNao;
	
	private String descricao;
	
	private EnumSimNao(char simNao, String descricao) {
		this.simNao = simNao;
		this.descricao = descricao;
	}
	
	public char toChar() {
		return this.simNao;
	}
	
	public boolean toBoolean(){		
		if (this.simNao == 'S')
			return true;
		
		return false;		
	}
	
	public static EnumSimNao valueOf(char c) {
		switch (c) {
		case 'S' : return SIM;			
		case 'N' : return NAO;			
			default: return null;
		}
	}
	

	@Override
	public String toString() {
		return descricao;
	}

	public String getValue() {
		return String.valueOf(this.simNao);
	}

	public String getDescricao() {
		return toString();
	}

}
