package br.com.neainformatica.infrastructure.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import br.com.neainformatica.infrastructure.enumeration.EnumNaParametroTipo;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

@Entity
@Table(name = "na_parametro", catalog = "", schema = "public")
public class NaParametro implements Serializable {
	private static final long serialVersionUID = 1L;

	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Id
	@SequenceGenerator(name = "genNaParametro", allocationSize = 1, schema = "public", sequenceName = "public.seq_na_parametro")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaParametro")
	@Column(name = "ID", nullable = true)
	private Integer id;

	@JoinColumn(name = "id_sistema", nullable = false, foreignKey = @ForeignKey(name = "fk_parametro_sistema"))
	@ManyToOne(targetEntity = NaSistema.class, fetch = FetchType.LAZY)
	private NaSistema sistema;

	@JoinColumn(name = "id_na_parametro_grupo", nullable = false, foreignKey = @ForeignKey(name = "fk_parametro_grupo"))
	@ManyToOne(targetEntity = NaParametroGrupo.class, fetch = FetchType.LAZY)
	private NaParametroGrupo grupo;

	@Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.LIKE)
	@Column(name = "nome", nullable = false, length = 150)
	private String nome;

	@Filter(name = "chave", simpleFilterMethod = EnumFilterMethod.LIKE)
	@Column(name = "chave", nullable = true, length = 60)
	private String chave;

	@Column(name = "nivel_usuario", nullable = true)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario"), @Parameter(name = "identifierMethod", value = "toInt") })
	private EnumNivelUsuario nivelUsuario;

	@Column(name = "valor_fixo", nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"), @Parameter(name = "identifierMethod", value = "toChar") })
	private EnumSimNao valorFixo;

	@Column(name = "id_na_tipo_parametro", nullable = false)
	@Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
			@Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumNaParametroTipo"), @Parameter(name = "identifierMethod", value = "toInt") })
	private EnumNaParametroTipo tipo;

	@Filter(name = "Descricao", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "descricao", nullable = true, length = 250)
	private String descricao;

	@OneToMany(targetEntity = NaParametroValor.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "parametro")
	private List<NaParametroValor> listaParametroValor;

	@Column(name = "id_na_usuario_auditoria", nullable = true)
	private Integer idNaUsuarioAuditoria;

	@Column(name = "id_na_sistema_auditoria", nullable = true)
	private Integer idNaSistemaAuditoria;

	@Column(name = "sql_tabela", nullable = true, length = 50)
	private String sqlTabela;

	@Column(name = "sql_campo_chave", nullable = true, length = 50)
	private String sqlCampoChave;

	@Column(name = "sql_campo_descricao", nullable = true, length = 50)
	private String sqlCampoDescricao;

	@Column(name = "valor_padrao", nullable = true, length = 250)
	private String valorPadrao;

	@Column(name = "SQL_WHERE", nullable = true, length = 250)
	private String sqlWhere;

	@Transient
	private String parametroValor;

	/**
	 * Tamanho, Valor ou Data Mínima para este parâmetro. se for data utilizar o
	 * padãro dd/MM/aaaa, se for float utilizar ponto como separador decimal, se
	 * hora hh:mm:ss
	 */
	@Column(name = "TAMANHO_VALOR_MINIMO")
	private BigDecimal tamanhoValorMinimo;

	/**
	 * Tamanho, Valor ou Data Maxima para este parâmetro. se for data utilizar o
	 * padãro dd/MM/aaaa, se for float utilizar ponto como separador decimal, se
	 * hora hh:mm:ss
	 */
	@Column(name = "TAMANHO_VALOR_MAXIMO")
	private BigDecimal tamanhoValorMaximo;

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

	public NaParametro() {
		this.valorFixo = EnumSimNao.SIM;
		this.tipo = EnumNaParametroTipo.TEXTO;
		this.nivelUsuario = EnumNivelUsuario.ADMINISTRADOR;
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

	public NaParametroGrupo getGrupo() {
		return grupo;
	}

	public void setGrupo(NaParametroGrupo grupo) {
		this.grupo = grupo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	public EnumNivelUsuario getNivelUsuario() {
		return nivelUsuario;
	}

	public void setNivelUsuario(EnumNivelUsuario nivelUsuario) {
		this.nivelUsuario = nivelUsuario;
	}

	public EnumSimNao getValorFixo() {
		return valorFixo;
	}

	public void setValorFixo(EnumSimNao valorFixo) {
		this.valorFixo = valorFixo;
	}

	public EnumNaParametroTipo getTipo() {
		return tipo;
	}

	public void setTipo(EnumNaParametroTipo tipo) {
		this.tipo = tipo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<NaParametroValor> getListaParametroValor() {
		return listaParametroValor;
	}

	public void setListaParametroValor(List<NaParametroValor> listaParametroValor) {
		this.listaParametroValor = listaParametroValor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((chave == null) ? 0 : chave.hashCode());
		result = prime * result + ((grupo == null) ? 0 : grupo.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		NaParametro other = (NaParametro) obj;
		if (chave == null) {
			if (other.chave != null)
				return false;
		} else if (!chave.equals(other.chave))
			return false;
		if (grupo == null) {
			if (other.grupo != null)
				return false;
		} else if (!grupo.equals(other.grupo))
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		if (sistema == null) {
			if (other.sistema != null)
				return false;
		} else if (!sistema.equals(other.sistema))
			return false;
		return true;
	}

	public String getSqlTabela() {
		return sqlTabela;
	}

	public void setSqlTabela(String sqlTabela) {
		this.sqlTabela = sqlTabela;
	}

	public String getSqlCampoChave() {
		return sqlCampoChave;
	}

	public void setSqlCampoChave(String sqlCampoChave) {
		this.sqlCampoChave = sqlCampoChave;
	}

	public String getSqlCampoDescricao() {
		return sqlCampoDescricao;
	}

	public void setSqlCampoDescricao(String sqlCampoDescricao) {
		this.sqlCampoDescricao = sqlCampoDescricao;
	}

	public String getParametroValor() {
		return parametroValor;
	}

	public void setParametroValor(String parametroValor) {
		this.parametroValor = parametroValor;
	}

	public BigDecimal getTamanhoValorMinimo() {
		return tamanhoValorMinimo;
	}

	public void setTamanhoValorMinimo(BigDecimal tamanhoValorMinimo) {
		this.tamanhoValorMinimo = tamanhoValorMinimo;
	}

	public BigDecimal getTamanhoValorMaximo() {
		return tamanhoValorMaximo;
	}

	public void setTamanhoValorMaximo(BigDecimal tamanhoValorMaximo) {
		this.tamanhoValorMaximo = tamanhoValorMaximo;
	}

	public String getValorPadrao() {
		return valorPadrao;
	}

	public void setValorPadrao(String valorPadrao) {
		this.valorPadrao = valorPadrao;
	}

	public String getSqlWhere() {
		return sqlWhere;
	}

	public void setSqlWhere(String sqlWhere) {
		this.sqlWhere = sqlWhere;
	}

}
