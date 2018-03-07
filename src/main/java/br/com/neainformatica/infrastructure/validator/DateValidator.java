package br.com.neainformatica.infrastructure.validator;

import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator(value = "dateValidator")
public class DateValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object object) throws ValidatorException {
		
		if (!(object instanceof Date)) return;
		
		Date date = (Date) object;
		
		Calendar calendarDate = Calendar.getInstance();
		calendarDate.setTime(date);
		
		//Only dates between January 1, 1753 and December 31, 9999 are accepted
		if (calendarDate.get(Calendar.YEAR) < 1753) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "A data possui um valor inválido.", null));
		}

	}

}
