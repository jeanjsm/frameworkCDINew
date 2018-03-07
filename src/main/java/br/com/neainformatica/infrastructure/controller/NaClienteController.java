package br.com.neainformatica.infrastructure.controller;

/**
 -----------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 28/04/2014 09:00:21
 @Author  = NELSON
 @Versão da Classe = 
 -----------------------------------------------
 */

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

import br.com.neainformatica.infrastructure.entity.NaBairro;
import br.com.neainformatica.infrastructure.entity.NaCidade;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaLogradouro;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoZona;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaBairroService;
import br.com.neainformatica.infrastructure.services.NaCidadeService;
import br.com.neainformatica.infrastructure.services.NaClienteService;
import br.com.neainformatica.infrastructure.services.NaLogradouroService;

@Named
@ConversationScoped
public class NaClienteController extends GenericController<NaCliente> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NaClienteService service;

	@Inject
	private NaCidadeService cidadeService;

	@Inject
	private NaBairroService bairroService;

	@Inject
	private NaLogradouroService logradouroService;

	@Inject
	private NaSessionController naSessionController;

	private UploadedFile file;

	private List<EnumSituacaoAcesso> listaSituacaoAcesso = new ArrayList<>();
	private List<EnumSimNao> listaSituacaoAtivo = new ArrayList<>();
	private List<EnumTipoZona> listaLogradouroTipoZona = new ArrayList<>();

	private List<NaCidade> cidadeLista;
	private List<NaBairro> bairroLista;
	private List<NaLogradouro> logradouroLista;

	@PostConstruct
	public void createDynamicColumns() {
		// monta a lista dinamica de colunas para exibicao no templateList.xhtml
		// define quais coluna aparecem na grid de listagem
		clearColumns().addColumn(new ColumnModel("Nome", "nome")).addColumn(new ColumnModel("Cnpj", "cnpj")).addColumn(new ColumnModel("LogradouroTipoZona", "logradouroTipoZona"))
				.addColumn(new ColumnModel("LogradouroCep", "logradouroCep")).addColumn(new ColumnModel("LogradouroNome", "logradouroNome"))
				.addColumn(new ColumnModel("BairroNome", "bairroNome")).addColumn(new ColumnModel("LogradouroComplemento", "logradouroComplemento"))
				.addColumn(new ColumnModel("LogradouroQuadra", "logradouroQuadra")).addColumn(new ColumnModel("LogradouroLote", "logradouroLote"))
				.addColumn(new ColumnModel("LogradouroLatitude", "logradouroLatitude")).addColumn(new ColumnModel("LogradouroLongitude", "logradouroLongitude"))
				.addColumn(new ColumnModel("CidadeNome", "cidadeNome"));

	}

	public void buscarCidades() {
		if (this.objeto.getUf() != null) {
			cidadeLista = cidadeService.buscaCidadesByEstado(this.objeto.getUf());
		} else
			cidadeLista = Collections.emptyList();
		// buscarBairros();
	}

	/*
	 * public void buscarBairros() { if(this.objeto.getCidadelogradouro() !=
	 * null) { bairroLista =
	 * bairroService.buscaBairrosByCidade(this.objeto.getCidadelogradouro()); }
	 * else bairroLista = Collections.emptyList(); buscarLogradouros(); }
	 * 
	 * public void buscarLogradouros() { if(this.objeto.getBairro() != null) {
	 * logradouroLista =
	 * logradouroService.buscaLogradouroByBairro(this.objeto.getBairro()); }
	 * else logradouroLista = Collections.emptyList(); }
	 */

	@Override
	protected List<IFilter> filtersBeforeSearch(List<IFilter> filters) {
		return filterController.doSimpleSearch("Cnpj", naSessionController.getNaCliente().getCnpj());
	}

	@Override
	protected void beforeUpdate() {
		super.beforeUpdate();
	}

	@Override
	public String update() {
		String pageNavegator = super.update();
		this.objeto = service.buscarCliente(this.objeto.getId());
		objeto.setBrasao(null);
		return pageNavegator;
	}

	@Override
	public String showFormDetail() {
		String nameForm = super.showFormDetail();
		objeto = service.buscarCliente(objeto.getId());
		return nameForm;
	}

	public List<NaCliente> buscarListaCliente() {
		return service.buscaListaDeClientes();
	}

	@Override
	public NaCliente newInstanceEntity() {
		NaCliente naCliente = super.newInstanceEntity();
		return naCliente;
	}

	@Override
	public void onRowSelect(SelectEvent event) {
		super.onRowSelect(event);
	}

	@Override
	public void setService(GenericService<NaCliente> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaCliente> getService() {
		return this.service;
	}


	@Override
	protected void beforeSave(NaCliente entity) {
		super.beforeSave(entity);
	}

	public void handleFileUpload(FileUploadEvent event) {
		file = event.getFile();

		byte[] brasao;
		try {
			brasao = IOUtils.toByteArray(file.getInputstream());
			this.objeto.setBrasao(brasao);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (file != null) {
			FacesMessage message = new FacesMessage("Concluido uploaded da imagem : " + event.getFile().getFileName());
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public boolean exibeImagem(byte[] bytes) {
		if (bytes == null) {
			return false;
		}
		return true;
	}

	public byte[] getImagemById(Integer id) {
		return service.getImagemById(id);
	}

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public List<EnumSituacaoAcesso> getListaSituacaoAcesso() {
		if (listaSituacaoAcesso.isEmpty()) {
			listaSituacaoAcesso = Arrays.asList(EnumSituacaoAcesso.values());
		}
		return listaSituacaoAcesso;
	}

	public void setListaSituacaoAcesso(List<EnumSituacaoAcesso> listaSituacaoAcesso) {
		this.listaSituacaoAcesso = listaSituacaoAcesso;
	}

	public List<EnumSimNao> getListaSituacaoAtivo() {
		if (listaSituacaoAtivo.isEmpty()) {
			listaSituacaoAtivo = Arrays.asList(EnumSimNao.values());
		}
		return listaSituacaoAtivo;
	}

	public void setListaSituacaoAtivo(List<EnumSimNao> listaSituacaoAtivo) {
		this.listaSituacaoAtivo = listaSituacaoAtivo;
	}

	public List<EnumTipoZona> getListaLogradouroTipoZona() {
		if (listaLogradouroTipoZona.isEmpty()) {
			listaLogradouroTipoZona = Arrays.asList(EnumTipoZona.values());
		}
		return listaLogradouroTipoZona;
	}

	public void setListaLogradouroTipoZona(List<EnumTipoZona> listaLogradouroTipoZona) {
		this.listaLogradouroTipoZona = listaLogradouroTipoZona;
	}

	public NaSessionController getNaSessionController() {
		return naSessionController;
	}

	public void setNaSessionController(NaSessionController naSessionController) {
		this.naSessionController = naSessionController;
	}

	public NaCidadeService getCidadeService() {
		return cidadeService;
	}

	public void setCidadeService(NaCidadeService cidadeService) {
		this.cidadeService = cidadeService;
	}

	public void setService(NaClienteService service) {
		this.service = service;
	}

	public NaBairroService getBairroService() {
		return bairroService;
	}

	public void setBairroService(NaBairroService bairroService) {
		this.bairroService = bairroService;
	}

	public List<NaLogradouro> getLogradouroLista() {
		return logradouroLista;
	}

	public void setLogradouroLista(List<NaLogradouro> logradouroLista) {
		this.logradouroLista = logradouroLista;
	}

	public void setCidadeLista(List<NaCidade> cidadeLista) {
		this.cidadeLista = cidadeLista;
	}

	public void setBairroLista(List<NaBairro> bairroLista) {
		this.bairroLista = bairroLista;
	}

	public List<NaCidade> getCidadeLista() {
		return cidadeLista;
	}

	public List<NaBairro> getBairroLista() {
		return bairroLista;
	}

	public NaLogradouroService getLogradouroService() {
		return logradouroService;
	}

	public void setLogradouroService(NaLogradouroService logradouroService) {
		this.logradouroService = logradouroService;
	}

}
