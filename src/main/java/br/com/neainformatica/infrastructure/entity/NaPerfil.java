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

import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "NA_PERFIL", schema = "seguranca")
@SequenceGenerator(name = "genNaPerfil", schema = "", sequenceName = "seguranca.seq_na_perfil", allocationSize = 1)
public class NaPerfil implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaPerfil")
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private Integer id;

	@JoinColumn(name = "id_ator", nullable = true, foreignKey = @ForeignKey(name = "fk_na_perfil_na_ator"))
	@ManyToOne(targetEntity = NaAtor.class, fetch = FetchType.LAZY)
	private NaAtor ator;

	@JoinColumn(name = "id_sistema", nullable = false, foreignKey = @ForeignKey(name = "fk_perfil_sistema"))
	@ManyToOne(targetEntity = NaSistema.class, fetch = FetchType.LAZY)
	private NaSistema sistema;

	@Column(name = "CADASTRO_PADRAO", nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	@Filter(name = "Padrao", simpleFilterMethod = EnumFilterMethod.LIKE)
	private EnumSimNao padrao;

	@Column(length = 150, nullable = true, unique = false)
	@Filter(name = "Descrição", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String descricao;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	@JoinColumn(name = "id_na_cliente", nullable = true, foreignKey = @ForeignKey(name = "FK_NA_PERFIL_CLIENTE"))
	@ManyToOne(targetEntity = NaCliente.class, fetch = FetchType.LAZY)
	private NaCliente cliente;

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

	public NaPerfil() {
		super();
		this.padrao = EnumSimNao.NAO;
	}

	public NaPerfil(Integer id, String descricao) {
		super();
		this.id = id;
		this.descricao = descricao;
		this.padrao = EnumSimNao.SIM;
	}

	public NaPerfil(NaSistema sistema, String descricao) {
		super();
		this.sistema = sistema;
		this.descricao = descricao;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public NaSistema getSistema() {
		return sistema;
	}

	public void setSistema(NaSistema sistema) {
		this.sistema = sistema;
	}

	@Override
	public String toString() {
		return descricao;
	}

	public NaAtor getAtor() {
		return ator;
	}

	public void setAtor(NaAtor ator) {
		this.ator = ator;
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
		NaPerfil other = (NaPerfil) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public EnumSimNao getPadrao() {
		return padrao;
	}

	public void setPadrao(EnumSimNao padrao) {
		this.padrao = padrao;
	}

	public NaCliente getCliente() {
		return cliente;
	}

	public void setCliente(NaCliente cliente) {
		this.cliente = cliente;
	}
	
}
