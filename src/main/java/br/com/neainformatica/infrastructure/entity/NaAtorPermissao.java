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

import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;

@Entity
@Table(name = "na_ator_permissao", schema = "seguranca")
public class NaAtorPermissao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "genNaAtorPermissao", schema = "seguranca", sequenceName = "seguranca.seq_na_ator_permissao", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaAtorPermissao")
	private Integer id;

	@JoinColumn(name = "id_ator", nullable = false, foreignKey = @ForeignKey(name = "FK_NA_ATOR_PERM_PERFIL"))
	@ManyToOne(targetEntity = NaAtor.class)
	private NaAtor ator;

	@JoinColumn(name = "id_permissao", nullable = false, foreignKey = @ForeignKey(name = "FK_NA_ATOR_PERM_PERMISSAO"))
	@ManyToOne(targetEntity = NaPermissao.class, cascade = CascadeType.ALL)
	private NaPermissao permissao;

	@Column(nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao acessar;

	@Column(nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao alterar;

	@Column(nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao excluir;

	@Column(nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao inserir;

	@JoinColumn(name = "ID_NA_CLIENTE", nullable = false, foreignKey = @ForeignKey(name = "FK_NA_ATOR_PERMISSAO_CLIENTE"))
	@ManyToOne(targetEntity = NaCliente.class, fetch = FetchType.LAZY)
	private NaCliente cliente;

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

	public NaAtorPermissao() {
		this.acessar = EnumSimNao.NAO;
		this.inserir = EnumSimNao.NAO;
		this.alterar = EnumSimNao.NAO;
		this.excluir = EnumSimNao.NAO;
	}

	public NaAtorPermissao(NaAtor ator, NaPermissao permissao, NaCliente naCliente) {

		this.acessar = EnumSimNao.NAO;
		this.inserir = EnumSimNao.NAO;
		this.alterar = EnumSimNao.NAO;
		this.excluir = EnumSimNao.NAO;

		this.ator = ator;
		this.permissao = permissao;
		this.cliente = naCliente;
	}

	public NaAtorPermissao(boolean acessar, boolean inserir, boolean alterar, boolean excluir) {
		this.acessar = (acessar ? EnumSimNao.SIM : EnumSimNao.NAO);
		this.inserir = (inserir ? EnumSimNao.SIM : EnumSimNao.NAO);
		this.alterar = (alterar ? EnumSimNao.SIM : EnumSimNao.NAO);
		this.excluir = (excluir ? EnumSimNao.SIM : EnumSimNao.NAO);
	}

	public Boolean todosSelecionados() {
		if (this.acessar == EnumSimNao.SIM && this.inserir == EnumSimNao.SIM && this.alterar == EnumSimNao.SIM && this.excluir == EnumSimNao.SIM) {
			return true;
		}

		return false;
	}

	public void setSelecionarTodos(Boolean selecionarTodos) {

		if (selecionarTodos) {
			this.acessar = EnumSimNao.SIM;
			this.inserir = EnumSimNao.SIM;
			this.alterar = EnumSimNao.SIM;
			this.excluir = EnumSimNao.SIM;
		} else {
			this.acessar = EnumSimNao.NAO;
			this.inserir = EnumSimNao.NAO;
			this.alterar = EnumSimNao.NAO;
			this.excluir = EnumSimNao.NAO;
		}
	}

	public NaCliente getCliente() {
		return cliente;
	}

	public void setCliente(NaCliente cliente) {
		this.cliente = cliente;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NaAtor getAtor() {
		return ator;
	}

	public void setAtor(NaAtor perfil) {
		this.ator = perfil;
	}

	public NaPermissao getPermissao() {
		return permissao;
	}

	public void setPermissao(NaPermissao permissao) {
		this.permissao = permissao;
	}

	public EnumSimNao getAcessar() {
		return acessar;
	}

	public void setAcessar(EnumSimNao acessar) {
		this.acessar = acessar;

		if (acessar == EnumSimNao.NAO) {
			this.inserir = EnumSimNao.NAO;
			this.alterar = EnumSimNao.NAO;
			this.excluir = EnumSimNao.NAO;
		}
	}

	public EnumSimNao getAlterar() {
		return alterar;

	}

	public void setAlterar(EnumSimNao alterar) {
		this.alterar = alterar;

		if (alterar == EnumSimNao.SIM)
			this.acessar = EnumSimNao.SIM;
	}

	public EnumSimNao getExcluir() {
		return excluir;
	}

	public void setExcluir(EnumSimNao excluir) {
		this.excluir = excluir;

		if (excluir == EnumSimNao.SIM)
			this.acessar = EnumSimNao.SIM;
	}

	public EnumSimNao getInserir() {
		return inserir;
	}

	public void setInserir(EnumSimNao inserir) {
		this.inserir = inserir;

		if (inserir == EnumSimNao.SIM)
			this.acessar = EnumSimNao.SIM;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ator == null) ? 0 : ator.hashCode());
		result = prime * result + ((permissao == null) ? 0 : permissao.hashCode());
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
		NaAtorPermissao other = (NaAtorPermissao) obj;
		if (ator == null) {
			if (other.ator != null)
				return false;
		} else if (!ator.equals(other.ator))
			return false;
		if (permissao == null) {
			if (other.permissao != null)
				return false;
		} else if (!permissao.equals(other.permissao))
			return false;
		return true;
	}

}
