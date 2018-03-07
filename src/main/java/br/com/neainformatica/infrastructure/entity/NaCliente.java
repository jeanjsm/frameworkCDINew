package br.com.neainformatica.infrastructure.entity;

/**
 * -----------------------------------------------------------------------------------------------------------------------------
 *
 * @Empresa: N&A Informática Ltda
 * @Gerador: MultiSource
 * Dados da Classe:
 * @Data = 28/04/2014 09:00:17
 * @Author = NELSON
 * @Versão da Classe = 15
 * <p>
 * -----------------------------------------------------------------------------------------------------------------------------
 */

import br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoZona;
import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;
import br.com.neainformatica.infrastructure.tools.Tools;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "na_cliente", catalog = "", schema = "public")
@SequenceGenerator(name = "genNaCliente", allocationSize = 1, schema = "public", sequenceName = "seq_na_cliente")
public class NaCliente implements Serializable {
    private static final long serialVersionUID = 1L;

    @Filter(name = "Id", simpleFilterMethod = EnumFilterMethod.EQUALS)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genNaCliente")
    @Column(name = "ID", nullable = false)
    private Integer id;

    @Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.LIKE)
    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Filter(name = "Cnpj", simpleFilterMethod = EnumFilterMethod.EQUALS)
    @Column(name = "cnpj", nullable = true, length = 20)
    private String cnpj;

	@Filter(name = "Nome Secretaria", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "nome_secretaria", nullable = true, length = 100)
    private String nomeSecretaria;

    @Column(name = "site_cliente", nullable = true, length = 150)
    @Filter(name = "Site do Cliente", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
    private String siteCliente;

    @Column(name = "link_brasao", nullable = true, length = 200)
    @Filter(name = "Link do Brasão", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
    private String linkBrasao;

    @Column(name = "link_logo", nullable = true, length = 200)
    @Filter(name = "Link da Logo", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
    private String linkLogo;

    @Column(nullable = true, name = "situacao_acesso")
    private String situacaoAcessoHash;

    @Transient
    private EnumSituacaoAcesso situacaoAcesso;

    @Column(name = "id_cliente_nea", nullable = true)
    private Integer idClienteNeA;

    @Filter(name = "Ativo", simpleFilterMethod = EnumFilterMethod.EQUALS)
    @Column(name = "ativo", nullable = true)
    @Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
            @Parameter(name = "identifierMethod", value = "toChar")})
    private EnumSimNao ativo;

    @Column(name = "telefone_ddd", nullable = true, length = 3)
    @Filter(name = "DDD", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
    private String telefoneDdd;

    @Column(name = "telefone_numero", nullable = true, length = 15)
    @Filter(name = "Número Telefone", simpleFilterMethod = EnumFilterMethod.LIKE, disabled = true)
    private String telefoneNumero;

    @Filter(name = "CEP", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_cep", nullable = true, length = 8)
    private String logradouroCep;

    @Filter(name = "Tipo Zona", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_tipo_zona", nullable = true)
    @Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumTipoZona"),
            @Parameter(name = "identifierMethod", value = "toChar")})
    private EnumTipoZona logradouroTipoZona;

    @Filter(name = "Logradouro Nome", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_nome", nullable = true, length = 150)
    private String logradouroNome;

    @Filter(name = "Logradouro Numero", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_numero", nullable = true)
    private Integer logradouroNumero;

    @Filter(name = "S/N", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_sem_numero", nullable = true)
    @Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumSimNao"),
            @Parameter(name = "identifierMethod", value = "toChar")})
    private EnumSimNao logradouroSemNumero;

    @Filter(name = "Logradouro Quadra", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_quadra", nullable = true, length = 10)
    private String logradouroQuadra;

    @Filter(name = "Logradouro Lote", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_lote", nullable = true, length = 10)
    private String logradouroLote;

    @Filter(name = "Tipo de Logradouro", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @ManyToOne(targetEntity = NaTipoLogradouro.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_tipo_logradouro", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_nacliente_tipo_logradouro"))
    // @ForeignKey(name = "fk_nacliente_tipo_logradouro")
    private NaTipoLogradouro tipologradouro;

    @Filter(name = "Logradouro", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @ManyToOne(targetEntity = NaLogradouro.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_logradouro", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_nacliente_logradouro"))
    // @ForeignKey(name = "fk_nacliente_logradouro")
    private NaLogradouro logradouro;

    @Filter(name = "Logradouro Complemento", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_complemento", nullable = true, length = 150)
    private String logradouroComplemento;

    @Filter(name = "Latitude", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_latitude", nullable = true, length = 20)
    private String logradouroLatitude;

    @Filter(name = "Longitude", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logradouro_longitude", nullable = true, length = 20)
    private String logradouroLongitude;

    @Filter(name = "Tipo de Bairro", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @ManyToOne(targetEntity = NaTipoBairro.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_tipo_bairro", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_nacliente_tipo_bairro"))
    private NaTipoBairro tipobairro;

    @Filter(name = "Bairro", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @ManyToOne(targetEntity = NaBairro.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_bairro", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_cliente_bairro"))
    private NaBairro bairro;

    @Filter(name = "Nome do Bairro", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "bairro_nome", nullable = true, length = 150)
    private String bairroNome;

    @Filter(name = "Cidade", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @ManyToOne(targetEntity = NaCidade.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_cidade_logradouro", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_cliente_cidade"))
    private NaCidade cidadelogradouro;

    @Filter(name = "Nome Cidade", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "cidade_nome", nullable = true, length = 150)
    private String cidadeNome;

    @Column(name = "uf", nullable = true, length = 150)
    @Filter(name = "UF", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    private String ufNome;

    @Filter(name = "Estado", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @ManyToOne(targetEntity = NaEstado.class, fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "id_estado_logradouro", referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_na_cliente_estado"))
    private NaEstado uf;

    @Filter(name = "Brasao", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "brasao", nullable = true)
    private byte[] brasao;

    @Filter(name = "LogoAdministracao", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "logo_administracao", nullable = true)
    private byte[] logoAdministracao;

    @Column(name = "id_na_usuario_auditoria", nullable = true)
    private Integer idNaUsuarioAuditoria;

    @Column(name = "id_na_sistema_auditoria", nullable = true)
    private Integer idNaSistemaAuditoria;

/*    @Filter(name = "Origem Sincronismo", simpleFilterMethod = EnumFilterMethod.EQUALS, disabled = true)
    @Column(name = "origem_sincronismo", nullable = true)
    @Type(type = "br.com.neainformatica.infrastructure.tools.GenericEnumUserType", parameters = {
            @Parameter(name = "enumClass", value = "br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo"),
            @Parameter(name = "identifierMethod", value = "toChar")})
    private EnumOrigemSincronismo origemSincronismo;*/

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_alteracao", nullable = true)
    @Filter(name = "Data Alteração", simpleFilterMethod = EnumFilterMethod.DEFAULT, disabled = true)
    private Date dataAlteracao;

    @Transient
    private boolean semNumero;

    public NaCliente() {
        this.situacaoAcesso = EnumSituacaoAcesso.ATIVO;
    }

    public Integer getIdCLientSituacaoAcesso() {
        if (idClienteNeA != null)
            return this.idClienteNeA;

        return this.getId();
    }

    public boolean isSemNumero() {
        if (this.logradouroSemNumero != null)
            return this.logradouroSemNumero == EnumSimNao.SIM ? true : false;
        return false;
    }

    public void setSemNumero(boolean semNumero) {
        this.semNumero = semNumero;
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNomeSecretaria() {
        return nomeSecretaria;
    }

    public void setNomeSecretaria(String nomeSecretaria) {
        this.nomeSecretaria = nomeSecretaria;
    }

    public String getSiteCliente() {
        return siteCliente;
    }

    public void setSiteCliente(String siteCliente) {
        this.siteCliente = siteCliente;
    }

    public String getLinkBrasao() {
        return linkBrasao;
    }

    public void setLinkBrasao(String linkBrasao) {
        this.linkBrasao = linkBrasao;
    }

    public String getLinkLogo() {
        return linkLogo;
    }

    public void setLinkLogo(String linkLogo) {
        this.linkLogo = linkLogo;
    }

    public EnumSituacaoAcesso getSituacaoAcesso() {

        if (this.situacaoAcesso == null) {
            if (this.situacaoAcessoHash == null || this.situacaoAcessoHash.trim().equals(""))
                setSituacaoAcesso(EnumSituacaoAcesso.BLOQUEADO);
            else {

                if (this.situacaoAcessoHash.equalsIgnoreCase(Tools.geraMD5(this.idClienteNeA + EnumSituacaoAcesso.ATIVO.getId() + "CNEA")))
                    this.situacaoAcesso = EnumSituacaoAcesso.ATIVO;  
                else if (this.situacaoAcessoHash.equalsIgnoreCase(Tools.geraMD5(this.idClienteNeA + EnumSituacaoAcesso.SOMENTE_LEITURA.getId() + "CNEA")))
                    this.situacaoAcesso = EnumSituacaoAcesso.SOMENTE_LEITURA;
                else
                    this.situacaoAcesso = EnumSituacaoAcesso.BLOQUEADO;
            }
        }

        return this.situacaoAcesso;
    }

    public void setSituacaoAcesso(EnumSituacaoAcesso situacaoAcesso) {
    	
    	StringBuilder value = new StringBuilder();
        if (this.idClienteNeA != null) {
            if (situacaoAcesso != null){
            	value.append(this.idClienteNeA.toString());
            	value.append(situacaoAcesso.getId());
            	value.append("CNEA");
            	this.situacaoAcessoHash = Tools.geraMD5(value.toString());
            }else{
            	value.append(EnumSituacaoAcesso.BLOQUEADO.getId());
            	value.append("CNEA");
            	this.situacaoAcessoHash = Tools.geraMD5(value.toString());
            }
        }
        this.situacaoAcesso = situacaoAcesso;
    }
    
	public String getNomeAbreviado() {

		String nomeCliente = this.nome;

		if (nomeCliente == null)
			return null;

		nomeCliente = nomeCliente.toUpperCase();

		nomeCliente = nomeCliente.replaceAll("INSTITUTO DE PREVIDENCIA SOCIAL DOS SERVIDORES DO MUNICIPIO", "INST");
		nomeCliente = nomeCliente.replaceAll("INSTITUTO DE PREVIDENCIA DOS SERVIDORES MUNICIPAIS", "INST");
		nomeCliente = nomeCliente.replaceAll("INSTITUTO DE PREVIDENCIA SOCIAL DOS SERVIDORES MUNICIPAL", "INST");

		nomeCliente = nomeCliente.replaceAll("PREFEITURA MUNICIPAL DE", "PREF");
		nomeCliente = nomeCliente.replaceAll("PREFEITURA MUNICIPAL", "PREF");

		nomeCliente = nomeCliente.replaceAll("MUNICIPIO DE", "PREF");

		nomeCliente = nomeCliente.replaceAll("CAMARA MUNICIPAL DE", "CAM");
		nomeCliente = nomeCliente.replaceAll("CÂMARA MUNICIPAL DE", "CAM");
		nomeCliente = nomeCliente.replaceAll("CAMARA MUNICIPAL", "CAM");
		nomeCliente = nomeCliente.replaceAll("CÂMARA MUNICIPAL", "CAM");

		return nomeCliente;

	}

    public Integer getIdClienteNeA() {
        return idClienteNeA;
    }

    public void setIdClienteNeA(Integer idClienteNeA) {
        this.idClienteNeA = idClienteNeA;
    }

    public EnumSimNao getAtivo() {
        return ativo;
    }

    public void setAtivo(EnumSimNao ativo) {
        this.ativo = ativo;
    }

    public String getTelefoneDdd() {
        return telefoneDdd;
    }

    public void setTelefoneDdd(String telefoneDdd) {
        this.telefoneDdd = telefoneDdd;
    }

    public String getTelefoneNumero() {
        return telefoneNumero;
    }

    public void setTelefoneNumero(String telefoneNumero) {
        this.telefoneNumero = telefoneNumero;
    }

    public String getLogradouroCep() {
        return logradouroCep;
    }

    public void setLogradouroCep(String logradouroCep) {
        this.logradouroCep = logradouroCep;
    }

    public EnumTipoZona getLogradouroTipoZona() {
        return logradouroTipoZona;
    }

    public void setLogradouroTipoZona(EnumTipoZona logradouroTipoZona) {
        this.logradouroTipoZona = logradouroTipoZona;
    }

    public String getLogradouroNome() {
        return logradouroNome;
    }

    public void setLogradouroNome(String logradouroNome) {
        this.logradouroNome = logradouroNome;
    }

    public Integer getLogradouroNumero() {
        return logradouroNumero;
    }

    public void setLogradouroNumero(Integer logradouroNumero) {
        this.logradouroNumero = logradouroNumero;
    }

    public EnumSimNao getLogradouroSemNumero() {
        return logradouroSemNumero;
    }

    public void setLogradouroSemNumero(EnumSimNao logradouroSemNumero) {
        this.logradouroSemNumero = logradouroSemNumero;
    }

    public String getLogradouroQuadra() {
        return logradouroQuadra;
    }

    public void setLogradouroQuadra(String logradouroQuadra) {
        this.logradouroQuadra = logradouroQuadra;
    }

    public String getLogradouroLote() {
        return logradouroLote;
    }

    public void setLogradouroLote(String logradouroLote) {
        this.logradouroLote = logradouroLote;
    }

    public NaTipoLogradouro getTipologradouro() {
        return tipologradouro;
    }

    public void setTipologradouro(NaTipoLogradouro tipologradouro) {
        this.tipologradouro = tipologradouro;
    }

    public NaLogradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(NaLogradouro logradouro) {
        this.logradouro = logradouro;
    }

    public String getLogradouroComplemento() {
        return logradouroComplemento;
    }

    public void setLogradouroComplemento(String logradouroComplemento) {
        this.logradouroComplemento = logradouroComplemento;
    }

    public String getLogradouroLatitude() {
        return logradouroLatitude;
    }

    public void setLogradouroLatitude(String logradouroLatitude) {
        this.logradouroLatitude = logradouroLatitude;
    }

    public String getLogradouroLongitude() {
        return logradouroLongitude;
    }

    public void setLogradouroLongitude(String logradouroLongitude) {
        this.logradouroLongitude = logradouroLongitude;
    }

    public NaTipoBairro getTipobairro() {
        return tipobairro;
    }

    public void setTipobairro(NaTipoBairro tipobairro) {
        this.tipobairro = tipobairro;
    }

    public NaBairro getBairro() {
        return bairro;
    }

    public void setBairro(NaBairro bairro) {
        this.bairro = bairro;
    }

    public String getBairroNome() {
        return bairroNome;
    }

    public void setBairroNome(String bairroNome) {
        this.bairroNome = bairroNome;
    }

    public NaCidade getCidadelogradouro() {
        return cidadelogradouro;
    }

    public void setCidadelogradouro(NaCidade cidadelogradouro) {
        this.cidadelogradouro = cidadelogradouro;
    }

    public String getCidadeNome() {
        return cidadeNome;
    }

    public void setCidadeNome(String cidadeNome) {
        this.cidadeNome = cidadeNome;
    }

    public String getUfNome() {
        return ufNome;
    }

    public void setUfNome(String ufNome) {
        this.ufNome = ufNome;
    }

    public NaEstado getUf() {
        return uf;
    }

    public void setUf(NaEstado uf) {
        this.uf = uf;
    }

    public byte[] getBrasao() {
        return brasao;
    }

    public void setBrasao(byte[] brasao) {
        this.brasao = brasao;
    }

    public byte[] getLogoAdministracao() {
        return logoAdministracao;
    }

    public void setLogoAdministracao(byte[] logoAdministracao) {
        this.logoAdministracao = logoAdministracao;
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

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.idClienteNeA);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NaCliente other = (NaCliente) obj;
        if (!Objects.equals(this.idClienteNeA, other.idClienteNeA)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome;
    }

	public String getSituacaoAcessoHash() {
		return situacaoAcessoHash;
	}

}
