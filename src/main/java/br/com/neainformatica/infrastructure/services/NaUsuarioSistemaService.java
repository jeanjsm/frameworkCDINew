package br.com.neainformatica.infrastructure.services;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaUsuarioSistemaRepository;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.exception.NeaException;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Created by rodolpho.sotolani on 20/07/2017.
 */
public class NaUsuarioSistemaService extends GenericService<NaUsuarioSistema> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private NaUsuarioSistemaRepository dao;

    @Override
    public GenericRepository<NaUsuarioSistema> getRepository() {
        return dao;
    }

    public void setRepository(NaUsuarioSistemaRepository repository) {
    	dao = repository;
    }
    
    public List<NaCliente> buscarClientesDoUsuario(NaUsuario naUsuario, NaSistema naSistema) {
        return dao.buscarClientesDoUsuario(naUsuario, naSistema);
    }

    public List<NaUsuario> buscarUsuariosSistema(Integer idSistema) {
        return dao.buscarUsuariosSistema(idSistema);
    }

    public NaUsuarioSistema buscaUsuarioSistema(NaUsuario usuario, NaSistema sistema, NaCliente cliente) {
        return dao.buscaUsuarioSistema(usuario, sistema, cliente);
    }

    public List<NaUsuarioSistema> buscarSistemasUsuario(NaUsuario usuario, NaCliente cliente) {
        return dao.buscarSistemasUsuario(usuario, cliente);
    }

    public List<NaUsuarioSistema> buscarSistemasUsuario(NaUsuario usuario) {
        return dao.buscarSistemasUsuario(usuario);
    }

    public NaUsuarioSistema obterUsuarioSistema(Integer idNaSistema, Integer idNaUsuario, Integer idNaCliente, EnumSimNao ativo) {
        return dao.obterUsuarioSistema(idNaSistema, idNaUsuario, idNaCliente, ativo);
    }

    public List<NaSistema> obterSistemaDoUsarioLogado(NaUsuario usuario, NaCliente cliente) {
        return dao.obterSistemaDoUsarioLogado(usuario, cliente);
    }

    public List<NaUsuarioSistema> buscarSistemasDoUsuario(NaUsuario usuario, NaCliente cliente) {
        return dao.buscarSistemasDoUsuario(usuario, cliente);
    }

    public NaUsuarioSistema obterUsuarioSistema(Integer codigoSistema, Integer codigoUsuario, NaCliente cliente) {
        return dao.obterUsuarioSistema(codigoSistema, codigoUsuario, cliente);
    }

    public EnumNivelUsuario obtemNivelMaximoAcessoUsuarioSistema(NaUsuario usuario) {
        return dao.obtemNivelMaximoAcessoUsuarioSistema(usuario);
    }

    public List<NaSistema> obtemSistemasQueUsuarioNaoPossuiAcesso(NaUsuario usuario) {
        return dao.obtemSistemasQueUsuarioNaoPossuiAcesso(usuario);
    }

    public List<NaUsuarioSistema> buscarListaPorSistemaCliente(Integer idSistema, Integer idCliente) {
        return dao.buscarListaPorSistemaCliente(idSistema, idCliente);
    }

    public void salvarSqlNativo(NaUsuarioSistema usuarioSistema) throws NeaException {
    	// TODO Auto-generated method stub
    	this.dao.salvarSqlNativo(usuarioSistema);
    }

    }
