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

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_bairro", catalog = "", schema = "public")
public class NaBairro implements Serializable {
	private static final long serialVersionUID = 1L;

	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Id
	@SequenceGenerator(name = "genNaBairro", allocationSize = 1, schema = "public", sequenceName = "seq_na_bairro")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaBairro")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@Filter(name = "Tipo", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@ManyToOne(targetEntity = NaTipoBairro.class, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_tipo_bairro", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_bairro_tipo"))
	// @ForeignKey(name = "fk_bairro_tipo")
	private NaTipoBairro tipoBairro;

	@ManyToOne(targetEntity = NaCidade.class, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "id_cidade", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_bairro_na_cidade"))
	private NaCidade cidade;

	@Filter(name = "Descrição", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "descricao", nullable = true, length = 150)
	private String descricao;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	public Integer getId() {
		return this.id;
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

	public void setId(Integer id) {
		this.id = id;
	}

	public NaTipoBairro getTipoBairro() {
		return this.tipoBairro;
	}

	public void setTipoBairro(NaTipoBairro tipoBairro) {
		this.tipoBairro = tipoBairro;
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
		NaBairro other = (NaBairro) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public NaCidade getCidade() {
		return cidade;
	}

	public void setCidade(NaCidade cidade) {
		this.cidade = cidade;
	}

	@Override
	public String toString() {
		return this.getDescricao();
	}
}
