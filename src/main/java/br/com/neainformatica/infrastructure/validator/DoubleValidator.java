package br.com.neainformatica.infrastructure.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "doubleValidator")
public class DoubleValidator implements Validator{

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		
		try {
			if(value != null && !value.toString().equals(""))
				Double.parseDouble(value.toString());
		} catch (Exception e) {
			throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Valor inválido", null));
		}
		
		
	}

}
