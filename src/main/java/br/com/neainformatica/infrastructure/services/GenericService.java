package br.com.neainformatica.infrastructure.services;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

public abstract class GenericService<T> {

	@Inject
	protected Log log;

	private boolean auditar = true;
	private boolean ignoreBeforeSave = false;
	private boolean ignoreAfterSave = false;

	/**
	 * Este deve ser utilizado para rotinas onde o usuário esta fora do JSF, ex:
	 * REST
	 */	
	protected NaUsuario usuarioAuditoria;

	protected void beforeInsert(T entity) {
	}

	protected void afterInsert(T entity) {
	}

	protected void beforeUpdate(T entity) {
	}

	protected void afterUpdate(T entity) {
	}

	protected void beforeSave(T entity) {
	}

	/**
	 * Favor utilizar o método noAudit()
	 * @return
	 */
	@Deprecated
	public GenericService<T> isAudit() {
		this.auditar = false;
		return this;
	}

	public GenericService<T> noAudit() {
		this.auditar = false;
		return this;
	}

	public GenericService<T> ignoreBeforeSave() {
		this.ignoreBeforeSave = true;
		return this;
	}
	
	public GenericService<T> ignoreAfterSave() {
		this.ignoreAfterSave = true;
		return this;
	}

	public GenericService<T> setUserAudit(NaUsuario user) {
		this.usuarioAuditoria = user;
		return this;
	}

/*	public void setUsuarioAuditoria(NaUsuario usuarioAuditoria) {

		// tenho que setar este usuário em todos os services injetados

		this.usuarioAuditoria = usuarioAuditoria;

		Field[] fields = FieldUtils.getAllFields(getClass());

		for (Field field : fields) {

			if (field.getType().getSuperclass() != null && (field.getType().getSuperclass().equals(GenericService.class))) {

				try {

					Object parametros[] = new Object[1];
					parametros[0] = usuarioAuditoria;

					Object obj = FieldUtils.readDeclaredField(this, field.getName(), true);

					if (obj == null)
						continue;

					MethodUtils.invokeMethod(obj, "setUsuarioAuditoria", parametros);

					log.debug("preenchendo usuário auditoria: classe: " + field.getType() + "usuario: " + usuarioAuditoria);

				} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
					e.printStackTrace();
				}

			}
		}

	}*/

	/**
	 * Grava o objeto com auditoria
	 * 
	 * @param entity
	 * @param oldEntity
	 * @return
	 */
	public T save(T entity, T oldEntity) throws NeaException {
		try {
			if(!ignoreBeforeSave)
				beforeSave(entity);
			if (auditar) {

				if (this.usuarioAuditoria != null && !this.usuarioAuditoria.getNome().trim().equals(""))
					entity = getRepository().setUserAudit(this.usuarioAuditoria).save(entity, oldEntity);
				else
					entity = getRepository().save(entity, oldEntity);
			} else
				entity = getRepository().noAudit().save(entity, oldEntity);
			if(!ignoreAfterSave)
				afterSave(entity);
			return entity;
		} catch (Exception e) {
			throw new NeaException(e.getMessage());
		}
	}

	/**
	 * grava o objeto sem auditoria
	 * 
	 * @param entity
	 * @param oldEntity
	 * @return
	 */
	public T save(T entity) throws NeaException {
		try {
			if(!ignoreBeforeSave)
				beforeSave(entity);

			if (auditar) {
				if (this.usuarioAuditoria != null && !this.usuarioAuditoria.getNome().trim().equals(""))
					entity = getRepository().setUserAudit(this.usuarioAuditoria).save(entity);
				else
					entity = getRepository().save(entity);
			} else
				entity = getRepository().noAudit().save(entity);

			if(!ignoreAfterSave)
				afterSave(entity);
			return entity;
		} catch (Exception e) {
			e.printStackTrace();
			throw new NeaException(e.getMessage());
		}

	}

	protected void afterSave(T entity) {
	}

	protected void beforeSaveAll(List<T> itens) {
	}

	public List<T> saveAll(List<T> itens) throws NeaException {
		try {
			beforeSaveAll(itens);
			
			if(!auditar)
				getRepository().noAudit();				
				
			itens = getRepository().saveAll(itens);			
			afterSaveAll(itens);
			return itens;
		} catch (Exception e) {
			throw new NeaException(e.getMessage());
		}

	}

	public void deleteAll(List<T> itens) throws NeaException {
		try {
			beforeDeleteAll(itens);
			getRepository().deleteAll(itens);
			afterDeleteAll(itens);
		} catch (Exception e) {
			throw new NeaException(e.getMessage());
		}
	}

	protected void afterDeleteAll(List<T> itens) throws NeaException {
	}

	protected void beforeDeleteAll(List<T> itens) throws NeaException {
	}

	protected void afterSaveAll(List<T> itens) {
	}

	protected void beforeDelete(T entity) throws NeaException {
	}

	public void delete(T entity) throws NeaException {
		try {
			beforeDelete(entity);
			getRepository().delete(entity);
			afterDelete(entity);
		} catch (NeaException nea) {
			throw new NeaException(nea.getMessage());
		} catch (Exception e) {
			throw new NeaException(e.getMessage());
		}
	}

	protected void afterDelete(T entity) {

	}

	public List<T> findAll() {
		return getRepository().findAll();
	}

	public List<T> findAllLazy(int currentIndex, int count) {
		return getRepository().findAllLazy(currentIndex, count);
	}

	public List<T> findByParam(Object object, String field) {
		return getRepository().findByParam(object, field);
	}

	@SuppressWarnings("unchecked")
	public T findByField(Object object, String field) {
		return (T) getRepository().findByField(object, field);
	}

	public List<T> findByParamLazy(Object object, String field, int currentIndex, int count) {
		return getRepository().findByParamLazy(object, field, currentIndex, count);
	}

	public T findById(Integer id) {
		return getRepository().findById(id);
	}

	public T findByObjectId(Object id) {
		return getRepository().findByObjectId(id);
	}

	public Number criteriaSelectCount() {
		return getRepository().criteriaSelectCount();
	}

	public Number criteriaSelectCount(List<IFilter> filters) {
		return getRepository().criteriaSelectCount(filters);
	}

	public Number criteriaSelectCountByParam(Object object, String field) {
		return getRepository().criteriaSelectCountByParam(object, field);
	}

	/**
	 * Realiza a busca dinamica sem utilizar a paginacao
	 * 
	 * @param filters
	 * @return
	 */
	public List<T> dinamicSearch(List<IFilter> filters) {
		return getRepository().dinamicSearch(filters);
	}

	/**
	 * Realiza a busca dinâmica utilizando a paginação dos dados
	 * 
	 * @param filters
	 * @param currentIndex
	 * @param count
	 * @return
	 */
	public List<T> dinamicSearch(List<IFilter> filters, int currentIndex, int count) {
		return getRepository().dinamicSearch(filters, currentIndex, count);
	}

	/**
	 * marca como detach o objeto passado como parâmetro
	 * 
	 * @param obj
	 */
	public void detachObject(Object obj) {
		getRepository().detachObject(obj);
	}

	public abstract GenericRepository<T> getRepository();
	
	public GenericService<T> useEntityManager(EntityManager em) {
		getRepository().setEntityManager(em);
		return this;
	}
}