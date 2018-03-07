package br.com.neainformatica.frameworkCDI;

import org.junit.Assert;
import org.junit.Test;

import br.com.neainformatica.infrastructure.tools.Tools;

public class TestTools {

	@Test
	public void testValidaEmail() {

		String email;

		email = "elielcio.santos@neainformatica.com.br";
		Assert.assertTrue(Tools.validaEmail(email));

		email = "elielcio.santos@gmail.com";
		Assert.assertTrue(Tools.validaEmail(email));

		email = "elielcio.santos@neainformatica";
		Assert.assertFalse(Tools.validaEmail(email));

		email = "elielcio.santos@neainformatica.com.br;elielcio.santos@neainformatica.com.br";
		Assert.assertFalse(Tools.validaEmail(email));

	}

	@Test
	public void testGeraSenhaAleatoria() {
		Assert.assertTrue(Tools.geraSenhaAleatoria(5).length() == 5);
		Assert.assertTrue(Tools.geraSenhaAleatoria(6).length() == 6);
		Assert.assertTrue(Tools.geraSenhaAleatoria(7).length() == 7);
		Assert.assertTrue(Tools.geraSenhaAleatoria(15).length() == 15);
	}

	@Test
	public void testEncripta() {
		
		

		String textoOriginal = "123";
		String textoCriptografado = Tools.encripta(textoOriginal);

		Assert.assertEquals("", textoCriptografado, "040");
		
		Assert.assertEquals("", Tools.encripta(null), "");
		
		
	}

	@Test
	public void testDecripta() {

		Assert.assertEquals("", "123", Tools.decripta("040"));

		String textoOriginal = "testarotinadecriptografia123#$@";
		String textoCriptografado = Tools.encripta(textoOriginal);

		Assert.assertEquals("", textoOriginal, Tools.decripta(textoCriptografado));

	}

	@Test
	public void testGeraMD5() {

		Assert.assertEquals("", "9178441f07011bb94e23f3e03a703105", Tools.geraMD5("gerando_md5").toLowerCase());
		Assert.assertNotEquals("", "9178441f07011bb94e23f3e03a703105", Tools.geraMD5("gerando_md51").toLowerCase());

	}

}
