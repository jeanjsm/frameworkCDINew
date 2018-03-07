package br.com.neainformatica.infrastructure.services;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;

import br.com.neainformatica.infrastructure.controller.GenericMessages;
import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaClienteRepository;
import br.com.neainformatica.infrastructure.dao.NaUsuarioPerfilRepository;
import br.com.neainformatica.infrastructure.dao.NaUsuarioRepository;
import br.com.neainformatica.infrastructure.entity.NaAlteracaoSenha;
import br.com.neainformatica.infrastructure.entity.NaAtor;
import br.com.neainformatica.infrastructure.entity.NaAtorPermissao;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioPerfil;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumFormatDate;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumOrigemSincronismo;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumSituacaoAcesso;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;
import br.com.neainformatica.infrastructure.mail.EmailService;
import br.com.neainformatica.infrastructure.searchengine.parameters.filter.generic.IFilter;
import br.com.neainformatica.infrastructure.tools.NeaDate;
import br.com.neainformatica.infrastructure.tools.NeaFormatter;
import br.com.neainformatica.infrastructure.tools.NeaStrings;
import br.com.neainformatica.infrastructure.tools.Tools;

public class NaUsuarioService extends GenericService<NaUsuario> implements NaServiceInterface, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NaUsuarioRepository dao;

	@Inject
	NaClienteRepository naClienteRepository;

	@Inject
	private NaUsuarioPerfilRepository daoUsuarioPerfil;

	@Inject
	private EmailService emailService;

	@Inject
	private NaUsuarioSistemaService usuarioSistemaService;

	@Inject
	private InfrastructureController infrastructureController;

	@Inject
	private NaSessionController sessionController;

//	@Inject
//	private GenericMessages messages;

	@Inject
	@Any
	private Event<NaUsuario> eventoNaUsuario;

	@Inject
	private NaAlteracaoSenhaService naAlteracaoSenhaService;
	
	@Inject
	private Instance<NaAtorService> naAtorService;
	
	@Inject
	private NaAtorPermissaoService naAtorPermissaoService;

	@Override
	public GenericRepository<NaUsuario> getRepository() {
		return this.dao;
	}
	
	@Override
	public GenericService<NaUsuario> useEntityManager(EntityManager em) {
		usuarioSistemaService.useEntityManager(em);
		infrastructureController.useEntityManager(em);
		daoUsuarioPerfil.setEntityManager(em);
		naClienteRepository.setEntityManager(em);
		naAtorService.get().useEntityManager(em);
		naAtorPermissaoService.useEntityManager(em);
		dao.setEntityManager(em);
		return super.useEntityManager(em);
	}

	public NaUsuario buscaUsuarioLogin(String userName) {
		return dao.buscaUsuarioLogin(userName);
	}

	public List<NaUsuario> buscarListaPorLogin(String userName) {
		return dao.buscarListaPorLogin(userName);
	}

	public NaUsuario buscaUsuarioCpf(String cpfCnpj) {
		return dao.buscaUsuarioCpf(NeaStrings.somenteNumeros(cpfCnpj));
	}
	
	public NaUsuario buscaUsuarioCpfOrLogin(String cpfCnpj) {
		NaUsuario usuario = dao.buscaUsuarioCpf(cpfCnpj);
		
		if (usuario ==  null)
			usuario = dao.buscaUsuarioLogin(cpfCnpj);
		
		return usuario;
	}

	public void definirUsuarioSuporte(NaCliente naCliente, NaSistema sistema) {

		String nomeUsuario = "SUPORTE";
		EnumNivelUsuario nivelUsuario = EnumNivelUsuario.SUPORTE;

		try {

			NaUsuario usuario = buscaUsuarioLogin(nomeUsuario);

			if (usuario == null) {

				log.debug("nao existe o usuario " + nomeUsuario + ", criando...");
				usuario = new NaUsuario();
				usuario.setId(32000);
				usuario.setNome(nomeUsuario);
				usuario.setLogin(nomeUsuario);
				usuario.setSenha("CALCULADA");

				usuario = noAudit().save(usuario);
				log.debug("usuario SUPORTE criado com sucesso");
			}

			NaUsuarioSistema usuarioSistema = usuarioSistemaService.obterUsuarioSistema(sistema.getId(), usuario.getId(), naCliente);
			if (usuarioSistema == null) {

				// vincula o usuário suporte a base atual
				usuarioSistema = new NaUsuarioSistema();
				usuarioSistema.setSistema(sistema);
				usuarioSistema.setNaCliente(naCliente);
				usuarioSistema.setUsuario(usuario);
				usuario.setNivelUsuario(nivelUsuario);

				saveUsuarioSistemaSemAuditoria(usuarioSistema, sistema);
			}

		} catch (NeaException e) {
			log.error("Erro ao verificar se usuario é suporte! ", e);
		}
	}

	@Override
	protected void beforeSave(NaUsuario entity) {

		entity.setDataAlteracao(new Date());
		// Valida se a base o sistema esta no DataCenter
		NaSistema sistema = infrastructureController.getNaSistema();
		if (sistema.getBasePrincipal() != null && sistema.getBasePrincipal().equals(EnumSimNao.SIM)) {
			entity.setOrigemSincronismo(EnumOrigemSincronismo.SERVER);
		} else {
			entity.setOrigemSincronismo(EnumOrigemSincronismo.CLIENTE);
		}
		super.beforeSave(entity);
	}

	@Override
	protected void afterSave(NaUsuario naUsuario) {
		try {
			// quando o framework esta iniciando ainda não tem session
			if (sessionController != null) {

				NaSistema naSistema = null;
				NaCliente naCliente = null;

				try {
					naSistema = infrastructureController.getNaSistema();
					naCliente = sessionController.getNaCliente();
				} catch (ContextNotActiveException e) {
					// no momento do start do sistema não existe sessão do
					// usuário
					naSistema = null;
					naCliente = null;
				}

				if (naSistema != null && naCliente != null) {

					log.debug("nasistema: " + naSistema.getSistema());
					log.debug("naCliente:" + naCliente.getNome());

					if (naSistema.getBasePrincipal() != null && naSistema.getBasePrincipal() == EnumSimNao.SIM && naUsuario.getIdNaUsuarioServer() == null) {
						naUsuario.setIdNaUsuarioServer(naUsuario.getId());
						naUsuario = save(naUsuario);
					}

					// verifica se o usuário ja possui permissão no sistema
					NaUsuarioSistema usuarioSistema = possuiPermissaoParaAcessarEsteSistema(naUsuario, naCliente);

					if (usuarioSistema == null) {
						usuarioSistema = new NaUsuarioSistema();
						if (naUsuario.getNivelUsuario() != null) {
							usuarioSistema.setNivelUsuario(naUsuario.getNivelUsuario());
						} else {
							usuarioSistema.setNivelUsuario(EnumNivelUsuario.USUARIO);
						}
						usuarioSistema.setSistema(naSistema);
						usuarioSistema.setNaCliente(naCliente);
						usuarioSistema.setUsuario(naUsuario);
						usuarioSistema.setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);
						saveUsuarioSistemaSemAuditoria(usuarioSistema, naSistema);
					}

				}

			}
			eventoNaUsuario.fire(naUsuario);

		} catch (NeaException e) {
			log.error("Erro ao executar metodo afterSave! ", e);
		}

	}

	public List<NaUsuario> getNaUsuarios(List<IFilter> filters, NaSistema sistema, NaCliente cliente, int currentIndex, int count) {
		return dao.getNaUsuarios(filters, sistema, cliente, currentIndex, count);
	}

	@Deprecated
	public List<NaUsuario> getNaUsuariosBySistemaCliente(Integer sistemaId, Integer clienteId) {
		return dao.getNaUsuariosBySistemaCliente(sistemaId, clienteId);
	}

	public List<NaUsuario> getNaUsuariosBySistemaClienteNivelUsuario(Integer sistemaId, Integer clienteId, EnumNivelUsuario nivelUsuario) {
		List<NaUsuario> listaNaUsuarios = new ArrayList<>();

		// Busca os usuarios para aquele sistema/cliente e carrega seu nivel no usuario
		List<NaUsuarioSistema> listaNaUsuarioSistema = usuarioSistemaService.buscarListaPorSistemaCliente(sistemaId, clienteId);
		for (NaUsuarioSistema naUsuarioSistema : listaNaUsuarioSistema) {
			NaUsuario naUsuario = naUsuarioSistema.getUsuario();
			naUsuario.setNivelUsuario(naUsuarioSistema.getNivelUsuario());
			listaNaUsuarios.add(naUsuario);
		}

		// Se usuario for suporte, retorna todos os usuarios
		if (nivelUsuario.toInt() >= EnumNivelUsuario.SUPORTE.toInt()) {
			return listaNaUsuarios;
		}
		// Senao for suporte, filtra os usuarios que possuem nivel menor que suporte
		List<NaUsuario> listaRetorno = new ArrayList<>();
		for (NaUsuario naUsuario : listaNaUsuarios) {
			if (naUsuario.getNivelUsuario().toInt() < EnumNivelUsuario.SUPORTE.toInt()) {
				listaRetorno.add(naUsuario);
			}
		}
		return listaRetorno;
	}

	public Integer getNaUsuariosCount(List<IFilter> filters, NaSistema sistema, NaCliente cliente) {
		return dao.getNaUsuariosCount(filters, sistema, cliente);
	}

	public String resetaSenha(NaUsuario naUsuario) {
		String novaSenha = Tools.geraSenhaAleatoria(8).toUpperCase();
		try {
			naUsuario.setSenha(Tools.geraMD5(naUsuario.getIdUsuarioSenha() + novaSenha));
			naUsuario.setAlterarSenhaProximoLogin(EnumSimNao.SIM);
			dao.save(naUsuario);
			return novaSenha;
		} catch (Exception e) {
			return "";
		}
	}

	public NaUsuarioSistema saveUsuarioSistemaSemAuditoria(NaUsuarioSistema usuarioSistema, NaSistema sistemaAtual) throws NeaException {

		usuarioSistema.setDataAlteracao(new Date());

		if (usuarioSistema.getSituacaoAcesso() == null) {
			usuarioSistema.setSituacaoAcesso(EnumSituacaoAcesso.ATIVO);
		}

		if (sistemaAtual.getBasePrincipal() == null || sistemaAtual.getBasePrincipal() == EnumSimNao.NAO) {
			usuarioSistema.setOrigemSincronismo(EnumOrigemSincronismo.CLIENTE);
		} else {
			usuarioSistema.setOrigemSincronismo(EnumOrigemSincronismo.SERVER);
		}

		return usuarioSistemaService.noAudit().save(usuarioSistema);
	}

	/**
	 * Verifica se o usuário logado tem permissão para acessar o sistema atual
	 *
	 * @param usuario
	 * @return
	 */
	public NaUsuarioSistema possuiPermissaoParaAcessarEsteSistema(NaUsuario usuario, NaCliente cliente) {

		NaUsuarioSistema usuarioSistema = null;

		if (usuario.getLogin().equalsIgnoreCase("SUPORTE")) {
			usuarioSistema = new NaUsuarioSistema();
			usuarioSistema.setNivelUsuario(EnumNivelUsuario.SUPORTE);
			usuarioSistema.setSistema(infrastructureController.getNaSistema());
			usuarioSistema.setUsuario(usuario);
			usuarioSistema.setId(usuario.getId());
		}

		if (usuarioSistema == null) {
			usuarioSistema = buscarUsuarioSistema(usuario, infrastructureController.getNaSistema(), cliente);
		}

		return usuarioSistema;
	}

	public List<NaUsuarioPerfil> buscaUsuarioPerfil(NaUsuario usuario) {
		return daoUsuarioPerfil.buscaUsuarioPerfil(usuario);
	}

	@Deprecated
	public NaUsuarioSistema buscarUsuarioSistema(NaUsuario naUsuario, NaSistema naSistema, NaCliente cliente) {
		return usuarioSistemaService.obterUsuarioSistema(naSistema.getId(), naUsuario.getId(), cliente.getId(), EnumSimNao.SIM);
	}

	@Deprecated
	public NaUsuarioSistema buscarUsuarioSistemaInativo(Integer naUsuarioId, Integer naSistemaId, Integer clienteId) {
		return usuarioSistemaService.obterUsuarioSistema(naSistemaId, naUsuarioId, clienteId, EnumSimNao.NAO);
	}

	@Deprecated
	public NaUsuarioSistema buscarUsuarioSistemaAtivo(Integer naUsuarioId, Integer naSistemaId, Integer clienteId) {
		return usuarioSistemaService.obterUsuarioSistema(naSistemaId, naUsuarioId, clienteId, EnumSimNao.SIM);
	}

	public boolean existeUsuarioSistema(NaUsuario naUsuario, NaSistema naSistema, NaCliente cliente) {
		NaUsuarioSistema usuarioSistema = buscarUsuarioSistema(naUsuario, naSistema, cliente);

		return usuarioSistema != null;
	}

	public NaUsuarioPerfil getUsuarioPerfilById(Integer id) {
		return daoUsuarioPerfil.findById(id);
	}

	public void save(NaUsuarioPerfil usuarioPerfil) {
		try {
			daoUsuarioPerfil.save(usuarioPerfil);
		} catch (NeaException e) {
			log.error("Erro ao Salvar Perfil do Usuário! ", e);
		}
	}

	public void delete(NaUsuarioPerfil usuarioPerfil) {
		try {
			daoUsuarioPerfil.delete(usuarioPerfil);
		} catch (NeaException e) {
			log.error("Erro ao Deletar Perfil do Usuário !", e);
		}
	}

	public List<NaUsuario> getUsuariosSistema(Integer idSistema) {
		return usuarioSistemaService.buscarUsuariosSistema(idSistema);
	}

	private void recuperarSenha(NaUsuario naUsuario) throws UnsupportedEncodingException, AddressException, MessagingException {

		naAlteracaoSenhaService.mudaStatusHash(naUsuario);
		NaAlteracaoSenha naAlteracaoSenhaRecuperar = naAlteracaoSenhaService.salvarHash(naUsuario);

		StringBuilder corpoMensagem = lerArquivoHtml("corpoEmail.html");

		Date data = new Date();
		NeaStrings.replaceAllStringBuilder(corpoMensagem, "#cpf#", NeaFormatter.formatarCpfCnpj(naUsuario.getCpfCnpj()));
		NeaStrings.replaceAllStringBuilder(corpoMensagem, "#nomeSistema#", infrastructureController.getNaSistema().getSistema());
		NeaStrings.replaceAllStringBuilder(corpoMensagem, "#nomeFuncionario#", naUsuario.getNome());

		String linkExterno = infrastructureController.getNaSistema().getLinkExterno();
		int tamanho = linkExterno.length();
		String ultimoCaractere = linkExterno.substring(tamanho - 1, tamanho);

		if (!ultimoCaractere.equals("/")) {
			linkExterno = linkExterno + "/";
		}

		NeaStrings.replaceAllStringBuilder(corpoMensagem, "#linkExterno#",
				linkExterno + "framework/publico/alterarSenha.xhtml?hash=" + naAlteracaoSenhaRecuperar.getChaveValidacao());

		NeaStrings.replaceAllStringBuilder(corpoMensagem, "#novaSenha#", "novaSenha");
		NeaStrings.replaceAllStringBuilder(corpoMensagem, "#data#", NeaDate.formatarData(data, EnumFormatDate.RESUMIDO));
		NeaStrings.replaceAllStringBuilder(corpoMensagem, "#hora#", NeaDate.formatarData(data, EnumFormatDate.HORA_DATA_DETALHADA));

		Address[] destinatario = emailService.convertStringToInternetAddres(naUsuario.getEmail(), naUsuario.getNome());
		String assunto = "Alteração de Senha (Protocolo n. " + naAlteracaoSenhaRecuperar.getProtocolo() + ")";

		emailService.setNomeUsuario(infrastructureController.getNaSistema().getSistema());
		emailService.sendMail(destinatario, null, assunto, corpoMensagem, null);

	}

	public List<NaUsuarioSistema> obterSistemasDoUsuario(NaUsuario usuario) {
		return usuarioSistemaService.findByParam(usuario, "usuario");
	}

	public List<NaSistema> obeterSistemas(NaUsuario usuario, NaCliente cliente) {
		return usuarioSistemaService.obterSistemaDoUsarioLogado(usuario, cliente);
	}

	public List<NaUsuarioSistema> buscarSistemasDoUsuario(NaUsuario usuario, NaCliente cliente) {
		return usuarioSistemaService.buscarSistemasDoUsuario(usuario, cliente);
	}

	public void removerSistemaDoUsuario(List<NaUsuarioSistema> listaRemoverSistemaDoUsuario) {
		for (NaUsuarioSistema naUsuarioSistema : listaRemoverSistemaDoUsuario) {
			naUsuarioSistema.setAtivo(EnumSimNao.NAO);
			usuarioSistemaService.save(naUsuarioSistema);
		}
	}

	public void adicionarSistemasUsuario(List<NaUsuarioSistema> listaSistemaDoUsuario) {
		for (NaUsuarioSistema naUsuarioSistema : listaSistemaDoUsuario) {
			usuarioSistemaService.save(naUsuarioSistema);
		}
	}

	/**
	 * O usuário pode permitir acesso a outros sistemas somente a nível de acesso
	 * menor que o seu exceto administradores que podem permitir outros
	 * administradores
	 * 
	 * @param sistemaSelecionado
	 * @param nivelSelecionado
	 * @param usuarioSistemaAtual
	 * @param cliente
	 * @return
	 * @throws Exception
	 */
	public boolean permiteIncluirAcesso(NaSistema sistemaSelecionado, EnumNivelUsuario nivelSelecionado, NaUsuarioSistema usuarioSistemaAtual,
			NaCliente cliente) throws Exception {

		if (EnumNivelUsuario.nivelMenorAtualOuIgualAdministrador(nivelSelecionado, usuarioSistemaAtual.getNivelUsuario()))
			return true;

		throw new Exception("O usuário " + usuarioSistemaAtual.getUsuario().getNome() + " não possui permissão para utilizar o nível de acesso "
				+ nivelSelecionado.getDescricao() + " no sistema " + usuarioSistemaAtual.getSistema().getSistema());

	}

	public List<EnumNivelUsuario> obeterNiveisDeAcessoUsuario(NaUsuario usuarioLogado) {
		EnumNivelUsuario nivelMaximo = usuarioSistemaService.obtemNivelMaximoAcessoUsuarioSistema(usuarioLogado);
		return EnumNivelUsuario.getNiveisAbaixo(nivelMaximo);
	}

	/**
	 * Um usuário só pode dar nivel de acesso menor que o seu para outros usuários.
	 * Exceto administradores
	 * 
	 * @param usuarioSelecionado
	 * @param usuarioAtual
	 * @param naCliente
	 * @return
	 * @throws Exception
	 */
	public boolean validarEdicaoUsuario(NaUsuario usuarioSelecionado, NaUsuarioSistema usuarioAtual, NaCliente naCliente) throws Exception {

		NaUsuarioSistema usuarioSistemaSelecionado = usuarioSistemaService.obterUsuarioSistema(usuarioAtual.getSistema().getId(), usuarioSelecionado.getId(), naCliente);

		if (usuarioSistemaSelecionado == null)
			throw new Exception("Edição bloqueada! O usuário " + usuarioAtual.getUsuario().getNome() + " não possui acesso ao sistema.");

		Integer nivelUsuarioSelecionado = usuarioSistemaSelecionado.getNivelUsuario().getId();
		Integer nivelUsuarioAtual = usuarioAtual.getNivelUsuario().getId();

		if ((nivelUsuarioSelecionado < nivelUsuarioAtual)
				|| (nivelUsuarioSelecionado == EnumNivelUsuario.ADMINISTRADOR.toInt() && nivelUsuarioAtual == EnumNivelUsuario.ADMINISTRADOR.toInt()))
			return true;

		return false;
	}

	@Override
	public NaUsuario save(NaUsuario usuario, NaUsuario oldusuario) throws NeaException {

		/* Novo usuário então é preciso criar um registro de NaUsuarioSistema */
		NaSistema sistema = sessionController.getNaUsuarioSistema().getSistema();
		NaUsuarioSistema novoUsuarioSistema;
		EnumNivelUsuario nivelSelecionado = usuario.getNivelUsuario();

		usuario = super.save(usuario, oldusuario);

		if (usuario.getId() == null) {

			novoUsuarioSistema = new NaUsuarioSistema();
			novoUsuarioSistema.setUsuario(usuario);
			novoUsuarioSistema.setSistema(sistema);
			novoUsuarioSistema.setNivelUsuario(nivelSelecionado);

		} else {
			novoUsuarioSistema = usuarioSistemaService.obterUsuarioSistema(sistema.getId(), usuario.getId(), sessionController.getNaCliente());

			if (nivelSelecionado != null && novoUsuarioSistema.getNivelUsuario() != nivelSelecionado) {
				novoUsuarioSistema.setNivelUsuario(nivelSelecionado);
			}
		}
		usuarioSistemaService.save(novoUsuarioSistema);
		return usuario;
	}

	public void verificaPermissoesEmTodosOsSistemas(NaUsuarioSistema usuarioSistema) {

		List<NaSistema> sistemasSemAcesso = usuarioSistemaService.obtemSistemasQueUsuarioNaoPossuiAcesso(usuarioSistema.getUsuario());

		if (sistemasSemAcesso.isEmpty()) {
			return;
		}

		List<NaUsuarioSistema> listaNovosUsuarioSistema = new ArrayList<>();

		for (NaSistema naSistema : sistemasSemAcesso) {
			NaUsuarioSistema novoUsuarioSistema = new NaUsuarioSistema();
			novoUsuarioSistema.setSistema(naSistema);
			novoUsuarioSistema.setUsuario(usuarioSistema.getUsuario());
			novoUsuarioSistema.setNivelUsuario(usuarioSistema.getNivelUsuario());

			listaNovosUsuarioSistema.add(novoUsuarioSistema);
		}

		usuarioSistemaService.saveAll(listaNovosUsuarioSistema);
	}

	/**
	 * Retorno o conteúdo do arquivo passado como parâmetro
	 *
	 * @param nomeArquivo
	 * @return
	 */
	public StringBuilder lerArquivoHtml(String nomeArquivo) {
		StringBuilder conteudo = new StringBuilder();
		try {
			// java.io.InputStream in =
			// getClass().getResourceAsStream("/resources/"+ nomeArquivo);
			java.io.InputStream in = getClass().getClassLoader().getResourceAsStream("resources/neainformatica/framework/email/" + nomeArquivo);

			if (in == null) {
				in = getClass().getClassLoader().getResourceAsStream("/META-INF/resources/neainformatica/framework/email/" + nomeArquivo);
			}

			java.io.Reader reader = new java.io.InputStreamReader(in);
			java.io.BufferedReader leitor = new java.io.BufferedReader(reader);
			String dados = " ";
			while ((dados = leitor.readLine()) != null) {
				conteudo.append(dados);
			}

		} catch (FileNotFoundException e) {
			log.error("Erro ao Ler Arquivo HTML! ", e);
		} catch (IOException e) {
			log.error("Erro ao Ler Arquivo HTML! ", e);
		}
		return conteudo;
	}

	public List<NaUsuario> buscarPorClienteSistemaSemVinculoNaUsuarioSistema(NaCliente cliente, NaSistema sistema) {
		return this.dao.buscarPorClienteSistemaSemVinculoNaUsuarioSistema(cliente, sistema);
	}

	public List<NaUsuario> buscarListaPorNomeCliente(NaCliente cliente, String nomeUsuario) {
		return this.dao.buscarListaPorNomeCliente(cliente, nomeUsuario);
	}

	public List<NaUsuario> buscarListaPorNome(String nomeUsuario) {
		return this.dao.buscarListaPorNome(nomeUsuario);
	}
	
	public List<NaAtorPermissao> buscarPermissoesEfetivas(NaUsuarioSistema usuarioSistema) {
		NaCliente naCliente = sessionController.getNaCliente();
		
		List<NaAtor> atores = naAtorService.get().buscarAtoresUsuario(usuarioSistema.getUsuario(), usuarioSistema.getSistema());
		return naAtorPermissaoService.buscaAtorPermissao(atores, naCliente);
	}
	
	public void forcarNovaSenha(NaUsuario usuario, String novaSenha, String novaSenhaConfirmacao) throws Exception {
		if (usuario == null)	
			throw new Exception("Usuário não informado");
				
		if (novaSenha == null)	
			throw new Exception("Senha não informada");
		
		if (novaSenhaConfirmacao == null)	
			throw new Exception("Senha de confirmação não informada");
			
		if (!novaSenha.equals(novaSenhaConfirmacao))
			throw new Exception("A nova senha e a confirmação da senha devem ser iguais.");

		if (!validaFormatoDeSenha(novaSenha)) 
			throw new Exception("A nova senha deve possuir de 8 à 32 carácteres, sendo constituídos por letras e números!");
			
		usuario.setSenha(Tools.geraMD5(usuario.getIdUsuarioSenha() + novaSenha).toLowerCase());
		usuario.setAlterarSenhaProximoLogin(EnumSimNao.NAO);
		
		if (usuario.getIdNaUsuarioServer() == null)
			usuario.setSenhaNaUsuarioServer(Tools.encripta(usuario.getIdUsuarioSenha() + novaSenha));
						
		noAudit().save(usuario);		
	}
	
	
	public void alterarSenha(NaUsuario usuario, String senhaAtual, String novaSenha, String novaSenhaConfirmacao) throws Exception {

		if (usuario == null)	
			throw new Exception("Usuário não informado");
		
		if (novaSenha == null)	
			throw new Exception("Nova Senha não Informada");
		
		if (novaSenhaConfirmacao == null)	
			throw new Exception("Senha de confirmação não informada");
		
		String senhaMD5 = Tools.geraMD5(usuario.getIdUsuarioSenha() + senhaAtual).toUpperCase();

		if (!senhaMD5.toUpperCase().equals(usuario.getSenha().toUpperCase())) 
			throw new Exception("A senha atual informada, não corresponde a senha do usuário. Verifique a senha informada.");
			
		if (novaSenha.equals(senhaAtual)) 
			throw new Exception("A nova senha não pode ser idêntica a senha anterior. Verifique a senha informada.");
			
		if (!novaSenha.equals(novaSenhaConfirmacao))
			throw new Exception("A nova senha e a confirmação da senha devem ser iguais.");

		if (!validaFormatoDeSenha(novaSenha)) 
			throw new Exception("A nova senha deve possuir de 8 à 32 carácteres, sendo constituídos por letras e números!");
			
		usuario.setSenha(Tools.geraMD5(usuario.getIdUsuarioSenha() + novaSenha).toLowerCase());
		usuario.setAlterarSenhaProximoLogin(EnumSimNao.NAO);
		
		if (usuario.getIdNaUsuarioServer() == null)
			usuario.setSenhaNaUsuarioServer(Tools.encripta(usuario.getIdUsuarioSenha() + novaSenha));
		
		setUserAudit(usuario);		
		save(usuario);			
	}
	
	public boolean validaFormatoDeSenha(String senha) {

		boolean possuiLetra = false, possuiNumero = false;

		if (senha.length() < 8 || senha.length() > 32) {
			return false;
		}

		for (int i = 0; i < senha.length(); i++) {

			char caracter = senha.charAt(i);

			if (Character.isLetter(caracter)) {
				possuiLetra = true;
				continue;
			}
			if (Character.isDigit(caracter)) {
				possuiNumero = true;
				continue;
			}
		}
		if (possuiLetra && possuiNumero) {
			return true;
		}
		return false;
	}
	
	public void enviarEmailrecuperacaoSenha(String cpfInformado) throws Exception {
		
		cpfInformado = NeaStrings.somenteNumeros(cpfInformado);
		
		if ((cpfInformado == null) || (cpfInformado.trim().equals(""))) 
			throw new Exception("O campo CPF é obrigatório!");	
		
		String linkExterno = infrastructureController.getNaSistema().getLinkExterno();
		if (linkExterno == null || linkExterno.trim().equals("")) 
			throw new Exception("Informe o setor de TI que o link externo do sistema não está configurado!");
		
		NaUsuario naUsuario = buscaUsuarioCpfOrLogin(cpfInformado);
		
		if (naUsuario == null) 
			throw new Exception("O CPF informado não consta em nossa base de dados!");
		
		if (naUsuario.getEmail() == null || naUsuario.getEmail().trim().equals("")) 
			throw new Exception("O Usuario informado não possui e-mail cadastrado! - " + AtualizaBaseService.MENSAGEM_SETOR_RESPONSAVEL_USUARIOS);
		
		if (!Tools.validaEmail(naUsuario.getEmail())) 
			throw new Exception("O e-mail cadastrado não é válido! - " + AtualizaBaseService.MENSAGEM_SETOR_RESPONSAVEL_USUARIOS);
		
		recuperarSenha(naUsuario);		

	}
	
	
		

}
