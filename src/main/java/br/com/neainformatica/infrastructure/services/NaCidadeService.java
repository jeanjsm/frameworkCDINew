package br.com.neainformatica.infrastructure.services;
/**
-----------------------------------------------
   @Empresa: N&A Informática Ltda
   @Gerador: MultiSource 
      Dados da Classe:
	@Data  = 25/04/2014 16:28:55
	@Author  = NELSON
	@Versão da Classe = 
-----------------------------------------------
 */


import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaCidadeRepository;
import br.com.neainformatica.infrastructure.entity.NaCidade;
import br.com.neainformatica.infrastructure.entity.NaEstado;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaCidadeService extends GenericService<NaCidade> implements NaServiceInterface, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Inject
	NaCidadeRepository dao;

	@Override
	public GenericRepository<NaCidade> getRepository() {
		return this.dao;
	}
	
	public List<NaCidade> buscaCidadesByEstado(NaEstado estado){
		return dao.buscaCidadesByEstado(estado);
	}

}