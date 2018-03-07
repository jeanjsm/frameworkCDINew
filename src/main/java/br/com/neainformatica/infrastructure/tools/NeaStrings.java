package br.com.neainformatica.infrastructure.tools;

import javax.inject.Named;
import java.text.Normalizer;

@Named
public class NeaStrings {

    public static String firstUpper(String texto) {
        if (texto == null || texto.length() == 0)
            return texto;

        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    /**
     * Recebe uma string e retorna somente os numeros constantes
     *
     * @param string
     * @return string
     */
    public static String somenteNumeros(String str) {
        if (str != null) {
            return str.replaceAll("\\D", "");
        } else {
            return "";
        }
    }

    /**
     * Recebe uma string e retorna somente as letras constantes
     *
     * @param string
     * @return string
     */
    public static String somenteLetras(String str) {
        if (str != null) {
            return str.replaceAll("\\d", "");
        } else {
            return "";
        }
    }


    /**
     * @param text
     * @param substituir
     * @param novoTexto
     * @author Elielcio.Santos
     */
    public static void replaceAllStringBuilder(StringBuilder builder, String substituir, String novoTexto) {

        if (!builder.toString().contains(substituir))
            return;

        int index = builder.indexOf(substituir);
        while (index != -1) {
            builder.replace(index, index + substituir.length(), novoTexto);
            index += novoTexto.length(); // Move to the end of the replacement
            index = builder.indexOf(substituir, index);
        }
    }

    public static String removeAccents(String str) {

        str = Normalizer.normalize(str, Normalizer.Form.NFD);

        str = str.replaceAll("[^\\p{ASCII}]", "");

        return str;

    }

    public static String substituirAcentuacao(String str) {
        return str.replaceAll("[ãâàáä]", "a")
                .replaceAll("[êèéë]", "e")
                .replaceAll("[îìíï]", "i")
                .replaceAll("[õôòóö]", "o")
                .replaceAll("[ûúùü]", "u")
                .replaceAll("[ÃÂÀÁÄ]", "A")
                .replaceAll("[ÊÈÉË]", "E")
                .replaceAll("[ÎÌÍÏ]", "I")
                .replaceAll("[ÕÔÒÓÖ]", "O")
                .replaceAll("[ÛÙÚÜ]", "U")
                .replace('ç', 'c')
                .replace('Ç', 'C')
                .replace('ñ', 'n')
                .replace('Ñ', 'N');
    }

    public static String substituirAcentuacao2(String string) {
        if (string != null) {
            string = Normalizer.normalize(string, Normalizer.Form.NFD);
            string = string.replaceAll("[^\\p{ASCII}]", "");
        }
        return string;
    }


    public static String separeToCamelCase(String s) {

        s = s.substring(0, 1).toUpperCase().concat(s.substring(1));

        return s.replaceAll("([A-Z][a-z]+)", " $1") // Words beginning with UC
                .replaceAll("([A-Z][A-Z]+)", " $1") // "Words" of only UC
                .replaceAll("([^A-Za-z ]+)", " $1") // "Words" of non-letters
                .trim();
    }


    public static String abreviaNomeClientes(String nomeCliente){

        if (nomeCliente == null)
            return null;

        nomeCliente = nomeCliente.toUpperCase();

        nomeCliente = nomeCliente.replaceAll("INSTITUTO", "INST.");
        nomeCliente = nomeCliente.replaceAll("INSTITUTO", "INST.");
        nomeCliente = nomeCliente.replaceAll("PREVIDENCIA", "PREV.");
        nomeCliente = nomeCliente.replaceAll("PREVIDÊNCIA", "PREV.");
        nomeCliente = nomeCliente.replaceAll("SERVIDORES", "SERV.");
        nomeCliente = nomeCliente.replaceAll("PREFEITURA", "PREF.");
        nomeCliente = nomeCliente.replaceAll("MUNICIPAL", "MUN.");
        nomeCliente = nomeCliente.replaceAll("MUNICIPIO", "MUN.");
        nomeCliente = nomeCliente.replaceAll("MUNICÍPIO", "MUN.");
        nomeCliente = nomeCliente.replaceAll("CAMARA", "CAM.");
        nomeCliente = nomeCliente.replaceAll("CÂMARA", "CAM.");

        return nomeCliente;

    }
}
