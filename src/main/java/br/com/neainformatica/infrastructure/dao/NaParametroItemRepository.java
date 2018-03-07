package br.com.neainformatica.infrastructure.dao;
/**
 -----------------------------------------------------------------------------------------------------------------------------
  @Empresa: N&A Informática Ltda
  @Gerador: MultiSource 
  Dados da Classe:
	@Data  = 08/05/2014 13:23:24
	@Author  = NELSON
	@Versão da Classe = 4
 -----------------------------------------------------------------------------------------------------------------------------
 */


import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaParametroItem;

public class NaParametroItemRepository extends GenericRepository<NaParametroItem> {
	
	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}
}
