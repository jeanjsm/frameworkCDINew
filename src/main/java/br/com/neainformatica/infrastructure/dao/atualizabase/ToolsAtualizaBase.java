package br.com.neainformatica.infrastructure.dao.atualizabase;

import java.io.Serializable;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.neainformatica.infrastructure.dao.ToolsDBFirebird;
import br.com.neainformatica.infrastructure.tools.Tools;

public class ToolsAtualizaBase implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Log log = LogFactory.getLog(ToolsDBFirebird.class);

	
	public static void executaCommandoSql(EntityManager em, StringBuilder sql, HashMap<String, Object> parms) throws Exception {
		
		log.debug("executando script: " + sql);

		try {
			Query query = em.createNativeQuery(sql.toString());
			
			if (parms != null) {
				Tools.setarParametrosMapToQuery(parms, query);
				log.debug("PARAMS: "+parms.toString());
			}
			
			query.executeUpdate();
		} catch (Exception e) {
			log.error("Erro ao executar script: " + sql);

			String quebraLinha = System.getProperty("line.separator");

			StringBuilder exceptionEmail = new StringBuilder();
			exceptionEmail.append("Tipo Erro: " + e.getCause() + quebraLinha);
			exceptionEmail.append("Erro ao executar script: " + quebraLinha);
			exceptionEmail.append(quebraLinha + quebraLinha);

			throw new Exception(exceptionEmail.toString(), e);
		}
		
	}
	
	
	public static void executaCommandoSql(EntityManager em, StringBuilder sql) throws Exception {
		executaCommandoSql(em, sql, null);
	}

	/*
	public static void executaCommandoSql(EntityManager em, StringBuilder sql, Query query) throws Exception {

		log.debug("executando script: " + sql);

		try {
			// Query query = em.createNativeQuery(sql.toString());
			query.executeUpdate();
		} catch (Exception e) {
			log.error("Erro ao executar script: " + sql);

			String quebraLinha = System.getProperty("line.separator");

			StringBuilder exceptionEmail = new StringBuilder();
			exceptionEmail.append("Tipo Erro: " + e.getCause() + quebraLinha);
			exceptionEmail.append("Erro ao executar script: " + quebraLinha);
			exceptionEmail.append(quebraLinha + quebraLinha);

			throw new Exception(exceptionEmail.toString(), e);
		}

	}
	*/

}
