package br.com.neainformatica.infrastructure.searchengine.converter;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.bean.FilterBean;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.GreaterEqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.GreaterThanFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LessEqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LessThanFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LikeFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

/**
 * Classe Utilitária da Engine de Busca Dinamica usada para realizar conversão de dados pertinentes a esta engine
 * 
 * @author Pedro Sanchez
 * */
public class Converter {
	
	/**
	 * Converte o dado do tipo String informado na tela para o dado requerido pelo campo a ser pesquisado
	 * 
	 *  @param filterValue campo string a ser convertido caso necessário
	 *  @param resultClass informa para que tipo de dado que o filterValue deverá ser convertido 
	 * */
	public static Object convertFrom(String filterValue, Class<?> resultClass){

		if(resultClass.equals(String.class)){
			return filterValue;
		}
		
		if(resultClass.equals(Integer.class)){				
			return Integer.parseInt(filterValue);
		}
		
		if(resultClass.equals(Float.class)){
			return Float.parseFloat(filterValue);
		}
		
		if(resultClass.equals(Double.class)){
			return Double.parseDouble(filterValue);
		}
		
		if(resultClass.equals(BigDecimal.class)){
			return new BigDecimal(filterValue);
		}
		
		if(resultClass.equals(Boolean.class)){
			return Boolean.parseBoolean(filterValue);
		}
				
		if(resultClass.equals(Date.class)){
			//converter data para string
			return filterValue;
		}
		
		return filterValue;
		
		//buscar conversão para enum
			
		
	}
	
	/**
	 * Converte o campo da entidade para o Bean utilizado para montar a pesquisa
	 * 
	 * @param field campo que será convertido
	 * @param superProperty campo string que indica se nesta pesquisa quais os nives de join necessário para chegar neste campo
	 * @param fieldName propriedade que será usada como filtro ao gerar as criterias
	 * @param alias alias que deverá ser criado ao gerar as criterias, para que joins funcionem corretamente
	 * @param preFilterName prefixo que será colocado no nome do filtro, para exibição
	 * 
	 * @return Retorna o Bean utilizado para construção da consulta.
	 * 
	 * @see FilterBean
	 * */
	public static FilterBean convertField(Field field,  String superProperty, String fieldName, String alias, String preFilterName){

		Filter simplefilter = field.getAnnotation(Filter.class);

		if (simplefilter != null && !simplefilter.simpleFilterMethod().equals(EnumFilterMethod.DEFAULT)) {
			IFilter filterParameter = null;
			
			if(simplefilter.simpleFilterMethod().equals(EnumFilterMethod.EQUALS)){					
				filterParameter = new EqualFilter(superProperty + fieldName, "", alias);
			}
			else if(simplefilter.simpleFilterMethod().equals(EnumFilterMethod.LIKE)){					
				filterParameter = new LikeFilter(superProperty + fieldName, "", alias);
			}
			else if(simplefilter.simpleFilterMethod().equals(EnumFilterMethod.GREATER_EQUAL)){					
				filterParameter = new GreaterEqualFilter(superProperty + fieldName, "", alias);
			}
			else if(simplefilter.simpleFilterMethod().equals(EnumFilterMethod.GREATER_THAN)){					
				filterParameter = new GreaterThanFilter(superProperty + fieldName, "", alias);
			}
			else if(simplefilter.simpleFilterMethod().equals(EnumFilterMethod.LESS_EQUAL)){					
				filterParameter = new LessEqualFilter(superProperty + fieldName, "", alias);
			}
			else if(simplefilter.simpleFilterMethod().equals(EnumFilterMethod.LESS_THAN)){					
				filterParameter = new LessThanFilter(superProperty + fieldName, "", alias);
			}
			else if(simplefilter.simpleFilterMethod().equals(EnumFilterMethod.DIFERENTE)){					
				//filterParameter = new NotFilter(new EqualFilter(superProperty + fieldName, "", alias));
			}
			
			return new FilterBean(preFilterName+simplefilter.name(), field.getType(), filterParameter);
		}
		return null;
	}
		
}
