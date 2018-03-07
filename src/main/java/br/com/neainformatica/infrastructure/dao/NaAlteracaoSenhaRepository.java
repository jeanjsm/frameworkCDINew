package br.com.neainformatica.infrastructure.dao;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 23/12/2014 09:20:26
 @Author  = Eduardo Leite Ranzzani
 @Versão da Classe = 4
 -----------------------------------------------------------------------------------------------------------------------------
 */

import java.io.Serializable;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaAlteracaoSenha;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;

public class NaAlteracaoSenhaRepository extends GenericRepository<NaAlteracaoSenha> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}

	public NaAlteracaoSenha buscaNaAlteracaoSenhaPorHash(String chaveValidacao) {

		try {
			StringBuilder sql = new StringBuilder();
			sql.append("select u from NaAlteracaoSenha u  ");
			sql.append("  join fetch u.usuario ");
			sql.append("where u.chaveValidacao = :chaveValidacao ");
			Query query = entityManager.createQuery(sql.toString());
			query.setParameter("chaveValidacao", chaveValidacao);
			NaAlteracaoSenha naAlteracaoSenha = (NaAlteracaoSenha) query.getSingleResult();

			return naAlteracaoSenha;

		} catch (NoResultException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void mudaStatusHash(NaUsuario naUsuario) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" update NaAlteracaoSenha ");
			sql.append(" set invalido = :invalidoAtualizando ");
			sql.append(" where invalido = :invalido and ");
			sql.append(" usuario = :usuario ");
			org.hibernate.Query query = getSession().createQuery(sql.toString());
			query.setParameter("invalidoAtualizando", EnumSimNao.SIM);
			query.setParameter("invalido", EnumSimNao.NAO);
			query.setParameter("usuario", naUsuario);
			query.executeUpdate();
		} catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}