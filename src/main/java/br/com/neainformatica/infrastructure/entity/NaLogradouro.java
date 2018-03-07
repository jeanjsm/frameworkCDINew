package br.com.neainformatica.infrastructure.entity;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 28/04/2014 09:00:35
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

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_logradouro", catalog = "", schema = "public")
public class NaLogradouro implements Serializable {
	private static final long serialVersionUID = 1L;

	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Id
	@SequenceGenerator(name = "genNaLogradouro", allocationSize = 1, schema = "public", sequenceName = "seq_na_logradouro")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaLogradouro")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@Filter(name = "Tipo", simpleFilterMethod = EnumFilterMethod.LIKE)
	@ManyToOne(targetEntity=NaTipoLogradouro.class, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_tipo_logradouro", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_logradouro_tipo"))
	// @ForeignKey(name = "fk_logradouro_tipo")
	private NaTipoLogradouro tipologradouro;

	@Filter(name = "Descrição", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "descricao", nullable = true, length = 255)
	private String descricao;
	
	@ManyToOne(targetEntity=NaBairro.class, fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_bairro_ini", referencedColumnName = "id", nullable = false, foreignKey = @ForeignKey(name = "fk_na_logradouro_bairro_ini"))
	private NaBairro bairroIni;
	
	@ManyToOne(targetEntity=NaBairro.class, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "id_bairro_fim", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_logradouro_bairro_fim"))
	private NaBairro bairroFim;
	
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

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NaTipoLogradouro getTipologradouro() {
		return this.tipologradouro;
	}

	public void setTipologradouro(NaTipoLogradouro tipologradouro) {
		this.tipologradouro = tipologradouro;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		NaLogradouro other = (NaLogradouro) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public NaBairro getBairroIni() {
		return bairroIni;
	}

	public void setBairroIni(NaBairro bairroIni) {
		this.bairroIni = bairroIni;
	}

	public NaBairro getBairroFim() {
		return bairroFim;
	}

	public void setBairroFim(NaBairro bairroFim) {
		this.bairroFim = bairroFim;
	}

}
