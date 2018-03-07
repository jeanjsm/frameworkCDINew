package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;

public class NaSistemaRepository extends GenericRepository<NaSistema> implements Serializable {
	private static final long serialVersionUID = 1L;

	public NaSistema buscarSistema(Integer idSistema) {
		try {
			Query query = entityManager.createQuery("select o from NaSistema o where o.id = :codigo", NaSistema.class);
			query.setParameter("codigo", idSistema);
			return (NaSistema) query.getSingleResult();
		} catch (NoResultException e) {
			log.warn("não foi encontrado o sistema " + idSistema + " na base de dados.");
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<NaSistema> buscarSistemaUsuario(NaUsuario usuario, NaCliente cliente) {

		StringBuilder sql = new StringBuilder();
		sql.append("select us.sistema ");
		sql.append("from NaUsuarioSistema us ");
		sql.append("join us.sistema s ");
		sql.append("where us.usuario.id = :usuario ");
		sql.append("  and us.naCliente.id = :cliente ");
		sql.append("  and s.ativo = :ativo ");

		try {
			Query query = entityManager.createQuery(sql.toString());
			query.setParameter("usuario", usuario.getId());
			query.setParameter("cliente", cliente.getId());
			query.setParameter("ativo", EnumSimNao.SIM);
			return query.getResultList();
		} catch (NoResultException e) {
			return null;
		}

	}

}