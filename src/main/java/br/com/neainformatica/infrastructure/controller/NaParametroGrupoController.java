package br.com.neainformatica.infrastructure.controller;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.neainformatica.infrastructure.entity.NaParametroGrupo;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaParametroGrupoService;

@Named
@ConversationScoped
public class NaParametroGrupoController extends GenericController<NaParametroGrupo> implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private NaParametroGrupoService service;
	
	@Inject
	private NaSessionController sessionController;
	
	
	@Override
	public void setService(GenericService<NaParametroGrupo> service){
		super.setService(this.service);
	}
	
	@Override
	public GenericService<NaParametroGrupo> getService() {
		return this.service;
	}
	
	public List<NaParametroGrupo> listarParametroGrupo(){
		return service.listarParametroGrupo(sessionController.getNaSistema());
		
	}

	
	
	

}
