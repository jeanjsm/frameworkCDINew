package br.com.neainformatica.infrastructure.enumeration;

import java.util.ArrayList;
import java.util.List;

public enum EnumMesesDoAno {

	JANEIRO(1, "Janeiro"), FEVEREIRO(2, "Fevereiro"), MARCO(3, "Março"), ABRIL(4, "Abril"), MAIO(5, "Maio"), JUNHO(6, "Junho"), JULHO(7, "Julho"), AGOSTO(8, "Agosto"), SETEMBRO(9, "Setembro"), OUTUBRO(
			10, "Outubro"), NOVEMBRO(11, "Novembro"), DEZEMBRO(12, "Dezembro");

	private int id;
	private String descricao;

	private static List<EnumMesesDoAno> list = new ArrayList<>();

	static {
		list.add(JANEIRO);
		list.add(FEVEREIRO);
		list.add(MARCO);
		list.add(ABRIL);
		list.add(MAIO);
		list.add(JUNHO);
		list.add(JULHO);
		list.add(AGOSTO);
		list.add(SETEMBRO);
		list.add(OUTUBRO);
		list.add(NOVEMBRO);
		list.add(DEZEMBRO);
	}

	public static EnumMesesDoAno valueOf(int id) {
		switch (id) {
		case 1:
			return JANEIRO;
		case 2:
			return FEVEREIRO;
		case 3:
			return MARCO;
		case 4:
			return ABRIL;
		case 5:
			return MAIO;
		case 6:
			return JUNHO;
		case 7:
			return JULHO;
		case 8:
			return AGOSTO;
		case 9:
			return SETEMBRO;
		case 10:
			return OUTUBRO;
		case 11:
			return NOVEMBRO;
		case 12:
			return DEZEMBRO;
		default:
			return null;
		}

	}

	public int toInt() {
		return this.id;
	}

	private EnumMesesDoAno(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static List<EnumMesesDoAno> getList() {
		return list;
	}

	public static void setList(List<EnumMesesDoAno> list) {
		EnumMesesDoAno.list = list;
	}

}
