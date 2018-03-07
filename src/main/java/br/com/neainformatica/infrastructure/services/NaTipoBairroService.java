package br.com.neainformatica.infrastructure.services;
/**
-----------------------------------------------
   @Empresa: N&A Informática Ltda
   @Gerador: MultiSource 
      Dados da Classe:
	@Data  = 25/04/2014 16:17:04
	@Author  = NELSON
	@Versão da Classe = 
-----------------------------------------------
 */


import java.io.Serializable;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaTipoBairroRepository;
import br.com.neainformatica.infrastructure.entity.NaTipoBairro;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaTipoBairroService extends GenericService<NaTipoBairro> implements NaServiceInterface, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	NaTipoBairroRepository dao;

	@Override
	public GenericRepository<NaTipoBairro> getRepository() {
		return this.dao;
	}

}