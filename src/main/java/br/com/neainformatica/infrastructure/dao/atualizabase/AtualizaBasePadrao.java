package br.com.neainformatica.infrastructure.dao.atualizabase;

import java.io.InputStream;
import java.nio.charset.Charset;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.neainformatica.infrastructure.dao.IToolsDB;

public abstract class AtualizaBasePadrao {

	protected EntityManager em;
	private IToolsDB toolsDb;
	private Log log = LogFactory.getLog(AtualizaBasePadrao.class);

	public void executeSQL(String sql) throws Exception {	
		executaCommandoSql(em, new StringBuilder(sql));
	}	

	/**
	 * Executa o script com o mesmo nome do método de origem
	 * @throws Exception 
	 */
	public void executaScript() throws Exception {

		Throwable throwable = new Throwable();
		throwable.fillInStackTrace();
		StackTraceElement[] stackTraceElements = throwable.getStackTrace();
		String methodName = stackTraceElements[1].getMethodName();

		executaScript(methodName + ".sql");
	}

	/**
	 * Executa o script com o nome passado como parâmetro
	 * @throws Exception 
	 */
	public void executaScript(String nameScript) throws Exception {

		log.debug("procurando script " + nameScript);

		InputStream stream = getClass().getResourceAsStream("/neainformatica/scripts-atualiza-base/" + nameScript);

		if (stream == null)
			stream = getClass().getResourceAsStream("/META-INF/resources/neainformatica/scripts-atualiza-base/" + nameScript);
		
		if (stream == null) 
			stream = getClass().getResourceAsStream("/resources/neainformatica/scripts-atualiza-base/" + nameScript);
		
		if (stream == null) 
			stream = getClass().getResourceAsStream("resources/neainformatica/scripts-atualiza-base/" + nameScript);

		if (stream == null) {
			log.error("--------------------------------------------------------");
			log.error("Não foi possível encontrar o arquivo contendo scripts.  (arquivo: " + nameScript + ") ");
			log.error("--------------------------------------------------------");

			throw new RuntimeException("Erro ao executar script externo de atualizaBase.");
		}

		String script = null;		
		script = IOUtils.toString(stream, Charset.forName("UTF-8"));
		script = script.replaceAll(":", "\\\\:");
		log.debug("executando script");
		log.debug(script);
		
		executaCommandoSql(em, new StringBuilder(script));									
	}
	
	private void executaCommandoSql(EntityManager em, StringBuilder sql) throws Exception {

		log.debug("executando script: " + sql);

		try {
			Query query = em.createNativeQuery(sql.toString());
			query.executeUpdate();
		} catch (Exception e) {
			log.error("Erro ao executar script: " + sql);
			
			String quebraLinha = System.getProperty("line.separator");
			
			StringBuilder exceptionEmail = new StringBuilder();
			exceptionEmail.append("Tipo Erro: " + e.getCause() + quebraLinha);
			exceptionEmail.append("Erro ao executar script: " + quebraLinha);
			exceptionEmail.append(sql+quebraLinha);								
			
			throw new Exception(exceptionEmail.toString(), e);
		}
	}

	/**
	 * Utilize apenas db()
	 * @return
	 */
	@Deprecated
	public IToolsDB getToolsDb() {
		return toolsDb;
	}
	
	public IToolsDB db() {
		return toolsDb;
	}

	public void setToolsDb(IToolsDB toolsDb) {
		this.toolsDb = toolsDb;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.em = entityManager;

	}

}
