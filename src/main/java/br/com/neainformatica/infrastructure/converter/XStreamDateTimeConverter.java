package br.com.neainformatica.infrastructure.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class XStreamDateTimeConverter implements Converter {

	private SimpleDateFormat df = new SimpleDateFormat();

	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class classe) {
		return Date.class.isAssignableFrom(classe);
	}

	public void marshal(Object objeto, HierarchicalStreamWriter writer, MarshallingContext context) {

		if ((((Date) objeto).getTime() + TimeZone.getDefault().getRawOffset()) % 86400000 == 0) {
			df.applyPattern("yyyy-MM-dd");
		} else {
			df.applyPattern("yyyy-MM-dd'T'HH:mm:ss");
		}

		context.convertAnother(df.format((Date) objeto));
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {

		try {

			if (reader.getValue().length() == 10) {
				df.applyPattern("yyyy-MM-dd");
			} else {
				df.applyPattern("yyyy-MM-dd'T'HH:mm:ss");
			}

			return df.parse(reader.getValue());
		} catch (ParseException e) {
		}
		return null;
	}

}