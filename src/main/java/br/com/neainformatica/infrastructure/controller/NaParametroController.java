package br.com.neainformatica.infrastructure.controller;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;
import org.primefaces.event.ToggleEvent;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.entity.NaParametro;
import br.com.neainformatica.infrastructure.entity.NaParametroGrupo;
import br.com.neainformatica.infrastructure.entity.NaParametroValor;
import br.com.neainformatica.infrastructure.enumeration.EnumFormatDate;
import br.com.neainformatica.infrastructure.enumeration.EnumNaParametroTipo;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumStateForm;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaParametroService;
import br.com.neainformatica.infrastructure.services.NaParametroValorService;
import br.com.neainformatica.infrastructure.tools.Auditoria;
import br.com.neainformatica.infrastructure.tools.NeaDate;
import br.com.neainformatica.infrastructure.tools.Tools;

@Named
@ConversationScoped
public class NaParametroController extends GenericController<NaParametro> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaParametroService service;
	
	@Inject
	private NaParametroValorService naParametroValorService; 
	
	@Inject
	private InfrastructureController infrastructureController;
	
	private NaParametroGrupo grupo;
	private List<NaParametro> naParametros;
	private String nomeParametro;
	private List<NaParametroValor> naParametroValores;
	private NaParametro parametro;
	private NaParametroValor naParametroValor;
	private boolean exibeCampoTipoParametroData;
	private boolean exibeCampoTipoParametroTexto;
	private boolean exibeCampoTipoParametroInteiro;
	private boolean exibeCampoTipoParametroDataTime;
	private boolean exibeCampoTipoParametroEnum;
	private EnumStateForm state = EnumStateForm.BROWSE;
	private Date valorTipoDate;
	private Date valorTipoDateTime;
	private NaParametroValor naParametroValorOld;
	private EnumSimNao valorEnum;
	
	

	@Override
	public void setService(GenericService<NaParametro> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaParametro> getService() {
		return this.service;
	}

	@PostConstruct
	public void createDynamicColumns() {
		clearColumns()
			.addColumn(new ColumnModel("Descricao", "descricao"))
			.addColumn(new ColumnModel("Nome", "nome"))
			.addColumn(new ColumnModel("SomenteSuporte", "somenteSuporte"))
			.addColumn(new ColumnModel("Tipo", "tipo"))
			;
	}

	@Override
	public NaParametro newInstanceEntity() {
		NaParametro naParametro = super.newInstanceEntity();
		
		return naParametro;
	}
	
	/**
	 * Evento disparado quando um registro � selecionado na listagem
	 */
	@Override
	public void onRowSelect(SelectEvent event) {
		super.onRowSelect(event);
	}
	
	public void pesquisarParametro(){
		Integer id = null;
		
		if(this.nomeParametro == null)
			nomeParametro = "";
		
		if(this.nomeParametro.equals("") && this.grupo == null){
			messages.addWarnMessage("Informe um grupo ou o nome de um parâmetro !");
			setNaParametros(null);
			return;
		}
		
		if(this.grupo != null)
			id = this.grupo.getId();
		 
		List<NaParametro> naParametros = service.pesquisarParametro(id, this.nomeParametro, naSessionController.getNaSistema());		
		
		for (NaParametro naParametro2 : naParametros) {
			if(naParametro2.getValorFixo().equals(EnumSimNao.SIM)){
				NaParametroValor naParametrovalor = naParametroValorService.pesquisarParametroValorFixo(naSessionController.getNaSistema(), naParametro2);
				naParametro2.setParametroValor(naParametrovalor.getValor());
			}
			
		}
		setNaParametros(naParametros);
		
	}
	
	public void onRowToggle(ToggleEvent event){
		NaParametro parametro = (NaParametro) event.getData();
		this.naParametroValores = service.pesquisarParametroValor(naSessionController.getNaSistema(), parametro);
		
		
	}
	
	public boolean possuiValorFixo(NaParametro naParametro){
		
		if(naParametro.getValorFixo().equals(EnumSimNao.SIM))
			return true;
		
			return false;
	}
	
	public String alterarNaParametroValorFixo(NaParametro parametro){
		
		NaParametroValor naParametrovalor = naParametroValorService.pesquisarParametroValorFixo(naSessionController.getNaSistema(), parametro);
		return alterarNaParametroValor(naParametrovalor);
		
	}
	
	
	public String alterarNaParametroValor(NaParametroValor valor){
		this.naParametroValorOld = (NaParametroValor) Tools.cloneSerializable(valor);
		this.naParametroValor = valor;
		EnumNivelUsuario idUsuarioLogado = naSessionController.getNaUsuarioSistema().getNivelUsuario();
		
		if(idUsuarioLogado.getId() < valor.getParametro().getNivelUsuario().getId()){
			messages.addWarnMessage("Esse usuário não tem nível de permissão para fazer alteração !");
			return "naParametroList";
		}
		
		exibeCampoTipoParametro();
		this.state = EnumStateForm.EDIT;
		super.update();
		return "naParametroForm";
	}
	
	public void exibeCampoTipoParametro(){
		    this.exibeCampoTipoParametroEnum = false;
			this.exibeCampoTipoParametroTexto = false;
			this.exibeCampoTipoParametroData = false;
			this.exibeCampoTipoParametroInteiro = false;
			this.exibeCampoTipoParametroDataTime = false;
			
		if(this.naParametroValor.getParametro().getTipo().equals(EnumNaParametroTipo.TEXTO)){
			this.exibeCampoTipoParametroTexto = true;
		}else if (this.naParametroValor.getParametro().getTipo().equals(EnumNaParametroTipo.DATA)){
			this.exibeCampoTipoParametroData = true;
			
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			try {
				 setValorTipoDate(formatter.parse(this.naParametroValor.getValor()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else if (this.naParametroValor.getParametro().getTipo().equals(EnumNaParametroTipo.VALOR_INTEIRO)){
			this.exibeCampoTipoParametroInteiro = true;
			
		}else if (this.naParametroValor.getParametro().getTipo().equals(EnumNaParametroTipo.DATA_HORA)){
			this.exibeCampoTipoParametroDataTime = true;
			
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			try {
				 setValorTipoDateTime(formatter.parse(this.naParametroValor.getValor()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else if(this.naParametroValor.getParametro().getTipo().equals(EnumNaParametroTipo.OPCOES)){
			this.exibeCampoTipoParametroEnum = true;
			setarValorEnum();
			
		}else{
			this.exibeCampoTipoParametroTexto = true;
		}
		
		
	}
	
	public void setarValorEnum(){
		if(naParametroValor.getValor().equals("S"))
			this.valorEnum = EnumSimNao.SIM;
		else
			this.valorEnum = EnumSimNao.NAO;
	}
	
	@Override
	public String salvar()  {
		String dataFormatada = "";
		if(this.naParametroValor.getParametro().getTipo().equals(EnumNaParametroTipo.DATA)){			
			dataFormatada = NeaDate.formatarData(valorTipoDate, EnumFormatDate.RESUMIDO);						
			this.naParametroValor.setValor(dataFormatada);
			
		}else if(this.naParametroValor.getParametro().getTipo().equals(EnumNaParametroTipo.DATA_HORA)){			
			dataFormatada = NeaDate.formatarData(valorTipoDateTime, EnumFormatDate.DATE_TIME_HMS);				
			this.naParametroValor.setValor(dataFormatada);
		}else if(this.naParametroValor.getParametro().getTipo().equals(EnumNaParametroTipo.OPCOES)){
			this.naParametroValor.setValor(valorEnum.getValue());
		}
		
		
		this.naParametroValor = this.naParametroValorService.save(this.naParametroValor);
		messages.addInfoMessage("O Valor do Parâmetro foi alterado com sucesso!");
		service.getAuditoria().geraAuditoria(this.naParametroValorOld, this.naParametroValor);
		infrastructureController.removeParametroDaLista(this.naParametroValor.getParametro());
		pesquisarParametro();
		return getNomeFormularioList();
	}
	
	public void pesquisarParametroValor(NaParametro parametro){
		this.naParametroValores = service.pesquisarParametroValor(naSessionController.getNaSistema(), parametro);
	}

	
	public NaParametroGrupo getGrupo() {
		return grupo;
	}

	public void setGrupo(NaParametroGrupo grupo) {
		this.grupo = grupo;
	}

	public List<NaParametro> getNaParametros() {
		return naParametros;
	}

	public void setNaParametros(List<NaParametro> naParametros) {
		this.naParametros = naParametros;
	}

	public String getNomeParametro() {
		return nomeParametro;
	}

	public void setNomeParametro(String nomeParametro) {
		this.nomeParametro = nomeParametro;
	}

	public List<NaParametroValor> getNaParametroValores() {
		return naParametroValores;
	}

	public void setNaParametroValores(List<NaParametroValor> naParametroValores) {
		this.naParametroValores = naParametroValores;
	}

	public NaParametro getParametro() {
		return parametro;
	}

	public void setParametro(NaParametro parametro) {
		this.parametro = parametro;
	}

	public NaParametroValor getNaParametroValor() {
		return naParametroValor;
	}

	public void setNaParametroValor(NaParametroValor naParametroValor) {
		this.naParametroValor = naParametroValor;
	}

	public boolean isExibeCampoTipoParametroData() {
		return exibeCampoTipoParametroData;
	}

	public void setExibeCampoTipoParametroData(boolean exibeCampoTipoParametroData) {
		this.exibeCampoTipoParametroData = exibeCampoTipoParametroData;
	}

	public boolean isExibeCampoTipoParametroTexto() {
		return exibeCampoTipoParametroTexto;
	}

	public void setExibeCampoTipoParametroTexto(boolean exibeCampoTipoParametroTexto) {
		this.exibeCampoTipoParametroTexto = exibeCampoTipoParametroTexto;
	}

	public boolean isExibeCampoTipoParametroInteiro() {
		return exibeCampoTipoParametroInteiro;
	}

	public void setExibeCampoTipoParametroInteiro(boolean exibeCampoTipoParametroInteiro) {
		this.exibeCampoTipoParametroInteiro = exibeCampoTipoParametroInteiro;
	}

	public boolean isExibeCampoTipoParametroDataTime() {
		return exibeCampoTipoParametroDataTime;
	}

	public void setExibeCampoTipoParametroDataTime(boolean exibeCampoTipoParametroDataTime) {
		this.exibeCampoTipoParametroDataTime = exibeCampoTipoParametroDataTime;
	}

	public EnumStateForm getState() {
		return state;
	}

	public void setState(EnumStateForm state) {
		this.state = state;
	}

	public Date getValorTipoDate() {
		return valorTipoDate;
	}

	public void setValorTipoDate(Date valorTipoDate) {
		this.valorTipoDate = valorTipoDate;
	}

	public Date getValorTipoDateTime() {
		return valorTipoDateTime;
	}

	public void setValorTipoDateTime(Date valorTipoDateTime) {
		this.valorTipoDateTime = valorTipoDateTime;
	}

	public NaParametroValor getNaParametroValorOld() {
		return naParametroValorOld;
	}

	public void setNaParametroValorOld(NaParametroValor naParametroValorOld) {
		this.naParametroValorOld = naParametroValorOld;
	}
	
	public List<EnumSimNao> getSimNao(){
		return Arrays.asList(EnumSimNao.values());
	}

	public boolean isExibeCampoTipoParametroEnum() {
		return exibeCampoTipoParametroEnum;
	}

	public void setExibeCampoTipoParametroEnum(boolean exibeCampoTipoParametroEnum) {
		this.exibeCampoTipoParametroEnum = exibeCampoTipoParametroEnum;
	}

	public EnumSimNao getValorEnum() {
		return valorEnum;
	}

	public void setValorEnum(EnumSimNao valorEnum) {
		this.valorEnum = valorEnum;
	}



}

