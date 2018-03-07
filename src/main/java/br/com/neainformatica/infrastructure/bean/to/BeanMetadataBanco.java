package br.com.neainformatica.infrastructure.bean.to;

import java.io.Serializable;

import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "BeanMetadataBanco")
public class BeanMetadataBanco implements Serializable {
	private static final long serialVersionUID = 1L;

	private String tableSchema;
	private String tableName;
	private String fieldName;
	private EnumTipoColumnBD fieldType;
	private boolean fieldRequired;
	private Integer fieldLenght;
	private Integer fieldPrecision;
	private String fieldDefaultValue;

	public String getTableSchema() {
		return tableSchema;
	}

	public void setTableSchema(String tableSchema) {
		this.tableSchema = tableSchema;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public EnumTipoColumnBD getFieldType() {
		return fieldType;
	}

	public void setFieldType(EnumTipoColumnBD fieldType) {
		this.fieldType = fieldType;
	}

	public boolean isFieldRequired() {
		return fieldRequired;
	}

	public void setFieldRequired(boolean fieldRequired) {
		this.fieldRequired = fieldRequired;
	}

	public Integer getFieldLenght() {
		return fieldLenght;
	}

	public void setFieldLenght(Integer fieldLenght) {
		this.fieldLenght = fieldLenght;
	}

	public Integer getFieldPrecision() {
		return fieldPrecision;
	}

	public void setFieldPrecision(Integer fieldPrecision) {
		this.fieldPrecision = fieldPrecision;
	}

	public String getFieldDefaultValue() {
		return fieldDefaultValue;
	}

	public void setFieldDefaultValue(String fieldDefaultValue) {
		this.fieldDefaultValue = fieldDefaultValue;
	}

}
