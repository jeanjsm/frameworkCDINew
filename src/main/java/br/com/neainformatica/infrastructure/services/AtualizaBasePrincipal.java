package br.com.neainformatica.infrastructure.services;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.hibernate.Session;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.IToolsDB;
import br.com.neainformatica.infrastructure.dao.ToolsDBPostgre;
import br.com.neainformatica.infrastructure.dao.atualizabase.AtualizaBaseFramework01;
import br.com.neainformatica.infrastructure.entity.NaParametroValor;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumParametroFramework;
import br.com.neainformatica.infrastructure.mail.EmailService;
import br.com.neainformatica.infrastructure.tools.NeaStrings;
import br.com.neainformatica.infrastructure.tools.Tools;
import br.com.neainformatica.infrastructure.tools.ToolsPackage;

@TransactionManagement(TransactionManagementType.BEAN)
@Stateless
public class AtualizaBasePrincipal implements Serializable {
	private static final long serialVersionUID = 1L;
	static String quebraLinha = System.lineSeparator();

	static String PREFIXO_CLASS_ATUALIZA = "AtualizaBase";
	static String PREFIXO_SCRIPT_ATUALIZA = "scriptAtualiza";

	private Integer versaoAtualBase;

	private IToolsDB toolsDb = null;
	private Integer versaoTotal;
	private HashMap<Integer, Class> mapaInstancias;

	@Inject
	private UserTransaction userTransaction;

	@Inject
	private Log log;

	@Inject
	private Instance<EmailService> emailService;

	@Inject
	private NaParametroService parametroService;

	@Inject
	private NaSistemaService sistemaService;

	@Inject
	private EntityManager entityManager;

	public void iniciarAtualizacao(boolean framework, int idSistema) throws Exception {

		log.debug("Iniciando atualização de base idSistema:" + idSistema);

		if (framework) {
			PREFIXO_CLASS_ATUALIZA = "AtualizaBaseFramework";
			PREFIXO_SCRIPT_ATUALIZA = "scriptAtualizaFramework";
		} else {
			PREFIXO_CLASS_ATUALIZA = "AtualizaBase";
			PREFIXO_SCRIPT_ATUALIZA = "scriptAtualiza";
		}

		toolsDb = Tools.identificaBanco(entityManager);

		if (toolsDb == null)
			return;

		if (framework) {

			if (toolsDb.existCollumn(entityManager, AtualizaBaseFramework01.SCHEMA_PUBLIC, "NA_CLIENTE", "VERSAO_BASE_FRAMEWORK")) {

				userTransaction.begin();

				if (toolsDb instanceof ToolsDBPostgre)
					entityManager
							.createNativeQuery(
									"UPDATE NA_SISTEMA SET VERSAO_BASE = ( SELECT COALESCE(VERSAO_BASE_FRAMEWORK, 0) FROM NA_CLIENTE LIMIT 1) WHERE ID = 1002;")
							.executeUpdate();
				else
					entityManager
							.createNativeQuery(
									"UPDATE NA_SISTEMA SET VERSAO_BASE = (SELECT FIRST 1 COALESCE(VERSAO_BASE_FRAMEWORK,0) FROM NA_CLIENTE) WHERE ID = 1002;")
							.executeUpdate();

				userTransaction.commit();

			}				
			
			
			// como o allocationSize da auditoria e auditoria_item é maior que 1 sempre que a aplicação iniciar vamos atualizar os valores
			userTransaction.begin();
			if (toolsDb.existTable(entityManager, "auditoria", "na_auditoria"))
				toolsDb.atualizaValorSequence(entityManager, "auditoria", "NA_AUDITORIA", "seq_na_auditoria");
			
			if (toolsDb.existTable(entityManager, "auditoria", "na_auditoria_item"))
				toolsDb.atualizaValorSequence(entityManager, "auditoria", "na_auditoria_item", "seq_na_auditoria_item");
			userTransaction.commit();
			
		}

		log.debug("obtendo versão atual da base de dados");

		if (!toolsDb.existTable(entityManager, "public", "na_sistema") || !toolsDb.existCollumn(entityManager, "public", "na_sistema", "versao_base")){
			log.debug("Não encontrou a tabela na_sistema ou não encontrou o campo versão base. ");
			this.versaoAtualBase = 0;
		}
		else{
			this.versaoAtualBase = toolsDb.getVersaoBD(entityManager, framework, idSistema);
		}

		log.debug("Versão atual: " + this.versaoAtualBase);

		montarMapaInstancias();
		executaAtualizacao(framework, idSistema);
	}

	/**
	 * Varre o projeto buscando todas a classes que iniciam com
	 * AtualizaBaseFramework. <br/>
	 * podem ser AtualizaBaseFramework01,
	 * AtualizaBaseFramework02AtualizaBaseFramework03...<br/>
	 * Identificando onde esta cada script
	 * 
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private void montarMapaInstancias() throws ClassNotFoundException, IOException {

		log.debug("Iniciando busca pelas classes de atualização de base");

		List<Class> classesAtualizacao = new ArrayList<Class>();
		mapaInstancias = new HashMap<Integer, Class>();
		this.versaoTotal = 0;

		/* Buscando as classes de atualição disponíveis no projeto */
		List<Class<?>> classes = ToolsPackage.listClassesInPackage("br.com.neainformatica", PREFIXO_CLASS_ATUALIZA);
		for (Class<?> c : classes) {
			if (c.getSimpleName().startsWith(PREFIXO_CLASS_ATUALIZA)) {
				Method[] methods = c.getDeclaredMethods();
				for (Method m : methods) {
					if (m.getName().startsWith(PREFIXO_SCRIPT_ATUALIZA)) {

						log.debug("classe encontrada: " + m.getName());

						String nomeFimDoMetodo = m.getName().substring(PREFIXO_SCRIPT_ATUALIZA.length());

						if (NeaStrings.somenteNumeros(nomeFimDoMetodo).equals(nomeFimDoMetodo)) {
							Integer codVersao = Integer.parseInt(m.getName().substring(PREFIXO_SCRIPT_ATUALIZA.length()));
							mapaInstancias.put(codVersao, c);
							if (codVersao > this.versaoTotal)
								this.versaoTotal = codVersao;
						}
					}
				}
			}
		}
		log.debug("Finalizou a busca pelas classes de atualização de base");
	}

	public void executaAtualizacao(boolean framework, int idSistema) {

		if (this.versaoAtualBase == null)
			this.versaoAtualBase = 0;

		int codigoScriptExecutado = 0;
		try {

			for (int i = this.versaoAtualBase; i < this.versaoTotal; i++) {
				codigoScriptExecutado = i + 1;
				log.info("executando script " + (i + 1));
				userTransaction.begin();
				userTransaction.setTransactionTimeout(10800000);
				Class<?> classAtualiza = (Class<?>) mapaInstancias.get(i + 1);
				String codScript = StringUtils.leftPad(String.valueOf(i + 1), 6, "0");
				executaScript(classAtualiza, codScript, i + 1);

				if (!framework || Integer.parseInt(codScript) > 1)
					toolsDb.setVersaoBD(this.entityManager, i + 1, framework, idSistema);

				userTransaction.commit();
				log.info("script " + (i + 1) + " executado!");

			}
		} catch (Exception e) {
			try {
				userTransaction.rollback();
			} catch (IllegalStateException | SecurityException | SystemException e1) {
				log.error("Erro ao execuatar rollback Transação- ", e1.getCause());
			}
			// Só envia e-mail caso as tabelas de parâmetros esteja criadas!

			if (codigoScriptExecutado > 20 || !framework)
				enviarEmailResponsaveis(e, codigoScriptExecutado, idSistema);

			log.error("Erro ao executar Atualiza Base - ", e.getCause());

			e.printStackTrace();
		}
	}

	private void executaScript(Class<?> classAtualiza, String codScript, int versao) throws Exception {

		Method method;

		// busca classe

		Object obj = classAtualiza.newInstance();

		// seta o tipo de banco
		method = classAtualiza.getMethod("setToolsDb", IToolsDB.class);
		method.invoke(obj, this.toolsDb);

		method = classAtualiza.getMethod("setEntityManager", EntityManager.class);
		method.invoke(obj, this.entityManager);

		// executa o scipt
		method = classAtualiza.getMethod(PREFIXO_SCRIPT_ATUALIZA + codScript, null);
		method.invoke(obj, null);

	}

	public Session getSession(EntityManager entityManager) {
		return (Session) entityManager.getDelegate();
	}

	public HashMap<Integer, Class> getMapaInstancias() {
		return mapaInstancias;
	}

	public void setMapaInstancias(HashMap<Integer, Class> mapaInstancias) {
		this.mapaInstancias = mapaInstancias;
	}

	public void enviarEmailResponsaveis(Exception e, int codigoScript, Integer idSistema) {

		NaSistema sistema = sistemaService.findByField(idSistema, "id");

		StringBuilder mensagem = montarMensagemErroAtualizaBase(sistema, codigoScript, e);

		try {

			String listaEmails = obterDestinatariosEmail();

			emailService.get().sendMail(listaEmails, "", "Erro ao executar a rotina de Atualização de Base", mensagem);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private StringBuilder montarMensagemErroAtualizaBase(NaSistema sistema, int codigoScript, Exception e) {

		String ipServidor = obterIpMaquina();
		
		StringBuilder corpoEmail = new StringBuilder();
		corpoEmail.append("<br/>Erro ao executar atualização de Base");		
		corpoEmail.append("<br/>Um erro ocorreu ao executar o script - <b>" + codigoScript+"</b>");		
		corpoEmail.append("<br/>Sistema - " + sistema.getSistema());
		corpoEmail.append("<br/>IP: <b>" + ipServidor + "</b>");
		corpoEmail.append("<br/>");
		corpoEmail.append("<br/>");				
		corpoEmail.append("<br/> -----------------------------------------------------------------");
		corpoEmail.append("<br/> cause: " + e.getCause() + "<br/>");
		corpoEmail.append("<br/> message: " + e.getMessage());
		corpoEmail.append("<br/> stacktrace: " + Tools.stackTraceToString(e));
		corpoEmail.append("<br/> -----------------------------------------------------------------");	
		corpoEmail.append(quebraLinha);

		NeaStrings.replaceAllStringBuilder(corpoEmail, quebraLinha, "<br />");
		NeaStrings.replaceAllStringBuilder(corpoEmail, "\n", "<br />");

		return corpoEmail;
	}
	
	public String obterIpMaquina() {
		InetAddress addr = null;
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return addr.getHostAddress() + " " + addr.getHostName();

	}


	private String obterDestinatariosEmail() {
		NaParametroValor parametro = parametroService.getValorByParametro(EnumParametroFramework.EMAILS_NOTIFICADOS,
				InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID);
		String listaEmails = parametro.getValor();

		return listaEmails;
	}

}
