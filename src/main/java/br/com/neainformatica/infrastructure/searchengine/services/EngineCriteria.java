package br.com.neainformatica.infrastructure.searchengine.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;

import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * Classe utilitária usada para contruir o Criteria da consulta
 * 
 * @author Pedro Sanchez
 * 
 * */
public class EngineCriteria {
	
	
	/**
	 * Cria critéria para um objeto
	 * 
	 * @param session sessão do hibernate para criar o criteria
	 * @param object objeto para obter a classe principal da consulta
	 * 
	 * @return criteria utilizado para adciononar parametros na consulta
	 * */
	public static Criteria buildCriteria(Session session, Object object) {
		Criteria criteria = session.createCriteria(object.getClass());

		return criteria;
	}

	/**
	 * Cria crit�ria para uma classe
	 * 
	 * @param session sessão do hibernate para criar o criteria
	 * @param clazz classe principal da consulta
	 * 
	 * @return criteria utilizado para adciononar parametros na consulta
	 * */
	public static Criteria buildCriteria(Session session, Class<?> clazz) {
		Criteria criteria = session.createCriteria(clazz);

		return criteria;
	}

	
	/**
	 * Cria crit�ria para uma classe e adiciona filtro de consulta a esta
	 * 
	 * @param session sessão do hibernate para criar o criteria
	 * @param clazz classe principal da consulta
	 * @param filters filtros que a consulta dever� ter
	 * 
	 * @return criteria com filtros da consulta
	 * */
	public static Criteria buildCriteria(Session session, Class<?> clazz,
			List<IFilter> filters) {
		Criteria criteria = buildCriteria(session, clazz);
		return buildCriteria(criteria, filters);
	}

	
	/**
	 * Cria critéria para uma classe e adiciona filtro de consulta a esta
	 * 
	 * @param session sessão do hibernate para criar o criteria
	 * @param object objeto para obter a classe principal da consulta
	 * @param filters filtros que a consulta deverá ter
	 * 
	 * @return criteria com filtros da consulta
	 * */
	public static Criteria buildCriteria(Session session, Object object,
			List<IFilter> filters) {
		Criteria criteria = buildCriteria(session, object);
		return buildCriteria(criteria, filters);
	}



	
	/**
	 * Adiciona filtro de consulta ao criteria informado
	 * 
	 * @param criteria criteria utilizado para adciononar filtros da consulta
	 * @param filters filtros que a consulta deverá ter
	 * 
	 * @return criteria com filtros da consulta
	 * */
	public static Criteria buildCriteria(Criteria criteria, List<IFilter> filters) {
		List<Criterion> criterions = EngineCriterion.buildCritetions(filters);
		
		List<String> aliasList = new ArrayList<>();
		
		for (IFilter filter : filters) {
			if (filter.getAlias() != null && !filter.getAlias().equals("")) {
				
				if (!aliasList.contains(filter.getAlias())) {
					criteria.createAlias(filter.getAlias(), filter.getAlias());
					aliasList.add(filter.getAlias());
				}				
				
			}			
		}
		
		for (Criterion criterion : criterions) {
			criteria.add(criterion);
		}

		return criteria;
	}

}
