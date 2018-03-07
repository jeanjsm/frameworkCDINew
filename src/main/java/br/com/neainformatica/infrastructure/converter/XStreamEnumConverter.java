package br.com.neainformatica.infrastructure.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XStreamEnumConverter implements Converter {
	
	private static final Log log = LogFactory.getLog(XStreamEnumConverter.class);

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class classe) {
		return classe.isEnum();
	}

	@SuppressWarnings("rawtypes")
	public void marshal(Object objeto, HierarchicalStreamWriter writer, MarshallingContext context) {

		Class params[] = null;
		Method method = null;

		try {
			method = objeto.getClass().getDeclaredMethod("toChar", params);
		} catch (Exception e) {
			// log.debug("Não existe o método toChar");
		}

		try {
			if (method == null) {
				method = objeto.getClass().getDeclaredMethod("toInt", params);
			}
		} catch (Exception e) {
			// log.debug("Não existe o método toInt");
		}

		// Object obj = method.invoke(objeto, params);
		//String retorno = "";
		try {
			// retorno = (String) method.invoke(objeto);
			context.convertAnother(method.invoke(objeto));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			log.debug("Erro no enum " + objeto.getClass().toString());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			log.debug("Erro no enum " + objeto.getClass().toString());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			log.debug("Erro no enum " + objeto.getClass().toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("Erro no enum " + objeto.getClass().toString());
			log.debug("Verifique se o Enum possui o método toInt ou toChar");
		}

		// context.convertAnother(retorno);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		// http://grepcode.com/file/repo1.maven.org/maven2/org.jvnet.hudson/xstream/1.3.1-hudson-6/com/thoughtworks/xstream/converters/enums/EnumConverter.java
		Class type = context.getRequiredType();
		// TODO: There's no test case for polymorphic enums.
		if (type.getSuperclass() != Enum.class) {
			type = type.getSuperclass(); // polymorphic enums
		}
		String name = reader.getValue();
		try {
			return Enum.valueOf(type, name);
		} catch (IllegalArgumentException e) {

			Class params[] = new Class[1];
			Method method = null;
			ArrayList<Object> listParametros = new ArrayList<Object>();

			// todos os enum nossos tem o método valueOf, porém uns
			// são char outros são int, por isso tenho que testar os dois

			try {
				params[0] = char.class;
				method = type.getDeclaredMethod("valueOf", params);
				char valor = reader.getValue().charAt(0);
				listParametros.add(valor);
			} catch (Exception e2) {

			}

			// procurar o valueOf com int
			if (method == null) {
				try {
					params[0] = int.class;

					method = type.getDeclaredMethod("valueOf", params);

					int valor = Integer.parseInt(reader.getValue());
					listParametros.add(valor);
				} catch (Exception e2) {

				}
			}

			// procurar o valueOf com Integer
			if (method == null) {
				try {
					params[0] = Integer.class;

					method = type.getDeclaredMethod("valueOf", params);

					int valor = Integer.parseInt(reader.getValue());
					listParametros.add(valor);
				} catch (Exception e2) {

				}
			}

			if (method == null) {
				log.debug("Não foi encontrado o método valueOf no enum " + type.getClass().toString());
			} else {
				try {
					return method.invoke(type, listParametros.toArray());
				} catch (Exception e1) {
					log.debug("Erro ao chamar o método valueOf no enum " + type.getClass().toString());
					e.printStackTrace();
				}
			}

			// failed to find it, do a case insensitive match
			for (Enum c : (Enum[]) type.getEnumConstants())
				if (c.name().equalsIgnoreCase(name))
					return c;

			// all else failed
			throw e;
		}
	}

}