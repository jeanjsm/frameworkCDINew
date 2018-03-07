package br.com.neainformatica.frameworkCDI;

import org.junit.Assert;
import org.junit.Test;

import br.com.neainformatica.infrastructure.tools.NeaDate;

public class TestNeaDate {

	@Test
	public void testMesAnoData() {
		Assert.assertEquals("", "05/2000", NeaDate.mesAnoData(NeaDate.criaData(2000, 05, 30)));
		Assert.assertEquals("", "01/2000", NeaDate.mesAnoData(NeaDate.criaData(2000, 01, 30)));
		Assert.assertEquals("", "12/2000", NeaDate.mesAnoData(NeaDate.criaData(2000, 12, 30)));

		Assert.assertNotEquals("", "10/2000", NeaDate.mesAnoData(NeaDate.criaData(2000, 12, 30)));
	}

	// @Test
	// public void testSomaHoraData() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testMenor() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetDiferencaDeDias() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetDiferencaDeMinutos() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testCriaDataIntIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testCriaDataIntIntIntIntInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testSomaDiasData() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testPrimeiroDiaMes() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testUltimoDiaMes() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testZeraHoraData() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testUltimaHoraData() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testFormatarData() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testObterDiaDaSemana() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testIncrementaDataPorDias() {
	// fail("Not yet implemented");
	// }
	//
	@Test
	public void testDiaData() {
		Assert.assertSame("", 15, NeaDate.diaData(NeaDate.criaData(2000, 05, 15)));
		Assert.assertSame("", 30, NeaDate.diaData(NeaDate.criaData(2000, 01, 30)));
		Assert.assertSame("", 30, NeaDate.diaData(NeaDate.criaData(2000, 12, 30)));

		Assert.assertNotSame("", 5, NeaDate.diaData(NeaDate.criaData(2000, 12, 30)));
	}

	@Test
	public void testMesData() {
		Assert.assertSame("", 5, NeaDate.mesData(NeaDate.criaData(2000, 05, 30)));
		Assert.assertSame("", 1, NeaDate.mesData(NeaDate.criaData(2000, 01, 30)));
		Assert.assertSame("", 12, NeaDate.mesData(NeaDate.criaData(2000, 12, 30)));

		Assert.assertNotSame("", 5, NeaDate.mesData(NeaDate.criaData(2000, 12, 30)));
	}
	//
	// @Test
	// public void testAnoData() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testStringToDate() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testObject() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testGetClass() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testHashCode() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testEquals() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testClone() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testToString() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testNotify() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testNotifyAll() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testWaitLong() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testWaitLongInt() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testWait() {
	// fail("Not yet implemented");
	// }
	//
	// @Test
	// public void testFinalize() {
	// fail("Not yet implemented");
	// }

}
