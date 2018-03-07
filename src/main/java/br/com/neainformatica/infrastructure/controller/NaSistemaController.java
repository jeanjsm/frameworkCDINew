package br.com.neainformatica.infrastructure.controller;

import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaSistemaService;

@Named
@ConversationScoped
public class NaSistemaController extends GenericController<NaSistema> implements
		Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NaSistemaService service;

	@Override
	public void setService(GenericService<NaSistema> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaSistema> getService() {
		return this.service;
	}

}
