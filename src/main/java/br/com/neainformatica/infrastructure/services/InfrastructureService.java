package br.com.neainformatica.infrastructure.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.NaClienteRepository;
import br.com.neainformatica.infrastructure.dao.NaSistemaRepository;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaParametro;
import br.com.neainformatica.infrastructure.entity.NaParametroValor;
import br.com.neainformatica.infrastructure.entity.NaPermissao;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.interfaces.EnumParametroInterface;
import br.com.neainformatica.infrastructure.interfaces.EnumRelatorioInterface;
import br.com.neainformatica.infrastructure.tools.ToolsPackage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@ApplicationScoped
public class InfrastructureService implements Serializable {

	private static final long serialVersionUID = 1L;
	private HashMap<String, Object> reportListaParametro;
	private HashMap<EnumRelatorioInterface, JasperReport> reportLista;
	private List<NaPermissao> permissoes;

	@Inject
	private NaClienteRepository daoCliente;

	@Inject
	private NaSistemaRepository daoSistema;

	@Inject
	private InfrastructureController infrastructureController;

	@Inject
	private NaParametroService parametroService;

	@Inject
	private NaAgendamentoService naAgendamentoService;

	@Inject
	private ServletContext servletContext;
	
	@Inject
	protected Log log;

	@PostConstruct
	public void init() {
		try {
			reportLista = new HashMap<EnumRelatorioInterface, JasperReport>();
			reportListaParametro = new HashMap<String, Object>();

			loadParametersRelatorio();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadParametersRelatorio() {

		NaCliente cliente = daoCliente.buscarCliente();

		byte[] imagemBrasao = cliente.getBrasao();

		if (imagemBrasao == null)
			imagemBrasao = cliente.getLogoAdministracao();

		if (imagemBrasao != null)
			reportListaParametro.put(ReportService.BRASAO_PREFEITURA, (imagemBrasao));

		reportListaParametro.put(ReportService.REPORT_DIR, "/neainformatica/relatorios/");
		reportListaParametro.put(ReportService.SUBREPORT_DIR, "/neainformatica/relatorios/");
		reportListaParametro.put(ReportService.SITE, "www.neainformatica.com.br");
		reportListaParametro.put(ReportService.VERSAO, InfrastructureController.getNeaInfrastructureSistemaVersao());
		reportListaParametro.put(ReportService.NOME_SISTEMA, infrastructureController.getNaSistema().getSistema());
		reportListaParametro.put(ReportService.NOME_PREFEITURA, cliente.getNome());
		if (cliente.getCidadelogradouro() != null)
			reportListaParametro.put(ReportService.CIDADE_PREFEITURA, cliente.getCidadelogradouro());
		else
			reportListaParametro.put(ReportService.CIDADE_PREFEITURA, "CIDADE MODELO");

		if (cliente.getCidadelogradouro() != null)
			reportListaParametro.put(ReportService.ESTADO_PREFEITURA, cliente.getCidadelogradouro().getEstado().getNomeFormal());
		else
			reportListaParametro.put(ReportService.ESTADO_PREFEITURA, "ESTADO MODELO");

	}

	public HashMap<String, Object> getReportListaParametro() {
		return reportListaParametro;
	}

	public JasperReport getSubReport(String subReport) {

		try {
			InputStream memoria = getClass().getResourceAsStream("/resources/neainformatica/relatorios/" + subReport);
			return (JasperReport) JRLoader.loadObject(memoria);
		} catch (JRException e) {
			e.printStackTrace();
			return null;
		}

	}

	public JasperReport getJasperReport(EnumRelatorioInterface relatorio) throws JRException {
		// String path;
		InputStream stream = null;
		String pathRelatorio;
		try {
			pathRelatorio = reportListaParametro.get(ReportService.REPORT_DIR) + relatorio.getArquivo() + ReportService.REPORT_EXT;
			FacesContext facesContext = FacesContext.getCurrentInstance();
			if(facesContext != null)
				stream = facesContext.getExternalContext().getResourceAsStream("/resources" + pathRelatorio);
			if(stream == null && servletContext != null)
				stream = servletContext.getResourceAsStream("/resources" + pathRelatorio);

			// se não encontrou no projeto leio dentro do jar
			if (stream == null) {
				stream = getClass().getResourceAsStream("/META-INF/resources" + pathRelatorio);
			}
			
			if (stream != null) {
				if (!reportLista.containsKey(relatorio)) {
					reportLista.put(relatorio, (JasperReport) JRLoader.loadObject(stream));
				}

				return reportLista.get(relatorio);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public List<NaPermissao> getPermissoes() {
		return permissoes;
	}

	public void setPermissoes(List<NaPermissao> permissoes) {
		this.permissoes = permissoes;
	}

	/**
	 * Este método deve ser executado ao subir a aplicação, para que caso não
	 * existe um cliente definido na base de dados o mesmo seja criado
	 * automaticamente.
	 * 
	 * @author elielcio.santos
	 */
	public NaCliente verificaClienteDefault() {
		boolean gravar = false;
		NaCliente cliente;
		try {

			cliente = daoCliente.buscarCliente();

			if (cliente == null) {
				cliente = new NaCliente();
				cliente.setId(1);
				gravar = true;
			}

			if (cliente.getNome() == null || cliente.getNome().trim().equals("")) {
				cliente.setNome("PREFEITURA MUNICIPAL DE MODELO");
				gravar = true;
			}

			if (cliente.getSiteCliente() == null || cliente.getSiteCliente().trim().equals("")) {

				cliente.setSiteCliente("https://www.sitecliente.uf.gov.br");
				gravar = true;
			}

			if (cliente.getLinkBrasao() == null || cliente.getLinkBrasao().trim().equals("")) {

				cliente.setLinkBrasao("http://neainformatica.tempsite.ws/brasoes/ms/ms-campo-grande-brasao.png");
				gravar = true;
			}

			if (cliente.getLinkLogo() == null || cliente.getLinkLogo().trim().equals("")) {

				cliente.setLinkLogo("http://neainformatica.tempsite.ws/brasoes/ms/ms-campo-grande-brasao.png");
				gravar = true;
			}

			if (gravar)
				daoCliente.noAudit().save(cliente);

			return cliente;

		} catch (NeaException e) {
			return null;
		}
	}

	public NaCliente buscarCliente() {
		return daoCliente.buscarCliente();
	}

	public List<NaCliente> buscarListaCliente() {
		return daoCliente.buscarListaCliente();
	}

	public void salvarCliente(NaCliente cliente) {

	}

	public void criaParametrosDevaloresFixo(Integer idSistema, String simpleNameEnumParametros) throws ClassNotFoundException, IOException {

		NaSistema sistema = daoSistema.buscarSistema(idSistema);
		criaParametrosDevaloresFixo(sistema, simpleNameEnumParametros);

	}

	public void criaParametrosDevaloresFixo(NaSistema naSistema, String simpleNameEnumParametros) throws ClassNotFoundException, IOException {

		List<Class<?>> classes = ToolsPackage.listClassesInPackage("br.com.neainformatica", simpleNameEnumParametros);

		List<EnumParametroInterface> listaEnumParametroInterface = new ArrayList<EnumParametroInterface>();

		for (Class<?> classe : classes) {
			if (classe.getSimpleName().equals(simpleNameEnumParametros)) {
				listaEnumParametroInterface.addAll(Arrays.asList((EnumParametroInterface[]) classe.getEnumConstants()));
			}
		}

		for (EnumParametroInterface enumParametroInterface : listaEnumParametroInterface) {
			if (enumParametroInterface.getValorFixo() == EnumSimNao.SIM) {
				NaParametro parametro = parametroService.getParametro(enumParametroInterface, naSistema);
				NaParametroValor valor = parametroService.getValorByParametro(parametro, "FIXO", naSistema);

				if (valor == null)
					parametroService.adicionarValorDefault(parametro, enumParametroInterface.getValorPadrao(), "FIXO");
			}
		}

	}

	public NaClienteRepository getDaoCliente() {
		return daoCliente;
	}

}