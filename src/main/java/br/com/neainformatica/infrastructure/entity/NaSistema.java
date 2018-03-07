package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoAmbiente;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoBancoDados;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;
import br.com.neainformatica.infrastructure.tools.Tools;

@Entity
@Table(name = "NA_SISTEMA", schema = "public")
public class NaSistema implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * O campo ID deve ser sempre o mesmo código cadastrado no sistema de
	 * atendimento
	 */
	@Id
	@Column(name = "ID")
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private Integer id;

	@Column(length = 150, nullable = false)
	@Filter(name = "Sistema", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String sistema;

	@Column(nullable = true, name = "situacao_acesso")
	private String situacaoAcessoHash;

	@Transient
	private EnumSituacaoAcesso situacaoAcesso;

	@Column(name = "link_externo", nullable = true)
	@Filter(name = "Link Externo", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
	private String linkExterno;

	@Column(name = "versao_base", nullable = true)
	private Integer versaoBase;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	@Filter(name = "Tipo Ambiente", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "tipo_ambiente", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumTipoAmbiente"),
			@Parameter(name = "identifierMethod", value = "toInt") })
	private EnumTipoAmbiente tipoAmbiente;

	@Filter(name = "Tipo Banco", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	@Column(name = "tipo_banco_dados", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumTipoBancoDados"),
			@Parameter(name = "identifierMethod", value = "toInt") })
	private EnumTipoBancoDados tipoBancoDados;

	@Column(name = "id_versao_atual", nullable = true)
	private Integer idVersaoAtual;

	@Filter(name = "Ativo", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "ativo", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao ativo;

	@Filter(name = "Gestão", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	@ManyToOne(targetEntity = NaGestao.class, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "id_na_gestao", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_sistema_gestao"))
	private NaGestao naGestao;

	@Column(name = "versao_sistema", length = 20, nullable = true)
	@Filter(name = "Versão Sistema", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
	private String versaoSistema;

/*	@Filter(name = "Origem Sincronismo", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	@Column(name = "origem_sincronismo", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumOrigemSincronismo origemSincronismo;
*/
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao", nullable = true)
	@Filter(name = "Data Alteração", simpleFilterMethod = EnumFilterMethod.DEFAULT, disabled = true)
	private Date dataAlteracao;

	@Filter(name = "Base Principal", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	@Column(name = "base_principal", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao basePrincipal;

	@Filter(name = "Permite Bloqueio", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	@Column(name = "permite_bloqueio", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao permiteBloqueio;

	public NaSistema() {
		this.situacaoAcesso = EnumSituacaoAcesso.ATIVO;
		this.ativo = EnumSimNao.SIM;
		this.basePrincipal = EnumSimNao.NAO;
		this.tipoAmbiente = EnumTipoAmbiente.WEB;
//		this.origemSincronismo = EnumOrigemSincronismo.CLIENTE;
		this.tipoBancoDados = EnumTipoBancoDados.FIREBIRD;
		this.versaoSistema = "0.0.0.0";
	}

	public NaSistema(Integer idSistema, String sistema) {
		this.id = idSistema;
		this.sistema = sistema;
		this.situacaoAcesso = EnumSituacaoAcesso.ATIVO;
		this.ativo = EnumSimNao.SIM;
		this.basePrincipal = EnumSimNao.NAO;
		this.tipoAmbiente = EnumTipoAmbiente.WEB;
//		this.origemSincronismo = EnumOrigemSincronismo.CLIENTE;
		this.tipoBancoDados = EnumTipoBancoDados.FIREBIRD;
		this.versaoSistema = "0.0.0.0";
	}

	public EnumSituacaoAcesso getSituacaoAcesso() {

		if (this.situacaoAcesso == null) {
			if (this.situacaoAcessoHash == null || this.situacaoAcessoHash.trim().equals(""))
				setSituacaoAcesso(EnumSituacaoAcesso.BLOQUEADO);
			else {

				if (this.situacaoAcessoHash.equalsIgnoreCase(Tools.geraMD5(this.id.toString() + String.valueOf(EnumSituacaoAcesso.ATIVO.getId()) + "SNEA")))
					this.situacaoAcesso = EnumSituacaoAcesso.ATIVO;
				else if (this.situacaoAcessoHash.equalsIgnoreCase(Tools.geraMD5(this.id.toString() + String.valueOf(EnumSituacaoAcesso.SOMENTE_LEITURA.getId()) + "SNEA")))
					this.situacaoAcesso = EnumSituacaoAcesso.SOMENTE_LEITURA;
				else
					this.situacaoAcesso = EnumSituacaoAcesso.BLOQUEADO;
			}
		}

		return situacaoAcesso;
	}

	public void setSituacaoAcesso(EnumSituacaoAcesso situacaoAcesso) {
		StringBuilder value = new StringBuilder();

		if (situacaoAcesso != null){
			value.append(this.id);
			value.append(situacaoAcesso.getId());
			this.situacaoAcessoHash = Tools.geraMD5(value.toString() + "SNEA");
		}else{
			value.append(this.id);
			value.append(EnumSituacaoAcesso.BLOQUEADO.getId());
			this.situacaoAcessoHash = Tools.geraMD5(value.toString() + "SNEA");
		}

		this.situacaoAcesso = situacaoAcesso;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSistema() {
		return sistema;
	}

	public void setSistema(String sistema) {
		this.sistema = sistema;
	}

	public String getLinkExterno() {
		return linkExterno;
	}

	public void setLinkExterno(String linkExterno) {
		this.linkExterno = linkExterno;
	}

	public Integer getVersaoBase() {
		return versaoBase;
	}

	public void setVersaoBase(Integer versaoBase) {
		this.versaoBase = versaoBase;
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

	public EnumTipoAmbiente getTipoAmbiente() {
		return tipoAmbiente;
	}

	public void setTipoAmbiente(EnumTipoAmbiente tipoAmbiente) {
		this.tipoAmbiente = tipoAmbiente;
	}

	public EnumTipoBancoDados getTipoBancoDados() {
		return tipoBancoDados;
	}

	public void setTipoBancoDados(EnumTipoBancoDados tipoBancoDados) {
		this.tipoBancoDados = tipoBancoDados;
	}

	public Integer getIdVersaoAtual() {
		return idVersaoAtual;
	}

	public void setIdVersaoAtual(Integer idVersaoAtual) {
		this.idVersaoAtual = idVersaoAtual;
	}

	public EnumSimNao getAtivo() {
		return ativo;
	}

	public void setAtivo(EnumSimNao ativo) {
		this.ativo = ativo;
	}

	public NaGestao getNaGestao() {
		return naGestao;
	}

	public void setNaGestao(NaGestao naGestao) {
		this.naGestao = naGestao;
	}

	public String getVersaoSistema() {
		return versaoSistema;
	}

	public void setVersaoSistema(String versaoSistema) {
		this.versaoSistema = versaoSistema;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public EnumSimNao getBasePrincipal() {
		return basePrincipal;
	}

	public void setBasePrincipal(EnumSimNao basePrincipal) {
		this.basePrincipal = basePrincipal;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final NaSistema other = (NaSistema) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return sistema;
	}

	public EnumSimNao getPermiteBloqueio() {
		return permiteBloqueio;
	}

	public void setPermiteBloqueio(EnumSimNao permiteBloqueio) {
		this.permiteBloqueio = permiteBloqueio;
	}

	public String getSituacaoAcessoHash() {
		return situacaoAcessoHash;
	}

}
