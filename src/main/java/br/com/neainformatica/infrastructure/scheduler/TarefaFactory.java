package br.com.neainformatica.infrastructure.scheduler;

import java.io.Serializable;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TarefaFactory implements JobFactory, Serializable {
	private static final long serialVersionUID = 1L;

	private Logger log = LoggerFactory.getLogger(TarefaFactory.class);

	@Inject
	private BeanManager beanManager;

	@Override
	public Job newJob(TriggerFiredBundle trigger, Scheduler scheduler) throws SchedulerException {
		JobDetail detalhe;
		String tarefa;
		QuartzEvent evento;

		try {
			detalhe = trigger.getJobDetail();
			tarefa = (String) detalhe.getJobDataMap().get(detalhe.getKey().getName());
			evento = new QuartzEvent();

			log.debug("FireEvent " + evento.getClass().getSimpleName() + ", tarefa " + tarefa);

			beanManager.fireEvent(evento, new QuartzQualifier(tarefa));

			return evento;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}