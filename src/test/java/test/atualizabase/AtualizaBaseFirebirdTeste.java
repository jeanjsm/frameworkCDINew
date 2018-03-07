package test.atualizabase;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import test.TestTools;
import br.com.neainformatica.infrastructure.dao.ToolsDBFirebird;
import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;

public class AtualizaBaseFirebirdTeste {

	static EntityManager em;

	@BeforeClass
	public static void conecta() {
		em = TestTools.criaEntityManagerTestFirebird();
		// em.getTransaction().begin();
		// em.createNativeQuery("create table atualiza_teste (id integer not null);").executeUpdate();
		// em.getTransaction().commit();
	}

	@AfterClass
	public static void fechaConexao() throws Exception {
		ToolsDBFirebird dbFiribird = new ToolsDBFirebird();
		em.getTransaction().begin();
		dbFiribird.dropTable(em, "", "atualiza_teste");
		em.getTransaction().commit();
		em.close();
	}

	@Test
	@Ignore
	public void deveVerificarExistenciaDeUmaTabela() {
		ToolsDBFirebird dbFiribird = new ToolsDBFirebird();

		boolean existTable = dbFiribird.existTable(em, "", "atualiza_teste");
		assertEquals(true, existTable);

		assertEquals(false, dbFiribird.existTable(em, "", "tabela_inexistente"));

	}

	@Test
	@Ignore
	public void deveApagarUmatabela() throws Exception {
		ToolsDBFirebird dbFirebird = new ToolsDBFirebird();
		em.getTransaction().begin();
		boolean dropTable = dbFirebird.dropTable(em, "", "atualiza_teste");
		assertEquals(true, dropTable);
		em.getTransaction().commit();
		em.getTransaction().begin();

		em.createNativeQuery("create table atualiza_teste (id integer not null);").executeUpdate();
		em.getTransaction().commit();

	}

	@Test
	@Ignore
	public void deveVerificarExistenciaDeUmaColuna() {
		ToolsDBFirebird dbFirebird = new ToolsDBFirebird();
		boolean existCollumn;
		existCollumn = dbFirebird.existCollumn(em, "", "atualiza_test", "id");
		assertEquals(false, existCollumn);

		existCollumn = dbFirebird.existCollumn(em, "", "atualiza_teste", "id");
		assertEquals(true, existCollumn);

		existCollumn = dbFirebird.existCollumn(em, "", "atualiza_teste", "id_teste");
		assertEquals(false, existCollumn);

	}

	@Test
	@Ignore
	public void deveAdicionarUmaColuna() {
		ToolsDBFirebird dbFirebird = new ToolsDBFirebird();

		try {
			em.getTransaction().begin();
			assertEquals(true, dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_integer", EnumTipoColumnBD.INTEGER, null));

			assertEquals(true, dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_numeric", EnumTipoColumnBD.NUMERIC, false, "", 15, 2));

			assertEquals(true, dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_texto", EnumTipoColumnBD.VARCHAR, 60, true, "texto_padrao"));

			assertEquals(true, dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_blobbinario", EnumTipoColumnBD.BLOB_BINARIO, 60, false, ""));

			assertEquals(true, dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_blobbinario", EnumTipoColumnBD.BLOB_BINARIO, 60, false, ""));

			assertEquals(true, dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_blotexto", EnumTipoColumnBD.BLOB_TEXTO, 60, false, ""));

			assertEquals(true, dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_date", EnumTipoColumnBD.DATE, 60, true, "2012-04-05"));

			assertEquals(true, dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_timestamp", EnumTipoColumnBD.TIMESTAMP, 60, false, ""));
			em.flush();
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().commit();
			throw new RuntimeException();
		} finally {

		}
	}

	@Test
	@Ignore
	public void deveRemoverUmCampoDaTabela() {
		ToolsDBFirebird dbFirebird = new ToolsDBFirebird();
		try {
			em.getTransaction().begin();
			dbFirebird.addCollumn(em, "", "atualiza_teste", "campo_integer", EnumTipoColumnBD.INTEGER, null);
			em.getTransaction().commit();
			em.getTransaction().begin();
			dbFirebird.dropCollumn(em, "", "atualiza_teste", "campo_integer");
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
			em.getTransaction().commit();
			throw new RuntimeException(e);
		}
	}

	@Test
	@Ignore
	public void deveAdicionarUmaPrimaryKey() throws Exception {
		ToolsDBFirebird dbFirebird = new ToolsDBFirebird();

		em.getTransaction().begin();
		boolean addPrimaryKey = dbFirebird.addPrimaryKey(em, "", "atualiza_teste", "aualiza_primary_key", "id");
		em.getTransaction().commit();

		em.getTransaction().begin();
		boolean addPrimaryKey2 = dbFirebird.addPrimaryKey(em, "", "atualiza_teste", "aualiza_primary_key", "id");
		em.getTransaction().commit();

		assertEquals(true, addPrimaryKey);
		assertEquals(false, addPrimaryKey2);
	}

	@Test
	@Ignore
	public void deveRemoverPrimaryKey() throws Exception {

		ToolsDBFirebird dbFirebird = new ToolsDBFirebird();
		em.getTransaction().begin();
				
		//assertEquals(true, dbFirebird.dropPrimaryKey(em, "", "atualiza_teste"));
		dbFirebird.dropPrimaryKey(em, "", "atualiza_teste");
		em.getTransaction().commit();
	}

	public AtualizaBaseFirebirdTeste() {
		// TODO Auto-generated constructor stub
	}

	@Test
	@Ignore
	public void deveRemoverForeingKey() {
		ToolsDBFirebird dbFiribird = new ToolsDBFirebird();
		// String[] campos = { "id_01", "id_02" };
		// dbFiribird.dropForeingKey(em, "", "teste_fk_02", "", "", campos);
		// String[] campos = { "id_01", "id_02" };
		try {
			em.getTransaction().begin();
			dbFiribird.dropForeingKey(em, "", "teste_fk_02", "", "", "id_01");
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void deveRemoverUniqueKey() {
		ToolsDBFirebird dbFiribird = new ToolsDBFirebird();
		try {
			em.getTransaction().begin();
			dbFiribird.dropUniqueKey(em, "", "TESTE_FK_01", "", "", "nome");

			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void deveAdiconarForeingKey() {
		ToolsDBFirebird dbFiribird = new ToolsDBFirebird();
		try {
			em.getTransaction().begin();
			dbFiribird.addForeingKey(em, "", "TESTE_FK_01", "FK_TESTE","" ,"TESTE_FK_02", "NOME", "NOME");
			em.getTransaction().commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore
	public void deveVerificarAexistenciaDoIndice() {
		ToolsDBFirebird dbFiribird = new ToolsDBFirebird();
		dbFiribird.existIndex(em, "", "", "FK_TESTE");
	}
	
	@Test
	public void carregarArquivosSql() {
	
		@SuppressWarnings("unused")
		File arquivos[];
		File diretorio = new File("C:\\Na_java\\workspace\\frameworkCDI\\src\\main\\webapp\\resources\\neainformatica\\scripts-atualiza-base");
		arquivos = diretorio.listFiles();
		
		Object object = arquivos[0];
		
		String valueOf = String.valueOf(object);
		
		try {
			FileInputStream inputStream = new FileInputStream(valueOf);
			
			String texto = IOUtils.toString(inputStream);
			//System.out.println(texto);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		

	}

}
