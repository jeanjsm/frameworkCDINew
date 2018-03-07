package br.com.neainformatica.infrastructure.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import br.com.neainformatica.infrastructure.tools.NeaFormatter;

@FacesConverter("telefoneConverter")
public class TelefoneConverter implements javax.faces.convert.Converter {

	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {

		try {
			if (string != null) {
				return string.replaceAll("\\D", "");
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getAsString(FacesContext facesContext,
			UIComponent uiComponent, Object objeto) {
		try {
			if (objeto != null) {
				return NeaFormatter.gerarStringPadraoTelefone((String) objeto);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return objeto.toString();
		}
	}

}
