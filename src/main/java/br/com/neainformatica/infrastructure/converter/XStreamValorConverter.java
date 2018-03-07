package br.com.neainformatica.infrastructure.converter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XStreamValorConverter implements Converter {
	
	private static NumberFormat nf = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
	
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class classe) {
		return Double.class.isAssignableFrom(classe);
    }
	
	public void marshal(Object objeto, HierarchicalStreamWriter writer, MarshallingContext context) {
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		
		if (objeto == null || objeto.toString().trim().equals("")) {
			context.convertAnother(nf.format(Double.valueOf(0.0)));
		} else {
			context.convertAnother(nf.format(Double.valueOf(objeto.toString())));
		}
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    	
    	try {
    		return nf.parse(reader.getValue()).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
    }

}