package br.com.neainformatica.infrastructure.dao.atualizabase;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.ToolsDBFirebird;
import br.com.neainformatica.infrastructure.dao.ToolsDBPostgre;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;

import javax.persistence.Query;
import java.util.List;

public class AtualizaBaseFramework01 extends AtualizaBasePadrao {

	private static final String SCHEMA_SEGURANCA = "seguranca";
	private static final String SCHEMA_ORGANOGRAMA = "organograma";
	private static final String SCHEMA_AUDITORIA = "auditoria";
	public static final String SCHEMA_PUBLIC = "public";

	public static final String TABLE_NA_USUARIO = "na_usuario";
	public static final String TABLE_NA_USUARIO_SISTEMA = "na_usuario_sistema";
	public static final String TABLE_NA_USUARIO_TOKEN = "na_usuario_token";
	public static final String TABLE_NA_CLIENTE = "na_cliente";
	public static final String TABLE_NA_SISTEMA = "na_sistema";
	public static final String TABLE_NA_GESTAO = "na_gestao";
	public static final String TABLE_NA_PERMISSAO = "na_permissao";

	public void scriptAtualizaFramework000001() throws Exception {

		db().addSchema(em, "seguranca");
		db().addSchema(em, "organograma");
		db().addSchema(em, "auditoria");

		db().createTable(em, SCHEMA_PUBLIC, "na_cliente");
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "VERSAO_BASE_FRAMEWORK", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "ID_BAIRRO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "BAIRRO_NOME", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "BRASAO", EnumTipoColumnBD.BLOB_BINARIO, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "CIDADE_NOME", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "ID_CIDADE_LOGRADOURO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "CNPJ", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGO_ADMINISTRACAO", EnumTipoColumnBD.BLOB_BINARIO, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_CEP", EnumTipoColumnBD.VARCHAR, 8);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_COMPLEMENTO", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_LATITUDE", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_LONGITUDE", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_LOTE", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_NOME", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_NUMERO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_QUADRA", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_SEM_NUMERO", EnumTipoColumnBD.VARCHAR, 1);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LOGRADOURO_TIPO_ZONA", EnumTipoColumnBD.VARCHAR, 1);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "NOME", EnumTipoColumnBD.VARCHAR, 100);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "ID_TIPO_BAIRRO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "ID_LOGRADOURO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "ID_TIPO_LOGRADOURO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "NOME_SECRETARIA", EnumTipoColumnBD.VARCHAR, 100);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LINK_BRASAO", EnumTipoColumnBD.VARCHAR, 200);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "LINK_LOGO", EnumTipoColumnBD.VARCHAR, 200);
		db().addCollumn(em, SCHEMA_PUBLIC, "na_cliente", "SITE_CLIENTE", EnumTipoColumnBD.VARCHAR, 200);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		// se for firebird removo o not null para não dar erro
		if (db() instanceof ToolsDBFirebird)
			executeSQL("update RDB$RELATION_FIELDS set RDB$NULL_FLAG = NULL	where (RDB$FIELD_NAME = 'SITUACAO_ACESSO') and	(RDB$RELATION_NAME = 'NA_CLIENTE')");

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_CLIENTE");

		db().createTable(em, SCHEMA_PUBLIC, "NA_SISTEMA");

		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "SISTEMA", EnumTipoColumnBD.VARCHAR, 150, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "STATUS", EnumTipoColumnBD.VARCHAR, 150, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "VERSAO_BASE", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_SISTEMA");

	}

	public void scriptAtualizaFramework000002() throws Exception {

		String script;

		if (db() instanceof ToolsDBPostgre) {
			script = "INSERT INTO PUBLIC.NA_SISTEMA (ID, SISTEMA, STATUS) " + "SELECT 1002, 'FRAMEWORK', 'A' "
					+ "WHERE NOT EXISTS (SELECT 1002, 'FRAMEWORK' FROM PUBLIC.NA_SISTEMA WHERE ID = 1002 ) ";
		} else {
			script = "UPDATE OR INSERT INTO NA_SISTEMA (ID, SISTEMA, STATUS) " + "VALUES (1002, 'FRAMEWORK', 'A') MATCHING (ID); ";
		}

		executeSQL(script);

		if (!(db().maxId(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID") > 0)) {

			if (db() instanceof ToolsDBPostgre)
				executeSQL("INSERT INTO PUBLIC.NA_CLIENTE (ID, NOME, VERSAO_BASE_FRAMEWORK)  VALUES (1, 'PREFEITURA MUNICIPAL DE MODELO', 1); ");
			else
				executeSQL("INSERT INTO NA_CLIENTE (ID, NOME, VERSAO_BASE_FRAMEWORK)  VALUES (1, 'PREFEITURA MUNICIPAL DE MODELO', 1); ");
		}

	}

	public void scriptAtualizaFramework000003() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "ID_USUARIO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "ID_SISTEMA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "DATA", EnumTipoColumnBD.DATE, null, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "CHAVE_VALIDACAO", EnumTipoColumnBD.VARCHAR, 50, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "INVALIDO", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "EMAIL", EnumTipoColumnBD.VARCHAR, 150, false, "N");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "PROTOCOLO", EnumTipoColumnBD.VARCHAR, 150, false, "N");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_ALTERACAO_SENHA");

	}

	public void scriptAtualizaFramework000004() throws Exception {

		db().createTable(em, SCHEMA_SEGURANCA, "NA_ATOR");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR", "NOME", EnumTipoColumnBD.VARCHAR, 255);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR", "TIPO", EnumTipoColumnBD.VARCHAR, 1);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_SEGURANCA, "SEQ_NA_ATOR");
	}

	public void scriptAtualizaFramework000005() throws Exception {

		db().createTable(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "ID_ATOR", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "ID_PERMISSAO", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "ACESSAR", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "ALTERAR", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "EXCLUIR", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "INSERIR", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_SEGURANCA, "SEQ_NA_ATOR_PERMISSAO");

	}

	public void scriptAtualizaFramework000006() throws Exception {

		db().createTable(em, SCHEMA_AUDITORIA, "NA_AUDITORIA");
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "CHAVE_REGISTRO", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "DATA_AUDITORIA", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "EXPORTACAO_DADOS", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "TABELA", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "USUARIO", EnumTipoColumnBD.VARCHAR, 30);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "TIPO_OPERACAO", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "ID_SISTEMA", EnumTipoColumnBD.INTEGER, null, true, null);

		db().addSequence(em, SCHEMA_AUDITORIA, "SEQ_NA_AUDITORIA");
	}

	public void scriptAtualizaFramework000007() throws Exception {

		db().createTable(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM");
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM", "CAMPO", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM", "VALOR_ANTERIOR", EnumTipoColumnBD.VARCHAR, 250);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM", "VALOR_NOVO", EnumTipoColumnBD.VARCHAR, 250);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM", "ID_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_AUDITORIA, "SEQ_NA_AUDITORIA_ITEM");
	}

	public void scriptAtualizaFramework000008() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_BAIRRO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_BAIRRO", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_BAIRRO", "ID_TIPO_BAIRRO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_BAIRRO", "ID_CIDADE", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_BAIRRO", "ABREVIACAO", EnumTipoColumnBD.VARCHAR, 40);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_BAIRRO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_BAIRRO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_BAIRRO");

	}

	public void scriptAtualizaFramework000009() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_CIDADE");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CIDADE", "ATIVO", EnumTipoColumnBD.VARCHAR, 1, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CIDADE", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CIDADE", "CODIGO_IBGE", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CIDADE", "ID_ESTADO", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CIDADE", "CAPITAL", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CIDADE", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CIDADE", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_CIDADE");

	}

	public void scriptAtualizaFramework000010() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_CLIENTE");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_BAIRRO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "BAIRRO_NOME", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "BRASAO", EnumTipoColumnBD.BLOB_BINARIO, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "CIDADE_NOME", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_CIDADE_LOGRADOURO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "CNPJ", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGO_ADMINISTRACAO", EnumTipoColumnBD.BLOB_BINARIO, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_CEP", EnumTipoColumnBD.VARCHAR, 8);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_COMPLEMENTO", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_LATITUDE", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_LONGITUDE", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_LOTE", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_NOME", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_NUMERO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_QUADRA", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LOGRADOURO_TIPO_ZONA", EnumTipoColumnBD.VARCHAR, 1);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "NOME", EnumTipoColumnBD.VARCHAR, 100);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_TIPO_BAIRRO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_LOGRADOURO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_TIPO_LOGRADOURO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "NOME_SECRETARIA", EnumTipoColumnBD.VARCHAR, 100);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LINK_BRASAO", EnumTipoColumnBD.VARCHAR, 200);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "LINK_LOGO", EnumTipoColumnBD.VARCHAR, 200);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "SITE_CLIENTE", EnumTipoColumnBD.VARCHAR, 200);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "VERSAO_BASE_FRAMEWORK", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_CLIENTE");
	}

	public void scriptAtualizaFramework000011() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO", "SISTEMA");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO", "ATUALIZA_BASE", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO", "ID_SISTEMA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_CONFIGURACAO");
	}

	public void scriptAtualizaFramework000012() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_ESTADO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "ATIVO", EnumTipoColumnBD.VARCHAR, 1, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "CODIGO_IBGE", EnumTipoColumnBD.VARCHAR, 2);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "NOME", EnumTipoColumnBD.VARCHAR, 250);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "SIGLA", EnumTipoColumnBD.VARCHAR, 2);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "ID_PAIS", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "NOME_FORMAL", EnumTipoColumnBD.VARCHAR, 200);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "ID_REGIAO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_ESTADO");
	}

	public void scriptAtualizaFramework000013() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_ESTADO_REGIAO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO_REGIAO", "CODIGO_IBGE", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO_REGIAO", "NOME", EnumTipoColumnBD.VARCHAR, 100, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO_REGIAO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO_REGIAO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_ESTADO_REGIAO");
	}

	public void scriptAtualizaFramework000014() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_LOGRADOURO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 255);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID_TIPO_LOGRADOURO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID_CIDADE", EnumTipoColumnBD.INTEGER, null, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID_BAIRRO_INI", EnumTipoColumnBD.INTEGER, null, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID_BAIRRO_FIM", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID_TIPO_CEP", EnumTipoColumnBD.INTEGER, null, false, "1");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "SIGLA_ESTADO", EnumTipoColumnBD.VARCHAR, 2, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "DESCRICAO_SEM_TIPO", EnumTipoColumnBD.VARCHAR, 150, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "DESCRICAO_SEM_ACENTO", EnumTipoColumnBD.VARCHAR, 150, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "CEP", EnumTipoColumnBD.VARCHAR, 8, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "COMPLEMENTO", EnumTipoColumnBD.VARCHAR, 100);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_LOGRADOURO");

	}

	public void scriptAtualizaFramework000015() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_NACIONALIDADE");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_NACIONALIDADE", "NOME", EnumTipoColumnBD.VARCHAR, 250);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_NACIONALIDADE", "IDPAIS", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_NACIONALIDADE", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_NACIONALIDADE", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_NACIONALIDADE");

	}

	public void scriptAtualizaFramework000016() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_PAIS");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PAIS", "NOME", EnumTipoColumnBD.VARCHAR, 250);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PAIS", "CODIGO_IBGE", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PAIS", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PAIS", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_PAIS");

	}

	public void scriptAtualizaFramework000017() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_PARAMETRO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 250);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "NOME", EnumTipoColumnBD.VARCHAR, 150, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "ID_NA_PARAMETRO_GRUPO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "ID_SISTEMA", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "CHAVE", EnumTipoColumnBD.VARCHAR, 60);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "SOMENTE_SUPORTE", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "NIVEL_USUARIO", EnumTipoColumnBD.INTEGER, null, true, "4");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "VALOR_FIXO", EnumTipoColumnBD.VARCHAR, 1, true, "S");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "SQL_CONSULTA", EnumTipoColumnBD.BLOB_TEXTO, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "ID_NA_TIPO_PARAMETRO", EnumTipoColumnBD.INTEGER, null, true, "5");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_PARAMETRO");
	}

	public void scriptAtualizaFramework000018() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "NOME", EnumTipoColumnBD.VARCHAR, 150, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "ID_SISTEMA", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "SOMENTE_SUPORTE", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "NIVEL_USUARIO", EnumTipoColumnBD.INTEGER, null, true, "4");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_PARAMETRO_GRUPO");
	}

	public void scriptAtualizaFramework000019() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "BINARIO", EnumTipoColumnBD.BLOB_BINARIO, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 150, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "VALOR", EnumTipoColumnBD.VARCHAR, 250);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "ID_NA_PARAMETRO", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_PARAMETRO_ITEM");
	}

	public void scriptAtualizaFramework000020() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "BINARIO", EnumTipoColumnBD.BLOB_BINARIO, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "CHAVE", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "VALOR", EnumTipoColumnBD.VARCHAR, 250);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "ID_NA_PARAMETRO", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "VALOR", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_PARAMETRO_VALOR");
	}

	public void scriptAtualizaFramework000021() throws Exception {

		db().createTable(em, SCHEMA_SEGURANCA, "NA_PERFIL");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL", "ID_ATOR", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL", "ID_SISTEMA", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 150, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL", "CADASTRO_PADRAO", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_SEGURANCA, "SEQ_NA_PERFIL");

	}

	public void scriptAtualizaFramework000022() throws Exception {

		db().createTable(em, SCHEMA_SEGURANCA, "NA_PERMISSAO");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "ATIVO", EnumTipoColumnBD.VARCHAR, 1, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 255, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "CHAVE", EnumTipoColumnBD.VARCHAR, 100, false, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "ID_SISTEMA", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "TIPO", EnumTipoColumnBD.VARCHAR, 1);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_SEGURANCA, "SEQ_NA_PERMISSAO");
	}

	public void scriptAtualizaFramework000023() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_SISTEMA");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "SISTEMA", EnumTipoColumnBD.VARCHAR, 150, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "STATUS", EnumTipoColumnBD.VARCHAR, 150, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "LINK_EXTERNO", EnumTipoColumnBD.VARCHAR, 200);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "VERSAO_BASE", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_SISTEMA");
	}

	public void scriptAtualizaFramework000024() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_TIPO_BAIRRO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_BAIRRO", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_BAIRRO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_BAIRRO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_TIPO_BAIRRO");
	}

	public void scriptAtualizaFramework000025() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_TIPO_CEP");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_CEP", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_CEP", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_CEP", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_TIPO_BAIRRO");
	}

	public void scriptAtualizaFramework000026() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO", "ABREVIATURA", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_TIPO_LOGRADOURO");
	}

	public void scriptAtualizaFramework000027() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 50);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_TIPO_LOGRADOURO");
	}

	public void scriptAtualizaFramework000028() throws Exception {

		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "1"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (1, 'Valor Binário'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "2"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (2, 'Data'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "3"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (3, 'Hora'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "4"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (4, 'Data Hora'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "5"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (5, 'Senha'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "6"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (6, 'Texto'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "7"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (7, 'Valor Fracionário'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "8"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (8, 'Valor Inteiro'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "9"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (9, 'Lista de Opções'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "10"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (10, 'Lista de Opções de Binários'); ");
		if (!db().existId(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "11"))
			executeSQL("INSERT INTO NA_TIPO_PARAMETRO (ID, DESCRICAO) VALUES (11, 'Consulta SQL'); ");
	}

	public void scriptAtualizaFramework000029() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, "NA_TIPO_PRONOME_TRATAMENTO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_PRONOME_TRATAMENTO", "NOME", EnumTipoColumnBD.VARCHAR, 50, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_PRONOME_TRATAMENTO", "EMPREGO", EnumTipoColumnBD.VARCHAR, 200, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_PRONOME_TRATAMENTO", "SEXO", EnumTipoColumnBD.VARCHAR, 1, true, "A");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_PRONOME_TRATAMENTO", "ABREVIACAO_SINGULAR", EnumTipoColumnBD.VARCHAR, 10, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_PRONOME_TRATAMENTO", "ABREVIACAO_PLURAL", EnumTipoColumnBD.VARCHAR, 15);

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_TIPO_PRONOME_TRATAMENTO");
	}

	public void scriptAtualizaFramework000030() throws Exception {

		db().createTable(em, SCHEMA_SEGURANCA, "NA_USUARIO");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "LOGIN", EnumTipoColumnBD.VARCHAR, 20, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "NOME", EnumTipoColumnBD.VARCHAR, 100, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "SENHA", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "CPF_CNPJ", EnumTipoColumnBD.VARCHAR, 14);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "EMAIL", EnumTipoColumnBD.VARCHAR, 150);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "ID_ATOR", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "ALTERAR_SENHA_PROXIMO_LOGIN", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "DATA_NASCIMENTO", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "DATA_ALTERACAO", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "ID_NA_USUARIO_SERVER", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "SENHA_NA_USUARIO_SERVER", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "DATA_ULTIMO_LOGIN", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_SEGURANCA, "SEQ_NA_USUARIO");

	}

	public void scriptAtualizaFramework000031() throws Exception {

		db().createTable(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "ID_PERFIL", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "ID_USUARIO", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_SEGURANCA, "SEQ_NA_USUARIO_PERFIL");
	}

	public void scriptAtualizaFramework000032() throws Exception {

		db().createTable(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "ID_SISTEMA", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "ID_USUARIO", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "NIVEL_USUARIO", EnumTipoColumnBD.INTEGER, null, true, "4");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_SEGURANCA, "SEQ_NA_USUARIO_SISTEMA");
	}

	public void scriptAtualizaFramework000033() throws Exception {
		db().addCheck(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "CHK_ALTERACAO_SENHA_INVALIDO", "INVALIDO In ('N', 'S')");
		db().addCheck(em, SCHEMA_PUBLIC, "NA_TIPO_PRONOME_TRATAMENTO", "CHK_TP_PRON_TRATAMENTO_SEXO", "SEXO In ('A', 'M', 'F')");
		db().addCheck(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "CHK_USUARIO_SISTEMA_NIVEL_USER", "NIVEL_USUARIO In (4, 8, 16, 32, 64)");
	}

	public void scriptAtualizaFramework000034() throws Exception {

		if (db().existCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "TIPO")) {
			executeSQL("UPDATE NA_PARAMETRO SET ID_NA_TIPO_PARAMETRO = 1  WHERE TIPO = 'B'; ");
			executeSQL("UPDATE NA_PARAMETRO SET ID_NA_TIPO_PARAMETRO = 2  WHERE TIPO = 'D'; ");
			executeSQL("UPDATE NA_PARAMETRO SET ID_NA_TIPO_PARAMETRO = 4  WHERE TIPO = 'H'; ");
			executeSQL("UPDATE NA_PARAMETRO	SET ID_NA_TIPO_PARAMETRO = 5  WHERE TIPO = 'S'; ");
			executeSQL("UPDATE NA_PARAMETRO	SET ID_NA_TIPO_PARAMETRO = 6  WHERE TIPO = 'T'; ");
			executeSQL("UPDATE NA_PARAMETRO SET ID_NA_TIPO_PARAMETRO = 7  WHERE TIPO = 'F'; ");
			executeSQL("UPDATE NA_PARAMETRO	SET ID_NA_TIPO_PARAMETRO = 8  WHERE TIPO = 'I'; ");
			executeSQL("UPDATE NA_PARAMETRO	SET ID_NA_TIPO_PARAMETRO = 9  WHERE TIPO = 'O'; ");
			executeSQL("UPDATE NA_PARAMETRO	SET ID_NA_TIPO_PARAMETRO = 10 WHERE TIPO = 'R'; ");
			executeSQL("UPDATE NA_PARAMETRO	SET ID_NA_TIPO_PARAMETRO = 11 WHERE TIPO = 'L'; ");
		}

	}

	public void scriptAtualizaFramework000035() throws Exception {

		if (db().existCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "TIPO"))
			db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "TIPO");
	}

	public void scriptAtualizaFramework000036() throws Exception {
		
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "UNQ_NA_PARAMETRO_CHAVE", "ID_SISTEMA, CHAVE");
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "UNQ_NA_PARAMETRO_NOME", "ID_SISTEMA, NOME");		
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "UNQ_NA_PARAMETRO_GRUPO_NOME", "ID_SISTEMA, NOME");
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "UNQ_NA_PARAMETRO_ITEM_DESCRICAO", "ID_NA_PARAMETRO, DESCRICAO");
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "UNQ_NA_PARAMETRO_VALOR_CHAVE", "ID_NA_PARAMETRO, CHAVE");
		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "NA_PERMISSAO_DESCRICAO_KEY", "ID_SISTEMA, DESCRICAO");
		//db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_USUARIO", "NA_USUARIO_LOGIN_KEY", "LOGIN");
		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "UNQ_NA_USUARIO_PERFIL", "ID_PERFIL, ID_USUARIO");
		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_PERFIL", "UNQ_NA_PERFIL", "DESCRICAO, ID_SISTEMA");

	}

	public void scriptAtualizaFramework000037() throws Exception {

		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "PK_NA_ALTERACAO_SENHA", "ID");
		db().addPrimaryKey(em, SCHEMA_SEGURANCA, "NA_ATOR", "PK_NA_ATOR", "ID");
		db().addPrimaryKey(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "NA_ATOR_PERMISSAO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "NA_AUDITORIA_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM", "NA_AUDITORIA_ITEM_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_BAIRRO", "NA_BAIRRO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_CIDADE", "NA_CIDADE_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_CLIENTE", "NA_CLIENTE_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO", "NA_CONFIGURACAO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_ESTADO", "NA_ESTADO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_ESTADO_REGIAO", "PK_NA_ESTADO_REGIAO", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "NA_LOGRADOURO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_NACIONALIDADE", "NA_NACIONALIDADE_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_PAIS", "NA_PAIS_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "NA_PARAMETRO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "NA_PARAMETRO_GRUPO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "NA_PARAMETRO_ITEM_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "NA_PARAMETRO_VALOR_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_SEGURANCA, "NA_PERFIL", "NA_PERFIL_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "NA_PERMISSAO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_SISTEMA", "NA_SISTEMA_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_TIPO_BAIRRO", "PK_NA_TIPO_BAIRRO", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_TIPO_CEP", "PK_NA_TIPO_CEP", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO", "NA_TIPO_LOGRADOURO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "PK_NA_TIPO_PARAMETRO", "ID");
		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_TIPO_PRONOME_TRATAMENTO", "PK_NA_TIPO_PRONOME_TRATAMENTO", "ID");
		db().addPrimaryKey(em, SCHEMA_SEGURANCA, "NA_USUARIO", "NA_USUARIO_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "NA_USUARIO_PERFIL_PKEY", "ID");
		db().addPrimaryKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "PK_NA_USUARIO_SISTEMA", "ID");

	}

	public void scriptAtualizaFramework000038() throws Exception {

		db().addIndex(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "IDX_LOGRADOURO_BAIRRO_FINAL", "ID_BAIRRO_FIM");
		db().addIndex(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "IDX_LOGRADOURO_BAIRRO_INICIO", "ID_BAIRRO_INI");
		db().addIndex(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "IDX_LOGRADOURO_CEP", "CEP");
		db().addIndex(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "IDX_LOGRADOURO_CIDADE", "ID_CIDADE");
		db().addIndex(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "IDX_LOGRADOURO_CID_DESCRICAO_SA", "ID_CIDADE, DESCRICAO_SEM_ACENTO");
		db().addIndex(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "IDX_LOGRADOURO_UF_DESCRICAO", "SIGLA_ESTADO, DESCRICAO");
		db().addIndex(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "IDX_LOGRADOURO_UF_DESCRICAO_SA", "SIGLA_ESTADO, DESCRICAO_SEM_ACENTO");
	}

	public void scriptAtualizaFramework000039() throws Exception {

		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "FK_NA_ALTERACAO_SENHA_SISTEMA", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "ID_SISTEMA");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "FK_NA_ALTERACAO_SENHA_USUARIO", SCHEMA_SEGURANCA, "NA_USUARIO", "ID", "ID_USUARIO");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "FK_NA_ATOR_PERMISSAOPERMISSAO", SCHEMA_SEGURANCA, "NA_PERMISSAO", "ID", "ID_PERMISSAO");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "FK_NA_ATOR_PERMISSAO_PERFIL", SCHEMA_SEGURANCA, "NA_ATOR", "ID", "ID_ATOR");
		db().addForeingKey(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "FK_NA_SISTEMA_AUDITORIA", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "ID_SISTEMA");
		db().addForeingKey(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM", "FK_NA_AUDITORIA_ITEM_AUDITORIA", SCHEMA_AUDITORIA, "NA_AUDITORIA", "ID", "ID_AUDITORIA");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_BAIRRO", "FK_NA_BAIRRO_NA_CIDADE", SCHEMA_PUBLIC, "NA_CIDADE", "ID", "ID_CIDADE");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_BAIRRO", "FK_NA_BAIRRO_TIPO_BAIRRO", SCHEMA_PUBLIC, "NA_TIPO_BAIRRO", "ID", "ID_TIPO_BAIRRO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_CIDADE", "FK_NA_CIDADE_ESTADO", SCHEMA_PUBLIC, "NA_ESTADO", "ID", "ID_ESTADO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_CLIENTE", "FK_NA_CLIENTE_CIDADE", SCHEMA_PUBLIC, "NA_CIDADE", "ID", "ID_CIDADE_LOGRADOURO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_CLIENTE", "FK_NA_CLIENTE_LOGRADOURO", SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID", "ID_LOGRADOURO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_CLIENTE", "FK_NA_CLIENTE_TIPO_LOGRADOURO", SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO", "ID", "ID_TIPO_LOGRADOURO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO", "FK_NA_CONFIGURACAO_SISTEMA", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "ID_SISTEMA");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_ESTADO", "FK_NA_ESTADO_PAIS", SCHEMA_PUBLIC, "NA_PAIS", "ID", "ID_PAIS");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_ESTADO", "FK_NA_ESTADO_REGIAO", SCHEMA_PUBLIC, "NA_ESTADO_REGIAO", "ID", "ID_REGIAO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "FK_NA_LOGRADOURO_BAIRRO_FIM", SCHEMA_PUBLIC, "NA_BAIRRO", "ID", "ID_BAIRRO_FIM");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "FK_NA_LOGRADOURO_BAIRRO_INI", SCHEMA_PUBLIC, "NA_BAIRRO", "ID", "ID_BAIRRO_INI");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "FK_NA_LOGRADOURO_CIDADE", SCHEMA_PUBLIC, "NA_CIDADE", "ID", "ID_CIDADE");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "FK_NA_LOGRADOURO_TIPOLOGRADOURO", SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO", "ID", "ID_TIPO_LOGRADOURO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "FK_NA_LOGRADOURO_TIPO_CEP", SCHEMA_PUBLIC, "NA_TIPO_CEP", "ID", "ID_TIPO_CEP");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_NACIONALIDADE", "FK_NA_NACIONALIDADE_PAIS", SCHEMA_PUBLIC, "NA_PAIS", "ID", "IDPAIS");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "FK_NA_PARAMETRO_PARAMETRO_GRUPO", SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "ID", "ID_NA_PARAMETRO_GRUPO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "FK_NA_PARAMETRO_SISTEMA", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "ID_SISTEMA");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "FK_NA_PARAMETRO_TIPO_PARAMETRO", SCHEMA_PUBLIC, "NA_TIPO_PARAMETRO", "ID", "ID_NA_TIPO_PARAMETRO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "FK_NA_PARAMETRO_GRUPO_SISTEMA", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "ID_SISTEMA");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "FK_NA_PARAMETRO_ITEM_PARAMETRO", SCHEMA_PUBLIC, "NA_PARAMETRO", "ID", "ID_NA_PARAMETRO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "FK_NA_PARAMETRO_VALOR_PARAMETRO", SCHEMA_PUBLIC, "NA_PARAMETRO", "ID", "ID_NA_PARAMETRO");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_PERFIL", "FK_NA_PERFIL_NA_ATOR", SCHEMA_SEGURANCA, "NA_ATOR", "ID", "ID_ATOR");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_PERFIL", "FK_NA_PERFIL_SISTEMA", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "ID_SISTEMA");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "FK_NA_PERMISSAO_SISTEMA", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "ID_SISTEMA");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_USUARIO", "FK_NA_USUARIO_NA_ATOR", SCHEMA_SEGURANCA, "NA_ATOR", "ID", "ID_ATOR");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "FK_NA_USUARIO_PERFIL_PERFIL", SCHEMA_SEGURANCA, "NA_PERFIL", "ID", "ID_PERFIL");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "FK_NA_USUARIO_PERFIL_USUARIO", SCHEMA_SEGURANCA, "NA_USUARIO", "ID", "ID_USUARIO");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "FK_NA_USUARIO_SISTEMA_SISTEMA", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "ID_SISTEMA");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "FK_NA_USUARIO_SISTEMA_USUARIO", SCHEMA_SEGURANCA, "NA_USUARIO", "ID", "ID_USUARIO");

		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "UNQ_NA_USUARIO_SISTEMA", "ID_SISTEMA,ID_USUARIO");

	}

	public void scriptAtualizaFramework000040() throws Exception {

		if (!db().existTable(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL"))
			db().createTable(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL");

		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "ID", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 100);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "DATA_INICIO_VIGENCIA", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "TIPO_DOCUMENTO_DEFINICAO", EnumTipoColumnBD.VARCHAR, 100);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "DOCUMENTO_NUMERO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "DOCUMENTO_ANO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "DOCUMENTO_DATA", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "ID_USUARIO", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_ORGANOGRAMA, "SEQ_ESTRUTURA_ORGANIZACIONAL");

		db().addPrimaryKey(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "ESTRUTURA_ORGANIZACIONAL_PKEY", "ID");
	}

	public void scriptAtualizaFramework000041() throws Exception {

		if (!db().existTable(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL"))
			db().createTable(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL");

		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL", "ID", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL", "ID_ESTRUTURA_ORGANIZACIONAL", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL", "NIVEL", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL", "ID_USUARIO", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL", "TAMANHO", EnumTipoColumnBD.INTEGER, null, true, null);

		db().addSequence(em, SCHEMA_ORGANOGRAMA, "SEQ_ESTRUTURA_NIVEL");

		db().addPrimaryKey(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL", "ESTRUTURA_NIVEL_PKEY", "ID");
	}

	public void scriptAtualizaFramework000042() throws Exception {

		if (!db().existTable(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA"))
			db().createTable(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA");

		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "ID", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "ID_ESTRUTURA_NIVEL", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "ID_ESTRUTURA_ORGANIZACIONAL", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "ID_ORGANOGRAMA_SUPERIOR", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "DESCRICAO", EnumTipoColumnBD.VARCHAR, 100);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "CODIGO", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "MASCARA", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "SIGLA", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "ID_USUARIO", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_ORGANOGRAMA, "SEQ_ORGANOGRAMA");

		db().addPrimaryKey(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "ORGANOGRAMA_PKEY", "ID");

	}

	public void scriptAtualizaFramework000043() throws Exception {

		if (!db().existTable(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO"))
			db().createTable(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO");

		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO", "ID", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO", "ID_ORGANOGRAMA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO", "DATA_INICIO_VIGENCIA", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO", "ID_LOCAL_FISICO_PROTOCOLO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO", "ID_LOCAL_FISICO_FROTAS", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO", "ID_LOTACAO_FUNCIONARIO_FR", EnumTipoColumnBD.INTEGER, null);

		db().addSequence(em, SCHEMA_ORGANOGRAMA, "SEQ_ORGANOGRAMA_LOCAL_FISICO");

		db().addPrimaryKey(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO", "ORGANOGRAMA_LOCAL_FISICO_PKEY", "ID");
	}

	public void scriptAtualizaFramework000044() throws Exception {

		db().addForeingKey(em, SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL", "FK_ESTRTUT_ORGANIZACIONAL_ORG", SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "ID",
				"ID_ESTRUTURA_ORGANIZACIONAL");

		db().addForeingKey(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "EST_ORGANIZACIONAL_ORGANO_FK", SCHEMA_ORGANOGRAMA, "ESTRUTURA_ORGANIZACIONAL", "ID",
				"ID_ESTRUTURA_ORGANIZACIONAL");

		db().addForeingKey(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "ORG_NIVEL_ORGANOGRAMA_FK", SCHEMA_ORGANOGRAMA, "ESTRUTURA_NIVEL", "ID", "ID_ESTRUTURA_NIVEL");

		db().addForeingKey(em, SCHEMA_ORGANOGRAMA, "ORGANOGRAMA_LOCAL_FISICO", "FK_LOCAL_FISICO_ORAG", SCHEMA_ORGANOGRAMA, "ORGANOGRAMA", "ID", "ID_ORGANOGRAMA");
	}

	public void scriptAtualizaFramework000045() throws Exception {

		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PAIS", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_ALTERACAO_SENHA", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_BAIRRO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_CIDADE", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_CONFIGURACAO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_ESTADO_REGIAO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_LOGRADOURO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_NACIONALIDADE", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_BAIRRO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_CEP", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_TIPO_LOGRADOURO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "ID_NA_USUARIO");
		db().dropCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "ID_NA_USUARIO");

	}

	public void scriptAtualizaFramework000046() throws Exception {

		db().dropUniqueKey(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "", "NA_PERMISSAO_DESCRICAO_KEY", new String[] { "DESCRICAO" });

		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "SITUACAO_ACESSO", EnumTipoColumnBD.VARCHAR, 40, false, ".");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "SITUACAO_ACESSO", EnumTipoColumnBD.VARCHAR, 40, false, ".");
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "SITUACAO_ACESSO", EnumTipoColumnBD.VARCHAR, 40, false, ".");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "STATUS");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "STATUS");
		db().dropCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "STATUS");

	}

	public void scriptAtualizaFramework000047() throws Exception {
		if (db() instanceof ToolsDBPostgre) {
			executeSQL("UPDATE PUBLIC.NA_CLIENTE SET SITUACAO_ACESSO  = 'C99BA4FA30BC754B094AF0EA657FAFD0'");
			executeSQL("UPDATE PUBLIC.NA_SISTEMA SET SITUACAO_ACESSO  = '91574D199CC759A88C67AB0CEEA5ED26'");

			if (db().existTable(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA"))
				executeSQL("UPDATE SEGURANCA.NA_USUARIO_SISTEMA SET SITUACAO_ACESSO  = 'BE7616CD429B741C5466A191FE29B681'");
		} else {
			executeSQL("UPDATE NA_CLIENTE SET SITUACAO_ACESSO  = 'C99BA4FA30BC754B094AF0EA657FAFD0'");
			executeSQL("UPDATE NA_SISTEMA SET SITUACAO_ACESSO  = '91574D199CC759A88C67AB0CEEA5ED26'");
			if (db().existTable(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA"))
				executeSQL("UPDATE NA_USUARIO_SISTEMA SET SITUACAO_ACESSO  = 'BE7616CD429B741C5466A191FE29B681'");
		}

	}

	public void scriptAtualizaFramework000048() throws Exception {

		if (db() instanceof ToolsDBPostgre)
			executeSQL("UPDATE SEGURANCA.NA_PERMISSAO SET CHAVE = DESCRICAO WHERE CHAVE IS NULL;");
		else
			executeSQL("UPDATE NA_PERMISSAO SET CHAVE = DESCRICAO WHERE CHAVE IS NULL;");

		db().dropAllUniqueKeys(em, SCHEMA_SEGURANCA, "NA_PERMISSAO");
		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "UNQ_NA_PERMISSAO", "ID_SISTEMA, CHAVE");

		db().atualizaValorSequence(em, SCHEMA_SEGURANCA, "NA_ATOR", "SEQ_NA_ATOR");
		db().atualizaValorSequence(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "SEQ_NA_ATOR_PERMISSAO");
		db().atualizaValorSequence(em, SCHEMA_SEGURANCA, "NA_PERFIL", "SEQ_NA_PERFIL");
		db().atualizaValorSequence(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "SEQ_NA_PERMISSAO");
		db().atualizaValorSequence(em, SCHEMA_SEGURANCA, "NA_USUARIO", "SEQ_NA_USUARIO");
		db().atualizaValorSequence(em, SCHEMA_SEGURANCA, "NA_USUARIO_PERFIL", "SEQ_NA_USUARIO_PERFIL");
	}

	public void scriptAtualizaFramework000049() throws Exception {

		if (db() instanceof ToolsDBPostgre)
			executeSQL("DELETE FROM SEGURANCA.NA_USUARIO_SISTEMA WHERE ID_SISTEMA IS NULL OR ID_USUARIO IS NULL;");
		else {
			executeSQL("DELETE FROM NA_USUARIO_SISTEMA WHERE ID_SISTEMA IS NULL OR ID_USUARIO IS NULL;");
			executeSQL("update RDB$RELATION_FIELDS set RDB$NULL_FLAG = 1 where ((RDB$FIELD_NAME = 'ID_SISTEMA') OR (RDB$FIELD_NAME = 'ID_USUARIO')) and (RDB$RELATION_NAME = 'NA_USUARIO_SISTEMA');");
		}
	}

	public void scriptAtualizaFramework000050() throws Exception {
		if (db() instanceof ToolsDBPostgre) {
			executeSQL("delete from seguranca.NA_ATOR_PERMISSAO AP where (select count(*) from seguranca.NA_PERMISSAO P where (coalesce(upper(P.CHAVE), '') = 'NA_SISTEMA' or coalesce(upper(P.CHAVE), '') = 'SISTEMA' or coalesce(P.CHAVE, '') = '') and AP.ID_PERMISSAO = P.ID) > 0; ");
			if (db().existTable(em, SCHEMA_SEGURANCA, "NA_PERFIL_PERMISSAO")) {
				db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL_PERMISSAO", "ID_PERMISSAO", EnumTipoColumnBD.INTEGER, null);
				executeSQL("DELETE FROM seguranca.NA_PERFIL_PERMISSAO PP WHERE PP.ID_PERMISSAO IN (SELECT P.ID FROM seguranca.NA_PERMISSAO P WHERE P.CHAVE IS NULL OR UPPER (P.CHAVE) = 'SISTEMA' OR UPPER (P.CHAVE) = 'NA_SISTEMA'); ");
			}
			executeSQL("delete from seguranca.NA_PERMISSAO P where P.CHAVE is null or upper(P.CHAVE) = 'SISTEMA' or upper(P.CHAVE) = 'NA_SISTEMA'; ");
		} else {
			executeSQL("delete from NA_ATOR_PERMISSAO AP where (select count(*) from NA_PERMISSAO P where (coalesce(upper(P.CHAVE), '') = 'NA_SISTEMA' or coalesce(upper(P.CHAVE), '') = 'SISTEMA' or coalesce(P.CHAVE, '') = '') and AP.ID_PERMISSAO = P.ID) > 0; ");
			if (db().existTable(em, SCHEMA_SEGURANCA, "NA_PERFIL_PERMISSAO")) {
				db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL_PERMISSAO", "ID_PERMISSAO", EnumTipoColumnBD.INTEGER, null);
				executeSQL("DELETE FROM NA_PERFIL_PERMISSAO PP WHERE PP.ID_PERMISSAO IN (SELECT P.ID FROM NA_PERMISSAO P WHERE P.CHAVE IS NULL OR UPPER (P.CHAVE) = 'SISTEMA' OR UPPER (P.CHAVE) = 'NA_SISTEMA'); ");
			}
			executeSQL("delete from NA_PERMISSAO P where P.CHAVE is null or upper(P.CHAVE) = 'SISTEMA' or upper(P.CHAVE) = 'NA_SISTEMA'; ");
		}
	}

	public void scriptAtualizaFramework000051() throws Exception {

		db().addCheck(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "CHK_NA_PARAMETRO_NIVEL_USUARIO", "NIVEL_USUARIO In (4, 8, 16, 32, 64)");
		db().addCheck(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "CHK_NA_PARAMETRO_GRUPO_NIVEL_US", "NIVEL_USUARIO In (4, 8, 16, 32, 64)");

		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "ID_NA_PARAMETRO_ITEM", EnumTipoColumnBD.INTEGER, null);
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "FK_NA_PARAMETRO_VALOR_ITEM", SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "ID", "ID_NA_PARAMETRO_ITEM");

		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "SQL_TABELA", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "SQL_CAMPO_CHAVE", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "SQL_CAMPO_DESCRICAO", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "VALOR_DESCRICAO", EnumTipoColumnBD.VARCHAR, 250);
	}

	public void scriptAtualizaFramework000052() throws Exception {

		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "BINARIO_EXTENSAO", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "BINARIO_TIPO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "TAMANHO_VALOR_MINIMO", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "TAMANHO_VALOR_MAXIMO", EnumTipoColumnBD.VARCHAR, 10);

		db()
				.addDescriptionColumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "TAMANHO_VALOR_MINIMO",
						"Tamanho, Valor ou Data Minima para este parametro. se for data utilizar o padrao dd/MM/aaaa, se for float utilizar ponto como separador decimal, se hora hh:mm:ss");
		db()
				.addDescriptionColumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "TAMANHO_VALOR_MAXIMO",
						"Tamanho, Valor ou Data Maxima para este parametro. se for data utilizar o padrao dd/MM/aaaa, se for float utilizar ponto como separador decimal, se hora hh:mm:ss");

	}

	public void scriptAtualizaFramework000053() throws Exception {
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "SOMENTE_SUPORTE");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "VISIVEL");
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "SOMENTE_SUPORTE");
	}

	public void scriptAtualizaFramework000054() throws Exception {
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "ORIGEM_ALTERACAO", EnumTipoColumnBD.VARCHAR, 1);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "ID_NA_USUARIO_SERVER", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "SOMENTE_PERFIL", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ID_CLIENTE_NEA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "ID_NA_GESTAO", EnumTipoColumnBD.INTEGER, null);

		if (!db().existTable(em, SCHEMA_PUBLIC, "NA_GESTAO"))
			executaScript();

		if (!db().existTable(em, SCHEMA_PUBLIC, "NA_FAQ_TAG"))
			executaScript("scriptAtualizaFramework000054-1.sql");

		if (!db().existTable(em, SCHEMA_PUBLIC, "NA_FAQ_FORM_TAG"))
			executaScript("scriptAtualizaFramework000054-2.sql");

		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_SISTEMA", "FK_NA_SISTEMA_GESTAO", SCHEMA_PUBLIC, "NA_GESTAO", "ID", "ID_NA_GESTAO");

		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_GESTAO");
		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_FAQ_TAG");
		db().addSequence(em, SCHEMA_PUBLIC, "SEQ_NA_FAQ_FORM_TAG");

		db().addIndex(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "IDXNA_AUDITORIA_CHAVE", "CHAVE_REGISTRO");
		db().addIndex(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "IDXNA_AUDITORIA_TABELA", "TABELA");
		db().addIndex(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "IDXNA_AUDITORIA_USUARIO", "USUARIO");

		if (!db().existTable(em, SCHEMA_AUDITORIA, "na_auditoria_tabela")) {
			if (db() instanceof ToolsDBPostgre)
				executeSQL("CREATE TABLE auditoria.na_auditoria_tabela (id BIGINT NOT NULL ) ;");
			else
				executeSQL("CREATE TABLE NA_AUDITORIA_TABELA (ID BIGINT NOT NULL);");
		}

		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_TABELA", "NOME", EnumTipoColumnBD.VARCHAR, 50, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_TABELA", "NOME_AMIGAVEL", EnumTipoColumnBD.VARCHAR, 100, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_TABELA", "AUDIT_INSERT", EnumTipoColumnBD.VARCHAR, 1, true, "S");
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_TABELA", "AUDIT_UPDATE", EnumTipoColumnBD.VARCHAR, 1, true, "S");
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_TABELA", "AUDIT_DELETE", EnumTipoColumnBD.VARCHAR, 1, true, "S");
		db().addPrimaryKey(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_TABELA", "PK_NA_AUDITORIA_TABELA", "ID");
		db().addUniqueKey(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_TABELA", "UNQ_NA_AUDITORIA_TABELA", "NOME");

		db().createTable(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_CAMPO");
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_CAMPO", "TABELA", EnumTipoColumnBD.VARCHAR, 50, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_CAMPO", "CAMPO", EnumTipoColumnBD.VARCHAR, 50, true, null);

		db().addPrimaryKey(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_CAMPO", "PK_NA_AUDITORIA_CAMPO", "ID");
		db().addUniqueKey(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_CAMPO", "UNQ_NA_AUDITORIA_CAMPO", "TABELA, CAMPO");

		db().addSequence(em, SCHEMA_AUDITORIA, "SEQ_NA_AUDITORIA_CAMPO");

	}

	public void scriptAtualizaFramework000055() throws Exception {

		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "VALOR_PADRAO", EnumTipoColumnBD.VARCHAR, 250, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "SQL_WHERE", EnumTipoColumnBD.VARCHAR, 250, false, null);

		db().dropUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "", "UNQ_NA_PARAMETRO_VALOR_CHAVE", new String[] { "id_na_parametro", "chave" });
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "CHAVE2", EnumTipoColumnBD.VARCHAR, 20, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "DESCRICAO2", EnumTipoColumnBD.VARCHAR, 20, false, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "VALOR_TEXTO", EnumTipoColumnBD.BLOB_TEXTO, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "VALOR_DESCRICAO_ANTERIOR", EnumTipoColumnBD.VARCHAR, 250, false, null);

		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "UNQ_NA_PARAMETRO_VALOR_CHAVE", "ID_NA_PARAMETRO, CHAVE, CHAVE2");

	}

	public void scriptAtualizaFramework000056() throws Exception {

		if (db().existTable(em, SCHEMA_SEGURANCA, "NA_PERFIL_PERMISSAO"))
			db().dropTable(em, SCHEMA_SEGURANCA, "NA_PERFIL_PERMISSAO");
	}

	public void scriptAtualizaFramework000057() throws Exception {
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "ID_NA_CLIENTE", EnumTipoColumnBD.INTEGER, null, false, "1");

		db().dropUniqueKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "UNQ_NA_USUARIO_SISTEMA", "", new String[] { "ID_SISTEMA", "ID_USUARIO" });
		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "UNQ_NA_USUARIO_SISTEMA", "ID_NA_CLIENTE, ID_SISTEMA, ID_USUARIO");

		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "FK_NA_USUARIO_SISTEMA_CLIENTE", SCHEMA_PUBLIC, "NA_CLIENTE", "ID", "ID_NA_CLIENTE");
	}

	public void scriptAtualizaFramework000058() throws Exception {
		db().dropCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "VERSAO_BASE_FRAMEWORK");
	}

	public void scriptAtualizaFramework000059() throws Exception {

		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERFIL", "ID_NA_CLIENTE", EnumTipoColumnBD.INTEGER, null, true, "1");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_PERFIL", "FK_NA_PERFIL_CLIENTE", SCHEMA_PUBLIC, "NA_CLIENTE", "ID", "ID_NA_CLIENTE");

		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "ID_NA_CLIENTE", EnumTipoColumnBD.INTEGER, null, true, "1");
		db().addForeingKey(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "FK_NA_ATOR_PERMISSAO_CLIENTE", SCHEMA_PUBLIC, "NA_CLIENTE", "ID", "ID_NA_CLIENTE");
	}

	public void scriptAtualizaFramework000060() throws Exception {
		db().dropAllUniqueKeys(em, SCHEMA_SEGURANCA, "NA_ATOR");
	}

	public void scriptAtualizaFramework000061() throws Exception {
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "ATIVO", EnumTipoColumnBD.VARCHAR, 1, true, "S");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "TIPO_AMBIENTE", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "TIPO_BANCO_DADOS", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "ID_VERSAO_ATUAL", EnumTipoColumnBD.INTEGER, null);

		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "ATIVO", EnumTipoColumnBD.VARCHAR, 1, true, "S");

		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "ID_VERSAO_ATUAL", EnumTipoColumnBD.INTEGER, null);

	}

	public void scriptAtualizaFramework000062() throws Exception {

		if (db() instanceof ToolsDBPostgre)
			executeSQL("UPDATE SEGURANCA.NA_USUARIO_SISTEMA SET ID_NA_CLIENTE = 1 WHERE ID_NA_CLIENTE IS NULL;");
		else
			executeSQL("UPDATE NA_USUARIO_SISTEMA SET ID_NA_CLIENTE = 1 WHERE ID_NA_CLIENTE IS NULL;");

	}

	public void scriptAtualizaFramework000063() throws Exception {

		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "telefone_ddd", EnumTipoColumnBD.VARCHAR, 2);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "telefone_numero", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "uf", EnumTipoColumnBD.VARCHAR, 2);

	}

	public void scriptAtualizaFramework000064() throws Exception {
		db().dropAllUniqueKeys(em, SCHEMA_SEGURANCA, "NA_PERFIL");
		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_PERFIL", "UNQ_NA_PERFIL", "ID_NA_CLIENTE, ID_SISTEMA, DESCRICAO");
	}

	public void scriptAtualizaFramework000065() throws Exception {
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_CLIENTE", "id_estado_logradouro", EnumTipoColumnBD.INTEGER, null);
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_CLIENTE", "FK_NA_CLIENTE_ESTADO", SCHEMA_PUBLIC, "NA_ESTADO", "ID", "ID_ESTADO_LOGRADOURO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_CLIENTE", "FK_NA_CLIENTE_TIPO_BAIRRO", SCHEMA_PUBLIC, "NA_TIPO_BAIRRO", "ID", "ID_TIPO_BAIRRO");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_CLIENTE", "FK_NA_CLIENTE_BAIRRO", SCHEMA_PUBLIC, "NA_BAIRRO", "ID", "ID_BAIRRO");
	}

	public void scriptAtualizaFramework000066() throws Exception {
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "ATIVO", EnumTipoColumnBD.VARCHAR, 1, true, "S");
	}

	public void scriptAtualizaFramework000067() throws Exception {
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "telefone_ddd", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "telefone_numero", EnumTipoColumnBD.VARCHAR, 10);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "telefone_ramal", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "origem_sincronismo", EnumTipoColumnBD.VARCHAR, 1, true, "C");
	}

	public void scriptAtualizaFramework000068() throws Exception {

		db().createTable(em, SCHEMA_PUBLIC, TABLE_NA_GESTAO);

		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_GESTAO, "DESCRICAO", EnumTipoColumnBD.VARCHAR, 250, true, null);
		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_GESTAO, "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_GESTAO, "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		executeSQL("UPDATE na_sistema SET tipo_ambiente=1 WHERE tipo_ambiente is null;");
		executeSQL("UPDATE na_sistema SET tipo_banco_dados = 1 WHERE tipo_banco_dados is null;");
	}

	public void scriptAtualizaFramework000069() throws Exception {
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "origem_psw", EnumTipoColumnBD.VARCHAR, 1, true, "N");
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "id_old", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "login_old", EnumTipoColumnBD.VARCHAR, 20);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "matricula", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "rubrica", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "dias_inatividade", EnumTipoColumnBD.INTEGER, null, true, "999");
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "dias_expiracao", EnumTipoColumnBD.INTEGER, null, true, "999");
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "status", EnumTipoColumnBD.VARCHAR, 1, true, "A");
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "status_barra_atalho_principal", EnumTipoColumnBD.INTEGER, null, true, "1");
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "idusuario_herdar", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "setor", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "cargo", EnumTipoColumnBD.VARCHAR, 50);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "senha_internet", EnumTipoColumnBD.VARCHAR, 15);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO, "nivelusuario", EnumTipoColumnBD.INTEGER, null);

		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_CLIENTE, "origem_sincronismo", EnumTipoColumnBD.VARCHAR, 1, true, "C");
		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_CLIENTE, "data_alteracao", EnumTipoColumnBD.DATE, null, true, "now()");
		db().addUniqueKey(em, SCHEMA_PUBLIC, TABLE_NA_CLIENTE, "unq_na_cliente_cnpj", "CNPJ");
		db().addUniqueKey(em, SCHEMA_PUBLIC, TABLE_NA_CLIENTE, "unq_na_cliente_id_cliente_nea", "id_cliente_nea");

		db().setCollumnDefaultValue(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "tipo_ambiente", 1);
		db().setCollumnDefaultValue(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "tipo_banco_dados", 1);
		db().setCollumnNotNull(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "tipo_ambiente");
		db().setCollumnNotNull(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "tipo_banco_dados");

		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "versao_sistema", EnumTipoColumnBD.VARCHAR, 20, true, "1.0.0.0");
		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "origem_sincronismo", EnumTipoColumnBD.VARCHAR, 1, true, "C");
		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "data_alteracao", EnumTipoColumnBD.DATE, null, true, "now()");
		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "base_principal", EnumTipoColumnBD.VARCHAR, 1, true, "N");

		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_SISTEMA, "data_alteracao", EnumTipoColumnBD.DATE, null, true, "now()");
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_SISTEMA, "origem_sincronismo", EnumTipoColumnBD.VARCHAR, 1, true, "C");
	}

	public void scriptAtualizaFramework000070() throws Exception {
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_SISTEMA", "PERMITE_BLOQUEIO", EnumTipoColumnBD.VARCHAR, false, "S", 1, null);
	}

	public void scriptAtualizaFramework000071() throws Exception {
		db().alterTypeCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM", "valor_anterior", EnumTipoColumnBD.VARCHAR, 1000, null);
		db().alterTypeCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA_ITEM", "valor_novo", EnumTipoColumnBD.VARCHAR, 1000, null);
	}

	public void scriptAtualizaFramework000072() throws Exception {
		db().dropTriggerAuditoria(em, SCHEMA_SEGURANCA, "NA_USUARIO");
		db().alterTypeCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO", "SENHA", EnumTipoColumnBD.VARCHAR, 50, null);
		db().dropAllUniqueKeys(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO");
		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_ATOR_PERMISSAO", "UNQ_NA_ATOR_PERMISSAO", "ID_ATOR, ID_PERMISSAO, ID_NA_CLIENTE");
	}

	public void scriptAtualizaFramework000073() throws Exception {

		db().createTable(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_TOKEN);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_TOKEN, "DATA_ULTIMO_ACESSO", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_TOKEN, "ID_USUARIO", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_TOKEN, "TOKEN", EnumTipoColumnBD.VARCHAR, 200);
		db().addSequence(em, SCHEMA_SEGURANCA, "SEQ_NA_USUARIO_TOKEN");
	}

	public void scriptAtualizaFramework000074() throws Exception {
		db().addUniqueKey(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_TOKEN, "UNQ_NA_USUARIO_TOKEN", "ID_USUARIO");
		db().addForeingKey(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_TOKEN, "FK_NA_USUARIO_TOKEN_USUARIO", SCHEMA_SEGURANCA, "NA_USUARIO", "ID", "ID_USUARIO");
		db().addPrimaryKey(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_TOKEN, "PK_NA_USUARIO_TOKEN", "ID");
	}

	public void scriptAtualizaFramework000075() throws Exception {
		db().createTable(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "id_na_sistema", EnumTipoColumnBD.INTEGER, null, true, "");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "codigo_tarefa", EnumTipoColumnBD.INTEGER, null, true, "");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "nome", EnumTipoColumnBD.VARCHAR, 100, true, "");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "descricao", EnumTipoColumnBD.VARCHAR, 100, false, "");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "expressao_cron", EnumTipoColumnBD.VARCHAR, 50, true, "");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "ativo", EnumTipoColumnBD.VARCHAR, 1, true, "");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "id_na_usuario_auditoria", EnumTipoColumnBD.INTEGER, null, false, "");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "id_na_sistema_auditoria", EnumTipoColumnBD.INTEGER, null, false, "");

		db().addPrimaryKey(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "PK_NA_AGENDAMENTO", "ID");
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "unq_na_agendamento", "id_na_sistema, codigo_tarefa");
		db().addForeingKey(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "fk_na_agendamento_sistema", SCHEMA_PUBLIC, "NA_SISTEMA", "ID", "id_na_sistema");
		db().addSequence(em, SCHEMA_PUBLIC, "seq_na_agendamento");
	}

	public void scriptAtualizaFramework000076() throws Exception {
		db().dropTriggerAuditoria(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO");
		db().dropAllUniqueKeys(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO");
		db().alterTypeCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "codigo_tarefa", EnumTipoColumnBD.VARCHAR, 100, null);
	}

	public void scriptAtualizaFramework000077() throws Exception {
		db().dropTriggerAuditoria(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO");
		db().dropAllUniqueKeys(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO");
		db().alterTypeCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "codigo_tarefa", EnumTipoColumnBD.VARCHAR, 100, null);
		db().alterTypeCollumn(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "nome", EnumTipoColumnBD.VARCHAR, 150, null);
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_AGENDAMENTO", "unq_na_agendamento", "id_na_sistema, codigo_tarefa");
	}

	public void scriptAtualizaFramework000078() throws Exception {
		db().dropTriggerAuditoria(em, SCHEMA_PUBLIC, "NA_GESTAO");
		db().addCollumn(em, SCHEMA_PUBLIC, "NA_GESTAO", "email_responsavel", EnumTipoColumnBD.VARCHAR, 255);
	}

	public void scriptAtualizaFramework000079() throws Exception {
		db().dropTriggerAuditoria(em, SCHEMA_AUDITORIA, "NA_AUDITORIA");
		db().alterTypeCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "usuario", EnumTipoColumnBD.VARCHAR, 60, null);
	}

	public void scriptAtualizaFramework000080() throws Exception {

		db().createTable(em, SCHEMA_AUDITORIA, "auditoria_sistema");
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema", "cod_auditoria", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema", "data_auditoria", EnumTipoColumnBD.TIMESTAMP, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema", "usuario", EnumTipoColumnBD.VARCHAR, false, null, 30, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema", "tabela", EnumTipoColumnBD.VARCHAR, false, null, 50, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema", "chave_registro", EnumTipoColumnBD.VARCHAR, false, null, 150, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema", "tipo_operacao", EnumTipoColumnBD.INTEGER, null, false, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema", "exportacao_dados", EnumTipoColumnBD.INTEGER, null, false, "0");
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema", "hora_auditoria", EnumTipoColumnBD.TIMESTAMP, null);

		db().addPrimaryKey(em, SCHEMA_AUDITORIA, "auditoria_sistema", "pk_auditoria_sistema", "cod_auditoria");

		db().addSequence(em, SCHEMA_AUDITORIA, "seq_auditoria_sistema");

	}

	public void scriptAtualizaFramework000081() throws Exception {

		db().createTable(em, SCHEMA_AUDITORIA, "auditoria_sistema_item");
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema_item", "cod_auditoria", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema_item", "sequencial", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema_item", "campo", EnumTipoColumnBD.VARCHAR, false, null, 50, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema_item", "valor_anterior", EnumTipoColumnBD.VARCHAR, false, null, 250, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema_item", "valor_novo", EnumTipoColumnBD.VARCHAR, false, null, 250, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema_item", "valor_novo_blob", EnumTipoColumnBD.BLOB_TEXTO, false, null, null, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "auditoria_sistema_item", "valor_anterior_blob", EnumTipoColumnBD.BLOB_TEXTO, false, null, null, null);

		db().addPrimaryKey(em, SCHEMA_AUDITORIA, "auditoria_sistema_item", "pk_auditoria_sistema_item", "cod_auditoria,sequencial");

	}

	public void scriptAtualizaFramework000082() throws Exception {
		if (db() instanceof ToolsDBPostgre){
			db().dropUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "unq_na_auditoria_tabela", "", new String[] { "nome" });
			db().dropUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "unq_na_auditoria_campo", "", new String[] { "tabela", "campo" });
		}
	}

	public void scriptAtualizaFramework000083() throws Exception {
		db().createTable(em, SCHEMA_AUDITORIA, "na_auditoria_tabela");
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "id", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "nome", EnumTipoColumnBD.VARCHAR, true, null, 50, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "audit_insert", EnumTipoColumnBD.VARCHAR, 1, true, "S");
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "audit_update", EnumTipoColumnBD.VARCHAR, 1, true, "S");
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "audit_delete", EnumTipoColumnBD.VARCHAR, 1, true, "S");
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "identificador", EnumTipoColumnBD.VARCHAR, false, "", 50, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "schema", EnumTipoColumnBD.VARCHAR, false, null, 50, null);
		db().addPrimaryKey(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "pk_na_auditoria_tabela", "id");

		if (db() instanceof ToolsDBPostgre){
			db().dropUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "unq_na_auditoria_tabela", "", new String[] { "nome" });
			db().addUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "unq_na_auditoria_tabela", "nome, schema");
		}

		db().addSequence(em, SCHEMA_AUDITORIA, "seq_na_auditoria_tabela");
	}

	public void scriptAtualizaFramework000084() throws Exception {

		db().createTable(em, SCHEMA_AUDITORIA, "na_auditoria_campo");
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "id", EnumTipoColumnBD.INTEGER, null, true, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "tabela", EnumTipoColumnBD.VARCHAR, true, null, 50, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "campo", EnumTipoColumnBD.VARCHAR, true, null, 50, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "schema", EnumTipoColumnBD.VARCHAR, false, null, 50, null);
		db().addPrimaryKey(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "pk_na_auditoria_campo", "id");

		if (db() instanceof ToolsDBPostgre){
			db().dropUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "unq_na_auditoria_campo", "", new String[] { "tabela", "campo" });
			db().addUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "unq_na_auditoria_campo", "schema, tabela, campo");
		}

		db().addSequence(em, SCHEMA_AUDITORIA, "seq_na_auditoria_campo");

	}

	public void scriptAtualizaFramework000085() throws Exception {
		if (db() instanceof ToolsDBPostgre){
			db().dropAllUniqueKeys(em, SCHEMA_AUDITORIA, "na_auditoria_tabela");
			db().dropAllUniqueKeys(em, SCHEMA_AUDITORIA, "na_auditoria_campo");
		}

		// 83
		db().dropCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "\"schema\"");
		db().dropCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "schema");

		db().dropCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "\"schema\"");
		db().dropCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "schema");

		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "nome_schema", EnumTipoColumnBD.VARCHAR, false, null, 50, null);
		db().addCollumn(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "nome_schema", EnumTipoColumnBD.VARCHAR, false, null, 50, null);

		db().addUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "unq_na_auditoria_tabela_identif", "identificador");
		db().addUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "unq_na_auditoria_tabela", "nome_schema, nome");
		db().addUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_campo", "unq_na_auditoria_campo", "nome_schema, tabela, campo");
	}

	public void scriptAtualizaFramework000086() throws Exception {
		if (db() instanceof ToolsDBFirebird)
			executeSQL("UPDATE NA_AUDITORIA_TABELA SET IDENTIFICADOR = ID WHERE IDENTIFICADOR IS NULL OR IDENTIFICADOR = '';");
		else if (db() instanceof ToolsDBPostgre)
			executeSQL("delete from AUDITORIA.NA_AUDITORIA_TABELA;");
	}

	public void scriptAtualizaFramework000087() throws Exception {
		if (db() instanceof ToolsDBPostgre)
			db().addUniqueKey(em, SCHEMA_AUDITORIA, "na_auditoria_tabela", "unq_na_auditoria_tabela_identif", "identificador");
	}

	public void scriptAtualizaFramework000088() throws Exception {
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_TOKEN", "id_na_usuario_auditoria", EnumTipoColumnBD.INTEGER, null);
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_USUARIO_TOKEN", "id_na_sistema_auditoria", EnumTipoColumnBD.INTEGER, null);
	}
	
	public void scriptAtualizaFramework000089() throws Exception {
		db().addCollumn(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "NIVEL_USUARIO", EnumTipoColumnBD.INTEGER, null, false, null);
	}	
	
	public void scriptAtualizaFramework000090() throws Exception {
		db().permissaoAlterarNivelAcesso(em, "NA_AGENDAMENTO", InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID, EnumNivelUsuario.SUPORTE);
		db().permissaoAlterarNivelAcesso(em, "NA_CLIENTE", InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID, EnumNivelUsuario.SUPORTE);
		db().permissaoAlterarNivelAcesso(em, "NA_PERMISSOES", InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID, EnumNivelUsuario.SUPORTE);		
	}
	
	public void scriptAtualizaFramework000091() throws Exception {
		//migrado para os scripts 100, 101 e 102
	}	
	
	public void scriptAtualizaFramework000092() throws Exception {		
		executeSQL("update na_parametro_valor set valor = 'smtplw.com.br' where id_na_parametro = (select id from na_parametro p where p.chave = 'EMAIL_SMTP' and P.id_sistema = 1002); ");
		executeSQL("update na_parametro_valor set valor = '587' where id_na_parametro = (select id from na_parametro p where p.chave = 'EMAIL_PORT' and P.id_sistema = 1002); ");
		executeSQL("update na_parametro_valor set valor = 'nainfo@neainformatica.com.br' where id_na_parametro = (select id from na_parametro p where p.chave = 'EMAIL_USUARIO' and P.id_sistema = 1002); ");
        executeSQL("update na_parametro_valor set valor = 'wkALZHIX0787' where id_na_parametro = (select id from na_parametro p where p.chave = 'EMAIL_SENHA' and P.id_sistema = 1002); ");
		executeSQL("update na_parametro_valor set valor = 'neainformatica' where id_na_parametro = (select id from na_parametro p where p.chave = 'EMAIL_SMTP_USER' and P.id_sistema = 1002); ");								
	}
	
	public void scriptAtualizaFramework000093() throws Exception {
		db().dropSequence(em, SCHEMA_ORGANOGRAMA, "seq_organograma_local_fisico");
		db().dropSequence(em, SCHEMA_ORGANOGRAMA, "organograma_local_fisico_id_seq");
	}
	
	public void scriptAtualizaFramework000094() throws Exception {
		db().dropSequence(em, SCHEMA_ORGANOGRAMA, "seq_org_organograma_local_fisico");
		db().addSequence(em, SCHEMA_ORGANOGRAMA, "seq_org_organog_local_fisico");
		
		if (db().existTable(em, SCHEMA_ORGANOGRAMA, "organograma_local_fisico"))
			db().atualizaValorSequence(em, SCHEMA_ORGANOGRAMA, "organograma_local_fisico", "seq_org_organog_local_fisico");
	}

	public void scriptAtualizaFramework000095() throws Exception {
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_PERMISSAO, "ID_NA_CLIENTE", EnumTipoColumnBD.INTEGER, null, false, "1");
	}

	public void scriptAtualizaFramework000096() throws Exception {		
		db().addCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_PERMISSAO, "ID_NA_CLIENTE", EnumTipoColumnBD.INTEGER, null, false, null);				
		db().setCollumnDropDefaultValue(em, SCHEMA_SEGURANCA, "na_permissao", "id_na_cliente");
		
	}

	public void scriptAtualizaFramework000097() throws Exception {
		db().alterCollumRemoveNotNull(em, SCHEMA_SEGURANCA, TABLE_NA_PERMISSAO, "ID_NA_CLIENTE");
	}
	
	public void scriptAtualizaFramework000098() throws Exception {
		if (db() instanceof ToolsDBFirebird)
			executeSQL("update na_permissao set id_na_cliente = null");
		else if (db() instanceof ToolsDBPostgre)
			executeSQL("update seguranca.na_permissao set id_na_cliente = null");
	}
	
	public void scriptAtualizaFramework000099() throws Exception {
//		Já existe uma uniquekey com essa mesma função		
//		db().dropUniqueKey(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "uk_95ytxg0cttjm5y33r52176e4f", "", new String[] {"id_sistema", "descricao"});
//		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_PERMISSAO", "uk_95ytxg0cttjm5y33r52176e4f", "id_sistema, chave");
	}
	
	
	@SuppressWarnings("unchecked")
	public void scriptAtualizaFramework000100() throws Exception {

		Query query = em.createQuery("select c from NaCliente c ");
		List<NaCliente> clientes = query.getResultList();

		for (NaCliente cli : clientes) {
			if (cli.getAtivo().equals(EnumSimNao.SIM)) {
				String hashAnterior = cli.getSituacaoAcessoHash();
				cli.setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);
				if (cli.getSituacaoAcessoHash() == null)
					continue;
				if (!cli.getSituacaoAcessoHash().equals(hashAnterior))
					executeSQL("update na_cliente set situacao_acesso = '" + cli.getSituacaoAcessoHash() + "' where id = " + cli.getId() + ";");
			}

		}	

	}
	
	@SuppressWarnings("unchecked")
	public void scriptAtualizaFramework000101() throws Exception {

		Query query = em.createQuery("select s from NaSistema s");
		List<NaSistema> sistemas = query.getResultList();

		for (NaSistema sis : sistemas) {
			if (sis.getAtivo().equals(EnumSimNao.SIM)) {
				String hashAnterior = sis.getSituacaoAcessoHash();
				sis.setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);
				if (sis.getSituacaoAcessoHash() == null)
					continue;
				if (!sis.getSituacaoAcessoHash().equals(hashAnterior))
					executeSQL("update na_sistema set situacao_acesso = '" + sis.getSituacaoAcessoHash() + "' where id = " + sis.getId() + ";");
			}

		}
		
	}	
	
	@SuppressWarnings("unchecked")
	public void scriptAtualizaFramework000102() throws Exception {

		Query query = em.createQuery("select s from NaUsuarioSistema s");
		List<NaUsuarioSistema> usuarios = query.getResultList();

		for (NaUsuarioSistema us : usuarios) {
			if (us.getAtivo().equals(EnumSimNao.SIM)) {
				String hashAnterior = us.getSituacaoAcessoHash();
				us.setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);
				if (us.getSituacaoAcessoHash() == null)
					continue;
				if (!us.getSituacaoAcessoHash().equals(hashAnterior)) {	
					if (db() instanceof ToolsDBFirebird)
						executeSQL("update na_usuario_sistema set situacao_acesso = '" + us.getSituacaoAcessoHash() + "' where id = " + us.getId() + ";");
					else
						executeSQL("update seguranca.na_usuario_sistema set situacao_acesso = '" + us.getSituacaoAcessoHash() + "' where id = " + us.getId() + ";");
				}
			}

		}

	}
	
	public void scriptAtualizaFramework000103() throws Exception {	
	}
	
	public void scriptAtualizaFramework000104() throws Exception {		
		db().setCollumnDefaultValue(em, SCHEMA_SEGURANCA, "na_permissao", "NIVEL_USUARIO", 4);
	}
	
	public void scriptAtualizaFramework000105() throws Exception {		
		db().dropAllUniqueKeys(em, SCHEMA_PUBLIC, "NA_PARAMETRO");		
		db().dropAllUniqueKeys(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO");
		db().dropAllUniqueKeys(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM");
		db().dropAllUniqueKeys(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR");

		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO", "UNQ_NA_PARAMETRO_CHAVE", "ID_SISTEMA, CHAVE");		
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_GRUPO", "UNQ_NA_PARAMETRO_GRUPO_NOME", "ID_SISTEMA, NOME");		
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_ITEM", "UNQ_NA_PARAMETRO_ITEM_DESCRICAO", "ID_NA_PARAMETRO, DESCRICAO");
		db().addUniqueKey(em, SCHEMA_PUBLIC, "NA_PARAMETRO_VALOR", "UNQ_NA_PARAMETRO_VALOR_CHAVE", "ID_NA_PARAMETRO, CHAVE");
	}
	
	public void scriptAtualizaFramework000106() throws Exception {
		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_GESTAO, "DATA_ALTERACAO", EnumTipoColumnBD.TIMESTAMP, null);
	}

	public void scriptAtualizaFramework000107() throws Exception {
		db().addCollumn(em, SCHEMA_AUDITORIA, "NA_AUDITORIA", "ID_NA_CLIENTE", EnumTipoColumnBD.INTEGER, null, false, null);
	}

	public void scriptAtualizaFramework000108() throws Exception {
		db().alterCollumRemoveNotNull(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "SITUACAO_ACESSO");
	}
	
	public void scriptAtualizaFramework000109() throws Exception {
		db().dropAllUniqueKeys(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_SISTEMA);
		db().addUniqueKey(em, SCHEMA_SEGURANCA, "NA_USUARIO_SISTEMA", "UNQ_NA_USUARIO_SISTEMA", "ID_NA_CLIENTE, ID_SISTEMA, ID_USUARIO");
	}

	public void scriptAtualizaFramework000110() throws Exception {
		db().alterTypeCollumn(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "data_alteracao", EnumTipoColumnBD.TIMESTAMP, null, null);
		db().alterTypeCollumn(em, SCHEMA_PUBLIC, TABLE_NA_CLIENTE, "data_alteracao", EnumTipoColumnBD.TIMESTAMP, null, null);
		db().alterTypeCollumn(em, SCHEMA_SEGURANCA, TABLE_NA_USUARIO_SISTEMA, "data_alteracao", EnumTipoColumnBD.TIMESTAMP, null, null);
		db().addCollumn(em, SCHEMA_PUBLIC, TABLE_NA_GESTAO, "data_alteracao", EnumTipoColumnBD.TIMESTAMP, null, true, "now()");
	}
	
	public void scriptAtualizaFramework000111() throws Exception {
		db().alterCollumRemoveNotNull(em, SCHEMA_PUBLIC, TABLE_NA_SISTEMA, "ORIGEM_SINCRONISMO");
		db().alterCollumRemoveNotNull(em, SCHEMA_PUBLIC, TABLE_NA_CLIENTE, "ORIGEM_SINCRONISMO");
	}

}
