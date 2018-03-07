package br.com.neainformatica.infrastructure.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;

import org.picketlink.common.util.Base64;

@Named
@ConversationScoped
public class ImagemController implements Serializable {

	private static final long serialVersionUID = 1L;

	public String byteToImage(byte[] bytes) throws IOException {
		if(bytes == null)
			return "";
		return new String(Base64.encodeBytes(bytes));
	}
}
