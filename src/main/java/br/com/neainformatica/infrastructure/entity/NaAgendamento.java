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

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_agendamento", catalog = "", schema = "public")
public class NaAgendamento implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@SequenceGenerator(name = "genNaAgendamento", allocationSize = 1, sequenceName = "public.seq_na_agendamento")
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "genNaAgendamento")
	@Column(name = "id", nullable = false)
	private Integer id;

	@Filter(name = "Sistema", simpleFilterMethod = EnumFilterMethod.LIKE)
	@JoinColumn(name = "id_na_sistema", nullable = false, foreignKey = @ForeignKey(name = "fk_na_agendamento_sistema"))
	@ManyToOne(targetEntity = NaSistema.class, fetch = FetchType.LAZY)
	private NaSistema sistema;

	@Column(name = "codigo_tarefa", nullable = true)
	@Filter(name = "Código Tarefa", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private String codigoTarefa;

	@Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.LIKE)
	@Column(name = "nome", nullable = false, length = 100)
	private String nome;

	@Filter(name = "descricao", simpleFilterMethod = EnumFilterMethod.LIKE)
	@Column(name = "descricao", nullable = true, length = 250)
	private String descricao;

	@Column(name = "expressao_cron", length = 50, nullable = false)
	private String expressaoCron;

	@Column(name = "ativo", nullable = false, length = 1)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
			@Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao ativo;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	public NaAgendamento() {
		this.ativo = EnumSimNao.SIM;
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

	public String getCodigoTarefa() {
		return codigoTarefa;
	}

	public void setCodigoTarefa(String codigoTarefa) {
		this.codigoTarefa = codigoTarefa;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getExpressaoCron() {
		return expressaoCron;
	}

	public void setExpressaoCron(String expressaoCron) {
		this.expressaoCron = expressaoCron;
	}

	public EnumSimNao getAtivo() {
		return ativo;
	}

	public void setAtivo(EnumSimNao ativo) {
		this.ativo = ativo;
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
		NaAgendamento other = (NaAgendamento) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
