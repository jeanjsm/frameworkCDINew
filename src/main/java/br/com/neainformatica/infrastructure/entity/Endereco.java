package br.com.neainformatica.infrastructure.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoZona;

@Embeddable
public class Endereco {

	@Column(name = "logradouro_tipo_zona")
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = { @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumTipoZona"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumTipoZona tipoZona;

	@Column(name = "logradouro_cep", length = 8)
	private String cep;

	@JoinColumn(name = "id_tipo_logradouro", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	private NaTipoLogradouro tipoLogradouro;

	@JoinColumn(name = "id_logradouro", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	private NaLogradouro logradouro;

	@Column(name = "logradouro_nome", length = 150)
	private String logradouroNome;

	@JoinColumn(name = "id_tipo_bairro", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	private NaTipoBairro tipoBairro;

	@Column(name = "bairro_nome", length = 150)
	private String bairroNome;

	@JoinColumn(name = "id_bairro", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	private NaBairro idBairro;

	@Column(name = "logradouro_numero")
	private Integer logradouroNumero;

	@Column(nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = { @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao logradouroSemNumero;

	@Column(name = "logradouro_quadra", length = 10)
	private String logradouroQuadra;

	@Column(name = "logradouro_lote", length = 10)
	private String logradouroLote;

	@Column(name = "logradouro_complemento", length = 150)
	private String logradouroComplemento;

	@JoinColumn(name = "id_cidade_logradouro", referencedColumnName = "id")
	@ManyToOne(fetch = FetchType.LAZY)
	private NaCidade CidadeLogradouro;

	public Endereco() {
		tipoZona = EnumTipoZona.URBANA;
		logradouroSemNumero = EnumSimNao.NAO;
	}

	public EnumTipoZona getTipoZona() {
		return tipoZona;
	}

	public void setTipoZona(EnumTipoZona tipoZona) {
		this.tipoZona = tipoZona;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public NaTipoLogradouro getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(NaTipoLogradouro tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public NaLogradouro getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(NaLogradouro logradouro) {
		this.logradouro = logradouro;
	}

	public String getLogradouroNome() {
		return logradouroNome;
	}

	public void setLogradouroNome(String logradouroNome) {
		this.logradouroNome = logradouroNome;
	}

	public NaTipoBairro getTipoBairro() {
		return tipoBairro;
	}

	public void setTipoBairro(NaTipoBairro tipoBairro) {
		this.tipoBairro = tipoBairro;
	}

	public String getBairroNome() {
		return bairroNome;
	}

	public void setBairroNome(String bairroNome) {
		this.bairroNome = bairroNome;
	}

	public NaBairro getIdBairro() {
		return idBairro;
	}

	public void setIdBairro(NaBairro idBairro) {
		this.idBairro = idBairro;
	}

	public Integer getLogradouroNumero() {
		return logradouroNumero;
	}

	public void setLogradouroNumero(Integer logradouroNumero) {
		this.logradouroNumero = logradouroNumero;
	}

	public EnumSimNao getLogradouroSemNumero() {
		return logradouroSemNumero;
	}

	public void setLogradouroSemNumero(EnumSimNao logradouroSemNumero) {
		this.logradouroSemNumero = logradouroSemNumero;
	}

	public String getLogradouroQuadra() {
		return logradouroQuadra;
	}

	public void setLogradouroQuadra(String logradouroQuadra) {
		this.logradouroQuadra = logradouroQuadra;
	}

	public String getLogradouroLote() {
		return logradouroLote;
	}

	public void setLogradouroLote(String logradouroLote) {
		this.logradouroLote = logradouroLote;
	}

	public String getLogradouroComplemento() {
		return logradouroComplemento;
	}

	public void setLogradouroComplemento(String logradouroComplemento) {
		this.logradouroComplemento = logradouroComplemento;
	}

	public NaCidade getCidadeLogradouro() {
		return CidadeLogradouro;
	}

	public void setCidadeLogradouro(NaCidade cidadeLogradouro) {
		CidadeLogradouro = cidadeLogradouro;
	}

}
