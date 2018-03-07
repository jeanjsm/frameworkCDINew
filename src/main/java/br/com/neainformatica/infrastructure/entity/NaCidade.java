package br.com.neainformatica.infrastructure.entity;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 21/07/2014 10:35:27
 @Author  = NELSON
 @Versão da Classe = 15

 -----------------------------------------------------------------------------------------------------------------------------
 */

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
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_cidade", catalog = "", schema = "public")
public class NaCidade implements Serializable {
	private static final long serialVersionUID = 1L;

	@Filter(name = "Id", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Id
	@SequenceGenerator(name = "genNaCidade", allocationSize = 1, schema = "public", sequenceName = "seq_na_cidade")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaCidade")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@Filter(name = "Descrição", simpleFilterMethod = EnumFilterMethod.LIKE)
	@Column(name = "descricao", nullable = true, length = 150)
	private String descricao;

	@Filter(name = "Código IBGE", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "codigo_ibge", nullable = true, length = 10)
	private String codigoIbge;

	@Filter(name = "Estado", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@ManyToOne(targetEntity = NaEstado.class, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_estado", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_cidade_estado"))
	// @ForeignKey(name = "fk_na_cidade_estado")
	private NaEstado estado;

	@Filter(name = "Ativo", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "ativo", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao ativo;

	@Filter(name = "Capital", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "capital", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao capital;

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

	@Transient
	private String estadoSigla;

	@Transient
	private String nomeFormatado;
	
	
	public NaCidade() {
		this.capital = EnumSimNao.NAO;
		this.ativo = EnumSimNao.SIM;
	}
	

	public String getEstadoSigla() {
		if (this.estado != null)
			return this.estado.getSigla();
		else
			return null;
	}
	
	public String getNomeFormatado() {

		if (this.estado != null)
			this.nomeFormatado = this.descricao + " - " + this.estado.getSigla();
		else
			this.nomeFormatado = this.descricao;

		return nomeFormatado;
	}	

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCodigoIbge() {
		return this.codigoIbge;
	}

	public void setCodigoIbge(String codigoIbge) {
		this.codigoIbge = codigoIbge;
	}

	public NaEstado getEstado() {
		return this.estado;
	}

	public void setEstado(NaEstado estado) {
		this.estado = estado;
	}

	public EnumSimNao getAtivo() {
		return this.ativo;
	}

	public void setAtivo(EnumSimNao ativo) {
		this.ativo = ativo;
	}

	public EnumSimNao getCapital() {
		return this.capital;
	}

	public void setCapital(EnumSimNao capital) {
		this.capital = capital;
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
		NaCidade other = (NaCidade) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
    public String toString() {
        if (estado == null) {
            return descricao;
        }
	    return  descricao + " - " + estado.getSigla();
    }
	

	

}
