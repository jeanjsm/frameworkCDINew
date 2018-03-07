package br.com.neainformatica.infrastructure.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ToolsFile {

	private static final Log log = LogFactory.getLog(ToolsFile.class);

	/**
	 * @param path
	 * @return object file or null when fail
	 */
	public static File newFile(String path) {
		if (path != null) {
			File file = new File(path);

			return file;
		}
		return null;
	}

	public static Boolean deleteAllFilesByTime(String path, long miliseconds) {
		try {
			File[] listFiles = newFile(path).listFiles();
			long currentMilisecond = System.currentTimeMillis();

			for (File file : listFiles) {
				if ((currentMilisecond - file.lastModified() >= miliseconds)) {
					ToolsFile.deleteDirectoryRecursive(file);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param path
	 * @return true when is a directory
	 */
	public static Boolean isDirectory(String path) {
		if (path != null) {
			return newFile(path).isDirectory();
		}
		return false;
	}

	public static Boolean createDirectory(String path) {
		if (isDirectory(path) || isFile(path)) {
			return false;
		} else {
			if (!newFile(path).getParentFile().isDirectory()) {
				if (createDirectory(newFile(path).getParent())) {
					newFile(path).mkdir();
					return true;
				}
				return false;
			} else {
				newFile(path).mkdir();

				return true;
			}

		}
	}

	public static Boolean deleteDirectory(String path) {
		if (isDirectory(path)) {
			return newFile(path).delete();
		} else {
			return false;
		}
	}

	public static Boolean deleteDirectoryRecursive(String path) {
		if (isDirectory(path)) {
			File file = newFile(path);

			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectoryRecursive(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}

			return file.delete();
		} else {
			return false;
		}
	}

	public static Boolean deleteDirectoryRecursive(File file) {
		if (file.isDirectory()) {
			String[] children = file.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectoryRecursive(new File(file, children[i]));
				if (!success) {
					return false;
				}
			}

			return file.delete();
		} else if (file.isFile()) {
			return file.delete();
		} else {
			return false;
		}

	}

	public static Boolean zipDirectory(String path) {

		if (isDirectory(path)) {

			File file = new File(path);
			File zipFile = new File(path + ".zip");

			ZipOutputStream out;
			try {
				out = new ZipOutputStream(new FileOutputStream(zipFile));
				Stack<File> parentDirs = new Stack<File>();
				zipFiles(parentDirs, file.listFiles(), out);
				out.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		return false;
	}

	protected static void zipFiles(Stack<File> parentDirs, File[] files, ZipOutputStream out) throws IOException {
		byte[] buf = new byte[1024];

		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				// se a entrad é um diretório, empilha o diretório e chama o
				// mesmo método recursivamente
				parentDirs.push(files[i]);
				zipFiles(parentDirs, files[i].listFiles(), out);

				// após processar as entradas do diretório, desempilha
				parentDirs.pop();
			} else {
				FileInputStream in = new FileInputStream(files[i]);

				// itera sobre os itens da pilha para montar o caminho completo
				// do arquivo
				String path = "";
				for (File parentDir : parentDirs) {
					path += parentDir.getName() + "/";
				}

				// grava os dados no arquivo zip
				out.putNextEntry(new ZipEntry(path + files[i].getName()));

				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}

				out.closeEntry();
				in.close();
			}
		}
	}

	public static Boolean isFile(String path) {
		if (path != null) {
			return newFile(path).isFile();
		}
		return false;
	}

	public static Boolean createFile(String path) {
		if (!isFile(path)) {
			if (!isDirectory(newFile(path).getParent())) {
				createDirectory(newFile(path).getParent());
			}

			try {
				return newFile(path).createNewFile();
			} catch (Exception e) {
				return false;
			}
		}
		return false;
	}

	public static Boolean deleteFile(String path) {
		if (isFile(path)) {
			return newFile(path).delete();
		}
		return false;
	}

	public static Boolean writeFile(String path, String data) {
		Boolean canWrite = isFile(path);
		if (!canWrite) {
			canWrite = createFile(path);
		}

		if (canWrite) {

			FileWriter fw;
			try {
				fw = new FileWriter(newFile(path), true);

				fw.write(data);
				fw.close();

				return true;

			} catch (IOException e) {
				e.printStackTrace();
				return false;

			}

		}
		return false;

	}

	public static Boolean writeFile(String path, byte[] data) {
		Boolean canWrite = isFile(path);
		if (!canWrite) {
			canWrite = createFile(path);
		}

		if (canWrite) {
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(newFile(path));
				fos.write(data);
				fos.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;

	}

	public static BufferedReader getBufferedReader(String path) {

		if (isFile(path)) {
			File file = newFile(path);

			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
				return bufferedReader;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;

	}

	public static String readFile(String path) {
		BufferedReader bufferedReader = getBufferedReader(path);
		String result = "";
		if (bufferedReader != null) {
			try {
				while (bufferedReader.ready()) {
					result += bufferedReader.readLine() + "\n";
				}

			} catch (IOException e) {
				e.printStackTrace();
				result = null;
			} finally {

				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			result = null;
		}
		return result;
	}

	public static List<byte[]> readFileAsByteArray(String path) {
		List<byte[]> result = new ArrayList<byte[]>();
		byte[] buf = new byte[1024];

		FileInputStream in = null;
		try {
			in = new FileInputStream(newFile(path));
			int len;

			while ((len = in.read(buf)) != -1) {
				result.add(Arrays.copyOfRange(buf, 0, len));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public static List<String> readFileAsList(String path) {
		BufferedReader bufferedReader = getBufferedReader(path);
		List<String> result = new ArrayList<String>();
		if (bufferedReader != null) {
			try {
				while (bufferedReader.ready()) {
					result.add(bufferedReader.readLine());
				}
				return result;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/*
	 * public static String readFile(String path) { Boolean canWrite =
	 * isFile(path);
	 * 
	 * 
	 * if (canWrite) {
	 * 
	 * FileWriter fw; try { fw = new FileWriter(newFile(path), true);
	 * 
	 * fw.write(data); fw.close();
	 * 
	 * return true;
	 * 
	 * } catch (IOException e) { e.printStackTrace(); return false;
	 * 
	 * }
	 * 
	 * } return null;
	 * 
	 * }
	 */
	public static String directoryFromProperties(String propertiesName, String property) {
		Properties props = new Properties();
		try {
			ClassLoader classLoader = ClassLoader.getSystemClassLoader();

			InputStream in = classLoader.getResourceAsStream(propertiesName);

			if (in == null) {
				return null;
			}

			props.load(in);
			in.close();

		} catch (IOException ex) {
			log.debug(ex.getMessage());
			ex.printStackTrace();
			return null;
		}

		return props.getProperty(property);
	}

	private static final long K = 1024;
	private static final long M = K * K;
	private static final long G = M * K;
	private static final long T = G * K;

	public static String convertByteToStringRepresentation(final long value) {
		final long[] dividers = new long[] { T, G, M, K, 1 };
		final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
		if (value < 1)
			throw new IllegalArgumentException("Invalid file size: " + value);
		String result = null;
		for (int i = 0; i < dividers.length; i++) {
			final long divider = dividers[i];
			if (value >= divider) {
				result = format(value, divider, units[i]);
				break;
			}
		}
		return result;
	}

	private static String format(final long value, final long divider, final String unit) {
		final double result = divider > 1 ? (double) value / (double) divider : (double) value;
		return new DecimalFormat("#,##0.#").format(result) + " " + unit;
	}

}
