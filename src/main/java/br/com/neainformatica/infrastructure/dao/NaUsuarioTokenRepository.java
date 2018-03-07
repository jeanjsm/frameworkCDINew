package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioToken;

/**
 * Created by rodolpho.sotolani on 01/02/2017.
 */
public class NaUsuarioTokenRepository extends GenericRepository<NaUsuarioToken> implements Serializable {
	private static final long serialVersionUID = 1L;

	public NaUsuarioToken findByToken(String token) {

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select token ");
			sql.append("from NaUsuarioToken as token ");
			sql.append("  join fetch token.usuario as u ");
			sql.append("where token.token = :token ");

			Query query = this.entityManager.createQuery(sql.toString());
			query.setParameter("token", token);

			return (NaUsuarioToken) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public NaUsuarioToken findByNaUsuario(NaUsuario usuario) {
		Query query = this.entityManager.createQuery("select t from NaUsuarioToken as t where t.usuario = :usuario ");
		query.setParameter("usuario", usuario);
		try {
			return (NaUsuarioToken) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
