package br.com.neainformatica.infrastructure.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.neainformatica.infrastructure.enumeration.EnumFormatDate;
import br.com.neainformatica.infrastructure.tools.NeaDate;

public class QuartzEvent implements Job {

	private Logger log = LoggerFactory.getLogger(QuartzEvent.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("Executando " + context.getJobDetail().getKey().getName() + " em " + NeaDate.formatarData(context.getFireTime(), EnumFormatDate.DATE_TIME)
				+ " próxima excução " + NeaDate.formatarData(context.getNextFireTime(), EnumFormatDate.DATE_TIME));
	}
}