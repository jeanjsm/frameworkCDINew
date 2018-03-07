package br.com.neainformatica.infrastructure.controller;

/**
 -----------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 21/07/2014 10:35:29
 @Author  = NELSON
 @Versão da Classe = 
 -----------------------------------------------
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import br.com.neainformatica.infrastructure.entity.NaCidade;
import br.com.neainformatica.infrastructure.entity.NaEstado;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LikeFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaCidadeService;

@Named
@ConversationScoped
public class NaCidadeController extends GenericController<NaCidade> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaCidadeService service;

	@Override
	public void setService(GenericService<NaCidade> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaCidade> getService() {
		return this.service;
	}

	@PostConstruct
	public void createDynamicColumns() {
		// monta a lista dinamica de colunas para exibicao no templateList.xhtml
		// define quais coluna aparecem na grid de listagem
		clearColumns().addColumn(new ColumnModel("Descrição", "descricao")).addColumn(new ColumnModel("Estado", "estadoSigla"));
	}

	@Override
	public NaCidade newInstanceEntity() {
		// inicializa o novo objeto na inclusão, para não gerar erro de
		// referencia a objeto nulo na view.
		NaCidade naCidade = super.newInstanceEntity();

		naCidade.setEstado(new NaEstado());

		/*
		 * Definir valores padrão ao criar um objeto novo
		 */

		return naCidade;
	}

	/*
	 * @Override public void repeteValue(){ if (backupObjeto != null) { } }
	 */

	/**
	 * Evento disparado quando um registro é selecionado na listagem
	 */
	@Override
	public void onRowSelect(SelectEvent event) {

		super.onRowSelect(event);

		/*
		 * Regras a serem executadas ao selecionar um registro na listagem
		 */

	}

	@Override
	public String salvar() {
		return super.salvar();
	}
	
	public List<NaCidade> buscarAutoComplete(String query){
		
		List<IFilter> filters = new ArrayList<>();
		filters.add(new LikeFilter("descricao", query, "cidade"));
		
		return service.dinamicSearch(filters, 0, 20);
		
	}
}
