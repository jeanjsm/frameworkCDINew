package br.com.neainformatica.infrastructure.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.com.neainformatica.infrastructure.enumeration.EnumFormatDate;

@Named
public class NeaDate {
	
	private static final Log log = LogFactory.getLog(NeaDate.class);

	public static String mesAnoData(Date data) {

		SimpleDateFormat formatador = new SimpleDateFormat("MM");
		SimpleDateFormat formatador2 = new SimpleDateFormat("yyyy");
		return formatador.format(data) + "/" + formatador2.format(data);
	}

	public static Date somaMesData(Date data, Integer qtdMeses){
		Calendar date = Calendar.getInstance();
		date.setTime(data);
		date.add(Calendar.MONTH, qtdMeses);
		
		data= date.getTime();
		
		return data;
	}

	public static Date somaHoraData(Date data, Integer qtdHoras){
		Calendar date = Calendar.getInstance();
		date.setTime(data);
		date.add(Calendar.HOUR_OF_DAY, qtdHoras);

		data= date.getTime();

		return data;
	}

	public static Date somaSegundosData(Date data, Integer segundos){
		Calendar date = Calendar.getInstance();
		date.setTime(data);
		date.add(Calendar.SECOND, segundos);

		data= date.getTime();

		return data;
	}

	public static boolean menor(Date dataReferencia, Date dataComparacao) {
		Calendar dtReferencia = Calendar.getInstance();
		dtReferencia.setTime(dataReferencia);	
		
		Calendar dtComparacao = Calendar.getInstance();
		dtComparacao.setTime(dataComparacao);
		
		Boolean retorno;
		
		if (dtReferencia.before(dtComparacao)) {
			retorno = true;
		} else {
			retorno = false;
		}
		return retorno;
	}

	public static int getDiferencaDeDias(Date dataInicial, Date dataFinal) {
		// setando os quebrados da data inicial
		Calendar dtInicial = Calendar.getInstance();
		dtInicial.setTime(dataInicial);
		dtInicial.set(Calendar.AM_PM, Calendar.AM);
		dtInicial.set(Calendar.HOUR, 0);
		dtInicial.set(Calendar.MINUTE, 0);
		dtInicial.set(Calendar.SECOND, 0);
		dtInicial.set(Calendar.MILLISECOND, 0);

		// setando os quebrados da data final
		Calendar dtFinal = Calendar.getInstance();
		dtFinal.setTime(dataFinal);
		dtFinal.set(Calendar.AM_PM, Calendar.AM);
		dtFinal.set(Calendar.HOUR, 0);
		dtFinal.set(Calendar.MINUTE, 0);
		dtFinal.set(Calendar.SECOND, 0);
		dtFinal.set(Calendar.MILLISECOND, 0);

		long diferenca = (dtFinal.getTimeInMillis() - dtInicial.getTimeInMillis());
		return (int) (diferenca / (24 * 60 * 60 * 1000));
	}
	
	/**
	 * Retorna a diferença das datas passadas como parametros em minutos
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public static int getDiferencaDeMinutos(Date dataInicial, Date dataFinal) {
		Calendar dtInicial = Calendar.getInstance();
		dtInicial.setTime(dataInicial);
		
		Calendar dtFinal = Calendar.getInstance();
		dtFinal.setTime(dataFinal);
		
		long diferenca = (dtFinal.getTimeInMillis() - dtInicial.getTimeInMillis());
		return (int) (diferenca / (60 * 1000));
	}
	
	

	/**
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return Date
	 * 
	 *         Monta uma data com os parametros encaminhados.
	 * 
	 */
	public static Date criaData(int year, int month, int day) {
		Date data = new Date();
		try {
			Calendar cl = Calendar.getInstance();
			cl.set(year, (month - 1), day);

			data = zeraHoraData(cl.getTime());
		} catch (Exception e) {
			log.debug("Erro ao converter data ANO: " + year + " MÊS: " + month + " DIA: " + day);
			e.printStackTrace();
		}

		return data;
	}
	
	/**
	 * Cria uma data de acordo com os parâmetros encaminhados <br/>
	 * Este possibilita passar as horas e minutos também
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @return
	 */
	public static Date criaData(int year, int month, int day, int hour, int minute) {
		Date data = new Date();
		try {
			Calendar cl = Calendar.getInstance();
			cl.set(year, (month - 1), day);
						
			cl.set(Calendar.HOUR, hour);
			cl.set(Calendar.MINUTE, minute);
			cl.set(Calendar.SECOND, 0);
			cl.set(Calendar.MILLISECOND, 0);
			
			data = cl.getTime();

			
		} catch (Exception e) {
			log.debug("Erro ao converter data ANO: " + year + " MÊS: " + month + " DIA: " + day+ " hora: "+ hour + " minuto: "+ minute);
			e.printStackTrace();
			return null;
		}

		return data;
	}

	public static Date somaDiasData(Date data, int qtdeDias) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.DATE, qtdeDias);

		return c.getTime();
	}
	
	public static Date subtraiDiasData(Date data, int qtdeDias) {
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		c.add(Calendar.DAY_OF_MONTH, -qtdeDias);

		return c.getTime();
	}
	
	public static Date subtraiHoraData(Date data, Integer qtdHoras){
		Calendar date = Calendar.getInstance();
		date.setTime(data);
		date.add(Calendar.HOUR_OF_DAY, -qtdHoras);

		data= date.getTime();

		return data;
	}

	public static Date primeiroDiaMes(Date data) {
		Calendar calendario = GregorianCalendar.getInstance();

		calendario.setTime(data);
		calendario.set(Calendar.DAY_OF_MONTH, calendario.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendario.set(Calendar.HOUR_OF_DAY, 0);
		calendario.set(Calendar.MINUTE, 0);
		calendario.set(Calendar.SECOND, 0);
		calendario.set(Calendar.MILLISECOND, 0);

		return calendario.getTime();
	}

	public static Date ultimoDiaMes(Date data) {
		Calendar calendario = GregorianCalendar.getInstance();
		
		calendario.setTime(data);
		calendario.set(Calendar.DAY_OF_MONTH, calendario.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendario.set(Calendar.HOUR_OF_DAY, 0);
		calendario.set(Calendar.MINUTE, 0);
		calendario.set(Calendar.SECOND, 0);
		calendario.set(Calendar.MILLISECOND, 0);
		
		return calendario.getTime();
	}

	public static Date zeraHoraData(Date data) {

		Calendar c = Calendar.getInstance(new Locale("pt_BR"));
		c.setTime(data);
		c.set(Calendar.AM_PM, Calendar.AM);
		c.set(Calendar.HOUR, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);

		data = c.getTime();

		return c.getTime();
	}

	/**
	 * Setar a ultima hora do dia na data passada como parametros exemplo
	 * DATA_PARAMETRO 23:59:59
	 * 
	 * @param data
	 */
	public static Date ultimaHoraData(Date data) {

		Calendar c = Calendar.getInstance(new Locale("pt_BR"));
		c.setTime(data);
		c.set(Calendar.AM_PM, Calendar.PM);
		c.set(Calendar.HOUR_OF_DAY, c.getActualMaximum(Calendar.HOUR_OF_DAY));
		c.set(Calendar.MINUTE, (c.getActualMaximum(Calendar.MINUTE)) - 1);

		data = c.getTime();

		return c.getTime();
	}

	/**
	 * Metodo usado para conversao de datas de acordo com o tipo de parametro
	 * passado.
	 * 
	 * @param data
	 *            - parametro referente a data que sera formatada.
	 * @param tipoFormatacao
	 *            - parametro que referente a mascara que sera utilizada para
	 *            formatação.
	 * @return
	 */

	public static String formatarData(Date data, EnumFormatDate tipoFormatacao) {
		SimpleDateFormat formatador = new SimpleDateFormat(tipoFormatacao.getMascara());
		return formatador.format(data);

	}

	/**
	 * Metodo utilizado para retornar o dia da semana (1 - Domingo, ... 7 -
	 * Sabado)
	 */
	public static Integer obterDiaDaSemana(Date data) {
		Calendar calendario = new GregorianCalendar();
		calendario.setTime(data);
		return calendario.get(Calendar.DAY_OF_WEEK);
	}
	/**
	 * Metodo utilizado para incrementar uma data de acordo com a quantidade de dias passados por parametro
	 */
	public static Date incrementaDataPorDias(Date data, int quantidadeDeDias) {
		Calendar calendario = Calendar.getInstance();
		calendario.setTime(data);
		calendario.add(Calendar.DAY_OF_MONTH, quantidadeDeDias);
		return calendario.getTime();
	}
	
	public static Integer diaData(Date data) {
		SimpleDateFormat formatador = new SimpleDateFormat("dd");
		return Integer.parseInt(formatador.format(data));
	}

	public static Integer mesData(Date data) {

		SimpleDateFormat formatador = new SimpleDateFormat("MM");
		return Integer.parseInt(formatador.format(data));
	}

	public static Integer anoData(Date data) {

		SimpleDateFormat formatador = new SimpleDateFormat("yyyy");
		return Integer.parseInt(formatador.format(data));
	}
	
	public static Integer horaData(Date data) {
		
		SimpleDateFormat formatador = new SimpleDateFormat("HH");
		return Integer.parseInt(formatador.format(data));
	}
	
	public static Integer minutoData(Date data) {
		
		SimpleDateFormat formatador = new SimpleDateFormat("mm");
		return Integer.parseInt(formatador.format(data));
	}
	
	public static Integer segundosData(Date data) {
		
		SimpleDateFormat formatador = new SimpleDateFormat("ss");
		return Integer.parseInt(formatador.format(data));
	}
	
	
	public static Date stringToDate(String data, EnumFormatDate tipoFormatacao) {
		SimpleDateFormat format = new SimpleDateFormat(tipoFormatacao.getMascara());
		try {
			return  format.parse(data);
		} catch (ParseException e) {
			return null;
		}
	}
	
	/**
	 * Retorna a diferença entre datas no formato: 2 dias 2 horas e 2 minutos
	 * @param dataInicio
	 * @param dataFim
	 * @return
	 */
	public static String diferencaEntreDatas(Date dataInicio, Date dataFim) {

		int tempoDia = 1000 * 60 * 60 * 24;
		int tempoHora = 1000 * 60 * 60;
		int tempoMinutos = 1000 * 60;

		String retorno = "";

		Calendar dtIni = Calendar.getInstance();
		dtIni.setTime(dataInicio);

		Calendar dtFim = Calendar.getInstance();
		dtFim.setTime(dataFim);

		long diferenca = dtFim.getTimeInMillis() - dtIni.getTimeInMillis();

		if (diferenca >= tempoDia) {
			int dif = (int) (diferenca / tempoDia);
			if (dif == 1)
				retorno += dif + "  dia ";
			else
				retorno += dif + "  dias ";
			diferenca = diferenca - ( (long) dif * (long) tempoDia);
		}

		if (diferenca >= tempoHora) {
			int dif = (int) (diferenca / tempoHora);

			if (dif == 1)
				retorno += dif + "  hora ";
			else
				retorno += dif + "  horas ";
			diferenca = diferenca - (dif * tempoHora);
		}
		if (diferenca >= tempoMinutos) {
			int dif = (int) (diferenca / tempoMinutos);
			if (dif == 1)
				retorno += dif + "  minuto ";
			else
				retorno += dif + "  minutos ";
			diferenca = diferenca - (dif * tempoMinutos);
		}

		if (retorno.equals(""))
			retorno = "1 minuto";

		return retorno;

	}
	

	/**
	 * Favor utilizar o método stringToDate(String data, EnumFormatDate tipoFormatacao) desta mesma classe
	 * 
	 * @param dataString
	 * @return
	 */
	@Deprecated
	public Date stringToDate(String dataString) {

		if (dataString == null || dataString.trim().equals(""))
			return null;

		dataString = dataString.trim();

		DateFormat format = new SimpleDateFormat("dd/MM/yy");

		try {
			return (Date) format.parse(dataString);
		} catch (ParseException e) {

			return null;

		}

	}
	
	/**
	 * Favor utilizar o método formatarData(String data, EnumFormatDate tipoFormatacao) desta mesma classe
	 * 
	 * @param dataString
	 * @return
	 */
	@Deprecated
	public static String obterDataHora(Date data) {

		if (data == null)
			return "";

		SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return formatador.format(data);
	}

}
