package br.com.neainformatica.infrastructure.filter;

import javax.faces.application.ProjectStage;
import javax.faces.context.FacesContext;

import org.apache.log4j.spi.LoggingEvent;

public class Log4J extends org.apache.log4j.DailyRollingFileAppender {
	
	@Override
	public void setFile(String file) {
		String namePath = null;
		String nomeContexto = null;
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		
		if (ctx != null) {
			nomeContexto = ctx.getExternalContext().getContextName();
		} else {
			namePath =  Thread.currentThread()
					.getContextClassLoader()
					.getResource(".")
					.toString()
					.replaceAll("/target/test-classes/", "");
			nomeContexto = namePath.substring(namePath.lastIndexOf('/') + 1);
		}
		
		super.setFile(file.replaceAll("NOME_DO_CONTEXTO", nomeContexto));
	}
	
	@Override
	public synchronized void doAppend(LoggingEvent event) {
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		if (ctx != null
				&& ProjectStage.Production.equals(ctx.getExternalContext().getInitParameter("javax.faces.PROJECT_STAGE"))) {
			System.out.print(event.getMessage() + "\n");
		}
		
		super.doAppend(event);
	}
	
}