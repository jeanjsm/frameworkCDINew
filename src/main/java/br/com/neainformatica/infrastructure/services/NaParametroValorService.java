package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaParametroValorRepository;
import br.com.neainformatica.infrastructure.entity.NaParametro;
import br.com.neainformatica.infrastructure.entity.NaParametroValor;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaParametroValorService extends GenericService<NaParametroValor> implements NaServiceInterface, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NaParametroValorRepository dao;

	@Override
	public GenericRepository<NaParametroValor> getRepository() {
		return this.dao;
	}

	public NaParametroValor getValorByParametro(NaParametro parametro, String chave, NaSistema sistema) {
		return dao.getValorByParametro(parametro, chave, sistema);
	}

	public List<NaParametroValor> pesquisarParametroValor(NaSistema sistema, NaParametro parametro) {
		return dao.pesquisarParametroValor(sistema, parametro);
	}

	public NaParametroValor pesquisarParametroValorFixo(NaSistema sistema, NaParametro parametro) {
		return dao.pesquisarParametroValorFixo(sistema, parametro);
	}

	public NaParametroValor pesquisarParametroValorPorNome(String chave) {
		return dao.pesquisarParametroValorPorNome(chave);
	}

}
