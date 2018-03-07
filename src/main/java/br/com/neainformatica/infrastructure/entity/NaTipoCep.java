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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_tipo_cep", catalog = "", schema = "public")
public class NaTipoCep implements Serializable {
	private static final long serialVersionUID = 1L;

	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Id
	@SequenceGenerator(name = "genNaTipoCep", allocationSize = 1, schema = "public", sequenceName = "seq_na_tipo_cep")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaTipoCep")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@Filter(name = "Descrição", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "descricao", nullable = true, length = 255)
	private String descricao;
	
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
		NaTipoCep other = (NaTipoCep) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
