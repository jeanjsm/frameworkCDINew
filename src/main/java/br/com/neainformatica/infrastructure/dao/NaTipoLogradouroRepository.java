package br.com.neainformatica.infrastructure.dao;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 25/04/2014 16:16:52
 @Author  = NELSON
 @Versão da Classe = 4
 -----------------------------------------------------------------------------------------------------------------------------
 */

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaTipoLogradouro;

public class NaTipoLogradouroRepository extends GenericRepository<NaTipoLogradouro> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}
}
