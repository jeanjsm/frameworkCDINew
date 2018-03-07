package br.com.neainformatica.infrastructure.services;
/**
-----------------------------------------------
   @Empresa: N&A Informática Ltda
   @Gerador: MultiSource 
      Dados da Classe:
	@Data  = 25/04/2014 16:17:35
	@Author  = NELSON
	@Versão da Classe = 
-----------------------------------------------
 */


import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaBairroRepository;
import br.com.neainformatica.infrastructure.entity.NaBairro;
import br.com.neainformatica.infrastructure.entity.NaCidade;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaBairroService extends GenericService<NaBairro> implements NaServiceInterface, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	NaBairroRepository dao;

	@Override
	public GenericRepository<NaBairro> getRepository() {
		return this.dao;
	}

	public List<NaBairro> buscaBairrosByCidade(NaCidade cidade) {
		return dao.buscaBairrosByCidade(cidade);		
	}
	
}