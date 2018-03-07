package br.com.neainformatica.infrastructure.rest.bean;

import br.com.neainformatica.infrastructure.enumeration.EnumTipoMensagemRetorno;

public class Mensagem {

	private EnumTipoMensagemRetorno tipo;
	private String Mensagem;

	public EnumTipoMensagemRetorno getTipo() {
		return tipo;
	}

	public void setTipo(EnumTipoMensagemRetorno tipo) {
		this.tipo = tipo;
	}

	public String getMensagem() {
		return Mensagem;
	}

	public void setMensagem(String mensagem) {
		Mensagem = mensagem;
	}

}
