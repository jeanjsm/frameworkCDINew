package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaParametro;
import br.com.neainformatica.infrastructure.entity.NaParametroValor;
import br.com.neainformatica.infrastructure.entity.NaSistema;

public class NaParametroValorRepository extends GenericRepository<NaParametroValor> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}

	public NaParametroValor getValorByParametro(NaParametro parametro, String chave, NaSistema sistema) {
		entityManager.clear();
		try {
			Query query = entityManager.createQuery("select pv from NaParametroValor pv left join fetch pv.parametro p where pv.parametro = :parametro and pv.chave = :chave and p.sistema = :sistema");
			query.setParameter("parametro", parametro);
			query.setParameter("chave", chave);
			query.setParameter("sistema", sistema);

			return (NaParametroValor) query.getSingleResult();
		} catch (Exception e) {
			return null;
		}
	}
	

	public List<NaParametroValor> pesquisarParametroValor(NaSistema sistema, NaParametro parametro){
		try{
			Query query = entityManager.createQuery("select pv from NaParametroValor pv "
					+ " join fetch pv.parametro p "
					+ " join fetch p.grupo g"
					+ " where p.sistema = :sistema "
					+ " and pv.parametro = :parametro");
			query.setParameter("sistema", sistema);
			query.setParameter("parametro", parametro);
			
		    List<NaParametroValor> naParametroValor = query.getResultList();
		    return naParametroValor;
		}catch (NoResultException e) {
			return null;
		}
	}
	
	public NaParametroValor pesquisarParametroValorFixo(NaSistema sistema, NaParametro parametro){
		try{
			Query query = entityManager.createQuery("select pv from NaParametroValor pv "
					+ " join fetch pv.parametro p "
					+ " join fetch p.grupo g "
					+ " join fetch p.sistema s "
					+ " where p.sistema = :sistema "
					+ " and pv.parametro = :parametro");
			query.setParameter("sistema", sistema);
			query.setParameter("parametro", parametro);
			
		    NaParametroValor naParametroValor = (NaParametroValor) query.getSingleResult();
		    return naParametroValor;
		}catch (NoResultException e) {
			return null;
		}
	}
	
	public NaParametroValor pesquisarParametroValorPorNome(String chave){
		try{
			Query query = entityManager.createQuery("select pv from NaParametroValor pv "
					+ " join fetch pv.parametro p "
					+ " join fetch p.grupo g "
					+ " join fetch p.sistema s "
					+ " where p.chave = :chave ");
			query.setParameter("chave", chave);
			
		    NaParametroValor naParametroValor = (NaParametroValor) query.getSingleResult();
		    return naParametroValor;
		}catch (NoResultException e) {
			return null;
		}
	}

}
