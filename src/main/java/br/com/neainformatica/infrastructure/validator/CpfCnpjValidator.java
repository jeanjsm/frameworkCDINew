package br.com.neainformatica.infrastructure.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import br.com.neainformatica.infrastructure.tools.NeaFormatter;

@FacesValidator(value = "cpfCnpjValidator")
public class CpfCnpjValidator implements Validator {

	@Override
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

		if (value != null && !value.toString().equals("")) {
			String cpfCnpj = value.toString();

			if (cpfCnpj.replaceAll("\\D", "").length() == 11) {
				Boolean valido = NeaFormatter.validaCpf(cpfCnpj.replaceAll("\\D", ""));
				if (!valido) {
					throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "O CPF informado é inválido!", null));
				}
			} else if (cpfCnpj.replaceAll("\\D", "").length() == 14) {
				Boolean valido = NeaFormatter.validaCnpj(cpfCnpj.replaceAll("\\D", ""));
				if (!valido) {
					throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "O CNPJ informado é inválido!", null));
				}
			} else {
				throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "O CPF/CNPJ informado é inválido!", null));
			}

		}
	}

}
