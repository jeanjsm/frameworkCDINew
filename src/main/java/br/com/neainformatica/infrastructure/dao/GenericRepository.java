package br.com.neainformatica.infrastructure.dao;

import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.searchengine.services.EngineCriteria;
import br.com.neainformatica.infrastructure.services.AtualizaBaseService;
import br.com.neainformatica.infrastructure.tools.Auditoria;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ContextNotActiveException;
import javax.inject.Inject;
import javax.persistence.EmbeddedId;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Query;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

@SuppressWarnings("unchecked")
public abstract class GenericRepository<T> {

	@Inject
	protected Log log;

	@Inject
	protected EntityManager entityManager;

	private Criteria criteriaDinamicSearch;

	private boolean auditar = true;

	/**
	 * Este deve ser utilizado para rotinas onde o usuário esta fora do JSF, ex:
	 * REST
	 */
	private NaUsuario usuarioAuditoria;

	@Inject
	private Auditoria auditoria;

	@Inject
	private NaSessionController sessionController;

	@PostConstruct
	public void init() {

	}

	protected Criteria createCriteria() {
		Criteria consult;
		Class repositoryClass = getClass();
		while (repositoryClass.getSuperclass() != GenericRepository.class) {
			repositoryClass = repositoryClass.getSuperclass();
		}
		return getSession().createCriteria((Class<T>) ((ParameterizedType) repositoryClass.getGenericSuperclass()).getActualTypeArguments()[0])
				.setCacheable(false);
	}

	protected void addDistinct(Criteria consult) {

	}

	protected void addJoinFecth(Criteria consult) {

	}

	/**
	 * Método para ordenar a consulta de dados
	 * 
	 * @param criteria
	 */
	protected void addOrdem(Criteria criteria) {

	}

	protected void orderJoin(Criteria consult, String fieldName) {
		fieldName = fieldName.replace(".", "#");
		String[] splitResult = fieldName.split("#");
		String joinAt = "";
		Integer index = 0;
		Integer maxIndex = splitResult.length - 1;
		String alias = consult.toString();

		while (index < maxIndex) {
			if (index.equals(0)) {
				joinAt = splitResult[0];
			} else {
				joinAt += "." + splitResult[index];
			}
			if (!alias.contains(":" + splitResult[index] + ")")) {
				consult.createAlias(joinAt, splitResult[index]);
				alias = consult.toString();
			}
			joinAt = splitResult[index];
			index++;
		}
	}

	public List<T> findAll() {
		Criteria consult;
		try {
			consult = createCriteria();
			addDistinct(consult);
			addJoinFecth(consult);
			addOrdem(consult);
			return consult.list();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<T> findAllLazy(int currentIndex, int count) {
		Criteria consult;

		try {
			consult = createCriteria().setFirstResult(currentIndex).setMaxResults(count);

			addDistinct(consult);
			addJoinFecth(consult);
			addOrdem(consult);

			return consult.list();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<T> findByParam(Object object, String field) {
		return (List<T>) createCriteria().add(Restrictions.eq(field, object)).list();
	}

	public T findByField(Object object, String field) {
		return (T) createCriteria().add(Restrictions.eq(field, object)).uniqueResult();
	}

	public List<T> findByParamLazy(Object object, String field, int currentIndex, int count) {
		return (List<T>) createCriteria().add(Restrictions.eq(field, object)).setFirstResult(currentIndex).setMaxResults(count).list();
	}

	public T findById(Integer id) {
		return (T) createCriteria().add(Restrictions.idEq(id)).uniqueResult();
	}

	public T findByObjectId(Object id) {
		return (T) createCriteria().add(Restrictions.idEq(id)).uniqueResult();
	}

	/**
	 * Retorna a quantidade de resultados de uma consulta
	 * 
	 * @return
	 */
	public Number criteriaSelectCount() {
		return (Number) createCriteria().setProjection(Projections.rowCount()).uniqueResult();
	}

	/**
	 * Retorna a quantidade de resultados de uma consulta utilizando filtros
	 * 
	 * @param filters
	 * @return
	 */
	public Number criteriaSelectCount(List<IFilter> filters) {

		Criteria criteria = createCriteria();
		criteria = EngineCriteria.buildCriteria(criteria, filters);

		return (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();
	}

	/**
	 * Retorna a quantidade de resultados de uma consulta utilizando um objeto como
	 * filtro
	 * 
	 * @param object
	 * @param field
	 * @return Quantidade
	 */
	public Number criteriaSelectCountByParam(Object object, String field) {
		Number count = (Number) createCriteria().add(Restrictions.eq(field, object)).setProjection(Projections.rowCount()).uniqueResult();

		if (count != null)
			return count;
		else
			return 0;
	}

	/**
	 * Busca dinâmica sem utilizar a paginação
	 * 
	 * @param filters
	 * @return
	 */
	public List<T> dinamicSearch(List<IFilter> filters) {

		Criteria criteria = createCriteria();
		criteria = EngineCriteria.buildCriteria(criteria, filters);

		addOrdem(criteria);

		return criteria.list();
	}

	/**
	 * Busca dinâmica utilizando a paginação
	 * 
	 * @param filters
	 * @return
	 */
	public List<T> dinamicSearch(List<IFilter> filters, int currentIndex, int count) {
		getCriteriaDinamicSearch();
		criteriaDinamicSearch = EngineCriteria.buildCriteria(criteriaDinamicSearch, filters);

		criteriaDinamicSearch.setFirstResult(currentIndex).setMaxResults(count);

		return criteriaDinamicSearch.list();

	}

	@Deprecated
	protected void beforeSave(T entity) throws NeaException {

		defineSistemaEusarioDeManipulacaoDoRegistro(entity);
	}

	/**
	 * Favor utilizar o método noAudit()
	 * 
	 * @return
	 */
	@Deprecated
	public GenericRepository<T> isAudit() {
		this.auditar = false;
		return this;
	}

	public GenericRepository<T> noAudit() {
		this.auditar = false;
		return this;
	}

	public GenericRepository<T> setUserAudit(NaUsuario user) {
		this.usuarioAuditoria = user;
		return this;
	}

	public void setUsuarioAuditoria(NaUsuario usuarioAuditoria) {
		this.usuarioAuditoria = usuarioAuditoria;
	}

	/**
	 * Salva o objeto gravando autoria
	 * 
	 * @param entity
	 * @param oldEntity
	 * @return
	 */
	public T save(T entity, T oldEntity) throws NeaException {
		try {
			if (oldEntity != null)
				oldEntity = loadObject(oldEntity);

			beforeSave(entity);
			removeCamposNull(entity);
			entity = (T) getSession().merge(entity);
			getSession().flush();
			afterSave(entity);

			if (auditar) {
				if (this.usuarioAuditoria != null && !this.usuarioAuditoria.getNome().trim().equals(""))
					getAuditoria().geraAuditoria(oldEntity, entity, this.usuarioAuditoria.getNome());
				else
					getAuditoria().geraAuditoria(oldEntity, entity);
			}

			return entity;
		} catch (ConstraintViolationException ex) {
			rollbackSave(entity);
			log.error(ex.getSQLException());
			ex.printStackTrace();
			throw new NeaException("Erro ocorrido na base de dados!");
		} catch (Exception e) {
			e.printStackTrace();
			throw new NeaException("Falha ao salvar dados: " + e.getMessage());
		}
	}

	private void defineSistemaEusarioDeManipulacaoDoRegistro(T entity) {
		
		if (!AtualizaBaseService.ATIVA_AUDITORIA_BASE_DADOS)
			return;
		
		// se caso não exista sessão ativa ignora a auditoria
		try {
			if (sessionController == null || sessionController.getNaUsuarioSistema() == null)
				return;
		} catch (ContextNotActiveException e) {
			return;
		}

		try {

			Method method;

			Field fieldUsuarioAuditoria = entity.getClass().getDeclaredField("idNaUsuarioAuditoria");

			if (fieldUsuarioAuditoria != null) {
				method = entity.getClass().getDeclaredMethod("setIdNaUsuarioAuditoria", Integer.class);
				method.invoke(entity, sessionController.getNaUsuarioSistema().getUsuario().getId());
			}

			Field fieldSistemaAuditoria = entity.getClass().getDeclaredField("idNaSistemaAuditoria");

			if (fieldSistemaAuditoria != null) {
				method = entity.getClass().getMethod("setIdNaSistemaAuditoria", Integer.class);
				method.invoke(entity, sessionController.getNaUsuarioSistema().getSistema().getId());
			}

		} catch (Exception ex) {
			return;
		}
	}

	public T save(T entity) throws NeaException {
		try {
			beforeSave(entity);
			removeCamposNull(entity);
			entity = (T) getSession().merge(entity);
			getSession().flush();
			afterSave(entity);

			if (auditar) {
				if (this.usuarioAuditoria != null && !this.usuarioAuditoria.getNome().trim().equals(""))
					getAuditoria().geraAuditoria(null, entity, this.usuarioAuditoria.getNome());
				else
					getAuditoria().geraAuditoria(null, entity);
			}

			return entity;
		} catch (ConstraintViolationException ex) {
			throw new NeaException("Erro ocorrido na base de dados!");
		} catch (Exception e) {
			e.printStackTrace();
			//log.error("Erro ao gravar objeto" + entity.getClass().getName());
			//log.error("Objeto: " + entity);
			//log.error("Causa: " + e.getMessage());
			e.printStackTrace();
			throw new NeaException(e.getMessage());
		}
	}

	@Deprecated
	protected void afterSave(T entity) throws NeaException {
		// criar codigo no dao especifico;
	}

	@Deprecated
	protected void beforeSaveAll(List<T> itens) throws NeaException {
		// criar codigo no dao especifico;
	}

	protected void rollbackSave(T entity) throws NeaException {
		// excluir entidades salvas no beforeSave caso a inserção não seja
		// concluida
	}

	public List<T> saveAll(List<T> itens) throws NeaException {
		try {
			beforeSaveAll(itens);
			List<T> list = new ArrayList<T>();
			for (T entity : itens) {
				if (entity != null) {
					entity = save(entity);
					list.add(entity);
				}
			}
			afterSaveAll(itens);
			return list;
		} catch (ConstraintViolationException ex) {
			throw new NeaException("Erro ocorrido na base de dados!");
		} catch (Exception e) {
			throw new NeaException(e.getMessage());
		}
	}

	@Deprecated
	protected void afterSaveAll(List<T> itens) throws NeaException {
		// criar codigo no dao especifico;
	}

	protected void beforeDelete(T entity) throws NeaException {
		save(entity);
	}

	/**
	 * Método utilizado para remover objetos nulos de dentro do objeto
	 * 
	 * Este método foi criado para resolver o problema causa pelo inputSearch, pois
	 * para que o inputSearch funcione preciso iniciar o objeto correspondente, ai
	 * na hora de salvar da erro.
	 * 
	 * @param entity
	 */
	private void removeCamposNull(Object entity) {

		try {
			Field[] attributes = entity.getClass().getDeclaredFields();
			for (Field field : attributes) {

				if (field.getAnnotation(JoinColumn.class) != null) {

					Object objField = FieldUtils.readDeclaredField(entity, field.getName(), true);

					if (objField != null) {

						// o handler só contém nos objetos
						// JavassistLazyInitializer
						if (FieldUtils.getField(objField.getClass(), "handler") == null) {
							Object obj = null;
							boolean limpaField = false;

							if (FieldUtils.getDeclaredField(objField.getClass(), "id", true) != null) {
								obj = FieldUtils.readDeclaredField(objField, "id", true);
								if (obj == null)
									limpaField = true;
							}

							if (limpaField)
								FieldUtils.writeDeclaredField(entity, field.getName(), null, true);
						}

					}
				}
			}

		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public void delete(T entity) throws NeaException {
		try {

			beforeDelete(entity);
			getSession().refresh(entity);
			Object o = getSession().merge(entity);

			if (auditar) {
				if (this.usuarioAuditoria != null && !this.usuarioAuditoria.getNome().trim().equals(""))
					getAuditoria().geraAuditoria(o, null, this.usuarioAuditoria.getNome());
				else
					getAuditoria().geraAuditoria(o, null);
			}

			getSession().delete(o);
			getSession().flush();
			afterDelete(entity);

		} catch (UnresolvableObjectException e) {
			// No folha existe uma trigger que ja faz este delete
			// coloquei o catch para não estourar exception
		} catch (Exception e) {
			e.printStackTrace();
			getSession().clear();
			throw new NeaException(e.getMessage());
		}
	}

	protected void afterDelete(T entity) throws NeaException {
		// criar codigo no dao especifico;
	}

	public final Integer getGeneratedIdForField(Object entity, String nameField) {
		Query qry = entityManager.createQuery("select max(t." + nameField + ") from " + entity.getClass().getSimpleName() + " t");

		Integer resultado = (Integer) qry.getSingleResult();

		if (resultado == null)
			return 1;

		return ++resultado;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public Session getSession() {
		return (Session) entityManager.getDelegate();
	}

	public Auditoria getAuditoria() {
		return auditoria;
	}

	public Criteria getCriteriaDinamicSearch() {
		// if (criteriaDinamicSearch == null) // comentei para não dar erro de
		// session is closed
		criteriaDinamicSearch = createCriteria();
		return criteriaDinamicSearch;
	}

	/**
	 * Limpa o entityManager
	 */
	public void cleanEntityManager() {
		entityManager.clear();
	}

	/**
	 * marca como detach o objeto passado como parâmetro
	 * 
	 * @param obj
	 */
	public void detachObject(Object obj) {
		if (obj != null)
			entityManager.detach(obj);
	}

	public void deleteAll(List<T> itens) throws Exception {

		beforeDeleteAll(itens);
		List<T> list = new ArrayList<T>();
		for (T entity : itens) {
			if (entity != null) {
				delete(entity);
			}
		}
		afterDeleteAll(itens);

	}

	public T loadObject(T object) {

		StringBuilder query = new StringBuilder();

		query.append("select o from  " + object.getClass().getName() + " o");

		Field[] fields = object.getClass().getDeclaredFields();

		String getPrimaryKey = "";
		String namePrimaryKey = "";

		for (Field field : fields) {

			if (field.getDeclaredAnnotation(Id.class) != null || field.getDeclaredAnnotation(EmbeddedId.class) != null) {
				getPrimaryKey = "get" + StringUtils.capitalize(field.getName());
				namePrimaryKey = field.getName();
			}

			if (field.getType().getName().startsWith("br.com.neainformatica") && !field.getType().isEnum()
					&& field.getDeclaredAnnotation(Transient.class) == null) {
				query.append(" left join fetch o." + field.getName());
			}
		}

		query.append(" where o." + namePrimaryKey + " = :idObject");

		Method method = null;
		Object invoke;
		try {
			method = object.getClass().getMethod(getPrimaryKey, new Class[] {});
			invoke = method.invoke(object, new Object[] {});

			Query qry = this.entityManager.createQuery(query.toString());

			qry.setParameter("idObject", Integer.parseInt(invoke.toString()));

			return (T) copyObject(qry.getSingleResult());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object copyObject(Object objSource) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(objSource);
			oos.flush();
			oos.close();
			bos.close();
			byte[] byteData = bos.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(byteData);
			try {
				return new ObjectInputStream(bais).readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return objSource;

	}

	protected void beforeDeleteAll(List<T> itens) throws Exception {
		// criar codigo no dao especifico;
	}

	protected void afterDeleteAll(List<T> itens) throws Exception {
		// criar codigo no dao especifico;
	}

}
