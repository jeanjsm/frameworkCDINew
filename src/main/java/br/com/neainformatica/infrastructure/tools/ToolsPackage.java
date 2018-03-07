package br.com.neainformatica.infrastructure.tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;

public class ToolsPackage {

	public static List<Class<?>> listClassesInPackage(String packageName, String paramClassName) throws ClassNotFoundException, IOException {

		Reflections r = new Reflections(packageName, new SubTypesScanner(false), ClasspathHelper.forClassLoader());

		Set<Class<?>> classes = r.getSubTypesOf(Object.class);

		List<Class<?>> retorno = new ArrayList<Class<?>>();

		for (Class<?> className : classes) {
			if (className.getName().contains(paramClassName)) {
				retorno.add(className);
			}
		}

		return (List<Class<?>>) new ArrayList<Class<?>>(classes);

	}

}
