package br.com.neainformatica.infrastructure.services;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaParametroGrupoRepository;
import br.com.neainformatica.infrastructure.dao.NaParametroRepository;
import br.com.neainformatica.infrastructure.entity.NaParametro;
import br.com.neainformatica.infrastructure.entity.NaParametroGrupo;
import br.com.neainformatica.infrastructure.entity.NaParametroValor;
import br.com.neainformatica.infrastructure.entity.NaSistema;
import br.com.neainformatica.infrastructure.exception.NeaException;
import br.com.neainformatica.infrastructure.interfaces.EnumParametroGrupoInterface;
import br.com.neainformatica.infrastructure.interfaces.EnumParametroInterface;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;
import br.com.neainformatica.infrastructure.tools.Auditoria;

public class NaParametroService extends GenericService<NaParametro> implements NaServiceInterface, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NaParametroRepository dao;

	@Inject
	private NaParametroValorService naParametroValorService;

	@Inject
	private NaSistemaService naSistemaService;

	@Inject
	private NaParametroGrupoService naParametroGrupoService;

	@Override
	public GenericRepository<NaParametro> getRepository() {
		return this.dao;
	}

	@Override
	public List<NaParametro> findAll() {
		return super.findAll();
	}

	public NaParametro getParametroByEnum(EnumParametroInterface enumParametro, Integer idSistema) {

		NaSistema sistema = naSistemaService.buscarSistema(idSistema);

		return dao.findByNome(enumParametro.getChave(), sistema);
	}

	public NaParametro getParametroByEnum(EnumParametroInterface enumParametro, NaSistema sistema) {
		return dao.findByNome(enumParametro.getChave(), sistema);
	}

	public NaParametroGrupo getGrupoByEnum(EnumParametroGrupoInterface enumParametroGrupo, NaSistema sistema) {
		return naParametroGrupoService.findByNome(enumParametroGrupo.getNome(), sistema);
	}

	public NaParametro getParametro(EnumParametroInterface enumParametro, NaSistema sistema) {
		NaParametro p = dao.findByNome(enumParametro.getChave(), sistema);

		if (p == null) {
			log.debug("Criando parametro : " + enumParametro.getChave());
			p = new NaParametro();
			p.setNome(enumParametro.getNome());
			p.setChave(enumParametro.getChave());
			p.setTipo(enumParametro.getTipo());
			p.setGrupo(getGrupo(enumParametro.getGrupo(), sistema));
			p.setNivelUsuario(enumParametro.getNivelUsuario());
			p.setSistema(sistema);
			p.setDescricao(enumParametro.getDescricao());
			p.setValorFixo(enumParametro.getValorFixo());
			try {
				p = noAudit().save(p);
				log.debug("Parametro : " + enumParametro.getChave() + "criado com sucesso... código: " + p.getId());
			} catch (NeaException e) {
				e.printStackTrace();
				return null;
			}
		}

		return p;
	}

	public NaParametroGrupo getGrupo(EnumParametroGrupoInterface enumParametroGrupo, NaSistema sistema) {
		NaParametroGrupo grupo;
		grupo = naParametroGrupoService.findByNome(enumParametroGrupo.getNome(), sistema);

		if (grupo == null) {
			try {
				grupo = new NaParametroGrupo();
				grupo.setNome(enumParametroGrupo.getNome());
				grupo.setNivelUsuario(enumParametroGrupo.getNivelUsuario());
				grupo.setSistema(sistema);

				grupo = naParametroGrupoService.noAudit().save(grupo);
			} catch (NeaException e) {
				e.printStackTrace();
				return null;
			}

		}
		return grupo;

	}

	public NaParametroValor getValorByParametro(NaParametro parametro, String chave, NaSistema sistema) {
		return naParametroValorService.getValorByParametro(parametro, chave, sistema);
	}

	public NaParametroValor getValorByParametro(EnumParametroInterface enumParametro, String chave, NaSistema sistema) {
		NaParametro parametro = getParametro(enumParametro, sistema);

		return naParametroValorService.getValorByParametro(parametro, chave, sistema);
	}

	public NaParametroValor getValorByParametro(EnumParametroInterface enumParametro, NaSistema sistema) {
		return getValorByParametro(enumParametro, "FIXO", sistema);
	}

	public NaParametroValor getValorByParametro(EnumParametroInterface enumParametro, Integer idSistema) {
		NaSistema sistema = naSistemaService.buscarSistema(idSistema);
		return getValorByParametro(enumParametro, "FIXO", sistema);
	}

	public NaParametroValor adicionarValorDefault(NaParametro parametro, String valorDefault, String chave) {
		try {
			log.debug("Criando valor do parâmetro : " + parametro.getChave());
			NaParametroValor pv = new NaParametroValor();
			pv.setParametro(parametro);
			pv.setChave(chave);
			pv.setValor(valorDefault);
			pv.setDescricao("Valor para chave: " + chave);

			pv = naParametroValorService.noAudit().save(pv);
			log.debug("Valor do parâmetro : " + parametro.getChave() + " criado com sucesso... código : " + pv.getId());

			return pv;
		} catch (NeaException e) {
			e.printStackTrace();
			return null;
		}
	}

	public NaParametroValor saveParametroValor(NaParametroValor pv) throws NeaException {
		return naParametroValorService.save(pv);
	}

	public List<NaParametro> pesquisarParametro(Integer grupo, String nome, NaSistema sistema) {
		return dao.pesquisarParametro(grupo, nome, sistema);
	}

	public List<NaParametroValor> pesquisarParametroValor(NaSistema sistema, NaParametro parametro) {
		return naParametroValorService.pesquisarParametroValor(sistema, parametro);
	}

	public Auditoria getAuditoria() {
		return dao.getAuditoria();
	}

}