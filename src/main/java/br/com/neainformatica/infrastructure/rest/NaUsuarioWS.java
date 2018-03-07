package br.com.neainformatica.infrastructure.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioSistema;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.services.NaUsuarioService;
import br.com.neainformatica.infrastructure.services.NaUsuarioSistemaService;

@Path("/usuario")
public class NaUsuarioWS {

	@Inject
	private NaUsuarioService naUsuarioService;

	@Inject
	private NaUsuarioSistemaService usuarioSistemaService;

	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/resetarSenha")
	public Response recuperarSenha(@FormParam("cpf") String cpf, @FormParam("idNaCliente") Integer idNaCliente, @FormParam("idSistema") Integer idSistema) {

		NaUsuario usuario = naUsuarioService.buscaUsuarioCpf(cpf);

		try {

			if (usuario == null)
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Usuário não encontrado!").build();

			NaUsuarioSistema usuarioSistema = usuarioSistemaService.obterUsuarioSistema(idSistema, usuario.getId(), idNaCliente, EnumSimNao.SIM);

			if (usuarioSistema == null)
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Relação usuário/sistema não encontrada!").build();

			naUsuarioService.enviarEmailrecuperacaoSenha(cpf);
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();

		}
		return Response.ok("Email foi enviado com sucesso para: " + usuario.getEmail(), MediaType.APPLICATION_XML).build();
	}
}
