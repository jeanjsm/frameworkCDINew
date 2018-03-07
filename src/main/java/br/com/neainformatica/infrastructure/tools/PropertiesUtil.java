package br.com.neainformatica.infrastructure.tools;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertiesUtil {
	
	private static final Log log = LogFactory.getLog(PropertiesUtil.class);
	
	public static Properties getProperties(String propertiesName) {
		
		Properties props = new Properties();
	
		try {			
			// lê os dados que estão no arquivo
			 
			props.load(PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesName));
		} catch (IOException ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
		}

		return props;

	}


}
