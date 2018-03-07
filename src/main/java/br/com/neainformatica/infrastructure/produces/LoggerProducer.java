package br.com.neainformatica.infrastructure.produces;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LoggerProducer {
	
	@Produces
	public Log produceLogger(InjectionPoint injection) {
		return LogFactory.getLog(injection.getMember().getDeclaringClass().getName());
	}
	
}