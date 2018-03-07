package br.com.neainformatica.infrastructure.services;

/**
 -----------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 25/04/2014 16:16:52
 @Author  = NELSON
 @Versão da Classe = 
 -----------------------------------------------
 */

import java.io.Serializable;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaTipoCepRepository;
import br.com.neainformatica.infrastructure.entity.NaTipoCep;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaTipoCepService extends GenericService<NaTipoCep> implements NaServiceInterface, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	NaTipoCepRepository dao;

	@Override
	public GenericRepository<NaTipoCep> getRepository() {
		return this.dao;
	}
}
