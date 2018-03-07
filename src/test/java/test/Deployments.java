package test;

//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.eu.ingwar.tools.arquillian.extension.suite.annotations.ArquillianSuiteDeployment;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.WebArchive;
//import org.jboss.shrinkwrap.resolver.api.maven.Maven;

//@ArquillianSuiteDeployment
public class Deployments {
	
//	@Deployment
//	public static WebArchive createTestArchive() {
//		WebArchive war = null;		
//		File[] jars = null;
//		List<String> lsDependency = null;
//		
//		try {
//			war = ShrinkWrap.create(WebArchive.class, "appteste.war");
//			war.addPackages(true, "br.com.neainformatica.infrastructure");
//			war.addPackages(true, "test");
//			war.addAsResource("META-INF/beans.xml");
//			war.addAsResource("META-INF/persistence.xml");
//			
//			lsDependency = new ArrayList<>();
//			lsDependency.add("org.picketlink:picketlink-api:2.5.2.Final");
//			lsDependency.add("org.picketlink:picketlink-common:2.5.2.Final");
//			lsDependency.add("org.picketlink:picketlink-idm-api:2.5.2.Final");
//			lsDependency.add("org.picketlink:picketlink-idm-impl:2.5.2.Final");
//			lsDependency.add("org.picketlink:picketlink-impl:2.5.2.Final");
//			
//			jars = Maven.resolver().loadPomFromFile("pom.xml")
//					.importCompileAndRuntimeDependencies()				
//					.resolve(lsDependency)
//					.withoutTransitivity()
//					.asFile();
//			
//			war.addAsLibraries(jars);			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return war;
//    }

}
