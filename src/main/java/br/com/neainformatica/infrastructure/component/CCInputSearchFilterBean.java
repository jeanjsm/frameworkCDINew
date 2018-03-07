package br.com.neainformatica.infrastructure.component;

import java.io.Serializable;

import br.com.neainformatica.infrastructure.searchengine.enumeration.EnumFilterMethod;

public class CCInputSearchFilterBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Class<?> typeObject;
	private String fieldName;
	private String fieldAlias;
	private Object fieldValue;
	private EnumFilterMethod filterParameter;

	public Class<?> getTypeObject() {
		return typeObject;
	}

	public void setTypeObject(Class<?> typeObject) {
		this.typeObject = typeObject;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldAlias() {
		return fieldAlias;
	}

	public void setFieldAlias(String fieldAlias) {
		this.fieldAlias = fieldAlias;
	}

	public Object getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	public EnumFilterMethod getFilterParameter() {
		return filterParameter;
	}

	public void setFilterParameter(EnumFilterMethod filterParameter) {
		this.filterParameter = filterParameter;
	}

	@Override
	public String toString() {
		return "CCInputSearchFilterBean [fieldName=" + fieldName + ", fieldAlias=" + fieldAlias + ", fieldValue=" + fieldValue
				+ ", filterParameter=" + filterParameter +  ", typeObject=" + typeObject + "]";
	}

}
