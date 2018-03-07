package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaSistemaRepository;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaSistemaService extends GenericService<NaSistema> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaSistemaRepository dao;

	@Inject
	private InfrastructureController infrastructureController;

	@Override
	public GenericRepository<NaSistema> getRepository() {
		return this.dao;
	}
	
	public void setRepository(NaSistemaRepository dao) {
		this.dao = dao;
	}

	public NaSistema buscarSistema(Integer idSistema) {
		return dao.buscarSistema(idSistema);
	}

	public NaSistema verificaSistemaCadastrado(Integer idSistema, String sistema) {
		NaSistema naSistema;

		naSistema = buscarSistema(idSistema);

		if (naSistema == null) {
			naSistema = new NaSistema(idSistema, sistema);
			naSistema.setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);
			naSistema = noAudit().save(naSistema);
		}
		return naSistema;

	}

	public List<NaSistema> buscarSistemaUsuario(NaUsuario usuario, NaCliente cliente) {
		return dao.buscarSistemaUsuario(usuario, cliente);
	}

	@Override
	protected void beforeSave(NaSistema entity) {
		entity.setDataAlteracao(new Date());

		NaSistema sistema = infrastructureController.getNaSistema();

//		if (sistema != null) {
//
//			if (sistema.getBasePrincipal() == null || sistema.getBasePrincipal().equals(EnumSimNao.NAO))
//				entity.setOrigemSincronismo(EnumOrigemSincronismo.CLIENTE);
//			else
//				entity.setOrigemSincronismo(EnumOrigemSincronismo.SERVER);
//		}

		super.beforeSave(entity);
	}

}