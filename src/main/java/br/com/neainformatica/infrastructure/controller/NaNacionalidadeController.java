package br.com.neainformatica.infrastructure.controller;

/**
 -----------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 29/04/2014 13:04:36
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

import br.com.neainformatica.infrastructure.entity.NaNacionalidade;
import br.com.neainformatica.infrastructure.entity.NaPais;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaNacionalidadeService;

@Named
@ConversationScoped
public class NaNacionalidadeController extends GenericController<NaNacionalidade> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaNacionalidadeService service;

	@Override
	public void setService(GenericService<NaNacionalidade> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaNacionalidade> getService() {
		return this.service;
	}

	@PostConstruct
	public void createDynamicColumns() {
		// monta a lista dinamica de colunas para exibicao no templateList.xhtml
		// define quais coluna aparecem na grid de listagem
		clearColumns().addColumn(new ColumnModel("Nome", "nome"));
	}

	@Override
	public NaNacionalidade newInstanceEntity() {
		// inicializa o novo objeto na inclusão, para não gerar erro de
		// referencia a objeto nulo na view.
		NaNacionalidade naNacionalidade = super.newInstanceEntity();

		/*
		 * Definir valores padrão ao criar um objeto novo
		 */
		naNacionalidade.setPais(new NaPais());

		return naNacionalidade;
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

}
