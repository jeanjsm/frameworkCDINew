package br.com.neainformatica.infrastructure.interfaces;

public interface EnumTarefaInterface {

	/**
	 * Este campo deve ser um identificador único, não pode ser repetir para
	 * outros sistemas, então vamos adotar o seguinte padrão:
	 * 
	 * 
	 * S15_T1 -> Sistema: 15 Tarefa: 1; S15_T2_VALIDAR -> Sistema: 15 Tarefa: 2;
	 * 
	 * @return
	 */
	public String getIdentificador();

	public String getNome();

	public String getCronPadrao();

}