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

@Entity
@Table(name = "na_usuario_perfil", schema = "seguranca")
@SequenceGenerator(name = "genNaUsuarioPerfil", schema = "", sequenceName = "seguranca.seq_na_usuario_perfil", allocationSize = 1)
public class NaUsuarioPerfil implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaUsuarioPerfil")
	private Integer id;

	@JoinColumn(name = "id_perfil", nullable = false, foreignKey = @ForeignKey(name = "fk_usuario_perfil_perfil"))
	@ManyToOne(targetEntity = NaPerfil.class, fetch = FetchType.LAZY)
	private NaPerfil perfil;

	@JoinColumn(name = "id_usuario", nullable = false, foreignKey = @ForeignKey(name = "fkusuario_perfil_usuario"))
	@ManyToOne(targetEntity = NaUsuario.class, fetch = FetchType.LAZY)
	private NaUsuario usuario;

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

	public NaPerfil getPerfil() {
		return perfil;
	}

	public void setPerfil(NaPerfil perfil) {
		this.perfil = perfil;
	}

	public NaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(NaUsuario usuario) {
		this.usuario = usuario;
	}

}
