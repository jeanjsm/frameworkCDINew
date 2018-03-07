package br.com.neainformatica.infrastructure.scheduler;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.neainformatica.infrastructure.services.NaEstadoService;

@Stateless
public class JobValidaLinks {

	private Logger log = LoggerFactory.getLogger(JobValidaLinks.class);

	@Inject
	NaEstadoService naEstadoService;

	@Asynchronous
	public void quartzRun(@Observes @TarefaQuartzQualifier("S1012_JOB_VALIDA_LINKS") QuartzEvent evento) throws InterruptedException {
		log.debug("executando agendamento: " + evento.toString());

		naEstadoService.testarAgendamento();

	}

}
