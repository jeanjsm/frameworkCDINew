package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;

import org.hibernate.EmptyInterceptor;
import org.hibernate.Interceptor;

public class HibernateInterceptor extends EmptyInterceptor implements Interceptor {
	private static final long serialVersionUID = 1L;

	@Override
	public String onPrepareStatement(String sql) {
//		log.debug("Executando o sql: " + sql);

		sql = removeSchema(sql);

		return sql;
	}

	public static String removeSchema(String script) {

		return script.replaceAll(" public\\.", " ").
				replaceAll(" seguranca\\.", " ").
				replaceAll(" auditoria\\.", " ").
				replaceAll(" organograma\\.", " ").
				replaceAll(" PC\\.", " ");

	}
	
	
	@Override
	public Object getEntity(String entityName, Serializable id) {
		// TODO Auto-generated method stub
		return super.getEntity(entityName, id);
	}

}
