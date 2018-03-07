package br.com.neainformatica.infrastructure.controller;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.neainformatica.infrastructure.entity.NaPermissao;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaPermissaoService;

@Named
@ConversationScoped
public class NaPermissaoController extends GenericController<NaPermissao> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	private NaPermissaoService service;

	@Override
	public void setService(GenericService<NaPermissao> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaPermissao> getService() {
		return this.service;
	}

}
