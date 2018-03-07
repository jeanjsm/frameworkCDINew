package br.com.neainformatica.infrastructure.services;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaPermissaoRepository;
import br.com.neainformatica.infrastructure.entity.*;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoNaPermissao;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Dependent
public class NaPermissaoService extends GenericService<NaPermissao> implements NaServiceInterface, Serializable {
	public static final String PERFIL_TI = "T.I";
	public static final String PERFIL_ADMINISTRADOR = "ADMINISTRADOR";

	private static final long serialVersionUID = 1L;

	@Inject
	private NaPermissaoRepository dao;

	@Inject
	private NaPerfilService perfilService;

	@Inject
	private NaSistemaService naSistemaService;

	@Inject
	private NaAtorPermissaoService atorPermissaoService;

	@Inject
	private Instance<NaAtorService> naAtorService;
	
	@Inject
	private NaClienteService naClienteService;
		
	@Override
	public GenericRepository<NaPermissao> getRepository() {
		return this.dao;
	}

	public List<NaPermissao> buscarPermissoes(NaAtor ator, NaSistema sistema) {
		return dao.buscarPermissoes(ator, sistema);
	}

	public List<NaPermissao> buscarPermissoesSistema(NaSistema sistema, NaCliente naCliente) {
		return dao.buscarPermissoesSistema(sistema, naCliente);
	}

	/**
	 * Metodo utilizada para ter certeza que a permissão que esta sendo
	 * autorizada existe no cadastro de permissões caso não existe a permissão
	 * sera adicionada
	 * 
	 * @param permissao
	 * @throws Exception
	 */
	public void checaExistenciaPermissao(String permissao) throws RuntimeException {

		Boolean encontrou = false;

		// Verifica se a permissão ja existe

		for (NaPermissao p : findByParam(InfrastructureController.getNeaInfrastructureSistemaId(), "sistema.id")) {
			if (p.getDescricao().equalsIgnoreCase(permissao.trim()))
				encontrou = true;
			else if ((p.getChave() + "-ACESSAR").equalsIgnoreCase(permissao.trim()))
				encontrou = true;
			else if ((p.getChave() + "-INSERIR").equalsIgnoreCase(permissao.trim()))
				encontrou = true;
			else if ((p.getChave() + "-ALTERAR").equalsIgnoreCase(permissao.trim()))
				encontrou = true;
			else if ((p.getChave() + "-EXCLUIR").equalsIgnoreCase(permissao.trim()))
				encontrou = true;

			if (encontrou)
				break;

		}

		if (!encontrou)
			throw new RuntimeException("Mensagem ao programador: A permissão " + permissao + " não existe. adicione no Controller ConfiguracaoSistemaService");

	}

	public NaPermissao buscarPermissao(String chave, NaSistema sistema) {
		return dao.buscarPermissao(chave, sistema);
	}

	/**
	 * Busca as permissoes de acesso ja formatada para adicionar nas roles
	 * 
	 * @param naAtor
	 * @returno
	 */

	/**
	 * Adiciona uma nova permissão e a insere para todos os atores existentes
	 * 
	 * @param nomePermissao
	 * @param sistema
	 */
	@Transactional
	public void adicionarPermissao(EnumTipoNaPermissao tipo, String nomePermissao, String chave, NaSistema sistema, NaCliente naCliente) {
		chave = chave.toUpperCase();
		NaPermissao permissao = buscarPermissao(chave, sistema);

		if (permissao == null) {
			permissao = new NaPermissao(tipo, nomePermissao, chave, sistema);
			if(naCliente != null)
				permissao.setCliente(naCliente);
			try {
				permissao = noAudit().save(permissao);
				// naAtorPermissaoService.adicionarPermissaoAosAtoresExistentes(sistema,
				// permissao);
			} catch (NeaException e) {
				e.printStackTrace();
			}
		}
	}

	public void adicionarPermissao(EnumTipoNaPermissao tipo, String nomePermissao, String chave, NaSistema sistema) {
		adicionarPermissao(tipo, nomePermissao, chave, sistema, null);
	}
	/**
	 * Adiciona uma nova permissão e a insere para todos os atores existentes
	 * 
	 * @param nomePermissao
	 */
	@Transactional
	public void adicionarPermissao(EnumTipoNaPermissao tipo, String nomePermissao, String chave, Integer idSistema, Integer idNaCliente) {
		chave = chave.toUpperCase();
		NaSistema sistema = naSistemaService.buscarSistema(idSistema);
		NaCliente naCliente = null;
		if(idNaCliente != null) {
			naCliente = naClienteService.buscarCliente(idNaCliente);
		}
		InfrastructureController.setNaSistema(sistema);

		adicionarPermissao(tipo, nomePermissao, chave, sistema, naCliente);
	}
	

	public void adicionarPermissao(EnumTipoNaPermissao tipo, String nomePermissao, String chave, Integer idSistema) {
		adicionarPermissao(tipo, nomePermissao, chave, idSistema, null);
	}

	public void adicionarPermissao(EnumTipoNaPermissao tipo, String nomePermissao, String chave, Integer idSistema, boolean acessar, boolean alterar,
			boolean excluir, boolean inserir, EnumNivelUsuario nivelUsuario) {
		chave = chave.toUpperCase();
		NaSistema sistema = naSistemaService.buscarSistema(idSistema);
		InfrastructureController.setNaSistema(sistema);

		// a permissão ja existe
		NaPermissao permissao = buscarPermissao(chave, sistema);

		if (permissao == null) {
			try {
				permissao = new NaPermissao(tipo, nomePermissao, chave, sistema);
				noAudit().save(permissao);
			} catch (NeaException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Cria permissões padrões do framework
	 */
	public void criaPermissoesDefault(Integer idSistema) {

		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Agendamentos", "NA_AGENDAMENTO", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Auditoria", "NA_AUDITORIA", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Cliente", "NA_CLIENTE", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Cidade", "NA_CIDADE", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Estado", "NA_ESTADO", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Perfil", "NA_PERFIL", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Permissões", "NA_PERMISSOES", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Usuário", "NA_USUARIO", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Tipo Bairro", "NA_TIPO_BAIRRO", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Bairro", "NA_BAIRRO", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Logradouro", "NA_LOGRADOURO", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Tipo Logradouro", "NA_TIPO_LOGRADOURO", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "País", "NA_PAIS", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Nacionalidade", "NA_NACIONALIDADE", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Modo Administrador", "NA_MODO_ADMINISTRADOR", idSistema);
		adicionarPermissao(EnumTipoNaPermissao.CADASTRO, "Parametros", "NA_PARAMETROS", idSistema);
	}

	private void criaAtoresPermissoes(List<NaPermissao> permissoes, NaPerfil perfil, NaSistema sistema, NaCliente cliente) {

		List<NaPermissao> listaPermissoesParaCricao = new ArrayList<NaPermissao>();
		List<NaAtorPermissao> listaAtorPermissao = new ArrayList<NaAtorPermissao>();

		for (NaPermissao permissao : permissoes) {
			if (!existeAtorPermissao(permissao.getChave(), perfil, sistema)) {
				listaPermissoesParaCricao.add(permissao);
			}
		}

		if (listaPermissoesParaCricao.size() > 0) {

			for (NaPermissao permissao : listaPermissoesParaCricao) {

				NaAtorPermissao atorPermissao = new NaAtorPermissao();
				atorPermissao.setPermissao(permissao);
				atorPermissao.setAtor(perfil.getAtor());
				atorPermissao.setAcessar(EnumSimNao.SIM);
				atorPermissao.setAlterar(EnumSimNao.SIM);
				atorPermissao.setExcluir(EnumSimNao.SIM);
				atorPermissao.setInserir(EnumSimNao.SIM);
				atorPermissao.setCliente(cliente);
				listaAtorPermissao.add(atorPermissao);

				log.debug("Adicionando permissão -> Chave: "+ permissao.getChave() + " perfil: "+ perfil.getDescricao() +" sistema: " + sistema.getSistema());						
			}
			atorPermissaoService.noAudit().saveAll(listaAtorPermissao);
		}
	}

	public boolean existeAtorPermissao(String chavePermissao, NaPerfil perfil, NaSistema sistema) {

		List<NaAtorPermissao> permissoes = atorPermissaoService.buscaPermissao(chavePermissao, perfil.getAtor(), sistema);
		if (permissoes.size() > 0)
			return true;
		return false;
	}

	public void criaAtoresPermissoesDefaultUsuarioTI(NaCliente cliente, NaSistema sistema) {

		List<NaPermissao> listaPermissoesCandidatas = new ArrayList<NaPermissao>();

		List<String> listaChavePermissoes = new ArrayList<String>();
		listaChavePermissoes.add("NA_USUARIO");
		listaChavePermissoes.add("NA_PERFIL");
		listaChavePermissoes.add("NA_PERMISSOES");
		listaChavePermissoes.add("NA_MODO_ADMINISTRADOR");

		NaPermissao permissao;

		for (String chave : listaChavePermissoes) {
			permissao = dao.buscarPermissao(chave, sistema);
			if(permissao != null)
			listaPermissoesCandidatas.add(permissao);
			else
				log.warn("Permissão não encontrada.  (chave: "+chave+" sistema: "+sistema+")");
		}
		NaPerfil perfil = perfilService.existePerfil(PERFIL_TI, sistema, cliente);
		
		if (perfil != null)
			criaAtoresPermissoes(listaPermissoesCandidatas, perfil, sistema, cliente);
		else
			log.warn("Perfil não encontrado.  (chave: "+PERFIL_TI+" sistema: "+sistema+")");		

	}
	
	/**
	 * Adiciona a permissão informada para o perfil informado para os clientes
	 * passados como parâmetro
	 * 
	 * @param clientes
	 * @param idSistema
	 * @param nomePerfil
	 * @param permissoes
	 */
	public void criarPermissoesPerfilCliente(List<NaCliente> clientes, Integer idSistema, String nomePerfil, String... permissoes) {
		NaSistema sistema = naSistemaService.buscarSistema(idSistema);

		for (NaCliente cliente : clientes) {

			NaPerfil perfil = perfilService.existePerfil(nomePerfil, sistema, cliente);

			List<NaPermissao> permissoesPerfil = new ArrayList<>();

			for (String nomePermissao : permissoes) {
				NaPermissao permissao = dao.buscarPermissao(nomePermissao, sistema);
				if (permissao != null)
					permissoesPerfil.add(permissao);
				else
					log.warn("Permissão não encontrada no sistema - Permissão: " + nomePermissao + " sistema: " + sistema.getSistema());
			}

			criaAtoresPermissoes(permissoesPerfil, perfil, sistema, cliente);

		}
	}

	
	/**
	 * Adiciona a permissão informada para o perfil informado para todos os
	 * clientes da base de dados
	 * 
	 * @param idSistema
	 * @param nomePerfil
	 * @param permissoes
	 */
	public void criarPermissoesPerfil(Integer idSistema, String nomePerfil, String... permissoes) {

		log.debug(" Adicionando permissões para o perfil: " + nomePerfil + " permissões: " + permissoes);

		List<NaCliente> clientes = naClienteService.buscarListaCliente();
		NaSistema sistema = naSistemaService.buscarSistema(idSistema);

		for (NaCliente cliente : clientes) {

			NaPerfil perfil = perfilService.existePerfil(nomePerfil, sistema, cliente);

			List<NaPermissao> permissoesPerfil = new ArrayList<>();

			for (String nomePermissao : permissoes) {
				NaPermissao permissao = dao.buscarPermissao(nomePermissao, sistema);
				if (permissao != null)
					permissoesPerfil.add(permissao);
				else
					log.warn("Permissão não encontrada no sistema - Permissão: " + nomePermissao + " sistema: " + sistema.getSistema());
			}

			criaAtoresPermissoes(permissoesPerfil, perfil, sistema, cliente);

		}

	}

	public void criaAtoresPermissoesDefaultUsuarioAdministrador(NaCliente cliente, NaSistema sistema) {
		List<NaPermissao> listaPermissoes = dao.findByParam(sistema, "sistema");
		NaPerfil perfil = perfilService.existePerfil(PERFIL_ADMINISTRADOR, sistema, cliente);
		criaAtoresPermissoes(listaPermissoes, perfil, sistema, cliente);
	}

	public List<String> findListByUsuarioNaSistemaNaCliente(NaUsuario usuario, NaSistema naSistema, NaCliente naCliente){
		List<NaAtor> atores = naAtorService.get().buscarAtoresUsuario(usuario, naSistema);
		List<NaAtorPermissao> atorPermissoes = atorPermissaoService.buscaAtorPermissao(atores, naCliente);

		List<String> permissoes = atorPermissaoService.formatarPermissoesParaRole(atorPermissoes);
		return permissoes;
	}
}
