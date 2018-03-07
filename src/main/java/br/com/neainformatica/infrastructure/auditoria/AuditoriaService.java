package br.com.neainformatica.infrastructure.auditoria;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.UserTransaction;

import org.apache.commons.logging.Log;

import br.com.neainformatica.infrastructure.dao.IToolsDB;
import br.com.neainformatica.infrastructure.tools.Tools;

@TransactionManagement(TransactionManagementType.BEAN)
@Stateless
public class AuditoriaService {

	@Inject
	private EntityManager em;

	@Inject
	private UserTransaction userTransaction;

	@Inject
	private Log log;

	private AuditoriaTools auditoriaTools;
	private IToolsDB toolsDB = null;

	public void processaEstruturaAuditoria() throws Exception {

		userTransaction.begin();
		log.info("Criando estrutura de Auditoria na base de dados...");
		auditoriaTools = Tools.identificaTipoBancoAuditoria(em);
		toolsDB = Tools.identificaBanco(em);

		auditoriaTools.criaCampos(em, toolsDB);
		auditoriaTools.criaInsereItem(em, toolsDB);
		auditoriaTools.criaGeraTrigger(em, toolsDB);
		auditoriaTools.criaTrigger(em, toolsDB);
		auditoriaTools.criaAuditoriaTabelaAu(em, toolsDB);
		auditoriaTools.criaDisconect(em, toolsDB);
		userTransaction.commit();
		log.info("Estrutura de Auditoria criada com sucesso na base de dados!");

		processaCriacaoTriggers();
	}

	public void processaCriacaoTriggers() throws Exception {
		try {
			userTransaction.begin();
			userTransaction.setTransactionTimeout(2400);

			log.info("Iniciando a criação das Triggers de Auditoria em Banco de Dados...");
			log.info(" Executando Procedure DISCONECT_BASE()... ");

			auditoriaTools.iniciaCriacaoTriggersAuditoria(em);
			userTransaction.commit();

			log.info("Triggers de Auditoria foram criadas com sucesso!");
		} catch (Exception e) {
			userTransaction.commit();
			log.info(" Falha ao executar a procedure DISCONECT_BASE()! ");

			userTransaction.begin();
			log.info("Definindo Tabelas Auditaveis...");
			auditoriaTools.defineTabelasAuditaveis(em);
			log.info("Tabelas Auditaveis Definidas!");

			userTransaction.commit();

			List<NaAuditoriaTabela> listaNaAuditoriaTabela = new ArrayList<NaAuditoriaTabela>();
			listaNaAuditoriaTabela = auditoriaTools.getNaAuditoriaTabela(em, "");
			try {
				log.info(" Iniciando a criação das Triggers através da procedure GERA_TRIGGER()... ");

				for (NaAuditoriaTabela naAuditoriaTabela : listaNaAuditoriaTabela) {
					userTransaction.begin();
					auditoriaTools.getGeraTrigger(em, naAuditoriaTabela);
					userTransaction.commit();
				}

				log.info("Triggers de Auditoria foram criadas com sucesso!");
			} catch (Exception e1) {

				userTransaction.commit();
				log.info(" Falha ao  criar Triggers através da procedure GERA_TRIGGER()! ");
				userTransaction.begin();

				log.info("Iniciando a criação das Triggers através da aplicação FRAMEWORKCDI...");

				for (NaAuditoriaTabela nat : listaNaAuditoriaTabela) {

					auditoriaTools.geraTriggers(em, toolsDB, nat.getIdentificador(), nat.getNome(), "S", nat.getAuditInsert(), nat.getAuditUpdate(), nat.getAuditDelete(),
							nat.getSchema());
				}

				userTransaction.commit();

				log.info("Triggers de Auditoria foram criadas com sucesso!");
			}
		}
	}

}
