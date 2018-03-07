package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;
import br.com.neainformatica.infrastructure.tools.Tools;

@Entity
@Table(name = "NA_USUARIO_SISTEMA", schema = "seguranca")
public class NaUsuarioSistema implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "genNaUsuarioSistema", schema = "", sequenceName = "seguranca.seq_na_usuario_sistema", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaUsuarioSistema")
	private Integer id;

	@ManyToOne(targetEntity = NaSistema.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sistema", nullable = false, foreignKey = @ForeignKey(name = "fk_na_usuario_sistema_sistema"))
	private NaSistema sistema;

	@ManyToOne(targetEntity = NaUsuario.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fk_na_usuario_sistema_usuario"))
	private NaUsuario usuario;

	@Column(name = "nivel_usuario", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario"), @Parameter(name = "identifierMethod", value = "toInt") })
	private EnumNivelUsuario nivelUsuario;

	@Column(nullable = true, name = "situacao_acesso")
	private String situacaoAcessoHash;

	@Transient
	private EnumSituacaoAcesso situacaoAcesso;

	@ManyToOne(targetEntity = NaCliente.class, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_na_cliente", nullable = true, foreignKey = @ForeignKey(name = "fk_na_usuario_sistema_cliente"))
	private NaCliente naCliente;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	@Filter(name = "Ativo", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "ativo", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao ativo;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao", nullable = true)
	@Filter(name = "Data Alteração", simpleFilterMethod = EnumFilterMethod.DEFAULT)
	private Date dataAlteracao;

	@Filter(name = "Origem Sincronismo", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "origem_sincronismo", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumOrigemSincronismo origemSincronismo;

	public NaUsuarioSistema() {
		this.nivelUsuario = EnumNivelUsuario.USUARIO;
		this.setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);	
		this.ativo = EnumSimNao.SIM;
		
	}

	public EnumSituacaoAcesso getSituacaoAcesso() {

		if (usuario.getLogin().equalsIgnoreCase("SUPORTE")) {
			setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);
			return this.situacaoAcesso;
		}
		if (this.situacaoAcesso == null) {
			if (this.situacaoAcessoHash == null || this.situacaoAcessoHash.trim().equals("")) {
				setSituacaoAcesso(EnumSituacaoAcesso.BLOQUEADO);
			} else {

				if (this.situacaoAcessoHash.equalsIgnoreCase(Tools.geraMD5(this.naCliente.getIdCLientSituacaoAcesso().toString() + this.sistema.getId().toString()
						+ this.usuario.getIdUsuarioSenha().toString() + String.valueOf(EnumSituacaoAcesso.ATIVO.getId()) + "NEA")))
					this.situacaoAcesso = EnumSituacaoAcesso.ATIVO;
				else if (this.situacaoAcessoHash.equalsIgnoreCase(Tools.geraMD5(this.naCliente.getIdCLientSituacaoAcesso().toString() + this.sistema.getId().toString()
						+ this.usuario.getIdUsuarioSenha().toString() + String.valueOf(EnumSituacaoAcesso.SOMENTE_LEITURA.getId()) + "NEA")))
					this.situacaoAcesso = EnumSituacaoAcesso.SOMENTE_LEITURA;
				else
					this.situacaoAcesso = EnumSituacaoAcesso.BLOQUEADO;
			}
		}
		return this.situacaoAcesso;

	}

	public void setSituacaoAcesso(EnumSituacaoAcesso situacaoAcesso) {

		StringBuilder value = new StringBuilder();
		if (situacaoAcesso != null && this.naCliente != null) {

			value.append(this.naCliente.getIdCLientSituacaoAcesso().toString());
			value.append(this.sistema.getId().toString());
			value.append(this.usuario.getIdUsuarioSenha().toString());
			value.append(situacaoAcesso.getId());

			this.situacaoAcessoHash = Tools.geraMD5(value.toString() + "NEA");
		} else {
			value.append(EnumSituacaoAcesso.BLOQUEADO.getId());
			this.situacaoAcessoHash = Tools.geraMD5(value.toString() + "NEA");
		}

		this.situacaoAcesso = situacaoAcesso;

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NaSistema getSistema() {
		return sistema;
	}

	public void setSistema(NaSistema sistema) {
		this.sistema = sistema;
	}

	public NaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(NaUsuario usuario) {
		this.usuario = usuario;
	}

	public EnumNivelUsuario getNivelUsuario() {
		return nivelUsuario;
	}

	public void setNivelUsuario(EnumNivelUsuario nivelUsuario) {
		this.nivelUsuario = nivelUsuario;
	}

	public NaCliente getNaCliente() {
		return naCliente;
	}

	public void setNaCliente(NaCliente naCliente) {
		this.naCliente = naCliente;
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

	public EnumSimNao getAtivo() {
		return ativo;
	}

	public void setAtivo(EnumSimNao ativo) {
		this.ativo = ativo;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public EnumOrigemSincronismo getOrigemSincronismo() {
		return origemSincronismo;
	}

	public void setOrigemSincronismo(EnumOrigemSincronismo origemSincronismo) {
		this.origemSincronismo = origemSincronismo;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 83 * hash + Objects.hashCode(this.sistema);
		hash = 83 * hash + Objects.hashCode(this.usuario);
		hash = 83 * hash + Objects.hashCode(this.naCliente);
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
		final NaUsuarioSistema other = (NaUsuarioSistema) obj;
		if (!Objects.equals(this.sistema, other.sistema)) {
			return false;
		}
		if (!Objects.equals(this.usuario, other.usuario)) {
			return false;
		}
		if (!Objects.equals(this.naCliente, other.naCliente)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "NaUsuarioSistema [sistema=" + sistema + ", usuario=" + usuario + ", naCliente=" + naCliente + "]";
	}

	public String getSituacaoAcessoHash() {
		return situacaoAcessoHash;
	}

	public boolean isAtivoBoolean() {
		if (EnumSimNao.SIM.equals(getAtivo())) {
			return true;
		} else {
			return false;
		}
	}

	public void setAtivoBoolean(boolean ativoBoolean) {
		if (ativoBoolean) {
			this.setAtivo(EnumSimNao.SIM);
		} else {
			this.setAtivo(EnumSimNao.NAO);
		}
	}

}
