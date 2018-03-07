package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaGestaoRepository;
import br.com.neainformatica.infrastructure.entity.NaGestao;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaGestaoService extends GenericService<NaGestao> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	NaGestaoRepository dao;

	@Override
	public GenericRepository<NaGestao> getRepository() {
		return this.dao;
	}

}
