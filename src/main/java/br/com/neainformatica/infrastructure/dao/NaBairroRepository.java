package br.com.neainformatica.infrastructure.dao;

/**
 -----------------------------------------------------------------------------------------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 25/04/2014 16:17:34
 @Author  = NELSON
 @Versão da Classe = 4
 -----------------------------------------------------------------------------------------------------------------------------
 */

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;

import br.com.neainformatica.infrastructure.entity.NaBairro;
import br.com.neainformatica.infrastructure.entity.NaCidade;

public class NaBairroRepository extends GenericRepository<NaBairro> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	protected void addOrdem(Criteria criteria) {
		criteria.addOrder(Order.asc("id"));
	}

	@SuppressWarnings("unchecked")
	public List<NaBairro> buscaBairrosByCidade(NaCidade cidade) {
		Query query = entityManager.createQuery("select b from NaBairro b where b.cidade = :cidade");
		query.setParameter("cidade", cidade);
		return query.getResultList();
	}

//	@Override
//	public NaBairro save(NaBairro entity, NaBairro oldEntity) throws NeaException {
//		try {
//			return super.save(entity, oldEntity);
//
//		} catch (ClassCastException c) {
//			throw new NeaException("Erro de conversão");
//		}
//
//		catch (Exception e) {
//			throw new NeaException("Erro no repository");
//		}
//
//	}

}
