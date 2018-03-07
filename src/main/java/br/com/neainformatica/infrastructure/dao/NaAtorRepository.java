package br.com.neainformatica.infrastructure.dao;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.Criteria;

import br.com.neainformatica.infrastructure.entity.NaAtor;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaPerfil;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoAtor;
import br.com.neainformatica.infrastructure.exception.NeaException;

public class NaAtorRepository extends  GenericRepository<NaAtor> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	NaAtorPermissaoRepository naAtorPermissaoRepository;

	@Override
	protected void addOrdem(Criteria criteria) {
		super.addOrdem(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<NaAtor> buscarAtoresSistema(NaSistema sistema) {

		Query query = entityManager.createQuery("select p from NaAtorPermissao ap join ap.permissao p where p.sistema = :sistema");
		query.setParameter("sistema", sistema);

		return query.getResultList();

	}

	/**
	 * Retorna a lista de atores que são do tipo Perfil para o sistema passado
	 * como parâmetro
	 */
	@SuppressWarnings("unchecked")
	public List<NaAtor> buscarAtoresPerfilBySistema(NaSistema sistema, NaCliente cliente) {

		StringBuilder sql = new StringBuilder();
		sql.append("select a ");
		sql.append("from NaPerfil p ");
		sql.append("  join p.ator a ");
		sql.append("where p.cliente = :cliente ");
		sql.append("  and p.sistema = :sistema ");
		sql.append(" order by p.descricao asc");

		Query query = entityManager.createQuery(sql.toString());

		query.setParameter("sistema", sistema);
		query.setParameter("cliente", cliente);

		return query.getResultList();
	}

	/**
	 * Retorna a lista de atores que são do tipo Usuario para o sistema passado
	 * como parâmetro
	 */
	@SuppressWarnings("unchecked")
	public List<NaAtor> buscarAtoresUsuarioBySistema(NaSistema sistema, NaCliente cliente) {

		StringBuilder sql = new StringBuilder();
		sql.append("select a  ");
		sql.append("from NaUsuarioSistema us ");
		sql.append("join us.usuario u  ");
		sql.append("join u.ator a ");
		sql.append("where us.sistema = :sistema ");
		sql.append("and us.naCliente = :cliente ");
		sql.append(" order by u.nome asc");

		Query query = entityManager.createQuery(sql.toString());
		query.setParameter("sistema", sistema);
		query.setParameter("cliente", cliente);

		return query.getResultList();
	}

	/**
	 * Retorna a lista de atores do tipo perfil do sistema passado como
	 * parametro juntamente com a lista de todos os usuários que possuem acesso
	 * a este sistema
	 * 
	 * @param sistema
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NaAtor> buscarAtoresPermissao(NaSistema sistema, NaCliente cliente) {

		StringBuilder sql = new StringBuilder();
		sql.append("select a from NaAtor a ");
		sql.append("where a.id in ");
		sql.append("  (select np.ator.id from NaPerfil np where np.sistema = :sistema and np.cliente = :cliente) ");
		sql.append("or a.id in ");
		sql.append("  (select u.ator.id from NaUsuarioSistema us join us.usuario u where us.sistema = :sistema and us.naCliente = :cliente) ");

		Query query = entityManager.createQuery(sql.toString());

		query.setParameter("sistema", sistema);
		query.setParameter("cliente", cliente);

		return query.getResultList();

	}

	/**
	 * Busca os naAtor de acordo com o tipo passado por parâmetro
	 * 
	 * @param tipoAutor
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NaAtor> buscarAtores(EnumTipoAtor tipoAtor) {
		try {
			Query query = entityManager.createQuery("select a from NaAtor a where a.tipo = :tipo");
			query.setParameter("tipo", tipoAtor);

			return query.getResultList();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * busca todos os atores relacionados ao usuário passado como parâmetro. O
	 * ator pode ser um usuário ou perfil
	 * 
	 * @param usuario
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NaAtor> buscarAtoresUsuario(NaUsuario usuario, NaSistema sistema) {

		StringBuilder sql = new StringBuilder();
		sql.append("select a from NaAtor a ");
		sql.append("where a.id in ");
		sql.append("     (select p.ator.id from NaUsuarioPerfil up join up.perfil p where up.usuario = :usuario and p.sistema = :sistema) ");
		sql.append("or a.id in ");
		sql.append("     (select u.ator.id from NaUsuario u where u.id = :idUsuario ) ");

		Query query = entityManager.createQuery(sql.toString());

		query.setParameter("idUsuario", usuario.getId());
		query.setParameter("usuario", usuario);
		query.setParameter("sistema", sistema);

		return query.getResultList();

	}

	public NaAtor buscarAtorPerfil(NaPerfil perfil) {

		StringBuilder sql = new StringBuilder();

		sql.append("select p.ator from NaPerfil p where p.id = :idPerfil");

		Query query = entityManager.createQuery(sql.toString());

		query.setParameter("idPerfil", perfil.getId());

		return (NaAtor) query.getSingleResult();

	}

	public NaAtor buscarAtorAdm(Integer idAtor) {
		StringBuilder sql = new StringBuilder();
		sql.append("select p from NaAtor p where p.id = :idAtor ");
		Query query = entityManager.createQuery(sql.toString());
		query.setParameter("idAtor", idAtor);
		return (NaAtor) query.getSingleResult();
	}

	/**
	 * Busca o ator por nome e tipo
	 * 
	 * @param nome
	 * @param tipo
	 * @return
	 */
	public NaAtor buscarAtorPorNome(String nome, EnumTipoAtor tipo) {
		try {
			Query query = entityManager.createQuery("select a from NaAtor a where a.nome = upper(:nome) and a.tipo = :tipo", NaAtor.class);

			query.setParameter("nome", nome.toUpperCase());
			query.setParameter("tipo", tipo);

			return (NaAtor) query.getSingleResult();
		} catch (NoResultException e) {

			return null;
		}

	}

	@Override
	protected void beforeDelete(NaAtor entity) throws NeaException {
		naAtorPermissaoRepository.apagarPermissoes(entity);
		super.beforeDelete(entity);
	}

	public void apagarAtor(NaAtor ator) {
		if (ator != null) {
			this.delete(ator);
		}
	}

	public void removeAtoresSemVinculos() {
		StringBuilder jpql = new StringBuilder();

		jpql.append("select a from NaAtor a where ");
		jpql.append(" (select count(u.ator) from NaUsuario u where u.ator = a ) = 0");
		jpql.append(" and (select count(ap.ator) from NaAtorPermissao ap where ap.ator = a) = 0 ");
		jpql.append("and (select count(p.ator) from NaPerfil p where p.ator = a) = 0");

		Query query = entityManager.createQuery(jpql.toString());
		List<NaAtor> resultado = query.getResultList();

		try {
			deleteAll(resultado);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
