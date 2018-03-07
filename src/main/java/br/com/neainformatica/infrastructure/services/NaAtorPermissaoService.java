package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaAtorPermissaoRepository;
import br.com.neainformatica.infrastructure.entity.NaAtor;
import br.com.neainformatica.infrastructure.entity.NaAtorPermissao;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaAtorPermissaoService extends GenericService<NaAtorPermissao> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaAtorPermissaoRepository dao;

	@Override
	public GenericRepository<NaAtorPermissao> getRepository() {
		return this.dao;
	}

	public List<NaAtorPermissao> buscaAtorPermissao(NaAtor ator, NaCliente naCliente) {
		return dao.buscaAtorPermissao(ator, naCliente);
	}

	public List<NaAtorPermissao> buscaAtorPermissao(Integer idNaAtor, Integer idNaCliente) {
		return dao.buscaAtorPermissao(idNaAtor, idNaCliente);
	}

	public List<NaAtorPermissao> buscaAtorPermissao(List<NaAtor> atores, NaCliente naCliente) {
		return dao.buscaAtorPermissao(atores, naCliente);
	}

	public List<NaAtorPermissao> buscaPermissao(String chavePermissao, NaAtor ator, NaSistema sistema) {
		return dao.buscaPermissao(chavePermissao, ator, sistema);
	}

	public List<String> formatarPermissoesParaRole(List<NaAtorPermissao> atorPermissao) {
		List<String> permissoes = new ArrayList<String>();

		for (NaAtorPermissao ap : atorPermissao) {

			if (ap.getPermissao().getAtivo() == EnumSimNao.SIM) {

				if (ap.getAcessar() == EnumSimNao.SIM)
					permissoes.add(ap.getPermissao().getChave() + "-ACESSAR");

				if (ap.getInserir() == EnumSimNao.SIM)
					permissoes.add(ap.getPermissao().getChave() + "-INSERIR");

				if (ap.getAlterar() == EnumSimNao.SIM)
					permissoes.add(ap.getPermissao().getChave() + "-ALTERAR");

				if (ap.getExcluir() == EnumSimNao.SIM)
					permissoes.add(ap.getPermissao().getChave() + "-EXCLUIR");

			}

		}

		return permissoes;
	}

	public List<NaAtorPermissao> buscarListaPorAtorClienteNivelUsuario(Integer idNaAtor, Integer idNaCliente, EnumNivelUsuario nivelUsuario){
		List<NaAtorPermissao> listaNaAtorPermissao = buscaAtorPermissao(idNaAtor, idNaCliente);
		//Se o Usuario tiver nivel MAIOR que Suporte
		if (nivelUsuario.toInt() >= EnumNivelUsuario.SUPORTE.toInt())
			return listaNaAtorPermissao;

		List<NaAtorPermissao> listaRetorno = new ArrayList<>();
		//Se a Permissao tiver nivel MENOR que Suporte
		for (NaAtorPermissao atorPermissao : listaNaAtorPermissao) {
			if ((atorPermissao.getPermissao().getNivelUsuario() == null)
					|| (atorPermissao.getPermissao().getNivelUsuario().toInt() < EnumNivelUsuario.SUPORTE.toInt())){
				listaRetorno.add(atorPermissao);
			}
		}
		return listaRetorno;
	}

}
