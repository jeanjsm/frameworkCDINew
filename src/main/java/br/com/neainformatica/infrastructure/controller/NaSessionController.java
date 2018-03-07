package br.com.neainformatica.infrastructure.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.picketlink.annotations.PicketLink;
import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.filter.event.AfterSelectionClientEvent;
import br.com.neainformatica.infrastructure.services.AtualizaBaseService;
import br.com.neainformatica.infrastructure.services.InfrastructureService;
import br.com.neainformatica.infrastructure.services.NaClienteService;
import br.com.neainformatica.infrastructure.services.NaUsuarioService;

@Named
@SessionScoped
public class NaSessionController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private InfrastructureService infrastructureService;

	@Inject
	@PicketLink
	@Named
	private Autenticador autenticador;

	@Inject
	private GenericMessages messages;

	@Inject
	private InfrastructureController infrastructureController;

	@Inject
	private NaUsuarioService usuarioService;

	@Inject
	private NaClienteService naClienteService;

	@Inject
	protected Log log;

	@Inject
	private Event<AfterSelectionClientEvent> afterSelectionClientEvent;

	private NaCliente naCliente;
	private NaSistema naSistema;
	private NaUsuario naUsuario;
	private NaUsuarioSistema naUsuarioSistema;
	private boolean multiplosCliente = false;
	private boolean modalSelectClienteBtnCancel = false;
	private boolean sistemaEmModoSomenteLeitura = false;

	// atributos, parâmetros de sessão
	private Map<String, Object> atributos;

	private int quantidadeLinhasGrid;

	@PostConstruct
	public void init() {
		atributos = new HashMap<>();
	}

	public void limparNaCliente() {
		this.naCliente = null;

		if (naClienteService.buscarListaCliente().size() < 1)
			naCliente = infrastructureService.buscarCliente();

	}

	public Map<String, Object> getAtributos() {
		return atributos;
	}
	
	public void redirecionaParaLogin() {
	    FacesContext fc = FacesContext.getCurrentInstance();
	    fc.getApplication().getNavigationHandler().handleNavigation(fc, null, "/common/login.xhtml?faces-redirect=true");
	}

	public void setAtributos(Map<String, Object> atributos) {
		this.atributos = atributos;
	}

	/**
	 * Obtém um atributo de sessão definido pelo método setAtributo
	 * 
	 * @param key
	 * @return Atributo da sessão
	 */
	public Object getAtributo(String key) {
		Object object = atributos.get(key);
		return object;
	}

	/**
	 * Define um atributo de sessão pela chave key
	 * 
	 * @param key
	 *            String chave de atributo
	 * @param object
	 *            Valor do atributo de sessão
	 */
	public void setAtributo(String key, Object object) {
		atributos.put(key, object);
	}

	/**
	 * Este método deve ser executado quando o usuário logar, para que verifique
	 * se existe mais de um cliente na base de dados.
	 * 
	 */
	@SuppressWarnings("static-access")
	public void verificaListaNaCliente() {

		List<NaCliente> listaNaCliente = naClienteService.buscaListaDeClientes();

		if (listaNaCliente.size() == 1) {
			this.naCliente = listaNaCliente.get(0);
		} else {
			this.multiplosCliente = true;

			if (infrastructureController.getNeaInfrastructureClienteDefault() != null)
				this.naCliente = infrastructureService.getDaoCliente().buscarClientePorIdNea(infrastructureController.getNeaInfrastructureClienteDefault());
		}

	}

	public void defineNaCliente() {
		naCliente = naClienteService.defineNaCliente(naCliente);

		RequestContext context = RequestContext.getCurrentInstance();

		NaUsuarioSistema usuarioSistema = null;

		if (this.naUsuario != null) {
			usuarioSistema = usuarioService.possuiPermissaoParaAcessarEsteSistema(naUsuario, naCliente);

			if (usuarioSistema != null) {
				setNaUsuarioSistema(usuarioSistema);
				autenticador.criaAutenticacaoUsuario();

				if (!autenticador.validacoesAposAutenticacao(usuarioSistema)) {
					this.naCliente = null;
					context.addCallbackParam("ocorreuInconsistencia", true);
					setNaUsuarioSistema(null);
				}
				autenticador.setModoAdministradorDisponivel(autenticador.hasRole("NA_MODO_ADMINISTRADOR-ACESSAR"));
				context.addCallbackParam("ocorreuInconsistencia", false);
			} else {
				context.addCallbackParam("ocorreuInconsistencia", true);
				messages.addErrorMessage("Você não tem permissão para acessar este Sistema no cliente : " + this.naCliente.getNome());
				this.naCliente = null;
			}
		}

		// dispara um evento do CDI para qualquer aplicação cliente validar o
		// que for necessário
		if (this.naCliente != null)
			afterSelectionClientEvent.fire(new AfterSelectionClientEvent());

	}

	public boolean apresentaModalEscolhaCliente() {
		if (this.multiplosCliente && naCliente == null)
			return true;
		return false;
	}

	public NaCliente getNaCliente() {

		if (AtualizaBaseService.SISTEMA_SEM_LOGIN_CLIENTE_UNICO) {
			if (this.naCliente == null)
				this.naCliente = buscarClientePadrao();
		}

		if (this.naCliente == null) {
			log.error("------------------------------------------");
			log.error("Verificar porque o NaCliente esta null");
			log.error("------------------------------------------");
		}

		return this.naCliente;
	}

	/**
	 * sistemas clientes do Framework que não possuem login não colocam o
	 * cliente em sessão nesses casos vamos colocar o primeiro encontrada na
	 * base na sessão. Em todos os casos atuais os sistemas que não possuem
	 * login possuem apenas um cliente o que não gera problemas
	 */
	private NaCliente buscarClientePadrao() {

		List<NaCliente> clientes = infrastructureService.getDaoCliente().buscarListaCliente();

		if (clientes != null && clientes.size() > 0)
			return clientes.get(0);

		return null;
	}

	public void setNaCliente(NaCliente naCliente) {
		this.naCliente = naCliente;
	}

	public StreamedContent getLogoMunicipio() {
		if (naCliente != null && naCliente.getBrasao() != null) {
			return new DefaultStreamedContent(new ByteArrayInputStream(naCliente.getBrasao()), "image/jpg");
		} else {
			return null;
		}

	}

	public StreamedContent getLogoAdministracao() {
		if (naCliente != null) {
			return new DefaultStreamedContent(new ByteArrayInputStream(naCliente.getLogoAdministracao()), "image/jpg");
		} else {
			return null;
		}
	}

	public void redimensionarGrid() {

		Map<String, String> requestParamMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();

		int quantidadeLinhaPadrao = 19;

		if (requestParamMap.containsKey("tamanho")) {
			String valor = requestParamMap.get("tamanho");

			int tamanhoTela = Integer.parseInt(valor);

			int tamanhoTelaPadrao = 653;

			int regraTres = (tamanhoTela * quantidadeLinhaPadrao);
			quantidadeLinhasGrid = (regraTres / tamanhoTelaPadrao);

		}

	}

	public NaSistema getNaSistema() {
		if (this.naSistema == null) {
			this.naSistema = infrastructureController.getNaSistema();
		}
		return this.naSistema;
	}

	public boolean usuarioLogadoPossuiNivelAcessoMaiorIgual(EnumNivelUsuario parametro) {
		if (naUsuarioSistema.getNivelUsuario().getId() >= parametro.getId())
			return true;
		return false;
	}

	public boolean usuarioMaiorIgualAdministrador() {
		if (naUsuarioSistema.getNivelUsuario().getId() >= EnumNivelUsuario.ADMINISTRADOR.getId())
			return true;
		return false;
	}

	public NaUsuarioSistema getNaUsuarioSistema() {
		return naUsuarioSistema;
	}

	public void setNaUsuarioSistema(NaUsuarioSistema naUsuarioSistema) {
		this.naUsuarioSistema = naUsuarioSistema;
	}

	public Integer tempoDeSessao() {
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return req.getSession().getMaxInactiveInterval() / 60;
	}

	public int getQuantidadeLinhasGrid() {
		return quantidadeLinhasGrid;
	}

	public void setQuantidadeLinhasGrid(int quantidadeLinhasGrid) {
		this.quantidadeLinhasGrid = quantidadeLinhasGrid;
	}

	public boolean isSistemaEmModoSomenteLeitura() {
		return sistemaEmModoSomenteLeitura;
	}

	public void setSistemaEmModoSomenteLeitura(boolean sistemaEmModoSomenteLeitura) {
		this.sistemaEmModoSomenteLeitura = sistemaEmModoSomenteLeitura;
	}

	public boolean isMultiplosCliente() {
		return multiplosCliente;
	}

	public void setMultiplosCliente(boolean multiplosCliente) {
		this.multiplosCliente = multiplosCliente;
	}

	public boolean isModalSelectClienteBtnCancel() {
		return modalSelectClienteBtnCancel;
	}

	public void setModalSelectClienteBtnCancel(boolean modalSelectClienteBtnCancel) {
		this.modalSelectClienteBtnCancel = modalSelectClienteBtnCancel;
	}

	public NaUsuario getNaUsuario() {
		return naUsuario;
	}

	public void setNaUsuario(NaUsuario naUsuario) {
		this.naUsuario = naUsuario;
	}

	public void setNaSistema(NaSistema naSistema) {
		this.naSistema = naSistema;
	}

}