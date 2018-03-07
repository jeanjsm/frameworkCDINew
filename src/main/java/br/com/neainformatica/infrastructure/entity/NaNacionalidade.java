package br.com.neainformatica.infrastructure.entity;

/**
-----------------------------------------------------------------------------------------------------------------------------
  @Empresa: N&A Informática Ltda
  @Gerador: MultiSource 
  Dados da Classe:
	@Data  = 29/04/2014 13:08:37
	@Author  = NELSON
	@Versão da Classe = 15

-----------------------------------------------------------------------------------------------------------------------------
 */

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

import br.com.neainformatica.infrastructure.searchengine.annotation.Filter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;


@Entity
@Table(name = "na_nacionalidade",  catalog = "",  schema = "public")
public class NaNacionalidade  implements Serializable {
	private static final long serialVersionUID = 1L;

	@Filter(name = "Código", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Id	
	@SequenceGenerator(name="genNaNacionalidade", allocationSize = 1, schema="public", sequenceName = "seq_na_nacionalidade")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="genNaNacionalidade" )
	@Column(name = "ID", nullable=true)
	private  Integer  id;

	@Filter(name = "Nome", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@Column(name = "nome",  nullable=true, length=250)
	private  String  nome;

	@Filter(name = "País", simpleFilterMethod = EnumFilterMethod.EQUALS)
	@ManyToOne(targetEntity=NaPais.class, fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "idpais" , referencedColumnName = "id", nullable = true, foreignKey = @ForeignKey(name = "fk_nacionalidade_pais") )
	//@ForeignKey(name = "fk_nacionalidade_pais")
	private  NaPais  pais;
	
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
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	public NaPais getPais() {
		return this.pais;
	}

	public void setPais(NaPais pais) {
		this.pais = pais;
	}
	
@Override
public int hashCode()   {   
final int prime = 31;
int result = 1;
result = prime * result + ((id == null) ? 0 : id.hashCode());
return result;
}   
 
@Override
public boolean equals(Object obj)   {   
if (this == obj)
     return true;
if (obj == null)
     return false;
if (getClass() != obj.getClass())
     return false;
NaNacionalidade other = (NaNacionalidade) obj;
if (id == null)   {   
     if (other.id != null)
      return false;
     }    else if (!id.equals(other.id))
     return false;
return true;
}   
 
 
 }



