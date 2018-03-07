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
@Table(name = "na_parametro_item", catalog = "", schema = "public")
public class NaParametroItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@SequenceGenerator(name = "seq_na_parametro_item", allocationSize = 1, schema = "public", sequenceName = "public.seq_na_parametro_item")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_na_parametro_item")
	@Column(name = "ID", nullable = true)	
	private Integer id;

	@JoinColumn(name = "id_na_parametro", nullable = false, foreignKey = @ForeignKey(name = "fk_parametro_item_parametro"))
	@ManyToOne(targetEntity = NaParametro.class, fetch = FetchType.LAZY)	
	private NaParametro parametro;

	@Filter(name = "Descrição", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "descricao", nullable = false, length = 150)
	private String descricao;

	@Column(length = 250, nullable = true)
	private String valor;

	@Column(nullable = true)
	private byte[] binario;
	
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

	public NaParametro getParametro() {
		return parametro;
	}

	public void setParametro(NaParametro parametro) {
		this.parametro = parametro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public byte[] getBinario() {
		return binario;
	}

	public void setBinario(byte[] binario) {
		this.binario = binario;
	}

}
