package br.com.neainformatica.infrastructure.converter;

import com.thoughtworks.xstream.converters.SingleValueConverter;

public class XStreamStringConverter implements SingleValueConverter {

	@Override
	@SuppressWarnings("rawtypes")
	public boolean canConvert(Class classe) {
		return String.class.isAssignableFrom(classe);
	}

	@Override
	public Object fromString(String texto) {
		return texto;
	}

	@Override
	public String toString(Object texto) {

		return ((String) texto).trim();
	}

}