package br.com.neainformatica.infrastructure.annotation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import br.com.neainformatica.infrastructure.enumeration.EnumAuditoriaOperacao;
import br.com.neainformatica.infrastructure.entity.NaAuditoria;
import br.com.neainformatica.infrastructure.entity.NaAuditoriaItem;

public class ReportDataSource {
	
	public static void main(String[] args) throws Exception {
		
	}
	
	public static List<NaAuditoria> getDados() {
		List<NaAuditoria> lsDados = new ArrayList<NaAuditoria>();
		
		NaAuditoria n1 = new NaAuditoria(1, new Date(), "Wanderson", "TABELA", "1", EnumAuditoriaOperacao.ALTERACAO, 0,null, null);
		List<NaAuditoriaItem> l1 = new ArrayList<NaAuditoriaItem>();
		l1.add(new NaAuditoriaItem(n1, "XXX", "", "123"));
		l1.add(new NaAuditoriaItem(n1, "YYY", "456", "789"));
		l1.add(new NaAuditoriaItem(n1, "ZZZ", null, "Nos dois documentos há a mesma foto, porém, com os primeiros nomes diferentes. Segundo a PM, pelo nome registrado na carteira de São Paulo ela tem autorização para visitar o marido preso; pela expedida em Mato Grosso do Sul, o irmão."));
		n1.setAuditoriaItems(l1);
		lsDados.add(n1);
		
		NaAuditoria n2 = new NaAuditoria(2, new Date(), "Xico", "TABELA", "2", EnumAuditoriaOperacao.ALTERACAO, 0, null, null);
		List<NaAuditoriaItem> l2 = new ArrayList<NaAuditoriaItem>();
		l2.add(new NaAuditoriaItem(n2, "XXX", "", "444"));
		l2.add(new NaAuditoriaItem(n2, "YYY", "456", "777"));
		l2.add(new NaAuditoriaItem(n2, "ZZZ", null, "PPP"));
		n2.setAuditoriaItems(l2);
		lsDados.add(n2);
		
		return lsDados;
	}
	
}
