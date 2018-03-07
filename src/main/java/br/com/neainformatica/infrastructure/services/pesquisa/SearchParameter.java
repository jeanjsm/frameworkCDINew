package br.com.neainformatica.infrastructure.services.pesquisa;

public class SearchParameter {
	
	private String alias;
	private String field;
	private OperationEnum operation;
	private Object initialValue;
	private Object finalValue;
	private JavaType javaType;
	
	public SearchParameter() {
		
	}

	public SearchParameter(String field, Object value) {
		this.alias = "o";
		this.field = field;
		this.operation = OperationEnum.EQUALS;
		this.initialValue = value;
		this.finalValue = null;
		this.javaType = JavaType.INTEGER;
	}
	

	public SearchParameter(String field, Object value, JavaType javaType) {
		this.alias = "o";
		this.field = field;
		this.operation = OperationEnum.EQUALS;
		this.initialValue = value;
		this.finalValue = null;
		this.javaType = javaType;
	}
	
	public SearchParameter(String alias, String field, OperationEnum operation, Object initialValue, Object finalValue, JavaType javaType) {
		this.alias = alias;
		this.field = field;
		this.operation = operation;
		this.initialValue = initialValue;
		this.finalValue = finalValue;
		this.javaType = javaType;
	}
	
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getField() {
		return field;
	}
	
	public void setField(String field) {
		this.field = field;
	}
	
	public OperationEnum getOperation() {
		return operation;
	}
	
	public void setOperation(OperationEnum operation) {
		this.operation = operation;
	}
	
	public Object getInitialValue() {
		return initialValue;
	}
	
	public void setInitialValue(Object initialValue) {
		this.initialValue = initialValue;
	}
	
	public Object getFinalValue() {
		return finalValue;
	}
	
	public void setFinalValue(Object finalValue) {
		this.finalValue = finalValue;
	}

	public void setJavaType(JavaType javaType) {
		this.javaType = javaType;
	}

	public JavaType getJavaType() {
		return javaType;
	}
	
}
