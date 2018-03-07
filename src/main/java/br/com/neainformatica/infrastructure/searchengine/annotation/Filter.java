package br.com.neainformatica.infrastructure.searchengine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

/**
 * 
 * 
 * Anotação para campos da entidade do sistema onde define: <br/>
 * 		- um nome amigavel ao campo da entidade <br/>
 *      - nome do campo de filtro (padrão vazio, será usado o nome da propriedade anotada como filtro)
 * 		- se o campo é um filtro da grid;  <br/>
 * 		- qual é o método de filtragem padrão; <br/>
 * 		- se o campo será ou não um filtro avançado; <br/>
 * 
 * @author Pedro Sanchez
 * */

//Onde a anotaçõeo pode ser aplicada FIELD = atributos, TYPE = em qualquer lugar
@Target (ElementType.FIELD)
//qual o escopo da annotation (SOURCE, CLASS e RUNTIME)
@Retention (RetentionPolicy.RUNTIME)

public @interface Filter {
	/**Define um campo amigavel que será exibido ao usuário do sistema*/
	public String name();
	
	/**Define o atributo de filtro, (padrão vazio, será usado o nome da propriedade anotada como filtro)*/
	public String filterProperty() default "";
	
	/**Define um método de filtragem padrão para o campo, por padrão ele vem com o tipo EnumFilterMethod.DEFAULT
	 * Caso não se altere esse tipo o campo <strong>não</strong> será um filtro da grid
	 * @see EnumFilterMethod
	 * */
	public EnumFilterMethod simpleFilterMethod() default EnumFilterMethod.DEFAULT;
	
	/**
	 * Define se o campo será ou não um campo da pesquisa avançada. Por padrão o campo será definido como campo da pesquisa avançada
	 * */
	public boolean advancedFilter() default true;
	
	public boolean disabled() default false;
}
