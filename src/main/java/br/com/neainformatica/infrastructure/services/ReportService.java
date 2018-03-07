package br.com.neainformatica.infrastructure.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.picketlink.credential.DefaultLoginCredentials;

import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.enumeration.EnumRelatorio;
import br.com.neainformatica.infrastructure.enumeration.EnumRelatorioExportacao;
import br.com.neainformatica.infrastructure.interfaces.EnumRelatorioInterface;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.type.OrientationEnum;
import net.sf.jasperreports.engine.util.JRLoader;

public class ReportService implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String REPORT_EXT = ".jasper";
	public static final String REPORT_DIR = "REPORT_DIR";
	public static final String SUBREPORT_DIR = "SUBREPORT_DIR";
	public static final String SUBREPORT_JAR = "SUBREPORT_JAR";
	public static final String PAGE_HEADER = "PAGE_HEADER";
	public static final String PAGE_FOOTER = "PAGE_FOOTER";

	public static final String ESTADO_PREFEITURA = "ESTADO_PREFEITURA";
	public static final String CIDADE_PREFEITURA = "CIDADE_PREFEITURA";
	public static final String BRASAO_PREFEITURA = "BRASAO_PREFEITURA";
	public static final String NOME_PREFEITURA = "NOME_PREFEITURA";
	public static final String NOME_SECRETARIA = "NOME_SECRETARIA";
	public static final String NOME_DIVISAO = "NOME_DIVISAO";
	public static final String NOME_USUARIO = "NOME_USUARIO";
	public static final String NOME_SISTEMA = "NOME_SISTEMA";
	public static final String LOGO_SISTEMA = "LOGO_SISTEMA";

	public static final String TITULO = "TITULO";
	public static final String SUBTITULO = "SUBTITULO";
	public static final String SITE = "SITE";
	public static final String VERSAO = "VERSAO";

	@Inject
	DefaultLoginCredentials credentials;

	@Inject
	InfrastructureService infrastructureService;

	@Inject
	private NaSessionController naSessionController;

	/**
	 * Imprime o relatório na saída do faces servlet
	 * 
	 * @param relatorio
	 *            RelatorioEnum - Relatório que será impresso
	 * @param listDados
	 *            List - Lista de dados
	 * @return void - Não há retorno
	 * @throws IOException
	 * @throws JRException
	 */
	@SuppressWarnings("rawtypes")
	public void imprimir(EnumRelatorioInterface relatorio, List listDados) throws JRException, IOException {
		print(relatorio, listDados, relatorio.toString(), new HashMap<String, Object>(), EnumRelatorioExportacao.PDF);
	}

	/**
	 * Imprime o relatório na saída do faces servlet
	 * 
	 * @param relatorio
	 *            RelatorioEnum - Relatório que será impresso
	 * @param listDados
	 *            List - Lista de dados
	 * @param nome
	 *            String - Nome do arquivo de saída
	 * @return void - Não há retorno
	 */
	@SuppressWarnings("rawtypes")
	public void imprimir(EnumRelatorioInterface relatorio, List listDados, String nome) throws JRException, IOException {
		print(relatorio, listDados, nome, new HashMap<String, Object>(), EnumRelatorioExportacao.PDF);
	}

	/**
	 * Imprime o relatório na saída do faces servlet
	 * 
	 * @param relatorio
	 *            RelatorioEnum - Relatório que será impresso
	 * @param listDados
	 *            List - Lista de dados
	 * @param nome
	 *            String - Nome do arquivo de saída
	 * @param parametros
	 *            HashMap<String, Object> - Parâmetros adicionais
	 * @param exportacao
	 *            RelatorioExportacaoEnum - Tipo do arquivo de saída
	 * @return void - Não há retorno
	 */
	@SuppressWarnings("rawtypes")
	public void imprimir(EnumRelatorioInterface relatorio, List listDados, String nome, HashMap<String, Object> parametros, EnumRelatorioExportacao exportacao)
			throws JRException, IOException {
		print(relatorio, listDados, nome, parametros, exportacao);
	}

	@SuppressWarnings("rawtypes")
	public void imprimir(byte[] arquivojasper, String nomeDownload, List listDados) throws JRException, IOException {
		print(arquivojasper, listDados, nomeDownload, new HashMap<String, Object>(), EnumRelatorioExportacao.PDF);
	}

	@SuppressWarnings("rawtypes")
	public void imprimir(byte[] arquivojasper, String nomeDownload, List listDados, EnumRelatorioExportacao formatoExportacao) throws JRException, IOException {
		print(arquivojasper, listDados, nomeDownload, new HashMap<String, Object>(), formatoExportacao);
	}

	@SuppressWarnings("rawtypes")
	private String print(byte[] arquivoJasper, List listDados, String nomeDownload, HashMap<String, Object> parametros, EnumRelatorioExportacao exportacao)
			throws JRException, IOException {
		JRBeanCollectionDataSource dataSource;
		HttpServletResponse response;
		ServletOutputStream output;
		FacesContext context;
		JasperReport report;
		JasperPrint print;

		dataSource = new JRBeanCollectionDataSource(listDados);

		parametros = getReportParametros(null, parametros);

		report = (JasperReport) JRLoader.loadObject(new ByteArrayInputStream(arquivoJasper));

		print = JasperFillManager.fillReport(report, parametros, dataSource);
		context = FacesContext.getCurrentInstance();
		response = (HttpServletResponse) context.getExternalContext().getResponse();
		defineRetorno(exportacao, nomeDownload, response);

		output = response.getOutputStream();
		output.write(export(print, exportacao));
		output.flush();
		output.close();

		dataSource = null;
		report = null;
		print = null;
		context.responseComplete();

		return "";
	}

	@SuppressWarnings({ "rawtypes" })
	private String print(EnumRelatorioInterface relatorio, List listDados, String nome, HashMap<String, Object> parametros, EnumRelatorioExportacao exportacao)
			throws JRException, IOException {
		JRBeanCollectionDataSource dataSource;
		HttpServletResponse response;
		ServletOutputStream output;
		FacesContext context;
		JasperReport report;
		JasperPrint print;

		dataSource = new JRBeanCollectionDataSource(listDados);

		parametros = getReportParametros(relatorio, parametros);

		report = infrastructureService.getJasperReport(relatorio);

		if (report.getOrientationValue() == OrientationEnum.PORTRAIT) {
			parametros.put(PAGE_HEADER, infrastructureService.getJasperReport(EnumRelatorio.PAGE_HEADER_RETRATO));
			parametros.put(PAGE_FOOTER, infrastructureService.getJasperReport(EnumRelatorio.PAGE_FOOTER_RETRATO));
		} else {
			parametros.put(PAGE_HEADER, infrastructureService.getJasperReport(EnumRelatorio.PAGE_HEADER_PAISAGEM));
			parametros.put(PAGE_FOOTER, infrastructureService.getJasperReport(EnumRelatorio.PAGE_FOOTER_PAISAGEM));
		}

		print = JasperFillManager.fillReport(report, parametros, dataSource);
		context = FacesContext.getCurrentInstance();
		response = (HttpServletResponse) context.getExternalContext().getResponse();
		defineRetorno(exportacao, nome, response);

		output = response.getOutputStream();
		output.write(export(print, exportacao));
		output.flush();
		output.close();

		dataSource = null;
		report = null;
		print = null;
		context.responseComplete();

		return "";
	}

	@SuppressWarnings({ "rawtypes" })
	public byte[] geraPdf(EnumRelatorioInterface relatorio, List listDados, String nome, HashMap<String, Object> parametros, EnumRelatorioExportacao exportacao)
			throws JRException, IOException {
		JRBeanCollectionDataSource dataSource;
		JasperReport report;
		JasperPrint print;

		dataSource = new JRBeanCollectionDataSource(listDados);

		parametros = getReportParametros(relatorio, parametros);

		report = infrastructureService.getJasperReport(relatorio);

		if (report.getOrientationValue() == OrientationEnum.PORTRAIT) {
			parametros.put(PAGE_HEADER, infrastructureService.getJasperReport(EnumRelatorio.PAGE_HEADER_RETRATO));
			parametros.put(PAGE_FOOTER, infrastructureService.getJasperReport(EnumRelatorio.PAGE_FOOTER_RETRATO));
		} else {
			parametros.put(PAGE_HEADER, infrastructureService.getJasperReport(EnumRelatorio.PAGE_HEADER_PAISAGEM));
			parametros.put(PAGE_FOOTER, infrastructureService.getJasperReport(EnumRelatorio.PAGE_FOOTER_PAISAGEM));
		}

		print = JasperFillManager.fillReport(report, parametros, dataSource);
		byte[] result = export(print, exportacao);
		dataSource = null;
		report = null;
		print = null;

		return result;
	}
	
	private void defineRetorno(EnumRelatorioExportacao exportacao, String nome, HttpServletResponse response) throws IOException {
		switch (exportacao) {
		case PDF:
			nome += "." + EnumRelatorioExportacao.PDF;
			response.setContentType("application/pdf");
			break;
		case XLS:
			nome += "." + EnumRelatorioExportacao.XLS;
			response.setContentType("application/xls");
			break;
		case RTF:
			nome += "." + EnumRelatorioExportacao.RTF;
			response.setContentType("application/rtf");
			break;
		case HTML:
			nome += "." + EnumRelatorioExportacao.HTML;
			response.setContentType("application/html");
			break;
		case CSV:
			nome += "." + EnumRelatorioExportacao.CSV;
			response.setContentType("application/csv");
			break;
		case XML:
			nome += "." + EnumRelatorioExportacao.XML;
			response.setContentType("application/xml");
			break;
		case ODF:
			nome += "." + EnumRelatorioExportacao.ODF;
			response.setContentType("application/vnd.oasis.opendocument.text");
			break;
		case TXT:
			nome += "." + EnumRelatorioExportacao.TXT;
			response.setContentType("application/text");
			break;
		default:
			break;
		}

		response.addHeader("Content-Disposition:", "attachment; filename=" + nome);
	}

	private byte[] export(JasperPrint jasperPrint, EnumRelatorioExportacao exportacao) throws JRException, IOException {
		byte[] memoria = null;
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		// JRExporter<?, ?, ?, ?> exporter = null;
		JRAbstractExporter<?, ?, ?, ?> exporter = null;
		// JRAbstractExporter<ReportExportConfiguration, ExporterConfiguration,
		// ExporterOutput, JRExporterContext> exporter = null;

		switch (exportacao) {
		case PDF:
			memoria = JasperExportManager.exportReportToPdf(jasperPrint);
			break;
		case XLS:
			exporter = new JRXlsExporter();
			// exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,
			// Boolean.TRUE);
			// exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
			// Boolean.TRUE);
			// exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND,
			// Boolean.FALSE);
			// exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN,
			// Boolean.TRUE);
			break;
		case RTF:
			exporter = new JRRtfExporter();
			// exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING,
			// "UTF-8");
			break;
		// case HTML:
		// exporter = new JRHtmlExporter();
		// //
		// exporter.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,
		// // false);
		// // exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING,
		// // "UTF-8");
		// break;
		case CSV:
			exporter = new JRCsvExporter();
			// exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING,
			// "ISO-8859-1");
			break;
		case XML:
			exporter = new JRXmlExporter();
			break;
		case ODF:
			exporter = new JROdtExporter();
			// exporter.setParameter(JRExporterParameter.OFFSET_X, new
			// Integer(0));
			// exporter.setParameter(JRExporterParameter.OFFSET_Y, new
			// Integer(0));
			break;
		case TXT:
			exporter = new JRTextExporter();
			break;
		default:
			break;
		}

		if (exporter != null) {
			// exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, output);
			// exporter.setParameter(JRExporterParameter.JASPER_PRINT,
			// jasperPrint);
			exporter.exportReport();
			memoria = output.toByteArray();
		}

		output.close();
		output = null;

		return memoria;
	}

	/**
	 * Busca os parâmetros default e ja substitui os parâmetros personalizados
	 * 
	 * @param relatorio
	 * @param parametrosPersonalizados
	 * @return
	 */
	private HashMap<String, Object> getReportParametros(EnumRelatorioInterface relatorio, HashMap<String, Object> parametrosPersonalizados) {
		if (parametrosPersonalizados == null)
			parametrosPersonalizados = new HashMap<String, Object>();

		HashMap<String, Object> parametrosRelatorio = new HashMap<String, Object>();
		parametrosRelatorio.putAll(infrastructureService.getReportListaParametro());

		for (String pp : parametrosPersonalizados.keySet()) {
			if (parametrosRelatorio.get(pp) == null)
				parametrosRelatorio.put(pp, parametrosPersonalizados.get(pp));
		}

		if (relatorio != null)
			parametrosRelatorio.put("TITULO", relatorio.getTitulo());

		parametrosRelatorio.put("DATA_HORA_IMPRESSAO", new Date());

		if (parametrosPersonalizados.get(REPORT_DIR) != null)
			parametrosRelatorio.put(REPORT_DIR, parametrosPersonalizados.get(REPORT_DIR));

		if (parametrosPersonalizados.get(SUBREPORT_DIR) != null)
			parametrosRelatorio.put(SUBREPORT_DIR, parametrosPersonalizados.get(SUBREPORT_DIR));

		if (parametrosPersonalizados.get(PAGE_HEADER) != null)
			parametrosRelatorio.put(PAGE_HEADER, parametrosPersonalizados.get(PAGE_HEADER));

		if (parametrosPersonalizados.get(PAGE_FOOTER) != null)
			parametrosRelatorio.put(PAGE_FOOTER, parametrosPersonalizados.get(PAGE_FOOTER));

		if (parametrosPersonalizados.get(ESTADO_PREFEITURA) != null)
			parametrosRelatorio.put(ESTADO_PREFEITURA, parametrosPersonalizados.get(ESTADO_PREFEITURA));

		if (parametrosPersonalizados.get(CIDADE_PREFEITURA) != null)
			parametrosRelatorio.put(CIDADE_PREFEITURA, parametrosPersonalizados.get(CIDADE_PREFEITURA));

		if (parametrosPersonalizados.get(BRASAO_PREFEITURA) != null)
			parametrosRelatorio.put(BRASAO_PREFEITURA, parametrosPersonalizados.get(BRASAO_PREFEITURA));

		if (parametrosPersonalizados.get(NOME_PREFEITURA) != null)
			parametrosRelatorio.put(NOME_PREFEITURA, parametrosPersonalizados.get(NOME_PREFEITURA));

		if (parametrosPersonalizados.get(NOME_SECRETARIA) != null)
			parametrosRelatorio.put(NOME_SECRETARIA, parametrosPersonalizados.get(NOME_SECRETARIA));

		if (parametrosPersonalizados.get(NOME_DIVISAO) != null)
			parametrosRelatorio.put(NOME_DIVISAO, parametrosPersonalizados.get(NOME_DIVISAO));

		if (parametrosPersonalizados.get(NOME_USUARIO) != null)
			parametrosRelatorio.put(NOME_USUARIO, parametrosPersonalizados.get(NOME_USUARIO));
		else if (naSessionController != null && naSessionController.getNaUsuario() != null)
			parametrosRelatorio.put(NOME_USUARIO, naSessionController.getNaUsuario().getNome());

		if (parametrosPersonalizados.get(NOME_SISTEMA) != null)
			parametrosRelatorio.put(NOME_SISTEMA, parametrosPersonalizados.get(NOME_SISTEMA));

		if (parametrosPersonalizados.get(LOGO_SISTEMA) != null)
			parametrosRelatorio.put(LOGO_SISTEMA, parametrosPersonalizados.get(LOGO_SISTEMA));

		if (parametrosPersonalizados.get(TITULO) != null)
			parametrosRelatorio.put(TITULO, parametrosPersonalizados.get(TITULO));

		if (parametrosPersonalizados.get(SUBTITULO) != null)
			parametrosRelatorio.put(SUBTITULO, parametrosPersonalizados.get(SUBTITULO));

		if (parametrosPersonalizados.get(SITE) != null)
			parametrosRelatorio.put(SITE, parametrosPersonalizados.get(SITE));

		if (parametrosPersonalizados.get(VERSAO) != null)
			parametrosRelatorio.put(VERSAO, parametrosPersonalizados.get(VERSAO));

		return parametrosRelatorio;
	}

}