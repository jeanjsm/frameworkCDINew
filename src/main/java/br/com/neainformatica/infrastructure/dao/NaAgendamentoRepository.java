package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.com.neainformatica.infrastructure.entity.NaAgendamento;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

public class NaAgendamentoRepository extends GenericRepository<NaAgendamento> implements Serializable {
	private static final long serialVersionUID = 1L;

	public List<NaAgendamento> findAllActive(NaSistema sistema) {

		List<IFilter> filtros = new ArrayList<IFilter>();
		filtros.add(new EqualFilter("sistema", sistema, null));
		filtros.add(new EqualFilter("ativo", EnumSimNao.SIM, null));

		return dinamicSearch(filtros);

	}

	public NaAgendamento findBySistemaCodigo(NaSistema sistema, String codigoTarefa) {

		try {
			Query query = entityManager.createQuery("select a from NaAgendamento a where a.sistema = :sistema and a.codigoTarefa = :codigoTarefa ");
			query.setParameter("sistema", sistema);
			query.setParameter("codigoTarefa", codigoTarefa);

			NaAgendamento agendamento = (NaAgendamento) query.getSingleResult();
			return agendamento;
		} catch (NoResultException e) {
			return null;
		}

	}

}
