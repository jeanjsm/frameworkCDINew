package br.com.neainformatica.infrastructure.bean.to;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias(value = "BeanSchemaTabelaBanco")
public class BeanSchemaTabelaBanco {

	private String schema;
	private String tabela;

	public BeanSchemaTabelaBanco(String schema, String tabela) {
		super();
		this.schema = schema;
		this.tabela = tabela;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getTabela() {
		return tabela;
	}

	public void setTabela(String tabela) {
		this.tabela = tabela;
	}

}
