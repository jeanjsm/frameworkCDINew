package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.atualizabase.ToolsAtualizaBase;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoConstraint;

public class ToolsDBPostgre implements IToolsDB, Serializable {
	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(ToolsDBPostgre.class);
	private  enum TypeLenght { TABLE, COLUMN, PRIMARYKEY, FOREINGKEY, CHECK, UNIQUE, SEQUENCE, TRIGGER }

	@Override
	public boolean existTable(EntityManager em, String schema, String table) {

		log.debug("Existe Tabela (Schema: " + schema + " Nome: " + table + ")");

		StringBuilder sql = new StringBuilder();

		sql.append("select * from information_schema.tables where ");
		sql.append("upper(table_name) = upper(:table) ");
		sql.append("and upper(table_schema) = upper(:schema) ");
		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("table", table);
		query.setParameter("schema", schema);

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	@Override
	public boolean dropTable(EntityManager em, String schema, String table) throws Exception {

		if (existTable(em, schema, table)) {

			dropTriggerAuditoria(em, schema, table);
			dropAllForeingKey(em, schema, table);
			dropAllUniqueKeys(em, schema, table);
			dropPrimaryKey(em, schema, table);

			StringBuilder sql = new StringBuilder();
			sql.append("drop table " + schema + "." + table + ";");
			
			ToolsAtualizaBase.executaCommandoSql(em, sql);
			
		}
		return true;

	}

	@Override
	public boolean existCollumn(EntityManager em, String schema, String table, String collunName) {

		log.debug("Verificando se existe collumn (schema:" + schema + " table:" + table + " collumn:" + collunName + ")");

		StringBuilder sql = new StringBuilder();

		sql.append("select * from information_schema.columns where ");
		sql.append("upper(table_name) = upper(:table) ");
		sql.append("and upper(table_schema) = upper(:schema) ");
		sql.append("and upper(column_name) = upper(:collunName)");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("table", table);
		query.setParameter("schema", schema);
		query.setParameter("collunName", collunName);

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	@Override
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght) throws Exception {

		return addCollumn(em, schema, table, collunName, tipo, lenght, false, "");
	}

	@Override
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght, boolean notNull, String defaultValue) throws Exception {
		addCollumn(em, schema, table, collunName, tipo, notNull, defaultValue, lenght, null);
		return true;
	};

	@Override
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, boolean notNull, String defaultValue, Integer lenght,
			Integer precision) throws Exception {

		if (existCollumn(em, schema, table, collunName))
			return false;

		validaLength(TypeLenght.COLUMN, collunName);

		log.debug("adicionando column (schema:" + schema + " table:" + table + " column:" + collunName + " tipo:" + tipo + " notNull:" + notNull + ")");

		StringBuilder sql = new StringBuilder();

		sql.append("alter table ");
		sql.append(schema);
		sql.append(".");
		sql.append(table);
		sql.append(" add column ");
		sql.append(collunName);

		sql.append(getTypeBD(tipo, lenght, precision));

		if (notNull)
			sql.append(" not null");

		if (defaultValue != null && !defaultValue.trim().equals("")) {
			if (defaultValue.trim().equals("now")) {
				defaultValue += "()";
			}
			sql.append(" default " + "'" + defaultValue + "'");
		}

		sql.append(" ;");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	
	private String getTypeBD(EnumTipoColumnBD tipo, Integer lenght, Integer precision) {

		String retorno = "";

		if (tipo == EnumTipoColumnBD.VARCHAR)
			retorno = " varchar(" + lenght + ")";
		else if (tipo == EnumTipoColumnBD.INTEGER)
			retorno = " integer";
		else if (tipo == EnumTipoColumnBD.BLOB_BINARIO)
			retorno = " bytea";
		else if (tipo == EnumTipoColumnBD.BLOB_TEXTO)
			retorno = " varchar";
		else if (tipo == EnumTipoColumnBD.NUMERIC)
			retorno = " numeric(" + lenght + "," + precision + ")";
		else if (tipo == EnumTipoColumnBD.TIMESTAMP)
			retorno = " timestamp";
		else if (tipo == EnumTipoColumnBD.DATE)
			retorno = " date";
		else if (tipo == EnumTipoColumnBD.BIG_INTEGER)
			retorno = " bigint";
		else if (tipo == EnumTipoColumnBD.CHAR)
			retorno = " char(" + lenght + ")";

		return retorno;
	}

	@Override
	public boolean dropCollumn(EntityManager em, String schema, String table, String collunName) throws Exception {

		if (!existCollumn(em, schema, table, collunName))
			return true;

		dropTriggerAuditoria(em, schema, table);

		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(schema);
		sql.append(".");
		sql.append(table);
		sql.append(" drop ");
		sql.append(collunName);
		sql.append(" ;");

		ToolsAtualizaBase.executaCommandoSql(em, sql);
		
		return true;

	}

	@Override
	public boolean setCollumnNotNull(EntityManager em, String schema, String table, String collunName) throws Exception {

		if (!existCollumn(em, schema, table, collunName))
			return false;

		StringBuilder sql = new StringBuilder();
		sql.append(" ALTER TABLE ");
		sql.append(schema);
		sql.append(".");
		sql.append(table.toUpperCase());
		sql.append(" ALTER COLUMN ");
		sql.append(collunName.toUpperCase());
		sql.append(" SET NOT NULL; ");

		ToolsAtualizaBase.executaCommandoSql(em, sql);
		
		return true;
	}

	@Override
	public boolean setCollumnDefaultValue(EntityManager em, String schema, String table, String collunName, String defaultValue) throws Exception {

		if (!existCollumn(em, schema, table, collunName))
			return false;

		StringBuilder sql = new StringBuilder();
		sql.append(" ALTER TABLE " + schema + "." + table.toUpperCase() + " ALTER COLUMN " + collunName.toUpperCase());
		sql.append(" SET DEFAULT '" + defaultValue + "'");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean setCollumnDefaultValue(EntityManager em, String schema, String table, String collunName, Integer defaultValue) throws Exception {
		if (!existCollumn(em, schema, table, collunName))
			return false;
		
		StringBuilder sql = new StringBuilder();
		sql.append(" ALTER TABLE " + schema + "." + table.toUpperCase() + " ALTER COLUMN " + collunName.toUpperCase());
		sql.append(" SET DEFAULT " + defaultValue + " ");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}
	
	@Override
	public boolean setCollumnDropDefaultValue(EntityManager em, String schema, String table, String collunName) throws Exception {
		if (!existCollumn(em, schema, table, collunName))
			return false;

		StringBuilder sql = new StringBuilder();
		sql.append(" ALTER TABLE " + schema + "." + table.toUpperCase() + " ALTER COLUMN " + collunName.toUpperCase());
		sql.append(" DROP DEFAULT");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;		
	}
	
	@Override
	public void setCollumnAutoincrement(EntityManager em, String schema, String table, String collunName, String nameSequence) throws Exception {

		addSequence(em, schema, nameSequence);
		
		if (!existCollumn(em, schema, table, collunName))
			throw new Exception("field "+ collunName+ " não existe ");
		
		StringBuilder sql = new StringBuilder();		
		sql.append("ALTER TABLE "+schema+"."+table+" ALTER COLUMN "+collunName+" SET DEFAULT nextval('"+schema+"."+nameSequence+"');");		

		ToolsAtualizaBase.executaCommandoSql(em, sql);
		
	}

	@Override
	public boolean alterTypeCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght, Integer precision) throws Exception {

		dropTriggerAuditoria(em, schema, table);

		StringBuilder sql = new StringBuilder();

		sql.append(" ALTER TABLE ");
		sql.append(schema);
		sql.append(".");
		sql.append(table.toUpperCase());
		sql.append(" ALTER COLUMN ");
		sql.append(collunName.toUpperCase());
		sql.append(" TYPE ");
		sql.append(getTypeBD(tipo, lenght, precision));
		sql.append(" ;");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;

	}

	@Override
	public boolean addPrimaryKey(EntityManager em, String schema, String table, String namePK, String fields) throws Exception {

		validaLength(TypeLenght.PRIMARYKEY, namePK);
		
		if (existPrimaryKey(em, schema, table))
			return false;		

		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(schema + ".");
		sql.append(table);
		sql.append(" add constraint ");
		sql.append(namePK);
		sql.append(" primary key ");
		sql.append("(" + fields + ")");
		sql.append(" ;");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;

	}

	@Override
	public void dropPrimaryKey(EntityManager em, String schema, String table) throws Exception {

		if (existPrimaryKey(em, schema, table)) {
			String namePrimaryKey = getNamePrimaryKey(em, schema, table);
			dropConstraint(em, schema, table, namePrimaryKey);		
		}
		
	}

	@Override
	public boolean existPrimaryKey(EntityManager em, String schema, String table) {

		log.debug("existe PK (schema: " + schema + " Tabela: " + table + ")");

		return existsConstraint(em, schema, table, EnumTipoConstraint.PRIMARY_KEY, "");
	}

	@Override
	public String getNamePrimaryKey(EntityManager em, String schema, String table) {

		StringBuilder sql = new StringBuilder();
		sql.append("select constraint_name from information_schema.table_constraints ");
		sql.append("where upper(table_name) = upper(:table) and upper(table_schema) = upper(:schema) ");
		sql.append("and upper(constraint_type) = upper(:constraintType) ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("table", table);
		query.setParameter("schema", schema);
		query.setParameter("constraintType", EnumTipoConstraint.PRIMARY_KEY.getDescricao());

		try {
			return (String) query.getSingleResult();
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	@Override
	public void addForeingKey(EntityManager em, String schema, String table, String nameFK, String schemaTableDestino, String tabelaDestino, String collumnDestino,
			String collumnOrigem) throws Exception {
		addForeingKey(em, schema, table, nameFK, schemaTableDestino, tabelaDestino, collumnDestino, collumnOrigem, "");
	}

	/**
	 * A propriedade rule deve ser utilizado para update ou delete cascade
	 */
	@Override
	public void addForeingKey(EntityManager em, String schema, String table, String nameFK, String schemaTableDestino, String tabelaDestino, String collumnDestino,
			String collumnOrigem, String rule) throws Exception {
		if (!existForeingKey(em, schema, table, "", nameFK, "")) {

			log.debug("Adicionando FK schema:" + schema + " tabela: " + table + " campo: " + collumnOrigem + " nome:    -   destino schema: " + schemaTableDestino + " tabela: "
					+ tabelaDestino + " campo: " + collumnDestino + "");

			StringBuilder sql = new StringBuilder();

			sql.append("alter table ");
			sql.append(schema + ".");
			sql.append(table);
			sql.append(" add constraint ");
			sql.append(nameFK);
			sql.append(" foreign key (");
			sql.append(collumnOrigem);
			sql.append(") references ");
			sql.append(schemaTableDestino + ".");
			sql.append(tabelaDestino);
			sql.append(" (");
			sql.append(collumnDestino);
			sql.append(")");
			sql.append(" " + rule + " ");
			sql.append(" DEFERRABLE INITIALLY DEFERRED;");

			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}

	}

	@Override
	public boolean dropForeingKey(EntityManager em, String schema, String table, String nameTableConstraint, String nameFK, String... nameFields) throws Exception {
		List<String> constraints = getConstraints(em, schema, table, nameFK, EnumTipoConstraint.FOREIGN_KEY);
		for (String constraintName : constraints) {
			dropConstraint(em, schema, table, constraintName);
		}
		return true;
	}

	@Override
	public boolean existForeingKey(EntityManager em, String schema, String table, String nameTableConstraint, String nameFK, String... nameFields) throws Exception {
		List<String> constraints = getConstraints(em, schema, table, nameFK, EnumTipoConstraint.FOREIGN_KEY);
		if (constraints == null || constraints.size() == 0)
			return false;
		return true;
	}

	@Override
	public void addSequence(EntityManager em, String schema, String nameSequence) throws Exception {
		
		validaLength(TypeLenght.SEQUENCE, nameSequence);
		
		if (!existSequence(em, schema, nameSequence)) {
						
			StringBuilder sql = new StringBuilder();
			sql.append("create sequence ");
			sql.append(schema);
			sql.append(".");
			sql.append(nameSequence);
						
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
	}

	@Override
	public Integer maxId(EntityManager em, String schema, String nameTable, String fieldKey) {

		StringBuilder sql = new StringBuilder();
		sql.append("select max(" + fieldKey + ") from ");
		sql.append(schema + ".");
		sql.append(nameTable);

		Query query = em.createNativeQuery(sql.toString());

		Integer resultado = (Integer) query.getSingleResult();

		if (resultado == null)
			return 0;
		return resultado;

	}

	@Override
	public void alterSequence(EntityManager em, String schema, String nameSequence, Integer value) throws Exception {

		validaLength(TypeLenght.SEQUENCE, nameSequence);
		
		if (existSequence(em, schema, nameSequence)){
			
			StringBuilder sql = new StringBuilder();
			sql.append("alter sequence " + schema + "." + nameSequence + " restart with " + value);
					
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}

	}

	
	@Override
	public boolean existSequence(EntityManager em, String schema, String nameSequence) {

		log.debug("existe sequence (schema:" + schema + " nome: " + nameSequence + ")");

		StringBuilder sql = new StringBuilder();

		sql.append("select count(*) from information_schema.sequences where ");
		sql.append("upper(sequence_name) = :nameSequence ");
		sql.append("and upper(sequence_schema) = :schema ");
		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("nameSequence", nameSequence.toUpperCase());
		query.setParameter("schema", schema.toUpperCase());

		BigInteger result = (BigInteger) query.getSingleResult();

		if (result.intValue() > 0)
			return true;
		return false;
	}

	@Override
	public void dropSequence(EntityManager em, String schema, String nameSequence) throws Exception {

		if (existSequence(em, schema, nameSequence)) {
			StringBuilder sql = new StringBuilder();
			sql.append("drop sequence  " + schema + "." + nameSequence);
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}

	}

	@Override
	public void addIndex(EntityManager em, String schema, String table, String indexName, String fields) throws Exception {
		addIndex(em, schema, table, indexName, fields, false);
	}

	@Override
	public void addIndex(EntityManager em, String schema, String table, String indexName, String fields, boolean unique) throws Exception {

		if (!existIndex(em, schema, table, indexName)) {

			log.debug("Adicionando Index (schema: " + schema + " tabela: " + table + " nome: " + indexName + " campos: (" + fields + ") )");

			StringBuilder sql = new StringBuilder();

			if (unique)
				sql.append("create unique index ");
			else
				sql.append("create index ");

			sql.append(indexName);
			sql.append(" on ");
			sql.append(schema + "." + table);
			sql.append("(" + fields + ")");

			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}

	}

	@Override
	public boolean existIndex(EntityManager em, String schema, String table, String indexName) {

		log.debug("Existe Index (schema: " + schema + " tabela: " + table + " nome: " + indexName + " )");

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) from pg_class t, pg_class i, pg_index ix, pg_attribute a ");
		sql.append("where ");
		sql.append("t.oid = ix.indrelid and i.oid = ix.indexrelid and a.attrelid = t.oid and a.attnum = ANY(ix.indkey) and t.relkind = 'r' ");
		sql.append("and i.relname ilike :indexName");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("indexName", indexName);

		BigInteger result = (BigInteger) query.getSingleResult();

		if (result.intValue() > 0)
			return true;
		return false;
	}

	@Override
	public void dropIndex(EntityManager em, String schema, String table, String indexName) throws Exception {

		if (existIndex(em, schema, table, indexName)) {
			StringBuilder sql = new StringBuilder();
			sql.append("drop index " + schema + "." + indexName);

			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
	}

	@Override
	public void addUniqueKey(EntityManager em, String schema, String table, String uniqName, String collunsName) throws Exception {

		if (!existUniqueKey(em, schema, table, uniqName)) {

			StringBuilder sql = new StringBuilder();
			sql.append("alter table ");
			sql.append(schema + ".");
			sql.append(table);
			sql.append(" add constraint ");
			sql.append(uniqName);
			sql.append(" unique ");
			sql.append(" (" + collunsName + ") ");
			sql.append(" DEFERRABLE INITIALLY DEFERRED ");

			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
	}

	@Override
	public boolean dropUniqueKey(EntityManager em, String schema, String table, String nameTableConstraint, String nameFK, String... nameFields) throws Exception {

		List<String> nameForeignKeys = getNameConstraints(em, schema, table, nameTableConstraint, EnumTipoConstraint.UNIQUE, nameFields);

		if (nameForeignKeys.size() == 0)
			return false;

		for (String nameForeignKey : nameForeignKeys) {
			dropConstraint(em, schema, table, nameForeignKey);
		}
		return true;
	}

	@Override
	public boolean existUniqueKey(EntityManager em, String schema, String table, String uniqName) {

		log.debug("Existe UniqueKey (schema: " + schema + " tabela: " + table + " nome: " + uniqName + ")");

		List<String> constraints = getConstraints(em, schema, table, "", EnumTipoConstraint.UNIQUE);

		if (constraints.size() == 0)
			return false;

		for (String nameUniqueKey : constraints) {
			if (nameUniqueKey.equalsIgnoreCase(uniqName))
				return true;
		}
		return false;

	}

	@Override
	public void addCheck(EntityManager em, String schema, String table, String chkName, String valores) throws Exception {

		if (!existCheck(em, schema, table, chkName)) {

			StringBuilder sql = new StringBuilder();
			sql.append("alter table ");
			sql.append(schema + ".");
			sql.append(table);
			sql.append(" add constraint ");
			sql.append(chkName);
			sql.append(" check ");
			sql.append(" (" + valores + ")");

			ToolsAtualizaBase.executaCommandoSql(em, sql);

		}

	}

	@Override
	public boolean existCheck(EntityManager em, String schema, String table, String chkName) {
		List<String> constraints = getConstraints(em, schema, table, chkName, EnumTipoConstraint.CHECK);
		if (constraints == null || constraints.size() == 0)
			return false;
		return true;
	}

	@Override
	public boolean dropCheck(EntityManager em, String schema, String table, String nameTableConstraint, String nameFK, String... nameFields) throws Exception {
		List<String> nameForeignKeys = getNameConstraints(em, "", table, nameFK, EnumTipoConstraint.CHECK, nameFields);

		if (nameForeignKeys.size() == 0)
			return false;

		for (String nameForeignKey : nameForeignKeys) {
			dropConstraint(em, schema, table, nameForeignKey);
		}
		return true;
	}

	@Override
	public void setVersaoBD(EntityManager em, Integer versao, boolean framework, Integer idSistema) throws Exception {

		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> params = new HashMap<>();

		if (framework)
			sql.append(" UPDATE PUBLIC.NA_SISTEMA SET VERSAO_BASE = :VERSAO WHERE ID = " + InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID);
		else
			sql.append(" UPDATE PUBLIC.NA_SISTEMA SET VERSAO_BASE = :VERSAO WHERE ID = :ID_SISTEMA ");

		params.put("VERSAO", versao);

		if (!framework) 
			params.put("ID_SISTEMA", idSistema);
		

		ToolsAtualizaBase.executaCommandoSql(em, sql, params);
	}

	@Override
	public Integer getVersaoBD(EntityManager em, boolean framework, Integer idSistema) {
		StringBuilder sql = new StringBuilder();

		if (framework)
			sql.append(" SELECT VERSAO_BASE from NA_SISTEMA WHERE ID = " + InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID);
		else
			sql.append(" SELECT VERSAO_BASE from NA_SISTEMA WHERE ID = :ID_SISTEMA ");

		Query query = em.createNativeQuery(sql.toString());

		if (!framework)
			query.setParameter("ID_SISTEMA", idSistema);

		try {

			Integer versao = (Integer) query.getSingleResult();

			if (versao == null)
				return 0;
			else
				return versao;

		} catch (NoResultException e) {
			return 0;
		}
	}

	private boolean existsConstraint(EntityManager em, String schema, String table, EnumTipoConstraint constraintType, String nameConstraint) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * from information_schema.table_constraints ");
		sql.append("where upper(table_name) = upper(:table) and upper(table_schema) = upper(:schema) ");
		sql.append("and upper(constraint_type) = upper(:constraintType) ");

		if (!nameConstraint.equals(""))
			sql.append("and upper(nameConstraint) = upper(" + nameConstraint + ")");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("table", table);
		query.setParameter("schema", schema);
		query.setParameter("constraintType", constraintType.getDescricao());

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	private boolean dropConstraint(EntityManager em, String schema, String table, String constraintName) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(schema + ".");
		sql.append(table);
		sql.append(" drop constraint ");
		sql.append(constraintName.toUpperCase());

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	private List<String> getNameConstraints(EntityManager em, String schema, String table, String nameConstraint, EnumTipoConstraint constraintType, String... fields) {

		List<String> nameForeignKeys = getConstraints(em, schema, table, nameConstraint, constraintType);

		List<String> retorno = new ArrayList<String>();

		if (fields != null && !fields[0].equals("")) {
			for (String nameForeingKey : nameForeignKeys) {

				int i = 0;

				boolean validou = true;

				while (i < fields.length) {
					if (!fieldHasConstraint(em, schema, table, fields[i], nameConstraint)) {
						validou = false;
						break;
					}
					i++;
				}
				if (validou)
					retorno.add(nameForeingKey);
			}
		}
		return retorno;
	}

	private boolean fieldHasConstraint(EntityManager em, String schema, String table, String campo, String nameConstraint) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select count(*) ");
		sql.append(" from information_schema.constraint_column_usage ");
		sql.append(" where upper(table_name) = :table ");
		sql.append(" and  upper(table_schema) = :table_schema ");
		sql.append(" and  upper(column_name) = :column_name ");
		sql.append(" and upper(constraint_name) = :nameConstraint");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("table", table.toUpperCase());
		query.setParameter("table_schema", schema.toUpperCase());
		query.setParameter("column_name", campo.toUpperCase());
		query.setParameter("nameConstraint", nameConstraint.toUpperCase());

		BigInteger result = (BigInteger) query.getSingleResult();
		if (result.intValue() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<String> getConstraints(EntityManager em, String schema, String table, String nameConstraint, EnumTipoConstraint constraintType) {

		StringBuilder sql = new StringBuilder();
		sql.append("select constraint_name from information_schema.table_constraints ");
		sql.append("where upper(table_name) = upper(:table) and upper(table_schema) = upper(:schema) ");
		sql.append("and upper(constraint_type) = upper(:constraintType) ");

		if (!nameConstraint.equals(""))
			sql.append("and upper(constraint_name) = upper('" + nameConstraint + "')");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("table", table);
		query.setParameter("schema", schema);
		query.setParameter("constraintType", constraintType.getDescricao());

		return query.getResultList();
	}

	@Override
	public boolean createTable(EntityManager em, String schema, String table) throws Exception {
		return createTable(em, schema, table, "id", EnumTipoColumnBD.INTEGER, false, null, null, null);
	}

	@Override
	public boolean createTable(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, boolean notNull, String defaultValue, Integer lenght,
			Integer precision) throws Exception {

		validaLength(TypeLenght.TABLE, table);
		
		if (existTable(em, schema, table))
			return false;

		if (!existsSchema(em, schema))
			addSchema(em, schema);

		StringBuilder sql = new StringBuilder();

		sql.append("create table ");
		sql.append(schema);
		sql.append(".");
		sql.append(table);
		sql.append(" (");
		sql.append(collunName);

		if (tipo == EnumTipoColumnBD.VARCHAR)
			sql.append(" varchar(" + lenght + ")");

		else if (tipo == EnumTipoColumnBD.INTEGER)
			sql.append(" integer");
		else if (tipo == EnumTipoColumnBD.BLOB_BINARIO)
			sql.append(" bytea");

		else if (tipo == EnumTipoColumnBD.BLOB_TEXTO)
			sql.append(" varchar");

		else if (tipo == EnumTipoColumnBD.NUMERIC)
			sql.append(" numeric");

		else if (tipo == EnumTipoColumnBD.TIMESTAMP)
			sql.append(" timestamp");
		else if (tipo == EnumTipoColumnBD.DATE)
			sql.append(" date");

		if (notNull)
			sql.append(" not null");

		if (defaultValue != null && !defaultValue.trim().equals(""))
			sql.append(" default " + "'" + defaultValue + "'");

		sql.append(" )");

		sql.append(" ;");
				
		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	private void validaLength(TypeLenght type, String name) {
		name = name.trim();
		String nameType = "";

		if (type.equals(TypeLenght.TABLE))
			nameType = "TABELA";
		else if (type.equals(TypeLenght.COLUMN))
			nameType = "COLUNA";
		else if (type.equals(TypeLenght.PRIMARYKEY))
			nameType = "PRIMARY KEY";
		else if (type.equals(TypeLenght.FOREINGKEY))
			nameType = "FOREING KEY";
		else if (type.equals(TypeLenght.CHECK))
			nameType = "CHECK";
		else if (type.equals(TypeLenght.UNIQUE))
			nameType = "UNIQUE KEY";
		else if (type.equals(TypeLenght.SEQUENCE))
			nameType = "SEQUENCE";
		else if (type.equals(TypeLenght.TRIGGER))
			nameType = "TRIGER";

		if (name.isEmpty())
			throw new RuntimeException("Nome da " + nameType + " vazio!");

		if (name.length() > 31)
			throw new RuntimeException("Nome da " + nameType + " com não pode conter mais de 31 Caracteres.   (" + name + ")");
	}
	
	
	@Override
	public boolean existsSchema(EntityManager em, String schema) {

		StringBuilder sql = new StringBuilder();

		sql.append("select * from information_schema.schemata where ");
		sql.append("upper(schema_name) = upper(:schema) ");
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("schema", schema);

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	@Override
	public boolean addSchema(EntityManager em, String schema) throws Exception {

		if (!existsSchema(em, schema)){
			StringBuilder sql = new StringBuilder();
			sql.append("create schema " + schema);
						
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
		return true;
	}

	@Override
	public void dropTriggerAuditoria(EntityManager em, String schema, String table) {
		// TODO Auto-generated method stub

	}

	@Override
	public void atualizaValorSequence(EntityManager em, String schema, String table, String nameSequence) throws Exception {

		log.debug("Atualizando posição do sequence: " + schema + " " + table);

		Integer maxId = maxId(em, schema, table, "id");

		maxId = (maxId + 1);

		if (maxId > 0) {

			StringBuilder sql = new StringBuilder();

			sql.append("ALTER SEQUENCE ");
			sql.append(schema + ".");
			sql.append(nameSequence);
			sql.append(" RESTART WITH ");
			sql.append(maxId);

			log.debug("  >" + sql.toString());
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}

	}

	@Override
	public boolean existId(EntityManager em, String schema, String table, String value) {
		return existId(em, schema, table, value, "id");
	}

	@Override
	public boolean existId(EntityManager em, String schema, String table, String value, String field) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * from " + schema + "." + table + " where " + field + " = " + value);
		Query query = em.createNativeQuery(sql.toString());

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public boolean addDescriptionTable(EntityManager em, String schema, String table, String description) throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" COMMENT ON TABLE ");
		sql.append(schema + "." + table);
		sql.append(" IS '" + description + "';");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean addDescriptionColumn(EntityManager em, String schema, String table, String column, String description) throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" COMMENT ON COLUMN ");
		sql.append(schema + "." + table + "." + column);
		sql.append(" IS '" + description + "';");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean dropAllUniqueKeys(EntityManager em, String schema, String table) throws Exception {

		List<String> contraints = getConstraints(em, schema, table, "", EnumTipoConstraint.UNIQUE);

		for (String nameConstraint : contraints)
			dropConstraint(em, schema, table, nameConstraint);

		return true;
	}
	
	@Override
	public void dropAllCheck(EntityManager em, String schema, String table) throws Exception {
		
		List<String> contraints = getConstraints(em, schema, table, "", EnumTipoConstraint.CHECK);
		
		for (String nameConstraint : contraints)
			dropConstraint(em, schema, table, nameConstraint);
		
		
	}

	@Override
	public void dropAllForeingKey(EntityManager em, String schema, String table) throws Exception {
		List<String> constraints = getConstraints(em, schema, table, "", EnumTipoConstraint.FOREIGN_KEY);
		for (String constraintName : constraints) {
			dropConstraint(em, schema, table, constraintName);
		}
	}

	@Override
	public boolean existProcedure(EntityManager em, String nameProcedure) throws Exception {

		log.debug(" Existe Procedure Nome: " + nameProcedure + ")");

		StringBuilder sql = new StringBuilder();

		sql.append(" select count(*) from pg_proc p ");
		sql.append(" where lower(p.proname) like :nameProcedure ");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("nameProcedure", "%" + nameProcedure.toLowerCase() + "%");

		if (((BigInteger) query.getSingleResult()).intValue() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getFieldAndPositionPrimaryKey(EntityManager em, String schema, String table) {

		log.debug("Obtendo lista de  Priamry Key da Tabela: " + schema + table);

		StringBuilder sql = new StringBuilder();

		sql.append(" select kcu.column_name as nome_campo , kcu.ordinal_position as posicao_campo ");
		sql.append(" from information_schema.table_constraints tc ");
		sql.append(" join information_schema.key_column_usage kcu on kcu.table_schema = tc.table_schema ");
		sql.append(" and kcu.table_name = tc.table_name and kcu.constraint_name = tc.constraint_name ");
		sql.append(" where lower(tc.table_name) = lower(:TABELA_NOME) and lower(tc.table_schema) = lower(:SCHEMA_NOME) ");
		sql.append(" and upper(tc.constraint_type) = 'PRIMARY KEY' ");
		sql.append(" ORDER BY KCU.ordinal_position ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("TABELA_NOME", table);
		query.setParameter("SCHEMA_NOME", schema);
		try {
			return query.getResultList();
		} catch (Exception e) {
			return new ArrayList<Object[]>();
		}
	}

	@Override
	public boolean existTrigger(EntityManager em, String nameTrigger) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	@Override
	public boolean permissaoAlterarNivelAcesso(EntityManager em, String chavePermissao, Integer idSistema, EnumNivelUsuario nivelUsuario) throws Exception {

		log.debug("definindo nível usuário da permissão de acesso ( chave:" + chavePermissao + " sistema:" + idSistema + " nível:" + nivelUsuario.getDescricao()+ ")");

		StringBuilder sql = new StringBuilder();
		sql.append("update seguranca.na_permissao ");
		sql.append("  set nivel_usuario = :nivelUsuario ");
		sql.append("where chave = :chave ");
		sql.append("  and id_sistema = :idSistema ");

		HashMap<String, Object> params = new HashMap<>();
		params.put("nivelUsuario", nivelUsuario.toInt());
		params.put("chave", chavePermissao);
		params.put("idSistema", idSistema);

		log.debug("script que será executado: " + sql.toString());

		ToolsAtualizaBase.executaCommandoSql(em, sql, params);
		
		return true;

	}
	
	
	@Override
	public boolean permissaoExcluir(EntityManager em, String chavePermissao, Integer idSistema) throws Exception {
		log.debug("excluindo permissão de acesso ( chave:" + chavePermissao + " sistema:" + idSistema + ")");

		StringBuilder sql = new StringBuilder();
		
		HashMap<String, Object> params = new HashMap<>();
		params.put("chave", chavePermissao);
		params.put("idSistema", idSistema);

		// removendo a referencia com os atores
        sql.append("delete from seguranca.na_ator_permissao ");
        sql.append("where id_permissao in (select id ");
        sql.append("                           from seguranca.na_permissao ");
        sql.append("                           where chave = :chave and ");
        sql.append("                                 id_sistema = :idSistema) ");

        ToolsAtualizaBase.executaCommandoSql(em, sql, params);

		// removendo as permissões
		sql = new StringBuilder();
        sql.append("delete from seguranca.na_permissao np ");
		sql.append("where np.chave = :chave ");
		sql.append("  and np.id_sistema = :idSistema ");

		ToolsAtualizaBase.executaCommandoSql(em, sql, params);

		return true;
	}

	@Override
	public boolean alterCollumAddNotNull(EntityManager em, String schema, String table, String column) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(schema);
		sql.append(".");
		sql.append(table);
		sql.append(" ALTER COLUMN ");
		sql.append(column);
		sql.append(" SET NOT NULL;");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean alterCollumRemoveNotNull(EntityManager em, String schema, String table, String column) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(schema);
		sql.append(".");
		sql.append(table);
		sql.append(" ALTER COLUMN ");
		sql.append(column);
		sql.append(" DROP NOT NULL;");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}
		
}
