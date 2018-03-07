package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.com.neainformatica.infrastructure.auditoria.AuditoriaService;
import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumParametroFramework;
import br.com.neainformatica.infrastructure.scheduler.NaSchedullerController;

@Singleton
@ApplicationScoped
public class AtualizaBaseService implements Serializable {

	private static final long serialVersionUID = 1L;

	public static boolean MONTAR_ESTRUTURA_PERFIS = true;
	public static boolean BLOQUEIA_LOGIN_USUARIO_SUPORTE = false;
	public static boolean ATIVA_ATUALIZA_BASE = true;
	public static boolean ATIVA_AUDITORIA_BASE_DADOS = false;
	public static boolean SISTEMA_SEM_LOGIN_CLIENTE_UNICO = false;
	public static boolean LIMPAR_NACLIENTE_SESSAO_AO_LOGAR = true;
	public static boolean ATIVAR_AGENDAMENTOS = true;
	public static boolean ADICIONA_PERFIL_TI_E_ADMINISTRADOR = true;
	public static String NOME_ENUM_PARAMETROS = "EnumParametro";
	public static String NOME_ENUM_AGENDAMENTOS = "EnumTarefa";
	public static String MENSAGEM_SETOR_RESPONSAVEL_USUARIOS = "Procure o setor de TI";

	@Inject
	protected Log log;

	@Inject
	private InfrastructureService infrastructureService;

	//@Inject
	//protected EntityManager entityManager;

	@Inject
	private NaSistemaService naSistemaService;

	@Inject
	private NaPerfilService naPerfilService;

	@Inject
	private NaUsuarioService naUsuarioService;

	@Inject
	private NaPermissaoService naPermissaoService;

	@Inject
	AtualizaBasePrincipal atualizaBasePrincipal;

	@Inject
	NaAgendamentoService naAgendamentoService;

	@Inject
	NaSchedullerController schedulerController;

	@Inject
	AuditoriaService auditoriaService;

	@PostConstruct
	public void init() {

		// naPermissaoService.setUsuarioAuditoria("TESTE");

		try {
			if (ATIVA_ATUALIZA_BASE)
				atualizaBase(InfrastructureController.getNeaInfrastructureSistemaId());

			naSistemaService.verificaSistemaCadastrado(InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID, "FrameworkCDI");

			Integer idSistema = InfrastructureController.getNeaInfrastructureSistemaId();
			String nomeSistema = InfrastructureController.getNeaInfrastructureSistemaNomeIdentify();
			NaSistema sistema = naSistemaService.verificaSistemaCadastrado(idSistema, nomeSistema);

			InfrastructureController.setNaSistema(sistema);

			log.debug("Sistema iniciado:  " + sistema.getId() + "-" + sistema.getSistema());

			infrastructureService.verificaClienteDefault();
			infrastructureService.criaParametrosDevaloresFixo(sistema, NOME_ENUM_PARAMETROS);
			infrastructureService.criaParametrosDevaloresFixo(InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID, EnumParametroFramework.class.getSimpleName());

			// atorRepository.removeAtoresSemVinculos();

			if (MONTAR_ESTRUTURA_PERFIS) {

				List<NaCliente> clientes = infrastructureService.getDaoCliente().buscarListaCliente();

				for (NaCliente cliente : clientes) {
					if (ADICIONA_PERFIL_TI_E_ADMINISTRADOR) {
						naPerfilService.adicionaPerfilDefault(cliente, sistema, NaPermissaoService.PERFIL_TI);
						naPerfilService.adicionaPerfilDefault(cliente, sistema, NaPermissaoService.PERFIL_ADMINISTRADOR);
						naPermissaoService.criaAtoresPermissoesDefaultUsuarioTI(cliente, sistema);
						naPermissaoService.criaAtoresPermissoesDefaultUsuarioAdministrador(cliente, sistema);
					}
					naUsuarioService.definirUsuarioSuporte(cliente, sistema);
					naSistemaService.verificaSistemaCadastrado(idSistema, nomeSistema);
					naPermissaoService.criaPermissoesDefault(sistema.getId());
				}
			}

			if (ATIVAR_AGENDAMENTOS) {
				schedulerController.atualizaSchedulers();
			}
			if (ATIVA_AUDITORIA_BASE_DADOS) {
				auditoriaService.processaEstruturaAuditoria();
			}
		} catch (Exception e) {
			log.debug("Erro ao iniciar AtualizaBaseService!");
			e.printStackTrace();
		}
	}

	public void adicionarAgendamento(Integer idSistema, String codServico, String descricao, String ExpressaoCron) {
		naAgendamentoService.adicionarAnexoifExists(idSistema, codServico, descricao, ExpressaoCron);
	}

	public void atualizaSistemaSessao() {
		NaSistema sistema = naSistemaService.verificaSistemaCadastrado(InfrastructureController.getNeaInfrastructureSistemaId(),
				InfrastructureController.getNeaInfrastructureSistemaNomeIdentify());

		InfrastructureController.setNaSistema(sistema);
	}
	
		
	private void atualizaBase(Integer idSIstema) {

		try {

			atualizaBasePrincipal.iniciarAtualizacao(true, idSIstema);
			log.info("Método atualizaBase  - Framework");
		} catch (Exception e) {
			log.error("Erro ao atualizar a base de dados do framework ");
			e.printStackTrace();
		}

		try {

			naSistemaService
					.verificaSistemaCadastrado(InfrastructureController.getNeaInfrastructureSistemaId(), InfrastructureController.getNeaInfrastructureSistemaNomeIdentify());

			atualizaBasePrincipal.iniciarAtualizacao(false, idSIstema);
			log.info("Método atualizaBase  - Aplicação Cliente");
		} catch (Exception e) {
			log.error("Erro ao atualizar a base de dados do Sistema ");
			e.printStackTrace();
		}

	}

}