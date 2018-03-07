package br.com.neainformatica.infrastructure.enumeration;

public enum EnumIdentificadorService {
	NASYNCATEND_BUSCA_ATENDIMENTOS_NOVOS(1, "", "Busca atendimentos novos"), 
	NASYNCATEND_ENVIAR_PROTOCOLOS_GERADOS(2, "","Devolve todos os protocolos gerados para que sejam atualizados no portal cliente"), 
	NASYNCATEND_ATUALIZAR_LISTA_CLIENTES(3, "rest/naCliente/atualizar", "Atualiza os dados com as informações dos clientes!"), 
	NASYNCATEND_ATUALIZAR_LISTA_SISTEMAS(4, "rest/naSistema/atualizar", "Atualiza os dados com as informações dos sistemas!"), 
	NASYNCATEND_BUSCAR_ATENDIMENTOS_PENDENTES(5, "", ""), 
	NASYNCATEND_ATUALIZAR_SITUACAO(6, "", ""), 
	NASYNCATEND_BLOQUEAR_USUARIOS_INATIVOS(7, "", ""),

	NAFFRAMEWORK_RECUPERAR_SENHA(50, "", "Recuperar Senha"),

	NAPORTAL_OBTER_METADATA_SINCRONISMO(101, "rest/infra/obterMetadataSincronismo", "Obter matadados para sincronismo de usuários");

	;

	private int id;
	private String pathAcesso;
	private String descricao;

	private EnumIdentificadorService(int id, String pathAcesso, String descricao) {
		this.id = id;
		this.pathAcesso = pathAcesso;
		this.descricao = descricao;
	}

	public static EnumIdentificadorService valueOf(int i) {
		switch (i) {
		case 1:
			return NASYNCATEND_BUSCA_ATENDIMENTOS_NOVOS;

		default:
			return null;
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

	public String getPathAcesso() {
		return pathAcesso;
	}

}