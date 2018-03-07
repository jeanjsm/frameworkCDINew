package br.com.neainformatica.infrastructure.services;
/**
-----------------------------------------------
   @Empresa: N&A Informática Ltda
   @Gerador: MultiSource 
      Dados da Classe:
	@Data  = 25/04/2014 16:16:37
	@Author  = NELSON
	@Versão da Classe = 
-----------------------------------------------
 */


import java.io.Serializable;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaNacionalidadeRepository;
import br.com.neainformatica.infrastructure.entity.NaNacionalidade;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaNacionalidadeService extends GenericService<NaNacionalidade> implements NaServiceInterface, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	NaNacionalidadeRepository dao;

	@Override
	public GenericRepository<NaNacionalidade> getRepository() {
		return this.dao;
	}
	
}
