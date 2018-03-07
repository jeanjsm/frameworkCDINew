package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "NA_GESTAO", schema = "public")
public class NaGestao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private Integer id;

	@Column(name = "descricao", length = 150, nullable = false)
	@Filter(name = "Descrição", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String descricao;

	/*
	 * O ideal era que o e-mail fosse pego atraves do usuário, mas por enquanto
	 * vamos deixa-lo aqui
	 */
	@Column(name = "email_responsavel", length = 255, nullable = true)
	@Filter(name = "E-mail Responsável", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String emailResponsavel;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	@OneToMany(mappedBy = "naGestao", fetch = FetchType.LAZY)
	private List<NaSistema> listaNaSistema;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao", nullable = true)
	@Filter(name = "Data Alteração", simpleFilterMethod = EnumFilterMethod.DEFAULT, disabled = true)
	private Date dataAlteracao;
	
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

	public List<NaSistema> getListaNaSistema() {
		return listaNaSistema;
	}

	public void setListaNaSistema(List<NaSistema> listaNaSistema) {
		this.listaNaSistema = listaNaSistema;
	}
	
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descricao == null) ? 0 : descricao.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NaGestao))
			return false;
		NaGestao other = (NaGestao) obj;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		return true;
	}

	public String getEmailResponsavel() {
		return emailResponsavel;
	}

	public void setEmailResponsavel(String emailResponsavel) {
		this.emailResponsavel = emailResponsavel;
	}

}
