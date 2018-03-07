package br.com.neainformatica.infrastructure.entity;

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
@Table(name = "na_estado_regiao", catalog = "", schema = "public")
public class NaEstadoRegiao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@SequenceGenerator(name = "genNaEstadoRegiao", allocationSize = 1, schema = "public", sequenceName = "seq_na_estado_regiao")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaEstadoRegiao")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@Filter(name = "Código do IBGE", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "codigo_ibge", nullable = true)
	private Integer ibge;

	@Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.LIKE)
	@Column(name = "nome", nullable = true, length = 250)
	private String nome;
	
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

	public Integer getIbge() {
		return ibge;
	}

	public void setIbge(Integer ibge) {
		this.ibge = ibge;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

}
