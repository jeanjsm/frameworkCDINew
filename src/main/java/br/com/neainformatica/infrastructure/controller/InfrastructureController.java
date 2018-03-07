package br.com.neainformatica.infrastructure.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.neainformatica.infrastructure.entity.NaParametro;
import br.com.neainformatica.infrastructure.entity.NaParametroValor;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumSexo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.interfaces.EnumParametroInterface;
import br.com.neainformatica.infrastructure.services.AtualizaBaseService;
import br.com.neainformatica.infrastructure.services.NaParametroService;
import br.com.neainformatica.infrastructure.services.NaSistemaService;

/**
 * Os atributos desta classe serão substituidos pelas aplicaçõeos clientes
 * quando elas estiverem startando
 * 
 * @author elielcio.santos
 */

@Named
@ApplicationScoped
public class InfrastructureController implements Serializable {
	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(InfrastructureController.class);

	@Inject
	private NaParametroService parametroService;

	@Inject
	private NaSistemaService sistemaService;

	@Inject
	private Instance<AtualizaBaseService> atualizaBaseService;

	@Inject
	Instance<NaSistemaService> naSistemaService;

	private List<NaParametroValor> listaParametroValor;

	/**
	 * Nome do sistema que aparecerá na tela principal e nos relatórios
	 */
	private static String neaInfrastructureSistemaNome = "FrameworkCDI";

	/**
	 * Versão atual do sistema
	 */
	private static String neaInfrastructureSistemaVersao = "2015.01.01.01";

	/**
	 * Nome de identificação do Sistema, este será utilizado para definir os
	 * perfis e acessos no sistema Este é obrigatório ser informado para bases
	 * que possuem mais de um sistema rodando
	 */
	private static String neaInfrastructureSistemaNomeIdentify = "FRAMEWORK";

	/**
	 * Número de identificação do cliente no sistema de atendimento.
	 */
	private static Integer neaInfrastructureSistemaId = 1002;

	/**
	 * Sistema cliente que esta utilizando o framework.
	 */
	private static NaSistema naSistema = null;

	/**
	 * Tempo limite para expirar validacao do Token Definido em 3 Horas.
	 */
	public static Integer LIMITE_TEMPO_TOKEN = 3 * 60 * 60;

	/**
	 * Nome da versão do framework. Este deve ser alterado todas as vezes que o
	 * framework for alterado
	 */
	public static final String NEA_INFRASTRURA_FRAMEWORK_VERSAO = "2017.02.07.2";
	public static final Integer NEA_INFRASTRURA_FRAMEWORK_ID = 1002;

	/**
	 * Este deve ser utilizado para os casos onde a base de dados possui vários
	 * clientes, porém quero que determinado cliente ja venha selecionado
	 */
	private static Integer neaInfrastructureClienteDefault = null;

	// Enuns comuns entre os sistemas ------------
	private List<EnumSexo> todosSexo;
	private List<EnumSimNao> todosSimNao;
	private List<EnumSituacaoAcesso> statusDoSistema;

	@PostConstruct
	public void init() {
		todosSexo = Arrays.asList(EnumSexo.values());
		todosSimNao = Arrays.asList(EnumSimNao.values());
		statusDoSistema = Arrays.asList(EnumSituacaoAcesso.values());
		// listaParametro = parametroServico.findAll();
	}

	public static String getNeaInfrastruraFrameworkVersao() {
		return NEA_INFRASTRURA_FRAMEWORK_VERSAO;
	}

	public static String getNeaInfrastructureSistemaNome() {
		return neaInfrastructureSistemaNome;
	}

	public static void setNeaInfrastructureSistemaNome(String neaInfrastructureSistemaNome) {
		InfrastructureController.neaInfrastructureSistemaNome = neaInfrastructureSistemaNome;
	}

	public static String getNeaInfrastructureSistemaVersao() {
		return neaInfrastructureSistemaVersao;
	}

	public static void setNeaInfrastructureSistemaVersao(String neaInfrastructureSistemaVersao) {
		InfrastructureController.neaInfrastructureSistemaVersao = neaInfrastructureSistemaVersao;
	}

	public List<EnumSexo> getTodosSexo() {
		return todosSexo;
	}

	public List<EnumSimNao> getTodosSimNao() {
		return todosSimNao;
	}

	public static String getNeaInfrastructureSistemaNomeIdentify() {
		return neaInfrastructureSistemaNomeIdentify;
	}

	public static void setNeaInfrastructureSistemaNomeIdentify(String neaInfrastructureSistemaNomeIdentify) {
		InfrastructureController.neaInfrastructureSistemaNomeIdentify = neaInfrastructureSistemaNomeIdentify;
	}

	public static Integer getNeaInfrastructureSistemaId() {
		return neaInfrastructureSistemaId;
	}

	public static void setNeaInfrastructureSistemaId(Integer neaInfrastructureAtendimentoId) {
		InfrastructureController.neaInfrastructureSistemaId = neaInfrastructureAtendimentoId;
	}

	public static Integer getNeaInfrastructureClienteDefault() {
		return neaInfrastructureClienteDefault;
	}

	public static void setNeaInfrastructureClienteDefault(Integer neaInfrastructureClienteDefault) {
		InfrastructureController.neaInfrastructureClienteDefault = neaInfrastructureClienteDefault;
	}

	public List<EnumSituacaoAcesso> getStatusDoSistema() {
		return statusDoSistema;
	}

	public void setStatusDoSistema(List<EnumSituacaoAcesso> statusDoSistema) {
		this.statusDoSistema = statusDoSistema;
	}

	public NaSistema getNaSistema() {
		if (naSistema == null) {
			naSistema = sistemaService.buscarSistema(this.neaInfrastructureSistemaId);
		}
		/*
		 * if (naSistema == null) { AtualizaBaseService atualizaBase;
		 * atualizaBase = atualizaBaseService.get();
		 * atualizaBase.atualizaSistemaSessao(); }
		 */

		return naSistema;
	}

	public static void setNaSistema(NaSistema naSistema) {
		InfrastructureController.naSistema = naSistema;
	}

	/**
	 * Busca o parâmetro do sistema framework independe de sistema
	 * 
	 * @param enumParametro
	 * @return
	 */
	public NaParametroValor getParametroInfraestrutura(EnumParametroInterface enumParametro) {

		NaSistemaService sistemaService = naSistemaService.get();
		NaSistema sistema = sistemaService.buscarSistema(NEA_INFRASTRURA_FRAMEWORK_ID);
		return getParametro(enumParametro, "", false, sistema);
	}

	/**
	 * Busca o parâmetro do sistema framework independe de sistema
	 * 
	 * @param enumParametro
	 * @param forceDatabase
	 * @return
	 */
	public NaParametroValor getParametroInfraestrutura(EnumParametroInterface enumParametro, boolean forceDatabase) {

		NaSistemaService sistemaService = naSistemaService.get();
		NaSistema sistema = sistemaService.buscarSistema(NEA_INFRASTRURA_FRAMEWORK_ID);

		return getParametro(enumParametro, "", forceDatabase, sistema);
	}

	/**
	 * Busca o valor do parâmetro Caso o forceDataBase seja true força o sistema
	 * e ler do banco ao invez de pegar no cache
	 * 
	 * @param enumParametro
	 * @param forceDatabase
	 * @return
	 */
	public NaParametroValor getParametro(EnumParametroInterface enumParametro, boolean forceDatabase) {
		return getParametro(enumParametro, "", forceDatabase, null);
	}

	/**
	 * Busca o valor do parâmetro. Este utiliza cache
	 * 
	 * @param enumParametro
	 * @return
	 */
	public NaParametroValor getParametro(EnumParametroInterface enumParametro) {

		NaSistema sistema = sistemaService.buscarSistema(this.neaInfrastructureSistemaId);

		return getParametro(enumParametro, "", false, sistema);
	}

	/**
	 * Busca o valor do parâmetro. Este utiliza cache Deve ser utilizado nos
	 * casos onde o parâmetro não é fixo. (por Gestão, por Empresa...)
	 * 
	 * @param enumParametro
	 * @param chave
	 * @return
	 */
	public NaParametroValor getParametro(EnumParametroInterface enumParametro, String chave) {

		if (naSistema == null) {
			naSistema = sistemaService.buscarSistema(this.neaInfrastructureSistemaId);
		}

		return getParametro(enumParametro, chave, false, naSistema);
	}

	/**
	 * Busca o valor do parâmetro. Este utiliza cache Deve ser utilizado nos
	 * casos onde o parâmetro não é fixo. (por Gestão, por Empresa...)
	 * 
	 * @param enumParametro
	 * @param chave
	 * @return
	 */
	public NaParametroValor getParametro(EnumParametroInterface enumParametro, String chave, NaSistema sistema) {
		return getParametro(enumParametro, chave, false, sistema);
	}

	/**
	 * Busca o valor do parâmetro. Este utiliza cache Deve ser utilizado nos
	 * casos onde o parâmetro não é fixo. (por Gestão, por Empresa...) Caso o
	 * forceDataBase seja true força o sistema e ler do banco ao invez de pegar
	 * no cache
	 * 
	 * @param enumParametro
	 * @param chave
	 * @param forceDatabase
	 * @return
	 */
	public NaParametroValor getParametro(EnumParametroInterface enumParametro, String chave, boolean forceDatabase, NaSistema sistema) {

		NaParametroValor valor = null;

		log.debug("buscando parâmetro: " + enumParametro.getChave() + " | " + chave + " | force:" + forceDatabase);

		if (chave == null || chave.equals(""))
			chave = "FIXO";

		if (this.listaParametroValor == null)
			this.listaParametroValor = new ArrayList<NaParametroValor>();

		log.debug("buscando parâmetro em memoria");

		for (NaParametroValor pv : this.listaParametroValor) {
			if (pv.getParametro().getChave().equals(enumParametro.getChave())) {

				if (forceDatabase) {
					this.listaParametroValor.remove(pv);
					break;
				}

				if (enumParametro.getValorFixo().equals(EnumSimNao.SIM) || pv.getChave().equals(chave)) {
					valor = pv;
				}
			}
		}

		// não encontrou em memoria
		if (valor == null) {

			if (sistema == null)
				sistema = getNaSistema();

			if (chave.equals("FIXO") && enumParametro.getValorFixo().equals(EnumSimNao.NAO)) {
				log.error("O Parâmetro não possui valor fixo, verifica o campo NaParametro.valorFixo");
				return null;
			}

			NaParametro parametro = parametroService.getParametro(enumParametro, sistema);
			valor = parametroService.getValorByParametro(parametro, chave, sistema);

			if (valor == null) {
				log.debug("O parâmetro ainda não existe - Criando com valor DEFAULT");
				valor = parametroService.adicionarValorDefault(parametro, enumParametro.getValorPadrao(), chave);
			}

			if (valor != null)
				this.listaParametroValor.add(valor);
		}

		return valor;

	}

	public void resetarParametrosMemoria() {
		this.listaParametroValor = new ArrayList<NaParametroValor>();
	}

	public void removeParametroDaLista(NaParametro parametro) {
		boolean possui = false;
		NaParametroValor naParametroValor = new NaParametroValor();

		if (listaParametroValor != null) {
			for (NaParametroValor parametroValor : listaParametroValor) {
				if (parametroValor.getParametro().getChave().equals(parametro.getChave())) {
					possui = true;
					naParametroValor = parametroValor;
				}
			}
		}

		if (possui) {
			listaParametroValor.remove(naParametroValor);
		}
	}
	
	public void useEntityManager(EntityManager em) {
		parametroService.useEntityManager(em);
		sistemaService.useEntityManager(em);
		naSistemaService.get().useEntityManager(em);
	}
}
