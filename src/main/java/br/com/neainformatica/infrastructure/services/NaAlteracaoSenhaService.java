package br.com.neainformatica.infrastructure.services;

/**
 -----------------------------------------------
 @Empresa: N&A Informática Ltda
 @Gerador: MultiSource 
 Dados da Classe:
 @Data  = 23/12/2014 09:20:27
 @Author  = Eduardo Leite Ranzzani
 @Versão da Classe = 
 -----------------------------------------------
 */

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaAlteracaoSenhaRepository;
import br.com.neainformatica.infrastructure.entity.NaAlteracaoSenha;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;
import br.com.neainformatica.infrastructure.tools.NeaDate;
import br.com.neainformatica.infrastructure.tools.Tools;

public class NaAlteracaoSenhaService extends GenericService<NaAlteracaoSenha> implements NaServiceInterface, Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	NaAlteracaoSenhaRepository dao;

	@Inject
	InfrastructureController infraStructureController;

	@Override
	public GenericRepository<NaAlteracaoSenha> getRepository() {
		return this.dao;
	}

	public NaAlteracaoSenha buscaNaAlteracaoSenhaPorHash(String chaveValidacao) {
		return dao.buscaNaAlteracaoSenhaPorHash(chaveValidacao);
	}

	public void mudaStatusHash(NaUsuario naUsuario) {
		dao.mudaStatusHash(naUsuario);
	}

	public NaAlteracaoSenha salvarHash(NaUsuario naUsuario) {
		Date data = new Date();
		SimpleDateFormat dataFormatada = new SimpleDateFormat("ddMMyyyyHHmmss");
		Random gerador = new Random();
		Integer numero = gerador.nextInt(8999) + 1000;
		NaAlteracaoSenha naAlteracaoSenha = new NaAlteracaoSenha();
		naAlteracaoSenha.setInvalido(EnumSimNao.NAO);
		naAlteracaoSenha.setUsuario(naUsuario);
		naAlteracaoSenha.setSistema(infraStructureController.getNaSistema());
		naAlteracaoSenha.setData(data);
		naAlteracaoSenha.setChaveValidacao(StringUtils.leftPad(naUsuario.getId().toString(), 6, "0") + StringUtils.leftPad(infraStructureController.getNaSistema().getId().toString(), 4, "0")
				+ StringUtils.leftPad(dataFormatada.format(data), 14, "0") + StringUtils.leftPad(numero.toString(), 4, "0"));
		naAlteracaoSenha.setChaveValidacao(Tools.geraMD5(naAlteracaoSenha.getChaveValidacao()).toLowerCase());
		naAlteracaoSenha.setEmail(naUsuario.getEmail());
		try {
			naAlteracaoSenha = dao.noAudit().save(naAlteracaoSenha);
			naAlteracaoSenha.setProtocolo(Tools.geraSenhaAleatoria(4).toUpperCase()+StringUtils.leftPad(naAlteracaoSenha.getId().toString(), 5, "0"));
			naAlteracaoSenha = dao.noAudit().save(naAlteracaoSenha);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		return naAlteracaoSenha;
	}
	
	public NaAlteracaoSenha buscaPorHash(String hashRecebido, boolean somenteAtivo) {

		Integer caracteresMD5 = 32;
		Integer horasValidasParaOHash = 24;

		if (hashRecebido == null)
			return null;

		if (hashRecebido.length() < caracteresMD5)
			return null;

		NaAlteracaoSenha naAlteracaoSenha = buscaNaAlteracaoSenhaPorHash(hashRecebido);
		
		if (naAlteracaoSenha == null)
			return null;

		if (!somenteAtivo)
			return naAlteracaoSenha;

		if (naAlteracaoSenha.getInvalido().equals(EnumSimNao.SIM))
			return null;

		if (NeaDate.menor((NeaDate.somaHoraData(naAlteracaoSenha.getData(), horasValidasParaOHash)), new Date()))
			return null;

		return naAlteracaoSenha;

	}
	
	
}
