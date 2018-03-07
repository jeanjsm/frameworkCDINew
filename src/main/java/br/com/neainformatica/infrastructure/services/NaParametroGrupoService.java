package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaParametroGrupoRepository;
import br.com.neainformatica.infrastructure.entity.NaParametroGrupo;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaParametroGrupoService extends GenericService<NaParametroGrupo> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaParametroGrupoRepository dao;

	@Override
	public GenericRepository<NaParametroGrupo> getRepository() {
		return this.dao;
	}

	public List<NaParametroGrupo> listarParametroGrupo(NaSistema sistema) {
		return dao.findByParam(sistema, "sistema");
	}

	public NaParametroGrupo findByNome(String nome, NaSistema sistema) {
		return dao.findByNome(nome, sistema);
	}

}
