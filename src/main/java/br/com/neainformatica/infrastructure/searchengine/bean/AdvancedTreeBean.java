package br.com.neainformatica.infrastructure.searchengine.bean;


/**
 * Bean utilizado para montar pesquisa dinamica
 * 
 * @author Pedro Sanchez
 * */
public class AdvancedTreeBean {
	private String fieldName;
	private FilterBean filterBean;
	
	public AdvancedTreeBean(String fieldName, FilterBean filterBean) {
		super();
		this.fieldName = fieldName;
		this.filterBean = filterBean;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public FilterBean getFilterBean() {
		return filterBean;
	}

	public void setFilterBean(FilterBean filterBean) {
		this.filterBean = filterBean;
	}
	
	
	

	
	
}
