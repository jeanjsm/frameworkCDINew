package br.com.neainformatica.infrastructure.interfaces;

import br.com.neainformatica.infrastructure.dao.GenericRepository;

public interface NaServiceInterface {
	
	
	public GenericRepository<?> getRepository();

}
