package br.com.neainformatica.infrastructure.dao;

import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.entity.NaAtor;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaPerfil;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioPerfil;
import br.com.neainformatica.infrastructure.enumeration.EnumNaUsuarioOrigemAlteracao;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoAtor;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.searchengine.services.EngineCriteria;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.proxy.HibernateProxy;

public class NaUsuarioRepository extends GenericRepository<NaUsuario> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	NaAtorRepository atorRepository;

	@Inject
	NaUsuarioPerfilRepository naUsuarioPerfil;

	@Inject
	NaSessionController naSessionController;

	@Override
	protected void addJoinFecth(Criteria criteria) {
		criteria.createAlias("ator", "ator");
	}

	public NaUsuario buscaUsuarioLogin(String userName) {

		try {
			entityManager.clear();
			Query query = entityManager.createQuery("select u from NaUsuario u left join u.ator a where lower(u.login) = :login");
			query.setParameter("login", userName.toLowerCase());
			query.setHint("org.hibernate.cacheable", false);

			return (NaUsuario) query.getSingleResult();
		} catch (NoResultException e) {

			return null;
		}
	}

	public List<NaUsuario> buscarListaPorLogin(String userName) {

		try {
			entityManager.clear();
			Query query = entityManager.createQuery("select u from NaUsuario u left join u.ator a where lower(u.login) like :login");
			query.setParameter("login", userName.toLowerCase());
			query.setHint("org.hibernate.cacheable", false);

			return query.getResultList();
		} catch (NoResultException e) {

			return null;
		}
	}

	public NaUsuario buscaUsuarioCpf(String cpfCnpj) {
		try {
			entityManager.clear();
			Query query = entityManager.createQuery("select u from NaUsuario u left join u.ator a where u.cpfCnpj = :cpfCnpj");
			query.setParameter("cpfCnpj", cpfCnpj);
			query.setHint("org.hibernate.cacheable", false);

			return (NaUsuario) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	public NaUsuario buscaUsuarioPorId(int id) {

		try {
			Query query = entityManager.createQuery("select u from NaUsuario u left join u.ator a where u.id = :id");
			query.setParameter("id", id);

			return (NaUsuario) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public List<NaUsuario> buscaUsuariosPorSistema(Integer idSistema) {

		try {
			Query query = entityManager.createQuery("select u from NaUsuario u left join u.ator a where u.id = :id");
			query.setParameter("id", idSistema);

			return (List<NaUsuario>) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}

	}

	@Override
	public void beforeSave(NaUsuario usuario) throws NeaException {

		// criando/atualizando Ator -------------------------
		if (usuario.getAtor() == null || usuario.getAtor() instanceof HibernateProxy) {

			NaAtor ator = new NaAtor(usuario.getNome(), EnumTipoAtor.USUARIO);
			ator = atorRepository.noAudit().save(ator);

			usuario.setAtor(ator);
		} else {
			if (!usuario.getNome().equals(usuario.getAtor().getNome())) {
				usuario.getAtor().setNome(usuario.getNome());
				usuario.setAtor(atorRepository.save(usuario.getAtor()));
			}
		}

		usuario.setOrigemAlteracao(EnumNaUsuarioOrigemAlteracao.WEB);

		super.beforeSave(usuario);

		// Adicionando permissao de acesso ao sistema atual -----------
		// NaUsuarioSistema usuarioSistema =
		// possuiPermissaoParaAcessarEsteSistema();
	}

	@Override
	protected void rollbackSave(NaUsuario usuario) {
		atorRepository.removeAtoresSemVinculos();
	}

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("nome"));
	}

	@Override
	protected void afterDelete(NaUsuario entity) throws NeaException {
		atorRepository.apagarAtor(entity.getAtor());
		super.afterDelete(entity);
	}

	public NaUsuario getNaUsuario(NaAtor ator) {
		return (NaUsuario) getCriteriaDinamicSearch().add(Restrictions.eq("ator", ator)).uniqueResult();
	}

	public List<NaUsuario> getUsuariosDoPerfil(NaPerfil naPerfil) {

		List<NaUsuario> usuarios = new ArrayList<NaUsuario>();

		List<NaUsuarioPerfil> usuarioPerfil = naUsuarioPerfil.buscaUsuarioPerfilPorPerfil(naPerfil);

		for (NaUsuarioPerfil naUsuarioPerfil : usuarioPerfil) {
			usuarios.add(naUsuarioPerfil.getUsuario());
		}

		return usuarios;
	}

	@Override
	public List<NaUsuario> dinamicSearch(List<IFilter> filters) {
		// TODO Auto-generated method stub
		return super.dinamicSearch(filters);
	}

	@SuppressWarnings("unchecked")
	public List<NaUsuario> getNaUsuarios(List<IFilter> listFilters, NaSistema sistema, NaCliente cliente, int currentIndex, int count) {
		Criteria consult;

		try {

			consult = createCriteria().setFirstResult(currentIndex).setMaxResults(count);
			addDistinct(consult);
			addJoinFecth(consult);
			addOrdem(consult);

			if (listFilters != null && !listFilters.isEmpty()) {
				consult = EngineCriteria.buildCriteria(consult, listFilters);
			}

			consult.createCriteria("listaNaUsuarioSistema", "us").add(Restrictions.eq("sistema", sistema)).add(Restrictions.eq("naCliente", cliente));

			return consult.list();
		} catch (Exception e) {
			log.error("Erro ao buscar lista de NaUsuarios! ", e);
			return new ArrayList<>();
		}
	}

	public Integer getNaUsuariosCount(List<IFilter> listFilters, NaSistema sistema, NaCliente cliente) {
		Criteria consult;
		try {
			consult = createCriteria().setProjection(Projections.rowCount());

			if (listFilters != null && !listFilters.isEmpty()) {
				consult = EngineCriteria.buildCriteria(consult, listFilters);
			}

			consult.createCriteria("listaNaUsuarioSistema", "us").add(Restrictions.eq("sistema", sistema)).add(Restrictions.eq("naCliente", cliente));

			return ((Long) consult.uniqueResult()).intValue();
		} catch (Exception e) {
			log.error("Erro ao Buscar quantidade de NaUsuario! ", e);
			return 0;
		}
	}

	@SuppressWarnings("unchecked")
	public List<NaUsuario> getNaUsuariosBySistemaCliente(Integer sistemaId, Integer clienteId) {
		StringBuilder sql = new StringBuilder("select distinct u from NaUsuarioSistema us ");
		sql.append(" join  us.usuario u");
		sql.append(" where us.naCliente.id = :clienteId");
		sql.append(" and us.sistema.id = :sistemaId");
		sql.append(" and us.ativo = :ativo");

		Query query = entityManager.createQuery(sql.toString());
		query.setParameter("clienteId", clienteId);
		query.setParameter("sistemaId", sistemaId);
		query.setParameter("ativo", EnumSimNao.SIM);
		return query.getResultList();
	}

	public List<NaUsuario> buscarUsuariosPorCliente(NaCliente cliente) {
		try {

			Query query = entityManager.createQuery("select us.usuario from NaUsuarioSistema us LEFT JOIN us.naCliente as c where c = :cliente");
			query.setParameter("cliente", cliente);

			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NaUsuario> buscarPorClienteSistemaSemVinculoNaUsuarioSistema(NaCliente cliente, NaSistema sistema) {
		try {

			StringBuilder sql = new StringBuilder();
			sql.append("SELECT distinct us.usuario FROM NaUsuarioSistema AS us ");
			sql.append("WHERE us.naCliente = :cliente ");
			sql.append(" and not exists ( ");
			sql.append("                  from NaUsuarioSistema us1 ");
			sql.append("                   where us1.usuario = us.usuario ");
			sql.append("                     and us1.naCliente = us.naCliente ");
			sql.append("                     and us1.sistema = :naSistema ");
			sql.append(" )");

			Query query = entityManager.createQuery(sql.toString());
			query.setParameter("cliente", cliente);
			query.setParameter("naSistema", sistema);

			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NaUsuario> buscarListaPorNomeCliente(NaCliente cliente, String nomeUsuario) {
		try {

			Query query = entityManager.createQuery("select distinct us.usuario from NaUsuarioSistema us LEFT JOIN us.naCliente as c "
					+ " LEFT JOIN us.usuario as u where us.ativo = :ativo AND c = :cliente AND upper(us.usuario.nome) LIKE :nomeUsuario");
			query.setParameter("ativo", EnumSimNao.SIM);
			query.setParameter("cliente", cliente);
			query.setParameter("nomeUsuario", "%" + nomeUsuario.toUpperCase() + "%");

			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	public List<NaUsuario> buscarListaPorNome(String nomeUsuario) {
		try {

			Query query = entityManager.createQuery("select distinct us.usuario from NaUsuarioSistema us LEFT JOIN us.naCliente as c "
					+ " LEFT JOIN us.usuario as u where us.ativo = :ativo AND upper(us.usuario.nome) LIKE :nomeUsuario");
			query.setParameter("ativo", EnumSimNao.SIM);
			query.setParameter("nomeUsuario", "%" + nomeUsuario.toUpperCase() + "%");

			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public void setEntityManager(EntityManager entityManager) {
		atorRepository.setEntityManager(entityManager);
		naUsuarioPerfil.setEntityManager(entityManager);
		super.setEntityManager(entityManager);
	}
}
