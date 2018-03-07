package br.com.neainformatica.infrastructure.dao;
/**
 -----------------------------------------------------------------------------------------------------------------------------
  @Empresa: N&A Informática Ltda
  @Gerador: MultiSource 
  Dados da Classe:
	@Data  = 18/07/2014 18:18:07
	@Author  = NELSON
	@Versão da Classe = 4
 -----------------------------------------------------------------------------------------------------------------------------
 */


import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaEstado;
import br.com.neainformatica.infrastructure.dao.GenericRepository;

public class NaEstadoRepository extends GenericRepository<NaEstado> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}
}
