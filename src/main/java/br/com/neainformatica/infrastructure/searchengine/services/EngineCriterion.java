package br.com.neainformatica.infrastructure.searchengine.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.AndFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.BetweenFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EndingFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.GreaterEqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.GreaterThanFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.InFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.IsNotNullFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.IsNullFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LessEqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LessThanFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LikeFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.OrFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.StartingFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;


/***
 * Classe utilitária usada na contrução de criterions
 * 
 * @author Pedro Sanchez
 * */
public class EngineCriterion {

	/**
	 * Controi o Criterio da consulta partindo de um filtro
	 * 
	 * <br/>
	 * <br/>
	 * <em><strong>Este Método é Recursivo</strong></em>
	 * 
	 * 
	 * @param filter filtro a transformar em Criterio
	 * 
	 * @return criterion usado como filtro.
	 * */
	public static Criterion buildByFilter(IFilter filter) {

		if (FilterEnum.EQUAL.equals(filter.getFilterBy())) {
			EqualFilter equalFilter = (EqualFilter) filter;
			return Restrictions.eq(equalFilter.getField(),
					equalFilter.getInitialValue());
		}

		if (FilterEnum.LIKE.equals(filter.getFilterBy())) {
			LikeFilter likeFilter = (LikeFilter) filter;
			return Restrictions.ilike(likeFilter.getField(), likeFilter
					.getInitialValue().toString().trim(), MatchMode.ANYWHERE);
		}
		
		if (FilterEnum.STARTING.equals(filter.getFilterBy())) {
			StartingFilter startingFilter = (StartingFilter) filter;
			return Restrictions.ilike(startingFilter.getField(), startingFilter
					.getInitialValue().toString().trim(), MatchMode.START);
		}
		
		if (FilterEnum.ENDING.equals(filter.getFilterBy())) {
			EndingFilter endingFilter = (EndingFilter) filter;
			return Restrictions.ilike(endingFilter.getField(), endingFilter
					.getInitialValue().toString().trim(), MatchMode.END);
		}

		if (FilterEnum.GREATER_EQUAL.equals(filter.getFilterBy())) {
			GreaterEqualFilter greaterEqualFilter = (GreaterEqualFilter) filter;
			return Restrictions.ge(greaterEqualFilter.getField(),
					greaterEqualFilter.getInitialValue());
		}

		if (FilterEnum.GREATER_THAN.equals(filter.getFilterBy())) {
			GreaterThanFilter greaterThanFilter = (GreaterThanFilter) filter;
			return Restrictions.gt(greaterThanFilter.getField(),
					greaterThanFilter.getInitialValue());
		}

		if (FilterEnum.LESS_EQUALS.equals(filter.getFilterBy())) {
			LessEqualFilter lessEqualFilter = (LessEqualFilter) filter;
			return Restrictions.le(lessEqualFilter.getField(),
					lessEqualFilter.getInitialValue());
		}

		if (FilterEnum.LESS_THAN.equals(filter.getFilterBy())) {
			LessThanFilter lessThanFilter = (LessThanFilter) filter;
			return Restrictions.lt(lessThanFilter.getField(),
					lessThanFilter.getInitialValue());
		}

		if (FilterEnum.BETWEEN.equals(filter.getFilterBy())) {
			BetweenFilter betweenFilter = (BetweenFilter) filter;
			return Restrictions.between(betweenFilter.getField(),
					betweenFilter.getInitialValue(),
					betweenFilter.getFinalValue());
		}

		if (FilterEnum.IN.equals(filter.getFilterBy())) {
			InFilter inFilter = (InFilter) filter;
			return Restrictions.in(inFilter.getField(), inFilter.getValues());
		}

		if (FilterEnum.ISNULL.equals(filter.getFilterBy())) {
			IsNullFilter isNullFilter = (IsNullFilter) filter;
			return Restrictions.isNull(isNullFilter.getField());
		}
		
		if (FilterEnum.IS_NOT_NULL.equals(filter.getFilterBy())) {
			IsNotNullFilter isNotNullFilter = (IsNotNullFilter) filter;
			return Restrictions.isNotNull(isNotNullFilter.getField());
		}
		

		if (FilterEnum.NOT.equals(filter.getFilterBy())) {
			//NotFilter notFilter = (NotFilter) filter;

			//if (FilterEnum.ISNULL.equals(notFilter.getFilter().getFilterBy())) {
				//return Restrictions.isNotNull(((IsNullFilter) notFilter
				//		.getFilter()).getField());
			//}
			//return Restrictions.not(buildByFilter(notFilter.getFilter()));

		}

		if (FilterEnum.OR.equals(filter.getFilterBy())) {
			OrFilter orFilter = (OrFilter) filter;

			Disjunction disjunction = Restrictions.disjunction();

			if (orFilter.getFilters() != null	&& !orFilter.getFilters().isEmpty()) {
				for (IFilter ifilter : orFilter.getFilters()) {
					disjunction.add(buildByFilter(ifilter));
				}
			}

			return disjunction;
		}

		if (FilterEnum.AND.equals(filter.getFilterBy())) {
			AndFilter andFilter = (AndFilter) filter;

			Conjunction conjunction = Restrictions.conjunction();

			if (andFilter.getFilters() != null
					&& !andFilter.getFilters().isEmpty()) {
				for (IFilter ifilter : andFilter.getFilters()) {
					conjunction.add(buildByFilter(ifilter));
				}
			}

			return conjunction;
		}

		return null;
	}
	
	/**
	 * Partindo de uma lista de filters constroi uma lista de criterions
	 * @param filters Filtros a serem convertidos
	 * @return lista de Criterions a ser usado na consulta
	 * 
	 * */
	public static List<Criterion> buildCritetions(List<IFilter> filters) {
		
		List<Criterion> criterions = new ArrayList<>();
		if (filters != null && !filters.isEmpty()) {
			for (IFilter filter : filters) {

				Criterion criterion = buildByFilter(filter);

				if (criterion != null)
					criterions.add(criterion);
			}
		}
		return criterions;
	}

}
