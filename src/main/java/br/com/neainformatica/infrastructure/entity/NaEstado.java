package br.com.neainformatica.infrastructure.entity;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 18/07/2014 18:18:05
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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.auditoria.NaAuditoriaInterface;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_estado", catalog = "", schema = "public")
public class NaEstado  implements Serializable , NaAuditoriaInterface {
	private static final long serialVersionUID = 1L;

	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Id
	@SequenceGenerator(name = "genNaEstado", allocationSize = 1, schema = "public", sequenceName = "public.seq_na_estado")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaEstado")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@Filter(name = "Código do IBGE", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "codigo_ibge", nullable = true, length = 2)
	private String ibge;

	@Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.LIKE)
	@Column(name = "nome", nullable = true, length = 250)
	private String nome;

	@Filter(name = "Sigla", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "sigla", nullable = true, length = 2)
	private String sigla;

	@Filter(name = "Pais", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@ManyToOne(targetEntity = NaPais.class, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "id_pais", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_estado_pais"))
	// @ForeignKey(name = "fk_na_cidade_pais")
	private NaPais pais;

	@Filter(name = "Região", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
	@ManyToOne(targetEntity = NaEstadoRegiao.class, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "id_regiao", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_estado_regiao"))
	// @ForeignKey(name = "fk_na_cidade_pais")
	private NaEstadoRegiao regiao;

	@Filter(name = "Ativo", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	@Column(name = "ativo", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao ativo;

	@Filter(name = "Nome Formal", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	@Column(name = "nome_formal", nullable = true, length = 200)
	private String nomeFormal;

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

	public String getIbge() {
		return this.ibge;
	}

	public void setIbge(String ibge) {
		this.ibge = ibge;
	}

	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public NaPais getPais() {
		return this.pais;
	}

	public void setPais(NaPais pais) {
		this.pais = pais;
	}

	public EnumSimNao getAtivo() {
		return this.ativo;
	}

	public void setAtivo(EnumSimNao ativo) {
		this.ativo = ativo;
	}

	public String getNomeFormal() {
		return this.nomeFormal;
	}

	public void setNomeFormal(String nomeFormal) {
		this.nomeFormal = nomeFormal;
	}

	public NaEstadoRegiao getRegiao() {
		return regiao;
	}

	public void setRegiao(NaEstadoRegiao regiao) {
		this.regiao = regiao;
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
		NaEstado other = (NaEstado) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toAuditoria() {
		return "Id: " + this.id + " ibge: " + this.ibge + " nome: " + this.nome;
	}

	@Override
	public String toString() {
		return "NaEstado [id=" + id + ", nome=" + nome + ", sigla=" + sigla + "]";
	}
	
	
	

}
