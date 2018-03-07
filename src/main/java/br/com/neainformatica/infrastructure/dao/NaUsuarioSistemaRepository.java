package br.com.neainformatica.infrastructure.dao;

import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.exception.NeaException;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NaUsuarioSistemaRepository extends GenericRepository<NaUsuarioSistema> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void addJoinFecth(Criteria criteria) {
		criteria.createAlias("usuario", "usuario");
		criteria.createAlias("sistema", "sistema");
	}

	@SuppressWarnings("unchecked")
	public List<NaUsuario> buscarUsuariosSistema(Integer idSistema) {
		try {
			Query query = entityManager.createQuery("select u from NaUsuarioSistema us join us.usuario u where us.sistema.id = :idSistema order by u.nome");
			query.setParameter("idSistema", idSistema);
			return query.getResultList();
		} catch (NoResultException e) {
			return new ArrayList<NaUsuario>();
		}
	}

	@SuppressWarnings("unchecked")
	public List<NaUsuarioSistema> buscarSistemasDoUsuario(NaUsuario usuario, NaCliente cliente) {

		StringBuilder sql = new StringBuilder();
		sql.append("select us ");
		sql.append("from NaUsuarioSistema us ");
		sql.append("  join fetch us.sistema s ");
		sql.append("  join fetch us.usuario u ");
		sql.append("where us.usuario = :usuario ");
		sql.append(" and us.naCliente = :cliente ");
		sql.append("  and us.ativo = :ativo");

		Query query = entityManager.createQuery(sql.toString());
		query.setParameter("usuario", usuario);
		query.setParameter("cliente", cliente);
		query.setParameter("ativo", EnumSimNao.SIM);
		return query.getResultList();

	}

	public NaUsuarioSistema buscaUsuarioSistema(NaUsuario usuario, NaSistema sistema, NaCliente cliente) {
		return (NaUsuarioSistema) getCriteriaDinamicSearch().add(Restrictions.eq("usuario", usuario)).add(Restrictions.eq("naCliente", cliente))
				.add(Restrictions.eq("sistema", sistema)).uniqueResult();
	}

	public NaUsuarioSistema obterUsuarioSistema(Integer codigoSistema, Integer codigoUsuario, NaCliente cliente) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("select us from NaUsuarioSistema us join fetch us.sistema s ");
			jpql.append("join fetch us.usuario u ");
			jpql.append("join fetch us.naCliente c ");
			jpql.append("where us.sistema.id = :idSistema and ");
			jpql.append("us.usuario.id = :idUsuario and us.naCliente = :cliente and us.ativo = :ativo");

			Query query = entityManager.createQuery(jpql.toString());
			query.setParameter("idSistema", codigoSistema);
			query.setParameter("idUsuario", codigoUsuario);
			query.setParameter("cliente", cliente);
			query.setParameter("ativo", EnumSimNao.SIM);
			return (NaUsuarioSistema) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}
	
	public NaUsuarioSistema obterUsuarioSistema(Integer codigoSistema, Integer codigoUsuario, Integer clienteId, EnumSimNao ativo) {
		try {
			StringBuilder jpql = new StringBuilder();
			jpql.append("select us from NaUsuarioSistema us join fetch us.sistema s ");
			jpql.append("join fetch us.usuario u ");
			jpql.append("join fetch us.naCliente c ");
			jpql.append("where us.sistema.id = :idSistema and ");
			jpql.append("us.usuario.id = :idUsuario and us.naCliente.id = :clienteId and us.ativo = :ativo");

			Query query = entityManager.createQuery(jpql.toString());
			query.setParameter("idSistema", codigoSistema);
			query.setParameter("idUsuario", codigoUsuario);
			query.setParameter("clienteId", clienteId);
			query.setParameter("ativo", ativo);
			return (NaUsuarioSistema) query.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public NaUsuarioSistema obterUsuarioSistema(Integer codigoSistema, Integer codigoUsuario, Integer idClienteNeA) {
		try {
			Query query = entityManager.createQuery(
					"select us from NaUsuarioSistema us join fetch us.sistema s  join fetch us.usuario u where us.sistema.id = :idSistema and us.usuario.id = :idUsuario"
							+ " and us.naCliente.idClienteNeA = :idClienteNeA");
			query.setParameter("idSistema", codigoSistema);
			query.setParameter("idUsuario", codigoUsuario);
			query.setParameter("idClienteNeA", idClienteNeA);
			return (NaUsuarioSistema) query.getSingleResult();
		} catch (NoResultException ex) {
			ex.printStackTrace();
			return null;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<NaSistema> obterSistemaDoUsarioLogado(NaUsuario usuario, NaCliente cliente) {
		StringBuilder jpql = new StringBuilder();

		jpql.append("select nus.sistema ");
		jpql.append(" from NaUsuarioSistema nus  ");
		jpql.append("where nus.usuario = :usuario  ");
		jpql.append("  and nus.naCliente = :cliente ");
		jpql.append("order by nus.sistema.sistema asc ");
		Query query = entityManager.createQuery(jpql.toString());
		query.setParameter("usuario", usuario);
		query.setParameter("cliente", cliente);

		return query.getResultList();
	}

	public EnumNivelUsuario obtemNivelMaximoAcessoUsuarioSistema(NaUsuario usuario) {

		Query query = entityManager.createQuery("select max(nus.nivelUsuario) from NaUsuarioSistema nus where nus.usuario = :usuario");
		query.setParameter("usuario", usuario);

		return (EnumNivelUsuario) query.getSingleResult();
	}

	@SuppressWarnings("unchecked")
	public List<NaSistema> obtemSistemasQueUsuarioNaoPossuiAcesso(NaUsuario usuario) {

		StringBuilder jpql = new StringBuilder();

		jpql.append("select s from NaSistema s where s not in ");
		jpql.append("( select us.sistema from NaUsuarioSistema us where us.usuario = :usuario )");

		Query query = entityManager.createQuery(jpql.toString());
		query.setParameter("usuario", usuario);

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<NaCliente> buscarClientesDoUsuario(NaUsuario naUsuario, NaSistema naSistema) {

		StringBuilder jpql = new StringBuilder();

		jpql.append("select distinct naus.naCliente from NaUsuarioSistema naus where naus.id > 0 ");

		if (naUsuario != null)
			jpql.append(" and naus.usuario = :naUsuario ");

		jpql.append("and naus.sistema = :naSistema ");
		jpql.append("and naus.ativo = :ativo ");

		Query query = entityManager.createQuery(jpql.toString());

		if (naUsuario != null)
			query.setParameter("naUsuario", naUsuario);

		query.setParameter("naSistema", naSistema);
		query.setParameter("ativo", EnumSimNao.SIM);

		return query.getResultList();
	}

	public NaUsuarioSistema buscarUsuarioSistema(NaUsuario naUsuario, NaSistema sistema, NaCliente naCliente) {

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT us ");
		sql.append("FROM NaUsuarioSistema us ");
		sql.append(" INNER JOIN FETCH us.sistema s ");
		sql.append(" INNER JOIN FETCH us.usuario u ");
		sql.append(" INNER JOIN FETCH us.naCliente cli ");
		sql.append("WHERE us.sistema = :sistema ");
		sql.append("  AND us.usuario = :usuario ");
		sql.append("  AND us.naCliente = :cliente ");

		try {

			Query query = entityManager.createQuery(sql.toString());
			query.setParameter("sistema", sistema);
			query.setParameter("usuario", naUsuario);
			query.setParameter("cliente", naCliente);
			return (NaUsuarioSistema) query.getSingleResult();

		} catch (NoResultException ex) {
			return null;
		}
	}

	@Override
	protected void beforeSave(NaUsuarioSistema usuarioSistema) throws NeaException {
		// Não posso injetar session no repository
		// NaSistema naSistema = sessionController.getNaSistema();
		// usuarioSistema.setOrigemSincronismo(naSistema.getBasePrincipal() ==
		// EnumSimNao.SIM ? EnumOrigemSincronismo.SERVER :
		// EnumOrigemSincronismo.CLIENTE);
	}

	public List<NaUsuarioSistema> buscarSistemasUsuario(NaUsuario usuario, NaCliente cliente) {

		StringBuilder jpql = new StringBuilder();
		jpql.append("select us  ");
		jpql.append("from NaUsuarioSistema us  ");
		jpql.append("  join fetch us.sistema s ");
		jpql.append("  join fetch us.usuario u ");
		jpql.append("where us.usuario = :usuario  ");
		jpql.append("  and us.naCliente = :cliente ");
		jpql.append("order by s.sistema ");

		Query query = entityManager.createQuery(jpql.toString());

		query.setParameter("usuario", usuario);
		query.setParameter("cliente", cliente);
		return query.getResultList();

	}

	public List<NaUsuarioSistema> buscarListaPorSistemaCliente(Integer idSistema, Integer idCliente) {

		Query query = entityManager.createQuery("SELECT us from NaUsuarioSistema as us join fetch us.usuario " +
				" where us.sistema.id = :idSistema and us.naCliente.id = :idCliente");

		query.setParameter("idSistema", idSistema);
		query.setParameter("idCliente", idCliente);
		return query.getResultList();

	}

	public void salvarSqlNativo(NaUsuarioSistema entity) throws NeaException {
		StringBuilder sqlNativo = new StringBuilder("INSERT INTO seguranca.na_usuario_sistema( ");
		sqlNativo.append("id_sistema, id_usuario, nivel_usuario, situacao_acesso, data_alteracao, id_na_cliente, ativo, origem_sincronismo) VALUES (");
		sqlNativo.append(entity.getSistema().getId());
		sqlNativo.append(", ");
		sqlNativo.append(entity.getUsuario().getId());
		sqlNativo.append(", ");
		sqlNativo.append(entity.getNivelUsuario().getId());
		sqlNativo.append(", '");
		sqlNativo.append(entity.getSituacaoAcessoHash());
		sqlNativo.append("', '");
		sqlNativo.append(entity.getDataAlteracao());
		sqlNativo.append("', ");
		sqlNativo.append(entity.getNaCliente().getId());
		sqlNativo.append(", '");
		sqlNativo.append(entity.getAtivo().toChar());
		sqlNativo.append("', '");
		sqlNativo.append(entity.getOrigemSincronismo().toChar());
		sqlNativo.append("');");

		getSession().createSQLQuery(sqlNativo.toString()).executeUpdate();
	}

	public List<NaUsuarioSistema> buscarSistemasUsuario(NaUsuario usuario) {

		StringBuilder jpql = new StringBuilder();
		jpql.append("select us  ");
		jpql.append("from NaUsuarioSistema us  ");
		jpql.append("  join fetch us.sistema s ");
		jpql.append("  join fetch us.usuario u ");
		jpql.append("  join fetch us.naCliente c ");
		jpql.append("where us.usuario = :usuario  ");
		jpql.append("order by s.sistema ");

		Query query = entityManager.createQuery(jpql.toString());

		query.setParameter("usuario", usuario);
		return query.getResultList();

	}

}
