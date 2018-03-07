package test;

//import java.io.File;
//import java.lang.annotation.Annotation;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import junit.framework.JUnit4TestAdapter;
//import junit.framework.TestSuite;
//
//import org.jboss.arquillian.junit.Arquillian;
//import org.junit.runner.RunWith;
//import org.junit.runners.AllTests;

//@RunWith(AllTests.class)
//@SuppressWarnings({"unchecked", "rawtypes"})
public class JUnitRun {
	
//	public static TestSuite suite() {
//		TestSuite suite;
//		
//		System.setProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager");
//		
//		suite = new TestSuite("FrameworkCDI");
//		
//		for (Class classe: getClassesInPackage("test")) {
//			suite.addTest(new JUnit4TestAdapter(classe));
//		}
//		
//		return suite;
//	}
//	
//	private static List<Class> getClassesInPackage(String packageName) {
//		List<Class> lsClasses = new ArrayList<>();
//		List<File> lsArquivos = null;
//		File diretorio = null;
//		Class classe = null;
//		
//		try {			
//			diretorio = new File(Thread.currentThread()
//					.getContextClassLoader()
//					.getResource(packageName.replace(".", "/"))
//					.getFile());
//			
//			if (diretorio.isDirectory()) {
//				lsArquivos = Arrays.asList(diretorio.listFiles());
//				
//				for (File arquivo : lsArquivos) {					
//					if (arquivo.isDirectory()) {
//						for (Class c: getClassesInPackage(packageName + "." + arquivo.getName())) {
//							lsClasses.add(c);
//						}
//					} else {
//						if (arquivo.getName().endsWith(".class")) {
//							classe = Class.forName(packageName + "." + 
//									arquivo.getName().substring(0, arquivo.getName().lastIndexOf('.')));
//							
//							if (isArquillianTestClass(classe)) {
//								lsClasses.add(classe);
//							}							
//						}
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return lsClasses;
//	}
//	
//	private static boolean isArquillianTestClass(Class classe) {
//		Annotation runWith = classe.getAnnotation(RunWith.class);
//		
//		if (runWith instanceof RunWith) {
//			if (((RunWith) runWith).value().getName().equals(Arquillian.class.getName())) { 
//				return true;
//			}
//		}
//		
//		return false;
//	}
	
}