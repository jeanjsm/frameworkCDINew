package br.com.neainformatica.infrastructure.filter;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.picketlink.Identity;
import org.picketlink.authentication.event.LoggedInEvent;

import br.com.neainformatica.infrastructure.controller.GenericMessages;
import br.com.neainformatica.infrastructure.services.NaClienteService;

public class AuthenticationFilter {

	@Inject
	private NaClienteService clienteService;

	@Inject
	private Identity identity;

	@Inject
	private GenericMessages messages;

	public void onSuccessFulLogin(@Observes LoggedInEvent event) {

		if (clienteService.buscaListaDeClientes().isEmpty()) {
			identity.logout();
			messages.addErrorMessage("Você não possui permissão para acessar este Sistema no cliente");
		}

	}
}
