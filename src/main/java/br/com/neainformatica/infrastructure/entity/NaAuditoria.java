package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumAuditoriaOperacao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_auditoria", schema = "auditoria")
@SequenceGenerator(name = "genNaAuditoria", schema = "auditoria", sequenceName = "auditoria.seq_na_auditoria", allocationSize = 1)
public class NaAuditoria implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaAuditoria")
	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private Integer id;

	@JoinColumn(name = "id_sistema", nullable = false, foreignKey = @ForeignKey(name = "fk_auditoria_sistema"))
	@ManyToOne(targetEntity = NaSistema.class, fetch = FetchType.LAZY)
	private NaSistema sistema;

	@JoinColumn(name = "id_na_cliente", nullable = false, foreignKey = @ForeignKey(name = "fk_auditoria_na_cliente"))
	@ManyToOne(targetEntity = NaCliente.class, fetch = FetchType.LAZY)
	private NaCliente naCliente;

	@Column(name = "data_auditoria")
	@Filter(name = "Data Auditoria", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private Date dataAuditoria;

	@NotNull
	@Column(name = "usuario", length = 30)
	@Filter(name = "Usuário", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private String usuario;

	@Column(name = "tabela", length = 50)
	@Filter(name = "Tabela", simpleFilterMethod = EnumFilterMethod.LIKE)
	private String tabela;

	@Column(name = "chave_registro", length = 50)
	@Filter(name = "Chave Registro", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private String chaveRegistro;

	@Column(name = "tipo_operacao")
	@Filter(name = "Tipo Operação", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumAuditoriaOperacao"),
			@Parameter(name = "identifierMethod", value = "toInt") })
	private EnumAuditoriaOperacao tipoOperacao;

	@Column(name = "exportacao_dados")
	@Filter(name = "Exportação Dados", simpleFilterMethod = EnumFilterMethod.EQUALS)
	private Integer exportacaoDados;

	@OneToMany(targetEntity = NaAuditoriaItem.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "auditoria")
	private List<NaAuditoriaItem> auditoriaItems;

	public NaAuditoria() {
		super();
		this.auditoriaItems = new ArrayList<NaAuditoriaItem>();
	}

	public NaAuditoria(Integer id, Date dataAuditoria, String usuario, String tabela, String chaveRegistro, EnumAuditoriaOperacao tipoOperacao,
			Integer exportacaoDados, NaSistema sistema, NaCliente naCliente) {
		super();
		this.id = id;
		this.dataAuditoria = dataAuditoria;
		this.usuario = usuario;
		this.tabela = tabela;
		this.chaveRegistro = chaveRegistro;
		this.tipoOperacao = tipoOperacao;
		this.exportacaoDados = exportacaoDados;
		this.sistema = sistema;
		this.naCliente = naCliente;
	}

	public NaAuditoria(Date dataAuditoria, String usuario, String tabela, String chaveRegistro, EnumAuditoriaOperacao tipoOperacao, Integer exportacaoDados,
			NaSistema sistema, NaCliente naCliente) {
		super();
		this.dataAuditoria = dataAuditoria;
		this.usuario = usuario;
		this.tabela = tabela;
		this.chaveRegistro = chaveRegistro;
		this.tipoOperacao = tipoOperacao;
		this.exportacaoDados = exportacaoDados;
		this.sistema = sistema;
		this.naCliente = naCliente;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDataAuditoria() {
		return dataAuditoria;
	}

	public void setDataAuditoria(Date dataAuditoria) {
		this.dataAuditoria = dataAuditoria;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

	public String getChaveRegistro() {
		return chaveRegistro;
	}

	public void setChaveRegistro(String chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
	}

	public Integer getExportacaoDados() {
		return exportacaoDados;
	}

	public void setExportacaoDados(Integer exportacaoDados) {
		this.exportacaoDados = exportacaoDados;
	}

	public List<NaAuditoriaItem> getAuditoriaItems() {
		return auditoriaItems;
	}

	public void setAuditoriaItems(List<NaAuditoriaItem> auditoriaItems) {
		this.auditoriaItems = auditoriaItems;
	}

	public NaSistema getSistema() {
		return sistema;
	}

	public void setSistema(NaSistema sistema) {
		this.sistema = sistema;
	}

	public NaCliente getNaCliente() {
		return naCliente;
	}

	public void setNaCliente(NaCliente naCliente) {
		this.naCliente = naCliente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chaveRegistro == null) ? 0 : chaveRegistro.hashCode());
		result = prime * result + ((dataAuditoria == null) ? 0 : dataAuditoria.hashCode());
		result = prime * result + ((tabela == null) ? 0 : tabela.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof NaAuditoria))
			return false;
		NaAuditoria other = (NaAuditoria) obj;
		if (chaveRegistro == null) {
			if (other.chaveRegistro != null)
				return false;
		} else if (!chaveRegistro.equals(other.chaveRegistro))
			return false;
		if (dataAuditoria == null) {
			if (other.dataAuditoria != null)
				return false;
		} else if (!dataAuditoria.equals(other.dataAuditoria))
			return false;
		if (tabela == null) {
			if (other.tabela != null)
				return false;
		} else if (!tabela.equals(other.tabela))
			return false;
		return true;
	}

	public EnumAuditoriaOperacao getTipoOperacao() {
		return tipoOperacao;
	}

	public void setTipoOperacao(EnumAuditoriaOperacao tipoOperacao) {
		this.tipoOperacao = tipoOperacao;
	}

	@Override
	public String toString() {
		return id + " - " + chaveRegistro;
	}

}
