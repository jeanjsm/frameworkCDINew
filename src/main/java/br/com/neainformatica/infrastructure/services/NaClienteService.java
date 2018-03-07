package br.com.neainformatica.infrastructure.services;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaClienteRepository;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;
import br.com.neainformatica.infrastructure.rest.bean.NaClienteBean;
import br.com.neainformatica.infrastructure.tools.Tools;
import org.apache.commons.io.IOUtils;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NaClienteService extends GenericService<NaCliente> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaClienteRepository dao;

	@Inject
	private NaSessionController naSessionController;

	@Inject
	private InfrastructureService infrastructureService;

	@Inject
	private InfrastructureController infrastructureController;

	@Inject
	private NaUsuarioSistemaService usuarioSistemaService;

	private List<NaCliente> listaClientes;

	@PostConstruct
	private void init() {
		listaClientes = new ArrayList<>();
	}

	@Override
	public GenericRepository<NaCliente> getRepository() {
		return this.dao;
	}
	
	public void setRepository(NaClienteRepository dao) {
		this.dao = dao;
	}

	@Override
	protected void beforeSave(NaCliente entity) {
		entity.setDataAlteracao(new Date());
		// Valida se a base o sistema esta no DataCenter
		NaSistema sistema = infrastructureController.getNaSistema();
//		if (sistema.getBasePrincipal().equals(EnumSimNao.SIM)) {
//			entity.setOrigemSincronismo(EnumOrigemSincronismo.SERVER);
//		} else {
//			entity.setOrigemSincronismo(EnumOrigemSincronismo.CLIENTE);
//		}
		super.beforeSave(entity);
	}

	public List<NaCliente> buscarListaCliente() {
		if (!listaClientes.isEmpty()) {
			return listaClientes;
		}
		return dao.buscarListaCliente();
	}

	/**
	 * Este método deve ser executado ao subir a aplicação, para que caso não
	 * existe um cliente definido na base de dados o mesmo seja criado
	 * automaticamente.
	 * 
	 * @author elielcio.santos
	 */
	public NaCliente verificaClienteDefault() {

		NaCliente cliente = dao.buscarCliente();

		if (cliente == null) {

			log.info("não existe cliente definido, criando...");

			cliente = new NaCliente();
			cliente.setId(1);
			cliente.setNome("PREFEITURA MUNICIPAL DE MODELO");
			try {
				save(cliente);
			} catch (NeaException e) {
				e.printStackTrace();
			}

			log.info("cliente padrão criado com sucesso");
		}

		return cliente;

	}

	public NaClienteBean buscarCliente(String cnpj, Integer idClienteNeA) {
		return getNaClienteBean(dao.buscarCliente(cnpj, idClienteNeA));
	}

	public NaClienteBean getNaClienteBean(NaCliente cliente) {
		NaClienteBean clienteBean = new NaClienteBean();
		clienteBean.setId(cliente.getId());
		clienteBean.setNome(cliente.getNome());
		clienteBean.setCnpj(cliente.getCnpj());
		clienteBean.setNomeSecretaria(cliente.getNomeSecretaria());
		clienteBean.setSiteCliente(cliente.getSiteCliente());
		clienteBean.setLinkBrasao(cliente.getLinkBrasao());
		clienteBean.setLinkLogo(cliente.getLinkLogo());
		clienteBean.setSituacaoAcesso(cliente.getSituacaoAcesso().getDescricao());
		clienteBean.setIdClienteNeA(cliente.getIdClienteNeA());
		clienteBean.setAtivo(cliente.getAtivo().getDescricao());
		clienteBean.setTelefoneDdd(cliente.getTelefoneDdd());
		clienteBean.setTelefoneNumero(cliente.getTelefoneNumero());
		clienteBean.setLogradouroCep(cliente.getLogradouroCep());
		clienteBean.setLogradouroTipoZona(cliente.getLogradouroTipoZona().getDescricao());
		clienteBean.setLogradouroNome(cliente.getLogradouroNome());
		clienteBean.setLogradouroNumero(cliente.getLogradouroNumero());
		clienteBean.setLogradouroSemNumero(cliente.getLogradouroSemNumero().getDescricao());
		clienteBean.setLogradouroQuadra(cliente.getLogradouroQuadra());
		clienteBean.setLogradouroLote(cliente.getLogradouroLote());
		clienteBean.setIdTipologradouro(cliente.getTipologradouro().getId());
		clienteBean.setLogradouroComplemento(cliente.getLogradouroComplemento());
		clienteBean.setLogradouroLongitude(cliente.getLogradouroLongitude());

		if (cliente.getTipobairro() != null)
			clienteBean.setIdTipobairro(cliente.getTipobairro().getId());
		if (cliente.getBairro() != null)
			clienteBean.setIdBairro(cliente.getBairro().getId());

		clienteBean.setBairroNome(cliente.getBairroNome());

		if (cliente.getCidadelogradouro() != null)
			clienteBean.setIdCidadelogradouro(cliente.getCidadelogradouro().getId());
		clienteBean.setCidadeNome(cliente.getCidadeNome());
		clienteBean.setUfNome(cliente.getUfNome());
		if (cliente.getUf() != null)
			clienteBean.setIdUf(cliente.getUf().getId());
		clienteBean.setBrasao(cliente.getBrasao());
		clienteBean.setLogoAdministracao(cliente.getLogoAdministracao());

		return clienteBean;

	}

	public List<NaCliente> buscaListaDeClientes() {

		// if (naSessionController.getNaUsuarioSistema() == null)
		// return buscarListaCliente();

		if (naSessionController.getNaUsuario().getLogin().equals("SUPORTE"))
			return usuarioSistemaService.buscarClientesDoUsuario(null, naSessionController.getNaSistema());
		return usuarioSistemaService.buscarClientesDoUsuario(naSessionController.getNaUsuario(), naSessionController.getNaSistema());
	}

	public List<NaCliente> buscarClientesUsuario(NaUsuario usuario, NaSistema sistema) {
		return usuarioSistemaService.buscarClientesDoUsuario(usuario, sistema);
	}

	@Override
	protected void afterSave(NaCliente entity) {
		//naSessionController.limparNaCliente();
		super.afterSave(entity);
	}

	public void setDao(NaClienteRepository dao) {
		this.dao = dao;
	}

	public List<NaCliente> getListaClientes() {
		return listaClientes;
	}

	public void setListaClientes(List<NaCliente> listaClientes) {
		this.listaClientes = listaClientes;
	}

	public NaCliente defineNaCliente(NaCliente naCliente) {
		// caso nao exista as imagens adiciono o logo da nea
		if (naCliente.getBrasao() == null || naCliente.getLogoAdministracao() == null) {

			try {

				File file = null;

				// tento ler a imagem dos arquivos .jar
				InputStream in = getClass().getResourceAsStream("/META-INF/resources/neainformatica/framework/images/logo_nea.png");

				// se não encontrou busco pelo caminho absoluto
				if (in == null) {
					ServletContext sContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
					file = new File(sContext.getRealPath("/resources/neainformatica/framework/images/logo_nea.png"));
				}

				if (naCliente.getBrasao() == null) {

					if (in == null)
						naCliente.setBrasao(Tools.fileToByte(file));
					else
						naCliente.setBrasao(IOUtils.toByteArray(in));
				}

				if (naCliente.getLogoAdministracao() == null) {
					if (in == null)
						naCliente.setLogoAdministracao(Tools.fileToByte(file));
					else

						naCliente.setLogoAdministracao(IOUtils.toByteArray(in));
				}

				infrastructureService.salvarCliente(naCliente);

			} catch (Exception e) {
				log.error("Erro ao definir NaCliente.", e);
				return naCliente;
			}
		}
		return naCliente;
	}

	public byte[] getImagemById(Integer id) {
		return dao.getImagemById(id);
	}

	/**
	 * 
	 * @param idClienteNeA
	 * @return
	 */
	public NaCliente buscarClientePorIdNea(Integer idClienteNeA) {
		return dao.buscarClientePorIdNea(idClienteNeA);
	}

	public NaCliente buscarCliente(Integer id) {
		return dao.buscarCliente(id);
	}

}
