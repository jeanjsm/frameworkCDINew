package br.com.neainformatica.infrastructure.controller;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.neainformatica.infrastructure.entity.NaPerfil;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaPerfilService;
import br.com.neainformatica.infrastructure.services.NaPermissaoService;

@Named
@ConversationScoped
public class NaPerfilController extends GenericController<NaPerfil> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaPerfilService service;

	@Inject
	NaPermissaoService naPermissaoService;

	@Inject
	private InfrastructureController infrastructureController;

	@Override
	public void setService(GenericService<NaPerfil> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaPerfil> getService() {
		return this.service;
	}

	@Override
	protected List<IFilter> filtersBeforeSearch(List<IFilter> filters) {
		filters.add(new EqualFilter("sistema", infrastructureController.getNaSistema(), ""));
		if (naSessionController != null)
			filters.add(new EqualFilter("cliente", naSessionController.getNaCliente(), ""));

		return super.filtersBeforeSearch(filters);
	}

	@Override
	protected void beforeSave(NaPerfil objeto) {

		if (objeto.getCliente() == null)
			objeto.setCliente(naSessionController.getNaCliente());

	}

	@Override
	protected void afterCreate() {
		this.objeto.setSistema(infrastructureController.getNaSistema());
	}

	@Override
	public void delete() {
		if (!permiteManipularPerfil())
			return;
		super.delete();
	}

	@Override
	public String update() {
		if (!permiteManipularPerfil())
			return "";
		return super.update();
	}

	private boolean permiteManipularPerfil() {
		if (this.objeto.getPadrao() == EnumSimNao.SIM) {
			messages.addErrorMessage("Este perfil está definido como padrão e não pode ser alterado/removido!");
			return false;
		}
		return true;
	}

}