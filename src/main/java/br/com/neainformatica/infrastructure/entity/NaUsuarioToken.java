package br.com.neainformatica.infrastructure.entity;

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

/**
 * Created by rodolpho.sotolani on 01/02/2017.
 */
@Entity
@Table(name = "NA_USUARIO_TOKEN", schema = "seguranca")
@SequenceGenerator(name = "genNaUsuarioToken", allocationSize = 1, schema = "seguranca", sequenceName = "SEQ_NA_USUARIO_TOKEN")
public class NaUsuarioToken implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaUsuarioToken")
	@Column(name = "ID", nullable = false)
	private Integer id;

	@JoinColumn(name = "ID_USUARIO", nullable = false, foreignKey = @ForeignKey(name = "fkusuario_perfil_usuario"))
	@ManyToOne(targetEntity = NaUsuario.class, fetch = FetchType.LAZY)
	private NaUsuario usuario;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ULTIMO_ACESSO", nullable = false)
	private Date dataUltimoAcesso;

	@Column(nullable = false, name = "TOKEN")
	private String token;
	
	
	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public NaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(NaUsuario usuario) {
		this.usuario = usuario;
	}

	public Date getDataUltimoAcesso() {
		return dataUltimoAcesso;
	}

	public void setDataUltimoAcesso(Date dataUltimoAcesso) {
		this.dataUltimoAcesso = dataUltimoAcesso;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		NaUsuarioToken that = (NaUsuarioToken) o;

		if (getUsuario() != null ? !getUsuario().equals(that.getUsuario()) : that.getUsuario() != null)
			return false;
		return getToken() != null ? getToken().equals(that.getToken()) : that.getToken() == null;

	}

	@Override
	public int hashCode() {
		int result = getUsuario() != null ? getUsuario().hashCode() : 0;
		result = 31 * result + (getToken() != null ? getToken().hashCode() : 0);
		return result;
	}
}
