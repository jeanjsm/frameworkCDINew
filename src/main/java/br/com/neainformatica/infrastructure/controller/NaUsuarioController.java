package br.com.neainformatica.infrastructure.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketlink.annotations.PicketLink;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DualListModel;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.neainformatica.infrastructure.entity.NaAlteracaoSenha;
import br.com.neainformatica.infrastructure.entity.NaAtorPermissao;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaPerfil;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioPerfil;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.OrFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaAlteracaoSenhaService;
import br.com.neainformatica.infrastructure.services.NaPerfilService;
import br.com.neainformatica.infrastructure.services.NaUsuarioService;
import br.com.neainformatica.infrastructure.services.NaUsuarioSistemaService;
import br.com.neainformatica.infrastructure.tools.NeaFormatter;
import br.com.neainformatica.infrastructure.tools.Tools;

@Named
@ConversationScoped
public class NaUsuarioController extends GenericController<NaUsuario> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaUsuarioService service;

	@Inject
	@PicketLink
	@Named
	private Autenticador autenticador;

	@Inject
	private NaPerfilService naPerfilService;

	@Inject
	private NaUsuarioService usuarioService;

	@Inject
	private NaUsuarioSistemaService usuarioSistemaService;

	@Inject
	private NaAlteracaoSenhaService naAlteracaoSenhaService;

	@Inject
	private InfrastructureController infrastructureController;

	private String senhaAtual;
	private String novaSenha;
	private String novaSenhaConfirmacao;
	
	private NaSistema sistemaSelecionado;
	private EnumNivelUsuario nivelSelecionado;
	private NaUsuario usuario;

	/**
	 * Eduardo Leite Ranzzani Rotinas para alteração de senha do usuario atraves de
	 * um link por email
	 *
	 */
	private String cpfInformado;
	private String captchaDigitado;
	private Boolean ocorreuInconsistencia = false;
	private NaAlteracaoSenha naAlteracaoSenha = null;
	private Boolean senhaAlterada = false;
	private String hashRecebido;
	

	private List<NaUsuarioSistema> listaSistemaDoUsuario;
	private List<NaUsuarioSistema> listaRemoverSistemaDoUsuario;
	private List<EnumNivelUsuario> listaNivelSistemaUsuario;
	private List<NaUsuario> listaUsuarios;

	private List<NaAtorPermissao> listaAtorPermissao;
	
	@Override
	public void initConversation() {
		super.initConversation();
	}

	public String refreshPageLogin() {
		return "login";
	}



	@Override
	protected List<IFilter> filtersBeforeSearch(List<IFilter> filters) {
		EnumNivelUsuario nivelUsuario = naSessionController.getNaUsuarioSistema().getNivelUsuario();
		if (EnumNivelUsuario.SUPORTE.getId() > nivelUsuario.getId()) {
			// nivel usuario menor que nivel suporte
			List<IFilter> filterOR = new ArrayList<>();
			for (EnumNivelUsuario nivel : Arrays.asList(EnumNivelUsuario.values())) {

				// todos os niveis menor que nivel suporte
				if (EnumNivelUsuario.SUPORTE.getId() > nivel.getId())
					filterOR.add(new EqualFilter("us.nivelUsuario", nivel, ""));

			}
			filters.add(new OrFilter(filterOR, ""));
		}
		return super.filtersBeforeSearch(filters);
	}

	
	@Override
	public String showFormDetail() {

		this.perfisSelecionados = naPerfilService.buscaPerfis(this.objeto, infrastructureController.getNaSistema());
		return super.showFormDetail();
	}


	

	

	public List<NaUsuario> getListaUsuarioSistema(String filtro) {
		List<NaUsuario> lsResultado = new ArrayList<NaUsuario>();

		if (this.listaUsuarios == null) {
			this.listaUsuarios = usuarioSistemaService.buscarUsuariosSistema(naSessionController.getNaSistema().getId());
		}

		if (filtro == null)
			filtro = "";

		for (NaUsuario usuario : this.listaUsuarios) {
			if (usuario.getNome().toLowerCase().contains(filtro.toLowerCase()))
				lsResultado.add(usuario);
		}

		return lsResultado;
	}

	
	

	@Override
	public void setService(GenericService<NaUsuario> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaUsuario> getService() {
		return this.service;
	}

	@PostConstruct
	public void createDynamicColumns() {
		getColumns().clear();
		getColumns().add(new ColumnModel("Usuário", "nome"));
	}

	@Override
	public String create() {
		this.perfisSelecionados = new ArrayList<NaPerfil>();
		return super.create();
	}

	public String buscarUsuario() throws IOException {
		RequestContext context = RequestContext.getCurrentInstance();

		if (!NeaFormatter.validaCpfCnpj(this.cpfInformado)) {
			context.addCallbackParam("permitirAlteracao", false);
			return "";
		}

		this.usuario = service.buscaUsuarioCpf(this.cpfInformado);

		if (this.usuario == null) {
			create();
			this.objeto.setCpfCnpj(this.cpfInformado);
			return "naUsuarioForm.xhtml?faces-redirect=true";
		} else {

			NaUsuarioSistema usuarioSistema = usuarioSistemaService.buscaUsuarioSistema(this.usuario, naSessionController.getNaSistema(),
					naSessionController.getNaCliente());

			if (usuarioSistema == null) {
				context.execute("PF('wvIncludeConfirmDialog').show()");
				context.execute("PF('confirminclude').focus()");
			} else {
				context.execute("PF('dlgCadastroExsistente').show()");
			}

			context.addCallbackParam("permitirAlteracao", true);
		}

		return "";
	}

	public void limpaCampos() {
		setCpfInformado("");
	}

	public void novoUsuarioSistema() {
		NaUsuarioSistema naUsuarioSistema = new NaUsuarioSistema();

		naUsuarioSistema.setNaCliente(naSessionController.getNaCliente());
		naUsuarioSistema.setUsuario(this.usuario);
		naUsuarioSistema.setSistema(naSessionController.getNaSistema());
		naUsuarioSistema.setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);
		naUsuarioSistema.setNivelUsuario(EnumNivelUsuario.USUARIO);
		naUsuarioSistema.setDataAlteracao(new Date());
		naUsuarioSistema.setOrigemSincronismo(EnumOrigemSincronismo.CLIENTE);
		usuarioSistemaService.save(naUsuarioSistema);
		messages.addInfoMessage("Usuário foi adicionado...");
		atualizaDadosDaGridPrincipal();
	}

	public void atualizaDadosDaGridPrincipal() {

		List<IFilter> filters = new ArrayList<IFilter>();

		filters.add(new EqualFilter("cpfCnpj", this.cpfInformado, ""));

		dinamicSearch(filters);
	}

//	@Override
//	public String update() {
//
//		try {
//			if (service.validarEdicaoUsuario(this.objeto, naSessionController.getNaUsuarioSistema(), naSessionController.getNaCliente())) {
//
//				this.perfisSelecionados = naPerfilService.buscaPerfisDisponiveis(this.objeto, infrastructureController.getNaSistema(),
//						naSessionController.getNaCliente());
//				
//				return super.update();
//			}
//
//			return "";
//
//		} catch (Exception e) {
//			messages.addErrorMessage(e.getMessage());
//			return "";
//		}
//
//	}

	public String salvarNovoUsuario() {

		if (!NeaFormatter.validaCpfCnpj(this.objeto.getCpfCnpj())) {
			return "";
		}

		salvar();

		this.objeto.setSenha(Tools.geraMD5(this.objeto.getIdUsuarioSenha() + this.novaSenha));

		salvar();

		return null;

	}

	@Override
	public void afterCreate() {
		this.novaSenha = Tools.geraSenhaAleatoria(6).toUpperCase();
	}

	@Override
	public String salvar() {

		if (!NeaFormatter.validaCpfCnpj(this.objeto.getCpfCnpj())) {
			return "";
		}

		NaUsuario usuario = service.buscaUsuarioCpf(this.objeto.getCpfCnpj());

		if (this.objeto.getId() == null) {
			if (usuario != null) {
				messages.addErrorMessage("CPF já possui cadastro na base de dados!");
				return "";
			}
		} else {
			if (usuario != null) {
				if (!usuario.getLogin().equals(this.objeto.getLogin())) {
					messages.addErrorMessage("CPF já possui cadastro na base de dados!");
					return "";
				}
			}
		}

		this.objeto.setLogin(this.objeto.getCpfCnpj());
		String retorno = super.salvar();

		if (retorno != null) {
			gravaListaPerfis();
		} else {
			this.objeto.setLogin("");
		}

		return retorno;
	}

	@Override
	protected void beforeUpdate() {
		this.novaSenha = null;
	}

	public String fecharModalSenha() {
		this.novaSenha = null;
		return "naUsuarioList.xhtml?faces-redirect=true";
	}

	/* Controle do picklist de Perfil */
	private DualListModel<NaPerfil> perfis = new DualListModel<>(new ArrayList<NaPerfil>(), new ArrayList<NaPerfil>());

	List<NaPerfil> perfisDisponiveis = new ArrayList<NaPerfil>();
	List<NaPerfil> perfisSelecionados = new ArrayList<NaPerfil>();

	public List<NaPerfil> getPerfisDisponiveis() {

		NaCliente cliente = naSessionController.getNaCliente();

		this.perfisDisponiveis = naPerfilService.buscaPerfisDisponiveis(this.objeto, infrastructureController.getNaSistema(), cliente);

		return perfisDisponiveis;
	}

	public List<NaPerfil> getPerfisSelecionados() {
		return perfisSelecionados;
	}

	public void setPerfisSelecionados(List<NaPerfil> perfisSelecionados) {
		this.perfisSelecionados = perfisSelecionados;
	}

	public void setPerfisDisponiveis(List<NaPerfil> perfisDisponiveis) {
		this.perfisDisponiveis = perfisDisponiveis;
	}

	public DualListModel<NaPerfil> getPerfis() {

		perfis = new DualListModel<>(getPerfisDisponiveis(), perfisSelecionados);
		return perfis;
	}

	public void setPerfis(DualListModel<NaPerfil> perfis) {
		this.perfis = perfis;
	}

	private void gravaListaPerfis() {

		this.perfisSelecionados = this.perfis.getTarget();

		try {
			/* incluindo novos perfis */
			List<NaPerfil> perfisJaExistentes = naPerfilService.buscaPerfis(objeto, infrastructureController.getNaSistema());
			for (NaPerfil perfil : this.perfisSelecionados) {
				if (!perfisJaExistentes.contains(perfil)) {
					NaUsuarioPerfil usuarioPerfil = new NaUsuarioPerfil();
					usuarioPerfil.setPerfil(perfil);
					usuarioPerfil.setUsuario(this.objeto);
					usuarioService.save(usuarioPerfil);
				}
			}

			/* removendo perfis excluidos */
			List<NaUsuarioPerfil> usuarioPerfilJaExistentes = usuarioService.buscaUsuarioPerfil(objeto);
			for (NaUsuarioPerfil up : usuarioPerfilJaExistentes) {
				if (!perfisSelecionados.contains(up.getPerfil())) {

					NaUsuarioPerfil usuarioPerfil = usuarioService.getUsuarioPerfilById(up.getId());
					usuarioService.delete(usuarioPerfil);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			messages.addErrorMessage("Falha ao gravar perfis: " + e.getMessage());
		}
	}

	public void vincularSistemas() {

	}

	public String getSenhaAtual() {
		return senhaAtual;
	}

	public void setSenhaAtual(String senhaAtual) {
		this.senhaAtual = senhaAtual;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha.trim();
	}

	public String getNovaSenhaConfirmacao() {
		return novaSenhaConfirmacao;
	}

	public void setNovaSenhaConfirmacao(String novaSenhaConfirmacao) {
		this.novaSenhaConfirmacao = novaSenhaConfirmacao;
	}

	public void alterarSenhaDoUsuario(ActionEvent actionEvent) {

		try {

			NaUsuario usuario = naSessionController.getNaUsuarioSistema().getUsuario();
			service.alterarSenha(usuario, this.senhaAtual, this.novaSenha, this.novaSenhaConfirmacao);

			messages.addInfoMessage("Senha alterada com sucesso!");

			this.senhaAtual = "";
			this.novaSenha = "";
			this.novaSenhaConfirmacao = "";

		} catch (Exception e) {
			messages.addErrorMessage(e.getMessage());
		}
	}

	@Override
	public void findAll() {
		super.setLazyList(new LazyDataModel<NaUsuario>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setRowIndex(int rowIndex) {

				if (rowIndex == -1 || getPageSize() == 0) {
					super.setRowIndex(-1);
				} else {
					super.setRowIndex(rowIndex % getPageSize());
				}
			}

			@Override
			public Object getRowKey(NaUsuario object) {
				return object;
			}

			@Override
			public NaUsuario getRowData(String rowKey) {
				@SuppressWarnings({ "unchecked", "unused" })
				List<NaUsuario> listaNaUsuarios = (List<NaUsuario>) getWrappedData();

				for (NaUsuario naUsuario : listaNaUsuarios) {
					if (naUsuario.getId().toString().equals(rowKey))
						return naUsuario;
				}

				return null;
			}

			@Override
			public List<NaUsuario> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

				int dataSize = 0;

				Collection<NaUsuario> data = new ArrayList<>();

				data = (List<NaUsuario>) service.getNaUsuarios(null, naSessionController.getNaSistema(), naSessionController.getNaCliente(), first, pageSize);

				dataSize = Integer.valueOf(service.getNaUsuariosCount(null, naSessionController.getNaSistema(), naSessionController.getNaCliente()));

				this.setRowCount(dataSize);

				return (List<NaUsuario>) data;
			}

		});
	}

	@Override
	public void dinamicSearch(final List<IFilter> listFilters) {
		setLazyList(new LazyDataModel<NaUsuario>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setRowIndex(int rowIndex) {
				if (rowIndex == -1 || getPageSize() == 0) {
					super.setRowIndex(-1);
				} else {
					super.setRowIndex(rowIndex % getPageSize());
				}
			}

			@Override
			public Object getRowKey(NaUsuario object) {
				return object;
			}

			@Override
			public NaUsuario getRowData(String rowKey) {
				@SuppressWarnings({ "unchecked", "unused" })
				List<NaUsuario> listaNaUsuarios = (List<NaUsuario>) getWrappedData();

				for (NaUsuario naUsuario : listaNaUsuarios) {
					if (naUsuario.getId().toString().equals(rowKey))
						return naUsuario;
				}

				return null;
			}

			@Override
			public List<NaUsuario> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

				int dataSize = 0;

				Collection<NaUsuario> data = new ArrayList<>();

				data = (List<NaUsuario>) service.getNaUsuarios(listFilters, naSessionController.getNaSistema(), naSessionController.getNaCliente(), first,
						pageSize);

				// dataSize = data.size();
				dataSize = Integer.valueOf(service.getNaUsuariosCount(listFilters, naSessionController.getNaSistema(), naSessionController.getNaCliente()));

				this.setRowCount(dataSize);

				return (List<NaUsuario>) data;

			}

		});
	}

	public void resetarSenha() {
		update();
		this.novaSenha = service.resetaSenha(objeto);
		if (!this.novaSenha.equals("")) {
			RequestContext context = RequestContext.getCurrentInstance();
			context.execute("PF('dlgNovaSenha').show();");
		} else {
			messages.addErrorMessage("Um erro ocorreu ao resetar a senha!");
		}
	}

	public String getCpfInformado() {
		return cpfInformado;
	}

	public void setCpfInformado(String cpfInformado) {
		this.cpfInformado = cpfInformado;
	}

	public String getCaptchaDigitado() {
		return captchaDigitado;
	}

	public void setCaptchaDigitado(String captchaDigitado) {
		this.captchaDigitado = captchaDigitado;
	}

	public Boolean getOcorreuInconsistencia() {
		return ocorreuInconsistencia;
	}

	public void setOcorreuInconsistencia(Boolean ocorreuInconsistencia) {
		this.ocorreuInconsistencia = ocorreuInconsistencia;
	}

	public String getHashRecebido() {
		return hashRecebido;
	}

	public void setHashRecebido(String hashRecebido) {
		this.hashRecebido = hashRecebido;
	}

	public Boolean getSenhaAlterada() {
		return senhaAlterada;
	}

	public void setSenhaAlterada(Boolean senhaAlterada) {
		this.senhaAlterada = senhaAlterada;
	}

	public List<NaUsuarioSistema> getListaSistemaDoUsuario() {
		return listaSistemaDoUsuario;
	}

	public void setListaSistemaDoUsuario(List<NaUsuarioSistema> listaSistemaDoUsuario) {
		this.listaSistemaDoUsuario = listaSistemaDoUsuario;
	}

	public List<NaSistema> obterSistemas(String nomeSistema) {

		NaUsuario usuario = naSessionController.getNaUsuario();
		NaCliente cliente = naSessionController.getNaCliente();

		return service.obeterSistemas(usuario, cliente);
	}

	public NaSistema getSistemaSelecionado() {
		return sistemaSelecionado;
	}

	public void setSistemaSelecionado(NaSistema sistemaSelecionado) {
		this.sistemaSelecionado = sistemaSelecionado;
	}

	public String navegaParaUsuarioSistema() {

		this.listaSistemaDoUsuario = service.buscarSistemasDoUsuario(this.objeto, naSessionController.getNaCliente());
		this.listaRemoverSistemaDoUsuario = new ArrayList<NaUsuarioSistema>();
		super.showFormDetail();
		initConversation();
		return "naUsuarioSistemaForm";

	}

	public void removerSistemaUsuario(NaUsuarioSistema nau) {
		if (listaRemoverSistemaDoUsuario == null) {
			listaRemoverSistemaDoUsuario = new ArrayList<NaUsuarioSistema>();
		}
		if (nau.getId() != null) {
			listaRemoverSistemaDoUsuario.add(nau);
		}
		if (listaSistemaDoUsuario.size() > 0) {
			listaSistemaDoUsuario.remove(nau);
		}
	}

	public String salvarSistemaUsuario() {
		if (listaRemoverSistemaDoUsuario != null && listaRemoverSistemaDoUsuario.size() > 0) {
			service.removerSistemaDoUsuario(listaRemoverSistemaDoUsuario);
		}
		if (listaSistemaDoUsuario != null && listaSistemaDoUsuario.size() > 0) {
			service.adicionarSistemasUsuario(listaSistemaDoUsuario);
		}

		messages.addInfoMessage("Registro salvo com sucesso... ");
		return "naUsuarioList";
	}

	public void adicionarSistemaUsuario() {

		if (sistemaSelecionado == null) {
			messages.addErrorMessage("O sistema deve ser selecionado!");
			return;
		}
		if (nivelSelecionado == null) {
			messages.addErrorMessage("O nível de acesso deve ser selecionado!");
			return;
		}

		try {
			if (!service.permiteIncluirAcesso(sistemaSelecionado, nivelSelecionado, naSessionController.getNaUsuarioSistema(),
					naSessionController.getNaCliente())) {
				messages.addErrorMessage("Seu usuário não tem permissão de dar este nível de acesso para o sistema selecionado");
				return;
			}
		} catch (Exception e) {
			messages.addErrorMessage(e.getMessage());
		}

		if (listaSistemaDoUsuario == null)
			listaSistemaDoUsuario = new ArrayList<NaUsuarioSistema>();

		boolean adiciona = true;
		for (NaUsuarioSistema us : listaSistemaDoUsuario) {
			if (us.getSistema().equals(sistemaSelecionado)) {
				adiciona = false;
				messages.addErrorMessage(this.objeto.getNome() + " já possui acesso no sistema de " + sistemaSelecionado.getSistema());
				break;
			}
		}

		if (adiciona) {

			NaUsuarioSistema nau = usuarioSistemaService.obterUsuarioSistema(sistemaSelecionado.getId(), this.objeto.getId(),
					naSessionController.getNaCliente());

			if (nau == null)
				nau = new NaUsuarioSistema();

			nau.setAtivo(EnumSimNao.SIM);
			nau.setNivelUsuario(nivelSelecionado);

			if (nau.getId() == null) {
				nau.setSistema(sistemaSelecionado);
				nau.setUsuario(this.objeto);
				nau.setNaCliente(naSessionController.getNaCliente());
				nau.setDataAlteracao(new Date());
				nau.setOrigemSincronismo(EnumOrigemSincronismo.CLIENTE);
			}

			listaSistemaDoUsuario.add(nau);
		}

		this.sistemaSelecionado = null;
		this.nivelSelecionado = null;
	}
	
	public void buscarPermissoesEfetivas(NaUsuarioSistema nau){
		listaAtorPermissao = service.buscarPermissoesEfetivas(nau);
	}

	public List<NaSistema> getSistemas() {
		return obterSistemas("");
	}

	public EnumNivelUsuario getNivelSelecionado() {
		return nivelSelecionado;
	}

	public void setNivelSelecionado(EnumNivelUsuario nivelSelecionado) {
		this.nivelSelecionado = nivelSelecionado;
	}

	public List<EnumNivelUsuario> getListaNivelSistemaUsuario() {
		if (listaNivelSistemaUsuario == null) {
			this.listaNivelSistemaUsuario = service.obeterNiveisDeAcessoUsuario(naSessionController.getNaUsuarioSistema().getUsuario());
		}
		return listaNivelSistemaUsuario;
	}

	public String cancelarSistemaUsuario() {
		return "naUsuarioList";
	}

	public NaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(NaUsuario usuario) {
		this.usuario = usuario;
	}

	public List<NaAtorPermissao> getListaAtorPermissao() {
		if (listaAtorPermissao == null) {
			listaAtorPermissao = new ArrayList<>();
		}
		return listaAtorPermissao;
	}

	public void setListaAtorPermissao(List<NaAtorPermissao> listaAtorPermissao) {
		this.listaAtorPermissao = listaAtorPermissao;
	}

}
