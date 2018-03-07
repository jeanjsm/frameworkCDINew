package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;

import br.com.neainformatica.infrastructure.entity.NaAuditoria;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumAuditoriaOperacao;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.tools.NeaDate;

public class NaAuditoriaRepository extends GenericRepository<NaAuditoria> implements Serializable {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public List<NaAuditoria> getListaAuditoria(NaSistema sistema, int first, int pageSize, Date dataInicial, Date dataFinal, String tabela,
			String chaveRegistro, EnumAuditoriaOperacao tipoOperacao, NaUsuario usuario, NaCliente naCliente) {

		StringBuilder jpql = new StringBuilder();

		jpql.append("select distinct nau from NaAuditoria nau ");
		jpql.append(" join fetch nau.auditoriaItems itens ");
		jpql.append(" where nau.id > 0 ");

		if (dataInicial != null && dataFinal != null)
			jpql.append(" and nau.dataAuditoria between :dataInicial and :dataFinal ");

		if (sistema != null)
			jpql.append(" and nau.sistema = :sistema ");

		if (tabela != null && !tabela.equals(""))
			jpql.append(" and lower(nau.tabela) like :tabela ");

		if (chaveRegistro != null && !chaveRegistro.equals(""))
			jpql.append(" and lower(nau.chaveRegistro) like :chaveRegistro ");

		if (tipoOperacao != null)
			jpql.append(" and nau.tipoOperacao = :tipoOperacao ");

		if (usuario != null)
			jpql.append(" and nau.usuario = :usuario ");
		
		if (naCliente != null)
			jpql.append(" and nau.naCliente = :naCliente ");

		Query query = this.entityManager.createQuery(jpql.toString());

		if (dataInicial != null && dataFinal != null) {
			query.setParameter("dataInicial", NeaDate.zeraHoraData(dataInicial));
			query.setParameter("dataFinal", NeaDate.ultimaHoraData(dataFinal));
		}

		if (sistema != null)
			query.setParameter("sistema", sistema);

		if (tabela != null && !tabela.equals(""))
			query.setParameter("tabela", "%" + tabela.toLowerCase() + "%");

		if (chaveRegistro != null && !chaveRegistro.equals(""))
			query.setParameter("chaveRegistro", "%" + chaveRegistro.toLowerCase() + "%");

		if (tipoOperacao != null)
			query.setParameter("tipoOperacao", tipoOperacao);

		if (usuario != null)
			query.setParameter("usuario", usuario.getLogin().equals("SUPORTE") ? usuario.getLogin() : usuario.getCpfCnpj());

		if (naCliente != null)
			query.setParameter("naCliente", naCliente);

		if (first == 0)
			query.setMaxResults(pageSize);
		else
			query.setMaxResults(pageSize).setFirstResult(first);
		try {

			return query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<NaAuditoria>();
		}
	}

	public Integer getListaAuditoriaCount(List<IFilter> listFilters, NaSistema sistema, Date dataInicial, Date dataFinal, String tabela, String chaveRegistro,
			EnumAuditoriaOperacao tipoOperacao, NaUsuario usuario, NaCliente naCliente) {

		StringBuilder jpql = new StringBuilder();

		jpql.append("select count(nau.id) from NaAuditoria nau ");
		jpql.append(" where nau.id > 0 ");

		if (dataInicial != null && dataFinal != null)
			jpql.append(" and nau.dataAuditoria between :dataInicial and :dataFinal");

		if (sistema != null)
			jpql.append(" and nau.sistema = :sistema");

		if (tabela != null && !tabela.equals(""))
			jpql.append(" and lower(nau.tabela) like :tabela ");

		if (chaveRegistro != null && !chaveRegistro.equals(""))
			jpql.append(" and lower(nau.chaveRegistro) like :chaveRegistro ");

		if (tipoOperacao != null)
			jpql.append(" and nau.tipoOperacao = :tipoOperacao");

		if (usuario != null)
			jpql.append(" and nau.usuario = :usuario ");

		if (naCliente != null)
			jpql.append(" and nau.naCliente = :naCliente ");
		
		Query query = this.entityManager.createQuery(jpql.toString());

		if (dataInicial != null && dataFinal != null) {
			query.setParameter("dataInicial", NeaDate.zeraHoraData(dataInicial));
			query.setParameter("dataFinal", NeaDate.ultimaHoraData(dataFinal));
		}

		if (sistema != null)
			query.setParameter("sistema", sistema);

		if (tabela != null && !tabela.equals(""))
			query.setParameter("tabela", "%" + tabela.toLowerCase() + "%");

		if (chaveRegistro != null && !chaveRegistro.equals(""))
			query.setParameter("chaveRegistro", "%" + chaveRegistro.toLowerCase() + "%");

		if (tipoOperacao != null)
			query.setParameter("tipoOperacao", tipoOperacao);

		if (usuario != null)
			query.setParameter("usuario", usuario.getLogin().equals("SUPORTE") ? usuario.getLogin() : usuario.getCpfCnpj());

		if (naCliente != null)
			query.setParameter("naCliente", naCliente);
		
		return ((Long) query.getSingleResult()).intValue();
	}

	@Override
	protected void addJoinFecth(Criteria criteria) {
		criteria.setFetchMode("auditoriaItems", FetchMode.JOIN);
	}

}
