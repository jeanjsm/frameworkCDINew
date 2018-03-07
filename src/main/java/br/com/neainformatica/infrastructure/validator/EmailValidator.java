package br.com.neainformatica.infrastructure.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "emailValidator")
public class EmailValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		if(value != null){
			String valor = String.valueOf(value);
			if(!validar(valor))
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "E-mail inválido.", null));
		}
	}

	public boolean validar(String email) {
		boolean isEmailIdValid = false;
		
		if (email != null && email.length() > 0) {
			String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
			Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(email);
			if (matcher.matches()) {
				isEmailIdValid = true;
			}
		}else if(email==null || email.isEmpty())
			isEmailIdValid = true;
		
		return isEmailIdValid;
	}

}
