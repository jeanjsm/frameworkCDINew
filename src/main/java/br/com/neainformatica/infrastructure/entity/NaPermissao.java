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
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoNaPermissao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_permissao", schema = "seguranca", uniqueConstraints = @UniqueConstraint(columnNames = { "id_sistema", "chave" }))
@SequenceGenerator(name = "genNaPermissao", schema = "", sequenceName = "seguranca.seq_na_permissao", allocationSize = 1)
public class NaPermissao implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaPermissao")
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private Integer id;

	@JoinColumn(name = "id_sistema", nullable = false, foreignKey = @ForeignKey(name = "fk_permissao_sistema"))
	@ManyToOne(targetEntity = NaSistema.class, fetch = FetchType.LAZY)
	private NaSistema sistema;

	@Column(nullable = false)
	@Filter(name = "Descrição", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String descricao;

	@Column(nullable = false)
	@Filter(name = "Chave", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String chave;

	@Column(nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumTipoNaPermissao"), @Parameter(name = "identifierMethod", value = "toChar") })
	@Filter(name = "Tipo", simpleFilterMethod = EnumFilterMethod.LIKE)
	private EnumTipoNaPermissao tipo;

	@Column
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	@Filter(name = "Ativo", simpleFilterMethod = EnumFilterMethod.LIKE)
	private EnumSimNao ativo;
		
	/**
	 * Algumas permissões são exclusivas para usuário de nível suporte, neste caso não vamos exibilas para o usuário comum
	 */
	@Column(name = "nivel_usuario", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario"), @Parameter(name = "identifierMethod", value = "toInt") })
	private EnumNivelUsuario nivelUsuario;
	
	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;
	
	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	@JoinColumn(name = "id_na_cliente", nullable = true, foreignKey = @ForeignKey(name = "FK_NA_PERMISSAO_CLIENTE"))
	@ManyToOne(targetEntity = NaCliente.class, fetch = FetchType.LAZY)
	private NaCliente cliente;
	
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

	public NaPermissao() {
		this.ativo = EnumSimNao.SIM;
		this.tipo = EnumTipoNaPermissao.CADASTRO;
		this.nivelUsuario = EnumNivelUsuario.USUARIO;		
	}
	
	public NaPermissao(String chave) {
		this.chave = chave;
		this.ativo = EnumSimNao.SIM;
		this.tipo = EnumTipoNaPermissao.CADASTRO;
		this.nivelUsuario = EnumNivelUsuario.USUARIO;		
	}

	public NaPermissao(EnumTipoNaPermissao tipo, String nomePermissao, String chave ,NaSistema sistema) {
		this.tipo = tipo;
		this.ativo = EnumSimNao.SIM;
		this.descricao = nomePermissao;
		this.sistema = sistema;
		this.chave = chave;
		this.tipo = EnumTipoNaPermissao.CADASTRO;
		this.nivelUsuario = EnumNivelUsuario.USUARIO;
	}

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

	public EnumSimNao getAtivo() {
		return ativo;
	}

	public void setAtivo(EnumSimNao ativo) {
		this.ativo = ativo;
	}

	public NaSistema getSistema() {
		return sistema;
	}

	public void setSistema(NaSistema sistema) {
		this.sistema = sistema;
	}

	public EnumTipoNaPermissao getTipo() {

		if (this.tipo == null)
			return EnumTipoNaPermissao.CADASTRO;

		return tipo;
	}

	public void setTipo(EnumTipoNaPermissao tipo) {
		this.tipo = tipo;
	}

	public EnumNivelUsuario getNivelUsuario() {
		return nivelUsuario;
	}

	public void setNivelUsuario(EnumNivelUsuario nivelUsuario) {
		this.nivelUsuario = nivelUsuario;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descricao == null) ? 0 : descricao.hashCode());
		result = prime * result + ((sistema == null) ? 0 : sistema.hashCode());
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
		NaPermissao other = (NaPermissao) obj;
		if (descricao == null) {
			if (other.descricao != null)
				return false;
		} else if (!descricao.equals(other.descricao))
			return false;
		if (sistema == null) {
			if (other.sistema != null)
				return false;
		} else if (!sistema.equals(other.sistema))
			return false;
		return true;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public NaCliente getCliente() {
		return cliente;
	}

	public void setCliente(NaCliente cliente) {
		this.cliente = cliente;
	}
}
