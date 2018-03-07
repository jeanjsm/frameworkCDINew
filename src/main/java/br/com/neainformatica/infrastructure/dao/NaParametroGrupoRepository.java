package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaParametroGrupo;
import br.com.neainformatica.infrastructure.entity.NaSistema;

public class NaParametroGrupoRepository extends GenericRepository<NaParametroGrupo> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}

	public NaParametroGrupo findByNome(String nome, NaSistema sistema) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select g ");
			sql.append("from NaParametroGrupo g ");
			sql.append(" where g.nome = :nome");
			sql.append("   and g.sistema = :sistema");

			Query qry = entityManager.createQuery(sql.toString());

			qry.setParameter("nome", nome);
			qry.setParameter("sistema", sistema);

			return (NaParametroGrupo) qry.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
