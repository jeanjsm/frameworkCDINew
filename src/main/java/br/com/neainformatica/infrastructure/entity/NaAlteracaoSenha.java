package br.com.neainformatica.infrastructure.entity;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 23/12/2014 09:20:24
 @Author  = Eduardo Leite Ranzzani
 @Versão da Classe = 15

 -----------------------------------------------------------------------------------------------------------------------------
 */

import java.io.Serializable;
import java.util.Date;

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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_alteracao_senha", catalog = "", schema = "public")
public class NaAlteracaoSenha implements Serializable {
	private static final long serialVersionUID = 1L;

	@Filter(name = "Id", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Id
	@SequenceGenerator(name = "genNaAlteracaoSenha", allocationSize = 1, schema = "public", sequenceName = "public.seq_na_alteracao_senha")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaAlteracaoSenha")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@Filter(name = "Usuário", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_alteracao_senha_usuario"))
	// @ForeignKey(name = "fk_na_alteracao_senha_usuario")
	private NaUsuario usuario;

	@Filter(name = "Sistema", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sistema", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_alteracao_senha_sistema"))
	// @ForeignKey(name = "fk_na_alteracao_senha_sistema")
	private NaSistema sistema;

	@Filter(name = "Data", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = true)
	private Date data;

	@Filter(name = "Chave de Validação", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "chave_validacao", nullable = true, length = 50)
	private String chaveValidacao;

	@Column
	@Filter(name = "Inválido", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao invalido;

	@Column(length = 150)
	@Filter(name = "Email", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private String email;

	@Column(length = 20)
	@Filter(name = "Protocolo", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private String protocolo;
	
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
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NaUsuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(NaUsuario usuario) {
		this.usuario = usuario;
	}

	public NaSistema getSistema() {
		return this.sistema;
	}

	public void setSistema(NaSistema sistema) {
		this.sistema = sistema;
	}

	public Date getData() {
		return this.data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getChaveValidacao() {
		return this.chaveValidacao;
	}

	public void setChaveValidacao(String chaveValidacao) {
		this.chaveValidacao = chaveValidacao;
	}

	public EnumSimNao getInvalido() {
		return invalido;
	}

	public void setInvalido(EnumSimNao invalido) {
		this.invalido = invalido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
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
		NaAlteracaoSenha other = (NaAlteracaoSenha) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
