package br.com.neainformatica.infrastructure.services;
/**
-----------------------------------------------
   @Empresa: N&A Informática Ltda
   @Gerador: MultiSource 
      Dados da Classe:
	@Data  = 28/04/2014 09:00:36
	@Author  = NELSON
	@Versão da Classe = 
-----------------------------------------------
 */


import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaLogradouroRepository;
import br.com.neainformatica.infrastructure.entity.NaBairro;
import br.com.neainformatica.infrastructure.entity.NaLogradouro;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaLogradouroService extends GenericService<NaLogradouro> implements NaServiceInterface, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	NaLogradouroRepository dao;

	@Override
	public GenericRepository<NaLogradouro> getRepository() {
		return this.dao;
	}

	public List<NaLogradouro> buscaLogradouroByBairro(NaBairro bairro) {
		return dao.buscaLogradouroByBairro(bairro);
	}

}
