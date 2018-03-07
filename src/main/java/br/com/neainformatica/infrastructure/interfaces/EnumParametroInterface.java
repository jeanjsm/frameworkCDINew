package br.com.neainformatica.infrastructure.interfaces;

import br.com.neainformatica.infrastructure.enumeration.EnumNaParametroTipo;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;

public interface EnumParametroInterface {

	/**
	 * Campo identificador, normalmente sem acentos e sem espaços.
	 * @return
	 */
	public String getChave();
	
	/**
	 * Nome para exibição
	 * @return
	 */
	public String getNome();
	
	/**
	 * Descrição explicativa
	 * @return
	 */
	public String getDescricao();
	
	/**
	 * Valor default para o caso do parâmetro não existir
	 * @return
	 */
	public String getValorPadrao();
	
	/**
	 * Tipo
	 * @return
	 */
	public EnumNaParametroTipo getTipo();
	
	/**
	 * Grupo para facilitar na identificação
	 * @return
	 */
	public EnumParametroGrupoInterface getGrupo();
	
	/**
	 * Nivel de acesso mínimo, para determinar quem pode alterar o valor do parâmetro
	 * @return
	 */
	public EnumNivelUsuario getNivelUsuario();
	
	/**
	 * O valor do parâmetro será fixo? 
	 * Não será divido em gestão ou empresas
	 */
	public EnumSimNao getValorFixo();

}
