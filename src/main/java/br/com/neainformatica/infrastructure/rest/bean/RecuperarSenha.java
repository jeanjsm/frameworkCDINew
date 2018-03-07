package br.com.neainformatica.infrastructure.rest.bean;

import java.util.Date;

import br.com.neainformatica.infrastructure.enumeration.EnumFormatDate;
import br.com.neainformatica.infrastructure.tools.NeaDate;
import br.com.neainformatica.infrastructure.tools.Tools;

public class RecuperarSenha {

	private String cpf;
	private Date dataSolicitacao;
	private String chave;

	public boolean validaChave() {

		if (chave == null || chave.trim().equals(""))
			return false;

		String chaveCalculda = Tools.geraMD5("NeA" + cpf + NeaDate.formatarData(dataSolicitacao, EnumFormatDate.DDMMYYYHHMMSS)+"NeA");

		if (chave.equals(chaveCalculda))
			return true;

		return false;

	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}

	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}

	public String getChave() {
		return chave;
	}

	public void setChave(String chave) {
		this.chave = chave;
	}

	@Override
	public String toString() {
		return "RecuperarSenha [cpf=" + cpf + ", dataSolicitacao=" + dataSolicitacao + ", chave=" + chave + "]";
	}

}
