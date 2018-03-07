package br.com.neainformatica.infrastructure.produces;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
//
//@ApplicationScoped
public class EntityManagerProduces {
//
	@PersistenceContext(unitName = "PU")
	private EntityManager em;
//
	@Produces
	public EntityManager em() {
		return em;
	}
//
//	private EntityManagerFactory emf;
//
//	private void criarEntityManagerFactory() {
//		Properties props = new Properties();
//		props.setProperty("javax.persistence.jtaDataSource", "java:/" + getContexto() + "DS");
//		this.emf = Persistence.createEntityManagerFactory("PU", props);
	}
//
//	// @Produces
//	// public EntityManager criaEntityManager() {
//	//
//	// if (emf == null)
//	// criarEntityManagerFactory();
//	//
//	// return emf.createEntityManager();
//	// }
//
//	// public void dispose(@Disposes @Default EntityManager entityManager) {
//	// /*
//	// * if (entityManager.isOpen()) { entityManager.close(); }
//	// */
//	// }
//
//	private String getContexto() {
//		String url = "";
//		try {
//			url = EntityManagerProduces.class.getResource("").toString();
//			url = url.substring(0, url.indexOf(".war"));
//			String[] resultado = url.split("/");
//			return resultado[resultado.length - 1].toString();
//		} catch (Exception e) {
//			System.out.println("Não foi possivel Criar EntityManager através do diretório: " + url);
//			return "";
//		}
//	}
// }
