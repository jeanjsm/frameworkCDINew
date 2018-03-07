package br.com.neainformatica.infrastructure.tools;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import javax.inject.Named;

@Named
public class NeaBigDecimal {

	public static String bigDecimalToStringDuasCasas(BigDecimal valor) {

		java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0.00");
		return df.format(valor);

	}

	public static String bigDecimalToCurrency(BigDecimal valor) {

		java.text.DecimalFormat df = new java.text.DecimalFormat("R$ ,##0.00;(R$,##0.00)");

		return df.format(valor);

	}

	public static BigDecimal currencyToBigDecimal(String valor) throws ParseException {
		valor = valor.replaceAll("R$", "");
		valor = valor.replace(".", "");
		valor = valor.replace(",", ".");
		return new BigDecimal(valor);
	}

	public static BigDecimal somar(BigDecimal... valores) {
		BigDecimal resultado = new BigDecimal("0");
		for (int i = 0; i < valores.length; i++) {
			resultado = resultado.add(valores[i]);
		}
		return resultado;
	}

	public static BigDecimal subtrair(BigDecimal valor1, BigDecimal valor2) {
		return valor1.subtract(valor2);
	}

	public static BigDecimal multiplicar(BigDecimal valor1, BigDecimal valor2) {
		return valor1.multiply(valor2);
	}

	public static BigDecimal dividir(BigDecimal divisor, BigDecimal dividendo) {
		BigDecimal retorno = new BigDecimal("0.00");

		if (dividendo.floatValue() != 0) {
			retorno = divisor.divide(dividendo, BigDecimal.ROUND_HALF_DOWN);
		}
		return retorno;
	}

	public static BigDecimal dividir(BigDecimal divisor, int dividendo) {
		BigDecimal retorno = new BigDecimal("0.00");
		if (dividendo != 0) {
			BigDecimal bdDividendo = new BigDecimal(dividendo);
			retorno = divisor.divide(bdDividendo, BigDecimal.ROUND_HALF_DOWN);
		}

		return retorno;
	}

	public static boolean maior(BigDecimal valor01, BigDecimal valor02) {
		if (valor01.doubleValue() > valor02.doubleValue()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean menor(BigDecimal valor01, BigDecimal valor02) {
		if (valor01.doubleValue() < valor02.doubleValue()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean igual(BigDecimal valor01, BigDecimal valor02) {
		if (valor01.doubleValue() == valor02.doubleValue()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean diferente(BigDecimal valor01, BigDecimal valor02) {
		return (!igual(valor01, valor02));
	}

	public static BigDecimal se(boolean condicao, BigDecimal ifTrue, BigDecimal ifFalse) {
		if (condicao) {
			return ifTrue;
		} else {
			return ifFalse;
		}
	}

	public static BigDecimal percentual(BigDecimal valor01, BigDecimal valor02) {
		BigDecimal retorno = new BigDecimal("0.00");

		if (valor02.floatValue() != 0) {
			retorno = valor01.multiply(new BigDecimal("100")).divide(valor02, BigDecimal.ROUND_HALF_DOWN);
		}

		return retorno;
	}

	public static BigDecimal porcentagem(BigDecimal valorPercentual, BigDecimal valor) {
		BigDecimal retorno = valor.multiply(valorPercentual).divide(new BigDecimal("100.00"), 2, BigDecimal.ROUND_HALF_DOWN);
		return retorno;
	}

	public static BigDecimal porcentagem(float f, BigDecimal valor) {
		BigDecimal retorno = valor.multiply(new BigDecimal(f)).divide(new BigDecimal("100.00"), BigDecimal.ROUND_HALF_DOWN);
		return retorno;
	}

	public static BigDecimal porcentagem(int i, BigDecimal valor) {
		BigDecimal retorno = valor.multiply(new BigDecimal(i)).divide(new BigDecimal("100"), BigDecimal.ROUND_HALF_DOWN);
		return retorno;
	}

	public static BigDecimal porcentagem(Integer i, BigDecimal valor) {
		BigDecimal retorno = valor.multiply(new BigDecimal(i)).divide(new BigDecimal("100.00"), BigDecimal.ROUND_HALF_DOWN);
		return retorno;
	}

	public static BigDecimal coalesce(BigDecimal valor, BigDecimal valorPadrao) {
		if (valor != null) {
			return valor;
		} else {
			return valorPadrao;
		}
	}

}
