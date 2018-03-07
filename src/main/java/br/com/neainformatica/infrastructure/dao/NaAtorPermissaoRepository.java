package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;

import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.entity.NaAtor;
import br.com.neainformatica.infrastructure.entity.NaAtorPermissao;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaPermissao;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.exception.NeaException;

public class NaAtorPermissaoRepository extends GenericRepository<NaAtorPermissao> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaSessionController naSessionControler;

	/**
	 * Retorna uma lista de permissões do ator passado como parâmetro
	 * 
	 * @param atores
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NaAtorPermissao> buscaAtorPermissao(NaAtor ator, NaCliente naCliente) {

		String sql = "select ap from NaAtorPermissao ap left join fetch ap.permissao p where ap.ator = :ator and ap.cliente = :cliente order by p.descricao ";

		Query query = getSession().createQuery(sql);
		query.setParameter("ator", ator);
		query.setParameter("cliente", naCliente);

		return (List<NaAtorPermissao>) query.list();
	}

	public List<NaAtorPermissao> buscaAtorPermissao(Integer idNaAtor, Integer idNaCliente) {

		String sql = "select ap from NaAtorPermissao ap left join fetch ap.permissao p where ap.ator.id = :idAtor and ap.cliente.id = :idCliente order by p.descricao ";

		Query query = getSession().createQuery(sql);
		query.setParameter("idAtor", idNaAtor);
		query.setParameter("idCliente", idNaCliente);

		return (List<NaAtorPermissao>) query.list();
	}

	/**
	 * Retorna uma lista de permissões considerando todos os atores passados
	 * como parametros
	 * 
	 * @param atores
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NaAtorPermissao> buscaAtorPermissao(List<NaAtor> atores, NaCliente cliente) {

		StringBuilder sql = new StringBuilder();
		sql.append("select ap ");
		sql.append("from NaAtorPermissao ap ");
		sql.append("  join ap.permissao p ");
		sql.append("where ap.ator in ( :ator ) ");
		sql.append("  and ap.cliente = :cliente ");
		sql.append("order by p.descricao ");

		Query query = getSession().createQuery(sql.toString());
		query.setParameterList("ator", atores);
		query.setParameter("cliente", cliente);
		query.setCacheable(false);

		return (List<NaAtorPermissao>) query.list();

	}

	@SuppressWarnings("unchecked")
	public List<NaAtorPermissao> buscaPermissao(String chavePermissao, NaAtor ator, NaSistema sistema) {

		StringBuilder sql = new StringBuilder();
		sql.append("select per from NaAtorPermissao per where per.permissao.chave = :chavePermissao and per.permissao.sistema = :sistema");
		sql.append(" and per.ator = :ator");

		javax.persistence.Query query = entityManager.createQuery(sql.toString());

		query.setParameter("chavePermissao", chavePermissao);
		query.setParameter("sistema", sistema);
		query.setParameter("ator", ator);

		return query.getResultList();
	}

	public void apagarPermissoes(NaAtor ator) {
		Query query = getSession().createQuery("delete from NaAtorPermissao ap where ap.ator = :ator");
		query.setParameter("ator", ator);
		query.executeUpdate();
	}

	@Override
	protected void beforeSave(NaAtorPermissao atorPermissao) throws NeaException {
		// atorPermissao.setCliente(naSessionControler.getNaCliente());
		super.beforeSave(atorPermissao);
	}
}