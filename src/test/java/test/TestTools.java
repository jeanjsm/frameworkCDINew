package test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class TestTools {

	public static EntityManager criaEntityManagerTestPostgre() {

		EntityManagerFactory emf;

		Map<String, String> props = new HashMap<String, String>();
		props.put("javax.persistence.jdbc.url", "jdbc:postgresql://192.168.10.106:5432/framework");
		props.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
		props.put("javax.persistence.jdbc.user", "postgres");
		props.put("javax.persistence.jdbc.password", "postgres");
		props.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
		props.put("hibernate.show-sql", "true");
		props.put("hibernate.format_sql", "true");
		props.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
		props.put("javax.persistence.transactionType", "RESOURCE_LOCAL");

		emf = Persistence.createEntityManagerFactory("PU-test", props);

		return emf.createEntityManager();

	}

	public static EntityManager criaEntityManagerTestFirebird() {

		EntityManagerFactory emf;

		Map<String, String> props = new HashMap<String, String>();
		props.put("javax.persistence.jdbc.url", "jdbc:firebirdsql:192.168.10.106/3050:D:/Bases/Bataguassu/TRIBUTACAO.FDB");
		props.put("javax.persistence.jdbc.driver", "org.firebirdsql.jdbc.FBDriver");
		props.put("javax.persistence.jdbc.user", "SYSDBA");
		props.put("javax.persistence.jdbc.password", "nakey");
		props.put("hibernate.dialect", "br.com.neainformatica.infrastructure.dao.FirebirdDialect");
		props.put("hibernate.ejb.interceptor", "br.com.neainformatica.infrastructure.dao.HibernateInterceptor");
		props.put("hibernate.show-sql", "true");
		props.put("hibernate.format_sql", "true");
		props.put("javax.persistence.provider", "org.hibernate.jpa.HibernatePersistenceProvider");
		props.put("javax.persistence.transactionType", "RESOURCE_LOCAL");

		emf = Persistence.createEntityManagerFactory("PU-test", props);
		return emf.createEntityManager();
	}

}
