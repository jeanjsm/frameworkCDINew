package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.auditoria.NaAuditoriaInterface;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoAtor;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_ator", catalog = "", schema = "seguranca")
@SequenceGenerator(name = "genNaAtor", allocationSize = 1, schema = "seguranca", sequenceName = "seguranca.seq_na_ator")
public class NaAtor   implements Serializable ,NaAuditoriaInterface {

	private static final long serialVersionUID = 1L;

	@Id
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaAtor")
	@Column(name = "id", nullable = true)
	private Integer id;

	@Column(name = "nome", length = 150, nullable = false)
	@Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String nome;

	@Column(name = "tipo", nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumTipoAtor"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	@Filter(name = "Tipo", simpleFilterMethod = EnumFilterMethod.LIKE)
	private EnumTipoAtor tipo;

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

	public NaAtor() {
	}

	public NaAtor(String nome, EnumTipoAtor tipo) {
		this.nome = nome;
		this.tipo = tipo;
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

	public EnumTipoAtor getTipo() {
		return tipo;
	}

	public void setTipo(EnumTipoAtor tipo) {
		this.tipo = tipo;
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
		NaAtor other = (NaAtor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toAuditoria() {
		return "ID: " + this.id + " Nome: " + this.nome + " Tipo: " + this.tipo.getDescricao();
	}

}
