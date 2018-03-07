package br.com.neainformatica.infrastructure.searchengine.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import br.com.neainformatica.infrastructure.searchengine.bean.AdvancedTreeBean;
import br.com.neainformatica.infrastructure.searchengine.bean.FilterBean;
import br.com.neainformatica.infrastructure.searchengine.converter.Converter;
import br.com.neainformatica.infrastructure.searchengine.enumeration.FilterEnum;
import br.com.neainformatica.infrastructure.searchengine.enumeration.OperatorEnum;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.AdvancedFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.AndFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.NotFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.OrFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;

public class FilterController {
	
	private static final Log log = LogFactory.getLog(FilterController.class);

	public FilterController(Class<?> tClass) {
		super();
		this.tClass = tClass;
		root = new DefaultTreeNode("root", null);
		lastTreeNode = root;
		lastFilterBean = getRootFilter();
		advancedTreeStack = new Stack<>();
		advancedFilterStack = new Stack<>();

		advancedTreeStack.push(root);
		advancedFilterStack.push(lastFilterBean);
	}

	public Class<?> tClass;
	/*
	 * Filtro Simples
	 */

	/** Campos que estão disponiveis para busca */
	protected List<String> simpleFilters = new ArrayList<>();
	/** Campo que foi selecionado para realizar a busca */
	private String filterBy = "";

	/** Valor que será atribuido como filtro */
	private String filterText = "";

	/** Filtros simples disponiveis neste controler */
	protected List<FilterBean> simpleFilterBeans = new ArrayList<>();

	public List<String> getSimpleFilters() {

		this.simpleFilters = new ArrayList<>();
		this.simpleFilterBeans = new ArrayList<>();
		String superProperty = "";

		simpleFilterBeans = Builder.buildSimpleFilters(tClass, superProperty);

		for (FilterBean filterBean : simpleFilterBeans) {
			simpleFilters.add(filterBean.getFieldName());
		}

		return this.simpleFilters;
	}

	public String getFilterBy() {
		return filterBy;
	}

	public void setFilterBy(String filterBy) {
		this.filterBy = filterBy;
	}

	public String getFilterText() {
		return filterText;
	}

	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	public List<IFilter> doSimpleSearch() {
		List<IFilter> filters = new ArrayList<>();

		if (filterBy != null && !filterBy.isEmpty() && filterText != null && !filterText.isEmpty()) {

			// pegar na entidade o campo que foi selecionado
			IFilter iFilter = Builder.getFilter(simpleFilterBeans, filterBy, filterText);
			filters.add(iFilter);

		}
		return filters;
	}

	public List<IFilter> doSimpleSearch(String _filterBy, String _filterText) {
		List<IFilter> filters = new ArrayList<>();

		if (_filterBy != null && !_filterBy.isEmpty() && _filterText != null && !_filterText.isEmpty()) {

			// pegar na entidade o campo que foi selecionado
			IFilter iFilter = Builder.getFilter(simpleFilterBeans, _filterBy, _filterText);
			filters.add(iFilter);

		}
		return filters;
	}

	/*
	 * ##########################################################################
	 * #################
	 * ########################################################
	 * ################################### Filtro Avançado
	 * ######################
	 * #####################################################################
	 * ####
	 * ######################################################################
	 * #################
	 */

	protected List<String> operatorFilters;
	private String operatorType = OperatorEnum.FILTER.getDescricao();

	protected List<String> advancedFilters;
	private String advancedFilterBy;

	/** Filtros simples disponiveis neste controler */
	protected List<FilterBean> advancedFilterBeans = new ArrayList<>();

	private Stack<FilterBean> advancedFilterStack = new Stack<>();
	private Stack<TreeNode> advancedTreeStack = new Stack<>();
	private TreeNode lastTreeNode = null;
	private FilterBean lastFilterBean = null;
	private AdvancedTreeBean lastAdvancedTreeBean = null;

	/** Valor que será atribuido como filtro */
	private String advancedInitialFilterText = "";

	/** Valor que será atribuido como filtro */
	private String advancedFinalFilterText = "";

	/** Valor que será atribuido como filtro */
	private boolean advancedFinalFilterEnable = false;

	protected List<String> criterionsFilters;
	private String criterionFilter;

	private String inLabel = "";

	private TreeNode root;

	private Integer nodeCount = 0;

	public List<String> getOperatorFilters() {

		if (operatorFilters == null || operatorFilters.isEmpty()) {
			operatorFilters = AdvancedSearchUtil.getOperators();
		}

		return operatorFilters;
	}

	public String getOperatorType() {
		return operatorType;
	}

	public void setOperatorType(String operatorType) {
		this.operatorType = operatorType;
	}

	public List<String> getAdvancedFilters() {

		advancedFilters = new ArrayList<>();
		this.advancedFilterBeans = new ArrayList<>();
		String superProperty = "";

		this.advancedFilterBeans = Builder.buildAdvancedFilters(tClass, superProperty);

		for (FilterBean filterBean : advancedFilterBeans) {
			advancedFilters.add(filterBean.getFieldName());
		}

		return this.advancedFilters;
	}

	public String getAdvancedFilterBy() {
		return advancedFilterBy;
	}

	public void setAdvancedFilterBy(String advancedFilterBy) {
		this.advancedFilterBy = advancedFilterBy;
	}

	public String getAdvancedInitialFilterText() {
		return advancedInitialFilterText;
	}

	public void setAdvancedInitialFilterText(String advancedInitialFilterText) {
		this.advancedInitialFilterText = advancedInitialFilterText;
	}

	public String getAdvancedFinalFilterText() {
		return advancedFinalFilterText;
	}

	public void setAdvancedFinalFilterText(String advancedFinalFilterText) {
		this.advancedFinalFilterText = advancedFinalFilterText;
	}

	public List<String> getCriterionsFilters() {

		if (criterionsFilters == null || criterionsFilters.isEmpty()) {
			criterionsFilters = AdvancedSearchUtil.getCriterions();
		}
		return criterionsFilters;
	}

	public void setCriterionsFilters(List<String> criterionsFilters) {
		this.criterionsFilters = criterionsFilters;
	}

	public String getCriterionFilter() {
		return criterionFilter;
	}

	public void setCriterionFilter(String criterionFilter) {
		this.criterionFilter = criterionFilter;

		inLabel = "                         ";

		if (criterionFilter != null && criterionFilter.equals(FilterEnum.BETWEEN.getDescricao())) {
			advancedFinalFilterEnable = true;// ativar segundo campo
		} else {
			advancedFinalFilterEnable = false; // suburbs = new HashMap<String,
												// String>();

			if (criterionFilter != null && criterionFilter.equals(FilterEnum.IN.getDescricao())) {
				inLabel = "Separe os dados por virgula";
			}
		}
	}

	public String getInLabel() {
		return inLabel;
	}

	public void setInLabel(String inLabel) {
		this.inLabel = inLabel;
	}

	public boolean getAdvancedFinalFilterEnable() {
		return advancedFinalFilterEnable;
	}

	public boolean isAdvancedFinalFilterEnable() {
		return advancedFinalFilterEnable;
	}

	public void handleCriterionChange() {

	}

	// private FilterBean rootFilter = null;

	public FilterBean getRootFilter() {

		AndFilter andFilter = new AndFilter(new ArrayList<IFilter>());
		FilterBean filterBean = new FilterBean(null, null, andFilter);

		// rootFilter = filterBean;
		return filterBean;
	}

	private FilterBean createAdvancedFilter() {

		FilterBean filterBean = Builder.getFilterBean(this.advancedFilterBeans, this.advancedFilterBy);

		FilterEnum filterEnum = AdvancedSearchUtil.getCriterion(this.criterionFilter);

		Object initialValue = null;

		if (filterEnum.equals(FilterEnum.IN)) {
			List<?> values = Arrays.asList(this.advancedInitialFilterText.toString().split(","));

			List<Object> result = new ArrayList<>();

			for (Object object : values) {
				result.add(Converter.convertFrom(object.toString(), filterBean.getFieldClass()));
			}

			initialValue = result;
		} else {
			initialValue = Converter.convertFrom(this.advancedInitialFilterText, filterBean.getFieldClass());
		}

		Object finalValue;
		if (!this.advancedFinalFilterText.equals("")) {
			finalValue = Converter.convertFrom(this.advancedFinalFilterText, filterBean.getFieldClass());
		} else {
			finalValue = this.advancedFinalFilterText;
		}

		IFilter filter = AdvancedSearchUtil.getFilter(filterEnum, ((AdvancedFilter) filterBean.getFilterParameter()).getField(), ((AdvancedFilter) filterBean.getFilterParameter()).getAlias(), initialValue, finalValue);

		return new FilterBean(filterBean.getFieldName(), filterBean.getFieldClass(), filter);
	}

	public void limparCamposAvancados() {

		this.advancedFilterBy = "";
		this.criterionFilter = "";
		this.advancedInitialFilterText = "";
		this.advancedFinalFilterText = "";
	}

	public boolean validaFiltrosPesquisaAvancada() {
		boolean retorno = true;

		if (this.operatorType == null || this.operatorType.equals("")) {
			retorno = false;
			addMessage(null, "Selecione um o tipo de operador.", FacesMessage.SEVERITY_ERROR);
		} else {

			if (this.operatorType.equals(OperatorEnum.FILTER.getDescricao()) || this.operatorType.equals(OperatorEnum.AND.getDescricao()) || this.operatorType.equals(OperatorEnum.OR.getDescricao())) {

				if (this.advancedFilterBy == null || this.advancedFilterBy.equals("")) {
					retorno = false;
					addMessage(null, "Selecione um campo para pesquisa.", FacesMessage.SEVERITY_ERROR);
				}

				if (this.criterionFilter == null || this.criterionFilter.equals("")) {
					retorno = false;
					addMessage(null, "Selecione um tipo de crit�rio.", FacesMessage.SEVERITY_ERROR);

					if (this.criterionFilter.equals(FilterEnum.BETWEEN.getDescricao())) {
						if (this.advancedInitialFilterText == null || this.advancedInitialFilterText.equals("")) {
							retorno = false;
							addMessage(null, "Informe o valor inicial para pesquisa.", FacesMessage.SEVERITY_ERROR);
						}
						if (this.advancedFinalFilterText == null || this.advancedFinalFilterText.equals("")) {
							retorno = false;
							addMessage(null, "Informe o valor Final para pesquisa.", FacesMessage.SEVERITY_ERROR);
						}
					}

				}

				if ((!this.criterionFilter.equals(FilterEnum.ISNULL.getDescricao())) && (this.advancedInitialFilterText == null || this.advancedInitialFilterText.equals(""))) {
					retorno = false;
					addMessage(null, "Informe um valor para pesquisa.", FacesMessage.SEVERITY_ERROR);
				}

			}
		}

		return retorno;

	}

	public void addAdvancedFilter() {

		try {
			log.debug("Operador: " + this.operatorType + " Campo: " + this.advancedFilterBy + " Criterio: " + this.criterionFilter + " Valor Inicial: " + this.advancedInitialFilterText + " Valor Final: "
					+ this.advancedFinalFilterText);

			if (validaFiltrosPesquisaAvancada()) {

				if (this.operatorType != null && !this.operatorType.isEmpty()) {

					nodeCount++;

					FilterBean filterBean = null;

					Boolean abreAgrupador = false;

					Boolean negar = false;

					if (this.operatorType.equals(OperatorEnum.OPEN_AGRUPATOR.getDescricao()) || this.operatorType.equals(OperatorEnum.NOT.getDescricao())) {

						if (this.operatorType.equals(OperatorEnum.NOT.getDescricao())) {
							negar = true;
						}

						if (advancedFilterStack.lastElement().getFilterParameter().getFilterBy().equals(FilterEnum.OR)) {
							this.operatorType = OperatorEnum.OR.getDescricao();
						} else {
							this.operatorType = OperatorEnum.AND.getDescricao();

						}

						abreAgrupador = true;
					}

					if (this.operatorType.equals(OperatorEnum.FILTER.getDescricao())) {
						filterBean = createAdvancedFilter();

						FilterBean topFilterBean = advancedFilterStack.lastElement();

						topFilterBean.addChildren(filterBean);

						String filtro = AdvancedSearchUtil.contruirFiltroString(filterBean);
						lastAdvancedTreeBean = new AdvancedTreeBean(filtro, filterBean);

						lastTreeNode = new DefaultTreeNode(lastAdvancedTreeBean, advancedTreeStack.lastElement());
					}

					if (this.operatorType.equals(OperatorEnum.AND.getDescricao())) {

						Integer filtroSize = 0;

						FilterBean topFilterBean = advancedFilterStack.lastElement();
						filtroSize = topFilterBean.getFilterBeans().size();

						// abrir agrupador and
						if (abreAgrupador || (this.advancedFilterBy == null || this.advancedFilterBy.isEmpty()) || !(advancedFilterStack.lastElement().getFilterParameter().getFilterBy().equals(FilterEnum.AND))) {

							AndFilter andFilter = new AndFilter(new ArrayList<IFilter>());
							filterBean = new FilterBean(null, null, andFilter);

							String filtro = "";

							if (filtroSize > 0) {
								filtro += "E ";
							}

							if (negar) {

								NotFilter notFilter = new NotFilter(new ArrayList<IFilter>(), "");
								filterBean = new FilterBean(null, null, notFilter);

								filtro += "não tenha";
							}

							filtro += "(";

							lastAdvancedTreeBean = new AdvancedTreeBean(filtro, filterBean);

							lastTreeNode = new DefaultTreeNode(lastAdvancedTreeBean, advancedTreeStack.lastElement());

							advancedTreeStack.push(lastTreeNode);
							advancedFilterStack.push(filterBean);

							topFilterBean.addChildren(filterBean);
							topFilterBean = advancedFilterStack.lastElement();
							filtroSize = topFilterBean.getFilterBeans().size();

							if (!(this.advancedFilterBy == null || this.advancedFilterBy.isEmpty())) {
								filterBean = createAdvancedFilter();

								filtro = AdvancedSearchUtil.contruirFiltroString(filterBean);

								lastAdvancedTreeBean = new AdvancedTreeBean(filtro, filterBean);

								lastTreeNode = new DefaultTreeNode(lastAdvancedTreeBean, advancedTreeStack.lastElement());
								topFilterBean.addChildren(filterBean);
							} else {
								this.operatorType = OperatorEnum.FILTER.getDescricao();
							}

						} else {// so adicionar filtro

							filterBean = createAdvancedFilter();

							String filtro = "";
							if (filtroSize > 0) {
								filtro += "E ";
							}
							filtro += AdvancedSearchUtil.contruirFiltroString(filterBean);

							lastAdvancedTreeBean = new AdvancedTreeBean(filtro, filterBean);

							lastTreeNode = new DefaultTreeNode(lastAdvancedTreeBean, advancedTreeStack.lastElement());

							topFilterBean.addChildren(filterBean);
						}

					}

					if (this.operatorType.equals(OperatorEnum.OR.getDescricao())) {

						Integer filtroSize = 0;

						FilterBean topFilterBean = advancedFilterStack.lastElement();
						filtroSize = topFilterBean.getFilterBeans().size();

						// abrir agrupador and
						if (abreAgrupador || (this.advancedFilterBy == null || this.advancedFilterBy.isEmpty()) || !(advancedFilterStack.lastElement().getFilterParameter().getFilterBy().equals(FilterEnum.OR))) {

							OrFilter orFilter = new OrFilter(new ArrayList<IFilter>(), "");
							filterBean = new FilterBean(null, null, orFilter);

							String filtro = "";

							if (filtroSize > 0) {
								filtro += "Ou ";
							}

							if (negar) {

								NotFilter notFilter = new NotFilter(new ArrayList<IFilter>(), "");
								filterBean = new FilterBean(null, null, notFilter);

								filtro += "não tenha";
							}

							filtro += "(";

							lastAdvancedTreeBean = new AdvancedTreeBean(filtro, filterBean);

							lastTreeNode = new DefaultTreeNode(lastAdvancedTreeBean, advancedTreeStack.lastElement());

							advancedTreeStack.push(lastTreeNode);
							advancedFilterStack.push(filterBean);

							topFilterBean.addChildren(filterBean);
							topFilterBean = advancedFilterStack.lastElement();
							filtroSize = topFilterBean.getFilterBeans().size();

							if (!(this.advancedFilterBy == null || this.advancedFilterBy.isEmpty())) {
								filterBean = createAdvancedFilter();

								filtro = AdvancedSearchUtil.contruirFiltroString(filterBean);

								lastAdvancedTreeBean = new AdvancedTreeBean(filtro, filterBean);

								lastTreeNode = new DefaultTreeNode(lastAdvancedTreeBean, advancedTreeStack.lastElement());
								topFilterBean.addChildren(filterBean);
							} else {
								this.operatorType = OperatorEnum.FILTER.getDescricao();
							}
						} else {// so adicionar filtro

							filterBean = createAdvancedFilter();

							String filtro = "";
							if (filtroSize > 0) {
								filtro += "Ou ";
							}
							filtro += AdvancedSearchUtil.contruirFiltroString(filterBean);

							lastAdvancedTreeBean = new AdvancedTreeBean(filtro, filterBean);

							lastTreeNode = new DefaultTreeNode(lastAdvancedTreeBean, advancedTreeStack.lastElement());
							topFilterBean.addChildren(filterBean);
						}

					}

					if (this.operatorType.equals(OperatorEnum.CLOSE_AGRUPATOR.getDescricao())) {
						// lastFilterBean
						lastAdvancedTreeBean.setFieldName(lastAdvancedTreeBean.getFieldName() + ")");
						advancedTreeStack.pop();
						advancedFilterStack.pop();

						// pega o operator anterior a ele

					}

					if (advancedFilterStack.size() == 0) {
						this.operatorType = OperatorEnum.FILTER.getDescricao();
					} else if (advancedFilterStack.lastElement().getFilterParameter().getFilterBy().equals(FilterEnum.OR)) {
						this.operatorType = OperatorEnum.OR.getDescricao();
					} else {
						this.operatorType = OperatorEnum.AND.getDescricao();

					}

					limparCamposAvancados();
				}

			}
		} catch (NumberFormatException e) {
			addMessage(null, "O valor numérico informado é inválido.", FacesMessage.SEVERITY_ERROR);
			// e.printStackTrace();
		} catch (Exception e) {
			addMessage(null, "O valor informado é inválido.", FacesMessage.SEVERITY_ERROR);
			// e.printStackTrace();
		}

	}

	private void addMessage(String componentId, String errorMessage, Severity severity) {

		FacesContext context = FacesContext.getCurrentInstance();

		FacesMessage message = new FacesMessage(errorMessage);
		message.setSeverity(severity);

		context.addMessage(componentId, message);
	}

	public void removeAllAdvancedFilter() {

		root = new DefaultTreeNode("root", null);

		lastFilterBean = getRootFilter();
		advancedFilterStack = new Stack<>();
		advancedFilterStack.push(lastFilterBean);

		advancedTreeStack = new Stack<>();
		advancedTreeStack.push(root);
		lastTreeNode = root;

		limparCamposAvancados();

		this.operatorType = OperatorEnum.FILTER.getDescricao();
	}

	public TreeNode getRoot() {
		return root;
	}

	public IFilter parser(FilterBean filterBean) {

		//List<FilterBean> orFilters = new ArrayList<>();

		//List<FilterBean> andFilters = new ArrayList<>();

		AndFilter andFilter = new AndFilter(null);

		/*
		 * for (TreeNode treeNode : filterBean) { AdvancedTreeBean
		 * advancedTreeBean = (AdvancedTreeBean) treeNode.getData();
		 * 
		 * FilterBean filterBean = advancedTreeBean.getFilterBean();
		 * 
		 * if(filterBean.getFilterParameter().getFilterBy().equals(FilterEnum.OR)
		 * ){
		 * 
		 * }
		 * 
		 * }
		 */

		return andFilter;
	}

	public List<IFilter> doAdvancedSearch() {

		List<IFilter> listaRetorno = new ArrayList<>();

		for (TreeNode tn : root.getChildren()) {

			AdvancedTreeBean beanPesquisa = (AdvancedTreeBean) tn.getData();
			FilterEnum tipoFiltro = beanPesquisa.getFilterBean().getFilterParameter().getFilterBy();

			if (tipoFiltro == FilterEnum.OR) {
				OrFilter orFilter = (OrFilter) beanPesquisa.getFilterBean().getFilterParameter();

				for (FilterBean filter : beanPesquisa.getFilterBean().getFilterBeans()) {
					orFilter.getFilters().add(filter.getFilterParameter());
				}

				listaRetorno.add(orFilter);

			} else {
				listaRetorno.add(beanPesquisa.getFilterBean().getFilterParameter());
			}
		}

		return listaRetorno;

	}

}
