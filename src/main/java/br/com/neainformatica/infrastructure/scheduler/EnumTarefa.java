package br.com.neainformatica.infrastructure.scheduler;

import br.com.neainformatica.infrastructure.interfaces.EnumTarefaInterface;

public enum EnumTarefa implements EnumTarefaInterface {

	JOB_VALIDA_LINKS("S1012_JOB_VALIDA_LINKS", "Valida links ativos", ExpressaoCron.A_CADA_UM_MINUTO);

	private String id;
	private String nome;
	private String cron;

	private EnumTarefa(String id, String nome, String cron) {
		this.id = id;
		this.nome = nome;
		this.cron = cron;
	}

	@Override
	public String getIdentificador() {
		return this.id;
	}

	@Override
	public String getNome() {
		return this.nome;
	}

	@Override
	public String getCronPadrao() {
		return this.cron;
	}

}
