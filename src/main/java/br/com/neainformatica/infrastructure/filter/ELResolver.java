package br.com.neainformatica.infrastructure.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.el.ELContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.proxy.HibernateProxy;

public class ELResolver extends javax.el.CompositeELResolver {
	
	private static Logger log = Logger.getLogger(ELResolver.class);
	private static String jndi_name = "java:comp/env/entityManager";
	
	public Object getValue(ELContext context, Object target, Object property) {
		Field field;
		Class<?> classe;
		Object fieldValue;
		try {
			if (target != null && property != null && !"".equals(property) && isEntity(target.getClass())) {
				field = target.getClass().getDeclaredField((String) property);
				field.setAccessible(true);
				fieldValue = field.get(target);
				
				if (field.getGenericType() instanceof ParameterizedType) {
					classe = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
				} else {
					classe = (Class<?>) field.getGenericType();
				}
				
				if (fieldValue instanceof PersistentBag) {
					if (!((PersistentBag) fieldValue).wasInitialized() &&
							((PersistentBag) fieldValue).getSession() == null) {
						initialize(classe, field, fieldValue, target);
					}
				} else if (fieldValue instanceof HibernateProxy) {
					if (((HibernateProxy) fieldValue).getHibernateLazyInitializer().isUninitialized() &&
							((HibernateProxy) fieldValue).getHibernateLazyInitializer().getSession() == null) {
						initialize(classe, field, fieldValue, target);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return super.getValue(context, target, property);
	}
	
	private <Tipo> void initialize(Class<Tipo> classe, Field field, Object fieldValue, Object target) throws Exception {
		Context ctx;
		TypedQuery<Tipo> qry;
		EntityManager em;
		CriteriaBuilder builder;
		CriteriaQuery<Tipo> criteria;
		Root<Tipo> root;
		String nomeFieldFK = null;
		
		ctx = new InitialContext();
		em = (EntityManager) ctx.lookup(jndi_name);
		
		log.debug("Classe: " + classe.getSimpleName() + " - Atributo: " + field.getName());
		
		if (fieldValue instanceof HibernateProxy) {
			((HibernateProxy) fieldValue).getHibernateLazyInitializer().setSession((SessionImplementor) em.getDelegate());
			Hibernate.initialize(fieldValue);
		} else {
			builder = em.getCriteriaBuilder();
			criteria = builder.createQuery(classe);
			
			root = criteria.from(classe);
			
			for (Field attr: classe.getDeclaredFields()) {
				if (isEntity(attr.getType())) {
					if (attr.getType() == target.getClass()) {
						nomeFieldFK = attr.getName();
					} else {						
						root.fetch(attr.getName(), JoinType.LEFT);
					}
				}
			}
			
			criteria.select(root).where(builder.equal(root.get(nomeFieldFK), target));
			
			qry = em.createQuery(criteria);
			
			if (field.getType() == List.class) {
				field.set(target, qry.getResultList());
			} else {
				field.set(target, qry.getSingleResult());
			}		
		}
	}
	
	private boolean isEntity(Class<?> classe) {
		Annotation anotacao = classe.getAnnotation(Entity.class);
		
		if (anotacao instanceof Entity) {
			return true;
		}
		
		return false;
	}
	
}