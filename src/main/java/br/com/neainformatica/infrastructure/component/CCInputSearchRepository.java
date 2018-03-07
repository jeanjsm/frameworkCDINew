package br.com.neainformatica.infrastructure.component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

public class CCInputSearchRepository implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private EntityManager entityManager;
	
	public List<?> executarConsulta(String hql, HashMap<String, Object> parametros, Integer qtdeRegistros) {
		Query q = entityManager.createQuery(hql);
		for(Entry<String, Object> parametro : parametros.entrySet()) {
			q.setParameter(parametro.getKey(), parametro.getValue());
		}
		if(qtdeRegistros != null)
			q.setMaxResults(qtdeRegistros);
		else
			q.setMaxResults(100);
		return q.getResultList();
	}
}
