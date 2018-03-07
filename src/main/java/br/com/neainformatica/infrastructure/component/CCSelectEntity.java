package br.com.neainformatica.infrastructure.component;

import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponentBase;


@FacesComponent(value = "CCSelectEntity")
public class CCSelectEntity extends UIComponentBase implements NamingContainer {

	
	@Override
	public String getFamily() {
		return "javax.faces.NamingContainer"; // Importante, essa é a familia
												// dos cc
	}


	/*
	public Converter getConverter() {
		
		if (getAttributes().get("converter") == null || getAttributes().get("converter").equals("")) {
			return (Converter)  GenericEntityConverter;
		}
		
		return (Converter) getAttributes().get("converter");
		
	}
*/

	

	/*
	@Override
	public void encodeBegin(FacesContext context) throws IOException {

		if (getAttributes().get("converter") == null || getAttributes().get("converter").equals("")) {
			ResponseWriter rw = context.getResponseWriter();
			rw.writeAttribute("converter", GenericEntityConverter.class, "converter");
		}
		
		super.encodeBegin(context);
		
	}*/
	
	/*
	@Override
	public void encodeEnd(FacesContext context) throws IOException {


		super.encodeEnd(context);
	}
	*/

}
