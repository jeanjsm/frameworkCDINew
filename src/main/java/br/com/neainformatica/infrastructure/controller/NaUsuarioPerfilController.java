/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.neainformatica.infrastructure.controller;

import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumDDD;
import br.com.neainformatica.infrastructure.services.NaUsuarioService;
import br.com.neainformatica.infrastructure.services.NaUsuarioSistemaService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author rodolpho.sotolani
 */
@Named
@ViewScoped
public class NaUsuarioPerfilController extends GenericController<NaUsuario> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private NaUsuarioService service;

	@Inject
	private NaUsuarioSistemaService usuarioSistemaService;

	private List<NaUsuarioSistema> listaSistemas;

	@PostConstruct
	public void init() {
		this.objeto = naSessionController.getNaUsuarioSistema().getUsuario();
		this.update();
	}

	@Override
	public String salvar() {
		super.salvar();
		return "";
	}

	@Override
	public String cancel() {
		return "index.xhtml";
	}

	private List<NaUsuarioSistema> buscarSistemasUsuario() {

		return usuarioSistemaService.buscarSistemasUsuario(naSessionController.getNaUsuarioSistema().getUsuario(), naSessionController.getNaCliente());

	}

	public List<NaUsuarioSistema> getListaSistemas() {

		if (this.listaSistemas == null || this.listaSistemas.size() == 0)
			this.listaSistemas = buscarSistemasUsuario();

		return listaSistemas;
	}

	public void setListaSistemas(List<NaUsuarioSistema> listaSistemas) {
		this.listaSistemas = listaSistemas;
	}

	public NaUsuarioService getService() {
		return service;
	}

	public void setService(NaUsuarioService service) {
		this.service = service;
	}

	public List<EnumDDD> getListEnumDdd() {
		return Arrays.asList(EnumDDD.values());
	}

}
