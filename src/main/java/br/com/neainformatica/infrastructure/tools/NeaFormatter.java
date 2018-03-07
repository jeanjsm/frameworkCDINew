package br.com.neainformatica.infrastructure.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

import javax.inject.Named;

@Named
public class NeaFormatter {

	/**
	 * passado uma string do cpf sem a mascara, o metodo devolve a string com a
	 * mascara
	 * 
	 * @param cpf
	 * @return cpfComMascara
	 * @author thiagosilva
	 */
	public static String gerarStringPadraoCPF(String cpf) {
		if (cpf != null && !cpf.isEmpty()) {
			try {
				return new StringBuilder(cpf).insert(3, ".").insert(7, ".").insert(11, "-").toString();
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}
	
	/**
	 * passado uma string do cpf sem a mascara, o metodo devolve a string com a
	 * mascara e exibe somente os 3 primeiros digitos e os 2 ultimos, deixando 6 digitos oculto.
	 * 
	 * @param cpf
	 * @return cpfComMascara
	 * @author thiagosilva
	 */
	public static String gerarStringOcultaCPF(String cpf) {
		if (cpf != null && !cpf.isEmpty()) {
			try {
				StringBuilder builder = new StringBuilder(cpf);
				builder.replace(3, 9, "******");
				return builder.insert(3, ".").insert(7, ".").insert(11, "-").toString();
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	/**
	 * passado uma string do cnpj sem a mascara, o metodo devolve a string com a
	 * mascara
	 * 
	 * @param cnpj
	 * @return cnpjComMascara
	 * @author thiagosilva
	 */
	public static String gerarStringPadraoCNPJ(String cnpj) {
		if (cnpj != null && !cnpj.isEmpty()) {
			try {
				return new StringBuilder(cnpj).insert(2, ".").insert(6, ".").insert(10, "/").insert(15, "-").toString();
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	/**
	 * Coloca marcara tanto em CPF quanto em CNPJ
	 * 
	 * @param String
	 *            cpfCnpj
	 * @return string com mascara
	 */
	public static String formatarCpfCnpj(String cpfCnpj) {

		if ((cpfCnpj != null) && (!cpfCnpj.equals(""))) {
			if (cpfCnpj.replaceAll("\\D", "").length() == 11) {
				return NeaFormatter.gerarStringPadraoCPF(cpfCnpj.replaceAll("\\D", ""));

			} else if (cpfCnpj.replaceAll("\\D", "").length() == 14) {
				return NeaFormatter.gerarStringPadraoCNPJ(cpfCnpj.replaceAll("\\D", ""));
			}
		}

		return cpfCnpj;
	}

	/**
	 * passado uma string de cep sem a mascara, o metodo devolve a string com a
	 * mascara
	 * 
	 * @param cep
	 * @return cepComMascara
	 * @author thiagosilva
	 */
	public static String gerarStringPadraoCEP(String cep) {

		try {
			if (cep != null && !cep.isEmpty()) {
				return new StringBuilder(cep).insert(2, ".").insert(6, "-").toString();
			}
			return "";
		} catch (StringIndexOutOfBoundsException e) {
			return "";
		}

	}

	/**
	 * passado uma string de telefone sem a mascara, o metodo devolve a string
	 * com a mascara
	 * 
	 * @param telefone
	 * @return telefoneComMascara
	 * @author thiagosilva
	 */
	public static String gerarStringPadraoTelefone(String telefone) {

		if (telefone != null && !telefone.isEmpty()) {
			try {
				return new StringBuilder(telefone).insert(0, "(").insert(3, ")").insert(8, "-").toString();
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	public static boolean validaCpfCnpj(String cpfCnpj) {
		if (cpfCnpj == null) {
			return false;
		}

		if (cpfCnpj.length() == 11) {
			return validaCpf(cpfCnpj);
		} else if (cpfCnpj.length() == 14) {
			return validaCnpj(cpfCnpj);
		} else {
			return false;
		}

	}

	public static String geraMD5(String texto) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			BigInteger assinatura = new BigInteger(1, md5.digest(texto.getBytes()));
			return assinatura.toString(16).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Calculo do CNPJ ex 91.155.259/0001-67
	 * (5*9)+(4*1)+(3*1)+(2*5)+(9*5)+(8*2)+
	 * (7*5)+(6*9)+(5*0)+(4*0)+(3*0)+(2*1)=(214 * 10) mod 11 = 6
	 * (6*9)+(5*1)+(4*1
	 * )+(3*5)+(2*5)+(9*2)+(8*5)+(7*9)+(6*0)+(5*0)+(4*0)+(3*1)+(2*6)=(224 *10 )
	 * mod 11 = 7
	 * 
	 * O valor do cnpj pode ser passado com ou sem mascara
	 * 
	 * @param cnpj
	 * @return boolean
	 * @author thiagosilva
	 */
	public static boolean validaCnpj(String cnpj) {

		if (cnpj == null) {
			return false;
		}

		cnpj = cnpj.replaceAll("\\D", "");

		if (cnpj.length() != 14)
			return false;

		/* primeiro digito */
		int j = 2, soma = 0, dig;
		for (int i = 11; i >= 0; i--) {
			soma += Integer.parseInt(cnpj.substring(i, i + 1)) * j++;
			if (j > 9)
				j = 2;
		}
		dig = (soma * 10) % 11;
		if (dig == 10)
			dig = 0;
		if (dig != Integer.parseInt(cnpj.substring(12, 13)))
			return false;

		/* segundo digito */
		j = 2;
		soma = 0;
		for (int i = 12; i >= 0; i--) {
			soma += Integer.parseInt(cnpj.substring(i, i + 1)) * j++;
			if (j > 9)
				j = 2;
		}
		dig = (soma * 10) % 11;
		if (dig == 10)
			dig = 0;
		if (dig != Integer.parseInt(cnpj.substring(13, 14)))
			return false;
		
		if(cnpj.equals("00000000000000"))
			return false;

		return true;
	}

	/**
	 * Calculo do CPF ex 244.436.621-20
	 * (10*2)+(9*4)+(8*4)+(7*4)+(6*3)+(5*6)+(4*6)+(3*2)+(2*1)=(295 * 10) mod 11
	 * = 2 (10*4)+(9*4)+(8*4)+(7*3)+(6*6)+(5*6)+(4*2)+(3*1)+(2*2)=(265 * 10) mod
	 * 11 = 10 = 0
	 * 
	 * O valor do cpf pode ser passado com ou sem mascara
	 * 
	 * @param cnpj
	 * @return boolean
	 * @author thiagosilva
	 */
	public static boolean validaCpf(String cpf) {
		if (cpf == null) {
			return false;
		}

		cpf = cpf.replaceAll("\\D", "");

		if (cpf.length() < 11)
			return false;

		/* primeiro digito */
		int j = 2, soma = 0, dig;
		for (int i = 8; i >= 0; i--) {
			soma += Integer.parseInt(cpf.substring(i, i + 1)) * j++;
		}
		dig = (soma * 10) % 11;
		if (dig == 10)
			dig = 0;
		if (dig != Integer.parseInt(cpf.substring(9, 10)))
			return false;

		/* segundo digito */
		j = 2;
		soma = 0;
		for (int i = 9; i > 0; i--) {
			soma += Integer.parseInt(cpf.substring(i, i + 1)) * j++;
		}
		dig = (soma * 10) % 11;
		if (dig == 10)
			dig = 0;
		if (dig != Integer.parseInt(cpf.substring(10, 11)))
			return false;

		if ((cpf.equals("00000000000")) || (cpf.equals("11111111111")) || (cpf.equals("22222222222")) || (cpf.equals("33333333333"))
				|| (cpf.equals("44444444444")) || (cpf.equals("55555555555")) || (cpf.equals("66666666666")) || (cpf.equals("77777777777"))
				|| (cpf.equals("88888888888")) || (cpf.equals("99999999999"))) {
			return false;
		}

		return true;
	}

	/**
	 * Este método pode ser utilizado para formatar qualquer string, no campo
	 * mascara utilize valores como 99.999.9999 ou ##.###.####
	 * 
	 * @param value
	 * @param mask
	 * @return
	 */
	public static String formatString(String value, String mask) {

		try {

			mask = mask.replaceAll("9", "#");

			javax.swing.text.MaskFormatter mf = new javax.swing.text.MaskFormatter(mask);
			mf.setValueContainsLiteralCharacters(false);
			return mf.valueToString(value);
		} catch (ParseException e) {
			return value;
		}
	}

}
