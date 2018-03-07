package br.com.neainformatica.infrastructure.scheduler;

import java.io.Serializable;
import java.util.List;

import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.entity.NaAgendamento;
import br.com.neainformatica.infrastructure.services.NaAgendamentoService;

@ApplicationScoped
public class NaSchedullerController implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	protected Log log;

	private Scheduler scheduler;

	@Inject
	private TarefaFactory tarefaFactory;

	@Inject
	private NaAgendamentoService service;

	@Inject
	private InfrastructureController infrastructureController;

	private void criaTarefa(NaAgendamento agenda) {
		log.debug("Iniciando agendamento da tarefa " + agenda.getNome() + " Identificador: " + agenda.getCodigoTarefa());

		JobDetail tarefa;
		Trigger gatilho;

		try {
			String chaveTarefa = agenda.getCodigoTarefa();

			tarefa = JobBuilder.newJob(QuartzEvent.class).withIdentity(chaveTarefa, chaveTarefa).build();

			tarefa.getJobDataMap().put(tarefa.getKey().getName(), chaveTarefa);

			gatilho = TriggerBuilder.newTrigger().withIdentity(chaveTarefa, chaveTarefa)

					.withSchedule(CronScheduleBuilder.cronSchedule(agenda.getExpressaoCron())).build();

			scheduler.scheduleJob(tarefa, gatilho);
		} catch (Exception e) {
			log.error("Erro ao agendar tarefa: " + agenda.getNome());
			e.printStackTrace();
		}
	}

	public void atualizaSchedulers() {
		List<NaAgendamento> lsAgendamento;

		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.setJobFactory(tarefaFactory);

			log.info("Buscando tarefas agendadas ->Sistema: " + infrastructureController.getNaSistema().getId() + "-"
					+ infrastructureController.getNaSistema().getSistema() + "...");
			lsAgendamento = service.findAllActive(infrastructureController.getNaSistema());

			log.info("Tarefas encontradas " + lsAgendamento.size());

			for (NaAgendamento agenda : lsAgendamento) {
				log.info("Agendando tarefa: " + agenda.getCodigoTarefa() + " - " + agenda.getNome() + " - " + agenda.getExpressaoCron());
				criaTarefa(agenda);
			}

			scheduler.start();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void removeAllAgendamentos() {

		try {
			this.scheduler.clear();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

}