package br.com.neainformatica.infrastructure.rest;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.neainformatica.infrastructure.enumeration.EnumTipoMensagemRetorno;
import br.com.neainformatica.infrastructure.rest.bean.Mensagem;
import br.com.neainformatica.infrastructure.rest.bean.MensagemRetorno;
import br.com.neainformatica.infrastructure.rest.bean.RecuperarSenha;
import br.com.neainformatica.infrastructure.tools.NeaDate;

@Stateless
@Consumes({ "application/xml", "application/json" })
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Path("/seguranca")
public class RestSeguranca {

	@POST
	@Path("recuperarsenha")
	public MensagemRetorno create(RecuperarSenha recuperarSenha) {

		recuperarSenha.toString();

		Date dataSolicitacao = NeaDate.zeraHoraData(recuperarSenha.getDataSolicitacao());
		Date dataAtual = NeaDate.zeraHoraData(new Date());

		MensagemRetorno ret = new MensagemRetorno();

		if (dataSolicitacao.before(dataAtual)) {

			Mensagem m = new Mensagem();
			m.setTipo(EnumTipoMensagemRetorno.ERRO);
			m.setMensagem("Conteúdo do mensagem de erro.");
			ret.getMensagens().add(m);

		} else if (dataSolicitacao.equals(dataAtual)) {
			Mensagem m = new Mensagem();
			m.setTipo(EnumTipoMensagemRetorno.SUCESSO);
			m.setMensagem("Conteúdo do mensagem de sucesso.");
			ret.getMensagens().add(m);

		} else if (dataSolicitacao.after(dataAtual)) {
			Mensagem m = new Mensagem();
			m.setTipo(EnumTipoMensagemRetorno.ALERTA);
			m.setMensagem("Conteúdo do mensagem de Alerta.");
			ret.getMensagens().add(m);

		}

		return ret;

	}

	@GET
	@Path("teste")	
	public MensagemRetorno testarServico() {
		
		MensagemRetorno ret = new MensagemRetorno();
		Mensagem m = new Mensagem();
		m.setTipo(EnumTipoMensagemRetorno.ALERTA);
		m.setMensagem("Conteúdo do mensagem de Alerta.");
		ret.getMensagens().add(m);
		
		return ret;
	}

}
