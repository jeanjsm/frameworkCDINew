package br.com.neainformatica.infrastructure.searchengine.services;

import java.util.ArrayList;
import java.util.List;

import br.com.neainformatica.infrastructure.searchengine.bean.FilterBean;
import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.enumeration.OperatorEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.AndFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.BetweenFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EndingFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.GreaterEqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.GreaterThanFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.InFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.IsNullFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LessEqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LessThanFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LikeFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.NotFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.OrFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.StartingFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * Classe Utilitária da Engine de Busca Dinamica usada para busca dinâmica
 * 
 * @author Pedro Sanchez
 * */
public class AdvancedSearchUtil {

	/**
	 * Método que converte enum Operadores em Lista de String
	 * 
	 * @return lista com os nomes dos operadores usado para preencher combo da pesquisa avan�ada
	 * 
	 * @see OperatorEnum
	 * */
	public static List<String> getOperators(){
		List<String> result = new ArrayList<>();
		
		for (OperatorEnum operatorEnum : OperatorEnum.getAll()) {
			result.add(operatorEnum.getDescricao());
		}
		
		return result;
	}
	


	/**
	 * Método que converte enum Operadores em Lista de String
	 * 
	 * @return lista com os nomes dos operadores usado para preencher combo da pesquisa avançada
	 * 
	 * @see OperatorEnum
	 * */
	public static List<String> getCriterions(){
		List<String> result = new ArrayList<>();
		
		for (FilterEnum filterEnum : FilterEnum.getAll()) {
			
			if(!filterEnum.equals(FilterEnum.AND) &&
			   !filterEnum.equals(FilterEnum.OR)  &&
			   !filterEnum.equals(FilterEnum.NOT)   ){
				
				result.add(filterEnum.getDescricao());
			}
		}
		
		return result;
	}
	

	public static FilterEnum getCriterion(String stringCriterion){
		
		for (FilterEnum filterEnum : FilterEnum.getAll()) {
			
			if(filterEnum.getDescricao().equals(stringCriterion)){
				
				return filterEnum;
			}
		}
		
		return null;
	}
	
	public static IFilter getFilter(FilterEnum filterEnum, String field, String alias, Object initialValue, Object finalValue){

		if(filterEnum.equals(FilterEnum.AND)){
			return new AndFilter(null);
		}
		
		if(filterEnum.equals(FilterEnum.BETWEEN)){
			return new BetweenFilter(field, initialValue, finalValue, alias);
		}
		if(filterEnum.equals(FilterEnum.ENDING)){
			return new EndingFilter(field, initialValue, alias);
		}
		if(filterEnum.equals(FilterEnum.EQUAL)){
			return new EqualFilter(field, initialValue, alias);
		}
		if(filterEnum.equals(FilterEnum.GREATER_EQUAL)){
			return new GreaterEqualFilter(field, initialValue, alias);
		}
		if(filterEnum.equals(FilterEnum.GREATER_THAN)){
			return new GreaterThanFilter(field, initialValue, alias);
		}
		if(filterEnum.equals(FilterEnum.IN)){
						
			return new InFilter(field, (List<?>) initialValue, alias);
		}
		if(filterEnum.equals(FilterEnum.ISNULL)){
			return new IsNullFilter(field, alias);
		}
		if(filterEnum.equals(FilterEnum.LESS_EQUALS)){
			return new LessEqualFilter(field, initialValue, alias);
		}
		if(filterEnum.equals(FilterEnum.LESS_THAN)){
			return new LessThanFilter(field, initialValue, alias);
		}
		if(filterEnum.equals(FilterEnum.LIKE)){
			return new LikeFilter(field, initialValue, alias);
		}
		if(filterEnum.equals(FilterEnum.NOT)){
			return new NotFilter(null, alias);
		}
		if(filterEnum.equals(FilterEnum.OR)){
			return new OrFilter(null, alias);
		}
		if(filterEnum.equals(FilterEnum.STARTING)){
			return new StartingFilter(field, initialValue, alias);
		}
		
		return null;
		
	}
	
	
	public static String contruirFiltroString(FilterBean filterBean){

		String filtro = "";
		filtro += (filterBean.getFieldName() + " ");
		
		IFilter filter = filterBean.getFilterParameter(); 
		
		if(filter != null){
			FilterEnum filterEnum = filter.getFilterBy();
			
			filtro+= filterEnum.getStringFilter();
			
			Boolean stringClass = filterBean.getFieldClass().equals(String.class);
			
			if(!filterEnum.equals(FilterEnum.ISNULL)){
				if(
				   !filterEnum.equals(FilterEnum.AND) && 
				   !filterEnum.equals(FilterEnum.OR) && 
				   !filterEnum.equals(FilterEnum.NOT)){

					Object initialValue = null;
					Object finalValue = null;
					
					if(filterEnum.equals(FilterEnum.EQUAL)){
						initialValue = ((EqualFilter) filter).getInitialValue();
					}
					if(filterEnum.equals(FilterEnum.GREATER_EQUAL)){
						initialValue = ((GreaterEqualFilter) filter).getInitialValue();
					}
					if(filterEnum.equals(FilterEnum.GREATER_THAN)){
						initialValue = ((GreaterThanFilter) filter).getInitialValue();
					}
					if(filterEnum.equals(FilterEnum.LESS_EQUALS)){
						initialValue = ((LessEqualFilter) filter).getInitialValue();
					}
					if(filterEnum.equals(FilterEnum.LESS_THAN)){
						initialValue = ((LessThanFilter) filter).getInitialValue();
					}
					if(filterEnum.equals(FilterEnum.LIKE)){
						initialValue = ((LikeFilter) filter).getInitialValue();
					}
					if(filterEnum.equals(FilterEnum.STARTING)){
						initialValue = ((StartingFilter) filter).getInitialValue();
					}
					if(filterEnum.equals(FilterEnum.ENDING)){
						initialValue = ((EndingFilter) filter).getInitialValue();
					}

					if(filterEnum.equals(FilterEnum.IN)){
						initialValue = ((InFilter) filter).getValues().toString().replace("[", "").replace("]", "");
					}
					if(filterEnum.equals(FilterEnum.BETWEEN)){
						initialValue = ((BetweenFilter) filter).getInitialValue();
						finalValue = ((BetweenFilter) filter).getFinalValue();
					}

					
					if(initialValue != null){
						filtro += " ";
						if(stringClass){
							filtro += "\"";
						}
						
						filtro += initialValue;
	
						if(stringClass){
							filtro += "\"";
						}
					}
					
					if(finalValue != null){

						filtro += " e";
						if(stringClass){
							filtro += " \"";
						}
						
						filtro += finalValue;
	
						if(stringClass){
							filtro += "\"";
						}
					}
					
				}
				else{//se for filtro agrupador;

					
				}
				
				
			}
			
		}
				
				
		
		return filtro;
		
	}
	

}
