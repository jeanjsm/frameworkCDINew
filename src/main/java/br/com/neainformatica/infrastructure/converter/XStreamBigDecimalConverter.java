package br.com.neainformatica.infrastructure.converter;

import java.math.BigDecimal;

import com.thoughtworks.xstream.converters.SingleValueConverter;

public class XStreamBigDecimalConverter implements SingleValueConverter {

	// http://208.109.186.6/Open-Source/Java/REST/tabulasoftmed/org/softmed/rest/server/xstream/BigDecimalConverter.java.htm

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class arg0) {
		String cname = arg0.getCanonicalName();
		String tname = BigDecimal.class.getCanonicalName();
		boolean result = cname.equals(tname);
		return result;
	}

	@Override
	public Object fromString(String arg0) {
		BigDecimal bd = new BigDecimal(arg0);
		return bd;
	}

	@Override
	public String toString(Object arg0) {
		BigDecimal bd = (BigDecimal) arg0;
		bd = bd.setScale(2, BigDecimal.ROUND_DOWN);
		return bd.toString();
	}

}