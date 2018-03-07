package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

public class NaClienteRepository extends GenericRepository<NaCliente> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("nome"));
		criteria.setFetchMode("tipologradouro", FetchMode.JOIN);
		criteria.setFetchMode("logradouro", FetchMode.JOIN);
		criteria.setFetchMode("tipobairro", FetchMode.JOIN);
		criteria.setFetchMode("bairro", FetchMode.JOIN);
		criteria.setFetchMode("cidadelogradouro", FetchMode.JOIN);
		criteria.setFetchMode("uf", FetchMode.JOIN);
	}

	/**
	 * Sempre vai existir um único cliente por sistema, então nao preciso passar
	 * parametro para localizar, basta ver se existe algum.
	 *
	 * @author elielcio.santos
	 * @return Cliente
	 */
	public NaCliente buscarCliente() {
		try {
			return entityManager.createQuery("select o from NaCliente o left join fetch o.bairro b", NaCliente.class).getResultList().get(0);
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Devido a necessidade de ter mais de um cliente por banco foi preciso
	 * criar método que retornava um ResultList pois agora pode-se ter uma lista
	 * de clientes.
	 *
	 * @author diego.silva
	 * @return List
	 */
	public List<NaCliente> buscarListaCliente() {

		return findAll();
	}

	public NaCliente buscarClientePorUsuario(NaUsuario naUsuario) {

		try {
			Query query = entityManager
					.createQuery("SELECT DISTINCT(clie) FROM NaUsuarioSistema AS ususis INNER JOIN ususis.usuario AS usu INNER JOIN USUSIS.naCliente AS clie WHERE usu = :usuario");
			query.setParameter("usuario", naUsuario);

			return (NaCliente) query.getResultList().get(0);
		} catch (NoResultException e) {
			log.error("Não foi possível encontrar nenhum NaCliente para o usuario: " + naUsuario, e);
			return null;
		}
	}

	public NaCliente buscarCliente(String cnpj, Integer idClienteNeA) {

		StringBuilder jpql = new StringBuilder();

		jpql.append("select c from NaCliente c ");
		jpql.append("left join fetch c.tipologradouro tl ");
		jpql.append("left join fetch c.logradouro l ");
		jpql.append("left join fetch c.tipobairro t ");
		jpql.append("left join fetch c.bairro b ");
		jpql.append("left join fetch c.cidadelogradouro cl ");
		jpql.append("left join fetch c.uf uf ");
		jpql.append("where c.cnpj = :cnpj and c.idClienteNeA = :idClienteNeA");

		Query query = entityManager.createQuery(jpql.toString());
		query.setParameter("cnpj", cnpj);
		query.setParameter("idClienteNeA", idClienteNeA);

		try {
			return (NaCliente) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param idClienteNeA
	 * @return
	 */
	public NaCliente buscarClientePorIdNea(Integer idClienteNeA) {

		List<IFilter> filtros = new ArrayList<IFilter>();
		filtros.add(new EqualFilter("idClienteNeA", idClienteNeA, null));

		List<NaCliente> clientes = dinamicSearch(filtros);

		if (clientes.size() == 0)
			return null;

		return clientes.get(0);

	}

	public byte[] getImagemById(Integer id) {
		Criteria consult;
		consult = createCriteria().add(Restrictions.eq("id", id));
		NaCliente cliente = (NaCliente) consult.uniqueResult();

		if (cliente != null)
			return cliente.getBrasao();
		return null;
	}
	
	public NaCliente buscarCliente(Integer id) {
		try {
			StringBuilder jpql = new StringBuilder();
			
			jpql.append("select o from NaCliente o left join fetch o.bairro b left join fetch o.tipologradouro tl ");
			jpql.append(" left join fetch o.logradouro l left join fetch o.tipobairro tb left join fetch o.cidadelogradouro cl ");
			jpql.append(" left join fetch o.uf uf ");
			jpql.append(" where o.id = :id");
			
			return entityManager.createQuery(jpql.toString(), NaCliente.class)
					.setParameter("id", id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
