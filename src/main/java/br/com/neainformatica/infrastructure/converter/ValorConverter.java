package br.com.neainformatica.infrastructure.converter;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;


@FacesConverter("valorConverter")
public class ValorConverter implements javax.faces.convert.Converter {

	private static NumberFormat nf = NumberFormat.getNumberInstance(new Locale(
			"pt", "BR"));

	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
		try {

			if (string == null || string.toString().trim().equals("")) {
				return BigDecimal.valueOf(0.0);
			} else {
				nf.setMaximumFractionDigits(2);
				return BigDecimal.valueOf(nf.parse(string).doubleValue());
			}

		} catch (Exception e) {
			return BigDecimal.valueOf(0.0);
		}
	}

	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);

		try {
			if (object == null || object.toString().trim().equals("")) {
				return nf.format(BigDecimal.valueOf(0.0));
			} else {
				return nf.format(new BigDecimal(object.toString()));
			}
		} catch (Exception e) {
			return null;
		}
	}

}
