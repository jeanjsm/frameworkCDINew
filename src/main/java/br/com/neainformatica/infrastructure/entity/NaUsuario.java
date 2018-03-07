package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumDDD;
import br.com.neainformatica.infrastructure.enumeration.EnumNaUsuarioOrigemAlteracao;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;
import br.com.neainformatica.infrastructure.tools.NeaFormatter;

@Entity
@Table(name = "na_usuario", schema = "seguranca")
@SequenceGenerator(name = "genNaUsuario", schema = "", sequenceName = "seguranca.seq_na_usuario", allocationSize = 1)
public class NaUsuario implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaUsuario")
	@Column(name = "id")
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private Integer id;

	@Column(name = "nome", length = 150, nullable = false)
	@Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String nome;

	@Column(length = 20, nullable = false)
	@Filter(name = "Login", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	private String login;

	@ManyToOne(targetEntity = NaAtor.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ator", nullable = true, foreignKey = @ForeignKey(name = "fk_na_usuario_ator"))
	private NaAtor ator;

	@Column(name = "senha", length = 50)
	private String senha;

	@Column(name = "cpf_cnpj", length = 14, unique = true)
	@Filter(name = "Cpf/Cnpj", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String cpfCnpj;

	@Column(name = "email", length = 150)
	@Filter(name = "E-mail", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
	private String email;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_nascimento", nullable = true)
	@Filter(name = "Data Nascimento", simpleFilterMethod = EnumFilterMethod.LIKE)
	private Date dataNascimento;

	@Column(name = "alterar_senha_proximo_login", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
	    @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
	    @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao alterarSenhaProximoLogin;

	@Column(name = "origem_alteracao", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
	    @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumNaUsuarioOrigemAlteracao"),
	    @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumNaUsuarioOrigemAlteracao origemAlteracao;

	@Column(name = "ID_NA_USUARIO_SERVER", nullable = true)
	private Integer idNaUsuarioServer;

	@Column(name = "SOMENTE_PERFIL", nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
	    @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
	    @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao somentePerfil;

	@OneToMany(mappedBy = "usuario", fetch = FetchType.LAZY)
	private List<NaUsuarioSistema> listaNaUsuarioSistema;

	@Transient
	private EnumNivelUsuario nivelUsuario;

	@Transient
	private Integer idUsuarioSenha;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	@Column(name = "telefone_ddd", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
		    @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumDDD"),
		    @Parameter(name = "identifierMethod", value = "toInt") })
	private EnumDDD telefoneDdd;

	@Column(name = "telefone_numero", nullable = true)
	private String telefoneNumero;

	@Column(name = "telefone_ramal", nullable = true)
	private Integer telefoneRamal;

	@Transient
	private String cpfCnpjFormatado;

	@Transient
	private String primeiroNome;

	@Filter(name = "Origem Sincronismo", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
	@Column(name = "origem_sincronismo", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
	    @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo"),
	    @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumOrigemSincronismo origemSincronismo;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_alteracao", nullable = true)
	@Filter(name = "Data Alteração", simpleFilterMethod = EnumFilterMethod.DEFAULT, disabled = true)
	private Date dataAlteracao;

	@Column(name = "senha_na_usuario_server", length = 50)
	private String senhaNaUsuarioServer;

	public NaUsuario() {
		this.alterarSenhaProximoLogin = EnumSimNao.NAO;
		this.somentePerfil = EnumSimNao.NAO;
		this.origemSincronismo = EnumOrigemSincronismo.CLIENTE;		
	}

	public String getCpfCnpjFormatado() {
		return NeaFormatter.formatarCpfCnpj(this.cpfCnpj);
	}

	public String getPrimeiroNome() {
		int idx = nome.indexOf(" ");
		if (idx == -1) {
			return nome;
		} else {
			return nome.substring(0, nome.indexOf(" "));
		}
	}

	public Integer getIdUsuarioSenha() {

		if (idNaUsuarioServer != null)
			return idNaUsuarioServer;

		return id;
	}
	
	public String getNomeUsuarioAuditoria() {
		if (this.cpfCnpj != null && !this.cpfCnpj.trim().equals(""))
			return this.cpfCnpj;

		return this.login;
	}	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public NaAtor getAtor() {
		return ator;
	}

	public void setAtor(NaAtor ator) {
		this.ator = ator;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public EnumSimNao getAlterarSenhaProximoLogin() {
		return alterarSenhaProximoLogin;
	}

	public void setAlterarSenhaProximoLogin(EnumSimNao alterarSenhaProximoLogin) {
		this.alterarSenhaProximoLogin = alterarSenhaProximoLogin;
	}

	public EnumNaUsuarioOrigemAlteracao getOrigemAlteracao() {
		return origemAlteracao;
	}

	public void setOrigemAlteracao(EnumNaUsuarioOrigemAlteracao origemAlteracao) {
		this.origemAlteracao = origemAlteracao;
	}

	public Integer getIdNaUsuarioServer() {
		return idNaUsuarioServer;
	}

	public void setIdNaUsuarioServer(Integer idUsuarioServer) {
		this.idNaUsuarioServer = idUsuarioServer;
	}

	public EnumSimNao getSomentePerfil() {
		return somentePerfil;
	}

	public void setSomentePerfil(EnumSimNao somentePerfil) {
		this.somentePerfil = somentePerfil;
	}

	public List<NaUsuarioSistema> getListaNaUsuarioSistema() {
		return listaNaUsuarioSistema;
	}

	public void setListaNaUsuarioSistema(List<NaUsuarioSistema> listaNaUsuarioSistema) {
		this.listaNaUsuarioSistema = listaNaUsuarioSistema;
	}

	public EnumNivelUsuario getNivelUsuario() {
		return nivelUsuario;
	}

	public void setNivelUsuario(EnumNivelUsuario nivelUsuario) {
		this.nivelUsuario = nivelUsuario;
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

	public EnumDDD getTelefoneDdd() {
		return telefoneDdd;
	}

	public void setTelefoneDdd(EnumDDD telefoneDdd) {
		this.telefoneDdd = telefoneDdd;
	}

	public String getTelefoneNumero() {
		return telefoneNumero;
	}

	public void setTelefoneNumero(String telefoneNumero) {
		this.telefoneNumero = telefoneNumero;
	}

	public Integer getTelefoneRamal() {
		return telefoneRamal;
	}

	public void setTelefoneRamal(Integer telefoneRamal) {
		this.telefoneRamal = telefoneRamal;
	}

	public EnumOrigemSincronismo getOrigemSincronismo() {
		return origemSincronismo;
	}

	public void setOrigemSincronismo(EnumOrigemSincronismo origemSincronismo) {
		this.origemSincronismo = origemSincronismo;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public String getSenhaNaUsuarioServer() {
		return senhaNaUsuarioServer;
	}

	public void setSenhaNaUsuarioServer(String senhaNaUsuarioServer) {
		this.senhaNaUsuarioServer = senhaNaUsuarioServer;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public void setIdUsuarioSenha(Integer idUsuarioSenha) {
		this.idUsuarioSenha = idUsuarioSenha;
	}

	public void setCpfCnpjFormatado(String cpfCnpjFormatado) {
		this.cpfCnpjFormatado = cpfCnpjFormatado;
	}

	public void setPrimeiroNome(String primeiroNome) {
		this.primeiroNome = primeiroNome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpfCnpj == null) ? 0 : cpfCnpj.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NaUsuario))
			return false;
		NaUsuario other = (NaUsuario) obj;
		if (cpfCnpj == null) {
			if (other.cpfCnpj != null)
				return false;
		} else if (!cpfCnpj.equals(other.cpfCnpj))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return nome;
	}

}
