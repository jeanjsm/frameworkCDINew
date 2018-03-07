package br.com.neainformatica.infrastructure.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.data.PageEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.neainformatica.infrastructure.dao.NaAuditoriaItemRepository;
import br.com.neainformatica.infrastructure.dao.NaAuditoriaRepository;
import br.com.neainformatica.infrastructure.entity.NaAuditoria;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumAuditoriaOperacao;
import br.com.neainformatica.infrastructure.enumeration.EnumRelatorio;
import br.com.neainformatica.infrastructure.enumeration.EnumRelatorioExportacao;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.BetweenFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.EqualFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.LikeFilter;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.services.GenericService;
import br.com.neainformatica.infrastructure.services.NaAuditoriaService;
import br.com.neainformatica.infrastructure.services.ReportService;
import br.com.neainformatica.infrastructure.tools.NeaDate;
import net.sf.jasperreports.engine.JRException;

@Named
@ConversationScoped
public class NaAuditoriaController extends GenericController<NaAuditoria> implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaAuditoriaService service;

	private LazyDataModel<NaAuditoria> listaNaAuditoria;

	@Inject
	private ReportService reportService;

	@Inject
	private NaAuditoriaRepository naAuditoriaRepository;

	@Inject
	private NaSessionController naSessionController;

	@Inject
	private NaAuditoriaItemRepository auditoriaItemRepository;

	private Date dataInicial;
	private Date dataFinal;
	private NaUsuario usuario;
	private EnumAuditoriaOperacao operacao;
	private String nomeTabela;
	private String chaveRegistro;
	private boolean somenteClienteAtual;

	protected int first;

	@PostConstruct
	private void init() {
		iniciaCamposDePesquisa();

	}

	@Override
	public void setService(GenericService<NaAuditoria> service) {
		super.setService(this.service);
	}

	@Override
	public GenericService<NaAuditoria> getService() {
		return this.service;
	}

	public ReportService getReportService() {
		return reportService;
	}

	public void setReportService(ReportService reportService) {
		this.reportService = reportService;
	}

	@Override
	public String create() {

		return super.create();
	}

	@Override
	protected List<IFilter> filtersBeforeSearch(List<IFilter> filters) {
		return montaParametrosDePesquisa();
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public void onPageChange(PageEvent event) {
		this.setFirst(((DataTable) event.getSource()).getFirst());
	}

	public void printAuditoriaSelecionada() {

		try {
			if (getObjeto() != null && getObjeto().getId() != null) {

				List<NaAuditoria> list = new ArrayList<NaAuditoria>();

				list.add(service.findById(getObjeto().getId()));

				reportService.imprimir(EnumRelatorio.AUDITORIA, list);

			} else {
				messages.addInfoMessage("Por favor selecione ao menos um item da auditoria");
			}
		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void iniciaCamposDePesquisa() {
		this.dataInicial = new Date();
		this.dataFinal = new Date();
		this.nomeTabela = "";
		this.chaveRegistro = "";
		this.operacao = null;
		this.usuario = null;
		this.somenteClienteAtual = true;
	}

	public void printAuditoria(String relatorioExportacaoEnum) {
		try {

			List<NaAuditoria> list = service.findAll();

			if (list != null && list.size() > 0) {
				reportService.imprimir(EnumRelatorio.AUDITORIA, list, EnumRelatorio.AUDITORIA.getTitulo(), new HashMap<String, Object>(),
						EnumRelatorioExportacao.valueOf(relatorioExportacaoEnum));
			} else {
				messages.addInfoMessage("Não existem registros para impressão.");
			}

		} catch (JRException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void pesquisar() {
		final List<IFilter> filtros = montaParametrosDePesquisa();

		this.listaNaAuditoria = new LazyDataModel<NaAuditoria>() {

			private static final long serialVersionUID = 1L;
			List<NaAuditoria> listaAuditoria = new ArrayList<NaAuditoria>();

			@Override
			public Object getRowKey(NaAuditoria object) {
				return object;
			}

			@Override
			public List<NaAuditoria> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

				Integer dataSize = 0;
				NaCliente naCliente = null;
				if (somenteClienteAtual) {
					naCliente = naSessionController.getNaCliente();
				}

				listaAuditoria = naAuditoriaRepository.getListaAuditoria(naSessionController.getNaSistema(), first, pageSize, dataInicial, dataFinal,
						nomeTabela, chaveRegistro, operacao, usuario, naCliente);

				dataSize = naAuditoriaRepository.getListaAuditoriaCount(filtros, naSessionController.getNaSistema(), dataInicial, dataFinal, nomeTabela,
						chaveRegistro, operacao, usuario, naCliente);

				listaNaAuditoria.setRowCount(dataSize);

				return listaAuditoria;

			}
		};

	}

	public void carregarItens(NaAuditoria auditoria) {
		auditoria.setAuditoriaItems(auditoriaItemRepository.findByParam(auditoria, "auditoria"));
	}

	private List<IFilter> montaParametrosDePesquisa() {

		List<IFilter> filters = new ArrayList<IFilter>();

		if (this.dataInicial != null && this.dataFinal != null)
			filters.add(new BetweenFilter("dataAuditoria", NeaDate.zeraHoraData(this.dataInicial), NeaDate.ultimaHoraData(this.dataFinal), ""));

		if (this.nomeTabela != null && !this.nomeTabela.equals(""))
			filters.add(new LikeFilter("tabela", this.nomeTabela, ""));

		if (this.chaveRegistro != null && !this.chaveRegistro.equals(""))
			filters.add(new LikeFilter("chaveRegistro", this.chaveRegistro, ""));

		if (this.operacao != null)
			filters.add(new EqualFilter("tipoOperacao", operacao, ""));

		if (this.usuario != null) {
			if (usuario.getLogin().equals("SUPORTE"))
				filters.add(new EqualFilter("usuario", usuario.getLogin(), ""));
			else
				filters.add(new EqualFilter("usuario", usuario.getCpfCnpj(), ""));
		}

		return filters;
	}

	// @Override
	// public void dinamicSearch(final List<IFilter> listFilters) {
	//
	// setLazyList(new LazyDataModel<NaAuditoria>() {
	// private static final long serialVersionUID = 1L;
	//
	// @Override
	// public void setRowIndex(int rowIndex) {
	// if (rowIndex == -1 || getPageSize() == 0) {
	// super.setRowIndex(-1);
	// } else {
	// super.setRowIndex(rowIndex % getPageSize());
	// }
	// }
	//
	// @Override
	// public List<NaAuditoria> load(int first, int pageSize, String sortField,
	// SortOrder sortOrder, Map<String, String> filters) {
	//
	// int dataSize = 0;
	//
	// Collection<NaAuditoria> data = new ArrayList<>();
	//
	// data = (List<NaAuditoria>)
	// naAuditoriaRepository.getListaAuditoria(listFilters,
	// naSessionController.getNaSistema(), first, pageSize);
	//
	// dataSize = naAuditoriaRepository.getListaAuditoriaCount(listFilters,
	// naSessionController.getNaSistema());
	//
	// this.setRowCount(dataSize);
	//
	// return (List<NaAuditoria>) data;
	// }
	//
	// });
	// }

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public NaUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(NaUsuario usuario) {
		this.usuario = usuario;
	}

	public EnumAuditoriaOperacao getOperacao() {
		return operacao;
	}

	public void setOperacao(EnumAuditoriaOperacao operacao) {
		this.operacao = operacao;
	}

	public List<EnumAuditoriaOperacao> getListaOperacoes() {
		return Arrays.asList(EnumAuditoriaOperacao.values());
	}

	public String getNomeTabela() {
		return nomeTabela;
	}

	public void setNomeTabela(String nomeTabela) {
		this.nomeTabela = nomeTabela;
	}

	public String getChaveRegistro() {
		return chaveRegistro;
	}

	public void setChaveRegistro(String chaveRegistro) {
		this.chaveRegistro = chaveRegistro;
	}

	public LazyDataModel<NaAuditoria> getListaNaAuditoria() {
		if (this.listaNaAuditoria == null)
			pesquisar();
		return listaNaAuditoria;

		// for (NaAuditoria audit : this.listaNaAuditoria) {
		// if (audit.getAuditoriaItems().size() == 0)
		// audit.setAuditoriaItems(new ArrayList<NaAuditoriaItem>());
		// }

	}

	public boolean isSomenteClienteAtual() {
		return somenteClienteAtual;
	}

	public void setSomenteClienteAtual(boolean somenteClienteAtual) {
		this.somenteClienteAtual = somenteClienteAtual;
	}

}
