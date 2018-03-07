package br.com.neainformatica.infrastructure.tools.sql;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Quando tenho um arquivo com varios scripts SQL o firebird não consegue
 * executar todos de uma unica vez eh necesario quebrar e executar um por um
 * 
 * esta classe contem os metodos necessarios para quebrar o script em varioas
 * comandos SQLs
 * 
 * @author elielcio.santos
 *
 */
public class ScriptExecutive implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Recebe uma String com varios comandos SQLs e retorna uma lista de
	 * Comandos separados
	 * 
	 * @param script
	 * @return
	 */
	public static List<String> separarInstrucoesSql(String script) {

		List<String> retorno = new ArrayList<String>();

		Integer posCaractereInicio = 0;
		Integer posCaractereFim = 0;
		String terminador = ";";
		boolean terminouScript = false;

		script = script.trim();

		while (!terminouScript) {

			script = script.trim();

			if (script.length() < 3) {
				terminouScript = true;
				break;
			}

			// verifica se inicia com set term
			String caractereAvaliado = script.substring(posCaractereInicio, 10).toUpperCase();
			caractereAvaliado = script.replaceAll(" ", "");

			if (caractereAvaliado.startsWith("SETTERM")) {
				terminador = caractereAvaliado.substring(7, 8);
				posCaractereInicio = devolvePosicaoCaractere(script, terminador) + 2;

				script = script.substring(posCaractereInicio);
				posCaractereInicio = 0;
			}

			// comentarios fora da instrução SQL
			if (caractereAvaliado.substring(0, 2).equals("/*")) {
				posCaractereFim = devolveUltimaPosicaoScript(script, "*/", true);
				script = script.substring(posCaractereFim + 2);
				continue;
			}

			posCaractereInicio = posicaoDoPrimeiroCaracterValido(script);

			if (posCaractereInicio > 0) {
				script = script.substring(posCaractereInicio);
				continue;
			}

			// percorrer a string até encontrar o terminador
			posCaractereFim = devolveUltimaPosicaoScript(script, terminador);

			if (script.length() < 3) {
				continue;
			}

			// adicionado na lista de scripts
			// System.out.println("-----------------------------------");
			// System.out.println(script.substring(posCaractereInicio,
			// posCaractereFim));

			retorno.add(script.substring(posCaractereInicio, posCaractereFim));

			if (script.length() > posCaractereFim + 1)
				script = script.substring(posCaractereFim + 1);

			// terminou os scripts
			script = script.trim();
			if (script.length() < 10)
				terminouScript = true;

			posCaractereInicio = 0;

		}

		return retorno;
	}

	private static Integer posicaoDoPrimeiroCaracterValido(String texto) {

		String[] caracteresInvalidos = new String[] { "\n", "\r", " ", ";", "^" };

		List<String> invalidos = Arrays.asList(caracteresInvalidos);

		for (int i = 0; i < texto.length(); i++) {
			String caractereAvaliado = texto.substring(i, i + 1);

			if (!invalidos.contains(caractereAvaliado))
				return i;

		}

		return 0;

	}

	private static Integer devolvePosicaoCaractere(String texto, String caractere) {

		for (int i = 0; i < texto.length(); i++) {
			String caractereAvaliado = String.valueOf(texto.charAt(i));
			if (caractereAvaliado.equals(caractere))
				return i;
		}

		return null;
	}

	private static Integer devolveUltimaPosicaoScript(String texto, String terminador) {
		return devolveUltimaPosicaoScript(texto, terminador, false);
	}

	private static Integer devolveUltimaPosicaoScript(String texto, String terminador, boolean ignoraComent) {

		boolean entreAspas = false;
		boolean dentroComentario = false;

		for (int i = 0; i < texto.length(); i++) {
			String caractereAvaliado = texto.substring(i, i + terminador.length());

			if (i < (texto.length() - terminador.length())) {

				if (!ignoraComent) {
					String tagComentario = texto.substring(i, i + 2);
					if (!dentroComentario && tagComentario.equals("/*")) {
						dentroComentario = true;
						continue;
					}
					if (tagComentario.equals("*/")) {
						dentroComentario = false;
						continue;
					}

					if (!entreAspas && caractereAvaliado.equals("'")) {
						entreAspas = true;
						continue;
					}

					if (entreAspas && caractereAvaliado.equals("'")) {
						entreAspas = false;
						continue;
					}

				}

			}

			if (caractereAvaliado.equals(terminador))
				return i;
		}

		return texto.length();
	}

}
