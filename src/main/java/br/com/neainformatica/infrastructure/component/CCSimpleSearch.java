package br.com.neainformatica.infrastructure.component;

import java.io.IOException;

import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.UINamingContainer;
import javax.faces.component.visit.VisitCallback;
import javax.faces.component.visit.VisitContext;
import javax.faces.context.FacesContext;
import javax.faces.render.Renderer;

import org.primefaces.component.selectonemenu.SelectOneMenu;

@FacesComponent(value = "cCSimpleSearch")
public class CCSimpleSearch extends UINamingContainer {

	private SelectOneMenu filtro;

	public CCSimpleSearch() {
		//System.out.println("CCSimpleSearch.CCSimpleSearch() " + getClientId());		

		//this.filtro = new SelectOneMenu();
		
//		if (this.filtro != null && this.getAttributes().get("filtroDefault") != null && !this.getAttributes().get("filtroDefault").equals(""))
//			this.filtro.setValue(this.getAttributes().get("filtroDefault"));
	}
	
	
	
	@Override
	public boolean visitTree(VisitContext context, VisitCallback callback) {
		//if (this.filtro != null && this.getAttributes().get("filtroDefault") != null && !this.getAttributes().get("filtroDefault").equals(""))
		//	this.filtro.setValue(this.getAttributes().get("filtroDefault"));
		return super.visitTree(context, callback);
	}

	@Override
	public void decode(FacesContext context) {
		//System.out.println("CCSimpleSearch.decode()");

		super.decode(context);
	}

	@Override
	public void encodeBegin(FacesContext context) throws IOException {
		//System.out.println("CCSimpleSearch.encodeBegin()");
		
//		if (this.filtro != null && this.getAttributes().get("filtroDefault") != null && !this.getAttributes().get("filtroDefault").equals("")){
//			this.filtro.setValue(this.getAttributes().get("filtroDefault"));
//			//System.out.println("filtro default" + this.getAttributes().get("filtroDefault"));
//		}

		super.encodeBegin(context);
	}

	@Override
	public void encodeAll(FacesContext context) throws IOException {
		//System.out.println("CCSimpleSearch.encodeAll()");

		super.encodeAll(context);
	}

	@Override
	public String getFamily() {
		//System.out.println("CCSimpleSearch.getFamily()");
		return super.getFamily();
	}

	public SelectOneMenu getFiltro() {
		//System.out.println("CCSimpleSearch.getFiltro()");
		

		
		return filtro;
	}
	
	@Override
	protected Renderer getRenderer(FacesContext context) {
		//System.out.println("CCSimpleSearch.getRenderer()");
		
//		if (this.filtro != null && this.getAttributes().get("filtroDefault") != null && !this.getAttributes().get("filtroDefault").equals(""))
//			this.filtro.setValue(this.getAttributes().get("filtroDefault"));
		
		return super.getRenderer(context);
	}
	
	@Override
	public void setValueExpression(String name, ValueExpression binding) {
		//System.out.println("CCSimpleSearch.setValueExpression()");
		
//		if (this.filtro != null && this.getAttributes().get("filtroDefault") != null && !this.getAttributes().get("filtroDefault").equals("")){
//			this.filtro.setValue(this.getAttributes().get("filtroDefault"));
//			//System.out.println("filtro default" + this.getAttributes().get("filtroDefault"));
//		}
		
		super.setValueExpression(name, binding);
	}
	
	@Override
	public void encodeEnd(FacesContext context) throws IOException {

//		if (this.filtro != null && this.getAttributes().get("filtroDefault") != null && !this.getAttributes().get("filtroDefault").equals(""))
//			this.filtro.setValue(this.getAttributes().get("filtroDefault"));
		
		super.encodeEnd(context);
		
	}

	public void setFiltro(SelectOneMenu filtro) {
		//System.out.println("CCSimpleSearch.setFiltro()" + filtro);		
		
		this.filtro = filtro;
		//filtro.getValue();
		
		//if (this.filtro != null && this.getAttributes().get("filtroDefault") != null && !this.getAttributes().get("filtroDefault").equals(""))
		//	this.filtro.setValue(this.getAttributes().get("filtroDefault"));
	}
	
	@Override
	public void setTransient(boolean transientFlag) {
		//System.out.println("CCSimpleSearch.setTransient()");
		super.setTransient(transientFlag);
	}

}
