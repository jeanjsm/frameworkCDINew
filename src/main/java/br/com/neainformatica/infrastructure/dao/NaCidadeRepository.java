package br.com.neainformatica.infrastructure.dao;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 21/07/2014 10:35:29
 @Author  = NELSON
 @Versão da Classe = 4
 -----------------------------------------------------------------------------------------------------------------------------
 */

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaCidade;
import br.com.neainformatica.infrastructure.entity.NaEstado;

public class NaCidadeRepository extends GenericRepository<NaCidade> implements Serializable{

	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}

	@Override
	protected void addJoinFecth(Criteria criteria) {
		criteria.createAlias("estado", "estado");
	}

	@SuppressWarnings("unchecked")
	public List<NaCidade> buscaCidadesByEstado(NaEstado estado) {
		Query query = entityManager.createQuery("select c from NaCidade c where c.estado = :estado");
		query.setParameter("estado", estado);

		return query.getResultList();
	}
}
