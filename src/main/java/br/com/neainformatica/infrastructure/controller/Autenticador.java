package br.com.neainformatica.infrastructure.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.picketlink.Identity;
import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.Authenticator;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.authentication.event.LoggedInEvent;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.PartitionManager;
import org.picketlink.idm.RelationshipManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.BasicModel;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;

import br.com.neainformatica.infrastructure.entity.NaPermissao;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.filter.FilterKaptcha;
import br.com.neainformatica.infrastructure.services.AtualizaBaseService;
import br.com.neainformatica.infrastructure.services.NaAtorService;
import br.com.neainformatica.infrastructure.services.NaPerfilService;
import br.com.neainformatica.infrastructure.services.NaPermissaoService;
import br.com.neainformatica.infrastructure.services.NaUsuarioService;
import br.com.neainformatica.infrastructure.tools.Tools;

@Named("autenticador")
@SessionScoped
@PicketLink
public class Autenticador extends BaseAuthenticator implements Authenticator, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private IdentityManager identityManager;

	@Inject
	private PartitionManager partitionManager;

	@Inject
	private RelationshipManager relationshipManager;

	@Inject
	private DefaultLoginCredentials credentials;

	@Inject
	private NaUsuarioService usuarioService;

	@Inject
	private NaAtorService naAtorService;

	@Inject
	private Identity identity;

	@Inject
	private GenericMessages messages;

	@Inject
	NaPerfilService perfilService;

	@Inject
	NaPermissaoService permissaoService;

	@Inject
	NaSessionController naSessionController;

	@Inject
	private Instance<AtualizaBaseService> atualizaBaseService;

	@Inject
	protected Log log;

	@Inject
	private InfrastructureController infrastructureController;

	private NaUsuario usuario;

	private boolean modoAdministradorDisponivel = false;
	private boolean modoAdministradorAtivo = false;
	
	private boolean alterarSenha;
	
	private List<String> listaDePermissoesUsuario;

	private boolean exibeFormLogin;
	private boolean exibeFormAlterarSenha;
	private boolean exibeFormRecuperarSenha;
	private boolean autorizarLoginSemSenha;

	private String senhaAtual;
	private String novaSenha;
	private String novaSenhaConfirmacao;
	
	private String loginUsuarioDigitado;
	private String loginSenhaDigitada;
	
	private String recuperarSenhaCpf;
	private String recuperarSenhaCaptcha;
		
	@PostConstruct
	private void init() {		
		this.alterarSenha = false;
		this.autorizarLoginSemSenha = false;
	
		this.exibeFormLogin = true;
		this.exibeFormAlterarSenha = false;
		this.exibeFormRecuperarSenha = false;
		
		this.recuperarSenhaCpf = null;
		this.recuperarSenhaCaptcha = null;
		
		this.senhaAtual = null;
		this.novaSenha = null;
    	this.novaSenhaConfirmacao = null;	
	}

	private boolean precisaTrocarSenhar(NaUsuario usuario, String senha) {

		if (usuario.getAlterarSenhaProximoLogin() != null && usuario.getAlterarSenhaProximoLogin().equals(EnumSimNao.SIM))
			return true;

		if (!usuarioService.validaFormatoDeSenha(senha))
			return true;

		if (senha.equals(usuario.getCpfCnpj()))
			return true;

		return false;

	}

	@Override
	public void authenticate() {
		
		
		if (!validaUsuarioSenha()) {
			setStatus(AuthenticationStatus.FAILURE);
			return;
		}
		
		credentials.setUserId(this.usuario.getNomeUsuarioAuditoria());
		credentials.setPassword(this.usuario.getSenha());
		
		
		if (this.usuario == null) {
			setStatus(AuthenticationStatus.FAILURE);
			messages.addErrorMessage("Usuário não foi encontrado na base de dados!.");
			return;
		}
		
		if (this.usuario == null) {
			setStatus(AuthenticationStatus.FAILURE);
			messages.addErrorMessage("Usuário não foi encontrado na base de dados!.");
			return;
		}

		naSessionController.setNaUsuario(usuario);
		naSessionController.verificaListaNaCliente();

		if (!naSessionController.isMultiplosCliente() || naSessionController.getNaCliente() != null) {

			criaAutenticacaoUsuario();
			NaUsuarioSistema usuarioSistema = usuarioService.possuiPermissaoParaAcessarEsteSistema(usuario, naSessionController.getNaCliente());

			if (usuarioSistema == null) {
				setStatus(AuthenticationStatus.FAILURE);
				messages.addErrorMessage("Você não tem permissão para acessar este sistema.");
				return;
			}

			naSessionController.setNaUsuarioSistema(usuarioSistema);

			if (!validacoesAposAutenticacao(usuarioSistema)) {
				setStatus(AuthenticationStatus.FAILURE);
				naSessionController.setNaUsuarioSistema(null);
				return;
			}
			setModoAdministradorDisponivel(hasRole("NA_MODO_ADMINISTRADOR-ACESSAR"));
		}

		setModoAdministradorAtivo(false);	
		
		log.debug("usuário logado com sucesso. " + (this.usuario.getId() + "-" + this.usuario.getCpfCnpj() + this.usuario.getNome()));
		
		setAccount(new User(this.usuario.getNomeUsuarioAuditoria()));
		exibirFormlogin();	
		setStatus(AuthenticationStatus.SUCCESS);				
		
		recdirectToIndex();
	}
	
	
	public void recdirectToIndex() {
		try {

			log.debug("Redirecionando para página principal");
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			String contextPath = ((HttpServletRequest) request).getContextPath();
			FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + "/index.xhtml");

		} catch (IOException e) {
			messages.addErrorMessage("Erro ao redirecionar para página principal");
			e.printStackTrace();
		}

	}
	
	public void login() {
		this.alterarSenha = false;
		this.usuario = null;
		
		if (identity.isLoggedIn())
			identity.logout();
		identity.login();
	}

	
	private boolean validaUsuarioSenha() {

		naSessionController.setNaCliente(null);
		naSessionController.setNaUsuarioSistema(null);
		naSessionController.setNaSistema(null);		
		this.alterarSenha = false;
		this.usuario = null;
				
		if (identity.isLoggedIn())
			identity.logout();

		iniciarAtualizaBaseService();

		if (this.loginUsuarioDigitado == null || this.loginSenhaDigitada == null) {
			messages.addErrorMessage("Favor informar o CPF e senha de acesso.");
			return false;
		}

		this.loginUsuarioDigitado = this.loginUsuarioDigitado.toUpperCase();

		this.usuario = usuarioService.buscaUsuarioCpfOrLogin(this.loginUsuarioDigitado);

		if (this.usuario == null) {
			messages.addErrorMessage("Usuário não encontrado na base de dados");
			return false;
		}

		if (this.loginUsuarioDigitado.equals("SUPORTE")) {
			if (AtualizaBaseService.BLOQUEIA_LOGIN_USUARIO_SUPORTE) {
				messages.addErrorMessage("o uso do usuário SUPORTE esta bloqueado neste sistema");
				return false;
			}

			if (!this.loginSenhaDigitada.equals(Tools.senhaDoDia())) {	
				messages.addErrorMessage("Usuário ou senha inválidos. Tente novamente");
				this.usuario = null;
				return false;
			}

		} else {

			String senhaCalculada = Tools.geraMD5(usuario.getIdUsuarioSenha() + this.loginSenhaDigitada).toUpperCase();

			if (!this.autorizarLoginSemSenha) {
			
				if (!senhaCalculada.equals(usuario.getSenha().toUpperCase())) {				
					this.usuario = null;
					messages.addErrorMessage("Usuário ou senha inválidos. Tente novamente");
					return false;
				}
	
				if (precisaTrocarSenhar(this.usuario, this.loginSenhaDigitada)) {
					messages.addErrorMessage("Sua senha não atende os requisitos mínimos de segurança, favor alterar.");
					exibirFormAlterarSenha();
					return false;
				}
			}
			
			preencherSenhaFraca(this.usuario, this.loginSenhaDigitada);
		}

		return true;
	}	

	private void preencherSenhaFraca(NaUsuario usuario, String senhaDigitada) {

		// se ja possui id no server não preciso gerar a senha fraca, pois esta ja foi
		// sincronizado
		if (usuario.getIdNaUsuarioServer() != null)
			return;

		String senhaFraca = Tools.encripta(usuario.getIdUsuarioSenha() + senhaDigitada);

		String senhaServer = usuario.getSenhaNaUsuarioServer();
		if (senhaServer == null || senhaServer.trim().equals("") || !senhaServer.equals(senhaFraca)) {
			usuarioService.setUserAudit(usuario);
			usuario = usuarioService.save(usuario);
			usuarioService.setUserAudit(null);
		}

	}

	/**
	 * Quando o login é feito pelo framework o atualizaBaseService não é injetado e
	 * as estruturas iniciais de permissões, usuários, parâmetros... não são criadas
	 */
	private void iniciarAtualizaBaseService() {
		if (InfrastructureController.getNeaInfrastructureSistemaId().equals(InfrastructureController.NEA_INFRASTRURA_FRAMEWORK_ID)) {
			AtualizaBaseService atualizaBase = atualizaBaseService.get();
			atualizaBase.atualizaSistemaSessao();
		}

	}

	public void limparVariaveis() {
		this.alterarSenha = false;
		// this.senhaSemMD5 = "";
		// this.senha = "";
		this.usuario = null;
	}

	public void criaAutenticacaoUsuario() {

		String senhaDigitada = credentials.getPassword();

		this.listaDePermissoesUsuario = new ArrayList<String>();
		IdentityManager identityManager = partitionManager.createIdentityManager();

		User user = BasicModel.getUser(identityManager, usuario.getNomeUsuarioAuditoria());
		if (user == null) {
			user = new User(usuario.getLogin());
			user.setFirstName(usuario.getNome());
			identityManager.add(user);
		} else {
			user.setFirstName(usuario.getNome());
			identityManager.update(user);
		}

		identityManager.updateCredential(user, new Password(senhaDigitada));

		// configurando roles de acesso
		Group group = BasicModel.getGroup(identityManager, "grupo");
		if (group == null) {
			group = new Group("grupo");
			identityManager.add(group);
		}

		// pega a lista de permissões

		if (!usuario.getLogin().toUpperCase().equals("SUPORTE")) {

			List<NaPermissao> permissaoAdm = permissaoService.buscarPermissoesSistema(infrastructureController.getNaSistema(), null);
			List<String> permissoesAdm = naAtorService.permissoesAdmParaRole(permissaoAdm);

			// quando existir um role na sessão ele vai ser apagado
			for (String pa : permissoesAdm) {
				Role roleAdm = BasicModel.getRole(identityManager, pa);
				if (roleAdm != null)
					BasicModel.revokeRole(relationshipManager, user, roleAdm);
			}

		}

		List<String> permissoes = permissaoService.findListByUsuarioNaSistemaNaCliente(usuario, infrastructureController.getNaSistema(),
				naSessionController.getNaCliente());

		for (String p : permissoes)
			listaDePermissoesUsuario.add(p);

	}

	public boolean validacoesAposAutenticacao(NaUsuarioSistema usuarioSistema) {

		// verifica se tem mais de um cliente, caso não tenha verifica se o
		// cliente tem permissão de acesso
		if (!naSessionController.isMultiplosCliente()) {
			if (naSessionController.getNaCliente().getSituacaoAcesso() == EnumSituacaoAcesso.BLOQUEADO) {
				messages.addErrorMessage("Não foi possivel efetuar o login! Cliente sem permissão de acesso");
				return false;
			}
		}
		// verificar se o sistema tem permisso de acesso
		if (usuarioSistema.getSistema().getSituacaoAcesso() == EnumSituacaoAcesso.BLOQUEADO) {
			messages.addErrorMessage("Não foi possivel efetuar o login! Sistema sem permissão de acesso");
			return false;
		}
		// Verifica se o usuário pode acessar o sistema
		if (usuarioSistema.getSituacaoAcesso() == EnumSituacaoAcesso.BLOQUEADO) {
			messages.addErrorMessage("Não foi possivel efetuar o login! Usuário sem permissão de acesso");
			return false;
		}

		if (naSessionController.getNaCliente() != null && naSessionController.getNaCliente().getSituacaoAcesso() == EnumSituacaoAcesso.SOMENTE_LEITURA) {
			naSessionController.setSistemaEmModoSomenteLeitura(true);
		} else {
			if (usuarioSistema.getSistema().getSituacaoAcesso() == EnumSituacaoAcesso.SOMENTE_LEITURA
					| usuarioSistema.getSituacaoAcesso() == EnumSituacaoAcesso.SOMENTE_LEITURA) {
				naSessionController.setSistemaEmModoSomenteLeitura(true);
			} else {
				naSessionController.setSistemaEmModoSomenteLeitura(false);
			}
		}

		atualizaPermissoesDeAcessoUsuario(usuarioSistema);

		return true;
	}

	private void atualizaPermissoesDeAcessoUsuario(NaUsuarioSistema usuarioSistema) {
		// if (usuarioSistema.getNivelUsuario().getId() >=
		// EnumNivelUsuario.SUPORTE.getId())
		// usuarioService.verificaPermissoesEmTodosOsSistemas(usuarioSistema);
	}

	public boolean hasRole(String role) {

		boolean has = false;
		if (naSessionController.getNaUsuarioSistema() == null)
			return has;

		String usuarioAtual = naSessionController.getNaUsuarioSistema().getUsuario().getLogin();

		if (usuarioAtual != null) {
			if (usuarioAtual.equals("SUPORTE")) {
				has = true;
			} else if (naSessionController.getNaUsuarioSistema().getNivelUsuario().getId() >= EnumNivelUsuario.SUPORTE.getId()) {
				has = true;
			} else if (usuarioAtual != null) {
				if (this.listaDePermissoesUsuario.contains(role))
					return true;
				return false;
			}
		}

		return has;

	}

	public boolean hasPermissao(String permissao) throws Exception {
		boolean possui = hasRole(permissao);

		if (!possui) {
			permissaoService.checaExistenciaPermissao(permissao);
			return false;
		}

		return possui;
	}

	public void teste(@Observes LoggedInEvent event) {
		System.out.println("Autenticador.teste()");
	}

	public NaUsuarioService getUsuarioService() {
		return usuarioService;
	}

	public void setUsuarioService(NaUsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	public NaPerfilService getPerfilService() {
		return perfilService;
	}

	public void setPerfilService(NaPerfilService perfilService) {
		this.perfilService = perfilService;
	}

	public NaPermissaoService getPermissaoService() {
		return permissaoService;
	}

	public void setPermissaoService(NaPermissaoService permissaoService) {
		this.permissaoService = permissaoService;
	}

	public boolean isModoAdministradorDisponivel() {
		return modoAdministradorDisponivel;
	}

	public void setModoAdministradorDisponivel(boolean modoAdministradorDisponivel) {
		this.modoAdministradorDisponivel = modoAdministradorDisponivel;
	}

	public boolean isModoAdministradorAtivo() {
		return modoAdministradorAtivo;
	}

	public void setModoAdministradorAtivo(boolean modoAdministradorAtivo) {
		this.modoAdministradorAtivo = modoAdministradorAtivo;
	}

	public String setModoAdministradorAtivo(String modoAdministradorAtivo) {
		if (modoAdministradorAtivo.equalsIgnoreCase("true"))
			this.modoAdministradorAtivo = true;
		else
			this.modoAdministradorAtivo = false;

		return "";
	}

	public void eliminarSessaoDoUsuarioLogado() {
		naSessionController.setNaUsuarioSistema(null);

		if (AtualizaBaseService.LIMPAR_NACLIENTE_SESSAO_AO_LOGAR)
			naSessionController.setNaCliente(null);

		if (identity.isLoggedIn()) {
			identity.logout();
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			request.getSession().invalidate();
		}
	}

	public void exibirFormRecuperarSenha() {		
		this.exibeFormLogin = false;
		this.exibeFormRecuperarSenha = true;
		this.exibeFormAlterarSenha = false;

	}

	public void exibirFormAlterarSenha() {
		this.exibeFormLogin = false;
		this.exibeFormRecuperarSenha = false;
		this.exibeFormAlterarSenha = true;
	}

	public void exibirFormlogin() {
		this.exibeFormLogin = true;
		this.exibeFormRecuperarSenha = false;
		this.exibeFormAlterarSenha = false;
	}

	
	/**
	 * Assim que o usuário alterar a senha, faço o login novamente ja com a nova esnha 
	 */
	public void alterarSenhaAntesLogin() {
		try {
			
			usuarioService.alterarSenha(this.usuario, this.senhaAtual, this.novaSenha, this.novaSenhaConfirmacao);
			this.alterarSenha = false;
			this.loginUsuarioDigitado = this.usuario.getCpfCnpj();
			this.loginSenhaDigitada = this.novaSenha;
			
			login();				

		} catch (Exception e) {
			messages.addErrorMessage(e.getMessage());
		}
	}
	
	public void recuperarSenha() {
		try {

			if (!validarCaptcha()) {
				messages.addErrorMessage("O campo chave não confere com a imagem!");
				return;
			}

			usuarioService.enviarEmailrecuperacaoSenha(this.recuperarSenhaCpf);

			NaUsuario naUsuario = usuarioService.buscaUsuarioCpfOrLogin(this.recuperarSenhaCpf);
			Integer quantidadeCaracteresExibidosNoEmail = 3;

			String email = naUsuario.getEmail();
			String primeirosCaracteres = email.substring(0, quantidadeCaracteresExibidosNoEmail);
			String ultimosCaracteres = email.substring(email.indexOf("@"), email.length());
			StringBuilder emailFormatado = new StringBuilder();
			emailFormatado.append(primeirosCaracteres + "********" + ultimosCaracteres);

			messages.addInfoMessage("E-mail enviado com sucesso!");
			messages.addInfoMessage("Dentro de instantes você receberá um email em " + emailFormatado.toString() + " com instruções para alterar sua senha!");

			exibirFormlogin();

		} catch (Exception e) {
			messages.addErrorMessage(e.getMessage());
		}
	}
	
	@SuppressWarnings({ "unchecked" })
	private boolean validarCaptcha() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		List<String> listaKaptchaKey = (List<String>) request.getSession().getAttribute(FilterKaptcha._LISTA_KSK);
		request.getSession().removeAttribute(FilterKaptcha._LISTA_KSK);

		log.debug("captchas digitdos pelo usuário: " + listaKaptchaKey);
		if (this.recuperarSenhaCaptcha != null && !listaKaptchaKey.contains(this.recuperarSenhaCaptcha)) {
			this.recuperarSenhaCaptcha = null;
			listaKaptchaKey = null;
			return false;
		}
		this.recuperarSenhaCaptcha = null;
		return true;
	}

	public boolean isAlterarSenha() {
		return alterarSenha;
	}

	public void setAlterarSenha(boolean alterarSenha) {
		this.alterarSenha = alterarSenha;
	}

	public NaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(NaUsuario usuario) {
		this.usuario = usuario;
	}

	public IdentityManager getIdentityManager() {
		return identityManager;
	}

	public void setIdentityManager(IdentityManager identityManager) {
		this.identityManager = identityManager;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}
	
	public boolean isExibeFormLogin() {
		return exibeFormLogin;
	}

	public boolean isExibeFormAlterarSenha() {
		return exibeFormAlterarSenha;
	}

	public boolean isExibeFormRecuperarSenha() {
		return exibeFormRecuperarSenha;
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
		this.novaSenha = novaSenha;
	}

	public String getNovaSenhaConfirmacao() {
		return novaSenhaConfirmacao;
	}

	public void setNovaSenhaConfirmacao(String novaSenhaConfirmacao) {
		this.novaSenhaConfirmacao = novaSenhaConfirmacao;
	}

	public String getLoginUsuarioDigitado() {
		return loginUsuarioDigitado;
	}

	public void setLoginUsuarioDigitado(String loginUsuarioDigitado) {
		this.loginUsuarioDigitado = loginUsuarioDigitado;
	}

	public String getLoginSenhaDigitada() {
		return loginSenhaDigitada;
	}

	public void setLoginSenhaDigitada(String loginSenhaDigitada) {
		this.loginSenhaDigitada = loginSenhaDigitada;
	}

	public void setAutorizarLoginSemSenha(boolean autorizarLoginSemSenha) {
		this.autorizarLoginSemSenha = autorizarLoginSemSenha;
	}

	public String getRecuperarSenhaCpf() {
		return recuperarSenhaCpf;
	}

	public void setRecuperarSenhaCpf(String recuperarSenhaCpf) {
		this.recuperarSenhaCpf = recuperarSenhaCpf;
	}

	public String getRecuperarSenhaCaptcha() {
		return recuperarSenhaCaptcha;
	}

	public void setRecuperarSenhaCaptcha(String recuperarSenhaCaptcha) {
		this.recuperarSenhaCaptcha = recuperarSenhaCaptcha;
	}


}
