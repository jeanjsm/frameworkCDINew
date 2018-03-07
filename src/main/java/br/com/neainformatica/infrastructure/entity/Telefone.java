package br.com.neainformatica.infrastructure.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Telefone {

	@Column(name = "fone_ddd", length = 2)
	private String ddd;

	@Column(name = "fone_numero", length = 11)
	private String numero;

	@Column(name = "fone_ramal", length = 10)
	private String ramal;

	public String getDdd() {
		return ddd;
	}

	public void setDdd(String ddd) {
		this.ddd = ddd;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getRamal() {
		return ramal;
	}

	public void setRamal(String ramal) {
		this.ramal = ramal;
	}

}
