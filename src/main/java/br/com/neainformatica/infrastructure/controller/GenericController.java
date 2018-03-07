package br.com.neainformatica.infrastructure.controller;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.enterprise.context.Conversation;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.primefaces.model.StreamedContent;

import br.com.neainformatica.infrastructure.enumeration.EnumStateForm;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoDadoColuna;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.searchengine.services.FilterController;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.tools.Tools;
import br.com.neainformatica.infrastructure.tools.ToolsReflection;

/**
 * Classe abstrata para ser utilizada como um controller genérico
 *
 * @author elielcio.santos
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public abstract class GenericController<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	protected Log log;

	@Inject
	protected Conversation conversation;

	@Inject
	protected NaSessionController naSessionController;

	@Inject
	protected GenericMessages messages;

	/**
	 * Atributo - Referencia o objeto passado como parametro
	 */
	protected T objeto;

	/**
	 * Atributo - Referencia o objeto que foi editado para auditoria
	 *
	 */
	protected T oldObjeto;

	/**
	 * Atributo - Lista paginada, é utilizado nos templates
	 *
	 */
	private LazyDataModel<T> lazyList;

	/**
	 * Atributo - Indica qual classe de serviço que será utilizada
	 *
	 */
	private GenericService<T> service;

	/**
	 * Atributo responsável pela pesquisa
	 *
	 */
	protected FilterController filterController;

	/**
	 * Atributo de verificação de tipo de tela (mestre ou detalhe)
	 *
	 */
	private Boolean master = true;

	/**
	 * Utilizado para controlar o estado do form e definir quando os campos são
	 * readOnly
	 */
	private EnumStateForm state = EnumStateForm.BROWSE;

	/**
	 * Atributo para gerenciar pagina do DataTable
	 */
	protected int pagina;

	private int posicaoPagina;

	/**
	 * Recupera a altura da tela para definir o tamanho do grid de listagem
	 *
	 */
	private int quantidadeLinhasNoGrid;

	private String atributoFiltroDefault;
	private Object valorFiltroDefault;
	private String campoCodigo;
	private boolean forceOpenDialog = false;
	private UIComponent componentActionOnComplete;

	private List<IFilter> filters = new ArrayList<IFilter>();

	/**
	 * Utilizado para definir filtros de pesquisa para os dados exibidos no grid
	 * das telas list.xhtml
	 */
	protected List<IFilter> filtrosDefault;

	private Object inputSearchSelectdObject;
	/**
	 * Quando solicitado ele clona o objeto principal (this.objeto) e devolve
	 * uma cópia Utilizado para se obter um novo objeto e não a referencia do
	 * anterior
	 */
	private T cloneObjetoPrincipal;

	private Object valorChave;

	/**
	 * Variável utilizada para armazenar o nome do Field onde deverá ser gravado
	 * o arquivo que vem do fileUPload
	 *
	 * @author elielcio.santos
	 */
	private String nomeFieldUpload;

	/**
	 * Método para criar uma nova instancia da entidade
	 *
	 * @return entidade criada
	 *
	 */
	public T newInstanceEntity() {

		try {
			return (T) ToolsReflection.getTNewInstance(this);
		} catch (Exception e) {
			log.error("Erro no metodo NewInstance!", e);
		}
		return null;
	}

	/**
	 * Este método inicializa a conversação da página
	 */
	public void initConversation() {
		if (conversation == null) {
			return;
		}
		if (!FacesContext.getCurrentInstance().isPostback() && conversation.isTransient()) {
			conversation.setTimeout(1800000);
			conversation.begin();
		}

	}

	public void endConversation() {
		if (conversation == null) {
			return;
		}
		try {
			conversation.end();
		} catch (Exception e) {
			log.error("Erro no metodo endConversation!", e);
			messages.addErrorMessage("Falha ao finalizar conversação: " + e.getMessage());
		}

	}

	public void reInitConversation() {
		endConversation();
		initConversation();
	}

	/**
	 * Método para realizar a busca de acordo com a lista de filtros
	 *
	 * @param listFilters
	 */
	public void dinamicSearch(final List<IFilter> listFilters) {

		setLazyList(new LazyDataModel<T>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setRowIndex(int rowIndex) {
				if (rowIndex == -1 || getPageSize() == 0) {
					super.setRowIndex(-1);
				} else {
					super.setRowIndex(rowIndex % getPageSize());
				}
			}

			@Override
			public Object getRowKey(T object) {
				return object;
			}

			@Override
			public T getRowData(String rowKey) {

				List<T> list = (List<T>) getWrappedData();

				return getSelectedObject(rowKey, list);
			}

			@Override
			public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

				int dataSize = 0;

				Collection<T> data = new ArrayList<>();

				data = (List<T>) getService().dinamicSearch(listFilters, first, pageSize);

				// dataSize = data.size();
				dataSize = Integer.valueOf(quantidadeRegistrosDinamicList(listFilters).intValue());

				this.setRowCount(dataSize);

				return (List<T>) data;
			}

		});

	}

	private T getSelectedObject(String rowKey, List<T> list) {

		T classSelected = list.get(0);

		String primaryKeyMethod = ToolsReflection.getPrimaryKeyMethod(classSelected);

		for (T t : list) {

			try {
				Object invoke = t.getClass().getMethod(primaryKeyMethod, null).invoke(t, null);

				if (String.valueOf(invoke).equals(rowKey))
					return t;

			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 *
	 * Método que busca todos os dados da entidade e insere na variável lazyList
	 *
	 *
	 */
	public void findAll() {

		setLazyList(new LazyDataModel<T>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void setRowIndex(int rowIndex) {

				if (rowIndex == -1 || getPageSize() == 0) {
					super.setRowIndex(-1);
				} else {
					super.setRowIndex(rowIndex % getPageSize());
				}
			}

			@Override
			public Object getRowKey(T object) {
				return object;
			}

			@Override
			public T getRowData(String rowKey) {
				List<T> list = (List<T>) getWrappedData();
				return getSelectedObject(rowKey, list);
			}

			@Override
			public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

				int dataSize = 0;

				Collection<T> data = new ArrayList<>();

				data = (List<T>) getService().findAllLazy(first, pageSize);

				dataSize = Integer.valueOf(quantidadeRegistrosLazyList().intValue());

				this.setRowCount(dataSize);

				return (List<T>) data;
			}

		});

	}

	/**
	 * Método executado antes da consulta que preenche a dataTable principal das
	 * telas list.xhtml
	 */
	protected void beforePesquisa() {
	}

	/**
	 *
	 * Método que pesquisa os dados com ou sem filtros
	 *
	 *
	 */
	public void filter() {

		// limpo os filtros de consultas anteriores
		this.filtrosDefault = new ArrayList<>();
		
		beforePesquisa();
		
		filters = getFilterController().doSimpleSearch();

		filters = filtersBeforeSearch(filters);

		// verifica se está definido os atributos atributoFiltroDefault e
		// valorFiltroDefault do inputSearch e
		// adiciona o filtro
		if ((filters == null || filters.isEmpty()) && (atributoFiltroDefault != null && valorFiltroDefault != null)) {
			filters = new ArrayList<>();
			filters.add(new EqualFilter(atributoFiltroDefault, valorFiltroDefault, ""));
		} else if (atributoFiltroDefault != null && valorFiltroDefault != null) {
			filters.add(new EqualFilter(atributoFiltroDefault, valorFiltroDefault, ""));
		}

		// Filtros personlizados
		if (this.filtrosDefault != null) {
			for (IFilter filtro : filtrosDefault) {
				filters.add(filtro);
			}
		}

		if (filters != null && !filters.isEmpty()) {
			dinamicSearch(filters);
		} else {
			findAll();
		}

		// ver forma de dar update na datatable
	}

	protected List<IFilter> filtersBeforeSearchInputSearch(List<IFilter> filters) {
		return filters;
	}

	protected List<IFilter> filtersBeforeSearch(List<IFilter> filters) {
		return filters;
	}

	public void filterAdvanced() {
		List<IFilter> filters = getFilterController().doAdvancedSearch();

		// verifica se está definido os atributos atributoFiltroDefault e
		// valorFiltroDefault do inputSearch e
		// adiciona o filtro
		if ((filters == null || filters.isEmpty()) && (atributoFiltroDefault != null && valorFiltroDefault != null)) {
			filters = new ArrayList<>();
			filters.add(new EqualFilter(atributoFiltroDefault, valorFiltroDefault, ""));
		} else if (atributoFiltroDefault != null && valorFiltroDefault != null) {
			filters.add(new EqualFilter(atributoFiltroDefault, valorFiltroDefault, ""));
		}

		if (filters != null && !filters.isEmpty()) {
			dinamicSearch(filters);
		} else {
			findAll();
		}
	}

	/**
	 * Componente: InputSearch <br/>
	 * Executado quando o usuário informa o código e sai do campo <br/>
	 * detach o objeto para o entityManager parar de gerenciá-lo
	 *
	 * @param event
	 * @throws IllegalAccessException
	 */
	public void detachObject(ActionEvent event) throws IllegalAccessException {

		if (this.objeto != null) {

			UIComponent component = findComponent(event.getComponent().getParent().getParent().getId());

			String campoInputSearch = component.getValueExpression("value").getExpressionString();

			// remover a ultima chave
			campoInputSearch = campoInputSearch.substring(0, campoInputSearch.length() - 1);

			// remover o controllher
			campoInputSearch = campoInputSearch.substring(campoInputSearch.indexOf(".") + 1);

			String[] partes = campoInputSearch.split("\\.");

			Object objInput = this.objeto;
			for (String field : partes) {
				if (!field.equalsIgnoreCase("objeto")) {
					objInput = FieldUtils.readDeclaredField(objInput, field, true);
				}
			}

			getService().detachObject(objInput);

		}

	}

	/**
	 * Componente: InputSearch <br/>
	 * Executado quando o usuário informa o código e sai do campo <br/>
	 * preenche o campo que deve deve ser filtrado
	 *
	 * @param event
	 */
	public void actionChangeKey(ActionEvent event) {

		this.forceOpenDialog = false;

		UIComponent component = findComponentAtributoCodigo(event.getComponent());
		String campoCodigo = component.getValueExpression("atributoCodigo").getExpressionString();
		campoCodigo = retornaFieldPesquisa(campoCodigo);

		this.campoCodigo = campoCodigo;

	}

	public UIComponent findComponentAtributoCodigo(UIComponent component) {

		UIComponent result = null;
		if (component.getValueExpression("atributoCodigo") != null) {
			return component;
		} else {
			if (component.getParent() != null) {
				result = findComponentAtributoCodigo(component.getParent());
			}
		}

		return result;
	}

	/**
	 * Componente: InputSearch <br/>
	 * Executado quando o usuário informa o código e sai do campo <br/>
	 * executa a pesquisa
	 *
	 * @return
	 */
	public String actionChangeKey() {
		// marco como detach porque outro objeto vai entrar no lugar
		// service.detachObject(this.objeto);

		this.objeto = null;

		try {
			if (this.valorChave != null) {

				if (!this.valorChave.toString().trim().equals("")) {

					this.objeto = findByField(valorChave, this.campoCodigo);

					if (this.objeto == null) {
						this.forceOpenDialog = true;
					}
				}

			}

			if (this.objeto != null && atributoFiltroDefault != null && valorFiltroDefault != null) {
				try {
					Object obj = ToolsReflection.getFieldValue(this.objeto, atributoFiltroDefault);
					if (obj == null || (!obj.equals(valorFiltroDefault))) {
						this.objeto = null;
					}
				} catch (SecurityException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
					this.objeto = null;
					log.error("Erro no método actionChangeKey!", e);
				}
			}
		} catch (Exception e) {
			// o valor passado é inválido
		}

		return "";
	}

	/**
	 * Componente: InputSearch <br/>
	 * Executado quando o usuário informa o código e sai do campo <br/>
	 * executa após a pesquisa
	 *
	 * @param event
	 */
	public void actionChangeKeyComplete(ActionEvent event) {

		// quando a pesquisa por código não retorna resultados devo abrir a
		// página de pesquisa
		if (isForceOpenDialog()) {
			RequestContext request = RequestContext.getCurrentInstance();
			request.execute("setForceDialog(true)");
		}

	}

	private static ValueExpression createValueExpression(String valueExpression, Class<?> valueType) {
		FacesContext context = FacesContext.getCurrentInstance();
		return context.getApplication().getExpressionFactory().createValueExpression(context.getELContext(), valueExpression, valueType);
	}

	private String retornaFieldPesquisa(String expression) {

		// tira a ultima chave
		expression = expression.substring(0, expression.length() - 1);

		int ultimoPonto = expression.lastIndexOf(".") + 1;
		expression = expression.substring(ultimoPonto, expression.length());

		return expression;
	}

	private UIComponent findComponent(String id) {

		UIComponent result = null;
		UIComponent root = FacesContext.getCurrentInstance().getViewRoot();
		if (root != null) {
			result = findComponent(root, id);
		}
		return result;

	}

	private UIComponent findComponent(UIComponent root, String id) {

		UIComponent result = null;
		if (root.getId().equals(id)) {
			return root;
		}

		for (UIComponent child : root.getChildren()) {
			if (child.getId().equals(id)) {
				result = child;
				break;
			}
			result = findComponent(child, id);
			if (result != null) {
				break;
			}
		}
		return result;
	}

	/**
	 * Componente: InputSearch <br/>
	 * Este método devolve uma cópia do objeto principal this.objeto para evitar
	 * que seja passada somente a referencia do objeto
	 *
	 * @return
	 */
	public Object getCloneObjetoPrincipal() {

		try {

			if (this.objeto == null) {
				this.cloneObjetoPrincipal = newInstanceEntity();
			} else {
				this.cloneObjetoPrincipal = (T) BeanUtils.cloneBean(this.objeto);
			}

		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			log.error("Erro no método getCloneObjetoPrincipal!", e);
		}

		return this.cloneObjetoPrincipal;

	}

	public String actionPreSearch() {
		setLazyList(null);
		filtersBeforeSearchInputSearch(this.filters);
		return null;
	}

	public String actionPosSelect() {
		return null;
	}

	/**
	 * Método para criar novo objeto para persistência
	 *
	 * @return String com a tela que será apresentada para criação do objeto
	 *
	 */
	public String create() {

		beforeCreate();

		this.state = EnumStateForm.INSERT;
		this.objeto = newInstanceEntity();
		this.oldObjeto = null;

		afterCreate();

		return getNomeFormularioForm();

	}

	/**
	 * Método para criar novo objeto para persistência OBS.: NÃO há retorno,
	 * pois nao deve fazer um refresh do modal ao instanciar
	 *
	 */
	public void createModal() {
		create();
	}

	/**
	 * Método executado antes de chamar o método create
	 */
	protected void beforeCreate() {
	}

	/**
	 * Método executado após chamar o método create
	 */
	protected void afterCreate() {
	}

	/**
	 * Método para editar um objeto existente
	 *
	 * @return String para retornar a página de listagem
	 *
	 */
	public String update() {

		beforeUpdate();

		this.state = EnumStateForm.EDIT;
		oldObjeto = (T) Tools.cloneSerializable((Serializable) objeto);

		afterUpdate();
		return getNomeFormularioForm();

	}

	/**
	 * Método executado antes de chamar o método update
	 */
	protected void beforeUpdate() {
	}

	/**
	 * Método executado após chamar o método update
	 */
	protected void afterUpdate() {
	}

	/**
	 * Método para editar um objeto existente, no detalhe (de um mestre-detalhe)
	 */
	public void updateInDetail() {
		this.state = EnumStateForm.EDIT;
		oldObjeto = (T) Tools.cloneSerializable((Serializable) objeto);
	}

	/**
	 * Método que cancela a inserção ou alteração de dados
	 *
	 * @return String para retornar a página de listagem
	 *
	 */
	public String cancel() {
		return getNomeFormularioList();
	}

	/**
	 * Cancela a inserção ou alteração no detalhe (de um mestre-detalhe)
	 *
	 */
	public void cancelDetail() {
		this.state = EnumStateForm.BROWSE;
	}

	/**
	 * Método para mostrar detalhes de um objeto selecionado
	 *
	 * @return String para retornar a página de formulario
	 *
	 */
	public String showFormDetail() {

		this.state = EnumStateForm.BROWSE;

		return getNomeFormularioForm();
	}

	/**
	 * Método para mostrar detalhes de um objeto selecionado, no detalhe (de um
	 * mestre-detalhe)
	 *
	 *
	 */
	public void showFormDetailInDetail() {

		this.state = EnumStateForm.BROWSE;

	}

	protected void beforeSave(T entity) {
	}

	protected void afterSave(T entity) {
	}

	public String salvar() {
		return salvar(true);
	}
	
	/**
	 * Método para gravar as informações inseridas na criação ou alteração de um
	 * novo objeto
	 * 
	 * @param imprimirMensagem boolean que determina se a mensagem de impresso com sucesso será exibida.
	 * @return String para retornar a página de listagem.
	 *
	 */
	public String salvar(boolean imprimirMensagem) {
		if (objeto != null) {
			try {
				beforeSave(objeto);
				objeto = getService().save(objeto, oldObjeto);
				afterSave(objeto);
				if(imprimirMensagem)
					messages.addInfoMessage("Registro salvo com sucesso... ");
			} catch (NeaException e) {
				messages.addErrorMessage(e.getMessage());
				return null;
			}
		}

		filter();

		return getNomeFormularioList();
	}

	public void salvarDetail() {
		salvarDetail(true);
	}
	
	/**
	 * Método para gravar as informações inseridas na criação ou alteração de um
	 * novo objeto, no detalhe (de um mestre-detalhe)
	 *
	 * @param imprimirMensagem boolean que determina se a mensagem de impresso com sucesso será exibida.
	 *
	 */
	public void salvarDetail(boolean imprimirMensagem) {
		if (objeto != null) {

			try {
				beforeSave(objeto);
				objeto = getService().save(objeto, oldObjeto);
				if(imprimirMensagem)
					messages.addInfoMessage("Registro salvo com sucesso... ");
				afterSave(objeto);
			} catch (NeaException e) {
				messages.addErrorMessage(e.getMessage());
			}
		}

		filter();

	}

	/**
	 *
	 * Método para gravar as informações inseridas na criação ou alteração de um
	 * objeto e criar um novo objeto
	 *
	 *
	 */
	public void salvarMais() {

		salvar();

		create();

	}

	/**
	 * Action executada ao clicar no botão delete, antes da confirmação
	 */
	public void prepareDelete() {
		log.debug("GenericController.prepareDelete");
	}

	/**
	 *
	 * Método para apagar um objeto selecionado na tela de listagem
	 *
	 *
	 */
	public void delete() {
		try {
			getService().delete(objeto);
			messages.addInfoMessage("Registro excluído com sucesso...");
			filter();
		} catch (NeaException e) {
			messages.addErrorMessage("Falha na exclusão: " + e.getMessage());
		}

	}

	/**
	 *
	 * Método para buscar o nome da tela de listagem no qual este controller
	 * esta referenciado
	 *
	 * <br/>
	 * <br/>
	 *
	 * @return String com o nome da página de listagem
	 *
	 *
	 */
	public String getNomeFormularioList() {

		if (getMaster()) {
			String nomeClasse = this.objeto.getClass().getSimpleName();
			nomeClasse = nomeClasse.substring(0, 1).toLowerCase().concat(nomeClasse.substring(1));

			return nomeClasse + "List";
		} else {
			return null;
		}

	}

	/**
	 *
	 * Método para buscar o nome da tela de formulario no qual este controller
	 * esta referenciado
	 *
	 * @return String com o nome da página de formulario
	 *
	 *
	 */
	public String getNomeFormularioForm() {

		if (getMaster()) {

			String nomeClasse = this.objeto.getClass().getSimpleName();
			nomeClasse = nomeClasse.substring(0, 1).toLowerCase().concat(nomeClasse.substring(1));

			return nomeClasse + "Form";

		} else {
			return null;
		}
	}

	/**
	 * Método para buscar a quantidade total de objetos existentes
	 *
	 * @return Number com a quantidade de objetos
	 *
	 *
	 */
	private Number quantidadeRegistrosLazyList() {
		return getService().criteriaSelectCount();
	}

	private Number quantidadeRegistrosDinamicList(List<IFilter> filters) {
		return getService().criteriaSelectCount(filters);
	}

	/**
	 * Método para realizar pesquisa por derterminado parâmetro
	 *
	 * <br/>
	 * <br/>
	 *
	 * @param object
	 *            - o que será buscado
	 * @param field
	 *            nome do campo que será pesquisado na entidade
	 *
	 * @return lista de Objetos da classe passada por parâmetro.
	 *
	 *
	 */
	public Collection<T> findByParam(Object object, String field) {
		return getService().findByParam(object, field);
	}

	/**
	 * Método para realizar a busca por determinado parâmetro
	 *
	 * @param object
	 *            valor de pesquisa
	 * @param field
	 *            nome do campo em que será feito a busca
	 * @return
	 */
	public T findByField(Object object, String field) {
		return getService().findByField(object, field);
	}

	/**
	 * Método para realizar pesquisa por id
	 *
	 * <br/>
	 * <br/>
	 *
	 * @param object
	 *            - o id que seré buscado
	 * @return objeto da classe passada por parâmetro.
	 *
	 *
	 */
	public T findByObjectId(Object object) {
		return getService().findByObjectId(object);
	}

	/**
	 * Método para realizar pesquisa por derterminado parâmetro
	 *
	 * <br/>
	 * <br/>
	 *
	 * @param object
	 *            - o que será buscado
	 * @param field
	 *            nome do campo que será pesquisado na entidade
	 * @param currentIndex
	 *            indice para iniciar a pesquisa
	 * @param count
	 *            indice final da pesquisa
	 *
	 * @return lista de Objetos da classe passada por parâmetro.
	 *
	 *
	 */
	public Collection<T> findByParamLazy(Object object, String field, int currentIndex, int count) {
		return getService().findByParamLazy(object, field, currentIndex, count);
	}

	// TODO - javadoc dos getters e setters
	public FilterController getFilterController() {

		/*
		 * Filter controller não pode ser singleton assim tem que se criar uma
		 * instantancia para cada controller se houver uma notação do cdi não
		 * singleton favor corrigir aqui
		 */
		if (filterController == null) {
			filterController = new FilterController(this.getObjeto().getClass());
		}

		return filterController;
	}

	public void setFilterController(FilterController filterController) {
		this.filterController = filterController;
	}

	public LazyDataModel<T> getLazyList() {

		if (this.lazyList == null) {
			filter();
		}

		return lazyList;
	}

	public void setLazyList(LazyDataModel<T> lazyList) {
		this.lazyList = lazyList;
	}

	public T getObjeto() {
		if (objeto == null && getService() != null) {
			objeto = newInstanceEntity();
		}
		return objeto;
	}

	public void setObjeto(T entity) {
		if (null != entity) {
			this.objeto = entity;
		}
	}

	public void setNullObjeto() {
		this.objeto = null;
	}

	public GenericService<T> getService() {
		return service;
	}

	public void setService(GenericService<T> service) {
		this.service = service;
	}

	public Boolean getMaster() {
		return master;
	}

	public void setMaster(Boolean master) {
		this.master = master;
	}

	public String getAtributoFiltroDefault() {
		return atributoFiltroDefault;
	}

	public void setAtributoFiltroDefault(String atributoFiltroDefault) {
		this.atributoFiltroDefault = atributoFiltroDefault;
	}

	public Object getValorFiltroDefault() {
		return valorFiltroDefault;
	}

	public void setValorFiltroDefault(Object valorFiltroDefault) {
		this.valorFiltroDefault = valorFiltroDefault;
	}

	public Object getInputSearchSelectdObject() {
		return inputSearchSelectdObject;
	}

	public void setInputSearchSelectdObject(Object inputSearchSelectdObject) {
		this.inputSearchSelectdObject = inputSearchSelectdObject;
		this.objeto = (T) inputSearchSelectdObject;
	}

	public Object getValorChave() {
		return valorChave;
	}

	public void setValorChave(Object valorChave) {
		this.valorChave = valorChave;
	}

	// ///// CRIAR METODOS PARA COLUNA DINAMICA
	private List<ColumnModel> columns = new ArrayList<>();

	static public class ColumnModel implements Serializable {

		private static final long serialVersionUID = 1L;

		private String header;
		private String property;
		private EnumTipoDadoColuna tipoDado;
		private String converter;

		public ColumnModel(String header, String property) {
			this.header = header;
			this.property = property;
			this.tipoDado = EnumTipoDadoColuna.TEXTO;
		}

		public ColumnModel(String header, String property, EnumTipoDadoColuna tipo) {
			this.header = header;
			this.property = property;
			this.tipoDado = tipo;
		}

		public ColumnModel(String header, String property, EnumTipoDadoColuna tipo, String converter) {
			this.header = header;
			this.property = property;
			this.tipoDado = tipo;
			this.converter = converter;
		}

		public String getHeader() {
			return header;
		}

		public String getProperty() {
			return property;
		}

		public EnumTipoDadoColuna getTipoDado() {
			return tipoDado;
		}

		public String getConverter() {
			return converter;
		}

	}

	public GenericController<T> clearColumns() {
		columns.clear();
		return this;
	}

	public GenericController<T> addColumn(ColumnModel column) {
		columns.add(column);
		return this;
	}

	public List<ColumnModel> getColumns() {
		return columns;
	}

	public EnumStateForm getState() {
		return state;
	}

	/**
	 * Busca os filtros para preencher as colunas dinâmicas do componente detail
	 *
	 * @return
	 */
	/*
	 * public List<ColunaDinamica> columnsDatatableDetailInterno(){
	 * List<ColunaDinamica> retorno = columnsDatatableDetail(); if (retorno ==
	 * null) retorno = columnsDatatableDynamic();
	 * 
	 * return retorno; }
	 */
	/**
	 * Busca os filtros para preencher as colunas dinâmicas do componente
	 * inputSearch
	 *
	 * @return
	 */
	/*
	 * public List<ColunaDinamica> columnsDatatableSearchInterno(){
	 * List<ColunaDinamica> retorno = columnsDatatableSearch(); if (retorno ==
	 * null) retorno = columnsDatatableDynamic();
	 * 
	 * return retorno; }
	 */
	/**
	 * Colunas a serem utilizadas nos datails
	 *
	 * @return
	 */
	/*
	 * protected List<ColunaDinamica> columnsDatatableDetail(){ return null; }
	 */
	/**
	 * Colunas a serem utilizadas nos inputSearchs
	 *
	 * @return
	 */
	/*
	 * protected List<ColunaDinamica> columnsDatatableSearch(){ return null; }
	 */
	/**
	 * Colunas a serem utilizadas no inputSearch e no detail
	 *
	 * @return
	 */

	/*
	 * protected List<ColunaDinamica> columnsDatatableDynamic(){ return null; }
	 */
	/**
	 * Criado para manter compatibilidade com o forms details *
	 *
	 * @return
	 */
	@Deprecated
	public boolean getViewStateInsert() {
		if (this.state == EnumStateForm.INSERT) {
			return true;
		}

		return false;
	}

	/**
	 * Criado para manter compatibilidade com o forms details *
	 *
	 * @return
	 */
	@Deprecated
	public boolean getViewStateEdit() {
		if (this.state == EnumStateForm.EDIT) {
			return true;
		}

		return false;
	}

	/**
	 *
	 * Método deve ser adicionado para controle de mestre-detalhe
	 *
	 * @param event
	 *            - busca o item clicado na view
	 */
	public void onRowSelect(SelectEvent event) {
		if (this.objeto != null) {
			log.debug("GenericController.onRowSelect: objeto selecionado = " + this.objeto);
		}
	}

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}

	public void setState(EnumStateForm state) {
		this.state = state;
	}

	/**
	 * Método utilizado para enviar uma imagem ou arquivo para um determinado
	 * field da Classe
	 *
	 * @param evento
	 * @author elielcio.santos
	 */
	public void anexarArquivo(FileUploadEvent evento) {
		try {
			byte[] imagem = evento.getFile().getContents();

			Method method = this.objeto.getClass().getMethod("set" + StringUtils.capitalize(this.nomeFieldUpload), byte[].class);

			// Method meth = objeto.getClass().getMethod (getNomeFieldUpload(),
			// new Class[]);
			Object parametros[] = new Object[1];
			parametros[0] = imagem;

			method.invoke(objeto, parametros);

			// this.objeto.setBrasaoMunicipio(imagem);
			messages.addInfoMessage("msgImgBrasao", "Arquivo importado com sucesso!", evento.getFile().getFileName());
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			messages.addFatalMessage("msgImgBrasao", "Um erro grave ocorreu ao importar o arquivo!", evento.getFile().getFileName());
		}
	}

	/**
	 * Método que recebe por parametro a pagina do dataTable selecionada
	 *
	 * @param event
	 */
	public void onPageChange(PageEvent event) {
		this.setPagina(((DataTable) event.getSource()).getFirst());
	}

	public StreamedContent getVisualizarImagemPrimeFaces() {

		try {

			byte[] img = null;

			if (this.nomeFieldUpload != null) {
				img = (byte[]) FieldUtils.readDeclaredField(this.objeto, this.nomeFieldUpload, true);
			}

			if (img != null) {
				return new DefaultStreamedContent(new ByteArrayInputStream(img), "image/jpg");
			} else {
				return null;
			}
		} catch (IllegalAccessException e) {
			log.error("Erro no Método getVisualizarImagemPrimeFaces!", e);
			return null;
		}

	}

	public List<Object> convertEnumToList(String nomeAtributo) {

		Field field = FieldUtils.getField(this.objeto.getClass(), nomeAtributo, true);

		return Tools.convertEnumToList(field.getType());

	}

	public void defineNomeImagem(String nomeImagem) {
		this.nomeFieldUpload = nomeImagem;
	}

	public void setNomeFieldUpload(String nomeFieldUpload) {
		this.nomeFieldUpload = nomeFieldUpload;
	}

	public String getCampoCodigo() {
		return campoCodigo;
	}

	public void setCampoCodigo(String campoCodigo) {
		this.campoCodigo = campoCodigo;
	}

	public boolean isForceOpenDialog() {
		return forceOpenDialog;
	}

	public String getContainerPadrao(String el) {
		return el.substring(el.lastIndexOf(":") + 1, el.length());
	}

	public int getQuantidadeLinhasNoGrid() {
		if (naSessionController.getQuantidadeLinhasGrid() > 0) {
			return naSessionController.getQuantidadeLinhasGrid();
		} else {
			int linhasMinimaNoGrid = 15;
			return linhasMinimaNoGrid;
		}

	}

	public void setQuantidadeLinhasNoGrid(int quantidadeLinhasNoGrid) {
		this.quantidadeLinhasNoGrid = quantidadeLinhasNoGrid;
	}

	public int getPagina() {
		if (posicaoPagina > 0) {
			return posicaoPagina;
		}
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public void paginacao(PageEvent event) {
		posicaoPagina = ((event.getPage()) * this.getQuantidadeLinhasNoGrid());

	}

	public String definirWidthForm(String maxWidth) {

		if (maxWidth == null || maxWidth.trim().equals("") || maxWidth.trim().equals("0")) {
			return "display: table; max-width : 800px;";
		} else {
			return "display: table; max-width : " + maxWidth + "; ";
		}
	}

}
