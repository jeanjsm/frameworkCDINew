package br.com.neainformatica.infrastructure.services;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaAtorRepository;
import br.com.neainformatica.infrastructure.entity.*;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoAtor;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NaAtorService extends GenericService<NaAtor> implements NaServiceInterface, Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private NaAtorRepository dao;

    @Inject
    private NaAtorPermissaoService atorPermissaoService;

    @Inject
    private NaPermissaoService permissaoService;

    @Inject
    private InfrastructureController infrastructureController;

    @Inject
    private NaSessionController naSessionController;

    @Override
    public GenericRepository<NaAtor> getRepository() {
        return this.dao;
    }

    public List<NaAtor> buscarAtoresSistema(NaSistema sistema) {
        return dao.buscarAtoresSistema(sistema);
    }

    @Override
    public NaAtor save(NaAtor ator) throws NeaException {
        NaAtor retorno = super.save(ator);

        // o ator é novo
        if (ator.getId() == null || ator.getId() == 0)
            criarPermissoesNovoAtor(ator, infrastructureController.getNaSistema());

        return retorno;
    }

    /**
     * Busca os naAtor de acordo com o tipo passado por parâmetro
     *
     * @return
     */
    public List<NaAtor> buscarAtores(EnumTipoAtor tipoAtor) {
        return dao.buscarAtores(tipoAtor);
    }

    /**
     * busca todos os atores relacionados ao usuário passado como parâmetro. O
     * ator pode ser um usuário ou perfil
     *
     * @param usuario
     * @return
     */
    public List<NaAtor> buscarAtoresUsuario(NaUsuario usuario, NaSistema sistema) {
        return dao.buscarAtoresUsuario(usuario, sistema);
    }

    /**
     * Atualizo todas as permissões do ator de acordo com as permissões do
     * sistema passado como parâmetro
     *
     * @param ator
     * @param sistema
     * @throws NeaException
     */
    public void atualizarPermissoes(NaAtor ator, NaSistema sistema, NaCliente naCliente) throws NeaException {
        List<NaPermissao> permissoesSistema = buscarPermissoesSistema(sistema, naCliente);
        List<NaAtorPermissao> atorPermissoes = buscaAtorPermissao(ator, naCliente);

        for (NaPermissao p : permissoesSistema) {

            boolean encontrou = false;

            for (NaAtorPermissao naAtorPermissao : atorPermissoes) {
                if (naAtorPermissao.getPermissao().equals(p)) {
                    encontrou = true;
                    break;
                }
            }
            if (!encontrou)
                atorPermissaoService.save(new NaAtorPermissao(ator, p, naCliente));
        }

    }

    /**
     * Retorna uma lista de permissões do ator passado como parâmetro
     *
     * @return
     */
    @Deprecated
    public List<NaAtorPermissao> buscaAtorPermissao(NaAtor ator, NaCliente naCliente) {
        return atorPermissaoService.buscaAtorPermissao(ator, naCliente);
    }

    public NaAtor buscarAtorAdm(Integer idAtor) {
        return dao.buscarAtorAdm(idAtor);
    }

    public List<NaAtorPermissao> buscaNovaAtorPermissao(NaAtor ator, NaSistema sistema) {

        List<NaAtorPermissao> AtorPermissao = new ArrayList<>();
        for (NaPermissao permissao : buscarPermissoesSistema(sistema, null)) {

            NaAtorPermissao ap = new NaAtorPermissao(ator, permissao, naSessionController.getNaCliente());

            AtorPermissao.add(ap);
        }

        return AtorPermissao;

    }

    /**
     * Retorna uma lista de permissões considerando todos os atores passados
     * como parametros
     *
     * @param atores
     * @return
     */
    @Deprecated
    public List<NaAtorPermissao> buscaAtorPermissao(List<NaAtor> atores, NaCliente cliente) {
        return atorPermissaoService.buscaAtorPermissao(atores, cliente);
    }

    /**
     * As roles de acesso são definidas de acordo com a tabela NaAtorPermissao,
     * só que o controle é feito por Strings então preciso converter a lista
     *
     * @param atorPermissao
     * @return
     */
    @Deprecated
    public List<String> formatarPermissoesParaRole(List<NaAtorPermissao> atorPermissao) {
        return atorPermissaoService.formatarPermissoesParaRole(atorPermissao);
    }

    public List<String> permissoesAdmParaRole(List<NaPermissao> permissaoAdm) {

        List<String> permissoesAdm = new ArrayList<String>();

        for (NaPermissao nap : permissaoAdm) {

            if (nap.getAtivo() == EnumSimNao.SIM) {
                permissoesAdm.add(nap.getChave() + "-ACESSAR");
                permissoesAdm.add(nap.getChave() + "-INSERIR");
                permissoesAdm.add(nap.getChave() + "-ALTERAR");
                permissoesAdm.add(nap.getChave() + "-EXCLUIR");

            }

        }

        return permissoesAdm;
    }

    /**
     * Quando um novo ator é cadastrado preciso adicionar as permissões
     * existentes para ele
     *
     * @param ator
     * @param sistema
     * @throws NeaException
     */
    public void criarPermissoesNovoAtor(NaAtor ator, NaSistema sistema) throws NeaException {

        List<NaPermissao> permissoes = buscarPermissoesSistema(sistema, null);
        for (NaPermissao naPermissao : permissoes) {

            atorPermissaoService.save(new NaAtorPermissao(ator, naPermissao, naSessionController.getNaCliente()));

        }

    }

    /**
     * Busca todas as permissões cadastradas para o sistema passado como
     * parâmetro
     *
     * @param sistema
     * @param naCliente
     * @return
     */
    @Deprecated
    public List<NaPermissao> buscarPermissoesSistema(NaSistema sistema, NaCliente naCliente) {
        return permissaoService.buscarPermissoesSistema(sistema, naCliente);
    }

    @Deprecated
    public NaAtorPermissao saveAtorPermissao(NaAtorPermissao naAtorPermissao) throws NeaException {
        return atorPermissaoService.save(naAtorPermissao);
    }

    /**
     * Retorna a lista de atores do tipo perfil do sistema passado como
     * parametro juntamente com a lista de todos os usuários que possuem acesso
     * a este sistema
     */
    public List<NaAtor> buscarAtoresPermissao(NaSistema sistema, NaCliente cliente) {
        return dao.buscarAtoresPermissao(sistema, cliente);
    }

    /**
     * Retorna a lista de atores que são do tipo Perfil para o sistema passado
     * como parâmetro
     */
    public List<NaAtor> buscarAtoresPerfilBySistema(NaSistema sistema, NaCliente cliente) {
        return dao.buscarAtoresPerfilBySistema(sistema, cliente);
    }

    /**
     * Retorna a lista de atores que são do tipo Usuario para o sistema passado
     * como parâmetro
     */
    public List<NaAtor> buscarAtoresUsuarioBySistema(NaSistema sistema, NaCliente cliente) {
        return dao.buscarAtoresUsuarioBySistema(sistema, cliente);
    }

    public List<NaAtor> buscarListaAtorPorSistemaCliente(NaSistema naSistema, NaCliente naCliente){
        List<NaAtor> listaAtores = new ArrayList<>();
        listaAtores.addAll(buscarAtoresPerfilBySistema(naSistema, naCliente));
        listaAtores.addAll(buscarAtoresUsuarioBySistema(naSistema, naCliente));
        return listaAtores;
    }
}
