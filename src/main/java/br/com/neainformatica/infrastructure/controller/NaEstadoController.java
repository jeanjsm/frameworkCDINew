package br.com.neainformatica.infrastructure.controller;

/**
 -----------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 18/07/2014 18:18:07
 @Author  = NELSON
 @Versão da Classe = 
 -----------------------------------------------
 */

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import br.com.neainformatica.infrastructure.entity.NaEstado;
import br.com.neainformatica.infrastructure.entity.NaPais;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaEstadoService;

@Named
@ConversationScoped
public class NaEstadoController extends GenericController<NaEstado> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaEstadoService service;

	@Override
	public void setService(GenericService<NaEstado> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaEstado> getService() {
		return this.service;
	}

	@PostConstruct
	public void createDynamicColumns() {
		// monta a lista dinamica de colunas para exibicao no templateList.xhtml
		// define quais coluna aparecem na grid de listagem
		clearColumns().addColumn(new ColumnModel("Código do IBGE", "ibge")).addColumn(new ColumnModel("Nome", "nome")).addColumn(new ColumnModel("Sigla", "sigla"))
				.addColumn(new ColumnModel("Nome Formal", "nomeFormal"));
	}

	@Override
	public NaEstado newInstanceEntity() {
		// inicializa o novo objeto na inclusão, para não gerar erro de
		// referencia a objeto nulo na view.
		NaEstado naEstado = super.newInstanceEntity();
		
		naEstado.setPais(new NaPais());

		/*
		 * Definir valores padrão ao criar um objeto novo
		 */

		return naEstado;
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
		// TODO Auto-generated method stub
		return super.salvar();
	}

}
