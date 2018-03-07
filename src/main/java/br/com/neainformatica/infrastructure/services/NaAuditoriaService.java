package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaAuditoriaRepository;
import br.com.neainformatica.infrastructure.entity.NaAuditoria;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaAuditoriaService extends GenericService<NaAuditoria> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;
	
	@Inject
	private NaAuditoriaRepository dao;
			
	@Override
	public GenericRepository<NaAuditoria> getRepository() {
		return this.dao;
	}	
}
