package br.com.neainformatica.infrastructure.scheduler;

import javax.enterprise.util.AnnotationLiteral;

import br.com.neainformatica.infrastructure.interfaces.EnumTarefaInterface;

@SuppressWarnings("all")
public class QuartzQualifier extends AnnotationLiteral<TarefaQuartzQualifier> implements TarefaQuartzQualifier {
	private static final long serialVersionUID = 1L;
//	private EnumTarefaInterface tarefa;
//	
//	public QuartzQualifier(EnumTarefaInterface tarefa) {
//		this.tarefa = tarefa;
//	}
	private String tarefa;
	
	public QuartzQualifier(String tarefa) {
		this.tarefa = tarefa;
	}
	
	
	@Override
	public String value() {
		//return tarefa.getIdentificador();
		return tarefa;
	}
	
}