package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaGestao;

public class NaGestaoRepository extends GenericRepository<NaGestao> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("descricao"));
	}
}
