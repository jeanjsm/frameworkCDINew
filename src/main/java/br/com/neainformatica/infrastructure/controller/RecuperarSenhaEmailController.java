package br.com.neainformatica.infrastructure.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;

import br.com.neainformatica.infrastructure.entity.NaAlteracaoSenha;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.filter.FilterKaptcha;
import br.com.neainformatica.infrastructure.services.NaAlteracaoSenhaService;
import br.com.neainformatica.infrastructure.services.NaUsuarioService;

@Named
@ViewScoped
public class RecuperarSenhaEmailController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaAlteracaoSenhaService naAlteracaoSenhaService;

	@Inject
	private NaUsuarioService naUsuarioService;

	@Inject
	private GenericMessages messages;

	@Inject
	protected Log log;

	private String hashRecebido;
	private String novaSenhaDigitada;
	private String novaSenhaConfirmacaoDigitada;
	private String captchaDigitado;

	private NaAlteracaoSenha naAlteracaoSenha;

	@PostConstruct
	private void init() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		this.hashRecebido = request.getParameter("hash");

		boolean somenteAtivo = true;
		this.naAlteracaoSenha = naAlteracaoSenhaService.buscaPorHash(this.hashRecebido, somenteAtivo);

	}

	public boolean renderizaPagina() {
		if (this.naAlteracaoSenha != null)
			return true;

		return false;
	}
	
	public String getNomeUsuario() {
		if (this.naAlteracaoSenha != null)
			return this.naAlteracaoSenha.getUsuario().getNome();
		
		return "USUÁRIO NÃO IDENTIFICADO";
	}

	public void recuperacaoDaSenhaViaEmail() {

		try {

			if (!validarCaptcha()) {
				messages.addErrorMessage("O campo código informado não confere com a imagem!");
				return;
			}

			naUsuarioService.forcarNovaSenha(this.naAlteracaoSenha.getUsuario(), this.novaSenhaDigitada, this.novaSenhaConfirmacaoDigitada);

			naAlteracaoSenha.setInvalido(EnumSimNao.SIM);
			naAlteracaoSenhaService.noAudit().save(naAlteracaoSenha);

			messages.addInfoMessage("Senha alterada com sucesso!");

			this.novaSenhaDigitada = null;
			this.novaSenhaConfirmacaoDigitada = null;
			this.naAlteracaoSenha = null;
			limpaListaCaptcha();

			redirecionarParaLogin();

		} catch (Exception e) {
			messages.addErrorMessage(e.getMessage());
		}
	}

	public void limpaListaCaptcha() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		request.getSession().setAttribute(FilterKaptcha._LISTA_KSK, null);
		setCaptchaDigitado(null);
	}

	@SuppressWarnings({ "unchecked" })
	private boolean validarCaptcha() {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		List<String> listaKaptchaKey = (List<String>) request.getSession().getAttribute(FilterKaptcha._LISTA_KSK);
		request.getSession().removeAttribute(FilterKaptcha._LISTA_KSK);

		log.debug("captchas digitdos pelo usuário: " + listaKaptchaKey);
		if (this.captchaDigitado != null && !listaKaptchaKey.contains(this.captchaDigitado)) {
			this.captchaDigitado = null;
			listaKaptchaKey = null;
			return false;
		}
		this.captchaDigitado = null;
		return true;
	}

	public void redirecionarParaLogin() throws IOException {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String contextPath = ((HttpServletRequest) request).getContextPath();
		FacesContext.getCurrentInstance().getExternalContext().redirect(contextPath + "/common/login.xhtml");
	}

	public NaAlteracaoSenhaService getNaAlteracaoSenhaService() {
		return naAlteracaoSenhaService;
	}

	public void setNaAlteracaoSenhaService(NaAlteracaoSenhaService naAlteracaoSenhaService) {
		this.naAlteracaoSenhaService = naAlteracaoSenhaService;
	}

	public String getHashRecebido() {
		return hashRecebido;
	}

	public void setHashRecebido(String hashRecebido) {
		this.hashRecebido = hashRecebido;
	}

	public String getNovaSenhaDigitada() {
		return novaSenhaDigitada;
	}

	public void setNovaSenhaDigitada(String novaSenhaDigitada) {
		this.novaSenhaDigitada = novaSenhaDigitada;
	}

	public String getNovaSenhaConfirmacaoDigitada() {
		return novaSenhaConfirmacaoDigitada;
	}

	public void setNovaSenhaConfirmacaoDigitada(String novaSenhaConfirmacaoDigitada) {
		this.novaSenhaConfirmacaoDigitada = novaSenhaConfirmacaoDigitada;
	}

	public String getCaptchaDigitado() {
		return captchaDigitado;
	}

	public void setCaptchaDigitado(String captchaDigitado) {
		this.captchaDigitado = captchaDigitado;
	}

	public NaAlteracaoSenha getNaAlteracaoSenha() {
		return naAlteracaoSenha;
	}

	public void setNaAlteracaoSenha(NaAlteracaoSenha naAlteracaoSenha) {
		this.naAlteracaoSenha = naAlteracaoSenha;
	}

}
