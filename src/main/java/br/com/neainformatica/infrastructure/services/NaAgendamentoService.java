package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaAgendamentoRepository;
import br.com.neainformatica.infrastructure.entity.NaAgendamento;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaAgendamentoService extends GenericService<NaAgendamento> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	NaAgendamentoRepository dao;

	@Inject
	NaSistemaService naSistemaService;

	@Override
	public GenericRepository<NaAgendamento> getRepository() {
		return this.dao;
	}

	public NaAgendamento findBySistemaCodigo(NaSistema sistema, String codigoTarefa) {
		return dao.findBySistemaCodigo(sistema, codigoTarefa);
	}

	public List<NaAgendamento> findAllActive(NaSistema sistema) {
		return dao.findAllActive(sistema);
	}

	public void adicionarAnexoifExists(Integer idSistema, String codServico, String descricao, String ExpressaoCron) {
		log.debug("Verificando se o agendamento existe:  cod:" + codServico);

		NaSistema naSistema = naSistemaService.findById(idSistema);
		NaAgendamento agendamento = findBySistemaCodigo(naSistema, codServico);

		if (agendamento == null) {
			log.debug("Adicionando novo agendamento: " + descricao + " - " + codServico + " não encontrado na base de dados, criando...");
			agendamento = new NaAgendamento();
			agendamento.setSistema(naSistema);
			agendamento.setCodigoTarefa(codServico);
			agendamento.setExpressaoCron(ExpressaoCron);
			agendamento.setNome(descricao);
			agendamento = noAudit().save(agendamento);
		}

	}

}