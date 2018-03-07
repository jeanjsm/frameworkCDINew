package br.com.neainformatica.infrastructure.dao;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 28/04/2014 09:00:36
 @Author  = NELSON
 @Versão da Classe = 4
 -----------------------------------------------------------------------------------------------------------------------------
 */

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaTipoCep;

public class NaTipoCepRepository extends GenericRepository<NaTipoCep> implements Serializable{

	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}
}
