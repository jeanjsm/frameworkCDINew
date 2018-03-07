package br.com.neainformatica.infrastructure.controller.components;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;


@Named("inputSearchController")
@RequestScoped
public class InputSearchController implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	

	// TODO - Verificar uma forma de fazer isto funcionar.
/*	
 * 
 * onblur="#{inputSearchController.objectSearchByParam(cc.attrs.controller, cc.attrs.atributoCodigo)}"
 * 
	@Inject
	RecenseadorController controller;
	
	public RecenseadorController getController() {
		return controller;
	}

	public void setController(RecenseadorController controller) {
		this.controller = controller;
	}

	public Object objectSearchByParam(Object controller, String id) {
		log.debug("Testando");
		
		if (controller != null && (id != null && id != "")) {
			return getController().findByParam(Integer.parseInt(id), "id");
		} else {
			return null;
		}
	}
*/	
}