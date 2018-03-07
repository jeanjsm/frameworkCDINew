package br.com.neainformatica.infrastructure.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import br.com.neainformatica.infrastructure.controller.GenericMessages;
import br.com.neainformatica.infrastructure.tools.NeaFormatter;

/**
 * Este converter deve ser usado apenas para o componente inputMask Ele
 * identifica o nome do componente pelo ID e valida a informação
 * 
 * @author elielcio.santos
 * 
 */
@FacesConverter("inputMaskConverter")
public class inputMaskConverter implements javax.faces.convert.Converter {

	@Inject
	GenericMessages messages;

	public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {

		try {
			if (string != null) {

				String nomeComponente = (String) uiComponent.getAttributes().get("id");

				if (nomeComponente.startsWith("cep_inputMask_")) {

					return string.replaceAll("\\D", "");

				} else if (nomeComponente.startsWith("cpf_inputMask_")) {

					Boolean valido = NeaFormatter.validaCpf(string.replaceAll("\\D", ""));
					if (!valido) {
						messages.addErrorMessage("O CPF informado é inválido!");
					}

				} else if (nomeComponente.startsWith("cnpj_inputMask_")) {

					Boolean valido = NeaFormatter.validaCnpj(string.replaceAll("\\D", ""));
					if (!valido) {
						messages.addErrorMessage("O CNPJ informado é inválido!");
					}

				}

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

				String nomeComponente = (String) uiComponent.getAttributes().get("id");

				if (nomeComponente.startsWith("cep_inputMask_"))
					return NeaFormatter.gerarStringPadraoCEP((String) objeto);
				else if (nomeComponente.startsWith("cpf_inputMask_"))
					return NeaFormatter.gerarStringPadraoCPF((String) objeto);
				else if (nomeComponente.startsWith("cnpj_inputMask_"))
					return NeaFormatter.gerarStringPadraoCNPJ((String) objeto);
				else if(nomeComponente.startsWith("telefone_inputMask_"))
					return NeaFormatter.gerarStringPadraoTelefone((String) objeto);
				else if (nomeComponente.startsWith("cpfcnpj_inputMask_")){
					String valor = (String) objeto;
				
					if(valor.length() > 11)
						return NeaFormatter.gerarStringPadraoCNPJ(valor);
					return NeaFormatter.gerarStringPadraoCPF(valor);
				}
				return objeto.toString();
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}