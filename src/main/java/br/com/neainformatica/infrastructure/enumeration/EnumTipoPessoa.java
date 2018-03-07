package br.com.neainformatica.infrastructure.enumeration;

import java.util.ArrayList;
import java.util.List;

public enum EnumTipoPessoa {

	FISICA('F', "Física", "cpf"), JURIDICA('J', "Jurídica", "cnpj");

	private char tipo;
	private String descricao;
	private String descricaoMascara;

	private static List<EnumTipoPessoa> list = new ArrayList<EnumTipoPessoa>();

	static {
		list.add(FISICA);
		list.add(JURIDICA);
	}

	EnumTipoPessoa(char tipo, String descricao, String descricaoMascara) {
		this.tipo = tipo;
		this.descricao = descricao;
		this.descricaoMascara = descricaoMascara;
	}

	public static EnumTipoPessoa valueOf(char tipo) {
		switch (tipo) {
		case 'F':
			return FISICA;
		case 'J':
			return JURIDICA;
		default:
			return null;
		}
	}

	public char toChar() {
		return this.tipo;
	}

	public char getTipo() {
		return tipo;
	}

	public void setTipo(char tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static List<EnumTipoPessoa> getList() {
		return list;
	}

	public String getDescricaoMascara() {
		return this.descricaoMascara;
	}

}
