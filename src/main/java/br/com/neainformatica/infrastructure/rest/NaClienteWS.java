package br.com.neainformatica.infrastructure.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import br.com.neainformatica.infrastructure.rest.bean.NaClienteBean;
import br.com.neainformatica.infrastructure.services.NaClienteService;

@Path("/cliente")
public class NaClienteWS {

	@Inject
	private NaClienteService clienteService;

	@GET
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/getCliente")
	public NaClienteBean recuperarSenha(@QueryParam("cnpj") String cnpj, @QueryParam("idClienteNeA") String idClienteNeA) {
		return clienteService.buscarCliente(cnpj, Integer.parseInt(idClienteNeA));
	}
}
