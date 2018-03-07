package br.com.neainformatica.infrastructure.exception;

public class NeaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String menssagem;

	public NeaException(String mensagem) {
		this.menssagem = mensagem;
	}
	@Override
	public String getMessage() {
		return this.menssagem;
	}
}
