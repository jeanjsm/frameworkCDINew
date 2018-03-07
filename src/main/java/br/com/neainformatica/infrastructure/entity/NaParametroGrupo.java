package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_parametro_grupo", catalog = "", schema = "public")
public class NaParametroGrupo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@SequenceGenerator(name = "genNaParametroGrupo", allocationSize = 1, schema = "public", sequenceName = "public.seq_na_parametro_grupo")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaParametroGrupo")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "nome", nullable = false, length = 150)
	private String nome;

	@Column(name = "nivel_usuario", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = { @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario"),
			@Parameter(name = "identifierMethod", value = "toInt") })
	private EnumNivelUsuario nivelUsuario;

	@JoinColumn(name = "id_sistema", nullable = false, foreignKey = @ForeignKey(name = "fk_parametro_grupo_sistema"))
	@ManyToOne(targetEntity = NaSistema.class, fetch = FetchType.LAZY)
	private NaSistema sistema;
	
	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;
	
	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;
	
	

	public Integer getIdNaUsuarioAuditoria() {
		return idNaUsuarioAuditoria;
	}

	public void setIdNaUsuarioAuditoria(Integer idNaUsuarioAuditoria) {
		this.idNaUsuarioAuditoria = idNaUsuarioAuditoria;
	}

	public Integer getIdNaSistemaAuditoria() {
		return idNaSistemaAuditoria;
	}

	public void setIdNaSistemaAuditoria(Integer idNaSistemaAuditoria) {
		this.idNaSistemaAuditoria = idNaSistemaAuditoria;
	}

	public NaParametroGrupo() {
		this.nivelUsuario = EnumNivelUsuario.ADMINISTRADOR;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public NaSistema getSistema() {
		return sistema;
	}

	public void setSistema(NaSistema sistema) {
		this.sistema = sistema;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NaParametroGrupo other = (NaParametroGrupo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	

}
