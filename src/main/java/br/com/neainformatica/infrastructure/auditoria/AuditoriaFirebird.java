package br.com.neainformatica.infrastructure.auditoria;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jpa.HibernateEntityManagerFactory;

import br.com.neainformatica.infrastructure.dao.IToolsDB;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;

public class AuditoriaFirebird implements AuditoriaTools {

	@Override
	public List<NaAuditoriaTabela> getNaAuditoriaTabela(EntityManager em, String schema) throws Exception {

		StringBuilder jpql = new StringBuilder();

		jpql.append(" select nat from NaAuditoriaTabela nat ");
		jpql.append(" order by nat.nome asc ");

		return em.createQuery(jpql.toString(), NaAuditoriaTabela.class).getResultList();
	}

	@Override
	public void addFieldsControl(EntityManager em, String schema, Map<String, EnumTipoColumnBD> fieldsControl, IToolsDB toolsDB) throws Exception {

	}

	@Override
	public void criaCampos(EntityManager em, IToolsDB toolsDb) throws Exception {
		if (toolsDb.existProcedure(em, "SP_AUDIT$CRIA_CAMPOS_AUDITORIA"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" CREATE OR ALTER PROCEDURE SP_AUDIT$CRIA_CAMPOS_AUDITORIA ( ");
		sql.append(" TABELA_NOME VARCHAR(31) ) ");
		sql.append(" AS DECLARE ");
		sql.append(" SQL_CABECALHO VARCHAR(250); ");
		sql.append(" BEGIN  ");
		sql.append(" IF ((select count(*) from rdb$relation_fields rc where upper(rc.rdb$relation_name) = :TABELA_NOME ");
		sql.append(" and upper(rc.rdb$field_name) = 'ID_NA_USUARIO_AUDITORIA') = 0 ) THEN ");
		sql.append(" BEGIN ");
		sql.append(" SQL_CABECALHO = 'ALTER TABLE ' || :TABELA_NOME || ' ADD COLUMN ID_NA_USUARIO_AUDITORIA INTEGER;'; ");
		sql.append(" EXECUTE STATEMENT SQL_CABECALHO; ");
		sql.append(" END ");
		sql.append(" IF ((select count(*) from rdb$relation_fields rc where upper(rc.rdb$relation_name) = :TABELA_NOME ");
		sql.append(" and upper(rc.rdb$field_name) = 'ID_NA_SISTEMA_AUDITORIA') = 0 ) THEN ");
		sql.append(" BEGIN ");
		sql.append(" SQL_CABECALHO = 'ALTER TABLE ' || :TABELA_NOME || ' ADD COLUMN ID_NA_SISTEMA_AUDITORIA INTEGER;'; ");
		sql.append(" EXECUTE STATEMENT SQL_CABECALHO; ");
		sql.append(" END ");
		sql.append(" END ");

		getConnection(em).createStatement().execute(sql.toString());
	}

	@Override
	public void criaInsereItem(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "SP_AUDIT$INSERE_ITEM"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" create or alter procedure SP_AUDIT$INSERE_ITEM ( ");
		sql.append(" ID_AUDITORIA integer, ");
		sql.append(" ID_OPERACAO integer, ");
		sql.append(" ID_SEQUENCIAL integer, ");
		sql.append(" NOME_CAMPO varchar(50), ");
		sql.append(" VALOR_OLD varchar(5000), ");
		sql.append(" VALOR_NEW varchar(5000), ");
		sql.append(" CONTADOR integer) ");
		sql.append(" returns ( ALTEROU integer ) ");
		sql.append(" as ");
		sql.append(" declare variable PODE_INSERIR integer; ");
		sql.append(" BEGIN ");
		sql.append(" PODE_INSERIR = 0; ");
		sql.append(" ALTEROU = :CONTADOR; ");
		sql.append(" IF ((:ID_OPERACAO = 1) AND (TRIM(COALESCE(:VALOR_NEW, '')) <> '')) THEN ");
		sql.append(" BEGIN ");
		sql.append(" VALOR_OLD = NULL; ");
		sql.append(" PODE_INSERIR = 1; ");
		sql.append(" END ");
		sql.append(" ELSE IF ((:ID_OPERACAO = 2) AND (TRIM(COALESCE(:VALOR_NEW, '')) <> TRIM(COALESCE(:VALOR_OLD, '')))) THEN ");
		sql.append(" BEGIN ");
		sql.append(" PODE_INSERIR = 1; ");
		sql.append(" END ");
		sql.append(" ELSE IF ((:ID_OPERACAO = 3) AND (TRIM(COALESCE(:VALOR_NEW, '')) <> '') AND (TRIM(COALESCE(:VALOR_OLD, '')) = '')) THEN ");
		sql.append(" BEGIN ");
		sql.append(" VALOR_OLD = VALOR_NEW; ");
		sql.append(" VALOR_NEW = NULL; ");
		sql.append(" PODE_INSERIR = 1; ");
		sql.append(" END ");
		sql.append(" ELSE IF ((:ID_OPERACAO = 3) AND (TRIM(COALESCE(:VALOR_OLD, '')) <> '')) THEN ");
		sql.append(" BEGIN ");
		sql.append(" VALOR_NEW = NULL; ");
		sql.append(" PODE_INSERIR = 1; ");
		sql.append(" END ");
		sql.append(" IF (PODE_INSERIR = 1) THEN ");
		sql.append(" BEGIN ");
		sql.append(" IF (TRIM(COALESCE(:VALOR_OLD, '')) <> '') THEN ");
		sql.append(" VALOR_OLD = SUBSTRING(VALOR_OLD FROM 1 FOR 250); ");
		sql.append(" IF (TRIM(COALESCE(:VALOR_NEW, '')) <> '') THEN ");
		sql.append(" VALOR_NEW = SUBSTRING(VALOR_NEW FROM 1 FOR 250); ");
		sql.append(" INSERT INTO AUDITORIA_SISTEMA_ITEM (COD_AUDITORIA, SEQUENCIAL, CAMPO, VALOR_ANTERIOR, VALOR_NOVO) ");
		sql.append(" VALUES (:ID_AUDITORIA, :ID_SEQUENCIAL, :NOME_CAMPO, :VALOR_OLD, :VALOR_NEW);  ");
		sql.append(" ALTEROU = :CONTADOR + 1; ");
		sql.append(" END ");
		sql.append(" END ");

		getConnection(em).createStatement().execute(sql.toString());

		sql = new StringBuilder();
		sql.append(" DELETE FROM NA_AUDITORIA_TABELA ");
		getConnection(em).createStatement().execute(sql.toString());
	}

	@Override
	public void criaGeraTrigger(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "SP_AUDIT$GERA_TRIGGER"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" create or alter procedure SP_AUDIT$GERA_TRIGGER ( ");
		sql.append(" TABELA_ID varchar(50), TABELA_NOME varchar(31), DROP_TRIGGER char(1),  ");
		sql.append(" AUDIT_INSERT char(1), AUDIT_UPDATE char(1), AUDIT_DELETE char(1))  ");
		sql.append(" as  ");
		sql.append(" declare variable NOME_TRIGGER varchar(300); ");
		sql.append(" declare variable POSICAO_CAMPO integer; ");
		sql.append(" declare variable DESATIVA_TRIGGER integer; ");
		sql.append(" declare variable QTDE_CAMPO integer; ");
		sql.append(" declare variable NOME_CAMPO varchar(31);  ");
		sql.append(" declare variable NOME_CAMPO_CHAVE_AUDIT varchar(620);  ");
		sql.append(" declare variable TIPO_CAMPO varchar(31); ");
		sql.append(" declare variable SQL_CABECALHO varchar(32765); ");
		sql.append(" declare variable SQL_CAMPOS01 varchar(32765);  ");
		sql.append(" declare variable SQL_CAMPOS02 varchar(32765);  ");
		sql.append(" declare variable SQL_RODAPE varchar(32765);  ");
		sql.append(" declare variable SQL_WHERE_UPDATE_AUDITORIA varchar(1000); ");
		sql.append(" declare variable SQL_COMPLETO blob sub_type 1 segment size 80;  ");
		sql.append(" BEGIN ");
		sql.append(" QTDE_CAMPO = 0;  ");
		sql.append(" TABELA_NOME = TRIM(TABELA_NOME); ");
		sql.append(" SQL_WHERE_UPDATE_AUDITORIA = ' WHERE '; ");
		sql.append(" NOME_CAMPO_CHAVE_AUDIT = ''; ");
		sql.append(" SQL_CABECALHO = ''; ");
		sql.append(" SQL_CAMPOS01 = ''; ");
		sql.append(" SQL_CAMPOS02 = ''; ");
		sql.append(" SQL_RODAPE = ''; ");
		sql.append(" SQL_COMPLETO = ''; ");
		sql.append(" TABELA_ID = upper(trim(TABELA_ID)); ");
		sql.append(" DROP_TRIGGER = COALESCE(:DROP_TRIGGER, 'N'); ");
		sql.append(" AUDIT_INSERT = COALESCE(:AUDIT_INSERT, 'S'); ");
		sql.append(" AUDIT_UPDATE = COALESCE(:AUDIT_UPDATE, 'S'); ");
		sql.append(" AUDIT_DELETE = COALESCE(:AUDIT_DELETE, 'S'); ");
		sql.append(" IF (:AUDIT_INSERT = 'N' AND :AUDIT_UPDATE = 'N' AND :AUDIT_DELETE = 'N') THEN ");
		sql.append(" DESATIVA_TRIGGER = 1; ");
		sql.append(" ELSE ");
		sql.append(" DESATIVA_TRIGGER = 0; ");
		sql.append(" IF ((select count(*) from rdb$relation_fields rc where upper(rc.rdb$relation_name) = :TABELA_NOME ");
		sql.append(" and upper(rc.rdb$field_name) = 'ID_NA_USUARIO_AUDITORIA') = 0 ) then ");
		sql.append(" BEGIN ");
		sql.append(" SQL_CABECALHO = 'ALTER TABLE ' || :TABELA_NOME || ' ADD ID_NA_USUARIO_AUDITORIA INTEGER;'; ");
		sql.append(" EXECUTE STATEMENT SQL_CABECALHO; ");
		sql.append(" END ");
		sql.append(" IF ((select count(*) from rdb$relation_fields rc where upper(rc.rdb$relation_name) = :TABELA_NOME ");
		sql.append(" and upper(rc.rdb$field_name) = 'ID_NA_SISTEMA_AUDITORIA') = 0 ) then ");
		sql.append(" BEGIN ");
		sql.append(" SQL_CABECALHO = 'ALTER TABLE ' || :TABELA_NOME || ' ADD ID_NA_SISTEMA_AUDITORIA INTEGER;'; ");
		sql.append(" EXECUTE STATEMENT SQL_CABECALHO; ");
		sql.append(" END ");
		sql.append(" IF (:DROP_TRIGGER = 'S') THEN ");
		sql.append(" BEGIN ");
		sql.append(" /*TODAS AS TRIGGERS DE AUDITORIA QUE EXISTIREM */ ");
		sql.append(" FOR SELECT 'DROP TRIGGER ' || TRIM(T.RDB$TRIGGER_NAME) ");
		sql.append(" FROM RDB$TRIGGERS T ");
		sql.append(" WHERE (SUBSTRING(UPPER(RDB$TRIGGER_NAME) FROM 1 FOR 6) = 'AUDIT$') AND ");
		sql.append(" UPPER(T.RDB$RELATION_NAME) = :TABELA_NOME ");
		sql.append(" ORDER BY T.RDB$TRIGGER_NAME ");
		sql.append(" INTO :NOME_TRIGGER ");
		sql.append(" DO ");
		sql.append(" EXECUTE STATEMENT :NOME_TRIGGER; ");
		sql.append(" END /* IF (:DROP_TRIGGER = 'S') THEN */ ");
		sql.append(" /* CRIA TRIGGER DESATIVADA */ ");
		sql.append(" IF (DESATIVA_TRIGGER = 1) THEN ");
		sql.append(" BEGIN ");
		sql.append(" SQL_CABECALHO = 'CREATE OR ALTER TRIGGER AUDIT$' || :TABELA_ID || ' FOR ' || :TABELA_NOME || ASCII_CHAR(13); ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'INACTIVE AFTER INSERT OR UPDATE OR DELETE POSITION 32765 AS' || ASCII_CHAR(13); ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'BEGIN' || ASCII_CHAR(13); ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'END'; ");
		sql.append(" EXECUTE STATEMENT :SQL_CABECALHO; ");
		sql.append(" END ");
		sql.append(" ELSE IF (:AUDIT_INSERT = 'S' OR ");
		sql.append(" :AUDIT_UPDATE = 'S' OR ");
		sql.append(" :AUDIT_DELETE = 'S') THEN ");
		sql.append(" BEGIN ");
		sql.append(" FOR SELECT IP.RDB$FIELD_POSITION, TRIM(IP.RDB$FIELD_NAME) ");
		sql.append(" FROM RDB$RELATION_CONSTRAINTS RC ");
		sql.append(" LEFT JOIN RDB$INDEX_SEGMENTS IP ON IP.RDB$INDEX_NAME = RC.RDB$INDEX_NAME ");
		sql.append(" WHERE RC.RDB$RELATION_NAME = :TABELA_NOME AND ");
		sql.append(" RC.RDB$CONSTRAINT_TYPE = 'PRIMARY KEY' ");
		sql.append(" ORDER BY IP.RDB$FIELD_POSITION ");
		sql.append(" INTO :POSICAO_CAMPO, NOME_CAMPO ");
		sql.append(" DO ");
		sql.append(" BEGIN ");
		sql.append(" IF (POSICAO_CAMPO > 0) THEN ");
		sql.append(" BEGIN ");
		sql.append(" NOME_CAMPO_CHAVE_AUDIT = :NOME_CAMPO_CHAVE_AUDIT || ' || '';'' || '; ");
		sql.append(" SQL_WHERE_UPDATE_AUDITORIA = :SQL_WHERE_UPDATE_AUDITORIA || ' AND '; ");
		sql.append(" END ");
		sql.append(" NOME_CAMPO_CHAVE_AUDIT = :NOME_CAMPO_CHAVE_AUDIT || 'XXX.' || :NOME_CAMPO; ");
		sql.append(" SQL_WHERE_UPDATE_AUDITORIA = :SQL_WHERE_UPDATE_AUDITORIA || NOME_CAMPO || ' =  NEW.' || NOME_CAMPO; ");
		sql.append(" END ");
		sql.append(" SQL_WHERE_UPDATE_AUDITORIA = :SQL_WHERE_UPDATE_AUDITORIA ||';'; ");
		sql.append(" /* ACHOU CHAVE PRIMARIA */ ");
		sql.append(" IF (:NOME_CAMPO_CHAVE_AUDIT <> '') THEN ");
		sql.append(" BEGIN ");
		sql.append(" SQL_CABECALHO = 'CREATE OR ALTER TRIGGER AUDIT$' || :TABELA_ID || ' FOR ' || :TABELA_NOME || ASCII_CHAR(13); ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ACTIVE AFTER'; ");
		sql.append(" IF (:AUDIT_INSERT = 'S') THEN ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || ' INSERT '; ");
		sql.append(" IF (:AUDIT_INSERT = 'S' AND :AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || ' OR '; ");
		sql.append(" IF (:AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || ' UPDATE '; ");
		sql.append(" IF ((:AUDIT_INSERT = 'S' OR ");
		sql.append(" :AUDIT_UPDATE = 'S') AND :AUDIT_DELETE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || ' OR '; ");
		sql.append(" IF (:AUDIT_DELETE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || ' DELETE '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || ' POSITION 32765 AS' || ASCII_CHAR(13); ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'DECLARE VARIABLE ID_OPERACAO INTEGER; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'DECLARE VARIABLE ID_AUDITORIA INTEGER; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'DECLARE VARIABLE CHAVE_AUDITORIA VARCHAR(150); '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'DECLARE VARIABLE CONTADOR INTEGER; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'DECLARE VARIABLE VARIAVEL_CONTEXTO VARCHAR(30); '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'DECLARE VARIABLE USUARIO_JAVA VARCHAR(30); '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'DECLARE VARIABLE ID_NA_USUARIO_AUDIT INTEGER; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'DECLARE VARIABLE ID_NA_SISTEMA_AUDIT INTEGER; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'BEGIN ' || ASCII_CHAR(13); ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'CONTADOR = 0; ' || ASCII_CHAR(13); ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_NA_USUARIO_AUDIT = NULL;'; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_NA_SISTEMA_AUDIT = NULL;'; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT(''USER_SESSION'', ''USUARIO_LOGIN''),'''')) FROM 1 FOR 30)) AS CONTEUDO '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'FROM RDB$DATABASE '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'INTO VARIAVEL_CONTEXTO; ' || ASCII_CHAR(13); ");
		sql.append(" /* DESCOBRINDO O USUARIO LOGADO NA SESSAO*/ ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'IF ((:VARIAVEL_CONTEXTO IS NULL) OR (TRIM(:VARIAVEL_CONTEXTO) = '''')) THEN VARIAVEL_CONTEXTO = USER;' || ASCII_CHAR(13); ");
		sql.append(" IF (:AUDIT_INSERT = 'S') THEN ");
		sql.append(" BEGIN ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'IF (INSERTING) THEN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'BEGIN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_OPERACAO = 1; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'CHAVE_AUDITORIA = ' || REPLACE(:NOME_CAMPO_CHAVE_AUDIT, 'XXX.', 'NEW.') || '; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_NA_USUARIO_AUDIT = NEW.ID_NA_USUARIO_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_NA_SISTEMA_AUDIT = NEW.ID_NA_SISTEMA_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'END' || ASCII_CHAR(13); ");
		sql.append(" END ");
		sql.append(" IF (:AUDIT_UPDATE = 'S') THEN ");
		sql.append(" BEGIN ");
		sql.append(" IF (:AUDIT_INSERT = 'S') THEN ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ELSE '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'IF (UPDATING) THEN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'BEGIN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_OPERACAO = 2; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'CHAVE_AUDITORIA = ' || REPLACE(:NOME_CAMPO_CHAVE_AUDIT, 'XXX.', 'NEW.') || '; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_NA_USUARIO_AUDIT = NEW.ID_NA_USUARIO_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_NA_SISTEMA_AUDIT = NEW.ID_NA_SISTEMA_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'END' || ASCII_CHAR(13); ");
		sql.append(" END ");
		sql.append(" IF (:AUDIT_DELETE = 'S') THEN ");
		sql.append(" BEGIN ");
		sql.append(" IF (:AUDIT_INSERT = 'S' OR :AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ELSE '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'IF (DELETING) THEN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'BEGIN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_OPERACAO = 3; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'CHAVE_AUDITORIA = ' || REPLACE(:NOME_CAMPO_CHAVE_AUDIT, 'XXX.', 'OLD.') || '; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_NA_USUARIO_AUDIT = OLD.ID_NA_USUARIO_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_NA_SISTEMA_AUDIT = OLD.ID_NA_SISTEMA_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'END' || ASCII_CHAR(13); ");
		sql.append(" END ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'IF (ID_NA_USUARIO_AUDIT IS NOT NULL) THEN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'BEGIN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'SELECT U.LOGIN FROM NA_USUARIO U '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'WHERE U.ID = :ID_NA_USUARIO_AUDIT INTO :USUARIO_JAVA;'; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'IF (USUARIO_JAVA IS NOT NULL) THEN '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'VARIAVEL_CONTEXTO = :USUARIO_JAVA; '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'END ' || ASCII_CHAR(13); ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'ID_AUDITORIA = GEN_ID(AUDITORIA_SISTEMA, 1);' || ASCII_CHAR(13); ");
		sql.append(" /* INCLUIDO OS NOMES DE CAMPOS DE TABELA CASO ALGUEM SISTEMA TENHA ESSES CAMPOS INVERTIDOS*/ ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'INSERT INTO AUDITORIA_SISTEMA (COD_AUDITORIA, TABELA, CHAVE_REGISTRO, DATA_AUDITORIA, HORA_AUDITORIA, USUARIO, TIPO_OPERACAO) '; ");
		sql.append(" SQL_CABECALHO = :SQL_CABECALHO || 'VALUES (:ID_AUDITORIA, ''' || :TABELA_NOME || '''' || ', :CHAVE_AUDITORIA, ''NOW'', ''NOW'', :VARIAVEL_CONTEXTO, :ID_OPERACAO); ' || ASCII_CHAR(13); ");
		sql.append(" IF (:AUDIT_INSERT = 'S' OR  :AUDIT_UPDATE = 'S' OR :AUDIT_DELETE = 'S') THEN ");
		sql.append(" BEGIN ");
		sql.append(" /* REMOVIDO OS FIELDS DE TIPO BLOB */ ");
		sql.append(" FOR SELECT RF.RDB$FIELD_POSITION, TRIM(RF.RDB$FIELD_NAME), TRIM(T.RDB$TYPE_NAME) ");
		sql.append(" FROM RDB$RELATION_FIELDS RF ");
		sql.append(" LEFT JOIN RDB$FIELDS F ON (F.RDB$FIELD_NAME = RF.RDB$FIELD_SOURCE) ");
		sql.append(" LEFT JOIN RDB$TYPES T ON (T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE' AND ");
		sql.append(" T.RDB$TYPE = F.RDB$FIELD_TYPE) ");
		sql.append(" WHERE RF.RDB$RELATION_NAME = :TABELA_NOME AND ");
		sql.append(" TRIM(RF.RDB$FIELD_NAME) <> 'ID_NA_USUARIO_AUDITORIA' AND ");
		sql.append(" TRIM(RF.RDB$FIELD_NAME) <> 'ID_NA_SISTEMA_AUDITORIA' AND ");
		sql.append(" TRIM(T.RDB$TYPE_NAME) <> 'BLOB' AND ");
		sql.append(" (SELECT COUNT(*) FROM NA_AUDITORIA_CAMPO AC WHERE AC.TABELA = :TABELA_NOME AND AC.CAMPO = RF.RDB$FIELD_NAME) = 0  ");
		sql.append(" ORDER BY RF.RDB$FIELD_POSITION  ");
		sql.append(" INTO :POSICAO_CAMPO, :NOME_CAMPO, :TIPO_CAMPO ");
		sql.append(" DO ");
		sql.append(" BEGIN ");
		sql.append(" IF (:TIPO_CAMPO <> 'BLOB') THEN ");
		sql.append(" BEGIN ");
		sql.append(" QTDE_CAMPO = :QTDE_CAMPO + 1; ");
		sql.append(" IF (QTDE_CAMPO <= 150) THEN ");
		sql.append(" BEGIN ");
		sql.append(" SQL_CAMPOS01 = :SQL_CAMPOS01 || 'EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA,:ID_OPERACAO,' || :POSICAO_CAMPO || ',''' || :NOME_CAMPO || ''''; ");
		sql.append(" IF (:AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CAMPOS01 = :SQL_CAMPOS01 || ',OLD.' || :NOME_CAMPO || ',NEW.' || :NOME_CAMPO; ");
		sql.append(" ELSE BEGIN ");
		sql.append(" IF (:AUDIT_DELETE = 'S') THEN ");
		sql.append(" SQL_CAMPOS01 = :SQL_CAMPOS01 || ',OLD.' || :NOME_CAMPO; ");
		sql.append(" ELSE ");
		sql.append(" SQL_CAMPOS01 = :SQL_CAMPOS01 || ',NULL'; ");
		sql.append(" IF (:AUDIT_INSERT = 'S') THEN ");
		sql.append(" SQL_CAMPOS01 = :SQL_CAMPOS01 || ',NEW.' || :NOME_CAMPO; ");
		sql.append(" ELSE  ");
		sql.append(" SQL_CAMPOS01 = :SQL_CAMPOS01 || ',NULL'; ");
		sql.append(" END ");
		sql.append(" SQL_CAMPOS01 = :SQL_CAMPOS01 || ',:CONTADOR) RETURNING_VALUES :CONTADOR;' || ASCII_CHAR(13); ");
		sql.append(" END ");
		sql.append(" ELSE BEGIN ");
		sql.append(" SQL_CAMPOS02 = :SQL_CAMPOS02 || 'EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA,:ID_OPERACAO,' || :POSICAO_CAMPO || ',''' || :NOME_CAMPO || ''''; ");
		sql.append(" IF (:AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CAMPOS02 = :SQL_CAMPOS02 || ',OLD.' || :NOME_CAMPO || ',NEW.' || :NOME_CAMPO; ");
		sql.append(" ELSE BEGIN ");
		sql.append(" IF (:AUDIT_DELETE = 'S') THEN ");
		sql.append(" SQL_CAMPOS02 = :SQL_CAMPOS02 || ',OLD.' || :NOME_CAMPO; ");
		sql.append(" ELSE ");
		sql.append(" SQL_CAMPOS02 = :SQL_CAMPOS02 || ',NULL'; ");
		sql.append(" IF (:AUDIT_INSERT = 'S') THEN  ");
		sql.append(" SQL_CAMPOS02 = :SQL_CAMPOS02 || ',NEW.' || :NOME_CAMPO; ");
		sql.append(" ELSE ");
		sql.append(" SQL_CAMPOS02 = :SQL_CAMPOS02 || ',NULL'; ");
		sql.append(" END ");
		sql.append(" SQL_CAMPOS02 = :SQL_CAMPOS02 || ',:CONTADOR) RETURNING_VALUES :CONTADOR;' || ASCII_CHAR(13); ");
		sql.append(" END ");
		sql.append(" END ");
		sql.append(" END ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'IF (:CONTADOR > 0) THEN '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'BEGIN' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'if (ID_NA_SISTEMA_AUDIT IS NOT NULL) then VARIAVEL_CONTEXTO = CAST(:ID_NA_SISTEMA_AUDIT AS VARCHAR(10)); ELSE ' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT(''USER_SESSION'', ''SISTEMA_ID''),'''')) FROM 1 FOR 30)) AS CONTEUDO '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'FROM RDB$DATABASE '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'INTO VARIAVEL_CONTEXTO;' || ASCII_CHAR(13); ");
		sql.append(" POSICAO_CAMPO = POSICAO_CAMPO + 1; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO, ' || :POSICAO_CAMPO || ', ''CODIGO DO SISTEMA'', NULL, :VARIAVEL_CONTEXTO, :CONTADOR) RETURNING_VALUES :CONTADOR;' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT(''USER_SESSION'', ''SISTEMA_VERSAO''),'''')) FROM 1 FOR 30)) AS CONTEUDO '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'FROM RDB$DATABASE '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'INTO VARIAVEL_CONTEXTO;' || ASCII_CHAR(13); ");
		sql.append(" POSICAO_CAMPO = POSICAO_CAMPO + 1; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO, ' || :POSICAO_CAMPO || ', ''VERSAO DO SISTEMA'', NULL, :VARIAVEL_CONTEXTO, :CONTADOR) RETURNING_VALUES :CONTADOR;' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT(''USER_SESSION'', ''TERMINAL_IP''),'''')) FROM 1 FOR 30)) AS CONTEUDO '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'FROM RDB$DATABASE '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'INTO VARIAVEL_CONTEXTO;' || ASCII_CHAR(13); ");
		sql.append(" POSICAO_CAMPO = POSICAO_CAMPO + 1; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO, ' || :POSICAO_CAMPO || ', ''IP DO TERMINAL'', NULL, :VARIAVEL_CONTEXTO, :CONTADOR) RETURNING_VALUES :CONTADOR;' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT(''USER_SESSION'', ''TERMINAL_NOME''),'''')) FROM 1 FOR 30)) AS CONTEUDO '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'FROM RDB$DATABASE '; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'INTO VARIAVEL_CONTEXTO;' || ASCII_CHAR(13); ");
		sql.append(" POSICAO_CAMPO = POSICAO_CAMPO + 1; ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO, ' || :POSICAO_CAMPO || ', ''NOME DO TERMINAL'', NULL, :VARIAVEL_CONTEXTO, :CONTADOR) RETURNING_VALUES :CONTADOR;' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'END ' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'ELSE ' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'DELETE FROM AUDITORIA_SISTEMA WHERE COD_AUDITORIA = :ID_AUDITORIA; ' || ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || 'if ((inserting or updating) AND (ID_NA_USUARIO_AUDIT IS NOT NULL OR ID_NA_SISTEMA_AUDIT IS NOT NULL)) then'|| ASCII_CHAR(13); ");
		sql.append(" SQL_RODAPE = :SQL_RODAPE || ' UPDATE '||:TABELA_NOME||' SET ID_NA_USUARIO_AUDITORIA = NULL, ID_NA_SISTEMA_AUDITORIA = NULL '||SQL_WHERE_UPDATE_AUDITORIA; ");
		sql.append(" END ");
		sql.append(" SQL_COMPLETO = :SQL_CABECALHO; ");
		sql.append(" SQL_COMPLETO = :SQL_COMPLETO || :SQL_CAMPOS01; ");
		sql.append(" SQL_COMPLETO = :SQL_COMPLETO || :SQL_CAMPOS02; ");
		sql.append(" SQL_COMPLETO = :SQL_COMPLETO || :SQL_RODAPE; ");
		sql.append(" SQL_COMPLETO = :SQL_COMPLETO || ' END'; ");
		sql.append(" EXECUTE STATEMENT :SQL_COMPLETO; ");
		sql.append(" END  ");
		sql.append(" END ");
		sql.append(" END ");

		System.out.println(sql.toString());
		getConnection(em).createStatement().execute(sql.toString());
	}

	@Override
	public void criaTrigger(EntityManager em, IToolsDB toolsDb) throws Exception {
		if (toolsDb.existProcedure(em, "SP_AUDIT$CRIA_TRIGGER"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" create or alter procedure SP_AUDIT$CRIA_TRIGGER  ");
		sql.append(" as ");
		sql.append(" declare variable IDENTIFICADOR varchar(50); ");
		sql.append(" declare variable NOME varchar(31); ");
		sql.append(" declare variable AUDIT_INSERT char(1); ");
		sql.append(" declare variable AUDIT_UPDATE char(1); ");
		sql.append(" declare variable AUDIT_DELETE char(1); ");
		sql.append(" declare variable DROP_TRIGGER char(1); ");
		sql.append(" BEGIN ");
		sql.append(" /* CRIA SOMENTE AS TRIGGERS QUE FALTAM*/ ");
		sql.append(" FOR SELECT DISTINCT AST.IDENTIFICADOR, AST.NOME, 'S' AS DROP_TRIGGER, COALESCE(AST.AUDIT_INSERT, 'S') AS AUDIT_INSERT, ");
		sql.append(" COALESCE(AST.AUDIT_UPDATE, 'S') AS AUDIT_UPDATE, COALESCE(AST.AUDIT_DELETE, 'S') AS AUDIT_DELETE ");
		sql.append(" FROM NA_AUDITORIA_TABELA AST ");
		sql.append(" WHERE SUBSTRING(TRIM(AST.NOME) FROM 1 FOR 17) <> 'AUDITORIA_SISTEMA' AND ");
		sql.append(" TRIM(AST.NOME) <> 'NA_AUDITORIA' AND TRIM(AST.NOME) <> 'NA_AUDITORIA_ITEM' AND ");
		sql.append(" (SELECT COUNT(*) FROM RDB$RELATIONS R ");
		sql.append(" WHERE R.RDB$RELATION_NAME = AST.NOME) > 0 AND ");
		sql.append(" (SELECT COUNT(*) FROM RDB$RELATION_CONSTRAINTS RC  ");
		sql.append(" WHERE RC.RDB$RELATION_NAME = AST.NOME AND ");
		sql.append(" RC.RDB$CONSTRAINT_TYPE = 'PRIMARY KEY') > 0 AND ");
		sql.append(" (SELECT COUNT(*) FROM RDB$TRIGGERS T ");
		sql.append(" WHERE SUBSTRING(T.RDB$TRIGGER_NAME FROM 1 FOR 6) = 'AUDIT$' AND ");
		sql.append(" T.RDB$RELATION_NAME = AST.NOME) = 0 ");
		sql.append(" ORDER BY AST.NOME ");
		sql.append(" INTO :IDENTIFICADOR, :NOME, :DROP_TRIGGER, :AUDIT_INSERT, :AUDIT_UPDATE, :AUDIT_DELETE ");
		sql.append(" DO ");
		sql.append(" BEGIN ");
		sql.append(" EXECUTE PROCEDURE  SP_AUDIT$GERA_TRIGGER(:IDENTIFICADOR, :NOME, :DROP_TRIGGER, :AUDIT_INSERT, :AUDIT_UPDATE, :AUDIT_DELETE); ");
		sql.append(" END ");
		sql.append(" /* CRIA AS TRIGGERS INATIVAS, MESMO SEM PRIMARY KEY*/ ");
		sql.append(" FOR SELECT DISTINCT AST.IDENTIFICADOR, TRIM(AST.NOME), 'S' AS DROP_TRIGGER, 'N' AS AUDIT_INSERT, 'N' AS AUDIT_UPDATE, 'N' AS AUDIT_DELETE ");
		sql.append(" FROM NA_AUDITORIA_TABELA AST ");
		sql.append(" WHERE SUBSTRING(TRIM(AST.NOME) FROM 1 FOR 17) <> 'AUDITORIA_SISTEMA' AND ");
		sql.append(" TRIM(AST.NOME) <> 'NA_AUDITORIA' AND TRIM(AST.NOME) <> 'NA_AUDITORIA_ITEM' AND ");
		sql.append(" (COALESCE(AST.AUDIT_INSERT, 'S') = 'N' AND COALESCE(AST.AUDIT_UPDATE, 'S') = 'N' AND ");
		sql.append(" COALESCE(AST.AUDIT_DELETE, 'S') = 'N') AND ");
		sql.append(" (SELECT COUNT(*) FROM RDB$RELATIONS R ");
		sql.append(" WHERE R.RDB$RELATION_NAME = AST.NOME) > 0 AND  ");
		sql.append(" (SELECT COUNT(*) FROM RDB$TRIGGERS T ");
		sql.append(" WHERE SUBSTRING(T.RDB$TRIGGER_NAME FROM 1 FOR 6) = 'AUDIT$' AND T.RDB$RELATION_NAME = AST.NOME AND ");
		sql.append(" T.RDB$TRIGGER_INACTIVE = 1) = 0 ");
		sql.append(" ORDER BY AST.NOME ");
		sql.append(" INTO :IDENTIFICADOR, :NOME, :DROP_TRIGGER, :AUDIT_INSERT, :AUDIT_UPDATE, :AUDIT_DELETE ");
		sql.append(" DO ");
		sql.append(" BEGIN ");
		sql.append(" EXECUTE PROCEDURE  SP_AUDIT$GERA_TRIGGER(:IDENTIFICADOR, :NOME, :DROP_TRIGGER, :AUDIT_INSERT, :AUDIT_UPDATE, :AUDIT_DELETE); ");
		sql.append(" END ");
		sql.append(" END ");

		getConnection(em).createStatement().execute(sql.toString());
	}

	@Override
	public void criaAuditoriaTabelaAu(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existTrigger(em, "TGR_AUDIT$_AUDITORIA_TABELA_AU"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" CREATE OR ALTER TRIGGER TGR_AUDIT$_AUDITORIA_TABELA_AU FOR NA_AUDITORIA_TABELA ");
		sql.append(" ACTIVE AFTER UPDATE POSITION 32765 ");
		sql.append(" AS ");
		sql.append(" DECLARE VARIABLE DROP_TRIGGER CHAR(1); ");
		sql.append(" DECLARE VARIABLE QTDE_PK INTEGER; ");
		sql.append(" BEGIN ");
		sql.append(" IF ((NEW.NOME <> 'AUDITORIA_SISTEMA') AND (NEW.NOME <> 'AUDITORIA_SISTEMA_ITEM') AND ");
		sql.append(" (NEW.NOME <> 'NA_AUDITORIA') AND (NEW.NOME <> 'NA_AUDITORIA_ITEM') AND ");
		sql.append(" ((COALESCE(NEW.AUDIT_INSERT, 'S') <> COALESCE(OLD.AUDIT_INSERT, 'S')) OR  ");
		sql.append(" (COALESCE(NEW.AUDIT_UPDATE, 'S') <> COALESCE(OLD.AUDIT_UPDATE, 'S')) OR ");
		sql.append(" (COALESCE(NEW.AUDIT_DELETE, 'S') <> COALESCE(OLD.AUDIT_DELETE, 'S')))) THEN  ");
		sql.append(" BEGIN ");
		sql.append(" SELECT CASE WHEN COUNT(*) > 0 THEN 'S' ELSE 'N' END AS DROP_TRIGGER ");
		sql.append(" FROM RDB$TRIGGERS T ");
		sql.append(" WHERE SUBSTRING(T.RDB$TRIGGER_NAME FROM 1 FOR 6) = 'AUDIT$' ");
		sql.append(" AND T.RDB$RELATION_NAME = NEW.NOME ");
		sql.append(" INTO :DROP_TRIGGER; ");
		sql.append(" SELECT COUNT(*) FROM RDB$RELATION_CONSTRAINTS RC ");
		sql.append(" WHERE RC.RDB$RELATION_NAME = NEW.NOME AND RC.RDB$CONSTRAINT_TYPE = 'PRIMARY KEY' ");
		sql.append(" INTO QTDE_PK; ");
		sql.append(" IF (QTDE_PK>0) THEN ");
		sql.append(" EXECUTE PROCEDURE SP_AUDIT$GERA_TRIGGER(NEW.IDENTIFICADOR, NEW.NOME, :DROP_TRIGGER, NEW.AUDIT_INSERT, NEW.AUDIT_UPDATE, NEW.AUDIT_DELETE); ");
		sql.append(" END ");
		sql.append(" END ");

		getConnection(em).createStatement().execute(sql.toString());

	}

	@Override
	public void criaDisconect(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "SP_AUDIT$DISCONNECT_BASE"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" create or alter procedure SP_AUDIT$DISCONNECT_BASE  ");
		sql.append(" as ");
		sql.append(" BEGIN  ");
		sql.append(" INSERT INTO NA_AUDITORIA_TABELA (ID, IDENTIFICADOR, NOME, NOME_AMIGAVEL, AUDIT_INSERT, AUDIT_UPDATE, AUDIT_DELETE) ");
		sql.append(" SELECT DISTINCT GEN_ID(SEQ_NA_AUDITORIA_TABELA, 1) as ID, HASH(TRIM(F.RDB$RELATION_NAME)) as IDENTIFICADOR, ");
		sql.append(" TRIM(F.RDB$RELATION_NAME) as NOME, TRIM(F.RDB$RELATION_NAME) as NOME_AMIGAVEL, 'S' as AUDIT_INSERT, 'S' as AUDIT_UPDATE, 'S' as AUDIT_DELETE ");
		sql.append(" FROM RDB$RELATIONS F ");
		sql.append(" WHERE F.RDB$RELATION_NAME IS NOT NULL AND ");
		sql.append(" COALESCE(F.RDB$RELATION_TYPE, 0) = 0 AND F.RDB$VIEW_SOURCE IS NULL AND ");
		sql.append(" SUBSTRING(F.RDB$RELATION_NAME FROM 1 FOR 4) <> 'RDB$' AND ");
		sql.append(" (SELECT COUNT(*) FROM NA_AUDITORIA_TABELA AST WHERE AST.NOME = F.RDB$RELATION_NAME) = 0 ");
		sql.append(" ORDER BY F.RDB$RELATION_NAME; ");
		sql.append(" ");
		sql.append(" DELETE FROM NA_AUDITORIA_TABELA ");
		sql.append(" WHERE (SELECT COUNT(*) ");
		sql.append(" FROM RDB$RELATIONS F  ");
		sql.append(" WHERE F.RDB$RELATION_NAME IS NOT NULL AND ");
		sql.append(" COALESCE(F.RDB$RELATION_TYPE, 0) = 0 AND F.RDB$VIEW_SOURCE IS NULL AND ");
		sql.append(" SUBSTRING(F.RDB$RELATION_NAME FROM 1 FOR 4) <> 'RDB$' AND ");
		sql.append(" UPPER(TRIM(NA_AUDITORIA_TABELA.NOME)) = UPPER(TRIM(F.RDB$RELATION_NAME))) = 0; ");
		sql.append(" ");
		sql.append(" EXECUTE PROCEDURE SP_AUDIT$CRIA_TRIGGER; ");
		sql.append(" END ");

		getConnection(em).createStatement().execute(sql.toString());

	}

	@Override
	public void iniciaCriacaoTriggersAuditoria(EntityManager em) throws Exception {

		StringBuilder sql = new StringBuilder();

		sql.append(" EXECUTE PROCEDURE SP_AUDIT$DISCONNECT_BASE ");

		getConnection(em).createStatement().execute(sql.toString());

	}

	@Override
	public void geraTriggers(EntityManager em, IToolsDB toolsDb, String identificador, String nomeTabela, String dropTrigger, EnumSimNao auditInsert, EnumSimNao auditUpdate,
			EnumSimNao auditDelete, String nomeSchema) throws Exception {
		Integer quantidadeCampos = 0;
		boolean desativaTrigger = false;
		String nomeCampoChaveAudit = "";
		String sqlWhereUpdateAudit = " where ";

		nomeSchema = "";

		if (auditInsert == EnumSimNao.NAO && auditUpdate == EnumSimNao.NAO && auditDelete == EnumSimNao.NAO)
			desativaTrigger = true;

		/* CRIAR CAMPO DE AUDITORIA */

		toolsDb.addCollumn(em, nomeSchema.toUpperCase(), nomeTabela.toUpperCase(), "ID_NA_USUARIO_AUDITORIA", EnumTipoColumnBD.INTEGER, null);
		toolsDb.addCollumn(em, nomeSchema.toLowerCase(), nomeTabela.toLowerCase(), "ID_NA_SISTEMA_AUDITORIA", EnumTipoColumnBD.INTEGER, null);

		StringBuilder sqlPrincipal = new StringBuilder();

		if (dropTrigger.equals("S"))
			dropTriggersAuditria(em, toolsDb, nomeSchema, nomeTabela);

		if (desativaTrigger)
			desativaTriggersAuditoria(em, toolsDb, nomeSchema, identificador, nomeTabela);

		if (auditInsert == EnumSimNao.SIM && auditUpdate == EnumSimNao.SIM && auditDelete == EnumSimNao.SIM) {

			List<Object[]> primaryKeyPosition = toolsDb.getFieldAndPositionPrimaryKey(em, nomeSchema, nomeTabela);

			for (Object[] keys : primaryKeyPosition) {

				if (Integer.parseInt(String.valueOf(keys[1])) > 0) {
					nomeCampoChaveAudit += " || '; ' || ";
					sqlWhereUpdateAudit += " and ";
				}

				nomeCampoChaveAudit += " XXX." + keys[0].toString();
				sqlWhereUpdateAudit += " " + keys[0].toString() + " = new." + keys[0].toString();
			}
			sqlWhereUpdateAudit += ";";

			if (!nomeCampoChaveAudit.equals("")) {
				sqlPrincipal.append(" CREATE OR ALTER TRIGGER AUDIT$" + identificador.toUpperCase() + " FOR " + nomeTabela.toUpperCase());
				sqlPrincipal.append(" ACTIVE AFTER ");

				if (auditInsert == EnumSimNao.SIM)
					sqlPrincipal.append(" INSERT ");
				if (auditInsert == EnumSimNao.SIM || auditUpdate == EnumSimNao.SIM)
					sqlPrincipal.append(" OR ");
				if (auditUpdate == EnumSimNao.SIM)
					sqlPrincipal.append(" UPDATE ");
				if ((auditInsert == EnumSimNao.SIM || auditUpdate == EnumSimNao.SIM) && auditDelete == EnumSimNao.SIM)
					sqlPrincipal.append(" OR ");
				if (auditDelete == EnumSimNao.SIM)
					sqlPrincipal.append(" DELETE ");

				sqlPrincipal.append(" POSITION 32765 AS ");
				sqlPrincipal.append(" DECLARE VARIABLE ID_OPERACAO INTEGER; ");
				sqlPrincipal.append(" DECLARE VARIABLE ID_AUDITORIA INTEGER; ");
				sqlPrincipal.append(" DECLARE VARIABLE CHAVE_AUDITORIA VARCHAR(150); ");
				sqlPrincipal.append(" DECLARE VARIABLE CONTADOR INTEGER; ");
				sqlPrincipal.append(" DECLARE VARIABLE VARIAVEL_CONTEXTO VARCHAR(30); ");
				sqlPrincipal.append(" DECLARE VARIABLE USUARIO_JAVA VARCHAR(30);  ");
				sqlPrincipal.append(" DECLARE VARIABLE ID_NA_USUARIO_AUDIT INTEGER; ");
				sqlPrincipal.append(" DECLARE VARIABLE ID_NA_SISTEMA_AUDIT INTEGER; ");
				sqlPrincipal.append(" BEGIN  ");
				sqlPrincipal.append(" CONTADOR = 0; ");
				sqlPrincipal.append(" ID_NA_USUARIO_AUDIT = NULL;  ");
				sqlPrincipal.append(" ID_NA_SISTEMA_AUDIT = NULL; ");
				sqlPrincipal.append(" SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT('USER_SESSION', 'USUARIO_LOGIN'),'')) FROM 1 FOR 30)) AS CONTEUDO  ");
				sqlPrincipal.append(" FROM RDB$DATABASE  ");
				sqlPrincipal.append(" INTO VARIAVEL_CONTEXTO;  ");

				sqlPrincipal.append(" IF ((:VARIAVEL_CONTEXTO IS NULL) OR (TRIM(:VARIAVEL_CONTEXTO) = '')) THEN VARIAVEL_CONTEXTO = USER; ");

				if (auditInsert == EnumSimNao.SIM) {
					sqlPrincipal.append(" IF (INSERTING) THEN  ");
					sqlPrincipal.append(" BEGIN ");
					sqlPrincipal.append(" ID_OPERACAO = 1;  ");
					sqlPrincipal.append(" CHAVE_AUDITORIA =  " + nomeCampoChaveAudit.replace("XXX.", "new.") + ";");
					sqlPrincipal.append(" ID_NA_USUARIO_AUDIT = NEW.ID_NA_USUARIO_AUDITORIA; ");
					sqlPrincipal.append(" ID_NA_SISTEMA_AUDIT = NEW.ID_NA_SISTEMA_AUDITORIA; ");
					sqlPrincipal.append(" END ");
				}
				if (auditUpdate == EnumSimNao.SIM) {
					if (auditInsert == EnumSimNao.SIM) {
						sqlPrincipal.append(" ELSE ");
					}
					sqlPrincipal.append(" IF (UPDATING) THEN ");
					sqlPrincipal.append(" BEGIN  ");
					sqlPrincipal.append(" ID_OPERACAO = 2;  ");
					sqlPrincipal.append(" CHAVE_AUDITORIA =  " + nomeCampoChaveAudit.replace("XXX.", "new.") + ";");
					sqlPrincipal.append(" ID_NA_USUARIO_AUDIT = NEW.ID_NA_USUARIO_AUDITORIA; ");
					sqlPrincipal.append(" ID_NA_SISTEMA_AUDIT = NEW.ID_NA_SISTEMA_AUDITORIA; ");
					sqlPrincipal.append(" END ");

				}
				if (auditDelete == EnumSimNao.SIM) {
					if (auditInsert == EnumSimNao.SIM || auditUpdate == EnumSimNao.SIM) {
						sqlPrincipal.append(" ELSE ");
					}
					sqlPrincipal.append(" IF (DELETING) THEN ");
					sqlPrincipal.append(" BEGIN ");
					sqlPrincipal.append(" ID_OPERACAO = 3;  ");
					sqlPrincipal.append(" CHAVE_AUDITORIA = " + nomeCampoChaveAudit.replace("XXX.", "old.") + "; ");
					sqlPrincipal.append(" ID_NA_USUARIO_AUDIT = OLD.ID_NA_USUARIO_AUDITORIA; ");
					sqlPrincipal.append(" ID_NA_SISTEMA_AUDIT = OLD.ID_NA_SISTEMA_AUDITORIA; ");
					sqlPrincipal.append(" END ");
				}

				sqlPrincipal.append(" IF (ID_NA_USUARIO_AUDIT IS NOT NULL) THEN ");
				sqlPrincipal.append(" BEGIN  ");
				sqlPrincipal.append(" SELECT U.LOGIN FROM NA_USUARIO U  ");
				sqlPrincipal.append(" WHERE U.ID = :ID_NA_USUARIO_AUDIT INTO :USUARIO_JAVA; ");
				sqlPrincipal.append(" IF (USUARIO_JAVA IS NOT NULL) THEN  ");
				sqlPrincipal.append(" VARIAVEL_CONTEXTO = :USUARIO_JAVA;  ");
				sqlPrincipal.append(" END ");

				sqlPrincipal.append(" ID_AUDITORIA = GEN_ID(AUDITORIA_SISTEMA, 1); ");
				sqlPrincipal.append(" INSERT INTO AUDITORIA_SISTEMA (COD_AUDITORIA, TABELA, CHAVE_REGISTRO, DATA_AUDITORIA, HORA_AUDITORIA, USUARIO, TIPO_OPERACAO)  ");
				sqlPrincipal.append(" VALUES (:ID_AUDITORIA,'" + nomeTabela.toUpperCase().trim() + "', :CHAVE_AUDITORIA, 'NOW', 'NOW', :VARIAVEL_CONTEXTO, :ID_OPERACAO); ");

				if (auditInsert == EnumSimNao.SIM && auditUpdate == EnumSimNao.SIM && auditDelete == EnumSimNao.SIM) {

					List<Object[]> listaCampos = getCamposTabela(em, toolsDb, nomeSchema, nomeTabela);

					Integer posicaoCampo = 0;

					for (Object[] campo : listaCampos) {

						// campo[0] Posição Campo
						// campo[1] Nome Coluna
						// campo[2] Tipo Campo

						if (!String.valueOf(campo[2]).equals("BLOB")) {
							quantidadeCampos++;
							posicaoCampo = Integer.parseInt(String.valueOf(campo[0]));

							sqlPrincipal.append(" EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO," + String.valueOf(campo[0]).trim() + ",'"
									+ String.valueOf(campo[1]).trim() + "' ");

							if (auditUpdate == EnumSimNao.SIM)
								sqlPrincipal.append(",OLD." + String.valueOf(campo[1]).trim() + ",NEW." + String.valueOf(campo[1]).trim());
							else {
								if (auditDelete == EnumSimNao.SIM)
									sqlPrincipal.append(" ,OLD." + String.valueOf(campo[1]).trim());
								else
									sqlPrincipal.append(" ,NULL ");
								if (auditInsert == EnumSimNao.SIM)
									sqlPrincipal.append(" ,NEW." + String.valueOf(campo[1]).trim());
								else
									sqlPrincipal.append(" ,NULL");
							}
							sqlPrincipal.append(" ,:CONTADOR) RETURNING_VALUES :CONTADOR; ");
						}
					}
					sqlPrincipal.append(" IF (:CONTADOR > 0) THEN  ");
					sqlPrincipal.append(" BEGIN ");
					sqlPrincipal.append(" if (ID_NA_SISTEMA_AUDIT IS NOT NULL) then VARIAVEL_CONTEXTO = CAST(:ID_NA_SISTEMA_AUDIT AS VARCHAR(10)); ELSE  ");
					sqlPrincipal.append("  SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT('USER_SESSION', 'SISTEMA_ID'),'')) FROM 1 FOR 30)) AS CONTEUDO ");
					sqlPrincipal.append("  	   FROM RDB$DATABASE ");
					sqlPrincipal.append("  INTO VARIAVEL_CONTEXTO; ");

					posicaoCampo++;
					sqlPrincipal.append(" EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO, " + posicaoCampo
							+ ", 'CODIGO DO SISTEMA', NULL, :VARIAVEL_CONTEXTO, :CONTADOR) RETURNING_VALUES :CONTADOR;  ");
					sqlPrincipal.append(" SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT('USER_SESSION', 'SISTEMA_VERSAO'),'')) FROM 1 FOR 30)) AS CONTEUDO  ");
					sqlPrincipal.append(" FROM RDB$DATABASE  ");
					sqlPrincipal.append(" INTO VARIAVEL_CONTEXTO;");

					posicaoCampo++;
					sqlPrincipal.append(" EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO, " + posicaoCampo
							+ ", 'VERSAO DO SISTEMA', NULL, :VARIAVEL_CONTEXTO, :CONTADOR) RETURNING_VALUES :CONTADOR; ");
					sqlPrincipal.append(" SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT('USER_SESSION', 'TERMINAL_IP'),'')) FROM 1 FOR 30)) AS CONTEUDO  ");
					sqlPrincipal.append(" FROM RDB$DATABASE  ");
					sqlPrincipal.append(" INTO VARIAVEL_CONTEXTO; ");

					posicaoCampo++;
					sqlPrincipal.append(" EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO, " + posicaoCampo
							+ ", 'IP DO TERMINAL', NULL, :VARIAVEL_CONTEXTO, :CONTADOR) RETURNING_VALUES :CONTADOR; ");
					sqlPrincipal.append(" SELECT TRIM(SUBSTRING(TRIM(COALESCE(RDB$GET_CONTEXT('USER_SESSION', 'TERMINAL_NOME'),'')) FROM 1 FOR 30)) AS CONTEUDO  ");
					sqlPrincipal.append(" FROM RDB$DATABASE  ");
					sqlPrincipal.append(" INTO VARIAVEL_CONTEXTO; ");

					posicaoCampo++;
					sqlPrincipal.append(" EXECUTE PROCEDURE SP_AUDIT$INSERE_ITEM(:ID_AUDITORIA, :ID_OPERACAO, " + posicaoCampo
							+ ", 'NOME DO TERMINAL', NULL, :VARIAVEL_CONTEXTO, :CONTADOR) RETURNING_VALUES :CONTADOR;  ");
					sqlPrincipal.append(" END  ");
					sqlPrincipal.append(" ELSE ");
					sqlPrincipal.append(" DELETE FROM AUDITORIA_SISTEMA WHERE COD_AUDITORIA = :ID_AUDITORIA;   ");

					sqlPrincipal.append(" if ((inserting or updating) AND (ID_NA_USUARIO_AUDIT IS NOT NULL OR ID_NA_SISTEMA_AUDIT IS NOT NULL)) then  ");
					sqlPrincipal.append(" UPDATE " + nomeTabela.toUpperCase() + " SET ID_NA_USUARIO_AUDITORIA = NULL, ID_NA_SISTEMA_AUDITORIA = NULL " + sqlWhereUpdateAudit);
					sqlPrincipal.append(" END");

					getConnection(em).createStatement().execute(sqlPrincipal.toString().toUpperCase());
				}
			}
		}

	}

	@Override
	public void dropTriggersAuditria(EntityManager em, IToolsDB toolsDb, String nomeSchema, String nomeTabela) throws Exception {

		StringBuilder sql = new StringBuilder();
		StringBuilder drop;

		sql.append(" SELECT TRIM(T.RDB$TRIGGER_NAME) FROM RDB$TRIGGERS T WHERE (SUBSTRING(UPPER(RDB$TRIGGER_NAME) FROM 1 FOR 6) = 'AUDIT$') ");

		if (!nomeTabela.equalsIgnoreCase(""))
			sql.append(" AND UPPER(T.RDB$RELATION_NAME) = :TABELA_NOME ");

		sql.append(" ORDER BY T.RDB$TRIGGER_NAME ");

		Query query = em.createNativeQuery(sql.toString());

		if (!nomeTabela.equalsIgnoreCase(""))
			query.setParameter("TABELA_NOME", nomeTabela.toUpperCase());

		@SuppressWarnings("unchecked")
		List<String> listaTriggers = query.getResultList();

		for (String nameTrigger : listaTriggers) {
			drop = new StringBuilder();
			drop.append(" DROP TRIGGER " + nameTrigger);
			em.createNativeQuery(drop.toString()).executeUpdate();
		}

	}

	@Override
	public void desativaTriggersAuditoria(EntityManager em, IToolsDB toolsDb, String nomeSchema, String identificador, String nomeTabela) throws Exception {

		StringBuilder sql = new StringBuilder();

		sql.append(" CREATE OR ALTER TRIGGER AUDIT$" + identificador.toUpperCase());
		sql.append(" FOR " + nomeTabela.toUpperCase() + " ASCII_CHAR(13)");
		sql.append(" INACTIVE AFTER INSERT OR UPDATE OR DELETE POSITION 32765 AS ");
		sql.append(" BEGIN  ");
		sql.append(" END ");

		em.createNativeQuery(sql.toString()).executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getCamposTabela(EntityManager em, IToolsDB toolsDb, String nomeSchema, String nomeTabela) {

		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT RF.RDB$FIELD_POSITION, RF.RDB$FIELD_NAME, T.RDB$TYPE_NAME ");
		sql.append(" FROM RDB$RELATION_FIELDS RF ");
		sql.append(" LEFT JOIN RDB$FIELDS F ON (F.RDB$FIELD_NAME = RF.RDB$FIELD_SOURCE) ");
		sql.append(" LEFT JOIN RDB$TYPES T ON (T.RDB$FIELD_NAME = 'RDB$FIELD_TYPE' AND T.RDB$TYPE = F.RDB$FIELD_TYPE) ");
		sql.append(" WHERE RF.RDB$RELATION_NAME = :TABELA_NOME AND ");
		sql.append(" TRIM(RF.RDB$FIELD_NAME) <> 'ID_NA_USUARIO_AUDITORIA' AND ");
		sql.append(" TRIM(RF.RDB$FIELD_NAME) <> 'ID_NA_SISTEMA_AUDITORIA' AND ");
		sql.append(" TRIM(T.RDB$TYPE_NAME) <> 'BLOB' AND ");
		sql.append(" (SELECT COUNT(*) FROM NA_AUDITORIA_CAMPO AC ");
		sql.append(" WHERE AC.TABELA = :TABELA_NOME AND ");
		sql.append(" AC.CAMPO = RF.RDB$FIELD_NAME) = 0 ");
		sql.append(" ORDER BY RF.RDB$FIELD_POSITION ");

		try {
			return em.createNativeQuery(sql.toString()).setParameter("TABELA_NOME", nomeTabela.toUpperCase()).getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public void defineTabelasAuditaveis(EntityManager em) {

		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO NA_AUDITORIA_TABELA (ID, IDENTIFICADOR, NOME, NOME_AMIGAVEL, AUDIT_INSERT, AUDIT_UPDATE, AUDIT_DELETE) ");
		sql.append(" SELECT DISTINCT GEN_ID(SEQ_NA_AUDITORIA_TABELA, 1) as ID, HASH(TRIM(F.RDB$RELATION_NAME)) as IDENTIFICADOR, ");
		sql.append(" TRIM(F.RDB$RELATION_NAME) as NOME, TRIM(F.RDB$RELATION_NAME) as NOME_AMIGAVEL, 'S' as AUDIT_INSERT, 'S' as AUDIT_UPDATE, 'S' as AUDIT_DELETE ");
		sql.append(" FROM RDB$RELATIONS F ");
		sql.append(" WHERE F.RDB$RELATION_NAME IS NOT NULL AND ");
		sql.append(" COALESCE(F.RDB$RELATION_TYPE, 0) = 0 AND F.RDB$VIEW_SOURCE IS NULL AND ");
		sql.append(" SUBSTRING(F.RDB$RELATION_NAME FROM 1 FOR 4) <> 'RDB$' AND ");
		sql.append(" (SELECT COUNT(*) FROM NA_AUDITORIA_TABELA AST WHERE AST.NOME = F.RDB$RELATION_NAME) = 0 ");
		sql.append(" AND SUBSTRING(TRIM(F.RDB$RELATION_NAME) FROM 1 FOR 17) <> 'AUDITORIA_SISTEMA' ");
		sql.append(" AND SUBSTRING(TRIM(F.RDB$RELATION_NAME) FROM 1 FOR 17) <> 'AUDITORIA_SISTEMA_ITEM' ");
		sql.append(" AND TRIM(F.RDB$RELATION_NAME) <> 'NA_AUDITORIA' ");
		sql.append(" AND TRIM(F.RDB$RELATION_NAME) <> 'NA_AUDITORIA_ITEM' ");
		sql.append(" AND TRIM(F.RDB$RELATION_NAME) <> 'NA_AUDITORIA_TABELA' ");
		sql.append(" ORDER BY F.RDB$RELATION_NAME; ");

		em.createNativeQuery(sql.toString()).executeUpdate();

		sql = new StringBuilder();
		sql.append(" DELETE FROM NA_AUDITORIA_TABELA ");
		sql.append(" WHERE (SELECT COUNT(*) ");
		sql.append(" FROM RDB$RELATIONS F  ");
		sql.append(" WHERE F.RDB$RELATION_NAME IS NOT NULL AND ");
		sql.append(" COALESCE(F.RDB$RELATION_TYPE, 0) = 0 AND F.RDB$VIEW_SOURCE IS NULL AND ");
		sql.append(" SUBSTRING(F.RDB$RELATION_NAME FROM 1 FOR 4) <> 'RDB$' AND ");
		sql.append(" UPPER(TRIM(NA_AUDITORIA_TABELA.NOME)) = UPPER(TRIM(F.RDB$RELATION_NAME))) = 0; ");

		em.createNativeQuery(sql.toString()).executeUpdate();
	}

	public static Connection getConnection(EntityManager em) throws SQLException {

		HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) em.getEntityManagerFactory();
		SessionFactoryImpl sessionFactory = (SessionFactoryImpl) hemf.getSessionFactory();
		return sessionFactory.getSessionFactoryOptions().getServiceRegistry().getService(ConnectionProvider.class).getConnection();
	}

	@Override
	public void getGeraTrigger(EntityManager em, NaAuditoriaTabela auditoriaTabela) throws Exception {

		StringBuilder sql = new StringBuilder();

		sql.append(" execute procedure SP_AUDIT$GERA_TRIGGER( cast( :TABELA_ID as varchar(50) ), cast( :TABELA_NOME as varchar(31) ),");
		sql.append(" cast( :DROP_TRIGGER as varchar(1)),cast(:AUDIT_INSERT as varchar(1) ), cast( :AUDIT_UPDATE as varchar(1)), cast( :AUDIT_DELETE as varchar(1)) )");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("TABELA_ID", auditoriaTabela.getIdentificador());
		query.setParameter("TABELA_NOME", auditoriaTabela.getNome());
		query.setParameter("DROP_TRIGGER", "S");
		query.setParameter("AUDIT_INSERT", auditoriaTabela.getAuditInsert().toChar());
		query.setParameter("AUDIT_UPDATE", auditoriaTabela.getAuditUpdate().toChar());
		query.setParameter("AUDIT_DELETE", auditoriaTabela.getAuditDelete().toChar());

		query.executeUpdate();

	}

	@Override
	public void dropEstruturaAuditoria(EntityManager em) throws Exception {

		dropTriggersAuditria(em, null, "", "");
		em.createNativeQuery(" DROP TRIGGER TGR_AUDIT$_AUDITORIA_TABELA_AU ").executeUpdate();
		em.createNativeQuery(" DROP PROCEDURE SP_AUDIT$CRIA_CAMPOS_AUDITORIA ").executeUpdate();
		em.createNativeQuery(" DROP PROCEDURE SP_AUDIT$DISCONNECT_BASE ").executeUpdate();
		em.createNativeQuery(" DROP PROCEDURE SP_AUDIT$CRIA_TRIGGER ").executeUpdate();
		em.createNativeQuery(" DROP PROCEDURE SP_AUDIT$GERA_TRIGGER ").executeUpdate();
		em.createNativeQuery(" DROP PROCEDURE SP_AUDIT$INSERE_ITEM ").executeUpdate();

	}

}
