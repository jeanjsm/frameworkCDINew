package br.com.neainformatica.infrastructure.auditoria;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;

@Entity
@Table(name = "na_auditoria_tabela", catalog = "", schema = "auditoria")
public class NaAuditoriaTabela implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "genNaAuditoriaTabela", allocationSize = 1, schema = "auditoria", sequenceName = "auditoria.seq_na_auditoria_tabela")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaAuditoriaTabela")
	@Column(name = "id", nullable = true)
	private Integer id;

	@Column(name = "nome", nullable = true, length = 50)
	private String nome;

	@Column(name = "nome_amigavel", nullable = true, length = 100)
	private String nomeAmigavel;

	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	@Column(name = "audit_insert", nullable = true, length = 1)
	private EnumSimNao auditInsert;

	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	@Column(name = "audit_update", nullable = true, length = 1)
	private EnumSimNao auditUpdate;

	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	@Column(name = "audit_delete", nullable = true, length = 1)
	private EnumSimNao auditDelete;

	@Column(name = "identificador", nullable = true, length = 50)
	private String identificador;

	@Column(name = "nome_schema",  length = 50)
	private String schema;

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

	public String getNomeAmigavel() {
		return nomeAmigavel;
	}

	public void setNomeAmigavel(String nomeAmigavel) {
		this.nomeAmigavel = nomeAmigavel;
	}

	public String getIdentificador() {
		return identificador;
	}

	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public EnumSimNao getAuditInsert() {
		return auditInsert;
	}

	public void setAuditInsert(EnumSimNao auditInsert) {
		this.auditInsert = auditInsert;
	}

	public EnumSimNao getAuditUpdate() {
		return auditUpdate;
	}

	public void setAuditUpdate(EnumSimNao auditUpdate) {
		this.auditUpdate = auditUpdate;
	}

	public EnumSimNao getAuditDelete() {
		return auditDelete;
	}

	public void setAuditDelete(EnumSimNao auditDelete) {
		this.auditDelete = auditDelete;
	}

}
