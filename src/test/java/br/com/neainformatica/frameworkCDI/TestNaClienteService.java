package br.com.neainformatica.frameworkCDI;

import javax.persistence.EntityManager;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import test.TestTools;
import br.com.neainformatica.infrastructure.dao.NaClienteRepository;
import br.com.neainformatica.infrastructure.entity.NaCliente;
import br.com.neainformatica.infrastructure.services.NaClienteService;

public class TestNaClienteService {

	static EntityManager em;

	@BeforeClass
	public static void conecta() {
		em = TestTools.criaEntityManagerTestPostgre();
	}

	@AfterClass
	public static void fechaConexao() {
		em.close();
	}
	
	
	//@Test
	public void testSohPodeExistirApenasUmCliente(){
		
		NaClienteService service = new NaClienteService();
		NaClienteRepository dao = new NaClienteRepository();		
		service.setRepository(dao);		
		service.setDao(dao);
		dao.setEntityManager(this.em);
		
		NaCliente cliente = service.findAll().get(0);
		
		Assert.assertNotNull(cliente);		
	}
	
	

	//@Test
	public void testNaoPodeSalvarUmCLienteSemNome() {
		NaCliente cliente = criarClienteVirtual();
		cliente.setNome(null);
		
		NaClienteService service = new NaClienteService();
		NaClienteRepository dao = new NaClienteRepository();		
		service.setRepository(dao);		
		service.setDao(dao);
		dao.setEntityManager(this.em);
		
		service.save(cliente);
		
		//System.out.println("TestNaClienteService.testNaoPodeSalvarUmCLienteSemNome()");
		
		
	}
	

	private NaCliente criarClienteVirtual() {
		NaCliente cliente = new NaCliente();
		cliente.setNome("PREFEITURA MUNICIPAL DE MODELO");
		cliente.setCnpj("07700699000191");

		return cliente;
	}

}
