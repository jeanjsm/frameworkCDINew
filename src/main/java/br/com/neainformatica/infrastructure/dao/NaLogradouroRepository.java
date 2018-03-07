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
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaBairro;
import br.com.neainformatica.infrastructure.entity.NaLogradouro;

public class NaLogradouroRepository extends GenericRepository<NaLogradouro> implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}

	@SuppressWarnings("unchecked")
	public List<NaLogradouro> buscaLogradouroByBairro(NaBairro bairro) {
		System.out.println("entrou buscar logradouro");
		Query query = entityManager.createQuery("select l from NaLogradouro l where l.bairroIni = :bairro or l.bairroFim = :bairro");
		query.setParameter("bairro", bairro);
		return query.getResultList();
	}
}
