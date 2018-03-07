package br.com.neainformatica.infrastructure.rest.bean;

import java.util.ArrayList;
import java.util.List;

public class MensagemRetorno {

	private List<Mensagem> mensagens;
	
	
	
	public MensagemRetorno() {
		this.mensagens = new ArrayList<>();
	}
	

	public List<Mensagem> getMensagens() {
		return mensagens;
	}

	public void setMensagens(List<Mensagem> mensagens) {
		this.mensagens = mensagens;
	}

}
