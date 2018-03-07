package br.com.neainformatica.infrastructure.enumeration;

public enum EnumDDD {
	DDD11(11, "11", "São Paulo"), DDD12(12, "12", "São Paulo"), DDD13(13, "13", "São Paulo"), DDD14(14, "14", "São Paulo"), DDD15(15, "15", "São Paulo"), DDD16(
			16, "16", "São Paulo"), DDD17(17, "17", "São Paulo"), DDD18(18, "18", "São Paulo"), DDD19(19, "19", "São Paulo"), DDD21(21, "21", "Rio de Janeiro"), DDD22(
			22, "22", "Rio de Janeiro"), DDD24(24, "24", "Rio de Janeiro"), DDD27(27, "27", "Espírito Santo"), DDD28(28, "28", "Espírito Santo"), DDD31(31,
			"31", "Minas Gerais"), DDD32(32, "32", "Minas Gerais"), DDD33(33, "33", "Minas Gerais"), DDD34(34, "34", "Minas Gerais"), DDD35(35, "35",
			"Minas Gerais"), DDD37(37, "37", "Minas Gerais"), DDD38(38, "38", "Minas Gerais"), DDD41(41, "41", "Paraná"), DDD42(42, "42", "Paraná"), DDD43(43,
			"43", "Paraná"), DDD44(44, "44", "Paraná"), DDD45(45, "45", "Paraná"), DDD46(46, "46", "Paraná"), DDD47(47, "47", "Santa Catarina"), DDD48(48,
			"48", "Santa Catarina"), DDD49(49, "49", "Santa Catarina"), DDD51(51, "51", "Rio Grande do Sul"), DDD53(53, "53", "Rio Grande do Sul"), DDD54(54,
			"54", "Rio Grande do Sul"), DDD55(55, "55", "Rio Grande do Sul"), DDD61(61, "61", "Distrito Federal"), DDD62(62, "62", "Goiás"), DDD63(63, "63",
			"Tocantins"), DDD64(64, "64", "Goiás"), DDD65(65, "65", "Mato Grosso"), DDD66(66, "66", "Mato Grosso"), DDD67(67, "67", "Mato Grosso do Sul"), DDD68(
			68, "68", "Acre"), DDD69(69, "69", "Rondônia"), DDD71(71, "71", "Bahia"), DDD73(73, "73", "Bahia"), DDD74(74, "74", "Bahia"), DDD75(75, "75",
			"Bahia"), DDD77(77, "77", "Bahia"), DDD79(79, "79", "Sergipe"), DDD81(81, "81", "Pernambuco"), DDD82(82, "82", "Alagoas"), DDD83(83, "83",
			"Paraíba"), DDD84(84, "84", "Rio Grande do Norte"), DDD85(85, "85", "Ceará"), DDD86(86, "86", "Piauí"), DDD87(87, "87", "Pernambuco"), DDD88(88,
			"88", "Ceará"), DDD89(89, "89", "Piauí"), DDD91(91, "91", "Pará"), DDD92(92, "92", "Amazonas"), DDD93(93, "93", "Pará"), DDD94(94, "94", "Pará"), DDD95(
			95, "95", "Roraima"), DDD96(96, "96", "Amapá"), DDD97(97, "97", "Amazonas"), DDD98(98, "98", "Maranhão"), DDD99(99, "99", "Maranhão");

	private Integer ddd;
	private String descricao;
	private String estado;

	private EnumDDD(Integer ddd, String descricao, String estado) {
		this.ddd = ddd;
		this.descricao = descricao;
		this.estado = estado;
	}

	public Integer toInt() {
		return this.ddd;
	}

	public static EnumDDD valueOf(Integer c) {
		switch (c) {
		case 11:
			return DDD11;
		case 12:
			return DDD12;
		case 13:
			return DDD13;
		case 14:
			return DDD14;
		case 15:
			return DDD15;
		case 16:
			return DDD16;
		case 17:
			return DDD17;
		case 18:
			return DDD18;
		case 19:
			return DDD19;
		case 21:
			return DDD21;
		case 22:
			return DDD22;
		case 24:
			return DDD24;
		case 27:
			return DDD27;
		case 28:
			return DDD28;
		case 31:
			return DDD31;
		case 32:
			return DDD32;
		case 33:
			return DDD33;
		case 34:
			return DDD34;
		case 35:
			return DDD35;
		case 37:
			return DDD37;
		case 38:
			return DDD38;
		case 41:
			return DDD41;
		case 42:
			return DDD42;
		case 43:
			return DDD43;
		case 44:
			return DDD44;
		case 45:
			return DDD45;
		case 46:
			return DDD46;
		case 47:
			return DDD47;
		case 48:
			return DDD48;
		case 49:
			return DDD49;
		case 51:
			return DDD51;
		case 53:
			return DDD53;
		case 54:
			return DDD54;
		case 55:
			return DDD55;
		case 61:
			return DDD61;
		case 62:
			return DDD62;
		case 63:
			return DDD63;
		case 64:
			return DDD64;
		case 65:
			return DDD65;
		case 66:
			return DDD66;
		case 67:
			return DDD67;
		case 68:
			return DDD68;
		case 69:
			return DDD69;
		case 71:
			return DDD71;
		case 73:
			return DDD73;
		case 74:
			return DDD74;
		case 75:
			return DDD75;
		case 77:
			return DDD77;
		case 79:
			return DDD79;
		case 81:
			return DDD81;
		case 82:
			return DDD82;
		case 83:
			return DDD83;
		case 84:
			return DDD84;
		case 85:
			return DDD85;
		case 86:
			return DDD86;
		case 87:
			return DDD87;
		case 88:
			return DDD88;
		case 89:
			return DDD89;
		case 91:
			return DDD91;
		case 92:
			return DDD92;
		case 93:
			return DDD93;
		case 94:
			return DDD94;
		case 95:
			return DDD95;
		case 96:
			return DDD96;
		case 97:
			return DDD97;
		case 98:
			return DDD98;
		case 99:
			return DDD99;
		default:
			return null;
		}
	}

	@Override
	public String toString() {
		return ddd + descricao;
	}

	public String getValue() {
		return toString();
	}

	public Integer getDdd() {
		return ddd;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getEstado() {
		return estado;
	}

}
