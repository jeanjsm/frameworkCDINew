package br.com.neainformatica.infrastructure.controller;

/**
 -----------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 23/12/2014 09:20:26
 @Author  = Eduardo Leite Ranzzani
 @Versão da Classe = 
 -----------------------------------------------
 */

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.SelectEvent;

import br.com.neainformatica.infrastructure.entity.NaAlteracaoSenha;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaAlteracaoSenhaService;

@Named
@ConversationScoped
public class NaAlteracaoSenhaController extends GenericController<NaAlteracaoSenha> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaAlteracaoSenhaService service;

	@Override
	public void setService(GenericService<NaAlteracaoSenha> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaAlteracaoSenha> getService() {
		return this.service;
	}

	@PostConstruct
	public void createDynamicColumns() {
		// monta a lista dinamica de colunas para exibicao no templateList.xhtml
		// define quais coluna aparecem na grid de listagem
		clearColumns().addColumn(new ColumnModel("ChaveValidacao", "chaveValidacao")).addColumn(new ColumnModel("Invalido", "invalido"));
	}

	@Override
	public NaAlteracaoSenha newInstanceEntity() {
		// inicializa o novo objeto na inclusão, para não gerar erro de
		// referencia a objeto nulo na view.
		NaAlteracaoSenha naAlteracaoSenha = super.newInstanceEntity();

		/*
		 * Definir valores padrão ao criar um objeto novo
		 */

		return naAlteracaoSenha;
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

	public NaAlteracaoSenha salvarHash(NaUsuario naUsuario) {
		return service.salvarHash(naUsuario);
	}

}
