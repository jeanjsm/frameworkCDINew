package br.com.neainformatica.infrastructure.converter;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.neainformatica.infrastructure.entity.NaPerfil;
import br.com.neainformatica.infrastructure.services.NaPerfilService;

@Named("naPerfilConverter")
@RequestScoped

/*
 * Marcos Bispo, 21/01/2014:
 * Este converter precisa ser um managedbean do cdi (@Named, @RequestScoped) para que a injeção da dependência NaPerfilService funcione.
 * Se for anotado com @FacesConverter a injeção de dependências não funciona.
 */

public class NaPerfilConverter implements Converter {

	@Inject
	private NaPerfilService service;
	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2)
			throws ConverterException {
		
		try {
			return service.findById(Integer.parseInt(arg2));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ConverterException("Erro no converter NaPerfilConverter, tentativa de converter " + arg2);
		}
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object o)
			throws ConverterException {
		NaPerfil np = (NaPerfil) o;
		return "" + np.getId();
	}

}
