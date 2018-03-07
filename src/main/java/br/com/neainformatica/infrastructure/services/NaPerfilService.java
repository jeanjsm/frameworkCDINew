package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaPerfilRepository;
import br.com.neainformatica.infrastructure.dao.NaUsuarioPerfilRepository;
import br.com.neainformatica.infrastructure.entity.*;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaPerfilService extends GenericService<NaPerfil> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaPerfilRepository dao;

	@Inject
	private NaUsuarioPerfilRepository naUsuarioPerfilRepository;

	@Inject
	NaSistemaService naSistemaService;
	
	@Inject
	private NaUsuarioService naUsuarioService;
	
	@Inject
	NaClienteService naClienteService;
	
	@Override
	public GenericRepository<NaPerfil> getRepository() {
		return this.dao;
	}

	public NaPerfil existePerfil(String nomePerfil, NaSistema sistema, NaCliente cliente) {
		return dao.existePerfil(nomePerfil, sistema, cliente);
	}

	/**
     * @deprecated
     * Método não filtra por cliente, não funcionando em sistemas multiclientes, utilizar o 
     * método buscaPerfisUsuario(Integer usuarioId, Integer sistemaId, Integer clienteId)
     */
	public List<NaPerfil> buscaPerfis(NaUsuario usuario, NaSistema sistema) {
		return dao.buscaPerfis(usuario, sistema);
	}
	
	public List<NaPerfil> buscaPerfisUsuario(Integer usuarioId, Integer sistemaId, Integer clienteId) {
		return dao.buscaPerfisUsuario(usuarioId, sistemaId, clienteId);
	}

	public List<NaPerfil> buscaPerfisDisponiveis(NaUsuario usuario, NaSistema sistema, NaCliente cliente) {
		return dao.buscaPerfisDisponiveis(usuario, sistema, cliente);
	}
	
	public List<NaPerfil> buscaPerfisPorSistemaCliente(Integer sistemaId, Integer clienteId) {
		return dao.buscaPerfisPorSistemaCliente(sistemaId, clienteId);
	}

	public List<NaUsuarioPerfil> buscaUsuarioPerfilPorPerfil(NaPerfil naPerfil) {
		return naUsuarioPerfilRepository.buscaUsuarioPerfilPorPerfil(naPerfil);
	}
	
	/**
	 * Busca todos os clientes da base e insere o perfil para o sistema atual
	 */
	public void adicionarPerfilDefaultAllClientes(Integer idSistema, String nomePerfil){
		
		List<NaCliente> clientes = naClienteService.buscarListaCliente();
		NaSistema sistema = naSistemaService.buscarSistema(idSistema);
		
		for (NaCliente cliente : clientes) {
			adicionaPerfilDefault(cliente, sistema, nomePerfil);
		}
		
		
	}

	
	public void adicionaPerfilDefault(NaCliente cliente, NaSistema sistema, String nomePerfil) {

		NaPerfil perfil = existePerfil(nomePerfil, sistema, cliente);

		if (perfil == null) {

			log.debug("O perfil " + nomePerfil + " nao existe, criando....");
			perfil = new NaPerfil();
			perfil.setDescricao(nomePerfil);
			perfil.setSistema(sistema);
			perfil.setPadrao(EnumSimNao.SIM);
			perfil.setCliente(cliente);

			try {
				noAudit().save(perfil);
			} catch (NeaException e) {
				e.printStackTrace();
			}
			log.debug("Perfil " + nomePerfil + " criado com sucesso");
		}

	}

	public void adicionaPerfil(NaCliente cliente, NaSistema sistema, String nomePerfil) {

		NaPerfil perfil = existePerfil(nomePerfil, sistema, cliente);

		if (perfil == null) {

			log.debug("O perfil " + nomePerfil + " nao existe, criando....");
			perfil = new NaPerfil();
			perfil.setDescricao(nomePerfil);
			perfil.setSistema(sistema);
			perfil.setPadrao(EnumSimNao.NAO);
			perfil.setCliente(cliente);

			try {
				noAudit().save(perfil);
			} catch (NeaException e) {
				e.printStackTrace();
			}
			log.debug("Perfil " + nomePerfil + " criado com sucesso");
		}

	}

	public NaPerfil buscarPerfilPorAtorCliente(NaAtor naAtor, NaCliente naCliente){
		return dao.buscarPerfilPorAtorCliente(naAtor, naCliente);
	}
	
	@Override
	protected void beforeDelete(NaPerfil perfil) throws NeaException {
		//Remove todos os NaUsuarioperfil do perfil em questão.
		List<NaUsuarioPerfil> listaUsuarioPerfil = buscaUsuarioPerfilPorPerfil(perfil);
		for(NaUsuarioPerfil usuarioPerfil : listaUsuarioPerfil) {
			naUsuarioService.delete(usuarioPerfil);
		}
		super.beforeDelete(perfil);
	}
}
