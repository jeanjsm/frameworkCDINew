package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;

import br.com.neainformatica.infrastructure.entity.NaCliente;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaAtor;
import br.com.neainformatica.infrastructure.entity.NaPermissao;
import br.com.neainformatica.infrastructure.entity.NaSistema;

public class NaPermissaoRepository extends GenericRepository<NaPermissao> implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Retorna uma lista de NaPermissao ja cadastrada para o ator passado como
	 * parâmetro
	 * 
	 * @param ator
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NaPermissao> buscarPermissoes(NaAtor ator, NaSistema sistema) {

		StringBuilder sql = new StringBuilder();
		sql.append("select p ");
		sql.append("from NaAtorPermissao ap ");
		sql.append("  join ap.permissao p ");
		sql.append("where ap.ator = :ator and p.sistema = :sistema ");

		Query query = getSession().createQuery(sql.toString());
		query.setParameter("ator", ator);
		query.setParameter("sistema", sistema);

		return query.list();
	}
	
	public List<NaPermissao> buscarPermissoesSistema(NaSistema sistema, NaCliente naCliente) {

		String sql = "select p from NaPermissao p where p.sistema = :sistema and p.cliente is null ";
		if(naCliente != null)
			sql = sql + " or p.cliente.id = :cliente";
		Query query = getSession().createQuery(sql);
		query.setParameter("sistema", sistema);
		if(naCliente != null)
			query.setParameter("cliente", naCliente.getId());

		return query.list();
	}

	/**
	 * Busca uma permissão por nome, dentro das permissões do sistema informado
	 * 
	 * @param sistema
	 * @return
	 */
	public NaPermissao buscarPermissao(String chave, NaSistema sistema) {

		Query query = getSession().createQuery("select p from NaPermissao p where p.chave = :chave and p.sistema = :sistema");
		query.setParameter("chave", chave);
		query.setParameter("sistema", sistema);

		NaPermissao permissao;
		try {
			permissao = (NaPermissao) query.uniqueResult();
		} catch (NoResultException e) {
			return null;
		}

		return permissao;

	}

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("chave"));
	}

}
