//package test.atualizabase;
//
//import static org.junit.Assert.assertEquals;
//
//import javax.persistence.EntityManager;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import test.TestTools;
//import br.com.neainformatica.infrastructure.dao.ToolsDBPostgre;
//import br.com.neainformatica.infrastructure.enumeration.EnumTipoColumnBD;
//
//public class AtualizaBasePostgresqlTeste {
//
//	static EntityManager em;
//
//	@BeforeClass
//	public static void conecta() {
//		em = TestTools.criaEntityManagerTestPostgre();
//		em.getTransaction().begin();
//		em.createNativeQuery("create table atualiza_teste (id integer)").executeUpdate();
//		em.getTransaction().commit();
//	}
//
//	@AfterClass
//	public static void fechaConexao() {
//		ToolsDBPostgre dbPostgres = new ToolsDBPostgre();
//		em.getTransaction().begin();
//		dbPostgres.dropTable(em, "public", "atualiza_teste");
//		em.getTransaction().commit();
//		em.close();
//	}
//
//	@Test
//	public void deveVerificarAExistenciaDeUmaTabela() {
//		ToolsDBPostgre dbPostgres = new ToolsDBPostgre();
//		boolean adicionouColuna = dbPostgres.existTable(em, "public", "atualiza_teste");
//		assertEquals(true, adicionouColuna);
//	}
//
//	@Test
//	public void deveDeletarUmaTabela() {
//		ToolsDBPostgre dbPostgre = new ToolsDBPostgre();
//		em.getTransaction().begin();
//		assertEquals(true, dbPostgre.dropTable(em, "public", "atualiza_teste"));
//		em.getTransaction().commit();
//		
//		em.getTransaction().begin();
//
//		em.createNativeQuery("create table atualiza_teste (id integer)").executeUpdate();
//		em.getTransaction().commit();
//		
//	}
//
//	@Test
//	public void deveAdicionarUmaColuna() {
//		ToolsDBPostgre dbPostgre = new ToolsDBPostgre();
//
//		try {
//			em.getTransaction().begin();
//			assertEquals(true, dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_integer", EnumTipoColumnBD.INTEGER, null));
//			
//			assertEquals(true, dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_numeric", EnumTipoColumnBD.NUMERIC, false, "", 15, 2));
//
//			assertEquals(true, dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_texto", EnumTipoColumnBD.VARCHAR, 60, true, "texto_padrao"));
//
//			assertEquals(true, dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_blobbinario", EnumTipoColumnBD.BLOB_BINARIO, 60, false, ""));
//
//			assertEquals(true, dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_blobbinario", EnumTipoColumnBD.BLOB_BINARIO, 60, false, ""));
//
//			assertEquals(true, dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_blotexto", EnumTipoColumnBD.BLOB_TEXTO, 60, false, ""));
//
//			assertEquals(true, dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_date", EnumTipoColumnBD.DATE, 60, true, "2012-04-05"));
//
//			assertEquals(true, dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_timestamp", EnumTipoColumnBD.TIMESTAMP, 60, false, ""));
//			em.flush();
//			em.getTransaction().commit();
//		} catch (Exception e) {
//			e.printStackTrace();
//			em.getTransaction().commit();
//			throw new RuntimeException();
//		} finally {
//
//		}
//	}
//	@Test
//	public void deveDeletarUmaColunaExistente() throws Exception {
//		ToolsDBPostgre dbPostgre = new ToolsDBPostgre();
//		em.getTransaction().begin();
//		dbPostgre.addCollumn(em, "public", "atualiza_teste", "campo_numeric", EnumTipoColumnBD.NUMERIC, false, "", 15, 2);
//		em.getTransaction().commit();
//		em.getTransaction().begin();
//		boolean excluiuColuna = dbPostgre.dropCollumn(em, "public", "atualiza_teste", "campo_numeric");
//		em.getTransaction().commit();
//		assertEquals(true, excluiuColuna);
//	}
//
//}
