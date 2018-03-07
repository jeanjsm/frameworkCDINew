package br.com.neainformatica.infrastructure.controller;

/**
 -----------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 25/04/2014 16:17:34
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

import br.com.neainformatica.infrastructure.entity.NaBairro;
import br.com.neainformatica.infrastructure.entity.NaTipoBairro;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaBairroService;

@Named
@ConversationScoped
public class NaBairroController extends GenericController<NaBairro> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NaBairroService service;

	@PostConstruct
	public void createDynamicColumns() {
		// monta a lista dinamica de colunas para exibicao no templateList.xhtml
		// define quais coluna aparecem na grid de listagem
		clearColumns().addColumn(new ColumnModel("Descricao", "descricao"));

	}

	@Override
	public NaBairro newInstanceEntity() {
		// inicializa o novo objeto na inclusão, para não gerar erro de
		// referencia a objeto nulo na view.
		NaBairro naBairro = super.newInstanceEntity();
		
		naBairro.setTipoBairro(new NaTipoBairro());

		/*
		 * Definir valores padrão ao criar um objeto novo
		 */

		return naBairro;
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
	public void setService(GenericService<NaBairro> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaBairro> getService() {
		return this.service;
	}

}
