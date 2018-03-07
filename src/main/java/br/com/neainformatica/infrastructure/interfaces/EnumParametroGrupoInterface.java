package br.com.neainformatica.infrastructure.interfaces;

import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;

public interface EnumParametroGrupoInterface {

	/**
	 * Nome do grupo
	 * 
	 * @return
	 */
	public String getNome();

	/**
	 * Nivel de acesso mínimo, para determinar quem pode alterar o valor do
	 * grupo
	 * 
	 * @return
	 */
	public EnumNivelUsuario getNivelUsuario();

	/**
	 * Código do grupo
	 * 
	 * @return
	 */
	public Integer getCodigo();
	

}
