package br.com.neainformatica.infrastructure.enumeration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum EnumNivelUsuario {

	SOMENTE_LEITURA(2, "Somente Leitura"), 
	USUARIO(4, "Usuário"), 
	SUPERVISOR(8, "Supervisor"), 
	ADMINISTRADOR(16, "Administrador"), 
	SUPORTE(32, "Suporte"), 
	ROOT(64, "Root");

	private int id;
	private String descricao;
		
	public static EnumNivelUsuario valueOf(int id) {
		switch (id) {
		case 2: return SOMENTE_LEITURA;
		case 4: return USUARIO;
		case 8: return SUPERVISOR;
		case 16: return ADMINISTRADOR;
		case 32: return SUPORTE;
		case 64: return ROOT;
		default: return null;
		}
	}

	public int toInt() {
		return this.id;
	}

	private EnumNivelUsuario(int id, String descricao) {
		this.id = id;
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public static List<EnumNivelUsuario> getNiveisAbaixo(EnumNivelUsuario nivelMaximo) {
		
		List<EnumNivelUsuario> lista = Arrays.asList(EnumNivelUsuario.values());
		List<EnumNivelUsuario> retorno = new ArrayList<>();
		
		for (EnumNivelUsuario enumNivelUsuario : lista) {
			if (enumNivelUsuario.getId() < nivelMaximo.getId())
				retorno.add(enumNivelUsuario);
		}
		
		if (nivelMaximo.equals(EnumNivelUsuario.ADMINISTRADOR))
			retorno.add(EnumNivelUsuario.ADMINISTRADOR);
		return retorno;
	}
	
	public static boolean nivelMenorOuIgualAdministrador(EnumNivelUsuario nivel) {		
		if (nivel == null)
			return false;
		
		if (nivel.getId() <= EnumNivelUsuario.ADMINISTRADOR.toInt())
			return true;
		
		return false;				
	}
	
	public static boolean nivelMenorAtualOuIgualAdministrador(EnumNivelUsuario nivelASerVerificado, EnumNivelUsuario nivelAtual) {		
		if (nivelASerVerificado == null || nivelAtual == null )
			return false;
		
		if (nivelASerVerificado.toInt() <= nivelAtual.toInt())
			return true;
		
		if (nivelAtual.equals(EnumNivelUsuario.ADMINISTRADOR) &&  nivelASerVerificado.equals(EnumNivelUsuario.ADMINISTRADOR)  )
			return true;				
		
		return false;				
	}

}
