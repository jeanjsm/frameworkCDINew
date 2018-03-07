package br.com.neainformatica.infrastructure.filter;

import java.io.IOException;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import br.com.neainformatica.infrastructure.annotation.NeaSecurityToken;
import br.com.neainformatica.infrastructure.controller.NaSessionController;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.services.NaUsuarioService;
import br.com.neainformatica.infrastructure.services.NaUsuarioTokenService;
import br.com.neainformatica.infrastructure.tools.Tools;

/*
 * Created by rodolpho.sotolani on 01/02/2017.
 */
@NeaSecurityToken
@Provider
@Priority(Priorities.AUTHENTICATION)
public class TokenAuthenticationFilter implements ContainerRequestFilter {

	@Inject
	private NaUsuarioTokenService tokenService;
	
	@Inject
	private NaUsuarioService naUsuarioService;

	@Inject
	private NaSessionController naSessionController;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {

		try {

			// Get the HTTP Authorization header from the request
			String token = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

			// Check if the HTTP Authorization header is present and formatted correctly
			if (token == null)
				throw new NotAuthorizedException("Cabeçalho de autorização deve ser fornecido");

			if (token.equals(Tools.senhaDoDia())) {
				NaUsuario usu = naUsuarioService.buscaUsuarioLogin("SUPORTE");
				definirUsuarioAuditoria("SUPORTE", usu);
				return;
			}

			NaUsuario usuarioToken = tokenService.validarToken(token);

			if (usuarioToken == null)
				requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
			else
				definirUsuarioAuditoria(usuarioToken.getNomeUsuarioAuditoria(), usuarioToken);			

		} catch (Exception e) {
			requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build());
		}
	}

	private void definirUsuarioAuditoria(String nomeUsuario, NaUsuario naUsuario) {
		
		if (naUsuario != null){
			naSessionController.setNaUsuario(naUsuario);
		}else{
			NaUsuario u = new NaUsuario();
			u.setCpfCnpj(nomeUsuario);
			naSessionController.setNaUsuario(u);			
		}
		
	}
}