package br.com.neainformatica.infrastructure.auditoria;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import br.com.neainformatica.infrastructure.dao.IToolsDB;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;

public interface AuditoriaTools {

	public List<NaAuditoriaTabela> getNaAuditoriaTabela(EntityManager em, String schema) throws Exception;

	public void addFieldsControl(EntityManager em, String schema, Map<String, EnumTipoColumnBD> fieldsControl, IToolsDB toolsDB) throws Exception;

	public void criaCampos(EntityManager em, IToolsDB toolsDb) throws Exception;

	public void criaInsereItem(EntityManager em, IToolsDB toolsDb) throws Exception;

	public void criaGeraTrigger(EntityManager em, IToolsDB toolsDb) throws Exception;

	public void criaTrigger(EntityManager em, IToolsDB toolsDb) throws Exception;

	public void criaAuditoriaTabelaAu(EntityManager em, IToolsDB toolsDb) throws Exception;

	public void criaDisconect(EntityManager em, IToolsDB toolsDb) throws Exception;

	public void iniciaCriacaoTriggersAuditoria(EntityManager em) throws Exception;

	public void geraTriggers(EntityManager em, IToolsDB toolsDb, String identificador, String nomeTabela, String dropTrigger, EnumSimNao auditInsert, EnumSimNao auditUpdate,
			EnumSimNao auditDelete, String nomeSchema) throws Exception;

	public void dropTriggersAuditria(EntityManager em, IToolsDB toolsDb, String nomeSchema, String nomeTabela) throws Exception;

	public void desativaTriggersAuditoria(EntityManager em, IToolsDB toolsDb, String nomeSchema, String identificador, String nomeTabela) throws Exception;

	public List<Object[]> getCamposTabela(EntityManager em, IToolsDB toolsDb, String nomeSchema, String nomeTabela);

	public void defineTabelasAuditaveis(EntityManager em);
	
	public void getGeraTrigger(EntityManager em, NaAuditoriaTabela auditoriaTabela) throws Exception;
	
	public void dropEstruturaAuditoria(EntityManager em) throws Exception;		

}