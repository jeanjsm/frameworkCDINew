package br.com.neainformatica.infrastructure.converter;

import java.util.Collection;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import br.com.neainformatica.infrastructure.tools.Tools;
import br.com.neainformatica.infrastructure.tools.ToolsReflection;

@FacesConverter("selectOneConverter")
public class SelectOneConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null || value.equals(""))
			return null;

		try {
			Integer id = Integer.valueOf(value);
			Collection<?> items = (Collection<?>) component.getAttributes().get("items");
			return Tools.findById(items, id);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ConverterException("Não foi possível aplicar conversão de item com valor [" + value + "] no componente [" + component.getId() + "]", ex);
		}
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null || !value.equals(""))
			return "";
		else
			return ToolsReflection.getIdByReflection(value).toString();
	}

}
