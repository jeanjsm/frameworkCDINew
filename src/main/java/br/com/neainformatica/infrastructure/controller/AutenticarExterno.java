package br.com.neainformatica.infrastructure.controller;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.picketlink.Identity;
import org.picketlink.annotations.PicketLink;

import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.services.NaUsuarioService;
import br.com.neainformatica.infrastructure.tools.Tools;

@Named("autenticarExterno")
@SessionScoped
public class AutenticarExterno implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private NaUsuarioService usuarioService;

	@Inject	
	private GenericMessages messages;
	
	@Inject
	@SessionScoped
	@PicketLink
	private Autenticador autenticador;
	
	@Inject
	private Identity identity;
	
	@Inject
	protected Conversation conversation;
		
	@Inject
	protected Log log;
	
	private String cpf;
	private NaUsuario usuario;
	//private List<String> listaDePermissoesUsuario;
	private String contextPath;
	private String pwd;
	private String chave;
	
	public void init() throws IOException {
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		this.contextPath = ((HttpServletRequest) request).getContextPath();
		//this.chave = tools.geraMD5("neainformatica").toLowerCase();
		this.cpf = request.getParameter("usr");
		this.pwd = request.getParameter("pwd");
		
		verificarLoginExterno();
	}
	
	public void initConversation() {
		if (conversation == null) {
			return;
		}
		if (!FacesContext.getCurrentInstance().isPostback() && conversation.isTransient()) {
			conversation.setTimeout(1800000);
			conversation.begin();
		}

	}
	
	
	
	public void reInitConversation() throws IOException {
		identity.logout();
		initConversation();
		init();
	}
	
	public void verificarLoginExterno() throws IOException{
		
		if(validarUrl()){
		
		if (this.cpf != null && !this.cpf.equals("")) {
			
			this.usuario = usuarioService.buscaUsuarioCpfOrLogin(this.cpf);	
						
			if(usuario != null){
								
				autenticador.setLoginUsuarioDigitado(this.cpf);
				autenticador.setLoginSenhaDigitada(this.pwd);
				autenticador.login();			
									
			}else{
				autenticador.eliminarSessaoDoUsuarioLogado();
				messages.addErrorMessage("Falha de autenticação - Os dados de acesso fornecidos são inválidos.");
			}
			
		  }
		}else{
			messages.addErrorMessage("Falha de autenticação - Url inválida");
		}
					
	}
	
	public boolean validarUrl(){
		
		String url = Tools.geraMD5(cpf + chave + formatarData(new Date())).toLowerCase();
		if(url.equals(pwd))
		    return true;
		else
			return false;
		
	}
	
	@Deprecated
	/**O framework possui uma classe especifica para formação de datas.
	 * Favor utilizar o método br.com.neainformatica.infrastructure.tools.NeaDate.formatarData(data, EnumFormatDate.RESUMIDO_INTERNACIONAL_SEM_MASCARA)
	 * @param data
	 * @return
	 */
	public static String formatarData(Date data) {

		if (data == null)
			return "";

		SimpleDateFormat formatador = new SimpleDateFormat("yyyyMMdd");
		return formatador.format(data);
	}
	
	public NaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(NaUsuario usuario) {
		this.usuario = usuario;
	}
	
	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	

}
