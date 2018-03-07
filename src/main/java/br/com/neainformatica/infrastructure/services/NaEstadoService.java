package br.com.neainformatica.infrastructure.services;
/**
-----------------------------------------------
   @Empresa: N&A Informática Ltda
   @Gerador: MultiSource 
      Dados da Classe:
	@Data  = 25/04/2014 16:40:09
	@Author  = NELSON
	@Versão da Classe = 
-----------------------------------------------
 */

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaEstadoRepository;
import br.com.neainformatica.infrastructure.entity.NaEstado;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;

public class NaEstadoService extends GenericService<NaEstado> implements NaServiceInterface, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	NaEstadoRepository dao;

	@Override
	public GenericRepository<NaEstado> getRepository() {
		return this.dao;
	}

	// apagar
	public void testarAgendamento() {
		System.out.println("NaEstadoService.testarAgendamento()");

		List<NaEstado> estados = findAll();

		for (NaEstado naEstado : estados) {
			System.out.println(naEstado);
		}

		System.out.println("fim do serviço");

	}

}
