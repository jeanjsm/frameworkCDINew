package br.com.neainformatica.infrastructure.tools;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by rodolpho.sotolani on 25/10/2017.
 */
public class NeaValidador {

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);


    /**
     * Valida se o numero do NIS (PIS/PASEP/NIT) está correto
     * @param plPIS
     * @return
     */
    public static boolean validarPIS (String plPIS) {
        if (StringUtils.isBlank(plPIS))
            return false;

        int liTamanho = 0;
        StringBuffer lsAux = null;
        StringBuffer lsMultiplicador = new StringBuffer("3298765432");
        int liTotalizador = 0;
        int liResto = 0;
        int liMultiplicando = 0;
        int liMultiplicador = 0;
        boolean lbRetorno = true;
        int liDigito = 99;
        try{
            lsAux = new StringBuffer().append(plPIS);
            liTamanho = lsAux.length();
            if (liTamanho != 11) {
                lbRetorno = false;
            }
            if (lbRetorno) {
                for (int i=0; i<10; i++) {
                    liMultiplicando = Integer.parseInt(lsAux.substring(i, i+1));
                    liMultiplicador = Integer.parseInt(lsMultiplicador.substring(i, i+1));
                    liTotalizador += liMultiplicando * liMultiplicador;
                }
                liResto = 11 - liTotalizador % 11;
                liResto = liResto == 10 || liResto == 11 ? 0 : liResto;
                liDigito = Integer.parseInt("" + lsAux.charAt(10));
                lbRetorno = liResto == liDigito;
            }
            return lbRetorno;
        }catch (NumberFormatException e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Valida se o E-Mail informado é correto.
     * @param email
     * @return
     */
    public static boolean validarEmail(String email){
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
