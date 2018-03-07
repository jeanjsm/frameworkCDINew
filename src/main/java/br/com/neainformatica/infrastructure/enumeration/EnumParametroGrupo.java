package br.com.neainformatica.infrastructure.enumeration;

import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.interfaces.EnumParametroGrupoInterface;

public enum EnumParametroGrupo implements EnumParametroGrupoInterface {
	DEFAULT("Infraestrutura", EnumNivelUsuario.ADMINISTRADOR, 1);

	private String nome;
	private EnumNivelUsuario nivelUsuario;
	private Integer codigo;

	private EnumParametroGrupo(String nome, EnumNivelUsuario nivelUsuario, Integer codigo) {
		this.nome = nome;
		this.nivelUsuario = nivelUsuario;
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public EnumNivelUsuario getNivelUsuario() {
		return nivelUsuario;
	}

	public void setNivelUsuario(EnumNivelUsuario nivelUsuario) {
		this.nivelUsuario = nivelUsuario;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

}
