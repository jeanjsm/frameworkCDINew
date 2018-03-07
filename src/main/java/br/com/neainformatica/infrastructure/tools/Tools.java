package br.com.neainformatica.infrastructure.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.Query;

import net.sf.jasperreports.engine.JasperReport;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.io.OutputFormat;
import org.hibernate.Session;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;

import br.com.neainformatica.infrastructure.auditoria.AuditoriaFirebird;
import br.com.neainformatica.infrastructure.auditoria.AuditoriaPostgres;
import br.com.neainformatica.infrastructure.auditoria.AuditoriaTools;
import br.com.neainformatica.infrastructure.converter.XStreamBigDecimalConverter;
import br.com.neainformatica.infrastructure.converter.XStreamDateTimeConverter;
import br.com.neainformatica.infrastructure.converter.XStreamEnumConverter;
import br.com.neainformatica.infrastructure.converter.XStreamStringConverter;
import br.com.neainformatica.infrastructure.dao.IToolsDB;
import br.com.neainformatica.infrastructure.dao.ToolsDBFirebird;
import br.com.neainformatica.infrastructure.dao.ToolsDBPostgre;
import br.com.neainformatica.infrastructure.enumeration.EnumFormatDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;

@Named
public class Tools implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(Tools.class);

	public static Object findById(Collection<?> collection, Integer idToFind) {
		for (Object obj : collection) {
			Integer id = ToolsReflection.getIdByReflection(obj);
			if (id.equals(idToFind))
				return obj;
		}

		return null;

	}

	/**
	 * Método utilizado para retornar um arquivo pelo servidor de aplicação e
	 * não pelo contexto.
	 * 
	 * @param pastaDoArquivoDepoisDaPastaDoProjeto
	 * @return File
	 */
	/*
	 * public static File retornaArquivoNoServidorDeAplicacao(String
	 * pastaDoArquivoDepoisDaPastaDoProjeto) {
	 * 
	 * String pathToClass =
	 * AtualizaBaseService.class.getResource("GenericService.class").toString();
	 * 
	 * String preparaCaminhoArquivo = ""; String[] strings =
	 * pathToClass.split("/WEB-INF/classes/" +
	 * AtualizaBaseService.class.getPackage().getName().replace(".", "/") +
	 * "/GenericService.class"); preparaCaminhoArquivo =
	 * strings[0].concat(pastaDoArquivoDepoisDaPastaDoProjeto);
	 * 
	 * String arquivo = preparaCaminhoArquivo.replace("vfs:/", ""); arquivo =
	 * arquivo.replace("/", "\\");
	 * 
	 * return new File(arquivo); }
	 */

	// public static Method findGetter(Object bean, String propertyName) {
	// Class<? extends Object> beanClass = bean.getClass();
	// return findGetter(beanClass, propertyName);
	// }

	/**
	 * Método utilizado para criptografar a senha do usuário antes de gravar no
	 * banco de dados
	 * 
	 * @param pTexto
	 * @return senha criptografada
	 * @author Elielcio
	 */
	public static String encripta(String pTexto) {

		String resultado = "";

		if (pTexto != null) {

			StringBuffer texto = removeSpaces(pTexto);

			if (texto != null) {
				for (int i = 0; i < texto.length(); i++) {
					resultado = resultado + doEncripta(texto.charAt(i), i + 1);
				}
			}
		}

		return resultado;

	}

	public static String doEncripta(Character letra, Integer iPos) {

		int iCalc = (int) letra;

		// Verifica se a posição é PAR ou IMPAR para saber se vai avançar
		// ou retroagir na tabela de caracteres...

		if ((iPos % 2) != 0) {

			// é impar decrementa...
			iCalc = iCalc - iPos;
		} else {
			// é par... incrementa...
			iCalc = iCalc + iPos;
		}
		// Certifica que está dentro da faixa...

		while (iCalc > 255)
			iCalc = iCalc - 255;

		while (iCalc < 1)
			iCalc = iCalc + 255;

		return (new Character((char) iCalc).toString());
	}

	/**
	 * Método utilizado para descriptografar a senha do usuário vinda do banco
	 * de dados
	 * 
	 * @param pTexto
	 * @return senhaDescriptografada
	 * @author Elielcio
	 */
	public static String decripta(String pTexto) {

		String resultado = "";

		if (pTexto != null) {

			StringBuffer texto = removeSpaces(pTexto);

			if (texto != null) {
				for (int i = 0; i < texto.length(); i++) {
					resultado = resultado + doDecripta(texto.charAt(i), i + 1);
				}
			}
		}

		return resultado;

	}

	public static JasperReport getSubReport(String subReport) {
		/*
		 * try { InputStream memoria = getClass().getResourceAsStream(
		 * "/resources/neainformatica/framework/relatorios/" + subReport);
		 * return (JasperReport) JRLoader.loadObject(memoria); } catch
		 * (JRException e) { e.printStackTrace(); return null; }
		 */

		return null;

	}

	/*
	 * public static JasperReport getSubReport(String subReport) {
	 * 
	 * InputStream memoria = getClass().getResourceAsStream(
	 * "/resources/neainformatica/framework/relatorios/" + subReport);
	 * 
	 * 
	 * String path = getRealPath(); InputStream memoria;
	 * 
	 * try { if (path.endsWith(".jar")) { memoria = new URL(path +
	 * "!/META-INF/resources/neainformatica/framework/relatorios/" +
	 * subReport).openStream(); } else { memoria = new URL(path +
	 * "src/main/webapp/resources/neainformatica/framework/relatorios/" +
	 * subReport).openStream(); } return (JasperReport)
	 * JRLoader.loadObject(memoria); } catch (Exception e) {
	 * e.printStackTrace(); return null; }
	 * 
	 * }
	 */

	public static String doDecripta(Character letra, Integer iPos) {
		int iCalc = (int) letra;

		// Verifica se a posição é PAR ou IMPAR para saber se vai avançar
		// ou retroagir na tabela de caracteres...
		if ((iPos % 2) != 0) {

			// é impar decrementa...
			iCalc = iCalc + iPos;
		} else {
			// é par... incrementa...
			iCalc = iCalc - iPos;
		}
		// Certifica que está dentro da faixa...
		while (iCalc > 255)
			iCalc = iCalc - 255;

		while (iCalc < 1)
			iCalc = iCalc + 255;

		return (new Character((char) iCalc).toString());
	}

	public static StringBuffer removeSpaces(String palavra) {

		StringBuffer texto = new StringBuffer();

		StringTokenizer token = new StringTokenizer(palavra);

		while (token.hasMoreTokens()) {
			texto.append(token.nextToken());
		}

		return texto;
	}

	/**
	 * Retorna a senha do dia de hoje no novo padrão criado para ser usada pelo
	 * usuário suporte
	 * 
	 * @param date
	 * @return
	 */
	public static String senhaDoDia() {
		return senhaDoDia(new Date());
	}

	/**
	 * Retorna a senha do dia da data passada como parametro no novo padrão
	 * criado para ser usada pelo usuário suporte
	 * 
	 * @param date
	 * @return
	 */
	public static String senhaDoDia(Date dataParametro) {

		Calendar data = Calendar.getInstance(new Locale("pt_BR"));
		data.setTime(dataParametro);

		int dia = data.get(Calendar.DAY_OF_MONTH);
		int mes = data.get(Calendar.MONTH) + 1;
		int ano = data.get(Calendar.YEAR);

		Integer calc1 = (dia * mes * ano) + 5;

		String calc2;

		if ((calc1 % 2) == 0) {
			calc2 = Tools.geraMD5(calc1 + "N");
		} else {
			calc2 = Tools.geraMD5(calc1 + "A");
		}

		String letrasHash = NeaStrings.somenteLetras(calc2.substring(15, calc2.length()));
		String letraValidacao;
		if (letrasHash.length() == 0) {
			letraValidacao = "Z";
		} else {
			letraValidacao = letrasHash.substring(0, 1);
		}

		String calc3 = letraValidacao.toUpperCase() + NeaStrings.somenteNumeros(calc2).substring(0, 6);

		return calc3;
	}

	/***
	 * Utilizado para retornar o TimeZone do servidor
	 * 
	 * @return timeZene
	 */
	public String getDefaultTimeZone() {
		return TimeZone.getDefault().getID();
	}

	/**
	 * Método utilizado para criar um objeto clone sem implementar Cloneable
	 * 
	 * @param classe
	 *            que implementa Serializable
	 * @return Object
	 */
	public static Object cloneSerializable(Serializable obj) {
		ObjectOutputStream out = null;
		ObjectInputStream in = null;

		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bout);

			out.writeObject(obj);
			out.close();

			ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray());
			in = new ObjectInputStream(bin);
			Object copy = in.readObject();

			in.close();

			return copy;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}

				if (in != null) {
					in.close();
				}
			} catch (IOException ignore) {
			}
		}

		return null;
	}

	/**
	 * @return retorna uma string com 16 bits
	 */
	public static String geraMD5(String texto) {
		try {
			String retorno;
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			BigInteger assinatura = new BigInteger(1, md5.digest(texto.getBytes()));

			retorno = assinatura.toString(16).toUpperCase();

			/*
			 * Em alguns casos o valor da assinatura como é bigInteger ele
			 * ignora os zeros a esquerda
			 */
			while (retorno.length() < 32) {
				retorno = '0' + retorno;
			}

			return retorno;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * @return retorna uma string com 16 bits
	 */
	public static String geraMD5(byte[] texto) {
		try {
			String retorno;
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			BigInteger assinatura = new BigInteger(1, md5.digest(texto));

			retorno = assinatura.toString(16).toUpperCase();

			/*
			 * Em alguns casos o valor da assinatura como é bigInteger ele
			 * ignora os zeros a esquerda
			 */
			while (retorno.length() < 32) {
				retorno = '0' + retorno;
			}

			return retorno;

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int modulo10(String numero) {
		int soma = 0;
		int digito = 0;

		for (int iNum = numero.length() - 1; iNum >= 0; iNum--) {
			digito = Integer.valueOf(String.valueOf(numero.charAt(iNum))) * ((iNum % 2) + 1);
			soma += digito > 9 ? digito - 10 + 1 : digito;
		}

		return (soma % 10) != 0 ? 10 - (soma % 10) : 0;
	}

	/**
	 * Método usado para obter o nome e sigla de um estado brasileiro.
	 * 
	 * @param siglaUF
	 *            - parâmetro passado para identificar o estado desejado.
	 * @return - retorna uma String contendo o nome e sigla de um estado.
	 */
	public static String obterNomeUf(String siglaUF) {
		HashMap<String, String> estados = new HashMap<String, String>();

		estados.put("AC", "AC - Acre");
		estados.put("AL", "AL - Alagoas");
		estados.put("AP", "AP - Amapá");
		estados.put("AM", "AM - Amazonas");
		estados.put("BA", "BA - Bahia");
		estados.put("CE", "CE - Ceará");
		estados.put("DF", "DF - Distrito Federal");
		estados.put("ES", "ES - Espírito Santo");
		estados.put("GO", "GO - Goiás");
		estados.put("MA", "MA - Maranhão");
		estados.put("MT", "MT - Mato Grosso");
		estados.put("MS", "MS - Mato Grosso do Sul");
		estados.put("MG", "MG - Minas Gerais");
		estados.put("PA", "PA - Pará");
		estados.put("PB", "PB - Paraíba");
		estados.put("PR", "PR - Paraná");
		estados.put("PE", "PE - Pernambuco");
		estados.put("PI", "PI - Piauí");
		estados.put("RJ", "RJ - Rio de Janeiro");
		estados.put("RN", "RN - Rio Grande do Norte");
		estados.put("RS", "RS - Rio Grande do Sul");
		estados.put("RO", "RO - Rondônia");
		estados.put("RR", "RR - Roraima");
		estados.put("SC", "SC - Santa Catarina");
		estados.put("SP", "SP - São Paulo");
		estados.put("SE", "SE - Sergipe");
		estados.put("TO", "TO - Tocantins");

		return estados.get(siglaUF);
	}

	/*
	 * Formata um número de inscrição de acordo com a máscara enviado por
	 * parâmetro.
	 * 
	 * Ex: valor = 1223333444455 máscara = 9.99.999.9999.99 saída =
	 * 1.22.333.4444.55
	 */
	public static String formatarInscricao(String valor, String mascara) {

		String dado = "";
		// remove caracteres nao numericos
		for (int i = 0; i < valor.length(); i++) {
			char c = valor.charAt(i);
			if (Character.isDigit(c)) {
				dado += c;
			}
		}
		int indMascara = mascara.length();
		int indCampo = dado.length();
		for (; indCampo > 0 && indMascara > 0;) {
			if (mascara.charAt(--indMascara) == '9') {
				indCampo--;
			}
		}
		String saida = "";
		for (; indMascara < mascara.length(); indMascara++) {
			saida += ((mascara.charAt(indMascara) == '9') ? dado.charAt(indCampo++) : mascara.charAt(indMascara));
		}
		return saida;
	}

	/**
	 * Converte um File para Array de Byte
	 * 
	 * @author elielcio.santos
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] fileToByte(File file) throws IOException {

		byte[] buffer = new byte[(int) file.length()];
		InputStream ios = null;
		try {
			ios = new FileInputStream(file);
			if (ios.read(buffer) == -1) {
				throw new IOException("EOF atingido ao tentar ler o arquivo");
			}
		} finally {
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
			}
		}

		return buffer;
	}

	/**
	 * Método para gera uma senha aleatória com a quantidade de digitos passados
	 * como parametro
	 * 
	 * @param qtdDigitosSenha
	 * @return senhaAleatoria
	 * @throws Exception
	 */
	public static String geraSenhaAleatoria(int qtdDigitosSenha) {
		// variável que armazena-rá a senha para o retorno
		StringBuffer senha = new StringBuffer("");

		// string que sera utilizada para geração da de senha aleatoria
		String letras = "abcdefghijklmnopqrstuvwxyz";
		String numeros = "0123456789";

		// itera a quantidade de digitos informada para metodo afim de gera a
		// senha aleatoria
		for (int i = 0; i < qtdDigitosSenha; i++) {
			// pega a posição aleatoriamente baseado num intervalo de 0 a 35,
			// pq a string letras tem 36 posições mas para pegar o char pela
			// posição o indice inicial é 0 e o final é 35
			if (i % 2 == 0) {
				int pos = new Random().nextInt(26);
				senha.append(letras.charAt(pos));
			} else {
				int pos = new Random().nextInt(10);
				senha.append(numeros.charAt(pos));
			}

		}
		return senha.toString();
	}

	/**
	 * Compacta o arquivo enviado como parametro
	 * 
	 * @param byteArray
	 * @param nomeArquivo
	 * @return
	 */
	public static byte[] zip(byte[] byteArray, String nomeArquivo) {
		ByteArrayOutputStream mem = null;
		ZipOutputStream zip = null;

		try {
			mem = new ByteArrayOutputStream();
			zip = new ZipOutputStream(mem);

			zip.putNextEntry(new ZipEntry(nomeArquivo));
			zip.write(byteArray);
			zip.closeEntry();
			zip.finish();
			zip.close();

			return mem.toByteArray();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Descompacta o arquivo enviado como parametro
	 * 
	 * @param byteArray
	 * @param nomeArquivo
	 * @return
	 */
	public static byte[] unzip(byte[] byteArray, String nomeArquivo) {
		ZipInputStream zip = null;
		ZipEntry zipItem = null;
		byte[] conteudo = null;

		try {
			zip = new ZipInputStream(new ByteArrayInputStream(byteArray));

			while ((zipItem = zip.getNextEntry()) != null) {
				if (zipItem.getName().equalsIgnoreCase(nomeArquivo)) {
					conteudo = IOUtils.toByteArray(zip);
				}
			}

			zip.close();
			return conteudo;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gera um objeto do tipo Xstream ja com os converter registrados e com a
	 * opção de utilizar XML ou JSON
	 * 
	 * @param json
	 * @return
	 */
	public static XStream getXStream(boolean json) {

		XStream xstream = null;

		if (json) {

			xstream = new XStream(new JettisonMappedXmlDriver());

		} else {

			Dom4JDriver dom = new Dom4JDriver();
			OutputFormat format = new OutputFormat();
			format.setEncoding("UTF-8");
			format.setNewLineAfterDeclaration(false);
			format.setNewlines(false);
			dom.setOutputFormat(format);

			xstream = new XStream(dom);
		}

		xstream.setMode(XStream.NO_REFERENCES);
		xstream.autodetectAnnotations(true);
		xstream.addDefaultImplementation(ArrayList.class, PersistentBag.class);
		xstream.addDefaultImplementation(Date.class, Timestamp.class);

		xstream.registerConverter(new NullConverter());
		xstream.registerConverter(new XStreamDateTimeConverter());
		xstream.registerConverter(new XStreamEnumConverter());
		xstream.registerConverter(new XStreamBigDecimalConverter());
		xstream.registerConverter(new XStreamStringConverter());

		return xstream;
	}
	
	/**
	 * Gera um objeto do tipo ObjectMapper já com os converter registrados 
	 */
	public static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule convertersModule = new SimpleModule();
		convertersModule.addSerializer(Date.class, new JsonConverterDateTime.serialize());
		convertersModule.addDeserializer(Date.class, new JsonConverterDateTime.deserialize());
		mapper.registerModule(convertersModule);
		return mapper;
	}

	/**
	 * Converte um enum em lista
	 * 
	 * @param enumType
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	static public List<Object> convertEnumToList(Class enumType) {
		List<Object> retorno = new ArrayList<Object>();
		if (enumType.isEnum()) {
			try {
				Class<?> cls = Class.forName(enumType.getCanonicalName());
				Object[] consts = cls.getEnumConstants();
				retorno.addAll(Arrays.asList(consts));
			} catch (ClassNotFoundException e) {
				log.debug("Classe não localizada " + enumType);
			}
		}
		return retorno;
	}

	public static Boolean validaEmail(String email) {
		Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$");
		Matcher m = p.matcher(email);
		if (m.find()) {
			return true;
		} else {
			return false;
		}
	}

	public static String stackTraceToString(Throwable e) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement element : e.getStackTrace()) {
			sb.append(element.toString());
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}

	public static String getUrlConexao(EntityManager entityManager) {
		Session s = null;
		SessionFactoryImplementor sfi = null;
		ConnectionProvider cp = null;
		Connection c = null;
		DatabaseMetaData dmd = null;

		try {
			if (entityManager != null) {
				s = (Session) entityManager.getDelegate();
				cp =  s.getSessionFactory().getSessionFactoryOptions().getServiceRegistry().getService(ConnectionProvider.class);
				c = cp.getConnection();
				dmd = c.getMetaData();
				return dmd.getURL();
			} else {
			}
		} catch (SQLException e) {
			log.error("Erro em getUrlConexao. EntityManager: " + entityManager.getProperties().toString());
		}

		return null;
	}

	public static IToolsDB identificaBanco(EntityManager entityManager) {

		String hibernateDialect = entityManager.getEntityManagerFactory().getProperties().get("hibernate.dialect").toString();

		if (entityManager != null) {

			if (hibernateDialect.toLowerCase().contains("postgre"))
				return new ToolsDBPostgre();

			if (hibernateDialect.toLowerCase().contains("firebird"))
				return new ToolsDBFirebird();
		}

		log.error("Erro ao identificar banco de dados. EntityManager: " + entityManager.getProperties().toString());

		return null;
	}

	public static void setarParametrosMapToQuery(Map<String, Object> paramMap, Query paramQuery) {
		if ((paramMap == null) || (paramMap.size() == 0)) {
			return;
		}
		Set localSet = paramMap.keySet();
		@SuppressWarnings("rawtypes")
		Iterator localIterator = localSet.iterator();
		while (localIterator.hasNext()) {
			String str = (String) localIterator.next();

			boolean existeParametro = false;
			for (Parameter<?> p : paramQuery.getParameters()) {
				if (p.getName().equals(str)) {
					existeParametro = true;
					break;
				}
			}

			if (existeParametro)
				paramQuery.setParameter(str, paramMap.get(str));
		}
	}

	public static AuditoriaTools identificaTipoBancoAuditoria(EntityManager entityManager) {

		String hibernateDialect = entityManager.getEntityManagerFactory().getProperties().get("hibernate.dialect").toString();

		if (entityManager != null) {

			if (hibernateDialect.toLowerCase().contains("postgre"))
				return new AuditoriaPostgres();

			if (hibernateDialect.toLowerCase().contains("firebird"))
				return new AuditoriaFirebird();
		}

		// log.error("Erro ao identificar banco de dados. EntityManager: " +
		// entityManager.getProperties().toString());

		return null;
	}
	
	public static String objectToString(Object obj) {

		if (obj == null)
			return null;

		StringBuilder retorno = new StringBuilder();

		Field[] field = FieldUtils.getAllFields(obj.getClass());

		for (Field f : field) {
			try {
				if (java.lang.reflect.Modifier.isStatic(f.getModifiers()))
					continue;

				String value;

				value = formataValue(f, obj);

				if (value != null && !value.trim().equals(""))
					retorno.append(f.getName() + ": " + value + " |");

			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		return retorno.toString();

	}
	
	private static String formataValue(Field field, Object obj) throws IllegalAccessException {

		Object valueField = FieldUtils.readDeclaredField(obj, field.getName(), true);

		if (valueField == null)
			return "";

		if (field.getType().getSimpleName().equalsIgnoreCase("Integer"))
			return ((Integer) valueField).toString();
		if (field.getType().getSimpleName().equalsIgnoreCase("String"))
			return (String) valueField;
		if (field.getType().getSimpleName().equalsIgnoreCase("Date"))
			return NeaDate.formatarData((Date) valueField, EnumFormatDate.DATE_TIME_HMS);
		if (field.getType().getSimpleName().equalsIgnoreCase("List"))
			return "";

		else
			return valueField.toString();

	}

}
