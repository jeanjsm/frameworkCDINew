package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
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

import br.com.neainformatica.infrastructure.enumeration.EnumNaParametroTipoUpload;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_parametro_valor", catalog = "", schema = "public")
public class NaParametroValor implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@SequenceGenerator(name = "genNaParametroValor", allocationSize = 1, schema = "public", sequenceName = "public.seq_na_parametro_valor")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaParametroValor")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@JoinColumn(name = "id_na_parametro", nullable = false, foreignKey = @ForeignKey(name = "fk_parametro_valor_parametro"))
	@ManyToOne(targetEntity = NaParametro.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private NaParametro parametro;

	@Column(length = 20, nullable = true)
	private String chave;

	@Column(length = 20, nullable = true)
	private String chave2;

	@Column(length = 150, nullable = true)
	private String descricao;

	@Column(length = 150, nullable = true)
	private String descricao2;

	@Column(length = 250, nullable = true)
	private String valor;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	@JoinColumn(name = "id_na_parametro_item", nullable = true, foreignKey = @ForeignKey(name = "fk_na_parametro_valor_parametro"))
	@ManyToOne(targetEntity = NaParametroItem.class, fetch = FetchType.LAZY)
	private NaParametroItem naParametroItem;

	@Column(name = "valor_descricao", nullable = true, length = 250)
	private String valorDescricao;

	@Column(name = "VALOR_DESCRICAO_ANTERIOR", nullable = true, length = 250)
	private String valorDescricaoAnterior;

	private byte[] binario;

	@Column(name = "binario_extensao", nullable = true, length = 10)
	private String binario_extensao;

	@Column(name = "binario_tipo", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumNaParametroTipoUpload"),
			@Parameter(name = "identifierMethod", value = "toInt") })
	private EnumNaParametroTipoUpload binarioTipo;

	@Column(name = "valor_texto", nullable = true)
	private String valorTexto;
	

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((parametro == null) ? 0 : parametro.hashCode());
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
		NaParametroValor other = (NaParametroValor) obj;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (parametro == null) {
			if (other.parametro != null)
				return false;
		} else if (!parametro.equals(other.parametro))
			return false;
		return true;
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

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
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

	public NaParametroItem getNaParametroItem() {
		return naParametroItem;
	}

	public void setNaParametroItem(NaParametroItem naParametroItem) {
		this.naParametroItem = naParametroItem;
	}

	public String getValorDescricao() {
		return valorDescricao;
	}

	public void setValorDescricao(String valorDescricao) {
		this.valorDescricao = valorDescricao;
	}

	public String getBinario_extensao() {
		return binario_extensao;
	}

	public void setBinario_extensao(String binario_extensao) {
		this.binario_extensao = binario_extensao;
	}

	public EnumNaParametroTipoUpload getBinarioTipo() {
		return binarioTipo;
	}

	public void setBinarioTipo(EnumNaParametroTipoUpload binarioTipo) {
		this.binarioTipo = binarioTipo;
	}

	public String getChave2() {
		return chave2;
	}

	public void setChave2(String chave2) {
		this.chave2 = chave2;
	}

	public String getDescricao2() {
		return descricao2;
	}

	public void setDescricao2(String descricao2) {
		this.descricao2 = descricao2;
	}

	public String getValorDescricaoAnterior() {
		return valorDescricaoAnterior;
	}

	public void setValorDescricaoAnterior(String valorDescricaoAnterior) {
		this.valorDescricaoAnterior = valorDescricaoAnterior;
	}

	public String getValorTexto() {
		return valorTexto;
	}

	public void setValorTexto(String valorTexto) {
		this.valorTexto = valorTexto;
	}



}
