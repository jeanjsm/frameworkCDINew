package br.com.neainformatica.infrastructure.services;
/**
-----------------------------------------------
   @Empresa: N&A Informática Ltda
   @Gerador: MultiSource 
      Dados da Classe:
	@Data  = 25/04/2014 16:16:19
	@Author  = NELSON
	@Versão da Classe = 
-----------------------------------------------
 */


import java.io.Serializable;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaPaisRepository;
import br.com.neainformatica.infrastructure.entity.NaPais;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaPaisService extends GenericService<NaPais> implements NaServiceInterface, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	NaPaisRepository dao;

	@Override
	public GenericRepository<NaPais> getRepository() {
		return this.dao;
	}

}
