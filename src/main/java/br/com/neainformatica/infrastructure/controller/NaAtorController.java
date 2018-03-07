package br.com.neainformatica.infrastructure.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.neainformatica.infrastructure.entity.*;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoAtor;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.services.*;

@Named
@ConversationScoped
public class NaAtorController extends GenericController<NaAtor> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NaAtorService service;

	@Inject
	private NaAtorPermissaoService atorPermissaoService;

	@Inject
	private NaPerfilService perfilService;

	@Inject
	private NaUsuarioSistemaService usuarioSistemaService;

	@Inject
	private InfrastructureController infrastructureController;

	// parametros utilizados na tela de permissões
	private EnumTipoAtor tipoAtorSelecionado;
	private NaAtor atorSelecionado;

	private List<NaAtor> listAtores;
	private List<NaAtorPermissao> listaAtorPermissao;

	private boolean perfilPadrao;

	@PostConstruct
	public void init() {
		// service.setSistema(InfrastructureController.getNaSistema());
		this.tipoAtorSelecionado = EnumTipoAtor.PERFIL;
		perfilPadrao = false;
		createDynamicColumns();
	}

	public void createDynamicColumns() {
		// monta a lista dinamica de colunas para exibicao no templateList.xhtml
		// define quais coluna aparecem na grid de listagem
		clearColumns().addColumn(new ColumnModel("Código", "id")).addColumn(new ColumnModel("Nome", "nome"));
	}

	@Override
	public void setService(GenericService<NaAtor> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaAtor> getService() {
		return this.service;
	}

	public List<EnumTipoAtor> tiposAtores() {
		return Arrays.asList(EnumTipoAtor.values());
	}

	public String getLabelInputSearchAtor() {
		if (this.tipoAtorSelecionado.equals(EnumTipoAtor.USUARIO))
			return "Selecione o usuário";
		else
			return "Selecione o perfil";
	}

	/**
	 * Atualiza as permissões do ator e popula a lista de AtoresPermissões para
	 * ser exibida na tela
	 */
	public void buscarPermissoes() {

		perfilPadrao = false;
		
		if (this.atorSelecionado == null || this.atorSelecionado.getId() == 0) {
			if (this.tipoAtorSelecionado == EnumTipoAtor.PERFIL)
				messages.addErrorMessage("Selecione um Perfil para continuar");
			else
				messages.addErrorMessage("Selecione um Usuário para continuar");

			return;
		}

		try {
			service.atualizarPermissoes(atorSelecionado, infrastructureController.getNaSistema(), naSessionController.getNaCliente());

			NaPerfil perfil = perfilService.buscarPerfilPorAtorCliente(atorSelecionado, naSessionController.getNaCliente());
			if (perfil != null) {
				perfilPadrao = perfil.getPadrao().equals(EnumSimNao.SIM) ? true : false;
			}

//			this.listaAtorPermissao = atorPermissaoService.buscaAtorPermissao(atorSelecionado, naSessionController.getNaCliente());

			NaUsuario naUsuario = naSessionController.getNaUsuario();
			NaUsuarioSistema naUsuarioSistema = usuarioSistemaService.obterUsuarioSistema(InfrastructureController.getNeaInfrastructureSistemaId(), naUsuario.getId(), naSessionController.getNaCliente());

			this.listaAtorPermissao = atorPermissaoService.buscarListaPorAtorClienteNivelUsuario(atorSelecionado.getId(), naSessionController.getNaCliente().getId(), naUsuarioSistema.getNivelUsuario());

		} catch (NeaException e) {
			messages.addErrorMessage("Erro ao buscar permissões.");
			e.printStackTrace();
		}
	}

	public List<NaAtor> atoresDisponiveis() {
		if (this.tipoAtorSelecionado != null)
			service.buscarAtores(tipoAtorSelecionado);
		return null;
	}

	public void selecionarTodosHorizontal(NaAtorPermissao atorPermissao) {
		atorPermissao.setSelecionarTodos(true);
	}

	public void desmarcarTodosHorizontal(NaAtorPermissao atorPermissao) {
		atorPermissao.setSelecionarTodos(false);
	}

	public void marcarTodosColuna(String tipo) {
		selecionarTodos(tipo, true);
	}

	public void desmarcarTodosColuna(String tipo) {
		selecionarTodos(tipo, false);
	}

	public boolean mostraSelecionarTodosColuna(String tipo) {

		for (NaAtorPermissao atorPermissao : getListaAtorPermissao()) {
			if (tipo.equals("ACESSAR")) {
				if (atorPermissao.getAcessar() == EnumSimNao.NAO) {
					return false;
				}
			} else if (tipo.equals("INSERIR")) {
				if (atorPermissao.getInserir() == EnumSimNao.NAO) {
					return false;
				}
			} else if (tipo.equals("ALTERAR")) {
				if (atorPermissao.getAlterar() == EnumSimNao.NAO) {
					return false;
				}
			} else if (tipo.equals("EXCLUIR")) {
				if (atorPermissao.getExcluir() == EnumSimNao.NAO) {
					return false;
				}
			} else if (tipo.equals("TODOS")) {
				if (atorPermissao.getAcessar() == EnumSimNao.NAO || atorPermissao.getInserir() == EnumSimNao.NAO || atorPermissao.getAlterar() == EnumSimNao.NAO
						|| atorPermissao.getExcluir() == EnumSimNao.NAO) {
					return false;
				}
			}
		}
		return true;
	}

	private void selecionarTodos(String tipo, boolean seleciona) {

		for (NaAtorPermissao atorPermissao : getListaAtorPermissao()) {

			if (tipo.equals("ACESSAR")) {
				atorPermissao.setAcessar(seleciona ? EnumSimNao.SIM : EnumSimNao.NAO);
			} else if (tipo.equals("INSERIR")) {
				atorPermissao.setInserir(seleciona ? EnumSimNao.SIM : EnumSimNao.NAO);
			} else if (tipo.equals("ALTERAR")) {
				atorPermissao.setAlterar(seleciona ? EnumSimNao.SIM : EnumSimNao.NAO);
			} else if (tipo.equals("EXCLUIR")) {
				atorPermissao.setExcluir(seleciona ? EnumSimNao.SIM : EnumSimNao.NAO);
			} else if (tipo.equals("TODOS")) {
				atorPermissao.setAcessar(seleciona ? EnumSimNao.SIM : EnumSimNao.NAO);
				atorPermissao.setInserir(seleciona ? EnumSimNao.SIM : EnumSimNao.NAO);
				atorPermissao.setAlterar(seleciona ? EnumSimNao.SIM : EnumSimNao.NAO);
				atorPermissao.setExcluir(seleciona ? EnumSimNao.SIM : EnumSimNao.NAO);
			}

		}

	}

	public void alterarPermissaoAcessar(NaAtorPermissao atorPermissao) {
		atorPermissao.setAcessar(atorPermissao.getAcessar() == EnumSimNao.SIM ? EnumSimNao.NAO : EnumSimNao.SIM);
	}

	public void alterarPermissaoInserir(NaAtorPermissao atorPermissao) {
		atorPermissao.setInserir(atorPermissao.getInserir() == EnumSimNao.SIM ? EnumSimNao.NAO : EnumSimNao.SIM);
	}

	public void alterarPermissaoAlterar(NaAtorPermissao atorPermissao) {
		atorPermissao.setAlterar(atorPermissao.getAlterar() == EnumSimNao.SIM ? EnumSimNao.NAO : EnumSimNao.SIM);
	}

	public void alterarPermissaoExcluir(NaAtorPermissao atorPermissao) {
		atorPermissao.setExcluir(atorPermissao.getExcluir() == EnumSimNao.SIM ? EnumSimNao.NAO : EnumSimNao.SIM);
	}

	public void gravarAlteracoes() {

		try {

			for (NaAtorPermissao atorPermissao : listaAtorPermissao) {
				atorPermissaoService.save(atorPermissao);
			}
			messages.addInfoMessage("Permissões gravadas com sucesso!");

		} catch (NeaException e) {
			messages.addErrorMessage("Erro ao gravar dados.");
			e.printStackTrace();
		}
	}

	public List<NaAtorPermissao> getListaAtorPermissao() {

		if (this.listaAtorPermissao == null) {
			this.listaAtorPermissao = new ArrayList<>();
		}

		return listaAtorPermissao;
	}

	public EnumTipoAtor getTipoAtorSelecionado() {
		return tipoAtorSelecionado;
	}

	public void setTipoAtorSelecionado(EnumTipoAtor tipoAtorSelecionado) {
		this.tipoAtorSelecionado = tipoAtorSelecionado;
	}

	public NaAtor getAtorSelecionado() {

		if (atorSelecionado == null)
			this.atorSelecionado = new NaAtor();

		return atorSelecionado;
	}

	public void setAtorSelecionado(NaAtor atorSelecionado) {
		this.atorSelecionado = atorSelecionado;
	}

	public List<NaAtor> getListAtores() {
		
		NaSistema sistema = infrastructureController.getNaSistema();
		NaCliente cliente = naSessionController.getNaCliente();

		if (this.tipoAtorSelecionado.equals(EnumTipoAtor.USUARIO))
			this.listAtores = service.buscarAtoresUsuarioBySistema(sistema, cliente);
		else
			this.listAtores = service.buscarAtoresPerfilBySistema(sistema, cliente);
		
		return listAtores;
	}

	public boolean isPerfilPadrao() {
		return perfilPadrao;
	}
}
