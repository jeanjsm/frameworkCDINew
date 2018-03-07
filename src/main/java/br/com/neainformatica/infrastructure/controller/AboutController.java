package br.com.neainformatica.infrastructure.controller;

import java.io.File;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.New;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletContext;

import br.com.neainformatica.infrastructure.entity.About;
import br.com.neainformatica.infrastructure.entity.Build;
import br.com.neainformatica.infrastructure.entity.Implementation;
import br.com.neainformatica.infrastructure.entity.Version;

import com.thoughtworks.xstream.XStream;

@Named
@RequestScoped
public class AboutController implements Serializable {
	private static final long serialVersionUID = 1L;

	@New
	About about;

	public About getAbout() {
		return xmlForAbout();
	}

	private About xmlForAbout() {
		
		FacesContext fc = FacesContext.getCurrentInstance();
		ServletContext sc = (ServletContext) fc.getExternalContext()
				.getContext();
		String dirArquivo = sc.getRealPath("/common/sobre.xml");

		File file = new File(dirArquivo);

		XStream xstream = new XStream();
		xstream.alias("about", About.class);
		xstream.alias("versions", Version.class);
		xstream.alias("builds", Build.class);
		xstream.alias("implementations", Implementation.class);

		about = (About) xstream.fromXML(file);
		
		return about;
	}
	
}