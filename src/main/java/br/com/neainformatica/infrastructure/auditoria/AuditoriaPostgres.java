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

public class AuditoriaPostgres implements AuditoriaTools {

	@SuppressWarnings("unused")
	private static final String SCHEMA_AUDITORIA = "auditoria";

	public void criaCampos(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "fc_audit$cria_campos_auditoria"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" create function auditoria.FC_AUDIT$CRIA_CAMPOS_AUDITORIA( SCHEMA_NOME VARCHAR(32), TABELA_NOME VARCHAR(31) )");
		sql.append(" returns void  as $body$ ");
		sql.append(" declare SQL_CABECALHO varchar; begin ");
		sql.append(" if((select count(*) from information_schema.columns cl where lower(cl.table_name) = lower(TABELA_NOME) ");
		sql.append(" and lower(cl.table_schema) = lower(SCHEMA_NOME) ");
		sql.append(" and trim(lower(cl.column_name)) = 'id_na_usuario_auditoria') = 0) then ");
		sql.append(" SQL_CABECALHO = 'ALTER TABLE ' || SCHEMA_NOME || '.' || TABELA_NOME || ' ADD COLUMN id_na_usuario_auditoria INTEGER;'; ");
		sql.append(" EXECUTE SQL_CABECALHO; ");
		sql.append(" end if; ");
		sql.append(" if((select count(*) from information_schema.columns cl where lower(cl.table_name) = lower(TABELA_NOME) ");
		sql.append(" and lower(cl.table_schema) = lower(SCHEMA_NOME) ");
		sql.append(" and trim(lower(cl.column_name)) = 'id_na_sistema_auditoria') = 0) then ");
		sql.append(" SQL_CABECALHO = 'ALTER TABLE ' || SCHEMA_NOME || '.' || TABELA_NOME || ' ADD COLUMN id_na_sistema_auditoria INTEGER;'; ");
		sql.append(" EXECUTE SQL_CABECALHO; ");
		sql.append(" end if; end; $body$ language 'plpgsql'; ");
		sql.append(" ");

		em.createNativeQuery(sql.toString()).executeUpdate();
	}

	public void criaInsereItem(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "fc_audit$insere_item"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" CREATE FUNCTION auditoria.fc_audit$insere_item ( ");
		sql.append(" id_auditoria integer, id_operacao integer, id_sequencial integer, nome_campo varchar, valor_old varchar, valor_new varchar, contador integer )");
		sql.append(" RETURNS integer AS $body$ ");
		sql.append(" declare  pode_inserir integer; alterou integer; ");
		sql.append(" BEGIN ");
		sql.append(" pode_inserir = 0; ");
		sql.append(" alterou = contador; ");
		sql.append(" IF((id_operacao = 1) and (TRIM(COALESCE(valor_new,'')) <> '' )) THEN ");
		sql.append(" valor_old = NULL;  pode_inserir = 1; ");
		sql.append(" ELSEIF ((id_operacao = 2 ) and (TRIM(COALESCE(valor_new, '')) <> trim(COALESCE(valor_old,'')))) THEN ");
		sql.append(" pode_inserir = 1; ");
		sql.append(" ELSEIF ((ID_OPERACAO = 3) AND (TRIM(COALESCE(VALOR_NEW, '')) <> '') AND (TRIM(COALESCE(VALOR_OLD, '')) = '')) THEN ");
		sql.append(" valor_old = valor_new; valor_new = null; pode_inserir = 1; ");
		sql.append(" ELSEIF ((ID_OPERACAO = 3) AND (TRIM(COALESCE(VALOR_OLD, '')) <> '')) THEN ");
		sql.append(" valor_new = null; pode_inserir = 1; ");
		sql.append(" END IF; ");
		sql.append(" IF(pode_inserir = 1 ) THEN ");
		sql.append(" IF (TRIM(COALESCE(VALOR_OLD, '')) <> '') THEN ");
		sql.append(" VALOR_OLD = SUBSTRING(VALOR_OLD FROM 1 FOR 250);  ");
		sql.append(" END IF; ");
		sql.append(" IF (TRIM(COALESCE(VALOR_NEW, '')) <> '') THEN ");
		sql.append(" VALOR_NEW = SUBSTRING(VALOR_NEW FROM 1 FOR 250); ");
		sql.append(" END IF; ");
		sql.append(" INSERT INTO auditoria.auditoria_sistema_item(COD_AUDITORIA, SEQUENCIAL, CAMPO, VALOR_ANTERIOR, VALOR_NOVO) ");
		sql.append(" VALUES (ID_AUDITORIA,ID_SEQUENCIAL,NOME_CAMPO,VALOR_OLD,VALOR_NEW); ");
		sql.append(" ALTEROU = CONTADOR + 1; ");
		sql.append(" END IF; ");
		sql.append(" RETURN ALTEROU; ");
		sql.append(" END ");
		sql.append(" $body$ LANGUAGE 'plpgsql' VOLATILE CALLED ON NULL INPUT SECURITY INVOKER COST 100; ");

		em.createNativeQuery(sql.toString()).executeUpdate();
	}

	public void criaGeraTrigger(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "fc_audit$gera_trigger"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" create function auditoria.FC_AUDIT$GERA_TRIGGER( TABELA_ID varchar(50),TABELA_NOME varchar(31),DROP_TRIGGER varchar(1),");
		sql.append(" AUDIT_INSERT varchar(1), AUDIT_UPDATE varchar(1), AUDIT_DELETE varchar(1),SCHEMA_NOME VARCHAR(31)) ");
		sql.append(" RETURNS void AS $body$ declare ");
		sql.append(" DTRIGGER record; CHAVE_AUDITORIA record; CAMPOS_TABELA record; DESATIVA_TRIGGER integer; SQL_CABECALHO varchar; SHCHEMA_TABELA varchar(62); ");
		sql.append(" NOME_CAMPO_CHAVE_AUDIT varchar(620); QTDE_CAMPO integer; SQL_CAMPOS01 varchar(32765); SQL_WHERE_UPDATE_AUDITORIA VARCHAR; ");
		sql.append(" CONTADOR INTEGER; SQL_COMPLETO bytea; SQL_CAMPOSINSERT varchar; SQL_CAMPOSUPDATE varchar; SQL_CAMPOSDELETE varchar; ");
		sql.append(" begin ");
		sql.append(" SCHEMA_NOME = lower(SCHEMA_NOME); ");
		sql.append(" TABELA_NOME = lower(TABELA_NOME); ");
		sql.append(" nome_campo_chave_audit = ''; ");
		sql.append(" qtde_campo = 0; ");
		sql.append(" sql_campos01 = ''; ");
		sql.append(" CONTADOR = 0; ");
		sql.append(" SQL_CAMPOSINSERT = ''; ");
		sql.append(" SQL_CAMPOSUPDATE = ''; ");
		sql.append(" SQL_CAMPOSDELETE = ''; ");
		sql.append(" SQL_WHERE_UPDATE_AUDITORIA = ' WHERE '; ");
		sql.append(" SHCHEMA_TABELA = SCHEMA_NOME || '.' || TABELA_NOME; ");
		sql.append(" if(AUDIT_INSERT = 'N' and AUDIT_UPDATE = 'N' and AUDIT_DELETE = 'N') THEN ");
		sql.append(" desativa_trigger = 1; ");
		sql.append(" else ");
		sql.append(" desativa_trigger = 0; ");
		sql.append(" end if; ");
		sql.append(" PERFORM  auditoria.FC_AUDIT$CRIA_CAMPOS_AUDITORIA(SCHEMA_NOME,TABELA_NOME); ");
		sql.append(" if(DROP_TRIGGER = 'S') then ");
		sql.append(" for dtrigger in select distinct 'drop trigger ' || lower(tgg.trigger_name) || ' on ' || cast(lower(tgg.event_object_schema) as VARCHAR(31)) || '.' || CAST(lower(tgg.event_object_table) as VARCHAR(31))||';' as drop ,      'drop function ' || cast(lower(tgg.event_object_schema) as VARCHAR(31)) || '.' || lower(tgg.trigger_name) || '();' as dropfunction ");
		sql.append(" from information_schema.triggers tgg ");
		sql.append(" WHERE (SUBSTRING(lower(tgg.trigger_name) FROM 1 FOR 6) = 'audit$') AND ");
		sql.append(" lower(tgg.event_object_table) = TABELA_NOME ");
		sql.append(" and lower(tgg.event_object_schema) = SCHEMA_NOME ");
		sql.append(" loop execute dtrigger.drop; execute dtrigger.dropfunction; end loop; end if; ");
		sql.append(" IF(desativa_trigger = 1) then  ");
		sql.append(" SQL_CABECALHO = 'CREATE FUNCTION ' || lower(SCHEMA_NOME) || '.audit$' || lower(TABELA_ID) || ' ()RETURNS trigger AS $bo'||'dy$ DECLARE BEGIN END; $bo'||'dy$ LANGUAGE ' || chr(39) || 'plpgsql' || chr(39) ; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || 'VOLATILE CALLED ON NULL INPUT SECURITY INVOKER; '; ");
		sql.append(" execute SQL_CABECALHO; /*Criação da Trigger*/ ");
		sql.append(" SQL_CABECALHO = 'CREATE TRIGGER audit$' || TABELA_ID || ' AFTER INSERT OR UPDATE OR DELETE ' ;");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' on ' || SHCHEMA_TABELA || ' FOR EACH STATEMENT EXECUTE PROCEDURE '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || SCHEMA_NOME || '.audit$' || TABELA_ID || '();'; execute SQL_CABECALHO; /*Desabilita a trigger recém criada*/");
		sql.append(" SQL_CABECALHO = ' ALTER TABLE '|| SHCHEMA_TABELA || ' DISABLE TRIGGER ' || 'audit$' || TABELA_ID || ';'; ");
		sql.append(" execute SQL_CABECALHO; elseif(AUDIT_INSERT = 'S' or AUDIT_UPDATE ='S' or AUDIT_DELETE = 'S') then ");
		sql.append(" /*Identifica quais as chaves primárias dessa tabela que serão usadas como chave do registro de auditoira*/ ");
		sql.append(" for chave_auditoria in select kcu.column_name as nome_campo , KCU.ordinal_position as posicao_campo ");
		sql.append(" from information_schema.table_constraints tc ");
		sql.append(" join information_schema.key_column_usage kcu on kcu.table_schema = tc.table_schema ");
		sql.append(" and kcu.table_name = tc.table_name and kcu.constraint_name = tc.constraint_name ");
		sql.append(" where lower(tc.table_name) = lower(TABELA_NOME) and lower(tc.table_schema) = lower(SCHEMA_NOME) ");
		sql.append(" and upper(tc.constraint_type) = 'PRIMARY KEY' ");
		sql.append(" ORDER BY KCU.ordinal_position ");
		sql.append(" LOOP ");
		sql.append(" IF(chave_auditoria.posicao_campo > 1) THEN ");
		sql.append(" nome_campo_chave_audit = nome_campo_chave_audit || ' || '';'' || '; ");
		sql.append(" SQL_WHERE_UPDATE_AUDITORIA = SQL_WHERE_UPDATE_AUDITORIA || ' AND '; ");
		sql.append(" END IF; ");
		sql.append(" NOME_CAMPO_CHAVE_AUDIT = NOME_CAMPO_CHAVE_AUDIT || 'XXX.' || CHAVE_AUDITORIA.nome_campo; ");
		sql.append(" SQL_WHERE_UPDATE_AUDITORIA = SQL_WHERE_UPDATE_AUDITORIA || CHAVE_AUDITORIA.nome_campo || ' =  NEW.' || chave_auditoria.nome_campo; ");
		sql.append(" END LOOP; ");
		sql.append(" SQL_WHERE_UPDATE_AUDITORIA = SQL_WHERE_UPDATE_AUDITORIA || ' ;'; ");
		sql.append(" IF(nome_campo_chave_audit <> '') THEN ");
		sql.append(" SQL_CABECALHO = 'CREATE FUNCTION ' || lower(SCHEMA_NOME) || '.audit$' || lower(TABELA_ID) || ' ()RETURNS trigger AS $bo'||'dy$ DECLARE ' ; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_OPERACAO INTEGER;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_AUDITORIA INTEGER;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' CHAVE_AUDITORIA VARCHAR(150);'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' CONTADOR INTEGER;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_NA_USUARIO_AUDIT INTEGER;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_NA_SISTEMA_AUDIT INTEGER;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' LOGIN_USUARIO_LOGADO VARCHAR(30);'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO ||  ' BEGIN '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_NA_USUARIO_AUDIT = NULL;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_NA_SISTEMA_AUDIT = NULL;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' LOGIN_USUARIO_LOGADO = '''';'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' CONTADOR = 0;'; ");
		sql.append(" IF(AUDIT_INSERT = 'S') THEN ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO ||  ' IF (TG_OP = ''INSERT'') THEN '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_OPERACAO = 1; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' CHAVE_AUDITORIA = ' || REPLACE(NOME_CAMPO_CHAVE_AUDIT, 'XXX.', 'NEW.'); ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || '; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_NA_USUARIO_AUDIT = NEW.ID_NA_USUARIO_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_NA_SISTEMA_AUDIT = NEW.ID_NA_SISTEMA_AUDITORIA; '; ");
		sql.append(" END IF; ");
		sql.append(" IF(AUDIT_UPDATE = 'S') THEN ");
		sql.append(" IF (AUDIT_INSERT = 'S') THEN ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO ||  ' ELSEIF (TG_OP = ''UPDATE'') THEN '; ");
		sql.append(" ELSE ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO ||  ' IF (TG_OP = ''UPDATE'') THEN '; ");
		sql.append(" END IF; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || '  ID_OPERACAO = 2; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || '  CHAVE_AUDITORIA = ' || REPLACE(NOME_CAMPO_CHAVE_AUDIT, 'XXX.', 'NEW.') || '; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || '  ID_NA_USUARIO_AUDIT = NEW.ID_NA_USUARIO_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || '  ID_NA_SISTEMA_AUDIT = NEW.ID_NA_SISTEMA_AUDITORIA; '; ");
		sql.append(" END IF; ");
		sql.append(" IF(AUDIT_DELETE = 'S') THEN ");
		sql.append(" IF (AUDIT_INSERT = 'S' OR AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO ||  ' ELSEIF (TG_OP = ''DELETE'' OR TG_OP = ''TRUNCATE'') THEN '; ");
		sql.append(" ELSE ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO ||  ' IF (TG_OP = ''DELETE'' OR TG_OP = ''TRUNCATE'') THEN '; ");
		sql.append(" END IF; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_OPERACAO = 3; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' CHAVE_AUDITORIA = ' || REPLACE(NOME_CAMPO_CHAVE_AUDIT, 'XXX.', 'OLD.') || '; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_NA_USUARIO_AUDIT = OLD.ID_NA_USUARIO_AUDITORIA; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' ID_NA_SISTEMA_AUDIT = OLD.ID_NA_SISTEMA_AUDITORIA; '; ");
		sql.append(" END IF; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' END IF; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' IF(ID_NA_USUARIO_AUDIT IS NOT NULL) THEN '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' SELECT U.LOGIN INTO LOGIN_USUARIO_LOGADO FROM SEGURANCA.NA_USUARIO U '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' WHERE U.ID = ID_NA_USUARIO_AUDIT;'; ");
		sql.append(" /*Implementar Variavel de CONTEXTO*/ ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' END IF; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' IF (LOGIN_USUARIO_LOGADO = '''' OR LOGIN_USUARIO_LOGADO IS NULL) THEN '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' SELECT CURRENT_USER INTO LOGIN_USUARIO_LOGADO FROM USER ;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' END IF;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' SELECT NEXTVAL(''AUDITORIA.SEQ_AUDITORIA_SISTEMA'') INTO ID_AUDITORIA ;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' INSERT INTO AUDITORIA.AUDITORIA_SISTEMA (COD_AUDITORIA, TABELA, CHAVE_REGISTRO, DATA_AUDITORIA, HORA_AUDITORIA, USUARIO, TIPO_OPERACAO) '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' VALUES (ID_AUDITORIA, ''' || SHCHEMA_TABELA || '''' || ', CHAVE_AUDITORIA, CURRENT_DATE, CURRENT_DATE, LOGIN_USUARIO_LOGADO, ID_OPERACAO); '; ");
		sql.append(" IF(AUDIT_INSERT = 'S' OR AUDIT_UPDATE = 'S' OR AUDIT_DELETE = 'S') THEN ");
		sql.append(" SQL_CAMPOSINSERT = ' IF (TG_OP = ''INSERT'') THEN '; ");
		sql.append(" SQL_CAMPOSUPDATE = ' ELSEIF (TG_OP = ''UPDATE'') THEN '; ");
		sql.append(" SQL_CAMPOSDELETE = ' ELSEIF (TG_OP = ''DELETE'' OR TG_OP = ''TRUNCATE'') THEN '; ");
		sql.append(" FOR CAMPOS_TABELA IN ");
		sql.append(" select cl.ordinal_position as posicao , trim(cl.column_name) as nome_coluna, TRIM(cl.data_type) as tipo_campo from information_schema.columns cl ");
		sql.append(" where lower(cl.table_name) = lower(TABELA_NOME) and lower(cl.table_schema) = lower(SCHEMA_NOME) ");
		sql.append(" and trim(lower(cl.column_name)) <> 'id_na_usuario_auditoria' and trim(lower(cl.column_name)) <> 'id_na_sistema_auditoria' ");
		sql.append(" and trim(lower(cl.data_type)) <> 'bytea' ");
		sql.append(" and (select count(*) from auditoria.na_auditoria_campo ac ");
		sql.append(" where trim(lower(ac.tabela)) = lower(TABELA_NOME) ");
		sql.append(" and trim(lower(ac.campo)) = trim(lower(cl.column_name)) and lower(ac.nome_schema) = lower(SCHEMA_NOME) ) = 0 ");
		sql.append(" ORDER BY cl.ordinal_position ");
		sql.append(" LOOP ");
		sql.append(" IF(CAMPOS_TABELA.tipo_campo <> 'bytea') THEN ");
		sql.append(" qtde_campo = qtde_campo + 1; ");
		sql.append(" IF(AUDIT_INSERT = 'S') THEN ");
		sql.append(" SQL_CAMPOSINSERT = SQL_CAMPOSINSERT || 'select auditoria.fc_audit$insere_item( ID_AUDITORIA , ID_OPERACAO , ' || CAMPOS_TABELA.POSICAO || ', CAST(' || chr(39) || CAMPOS_TABELA.NOME_COLUNA || chr(39) || ' AS VARCHAR) '; ");
		sql.append(" SQL_CAMPOSINSERT = SQL_CAMPOSINSERT || ',NULL,CAST(NEW.' || CAMPOS_TABELA.NOME_COLUNA || ' AS VARCHAR), CONTADOR) into CONTADOR;'; ");
		sql.append(" END IF; ");
		sql.append(" IF(AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CAMPOSUPDATE = SQL_CAMPOSUPDATE || 'select auditoria.fc_audit$insere_item( ID_AUDITORIA , ID_OPERACAO , ' || CAMPOS_TABELA.POSICAO || ', CAST(' || chr(39) || CAMPOS_TABELA.NOME_COLUNA || chr(39) || ' AS VARCHAR) ';");
		sql.append(" SQL_CAMPOSUPDATE = SQL_CAMPOSUPDATE || ',CAST(OLD.'|| CAMPOS_TABELA.NOME_COLUNA ||' AS VARCHAR), CAST(NEW.' || CAMPOS_TABELA.NOME_COLUNA || ' AS VARCHAR), CONTADOR) into CONTADOR;'; ");
		sql.append(" END IF; ");
		sql.append(" IF (AUDIT_DELETE = 'S') THEN ");
		sql.append(" SQL_CAMPOSDELETE = SQL_CAMPOSDELETE || 'select auditoria.fc_audit$insere_item( ID_AUDITORIA , ID_OPERACAO , ' || CAMPOS_TABELA.POSICAO || ', CAST(' || chr(39) || CAMPOS_TABELA.NOME_COLUNA || chr(39) || ' AS VARCHAR) '; ");
		sql.append(" SQL_CAMPOSDELETE = SQL_CAMPOSDELETE || ',CAST(OLD.'|| CAMPOS_TABELA.NOME_COLUNA ||' AS VARCHAR),NULL, CONTADOR) into CONTADOR;'; ");
		sql.append(" END IF; ");
		sql.append(" END IF; ");
		sql.append(" END LOOP; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || SQL_CAMPOSINSERT || SQL_CAMPOSUPDATE || SQL_CAMPOSDELETE || ' END IF;' ; ");
		sql.append(" END IF; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' IF ( CONTADOR = 0 ) THEN'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' DELETE FROM AUDITORIA.AUDITORIA_SISTEMA WHERE COD_AUDITORIA = ID_AUDITORIA;  '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' END IF; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO ||' IF (TG_OP = ''DELETE'' OR TG_OP = ''TRUNCATE'') THEN RETURN OLD; ELSE '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' IF( NEW.id_na_usuario_auditoria is not NULL ) THEN '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || '   UPDATE ' || SHCHEMA_TABELA || ' SET id_na_usuario_auditoria = NULL ' || SQL_WHERE_UPDATE_AUDITORIA; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' END IF; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' IF( NEW.id_na_sistema_auditoria is not NULL ) THEN '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || '   UPDATE ' || SHCHEMA_TABELA || ' SET id_na_sistema_auditoria = NULL ' || SQL_WHERE_UPDATE_AUDITORIA; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' END IF; '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' RETURN NEW; END IF;'; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' END; $bo'||'dy$ LANGUAGE ' || chr(39) || 'plpgsql' || chr(39); ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || 'VOLATILE CALLED ON NULL INPUT SECURITY INVOKER; '; ");
		sql.append(" EXECUTE SQL_CABECALHO; ");
		sql.append(" SQL_CABECALHO = 'CREATE TRIGGER audit$' || TABELA_ID || ' AFTER ' ; ");
		sql.append(" IF (AUDIT_INSERT = 'S') THEN ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' INSERT '; ");
		sql.append(" END IF; ");
		sql.append(" IF (AUDIT_INSERT = 'S' AND AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' OR '; ");
		sql.append(" END IF; ");
		sql.append(" IF (AUDIT_UPDATE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' UPDATE '; ");
		sql.append(" END IF; ");
		sql.append(" IF ((AUDIT_INSERT = 'S' OR AUDIT_UPDATE = 'S') AND AUDIT_DELETE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' OR '; ");
		sql.append(" END IF; ");
		sql.append(" IF (AUDIT_DELETE = 'S') THEN ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' DELETE '; ");
		sql.append(" END IF; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || ' on ' || SHCHEMA_TABELA || ' FOR EACH ROW EXECUTE PROCEDURE '; ");
		sql.append(" SQL_CABECALHO = SQL_CABECALHO || SCHEMA_NOME || '.audit$' || TABELA_ID || '();'; ");
		sql.append(" EXECUTE SQL_CABECALHO; ");
		sql.append(" END IF; END IF; ");
		sql.append(" end; $body$ LANGUAGE 'plpgsql'; ");

		em.createNativeQuery(sql.toString()).executeUpdate();
	}

	public void criaTrigger(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "fc_audit$cria_trigger"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" create function auditoria.FC_AUDIT$CRIA_TRIGGER() ");
		sql.append(" returns void as $body$ declare ast record; adt record; value record; ");
		sql.append(" begin ");
		sql.append(" for ast in select distinct at.identificador as identificador, at.nome as nome , 'S' as drop_trigger, COALESCE(at.audit_insert, 'S') as audit_insert, ");
		sql.append(" COALESCE(at.audit_update, 'S') as audit_update, COALESCE(at.audit_delete, 'S') as audit_delete , at.nome_schema as nome_schema ");
		sql.append(" from auditoria.na_auditoria_tabela at ");
		sql.append(" where substring(trim(lower(at.nome)) from 1 for 17) <> 'auditoria_sistema' and ");
		sql.append(" trim(lower(at.nome)) <> 'na_auditoria' and TRIM(lower(at.nome)) <> 'na_auditoria_item' and ");
		sql.append(" (select count(*) from information_schema.tables tb where tb.table_name = at.nome and tb.table_schema = at.nome_schema) > 0 and ");
		sql.append(" (select count(*) from information_schema.table_constraints tc where tc.table_name = at.nome and tc.table_schema = at.nome_schema ");
		sql.append(" and upper(tc.constraint_type) = 'PRIMARY KEY' ) > 0 ");
		sql.append(" and (select count(*) from information_schema.triggers tgg ");
		sql.append(" where substring(lower(tgg.trigger_name) from 1 for 6) = 'audit$' and ");
		sql.append(" tgg.event_object_table = at.nome and tgg.event_object_schema = at.nome_schema) = 0 ");
		sql.append(" order by at.nome ");
		sql.append(" loop ");
		sql.append(" PERFORM auditoria.fc_audit$gera_trigger(ast.identificador, ast.nome, ast.drop_trigger, ast.audit_insert, ast.audit_update, ast.audit_delete, ast.nome_schema); ");
		sql.append(" end loop; ");
		sql.append(" for adt in select distinct at.identificador as identificador, at.nome as nome , 'S' as drop_trigger, 'N' as audit_insert,'N' as audit_update, 'N' as audit_delete , at.nome_schema as nome_schema  ");
		sql.append(" from auditoria.na_auditoria_tabela at ");
		sql.append(" where substring(trim(lower(at.nome)) from 1 for 17) <> 'auditoria_sistema' and ");
		sql.append(" trim(lower(at.nome)) <> 'na_auditoria' and TRIM(lower(at.nome)) <> 'na_auditoria_item' and ");
		sql.append(" (COALESCE(at.audit_insert, 'S') = 'N' AND ");
		sql.append(" COALESCE(at.audit_update, 'S') = 'N' AND ");
		sql.append(" COALESCE(at.audit_delete, 'S') = 'N') AND ");
		sql.append(" (select count(*) from information_schema.table_constraints tc where tc.table_name = at.nome and tc.table_schema = at.nome_schema) > 0 and  ");
		sql.append(" (select count(*) from information_schema.triggers tgg  ");
		sql.append(" join pg_trigger pgt on pgt.tgname = tgg.trigger_name  ");
		sql.append(" where substring(lower(tgg.trigger_name) from 1 for 6) = 'audit$' and ");
		sql.append(" tgg.event_object_table = at.nome and tgg.event_object_schema = at.nome_schema and  pgt.tgenabled = 'D' ) = 0 ");
		sql.append(" loop ");
		sql.append(" PERFORM auditoria.fc_audit$gera_trigger(adt.identificador, adt.nome, adt.drop_trigger, adt.audit_insert, adt.audit_update, adt.audit_delete, adt.nome_schema); ");
		sql.append(" end loop; ");
		sql.append(" end; $body$ language 'plpgsql'; ");

		em.createNativeQuery(sql.toString()).executeUpdate();

	}

	public void criaAuditoriaTabelaAu(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "fc_audit$_auditoria_tabela_au"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" CREATE FUNCTION auditoria.fc_audit$_auditoria_tabela_au() ");
		sql.append(" RETURNS trigger AS ");
		sql.append(" $body$ DECLARE ");
		sql.append(" DROP_TRIGGER CHAR(1); QTDE_PK INTEGER; PODE_GERAR INTEGER; ");
		sql.append(" BEGIN ");
		sql.append(" IF((NEW.NOME <> 'AUDITORIA_SISTEMA') AND (NEW.NOME <> 'AUDITORIA_SISTEMA_ITEM') AND ");
		sql.append(" (NEW.NOME <> 'NA_AUDITORIA') AND (NEW.NOME <> 'NA_AUDITORIA_ITEM')) THEN  ");
		sql.append(" PODE_GERAR = 0; ");
		sql.append(" IF(TG_OP = 'INSERT') THEN   ");
		sql.append(" PODE_GERAR = 1; ");
		sql.append(" ELSEIF ((TG_OP = 'UPDATE') AND ((COALESCE(NEW.AUDIT_INSERT, 'S') <> COALESCE(OLD.AUDIT_INSERT, 'S')) OR ");
		sql.append(" (COALESCE(NEW.AUDIT_UPDATE, 'S') <> COALESCE(OLD.AUDIT_UPDATE, 'S')) OR ");
		sql.append(" (COALESCE(NEW.AUDIT_DELETE, 'S') <> COALESCE(OLD.AUDIT_DELETE, 'S')))) THEN ");
		sql.append(" PODE_GERAR = 1; ");
		sql.append(" END IF; ");
		sql.append(" IF(PODE_GERAR = 1) THEN ");
		sql.append(" SELECT CASE WHEN COUNT(*) > 0 THEN 'S' ELSE 'N' END AS DROP_TRIGGER INTO DROP_TRIGGER ");
		sql.append(" FROM information_schema.triggers tgg  ");
		sql.append(" WHERE SUBSTRING(trim(lower(tgg.trigger_name)) from 1 for 6) = 'audit$' and ");
		sql.append(" trim(lower(tgg.event_object_table)) = lower(NEW.NOME) and trim(lower(tgg.event_object_schema)) = lower(NEW.NOME_SCHEMA); ");
		sql.append(" SELECT COUNT(*) INTO QTDE_PK FROM information_schema.table_constraints tc ");
		sql.append(" WHERE lower(tc.table_name) = lower(NEW.NOME) and lower(tc.table_schema) = lower(NEW.NOME_SCHEMA) ");
		sql.append(" AND upper(tc.constraint_type) = 'PRIMARY KEY'; ");
		sql.append(" IF(QTDE_PK > 0) THEN ");
		sql.append(" PERFORM auditoria.fc_audit$gera_trigger(NEW.identificador, NEW.nome, DROP_TRIGGER, NEW.audit_insert, NEW.audit_update, NEW.audit_delete, NEW.NOME_SCHEMA); ");
		sql.append(" END IF; ");
		sql.append(" END IF; ");
		sql.append(" END IF; ");
		sql.append(" RETURN NEW; ");
		sql.append(" END; ");
		sql.append(" $body$ ");
		sql.append(" LANGUAGE 'plpgsql' ");
		sql.append(" VOLATILE CALLED ON NULL INPUT SECURITY INVOKER COST 100; ");
		sql.append(" ");
		sql.append(" CREATE TRIGGER audit$_auditoria_tabela_au ");
		sql.append(" AFTER INSERT OR UPDATE  ");
		sql.append(" ON auditoria.na_auditoria_tabela FOR EACH ROW  ");
		sql.append(" EXECUTE PROCEDURE auditoria.fc_audit$_auditoria_tabela_au(); ");

		em.createNativeQuery(sql.toString()).executeUpdate();
	}

	public void criaDisconect(EntityManager em, IToolsDB toolsDb) throws Exception {

		if (toolsDb.existProcedure(em, "fc_audit$disconnect_base"))
			return;

		StringBuilder sql = new StringBuilder();

		sql.append(" create function auditoria.fc_audit$disconnect_base() ");
		sql.append(" returns void as $body$ ");
		sql.append(" begin ");
		sql.append(" INSERT INTO AUDITORIA.NA_AUDITORIA_TABELA (ID, IDENTIFICADOR, NOME_SCHEMA ,NOME, NOME_AMIGAVEL, AUDIT_INSERT, AUDIT_UPDATE, AUDIT_DELETE) ");
		sql.append(" select  nextval('auditoria.seq_na_auditoria_tabela') as id, trim(lower(md5(table_schema || table_name))) as identificador, ");
		sql.append(" trim(table_schema) as schema, trim(table_name) as nome , trim(table_name) as nome_amigavel ,'S' as AUDIT_INSERT, 'S' as AUDIT_UPDATE, 'S' as AUDIT_DELETE  ");
		sql.append(" from information_schema.tables ");
		sql.append(" where table_schema not in ('pg_catalog','information_schema', 'auditoria')  ");
		sql.append(" and  trim(upper(table_type)) <> 'VIEW' and table_name is not null  ");
		sql.append(" and (select count(*) from auditoria.na_auditoria_tabela ast  ");
		sql.append(" where ast.nome = table_name and ast.nome_schema = table_schema) = 0 ");
		sql.append(" order by table_schema , table_name; ");
		sql.append(" ");
		sql.append(" delete from AUDITORIA.NA_AUDITORIA_TABELA  ");
		sql.append(" where (select count(*) from information_schema.tables tb ");
		sql.append(" where  trim(lower(tb.table_name)) =  lower(auditoria.na_auditoria_tabela.nome) ");
		sql.append(" and trim(lower(tb.table_schema)) =  lower(auditoria.na_auditoria_tabela.nome_schema) ");
		sql.append(" and tb.table_name is not null) = 0; ");
		sql.append(" PERFORM auditoria.FC_AUDIT$CRIA_TRIGGER(); ");
		sql.append(" end; $body$ language 'plpgsql'; ");

		em.createNativeQuery(sql.toString()).executeUpdate();
	}

	@Override
	public void addFieldsControl(EntityManager em, String schema, Map<String, EnumTipoColumnBD> fieldsControl, IToolsDB toolsDB) throws Exception {

	}

	@Override
	public void iniciaCriacaoTriggersAuditoria(EntityManager em) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT count(*) FROM auditoria.fc_audit$disconnect_base(); ");
		em.createNativeQuery(sql.toString()).getSingleResult();

	}

	@Override
	public List<NaAuditoriaTabela> getNaAuditoriaTabela(EntityManager em, String schema) throws Exception {

		StringBuilder jpql = new StringBuilder();

		jpql.append(" select nat from NaAuditoriaTabela nat ");
		jpql.append(" order by nat.nome asc ");

		return em.createQuery(jpql.toString(), NaAuditoriaTabela.class).getResultList();
	}

	@Override
	public void geraTriggers(EntityManager em, IToolsDB toolsDb, String identificador, String nomeTabela, String dropTrigger, EnumSimNao auditInsert, EnumSimNao auditUpdate,
			EnumSimNao auditDelete, String nomeSchema) throws Exception {
		Integer quantidadeCampos = 0;
		boolean desativaTrigger = false;
		String nomeCampoChaveAudit = "";
		String sqlWhereUpdateAudit = " where ";
		StringBuilder sqlCamposInsert;
		StringBuilder sqlCamposUpdate;
		StringBuilder sqlCamposDelete;

		if (auditInsert == EnumSimNao.NAO && auditUpdate == EnumSimNao.NAO && auditDelete == EnumSimNao.NAO)
			desativaTrigger = true;

		/* CRIAR CAMPO DE AUDITORIA */

		toolsDb.addCollumn(em, nomeSchema.toLowerCase(), nomeTabela.toLowerCase(), "id_na_usuario_auditoria", EnumTipoColumnBD.INTEGER, null);
		toolsDb.addCollumn(em, nomeSchema.toLowerCase(), nomeTabela.toLowerCase(), "id_na_sistema_auditoria", EnumTipoColumnBD.INTEGER, null);

		StringBuilder sqlPrincipal = new StringBuilder();

		if (dropTrigger.equals("S"))
			dropTriggersAuditria(em, toolsDb, nomeSchema, nomeTabela);

		if (desativaTrigger)
			desativaTriggersAuditoria(em, toolsDb, nomeSchema, identificador, nomeTabela);

		if (auditInsert == EnumSimNao.SIM && auditUpdate == EnumSimNao.SIM && auditDelete == EnumSimNao.SIM) {

			List<Object[]> primaryKeyPosition = toolsDb.getFieldAndPositionPrimaryKey(em, nomeSchema, nomeTabela);

			for (Object[] keys : primaryKeyPosition) {

				if ((Integer) keys[1] > 1) {
					nomeCampoChaveAudit += " || '; ' || ";
					sqlWhereUpdateAudit += " and ";
				}

				nomeCampoChaveAudit += " XXX." + keys[0].toString();
				sqlWhereUpdateAudit += " " + keys[0].toString() + " = new." + keys[0].toString();
			}
			sqlWhereUpdateAudit += ";";

			if (!nomeCampoChaveAudit.equals("")) {
				sqlPrincipal.append(" create function " + nomeSchema.toLowerCase() + ".audit$" + identificador.toLowerCase());
				sqlPrincipal.append(" ()RETURNS trigger AS $body$ DECLARE ");
				sqlPrincipal.append(" id_operacao integer; ");
				sqlPrincipal.append(" id_auditoria integer; ");
				sqlPrincipal.append(" chave_auditoria varchar(150); ");
				sqlPrincipal.append(" contador integer; ");
				sqlPrincipal.append(" id_na_usuario_audit integer; ");
				sqlPrincipal.append(" id_na_sistema_audit integer; ");
				sqlPrincipal.append(" login_usuario_logado varchar(30); ");
				sqlPrincipal.append(" begin ");
				sqlPrincipal.append(" id_na_usuario_audit = null; ");
				sqlPrincipal.append(" id_na_sistema_audit = null; ");
				sqlPrincipal.append(" login_usuario_logado = ''; ");
				sqlPrincipal.append(" contador = 0; ");

				if (auditInsert == EnumSimNao.SIM) {
					sqlPrincipal.append(" if (TG_OP = 'INSERT') then ");
					sqlPrincipal.append(" id_operacao = 1;");
					sqlPrincipal.append(" chave_auditoria = " + nomeCampoChaveAudit.replace("XXX.", "new."));
					sqlPrincipal.append("; ");
					sqlPrincipal.append(" id_na_usuario_audit = new.id_na_usuario_auditoria; ");
					sqlPrincipal.append(" id_na_sistema_audit = new.id_na_sistema_auditoria; ");
				}
				if (auditUpdate == EnumSimNao.SIM) {
					if (auditInsert == EnumSimNao.SIM)
						sqlPrincipal.append(" elseif (TG_OP = 'UPDATE') then ");
					else
						sqlPrincipal.append(" if (tg_op = 'UPDATE') then ");

					sqlPrincipal.append(" id_operacao = 2; ");
					sqlPrincipal.append(" chave_auditoria = " + nomeCampoChaveAudit.replace("XXX.", "new.") + "; ");
					sqlPrincipal.append(" id_na_usuario_audit = new.id_na_usuario_auditoria; ");
					sqlPrincipal.append(" id_na_sistema_audit = new.id_na_sistema_auditoria; ");
				}
				if (auditDelete == EnumSimNao.SIM) {
					if (auditInsert == EnumSimNao.SIM || auditUpdate == EnumSimNao.SIM)
						sqlPrincipal.append(" elseif (TG_OP = 'DELETE' or tg_op = 'TRUNCATE') then ");
					else
						sqlPrincipal.append(" if (TG_OP = 'DELETE' or TG_OP = 'TRUNCATE') then ");

					sqlPrincipal.append(" id_operacao = 3; ");
					sqlPrincipal.append(" chave_auditoria = " + nomeCampoChaveAudit.replace("XXX.", "old.") + "; ");
					sqlPrincipal.append(" id_na_usuario_audit = old.id_na_usuario_auditoria; ");
					sqlPrincipal.append(" id_na_sistema_audit = old.id_na_sistema_auditoria; ");
				}
				sqlPrincipal.append(" end if;");
				sqlPrincipal.append(" if(id_na_usuario_audit is not null) then ");
				sqlPrincipal.append(" select u.login into login_usuario_logado from seguranca.na_usuario u ");
				sqlPrincipal.append(" where u.id = id_na_usuario_audit; ");
				sqlPrincipal.append(" end if; ");
				sqlPrincipal.append(" if (login_usuario_logado = '' or login_usuario_logado is null) then ");
				sqlPrincipal.append(" select current_user into login_usuario_logado from user ; ");
				sqlPrincipal.append(" end if; ");

				sqlPrincipal.append(" select nextval('auditoria.seq_auditoria_sistema') into id_auditoria ; ");
				sqlPrincipal.append(" insert into auditoria.auditoria_sistema (cod_auditoria, tabela, chave_registro, data_auditoria, hora_auditoria, usuario, tipo_operacao) ");
				sqlPrincipal.append(" values (id_auditoria, '" + nomeSchema.toLowerCase() + "." + nomeTabela.toLowerCase()
						+ "', chave_auditoria, current_date, current_date, login_usuario_logado, id_operacao); ");
				sqlPrincipal.append(" ");

				if (auditInsert == EnumSimNao.SIM && auditUpdate == EnumSimNao.SIM && auditDelete == EnumSimNao.SIM) {

					sqlCamposInsert = new StringBuilder();
					sqlCamposUpdate = new StringBuilder();
					sqlCamposDelete = new StringBuilder();

					sqlCamposInsert.append(" if (TG_OP = 'INSERT') then ");
					sqlCamposUpdate.append(" elseif (TG_OP = 'UPDATE') then ");
					sqlCamposDelete.append(" elseif (TG_OP = 'DELETE' or TG_OP = 'TRUNCATE') then  ");

					List<Object[]> listaCampos = getCamposTabela(em, toolsDb, nomeSchema, nomeTabela);

					for (Object[] campo : listaCampos) {

						// campo[0] Posição Campo
						// campo[1] Nome Coluna
						// campo[2] Tipo Campo

						if (!String.valueOf(campo[2]).equals("bytea")) {
							quantidadeCampos++;

							if (auditInsert == EnumSimNao.SIM) {

								sqlCamposInsert.append(" select auditoria.fc_audit$insere_item( ID_AUDITORIA , ID_OPERACAO , ");
								sqlCamposInsert.append(String.valueOf(campo[0]) + " , '" + String.valueOf(campo[1]) + "' , ");
								sqlCamposInsert.append(" null, CAST(NEW." + String.valueOf(campo[1]) + " AS VARCHAR), CONTADOR) into contador; ");
							}
							if (auditUpdate == EnumSimNao.SIM) {

								sqlCamposUpdate.append(" select auditoria.fc_audit$insere_item( ID_AUDITORIA , ID_OPERACAO , ");
								sqlCamposUpdate.append(String.valueOf(campo[0]) + " , '" + String.valueOf(campo[1]) + "' , ");
								sqlCamposUpdate.append(" CAST(OLD." + String.valueOf(campo[1]) + " AS VARCHAR),CAST(NEW." + String.valueOf(campo[1])
										+ " AS VARCHAR), CONTADOR) into contador; ");
							}
							if (auditDelete == EnumSimNao.SIM) {

								sqlCamposDelete.append(" select auditoria.fc_audit$insere_item( ID_AUDITORIA , ID_OPERACAO , ");
								sqlCamposDelete.append(String.valueOf(campo[0]) + " , '" + String.valueOf(campo[1]) + "' , ");
								sqlCamposDelete.append(" CAST(OLD." + String.valueOf(campo[1]) + " as varchar), null, CONTADOR) into contador; ");
							}
						}
					}

					sqlPrincipal.append(sqlCamposInsert);
					sqlPrincipal.append(sqlCamposUpdate);
					sqlPrincipal.append(sqlCamposDelete);
					sqlPrincipal.append(" end if;");
				}
				sqlPrincipal.append(" if ( contador = 0 ) then ");
				sqlPrincipal.append(" delete from auditoria.auditoria_sistema where cod_auditoria = id_auditoria; ");
				sqlPrincipal.append(" end if; ");
				sqlPrincipal.append(" if (TG_OP = 'DELETE' or TG_OP = 'TRUNCATE') then return old; else ");

				sqlPrincipal.append(" if( new.id_na_usuario_auditoria is not null ) then ");
				sqlPrincipal.append(" update " + nomeSchema.toLowerCase() + "." + nomeTabela.toLowerCase() + " SET id_na_usuario_auditoria = NULL " + sqlWhereUpdateAudit);
				sqlPrincipal.append(" end if;");

				sqlPrincipal.append(" if( new.id_na_sistema_auditoria is not null ) then ");
				sqlPrincipal.append(" update " + nomeSchema.toLowerCase() + "." + nomeTabela.toLowerCase() + " SET id_na_sistema_auditoria = NULL " + sqlWhereUpdateAudit);
				sqlPrincipal.append(" end if;");
				sqlPrincipal.append(" return new; end if; ");
				sqlPrincipal.append(" end; $body$ language 'plpgsql' ");
				sqlPrincipal.append(" volatile called on null input security invoker; ");
				Query query;

				query = em.createNativeQuery(sqlPrincipal.toString());
				query.executeUpdate();

				sqlPrincipal = new StringBuilder();

				sqlPrincipal.append(" create trigger audit$" + identificador.toLowerCase() + " after ");

				if (auditInsert == EnumSimNao.SIM)
					sqlPrincipal.append(" insert ");

				if (auditInsert == EnumSimNao.SIM && auditUpdate == EnumSimNao.SIM)
					sqlPrincipal.append(" or ");

				if (auditUpdate == EnumSimNao.SIM)
					sqlPrincipal.append(" update ");

				if ((auditInsert == EnumSimNao.SIM || auditUpdate == EnumSimNao.SIM) && auditDelete == EnumSimNao.SIM)
					sqlPrincipal.append(" or ");

				if (auditDelete == EnumSimNao.SIM)
					sqlPrincipal.append(" delete ");

				sqlPrincipal.append(" on " + nomeSchema.toLowerCase() + "." + nomeTabela.toLowerCase() + " for each row execute procedure ");
				sqlPrincipal.append(nomeSchema.toLowerCase() + ".audit$" + identificador.toLowerCase() + "();");

				query = em.createNativeQuery(sqlPrincipal.toString());
				query.executeUpdate();
			}
		}
	}

	@Override
	public void dropTriggersAuditria(EntityManager em, IToolsDB toolsDb, String nomeSchema, String nomeTabela) throws Exception {

		if (nomeSchema.equalsIgnoreCase("") && !nomeTabela.equalsIgnoreCase(""))
			return;
		if (!nomeSchema.equalsIgnoreCase("") && nomeTabela.equalsIgnoreCase(""))
			return;

		StringBuilder sql = new StringBuilder();
		StringBuilder drop = new StringBuilder();

		sql.append(" select distinct lower(tgg.trigger_name) as name_triger , lower(tgg.trigger_schema) as schema, lower(tgg.event_object_table) as tabela  ");
		sql.append(" from information_schema.triggers tgg ");
		sql.append(" WHERE (SUBSTRING(lower(tgg.trigger_name) FROM 1 FOR 6) = 'audit$')  ");

		if (!nomeSchema.equalsIgnoreCase("") && !nomeTabela.equalsIgnoreCase("")) {
			sql.append(" AND lower(tgg.event_object_table) = :TABELA_NOME ");
			sql.append(" and lower(tgg.event_object_schema) = :SCHEMA_NOME ");
		}

		Query query = em.createNativeQuery(sql.toString());

		if (!nomeSchema.equalsIgnoreCase("") && !nomeTabela.equalsIgnoreCase("")) {
			query.setParameter("TABELA_NOME", nomeTabela);
			query.setParameter("SCHEMA_NOME", nomeSchema);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> listaTriggers = query.getResultList();

		for (Object[] trigger : listaTriggers) {

			String nomeTrigger = String.valueOf(trigger[0]);
			nomeSchema = String.valueOf(trigger[1]);
			nomeTabela = String.valueOf(trigger[2]);

			drop.append(" drop trigger " + String.valueOf(nomeTrigger) + " on " + nomeSchema.toLowerCase() + "." + nomeTabela.toLowerCase() + "; ");
			// drop.append("  drop function " + nomeSchema.toLowerCase() + "." +
			// String.valueOf(nomeTrigger) + "(); ");
			Query query2 = em.createNativeQuery(drop.toString());
			query2.executeUpdate();
			em.flush();

			drop = new StringBuilder();
			drop.append(" delete from pg_proc where proname like '" + nomeTrigger.toLowerCase() + "';");

			Query query1 = em.createNativeQuery(drop.toString());
			query1.executeUpdate();
			// em.flush();
		}

	}

	@Override
	public void desativaTriggersAuditoria(EntityManager em, IToolsDB toolsDb, String nomeSchema, String identificador, String nomeTabela) throws Exception {

		Query query;

		StringBuilder trigger = new StringBuilder();
		StringBuilder function = new StringBuilder();

		function.append(" create function " + nomeSchema.toLowerCase() + ".audit$" + identificador.toLowerCase());
		function.append(" () returns trigger as $body$ declare begin end; $body$ language 'plpgsql' ");
		function.append(" volatile called on null input security invoker; ");

		query = em.createNativeQuery(function.toString());
		query.executeUpdate();

		trigger.append(" create trigger audit$" + identificador.toLowerCase() + " after insert or update or delete ");
		trigger.append(" on " + nomeSchema.toLowerCase() + "." + nomeTabela.toLowerCase());
		trigger.append(" for each statement execute procedure " + nomeSchema.toLowerCase() + ".audit$" + identificador.toLowerCase() + "();");

		query = em.createNativeQuery(trigger.toString());
		query.executeUpdate();

		trigger = new StringBuilder();
		trigger.append(" alter table " + nomeSchema.toLowerCase() + "." + nomeTabela.toLowerCase());
		trigger.append(" " + nomeSchema.toLowerCase() + ".audit$" + identificador.toLowerCase() + "();");

		query = em.createNativeQuery(trigger.toString());
		query.executeUpdate();

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getCamposTabela(EntityManager em, IToolsDB toolsDb, String nomeSchema, String nomeTabela) {

		StringBuilder sql = new StringBuilder();

		sql.append(" select cl.ordinal_position as posicao , trim(cl.column_name) as nome_coluna, ");
		sql.append(" TRIM(cl.data_type) as tipo_campo from information_schema.columns cl ");
		sql.append(" where lower(cl.table_name) = lower(:TABELA_NOME) and lower(cl.table_schema) = lower(:SCHEMA_NOME) ");
		sql.append(" and trim(lower(cl.column_name)) <> 'id_na_usuario_auditoria' and trim(lower(cl.column_name)) <> 'id_na_sistema_auditoria' ");
		sql.append(" and trim(lower(cl.data_type)) <> 'bytea' ");
		sql.append(" and (select count(*) from auditoria.na_auditoria_campo ac ");
		sql.append(" where trim(lower(ac.tabela)) = lower( :TABELA_NOME) ");
		sql.append(" and trim(lower(ac.campo)) = trim(lower(cl.column_name)) and lower(ac.nome_schema) = lower(:SCHEMA_NOME) ) = 0 ");
		sql.append(" order by cl.ordinal_position ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("TABELA_NOME", nomeTabela);
		query.setParameter("SCHEMA_NOME", nomeSchema);

		try {
			return query.getResultList();
		} catch (Exception e) {
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public void defineTabelasAuditaveis(EntityManager em) {

		StringBuilder sql = new StringBuilder();

		sql.append(" INSERT INTO AUDITORIA.NA_AUDITORIA_TABELA (ID, IDENTIFICADOR, NOME_SCHEMA ,NOME, NOME_AMIGAVEL, AUDIT_INSERT, AUDIT_UPDATE, AUDIT_DELETE) ");
		sql.append(" select  nextval('auditoria.seq_na_auditoria_tabela') as id, trim(lower(md5(table_schema || table_name))) as identificador, ");
		sql.append(" trim(table_schema) as schema, trim(table_name) as nome , trim(table_name) as nome_amigavel ,'S' as AUDIT_INSERT, 'S' as AUDIT_UPDATE, 'S' as AUDIT_DELETE ");
		sql.append(" from information_schema.tables ");
		sql.append(" where table_schema not in ('pg_catalog','information_schema', 'auditoria') ");
		sql.append(" and  trim(upper(table_type)) <> 'VIEW' and table_name is not null ");
		sql.append(" and not exists (select ast.id from auditoria.na_auditoria_tabela ast ");
		sql.append(" where ast.nome_schema = table_schema and ast.nome = table_name ) ");
		sql.append(" order by table_schema , table_name;");

		Query query = em.createNativeQuery(sql.toString());
		query.executeUpdate();

	}

	public static Connection getConnection(EntityManager em) throws SQLException {

		HibernateEntityManagerFactory hemf = (HibernateEntityManagerFactory) em.getEntityManagerFactory();
		SessionFactoryImpl sessionFactory = (SessionFactoryImpl) hemf.getSessionFactory();
		return sessionFactory.getSessionFactoryOptions().getServiceRegistry().getService(ConnectionProvider.class).getConnection();
	}

	@Override
	public void getGeraTrigger(EntityManager em, NaAuditoriaTabela auditoriaTabela) throws Exception {

		/*
		 * Aqui a Procedure Gera Trigger é executada sem erros, porém as
		 * triggers não são executadas em banco de dados!
		 */

		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT count(*) FROM  auditoria.fc_audit$gera_trigger( cast( :TABELA_ID as varchar(50) ), cast( :TABELA_NOME as varchar(31) ),");
		sql.append(" cast( :DROP_TRIGGER as varchar(1)),cast(:AUDIT_INSERT as varchar(1) ), cast( :AUDIT_UPDATE as varchar(1)), cast( :AUDIT_DELETE as varchar(1) ) , cast(:SCHEMA_NOME as varchar(31) ) );");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("TABELA_ID", auditoriaTabela.getIdentificador());
		query.setParameter("TABELA_NOME", auditoriaTabela.getNome());
		query.setParameter("DROP_TRIGGER", "S");
		query.setParameter("AUDIT_INSERT", auditoriaTabela.getAuditInsert().toChar());
		query.setParameter("AUDIT_UPDATE", auditoriaTabela.getAuditUpdate().toChar());
		query.setParameter("AUDIT_DELETE", auditoriaTabela.getAuditDelete().toChar());
		query.setParameter("SCHEMA_NOME", auditoriaTabela.getSchema());

		query.getSingleResult();

	}

	@Override
	public void dropEstruturaAuditoria(EntityManager em) throws Exception {

		dropTriggersAuditria(em, null, "", "");

		try {
			em.createNativeQuery(" DROP FUNCTION if exists auditoria.fc_audit$_auditoria_tabela_au(); ").executeUpdate();
			em.createNativeQuery(" DROP FUNCTION if exists auditoria.fc_audit$disconnect_base(); ").executeUpdate();
			em.createNativeQuery(" DROP FUNCTION if exists auditoria.fc_audit$cria_campos_auditoria(schema_nome varchar, tabela_nome varchar); ").executeUpdate();
			em.createNativeQuery(" DROP FUNCTION if exists auditoria.fc_audit$cria_trigger(); ").executeUpdate();
			em.createNativeQuery(
					" DROP FUNCTION if exists auditoria.fc_audit$gera_trigger(tabela_id varchar, tabela_nome varchar, drop_trigger varchar, audit_insert varchar, audit_update varchar, audit_delete varchar, schema_nome varchar); ")
					.executeUpdate();
			em.createNativeQuery(
					" DROP FUNCTION if exists auditoria.fc_audit$insere_item(id_auditoria integer, id_operacao integer, id_sequencial integer, nome_campo varchar, valor_old varchar, valor_new varchar, contador integer); ")
					.executeUpdate();

			em.createNativeQuery(" drop trigger if exists audit$_auditoria_tabela_au on auditoria.na_auditoria_tabela; ").executeUpdate();

			em.createNativeQuery(" DROP FUNCTION if exists auditoria.fc_audit$drop_triggers_auditoria(); ").executeUpdate();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
