package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.atualizabase.ToolsAtualizaBase;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoConstraint;

public class ToolsDBFirebird implements IToolsDB, Serializable {
	private static final long serialVersionUID = 1L;

	private Log log = LogFactory.getLog(ToolsDBFirebird.class);
	private  enum TypeLenght { TABLE, COLUMN, PRIMARYKEY, FOREINGKEY, CHECK, UNIQUE, SEQUENCE, TRIGGER }

	@Override
	public boolean existTable(EntityManager em, String schema, String table) {

		log.debug("Existe Tabela (Schema: " + schema + " Nome: " + table + ")");

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) as qtde from rdb$relations r where upper(r.rdb$relation_name) = :table");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("table", table.toUpperCase());

		Integer qtde = (Integer) query.getSingleResult();
		if (qtde > 0) {
			log.debug("-> Sim");
			return true;
		} else {
			log.debug("-> Não");
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
			sql.append("drop table ");
			sql.append(table);
			sql.append(";");
			
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
		return true;
	}

	@Override
	public boolean existCollumn(EntityManager em, String schema, String table, String collunName) {

		log.debug("Verificando se existe collumn (schema:" + schema + " table:" + table + " collumn:" + collunName + ")");

		StringBuilder sql = new StringBuilder();
		sql.append("select * from rdb$relation_fields rc where upper(rc.rdb$relation_name) = :table and upper(rc.rdb$field_name) = :collunname");

		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("table", table.toUpperCase());
		query.setParameter("collunname", collunName.toUpperCase());
		try {
			query.getSingleResult();
			log.debug("-> Sim existe");
			return true;
		} catch (NoResultException e) {
			log.debug("-> Não existe");
			return false;
		}
	}

	@Override
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght) throws Exception {

		return addCollumn(em, schema, table, collunName, tipo, lenght, false, "");
	}

	@Override
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght, boolean notNull, String defaultValue)
			throws Exception {

		return addCollumn(em, schema, table, collunName, tipo, notNull, defaultValue, lenght, null);
	}

	@Override
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, boolean notNull, String defaultValue, Integer lenght,
			Integer precision) throws Exception {

		if (existCollumn(em, schema, table, collunName))
			return true;

		validaLength(TypeLenght.COLUMN, collunName);

		log.debug("adicionando column (schema:" + schema + " table:" + table + " column:" + collunName + " tipo:" + tipo + " notNull:" + notNull + ")");

		dropTriggerAuditoria(em, schema, table);

		StringBuilder sql = new StringBuilder();

		sql.append("alter table ");
		sql.append(table);
		sql.append(" add ");
		sql.append(collunName);

		sql.append(getTypeBD(tipo, lenght, precision));

		if (defaultValue != null && !defaultValue.trim().equals("")) {
			defaultValue = defaultValue.replace("now()", "now");
			sql.append(" default " + "'" + defaultValue + "'");
		}

		if (notNull)
			sql.append(" not null");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	};

	
	private String getTypeBD(EnumTipoColumnBD tipo, Integer lenght, Integer precision) {

		String retorno = "";

		if (tipo == EnumTipoColumnBD.VARCHAR)
			retorno = " varchar(" + lenght + ")";
		else if (tipo == EnumTipoColumnBD.INTEGER)
			retorno = " Integer";
		else if (tipo == EnumTipoColumnBD.BLOB_BINARIO)
			retorno = "  blob sub_type 0 segment size 80";
		else if (tipo == EnumTipoColumnBD.BLOB_TEXTO)
			retorno = " blob sub_type 1 segment size 80";
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
		sql.append(table);
		sql.append(" drop ");
		sql.append(collunName);

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean setCollumnNotNull(EntityManager em, String schema, String table, String collunName) throws Exception {

		if (!existCollumn(em, schema, table, collunName))
			return false;
		
		StringBuilder sql = new StringBuilder();

		sql.append(" update RDB$RELATION_FIELDS set RDB$NULL_FLAG = 1 where (RDB$FIELD_NAME = '");
		sql.append(collunName.toUpperCase());
		sql.append("') and (RDB$RELATION_NAME = '");
		sql.append(table.toUpperCase());
		sql.append("' );");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean setCollumnDefaultValue(EntityManager em, String schema, String table, String collunName, String defaultValue) throws Exception {

		if (!existCollumn(em, schema, table, collunName))
			return false;

		StringBuilder sql = new StringBuilder();
		sql.append("ALTER TABLE " + table.toUpperCase() + " ALTER COLUMN " + collunName.toUpperCase());		
		sql.append(" SET DEFAULT '" + defaultValue + "'");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean setCollumnDefaultValue(EntityManager em, String schema, String table, String collunName, Integer defaultValue) throws Exception {
		if (!existCollumn(em, schema, table, collunName))
			return false;
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("ALTER TABLE " + table.toUpperCase() + " ALTER COLUMN " + collunName.toUpperCase());		
		sql.append(" SET DEFAULT " + defaultValue +" ");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}
	
	@Override
	public boolean setCollumnDropDefaultValue(EntityManager em, String schema, String table, String collunName) throws Exception {
		
		if (!existCollumn(em, schema, table, collunName))
			return false;

		StringBuilder sql = new StringBuilder();
		sql.append("ALTER TABLE " + table.toUpperCase() + " ALTER COLUMN " + collunName.toUpperCase()+" DROP DEFAULT");		

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;		
	}
	
	
	@Override
	public void setCollumnAutoincrement(EntityManager em, String schema, String table, String collunName, String nameSequence) throws Exception {

		addSequence(em, schema, nameSequence);
		
		if (!existCollumn(em, schema, table, collunName))
			throw new Exception("field "+ collunName+ " não existe ");
		
		String nomeTrigger = "BI_"+table+"_INC";
		
		validaLength(TypeLenght.TRIGGER, nomeTrigger);
		
		StringBuilder sql = new StringBuilder();		
		sql.append("CREATE OR ALTER TRIGGER "+nomeTrigger+" FOR "+table+" ACTIVE BEFORE INSERT POSITION 0 AS BEGIN IF (NEW."+collunName+" IS NULL) THEN NEW.ID = GEN_ID("+nameSequence+", 1); END; ");		

		ToolsAtualizaBase.executaCommandoSql(em, sql);
		
	}
	

	@Override
	public boolean alterTypeCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght, Integer precision) throws Exception {

		dropTriggerAuditoria(em, schema, table);

		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(table);
		sql.append(" ALTER COLUMN ");
		sql.append(collunName);
		sql.append(" TYPE ");
		sql.append(getTypeBD(tipo, lenght, precision));

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;

	}

	@Override
	public boolean addPrimaryKey(EntityManager em, String schema, String table, String namePK, String fields) throws Exception {
		
		validaLength(TypeLenght.PRIMARYKEY, namePK);

		if (existPrimaryKey(em, "", table))
			return false;
		
		StringBuilder sql = new StringBuilder();

		sql.append("alter table ");
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
			dropConstraint(em, table, namePrimaryKey);			
		}
		
	}

	@Override
	public String getNamePrimaryKey(EntityManager em, String schema, String table) {

		StringBuilder sql = new StringBuilder();
		sql.append("select rc.rdb$constraint_name from rdb$relation_constraints rc");
		sql.append(" where upper(rc.rdb$relation_name) = :table and");
		sql.append(" upper(rdb$constraint_type) = :constraintType");

		TypedQuery<String> query = (TypedQuery<String>) em.createNativeQuery(sql.toString());

		query.setParameter("table", table.toUpperCase());
		query.setParameter("constraintType", EnumTipoConstraint.PRIMARY_KEY.getDescricao().toUpperCase());

		try {
			return query.getSingleResult();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	};

	@Override
	public boolean existPrimaryKey(EntityManager em, String schema, String table) {

		log.debug("existe PK (schema: " + schema + " Tabela: " + table + ")");

		return existsConstraint(em, table, EnumTipoConstraint.PRIMARY_KEY, "");
	}

	private boolean existsConstraint(EntityManager em, String table, EnumTipoConstraint constraintType, String nameConstraint) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * from rdb$relation_constraints rc");
		sql.append(" where upper(rc.rdb$relation_name) = :table and");
		sql.append(" upper(rdb$constraint_type) = :constraintType");

		if (constraintType.equals(EnumTipoConstraint.PRIMARY_KEY)) {
			try {
				em.createNativeQuery(sql.toString()).setParameter("table", table.toUpperCase()).setParameter("constraintType", constraintType.getDescricao()).getSingleResult();
				return true;
			} catch (NoResultException e) {
				return false;
			}
		}

		sql.append("and upper(rdb$constraint_type) = :nameConstraint");

		try {
			em.createNativeQuery(sql.toString()).setParameter("table", table.toUpperCase()).setParameter("constraintType", constraintType.getDescricao())
					.setParameter("nameconstraint", nameConstraint.toUpperCase()).getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}
	}

	private List<String> getConstraints(EntityManager em, String table, String tableConstraint, String nameConstraint, EnumTipoConstraint constraintType) {

		List<String> resultado;
		StringBuilder sql = new StringBuilder();

		sql.append("select trim(rc.RDB$CONSTRAINT_NAME) as nome_index_constraint ");
		sql.append(" from rdb$relation_constraints rc ");
		sql.append(" where rc.rdb$relation_name = :table ");

		if (!nameConstraint.equals(""))
			sql.append(" and rc.rdb$constraint_name = '" + nameConstraint.toUpperCase() + "'");

		sql.append(" and  rc.rdb$constraint_type in ('PRIMARY KEY', 'FOREIGN KEY', 'CHECK', 'UNIQUE') ");
		sql.append(" and rc.rdb$constraint_type = :constraintType");

		if (!tableConstraint.trim().equals("") && constraintType == EnumTipoConstraint.FOREIGN_KEY) {
			sql.append(" and  (exists(select first 1 refc.rdb$constraint_name ");
			sql.append(" from rdb$ref_constraints refc ");
			sql.append(" join rdb$relation_constraints rc2 on refc.rdb$const_name_uq = rc2.rdb$constraint_name ");
			sql.append(" join rdb$indices i on i.rdb$index_name = rc2.rdb$index_name ");
			sql.append(" where trim(refc.rdb$constraint_name) = rc.rdb$constraint_name and ");
			sql.append(" trim(i.rdb$relation_name) = coalesce( '" + tableConstraint.toUpperCase() + "', trim(i.rdb$relation_name)))) ");
		}

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("table", table.toUpperCase());
		query.setParameter("constraintType", constraintType.getDescricao());

		try {
			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}

	}

	private boolean dropConstraint(EntityManager em, String table, String constraintName) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("alter table ");
		sql.append(table);
		sql.append(" drop constraint ");
		sql.append(constraintName.toUpperCase());

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;

	}

	@Override
	public boolean dropForeingKey(EntityManager em, String schema, String table, String nameTableConstrant, String nameFK, String... fields) throws Exception {

		List<String> nameForeignKeys = getNameConstraints(em, "", table, nameTableConstrant, nameFK, EnumTipoConstraint.FOREIGN_KEY, fields);

		if (nameForeignKeys.size() == 0)
			return false;

		for (String nameForeignKey : nameForeignKeys) {
			dropConstraint(em, table, nameForeignKey);
		}
		return true;
	}

	private List<String> getNameConstraints(EntityManager em, String schema, String table, String nameTableConstrant, String nameFK, EnumTipoConstraint constraintName,
			String... fields) {

		List<String> nameForeignKeys = getConstraints(em, table, nameTableConstrant, nameFK, constraintName);

		List<String> retorno = new ArrayList<String>();

		if (fields != null && !fields[0].equals("")) {
			for (String nameIndexForeingKey : nameForeignKeys) {

				int i = 0;

				boolean validou = true;

				while (i < fields.length) {
					if (!fieldHasConstraint(em, fields[i], nameIndexForeingKey)) {
						validou = false;
						break;
					}
					i++;
				}
				if (validou)
					retorno.add(nameIndexForeingKey);
			}
		}
		return retorno;
	}

	public boolean fieldHasConstraint(EntityManager em, String nameField, String nameIndexConstraint) {

		StringBuilder sql = new StringBuilder();
		sql.append(" select count(*) ");
		sql.append(" from rdb$index_segments s ");
		sql.append(" where upper(s.rdb$index_name) = '" + nameIndexConstraint.toUpperCase() + "' and ");
		sql.append(" upper(s.rdb$field_name) = '" + nameField.toUpperCase() + "'");

		Query query = em.createNativeQuery(sql.toString());

		Integer result;

		try {
			result = (Integer) query.getSingleResult();
			if (result > 0)
				return true;
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	@Override
	public boolean existForeingKey(EntityManager em, String schema, String table, String nameTableConstraint, String nameFK, String... nameFields) throws Exception {

		List<String> constraints = getConstraints(em, table, "", nameFK, EnumTipoConstraint.FOREIGN_KEY);
		if (constraints.size() == 0)
			return false;
		return true;
	}

	@Override
	public void addSequence(EntityManager em, String schema, String nameSequence) throws Exception {

		validaLength(TypeLenght.SEQUENCE, nameSequence);
		
		if (!existSequence(em, schema, nameSequence)) {
			log.debug("Adicionando sequence (schema:" + schema + " nome: " + nameSequence + ")");
			
			StringBuilder sql = new StringBuilder();
			sql.append("create sequence ");
			sql.append(nameSequence);
						
			ToolsAtualizaBase.executaCommandoSql(em, sql);			
		}
	}

	@Override
	public void alterSequence(EntityManager em, String schema, String nameSequence, Integer value) throws Exception {

		validaLength(TypeLenght.SEQUENCE, nameSequence);
		
		if (existSequence(em, schema, nameSequence)){
			StringBuilder sql = new StringBuilder();
			sql.append("alter sequence " + nameSequence + " restart with " + value);
			
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
	}

	
	@Override
	public void addIndex(EntityManager em, String schema, String table, String indexName, String fields) throws Exception {

		addIndex(em, schema, table, indexName, fields, false);
	}

	@Override
	public void addIndex(EntityManager em, String schema, String table, String indexName, String fields, boolean decrecente) throws Exception {

		if (!existIndex(em, schema, table, indexName)) {

			log.debug("Adicionando Index (schema: " + schema + " tabela: " + table + " nome: " + indexName + " campos: (" + fields + ") )");

			StringBuilder sql = new StringBuilder();

			sql.append("create index ");
			if (decrecente)
				sql.append(" descending ");
			sql.append(indexName);
			sql.append(" on ");
			sql.append(table);
			sql.append("(" + fields + ")");

			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
	}

	@Override
	public boolean existIndex(EntityManager em, String schema, String table, String indexName) {

		log.debug("Existe Index (schema: " + schema + " tabela: " + table + " nome: " + indexName + " )");

		StringBuilder sql = new StringBuilder();

		sql.append("select count(*) qtd ");
		sql.append("from rdb$indices rc ");
		sql.append("where upper(rc.rdb$index_name)= ");
		sql.append("'" + indexName + "'");

		Query query = em.createNativeQuery(sql.toString());

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}

	}

	@Override
	public void dropIndex(EntityManager em, String schema, String table, String indexName) throws Exception {

		if (existIndex(em, schema, table, indexName)) {
			StringBuilder sql = new StringBuilder();
			sql.append("drop index " + indexName);
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}

	}

	@Override
	public void addUniqueKey(EntityManager em, String schema, String table, String uniqName, String collunsName) throws Exception {

		if (!existUniqueKey(em, "", table, uniqName)) {

			log.debug("Adicionando UniqueKey (schema: " + schema + " tabela: " + table + " nome: " + uniqName + " campos: (" + collunsName + ")  )");

			StringBuilder sql = new StringBuilder();
			sql.append("alter table ");
			sql.append(table);
			sql.append(" add constraint ");
			sql.append(uniqName);
			sql.append(" unique ");
			sql.append(" (" + collunsName + ")");

			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
	}

	@Override
	public boolean dropUniqueKey(EntityManager em, String schema, String table, String nameTableConstraint, String nameFK, String... nameFields) throws Exception {

		List<String> nameForeignKeys = getNameConstraints(em, "", table, nameTableConstraint, nameFK, EnumTipoConstraint.UNIQUE, nameFields);

		if (nameForeignKeys.size() == 0)
			return false;

		for (String nameForeignKey : nameForeignKeys) {
			dropConstraint(em, table, nameForeignKey);
		}
		return true;

	}

	@Override
	public boolean existUniqueKey(EntityManager em, String schema, String table, String uniqName) {

		log.debug("Existe UniqueKey (schema: " + schema + " tabela: " + table + " nome: " + uniqName + ")");

		List<String> constraints = getConstraints(em, table, "", "", EnumTipoConstraint.UNIQUE);
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

		if (!existCheck(em, "", table, chkName)) {

			StringBuilder sql = new StringBuilder();
			sql.append("alter table ");
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

		List<String> constraints = getConstraints(em, table, "", chkName, EnumTipoConstraint.CHECK);
		if (constraints.size() == 0)
			return false;
		return true;
	}

	@Override
	public boolean dropCheck(EntityManager em, String schema, String table, String nameTableConstraint, String nameFK, String... nameFields) throws Exception {

		List<String> nameForeignKeys = getNameConstraints(em, "", table, nameTableConstraint, nameFK, EnumTipoConstraint.CHECK, nameFields);

		if (nameForeignKeys.size() == 0)
			return false;

		for (String nameForeignKey : nameForeignKeys) {
			dropConstraint(em, table, nameForeignKey);
		}
		return true;
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

		if (!existForeingKey(em, "", table, "", nameFK, "")) {

			log.debug("Adicionando FK  tabela: " + table + " campo: " + collumnOrigem + " nome:    -   destino tabela: " + tabelaDestino + " campo: " + collumnDestino + "");

			StringBuilder sql = new StringBuilder();

			sql.append("alter table ");
			sql.append(table);
			sql.append(" add constraint ");
			sql.append(nameFK);
			sql.append(" foreign key (");
			sql.append(collumnOrigem);
			sql.append(") references ");
			sql.append(tabelaDestino);
			sql.append(" (");
			sql.append(collumnDestino);
			sql.append(")");
			sql.append(" " + rule + " ");
			sql.append(";");

			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}

	}

	@Override
	public void setVersaoBD(EntityManager em, Integer versao, boolean framework, Integer idSistema) throws Exception {

		StringBuilder sql = new StringBuilder();
		HashMap<String, Object> params = new HashMap<>();

		if (framework)
			sql.append(" UPDATE NA_SISTEMA SET VERSAO_BASE = :VERSAO WHERE ID = " + InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID);
		else
			sql.append(" UPDATE NA_SISTEMA SET VERSAO_BASE = :VERSAO WHERE ID = :ID_SISTEMA ");
				
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

	@Override
	public Integer maxId(EntityManager em, String schema, String nameTable, String fieldKey) {

		StringBuilder sql = new StringBuilder();
		sql.append("select max(" + fieldKey + ") from ");
		sql.append(nameTable);

		Query query = em.createNativeQuery(sql.toString());

		try {
			Integer result = (Integer) query.getSingleResult();
			if (result == null)
				return 0;
			return result;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public boolean existSequence(EntityManager em, String schema, String nameSequence) {

		log.debug("existe sequence (schema:" + schema + " nome: " + nameSequence + ")");

		StringBuilder sql = new StringBuilder();
		sql.append("select count(*) qtd ");
		sql.append("from rdb$generators rc ");
		sql.append("where upper(rc.rdb$generator_name)= ");
		sql.append("'" + nameSequence.toUpperCase() + "'");
		Query query = em.createNativeQuery(sql.toString());
		try {
			Integer count = (Integer) query.getSingleResult();
			if (count > 0)
				return true;
			return false;
		} catch (NoResultException e) {
			return false;
		}
	}

	@Override
	public void dropSequence(EntityManager em, String schema, String nameSequence) throws Exception {
		if (existSequence(em, schema, nameSequence)) {
			StringBuilder sql = new StringBuilder();
			sql.append("drop sequence  " + nameSequence);
						
			ToolsAtualizaBase.executaCommandoSql(em, sql);
		}
	}

	@Override
	public boolean createTable(EntityManager em, String schema, String table) throws Exception {
		return createTable(em, "", table, "id", EnumTipoColumnBD.INTEGER, true, null, null, null);
	}

	@Override
	public boolean createTable(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, boolean notNull, String defaultValue, Integer lenght,
			Integer precision) throws Exception {

		if (existTable(em, schema, table))
			return false;

		validaLength(TypeLenght.TABLE, table);

		log.debug("Criando Tabela (Schema: " + schema + " Nome: " + table + ")");

		StringBuilder sql = new StringBuilder();
		sql.append("create table ");
		sql.append(table);

		sql.append(" ( ");

		sql.append(collunName);

		if (tipo == EnumTipoColumnBD.VARCHAR)
			sql.append(" varchar(" + lenght + ")");

		else if (tipo == EnumTipoColumnBD.INTEGER)
			sql.append(" integer");

		else if (tipo == EnumTipoColumnBD.BLOB_BINARIO)
			sql.append(" blob sub_type 0 segment size 80");

		else if (tipo == EnumTipoColumnBD.BLOB_TEXTO)
			sql.append(" blob sub_type 1 segment size 80");

		else if (tipo == EnumTipoColumnBD.NUMERIC)
			sql.append(" numeric(" + lenght + "," + precision + ")");

		else if (tipo == EnumTipoColumnBD.TIMESTAMP)
			sql.append(" timestamp");
		else if (tipo == EnumTipoColumnBD.DATE)
			sql.append(" date");

		if (defaultValue != null && !defaultValue.trim().equals(""))
			sql.append(" default " + "'" + defaultValue + "'");

		if (notNull)
			sql.append(" not null");

		sql.append(" );");
		
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addSchema(EntityManager em, String schema) {
		// TODO Auto-generated method stub
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dropTriggerAuditoria(EntityManager em, String schema, String table) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct T.RDB$TRIGGER_NAME ");
		sql.append("from RDB$TRIGGERS T ");
		sql.append("where (substring(upper(T.RDB$TRIGGER_NAME) from 1 for 6) = 'AUDIT$') and  ");
		sql.append("(upper(T.RDB$RELATION_NAME) = '" + table.toUpperCase() + "') ");
		sql.append("order by T.RDB$TRIGGER_NAME ");

		Query query = em.createNativeQuery(sql.toString());

		List<Object> resultado = query.getResultList();

		for (Object object : resultado) {
			String nameTrigger = (String) object;

			StringBuilder comandoDropTrigger = new StringBuilder();
			comandoDropTrigger.append("drop trigger " + nameTrigger.trim() + ";");

			ToolsAtualizaBase.executaCommandoSql(em, comandoDropTrigger);
		}

	}

	@Override
	public void atualizaValorSequence(EntityManager em, String schema, String table, String nameSequence) throws Exception {
		
		Integer maxId = maxId(em, schema, table, "id");

		StringBuilder sql = new StringBuilder();
		sql.append("ALTER SEQUENCE ");
		sql.append(nameSequence);
		sql.append(" RESTART WITH ");
		sql.append(maxId);

		ToolsAtualizaBase.executaCommandoSql(em, sql);

	}

	@Override
	public boolean existId(EntityManager em, String schema, String table, String value) {
		return existId(em, schema, table, value, "id");
	}

	@Override
	public boolean existId(EntityManager em, String schema, String table, String value, String field) {

		StringBuilder sql = new StringBuilder();
		sql.append("select * from " + table + " where " + field + " = :value");
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("value", value);

		try {
			query.getSingleResult();
			return true;
		} catch (NoResultException e) {
			return false;
		}

	}

	@Override
	public boolean addDescriptionTable(EntityManager em, String schema, String table, String description) throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" COMMENT ON TABLE ");
		sql.append(table);
		sql.append(" IS ");
		sql.append("'" + description + "'");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean addDescriptionColumn(EntityManager em, String schema, String table, String column, String description) throws Exception {
		StringBuilder sql = new StringBuilder();

		sql.append(" COMMENT ON COLUMN ");
		sql.append(table + "." + column);
		sql.append(" IS ");
		sql.append("'" + description + "'");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean dropAllUniqueKeys(EntityManager em, String schema, String table) throws Exception {

		List<String> contraints = getConstraints(em, table, "", "", EnumTipoConstraint.UNIQUE);

		for (String nameConstraint : contraints)
			dropConstraint(em, table, nameConstraint);

		return true;
	}

	@Override
	public void dropAllForeingKey(EntityManager em, String schema, String table) throws Exception {
		List<String> nameForeignKeys = getNameConstraints(em, schema, table, "", "", EnumTipoConstraint.FOREIGN_KEY, null);

		for (String fk : nameForeignKeys) {
			dropConstraint(em, table, fk);
		}
	}
	
	@Override
	public void dropAllCheck(EntityManager em, String schema, String table) throws Exception {
		
		List<String> contraints = getConstraints(em, table, "", "", EnumTipoConstraint.CHECK);

		for (String nameConstraint : contraints)
			dropConstraint(em, table, nameConstraint);
		
	}


	@Override
	public boolean existProcedure(EntityManager em, String nameProcedure) throws Exception {

		StringBuilder sql = new StringBuilder();

		sql.append(" select count(*) from RDB$PROCEDURES ");
		sql.append(" where upper(RDB$PROCEDURE_NAME) = :nameProcedure ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("nameProcedure", nameProcedure.toUpperCase());

		if (((Integer) query.getSingleResult()).intValue() > 0)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getFieldAndPositionPrimaryKey(EntityManager em, String schema, String table) {

		log.debug("Obtendo lista de  Priamry Key da Tabela: " + table);

		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT  TRIM(IP.RDB$FIELD_NAME) as nome_campo , IP.RDB$FIELD_POSITION as posicao_campo ");
		sql.append(" FROM RDB$RELATION_CONSTRAINTS RC ");
		sql.append(" LEFT JOIN RDB$INDEX_SEGMENTS IP ON IP.RDB$INDEX_NAME = RC.RDB$INDEX_NAME ");
		sql.append(" WHERE RC.RDB$RELATION_NAME = :TABELA_NOME AND ");
		sql.append(" RC.RDB$CONSTRAINT_TYPE = 'PRIMARY KEY' ORDER BY IP.RDB$FIELD_POSITION ");

		Query query = em.createNativeQuery(sql.toString());

		query.setParameter("TABELA_NOME", table);

		try {
			return query.getResultList();
		} catch (Exception e) {
			return new ArrayList<Object[]>();
		}
		
	}

	@Override
	public boolean existTrigger(EntityManager em, String nameTrigger) throws Exception {

		StringBuilder sql = new StringBuilder();

		sql.append(" select count(*) from RDB$TRIGGERS where upper(RDB$TRIGGER_NAME) = :nameTrigger ");
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter("nameTrigger", nameTrigger.toUpperCase());

		if ( (Integer) query.getSingleResult() > 0)
			return true;
		return false;
	}

	@Override
	public boolean permissaoAlterarNivelAcesso(EntityManager em, String chavePermissao, Integer idSistema, EnumNivelUsuario nivelUsuario) throws Exception {

		log.debug("definindo nível usuário da permissão de acesso ( chave:" + chavePermissao + " sistema:" + idSistema + " nível:" + nivelUsuario.getDescricao()+ ")");

		StringBuilder sql = new StringBuilder();
		sql.append("update na_permissao ");
		sql.append("  set nivel_usuario = :nivelUsuario ");
		sql.append("  where chave = :chave ");
		sql.append("  and id_sistema = :idSistema ");

		HashMap<String, Object> params = new HashMap<>();
		params.put("nivelUsuario", nivelUsuario.toInt());
		params.put("chave", chavePermissao);
		params.put("idSistema", idSistema);

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
		sql.append("delete from NA_ATOR_PERMISSAO ");
		sql.append("where ID_PERMISSAO in (select ID ");
		sql.append("                           from NA_PERMISSAO ");
		sql.append("                           where CHAVE = :CHAVE ");
		sql.append("                             and ID_SISTEMA = :IDSISTEMA) ");
						
		ToolsAtualizaBase.executaCommandoSql(em, sql, params);		

		// removendo as permissões
		sql = new StringBuilder();
		sql.append("delete from NA_PERMISSAO NP ");
		sql.append("where NP.CHAVE = :CHAVE ");
		sql.append("  and NP.ID_SISTEMA = :IDSISTEMA ");

		ToolsAtualizaBase.executaCommandoSql(em, sql, params);

		return true;
	}

	@Override
	public boolean alterCollumAddNotNull(EntityManager em, String schema, String table, String column) throws Exception {

		StringBuilder sql = new StringBuilder();
		sql.append(" update RDB$RELATION_FIELDS set ");
		sql.append(" RDB$NULL_FLAG = 1 ");
		sql.append(" where (UPPER(RDB$FIELD_NAME) = " + column.toUpperCase() + ") and ");
		sql.append(" (UPPER(RDB$RELATION_NAME) = " + table.toUpperCase() + ")");

		ToolsAtualizaBase.executaCommandoSql(em, sql);

		return true;
	}

	@Override
	public boolean alterCollumRemoveNotNull(EntityManager em, String schema, String table, String column) throws Exception {

			
		StringBuilder sql = new StringBuilder();
		sql.append(" update RDB$RELATION_FIELDS set ");
		sql.append(" RDB$NULL_FLAG = null ");
		sql.append(" where (UPPER(RDB$FIELD_NAME) = '" + column.toUpperCase() + "') and ");
		sql.append(" (UPPER(RDB$RELATION_NAME) = '" + table.toUpperCase() + "')");		
		
		ToolsAtualizaBase.executaCommandoSql(em, sql);
		
		return true;	
	}

	

}
