package br.com.neainformatica.infrastructure.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;

public interface IToolsDB {
	
	public boolean existTable(EntityManager em, String schema, String table);
	public boolean dropTable(EntityManager em, String schema, String table) throws Exception;
	public boolean createTable(EntityManager em, String schema, String table) throws Exception;
	public boolean createTable(EntityManager em, String schema, String table,String collunName, EnumTipoColumnBD tipo, boolean notNull, String defaultValue,Integer lenght,Integer precision) throws Exception;
	
	public boolean existsSchema(EntityManager em, String schema);
	public boolean addSchema(EntityManager em, String schema) throws Exception;
	
	public boolean existCollumn(EntityManager em, String schema, String table, String collunName);
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght) throws Exception;
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght,boolean notNull, String defaultValue) throws Exception;
	public boolean addCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, boolean notNull, String defaultValue,Integer lenght,Integer precision) throws Exception;
	public boolean dropCollumn(EntityManager em, String schema, String table, String collunName) throws Exception;
	public boolean setCollumnNotNull(EntityManager em, String schema, String table, String collunName) throws Exception;
	public boolean setCollumnDropDefaultValue(EntityManager em, String schema, String table, String collunName) throws Exception;
	public boolean setCollumnDefaultValue(EntityManager em, String schema, String table, String collunName, String defaultValue) throws Exception;
	public boolean setCollumnDefaultValue(EntityManager em, String schema, String table, String collunName, Integer defaultValue) throws Exception;
	public void setCollumnAutoincrement(EntityManager em, String schema, String table, String collunName, String nameSequence) throws Exception;
	public boolean alterTypeCollumn(EntityManager em, String schema, String table, String collunName, EnumTipoColumnBD tipo, Integer lenght, Integer precision) throws Exception;

	public boolean addDescriptionTable(EntityManager em, String schema, String table, String description) throws Exception;
	public boolean addDescriptionColumn(EntityManager em, String schema, String table, String column, String description) throws Exception;
	
	public boolean addPrimaryKey(EntityManager em, String schema, String table, String namePK, String fields) throws Exception;
	public void dropPrimaryKey(EntityManager em, String schema, String table) throws Exception;
	public boolean existPrimaryKey(EntityManager em, String schema, String table);
	public String getNamePrimaryKey(EntityManager em, String schema, String table);
	public List<Object[]> getFieldAndPositionPrimaryKey(EntityManager em, String schema, String table);
	
	public void addForeingKey(EntityManager em, String schema, String table, String nameFK,  String schemaTableDestino, String tabelaDestino, String collumnDestino, String collumnOrigem) throws Exception;
	public void addForeingKey(EntityManager em, String schema, String table, String nameFK,  String schemaTableDestino, String tabelaDestino, String collumnDestino, String collumnOrigem, String role) throws Exception;	
	public boolean dropForeingKey(EntityManager em, String schema, String table,String nameTableConstraint , String nameFK,String... nameFields) throws Exception;
	public boolean existForeingKey(EntityManager em, String schema, String table,String nameTableConstraint , String nameFK,String... nameFields) throws Exception;
	public void dropAllForeingKey(EntityManager em, String schema, String table) throws Exception;
		
	public void addSequence(EntityManager em, String schema, String nameSequence) throws Exception;
	public Integer maxId(EntityManager em, String schema, String nameTable, String fieldKey);
	public void alterSequence(EntityManager em, String schema, String nameSequence, Integer value) throws Exception;
	public boolean existSequence(EntityManager em, String schema, String nameSequence);
	public void dropSequence(EntityManager em, String schema, String nameSequence) throws Exception;
	
	public void addIndex(EntityManager em, String schema, String table, String indexName, String fields) throws Exception;
	public void addIndex(EntityManager em, String schema, String table, String indexName, String fields, boolean decrecente) throws Exception;
	public boolean existIndex(EntityManager em, String schema, String table, String indexName);
	public void dropIndex(EntityManager em, String schema, String table, String indexName) throws Exception;
	
	public void addUniqueKey(EntityManager em, String schema, String table, String uniqName, String collunsName) throws Exception;
	public boolean dropUniqueKey(EntityManager em, String schema, String table,String nameTableConstraint , String nameFK,String... nameFields) throws Exception;
	public boolean existUniqueKey(EntityManager em, String schema, String table, String uniqName);
	
	public void addCheck(EntityManager em, String schema, String table, String chkName, String valores) throws Exception;	
	public boolean existCheck(EntityManager em, String schema, String table, String chkName);
	public boolean dropCheck(EntityManager em, String schema, String table, String nameTableConstraint, String nameFK, String... nameFields) throws Exception;
	public void dropAllCheck(EntityManager em, String schema, String table) throws Exception;
	
	public void setVersaoBD(EntityManager em, Integer versao, boolean framework, Integer idSistema) throws Exception;
	public Integer getVersaoBD(EntityManager em, boolean framework, Integer idSistema);
	
	public void dropTriggerAuditoria(EntityManager em, String schema, String table) throws Exception;
	public void atualizaValorSequence(EntityManager em, String schema, String table, String nameSequence) throws Exception;
		
	public boolean existId(EntityManager em, String schema, String table, String value);
	public boolean existId(EntityManager em, String schema, String table, String value, String field);
	
	public boolean dropAllUniqueKeys(EntityManager em, String schema, String table) throws Exception;
	
	public boolean existProcedure(EntityManager em, String nameProcedure) throws Exception;
	public boolean existTrigger(EntityManager em, String nameTrigger) throws Exception;
	
	public boolean permissaoAlterarNivelAcesso(EntityManager em, String chavePermissao, Integer idSistema, EnumNivelUsuario nivelUsuario) throws Exception;
	public boolean permissaoExcluir(EntityManager em, String chavePermissao, Integer idSistema) throws Exception;
		
	public boolean alterCollumAddNotNull(EntityManager em, String schema, String table, String column) throws Exception;
	public boolean alterCollumRemoveNotNull(EntityManager em, String schema, String table, String column) throws Exception;
}
