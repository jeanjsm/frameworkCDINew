package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaAtor;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaPerfil;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioPerfil;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoAtor;
import br.com.neainformatica.infrastructure.exception.NeaException;

public class NaPerfilRepository extends GenericRepository<NaPerfil> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	NaAtorRepository atorRepository;

	public NaPerfil existePerfil(String nomePerfil, NaSistema sistema, NaCliente cliente) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select p ");
			sql.append("from NaPerfil p ");
			sql.append("  join fetch p.ator a ");
			sql.append("where p.descricao = :perfil ");
			sql.append("  and p.sistema = :sistema ");
			sql.append("  and p.cliente = :cliente ");

			Query query = entityManager.createQuery(sql.toString());
			query.setParameter("perfil", nomePerfil);
			query.setParameter("sistema", sistema);
			query.setParameter("cliente", cliente);

			return (NaPerfil) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<NaPerfil> buscaPerfis(NaUsuario usuario, NaSistema sistema) {
		List<NaPerfil> perfis = new ArrayList<NaPerfil>();

		if (usuario.getId() != null) {

			StringBuilder sql = new StringBuilder();
			sql.append("select up ");
			sql.append("from NaUsuarioPerfil up ");
			sql.append("  left join fetch up.perfil p  ");
			sql.append("  left join fetch p.sistema s ");
			sql.append("where up.usuario = :usuario ");
			sql.append("  and p.sistema = :sistema ");

			Query query = entityManager.createQuery(sql.toString());
			query.setParameter("usuario", usuario);
			query.setParameter("sistema", sistema);

			List<NaUsuarioPerfil> usuarioPerfil = query.getResultList();

			for (NaUsuarioPerfil naUsuarioPerfil : usuarioPerfil) {
				perfis.add(naUsuarioPerfil.getPerfil());
			}
		}

		return perfis;
	}

	@SuppressWarnings("unchecked")
	public List<NaPerfil> buscaPerfisSistema(Integer idSistema) {

		StringBuilder sql = new StringBuilder();
		sql.append("select distinct p ");
		sql.append("from NaUsuarioPerfil up ");
		sql.append("  join fetch up.perfil p ");
		sql.append("where p.sistema.id = :idSistema ");

		Query query = entityManager.createQuery(sql.toString());
		query.setParameter("idSistema", idSistema);

		return (List<NaPerfil>) query.getResultList();

	}

	@SuppressWarnings("unchecked")
	public List<NaPerfil> buscaPerfisDisponiveis(NaUsuario usuario, NaSistema sistema, NaCliente cliente) {

		List<NaPerfil> perfis;
		Query qryPerfis = entityManager.createQuery("select p from NaPerfil p where p.sistema = :sistema and p.cliente = :cliente");
		qryPerfis.setParameter("sistema", sistema);
		qryPerfis.setParameter("cliente", cliente);
		perfis = qryPerfis.getResultList();

		List<NaPerfil> perfisUsuario;
		perfisUsuario = buscaPerfis(usuario, sistema);

		for (NaPerfil naPerfil : perfisUsuario) {
			if (perfis.contains(naPerfil))
				perfis.remove(naPerfil);
		}

		return perfis;
	}

	@Override
	protected void afterDelete(NaPerfil entity) throws NeaException {
		atorRepository.apagarAtor(entity.getAtor());
		super.afterDelete(entity);
	}

	@Override
	public void beforeSave(NaPerfil perfil) throws NeaException {

		if (perfil.getId() != null)
			perfil.setAtor(atorRepository.buscarAtorPerfil(perfil));

		perfil.setDescricao(perfil.getDescricao().toUpperCase());

		if (perfil.getAtor() == null) {

			NaAtor ator = new NaAtor(perfil.getDescricao(), EnumTipoAtor.PERFIL);
			ator = atorRepository.noAudit().save(ator);

			perfil.setAtor(ator);
		} else if (!perfil.getDescricao().equals(perfil.getAtor().getNome())) {
			perfil.getAtor().setNome(perfil.getDescricao());
			perfil.setAtor(atorRepository.save(perfil.getAtor()));
		}
	}

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("descricao"));
	}

	@Override
	public void beforeDelete(NaPerfil perfil) throws NeaException {

	}

	public List<NaPerfil> buscaPerfisPorSistemaCliente(Integer sistemaId, Integer clienteId) {
		List<NaPerfil> perfis;
		Query qryPerfis = entityManager.createQuery("select p from NaPerfil p where p.sistema.id = :sistemaId and p.cliente.id = :clienteId");
		qryPerfis.setParameter("sistemaId", sistemaId);
		qryPerfis.setParameter("clienteId", clienteId);
		perfis = qryPerfis.getResultList();
		return perfis;
	}

	public List<NaPerfil> buscaPerfisUsuario(Integer usuarioId, Integer sistemaId, Integer clienteId) {
		List<NaPerfil> perfis = new ArrayList<NaPerfil>();
		if (usuarioId != null) {
			StringBuilder sql = new StringBuilder();
			sql.append("select up ");
			sql.append("from NaUsuarioPerfil up ");
			sql.append("  left join fetch up.perfil p  ");
			sql.append("where up.usuario.id = :usuarioId ");
			sql.append("  and p.sistema.id = :sistemaId ");
			sql.append(" and p.cliente.id = :clienteId");
			
			Query query = entityManager.createQuery(sql.toString());
			query.setParameter("usuarioId", usuarioId);
			query.setParameter("sistemaId", sistemaId);
			query.setParameter("clienteId", clienteId);

			List<NaUsuarioPerfil> usuarioPerfil = query.getResultList();

			for (NaUsuarioPerfil naUsuarioPerfil : usuarioPerfil) {
				perfis.add(naUsuarioPerfil.getPerfil());
			}
		}

		return perfis;
	}

	public NaPerfil buscarPerfilPorAtorCliente(NaAtor naAtor, NaCliente naCliente) {
		Query qryPerfis = entityManager.createQuery("select p from NaPerfil p where p.cliente = :naCliente and p.ator = :naAtor ");
		qryPerfis.setParameter("naAtor", naAtor);
		qryPerfis.setParameter("naCliente", naCliente);
		try{
			return (NaPerfil) qryPerfis.getSingleResult();
		}catch (NoResultException e){
			return null;
		}
	}
}
