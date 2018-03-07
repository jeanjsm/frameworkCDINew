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
import javax.validation.constraints.NotNull;

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_auditoria_item", schema = "auditoria")
@SequenceGenerator(name = "genNaAuditoriaItem", schema = "", sequenceName = "auditoria.seq_na_auditoria_item", allocationSize = 1)
public class NaAuditoriaItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaAuditoriaItem")
	private Integer id;

	@NotNull
	@ManyToOne(targetEntity=NaAuditoria.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_auditoria", foreignKey = @ForeignKey(name = "fk_na_auditoria_item_auditoria"))
	@Filter(name = "Auditoria", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private NaAuditoria auditoria;

	@Column(name = "campo", length = 50)
	@Filter(name = "Campo", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private String campo;

	@Column(name = "valor_anterior", length = 1000)
	@Filter(name = "Valor Anterior", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private String valorAnterior;

	@Column(name = "valor_novo", length = 1000)
	@Filter(name = "Valor Novo", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private String valorNovo;

	public NaAuditoriaItem() {
		super();
	}

	public NaAuditoriaItem(Integer id, NaAuditoria auditoria, String campo, String valorAnterior, String valorNovo) {
		super();
		this.id = id;
		this.auditoria = auditoria;
		this.campo = campo;
		this.valorAnterior = valorAnterior;
		this.valorNovo = valorNovo;
	}

	public NaAuditoriaItem(NaAuditoria auditoria, String campo, String valorAnterior, String valorNovo) {
		super();
		this.auditoria = auditoria;
		this.campo = campo;

		if (valorAnterior != null) {
			if (valorAnterior.length() > 1000)
				this.valorAnterior = valorAnterior.substring(0, 999);
			else
				this.valorAnterior = valorAnterior;
		}

		if (valorNovo != null) {
			if (valorNovo.length() > 1000)
				this.valorNovo = valorNovo.substring(0, 999);
			else
				this.valorNovo = valorNovo;
		}

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NaAuditoria getAuditoria() {
		return auditoria;
	}

	public void setAuditoria(NaAuditoria auditoria) {
		this.auditoria = auditoria;
	}

	public String getCampo() {
		return campo;
	}

	public void setCampo(String campo) {
		this.campo = campo;
	}

	public String getValorAnterior() {
		return valorAnterior;
	}

	public void setValorAnterior(String valorAnterior) {
		this.valorAnterior = valorAnterior.substring(0, 249);
	}

	public String getValorNovo() {
		return valorNovo;
	}

	public void setValorNovo(String valorNovo) {
		this.valorNovo = valorNovo.substring(0, 249);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((auditoria == null) ? 0 : auditoria.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NaAuditoriaItem))
			return false;
		NaAuditoriaItem other = (NaAuditoriaItem) obj;
		if (auditoria == null) {
			if (other.auditoria != null)
				return false;
		} else if (!auditoria.equals(other.auditoria))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return campo + " - " + valorAnterior + " - " + valorNovo;
	}

}
