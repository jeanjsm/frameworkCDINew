package br.com.neainformatica.infrastructure.scheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * montar expressão on-line http://www.cronmaker.com/
 * 
 ############    Examples   #############
 Here are some full examples:

 Expression	Meaning
 0 0 12 * * ?	Fire at 12pm (noon) every day
 0 15 10 ? * *	Fire at 10:15am every day
 0 15 10 * * ?	Fire at 10:15am every day
 0 15 10 * * ? *	Fire at 10:15am every day
 0 15 10 * * ? 2005	Fire at 10:15am every day during the year 2005
 0 * 14 * * ?	Fire every minute starting at 2pm and ending at 2:59pm, every day
 0 0/5 14 * * ?	Fire every 5 minutes starting at 2pm and ending at 2:55pm, every day
 0 0/5 14,18 * * ?	Fire every 5 minutes starting at 2pm and ending at 2:55pm, AND fire every 5 minutes starting at 6pm and ending at 6:55pm, every day
 0 0-5 14 * * ?	Fire every minute starting at 2pm and ending at 2:05pm, every day
 0 10,44 14 ? 3 WED	Fire at 2:10pm and at 2:44pm every Wednesday in the month of March.
 0 15 10 ? * MON-FRI	Fire at 10:15am every Monday, Tuesday, Wednesday, Thursday and Friday
 0 15 10 15 * ?	Fire at 10:15am on the 15th day of every month
 0 15 10 L * ?	Fire at 10:15am on the last day of every month
 0 15 10 ? * 6L	Fire at 10:15am on the last Friday of every month
 0 15 10 ? * 6L	Fire at 10:15am on the last Friday of every month
 0 15 10 ? * 6L 2002-2005	Fire at 10:15am on every last friday of every month during the years 2002, 2003, 2004 and 2005
 0 15 10 ? * 6#3	Fire at 10:15am on the third Friday of every month
 0 0 12 1/5 * ?	Fire at 12pm (noon) every 5 days every month, starting on the first day of the month.
 0 11 11 11 11 ?	Fire every November 11th at 11:11am.
 Pay attention to the effects of '?' and '*' in the day-of-week and day-of-month fields!
 */

public class ExpressaoCron implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	public static String TODO_DIA_4_HORAS = "0 0 4 * * ?";
	public static String TODO_DIA_4_10_HORAS = "0 10 4 * * ?";
	public static String TODO_DIA_4_20_HORAS = "0 20 4 * * ?";
	public static String TODO_DIA_4_30_HORAS = "0 30 4 * * ?";
	public static String TODO_DIA_4_40_HORAS = "0 40 4 * * ?";
	public static String TODO_DIA_4_50_HORAS = "0 50 4 * * ?";
	public static String TODO_DIA_5_HORAS = "0 0 5 * * ?";
	public static String TODO_DIA_5_10_HORAS = "0 10 5 * * ?";
	public static String TODO_DIA_7_HORAS = "0 0 7 * * ?";
	public static String TODO_DIA_MEIO_DIA = "0 0 12 * * ?";
	public static String TODO_DIA_MEIA_NOITE = "0 0 0 1/1 * ? *";
	public static String TODO_DIA_20_HORAS = "0 0 20 1/1 * ? *";
	public static String TODO_DIA_21_HORAS = "0 0 21 1/1 * ? *";
	public static String TODO_DIA_22_HORAS = "0 0 22 1/1 * ? *";
	
	public static String A_CADA_UM_MINUTO = "0 0/1 * * * ?";
	public static String A_CADA_TRES_MINUTOS = "0 0/3 * * * ?";
	public static String A_CADA_CINCO_MINUTOS = "0 0/5 * * * ?";
	public static String A_CADA_DEZ_SEGUNDOS = "*/10 * * * * ?";
	public static String A_CADA_DEZ_MINUTOS = "0 0/10 * * * ?";	
	public static String A_CADA_TRINTA_MINUTOS = "0 0/30 * * * ?";
	
	public static String A_CADA_HORA = "0 0/60 * * * ?";
	public static String A_CADA_DUAS_HORAS = "0 0 0/2 * * ? *";
	public static String A_CADA_TRES_HORAS = "0 0 0/3 * * ? *";
	public static String A_CADA_QUATRO_HORAS = "0 0 0/4 * * ? *";
	public static String A_CADA_SEIS_HORAS = "0 0 0/6 * * ? *";	
	public static String A_CADA_DOZE_HORAS = "0 0 0/12 * * ? *";
	
	
	private static final String TODOS = "Todos";
	private static final String ULTIMO = "Último";
	
	private List<String> minutos;
	private List<String> horas;
	private List<String> diasMes;
	private List<String> meses;
	private List<String> diasSemana;
	private String ano;

	private void init() {
		this.minutos = new ArrayList<>();
		this.horas = new ArrayList<>();
		this.diasMes = new ArrayList<>();
		this.meses = new ArrayList<>();
		this.diasSemana = new ArrayList<>();
	}
	
	private void getExressaoFromCampo(StringBuilder resultado, List<String> lista) {
		Integer intervaloFim = null;
		
		if (lista.size() == 0 || lista.contains(TODOS)) {
			resultado.append("* ");
			return ;
		} else {
			for (int i = 0; i < lista.size(); i++) {
				if (i == 0) {
					resultado.append(lista.get(i));
				} else {
					if (!ULTIMO.equals(lista.get(i)) && Integer.parseInt(lista.get(i)) - Integer.parseInt(lista.get(i - 1)) == 1) {
						intervaloFim = i;
					} else {
						if (intervaloFim != null && !ULTIMO.equals(lista.get(i))) 
							resultado.append("-" + lista.get(intervaloFim) + "," + lista.get(i));
						else
							if (ULTIMO.equals(lista.get(i))) 
								resultado.append(",L");
							else
								resultado.append("," + lista.get(i));
						
						intervaloFim = null;
					}
				}
			}
			
			if (intervaloFim != null)
				resultado.append("-" + lista.get(intervaloFim));
		}
		
		resultado.append(" ");
	}
	
	private void setCampoFromExpressao(String expressao, List<String> lista) {
		Integer ini;
		Integer fim;
		
		lista.clear();
		
		if (expressao.equals("*"))
			lista.add(TODOS);
		else {
			for (String item: expressao.split(",")) {
				if (item.equals(ULTIMO))
					lista.add(ULTIMO);
				else {
					if (item.indexOf("-") > -1) {
						ini = Integer.parseInt(item.split("-")[0]);
						fim = Integer.parseInt(item.split("-")[1]);
						
						for (int i = ini; i <= fim; i++)
							lista.add("" + i);
					} else {
						lista.add(item);
					}
				}
			}
		}
	}
	
	private void setExpressao(String expressao) {
		if (expressao != null && expressao.trim().length() > 0 && expressao.split(" ").length >= 6) {
			setCampoFromExpressao(expressao.split(" ")[1], minutos);
			setCampoFromExpressao(expressao.split(" ")[2], horas);
			setCampoFromExpressao(expressao.split(" ")[3], diasMes);
			setCampoFromExpressao(expressao.split(" ")[4], meses);
			setCampoFromExpressao(expressao.split(" ")[5], diasSemana);
			
			if (expressao.split(" ").length == 7)
				ano = expressao.split(" ")[6];
		}
	}
	
	@Override
	public String toString() {
		return getExpressao();
	}
	
	public ExpressaoCron() {
		init();
	}
	
	public ExpressaoCron(String expressao) {
		this();
		setExpressao(expressao);
	}
	
	public String getExpressao() {
		StringBuilder resultado = new StringBuilder("* ");
		
		if (diasMes.size() > 0 && diasSemana.size() > 0)
			diasMes.clear();
		
		if (diasSemana.size() == 0)
			diasSemana.add("?");
		
		getExressaoFromCampo(resultado, minutos);
		getExressaoFromCampo(resultado, horas);
		getExressaoFromCampo(resultado, diasMes);
		getExressaoFromCampo(resultado, meses);
		getExressaoFromCampo(resultado, diasSemana);
		
		if (ano != null && ano.length() > 4)
			resultado.append(ano);
		
		return resultado.toString().trim();
	}
	
	public static List<String> listaSegundos() {
		List<String> resultado = new ArrayList<>();
		
		resultado.add("Todos");		
		for (int i = 0; i < 59; i++)
			resultado.add("" + i);
		
		return resultado;
	}
	
	public static List<String> listaMinutos() {
		List<String> resultado = new ArrayList<>();
		
		resultado.add("Todos");		
		for (int i = 0; i < 59; i++)
			resultado.add("" + i);
		
		return resultado;
	}
	
	public static List<String> listaHoras() {
		List<String> resultado = new ArrayList<>();
		
		resultado.add("Todos");		
		for (int i = 0; i < 23; i++)
			resultado.add("" + i);
		
		return resultado;
	}
	
	public static List<String> listaDiasMes() {
		List<String> resultado = new ArrayList<>();
		
		resultado.add("Todos");
		for (int i = 0; i < 31; i++)
			resultado.add("" + i);
		resultado.add("Último");
		
		return resultado;
	}
	
	public static List<String> listaMeses() {
		List<String> resultado = new ArrayList<>();
		resultado.add("Janeiro");
		resultado.add("Fevereiro");
		resultado.add("Março");
		resultado.add("Abril");
		resultado.add("Maio");
		resultado.add("Junho");
		resultado.add("Julho");
		resultado.add("Agosto");
		resultado.add("Setembro");
		resultado.add("Outubro");
		resultado.add("Novembro");
		resultado.add("Dezembro");
		return resultado;
	}
	
	public static List<String> listaDiasSemana() {
		List<String> resultado = new ArrayList<>();
		resultado.add("Domingo");
		resultado.add("Segunda-Feira");
		resultado.add("Terça-Feira");
		resultado.add("Quarta-Feira");
		resultado.add("Quinta-Feira");
		resultado.add("Sexta-Feira");
		resultado.add("Sábado");
		return resultado;
	}
	
	//Getters and Setters
	public List<String> getMinutos() {
		return minutos;
	}

	public void setMinutos(List<String> minutos) {
		this.minutos = minutos;
	}

	public List<String> getHoras() {
		return horas;
	}

	public void setHoras(List<String> horas) {
		this.horas = horas;
	}

	public List<String> getDiasMes() {
		return diasMes;
	}

	public void setDiasMes(List<String> diasMes) {
		this.diasMes = diasMes;
	}

	public List<String> getMeses() {
		return meses;
	}
	
	public void setMeses(List<String> meses) {
		this.meses = meses;
	}

	public List<String> getDiasSemana() {
		return diasSemana;
	}

	public void setDiasSemana(List<String> diasSemana) {
		this.diasSemana = diasSemana;
	}

	public String getAno() {
		return ano;
	}
	
	public void setAno(String ano) {
		this.ano = ano;
	}

}
