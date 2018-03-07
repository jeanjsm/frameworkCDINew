package br.com.neainformatica.infrastructure.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;

import br.com.neainformatica.infrastructure.tools.NeaFormatter;

@FacesConverter("cpfCnpjConverter")
public class CpfCnpjConverter implements javax.faces.convert.Converter {

	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {

		try {
			if (string != null) {

				/*
				if (string.replaceAll("\\D", "").length() == 11) {
					Boolean valido = NeaFormatter.validaCpf(string.replaceAll("\\D", ""));
					if (!valido) {
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O CPF informado é inválido!", ""));
					}
				} else if (string.replaceAll("\\D", "").length() == 14) {
					Boolean valido = NeaFormatter.validaCnpj(string.replaceAll("\\D", ""));
					if (!valido) {
						facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O CNPJ informado é inválido!", ""));
					}
				} else {
					facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "O CPF/CNPJ informado é inválido!", ""));
				}
				*/

				return string.replaceAll("\\D", "");
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object objeto) {

		try {
			if (objeto != null) {
				if (((String) objeto).length() == 11) {
					return NeaFormatter.gerarStringPadraoCPF((String) objeto);
				} else {
					return NeaFormatter.gerarStringPadraoCNPJ((String) objeto);
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}