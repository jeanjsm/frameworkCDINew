package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.sql.JoinType;

import br.com.neainformatica.infrastructure.entity.NaParametro;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.tools.Auditoria;

@SuppressWarnings("unchecked")
public class NaParametroRepository extends GenericRepository<NaParametro> implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}
	
	
	@Override
	public List<NaParametro> findAll() {
		return createCriteria()
				.createAlias("grupo", "g", JoinType.LEFT_OUTER_JOIN)
				.createAlias("listaParametroValor", "v", JoinType.LEFT_OUTER_JOIN)
				.list();
	}
	
	public NaParametro findByNome(String chave, NaSistema sistema) {
		entityManager.clear();
		Query qry = entityManager.createQuery("select p from NaParametro p "
				+ " left join fetch p.grupo g"
				+ " left join fetch p.listaParametroValor v"
				+ " where p.chave = :chave"
				+ "   and p.sistema = :sistema");
		
		qry.setParameter("chave", chave);
		qry.setParameter("sistema", sistema);
		
		try {
			return (NaParametro) qry.getSingleResult();
		} catch(NoResultException e) {			
			return null;
		}
	}
	
	public List<NaParametro> pesquisarParametro(Integer grupo, String nome, NaSistema sistema){
		try{
			String stringCondicao = "";
			
			if(grupo != null && !nome.equals("")){
				stringCondicao = " and g.id = :grupo and lower(p.nome) like lower(:nome) ";
			}else if(grupo != null && nome.equals("")){
				stringCondicao = " and g.id = :grupo";
			}else if (grupo == null && !nome.equals("")){
				stringCondicao = " and lower(p.nome) like lower(:nome)";
			}
			
			Query qry = entityManager.createQuery("select p from NaParametro p "
					+ " left join fetch p.grupo g"
					+ " where p.sistema = :sistema"
					+ " "+stringCondicao+""); 
			qry.setParameter("sistema", sistema);
					
			if(grupo != null && !nome.equals("")){
				qry.setParameter("grupo", grupo);
				qry.setParameter("nome", "%"+nome+"%");	
			}else if(grupo != null && nome.equals("")){
				qry.setParameter("grupo", grupo);
			}else if (grupo == null && !nome.equals("")){
				qry.setParameter("nome", "%"+nome+"%");
			}
			
			List<NaParametro> naParametro = qry.getResultList();
			
			return naParametro;
			
		  }catch(NoResultException e){
			return null;
		  }
		
		}


	@Override
	public Auditoria getAuditoria() {
		// TODO Auto-generated method stub
		return super.getAuditoria();
	}

	
	
}
