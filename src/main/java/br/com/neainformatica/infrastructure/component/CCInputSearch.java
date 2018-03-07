package br.com.neainformatica.infrastructure.component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.hibernate.proxy.HibernateProxy;
import org.jfree.util.Log;

import br.com.neainformatica.infrastructure.controller.GenericController;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.tools.ToolsReflection;

@FacesComponent(value = "CCInputSearch")
public class CCInputSearch extends UIInput implements NamingContainer {

	@Inject
	private CCInputSearchService service;

	List<CCInputSearchFilterBean> simpleFilters = null;
	List<?> lista;
	private CCInputSearchFilterBean selectedFilter = null;
	private String valueFilter = "";
	private String codigoValor;
	private Object objetoSelecionado;

	private String[] dynamicHeaders = null;
	private String[] dynamicColumns = null;

	public CCInputSearch() {
	}

	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		Log.debug("CCInputSearch encodeBegin");
		montarColunas();
		Object filtroDefault = getAttributes().get("filtroDefault");
		objetoSelecionado = getAttributes().get("value");
		if(filtroDefault != null)
			atualizaFiltroDefault(filtroDefault.toString());
		super.encodeBegin(context);
	}

	private void montarColunas() {

		dynamicHeaders = new String[getSimpleFilters().size()];
		dynamicColumns = new String[getSimpleFilters().size()];
		
		for (int i = 0; i < getSimpleFilters().size(); i++) {
			CCInputSearchFilterBean filterBean = getSimpleFilters().get(i);
			String nomeColuna = filterBean.getFieldAlias();
			String nomeField = filterBean.getFieldName();
			
			dynamicHeaders[i] = nomeColuna;
			dynamicColumns[i] = nomeField;
		}
	}

	public List<CCInputSearchFilterBean> getSimpleFilters() {
		try {
			if (simpleFilters == null) {
				Object colunasUser = getAttributes().get("fieldList");
				if(colunasUser == null)
					simpleFilters = service.buildSimpleFilters(getClassType(), null, null);
				else {
					StringTokenizer tokenizer = new StringTokenizer(colunasUser.toString(), ";");
					List<String> fieldList = new ArrayList<>();
					while(tokenizer.hasMoreTokens()) {
						fieldList.add(tokenizer.nextToken());
					}
					simpleFilters = service.buildSimpleFilters(getClassType(), null, null, fieldList);
				}
			
			}
		} catch (Exception e) {	
			e.printStackTrace();
		}
		return simpleFilters;
	}

	public String getValorByFieldName(Object item, String fieldName) {
		try {
			StringTokenizer tokenizer = new StringTokenizer(fieldName, ".");
			Object fieldValue = item;
			while (tokenizer.hasMoreTokens()) {
				String aux = tokenizer.nextToken();
				fieldValue = ToolsReflection.getFieldValue(fieldValue, aux);
			}
			if(fieldValue != null)
				return fieldValue.toString();
			else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getCodigoValor() {
		try {
			String fieldName = getAttributes().get("atributoCodigo").toString();
			StringTokenizer tokenizer = new StringTokenizer(fieldName, ".");
			Object fieldValue = getObjetoSelecionado();
			//Converte Instancia Hibernate Proxy no objeto real
			if (fieldValue instanceof HibernateProxy) {
				fieldValue = ((HibernateProxy) fieldValue).getHibernateLazyInitializer()
		                .getImplementation();
		    }
			if (fieldValue == null) {
				return "";
			}
			while (tokenizer.hasMoreTokens()) {
				String aux = tokenizer.nextToken();
				fieldValue = ToolsReflection.getFieldValue(fieldValue, aux);
			}
			if(fieldValue != null)
				return fieldValue.toString();
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public void setCodigoValor(String codigoValor) {
		this.codigoValor = codigoValor;
	}

	private void atualizaFiltroDefault(String fieldName) {
		selectedFilter = getSimpleFilters().stream().filter(item -> item.getFieldName().equals(fieldName)).findFirst().orElse(null);
	}

	public void pesquisar() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		@SuppressWarnings("unchecked")
		List<IFilter> beforeFilters = (List<IFilter>) getAttributes().get("filtersBeforeSearch");
		Boolean mask= (Boolean)getAttributes().get("enableMask");
		String campoMascarado = (String)getAttributes().get("campoMask");
		if(campoMascarado != null){
		if(selectedFilter != null && selectedFilter.getFieldName().equals(campoMascarado)){
			if(mask){
				valueFilter=valueFilter.replaceAll("\\D", "");	
			}
		 }
		}
		setLista(service.pesquisar(getClassType(), valueFilter, selectedFilter, getSimpleFilters(), beforeFilters, qtdeRegistros()));
	}

	private Integer qtdeRegistros() {
		Object obj = getAttributes().get("qtdeMaxResults");
		if(obj != null)
			return Integer.parseInt(obj.toString());
		else
			return null;
	}

	public void selecionarRegistro(Object obj) {
		this.objetoSelecionado = obj;
		// pesquisar();
	}

	public void buscarRegistro() throws ClassNotFoundException {
		String fieldName = getAttributes().get("atributoCodigo").toString();
		
		@SuppressWarnings("unchecked")
		List<IFilter> beforeFilters = (List<IFilter>) getAttributes().get("filtersBeforeSearch");
		this.objetoSelecionado = service.pesquisarSingleResult(getClassType(), codigoValor, fieldName, getSimpleFilters(), beforeFilters);
	}
	
	@SuppressWarnings("rawtypes")
	public void novoItem() throws ClassNotFoundException {
		GenericController entityController = (GenericController) getAttributes().get("entityController");
		entityController.salvar();
		this.objetoSelecionado = entityController.getObjeto();
	}
	
	@SuppressWarnings("rawtypes")
	public void inicializaNovoItem() throws ClassNotFoundException {
		GenericController entityController = (GenericController) getAttributes().get("entityController");
		entityController.create();
	}


	private Class<?> getClassType() throws ClassNotFoundException {
		String classType = (String) getAttributes().get("classType");
		if(classType == null)
			Log.error("Favor Informar a propriedade classType.");
		return Class.forName(classType);
	}

	public CCInputSearchFilterBean getSelectedFilter() {
		return selectedFilter;
	}

	public void setSelectedFilter(CCInputSearchFilterBean selectedFilter) {
		this.selectedFilter = selectedFilter;
	}

	public String getValueFilter() {
		return valueFilter;
	}

	public void setValueFilter(String valueFilter) {
		this.valueFilter = valueFilter;
	}

	enum ProkertyKeys {
		lista, novoItemFormRendered
	};

	public List<?> getLista() {
		lista = (List<?>) getStateHelper().eval(ProkertyKeys.lista);
		return lista;
	}

	public void setLista(List<?> lista) {
		this.lista = lista;
		getStateHelper().put(ProkertyKeys.lista, lista);
	}

	public String[] getDynamicHeaders() {
		if (dynamicHeaders == null)
			montarColunas();
		return dynamicHeaders;
	}

	public void setDynamicHeaders(String[] dynamicHeaders) {
		this.dynamicHeaders = dynamicHeaders;
	}

	public String[] getDynamicColumns() {
		if (dynamicColumns == null)
			montarColunas();
		return dynamicColumns;
	}

	public void setDynamicColumns(String[] dynamicColumns) {
		this.dynamicColumns = dynamicColumns;
	}

	public Object getObjetoSelecionado() {
		return objetoSelecionado;
	}

	public void setObjetoSelecionado(Object objetoSelecionado) {
		this.objetoSelecionado = objetoSelecionado;
	}

	public Boolean getNovoItemFormRendered() {	
		Boolean result = (Boolean) getStateHelper().eval(ProkertyKeys.novoItemFormRendered);
		if(result == null)
			result = false;
		return result;
	}

	public void setNovoItemFormRendered(Boolean novoItemFormRendered) {
		getStateHelper().put(ProkertyKeys.novoItemFormRendered, novoItemFormRendered);
	}

	public void toggleNovoItemForm() {
		Boolean result = getNovoItemFormRendered();
		setNovoItemFormRendered(!result);
	}
}
