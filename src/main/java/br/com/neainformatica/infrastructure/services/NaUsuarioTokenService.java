package br.com.neainformatica.infrastructure.services;

import static br.com.neainformatica.infrastructure.controller.InfrastructureController.LIMITE_TEMPO_TOKEN;
import static br.com.neainformatica.infrastructure.controller.InfrastructureController.getNeaInfrastructureSistemaNome;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.Asynchronous;
import javax.inject.Inject;

import br.com.neainformatica.infrastructure.dao.GenericRepository;
import br.com.neainformatica.infrastructure.dao.NaUsuarioTokenRepository;
import br.com.neainformatica.infrastructure.entity.NaUsuario;
import br.com.neainformatica.infrastructure.entity.NaUsuarioToken;
import br.com.neainformatica.infrastructure.interfaces.NaServiceInterface;
import br.com.neainformatica.infrastructure.tools.NeaDate;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Created by rodolpho.sotolani on 01/02/2017.
 */
public class NaUsuarioTokenService extends GenericService<NaUsuarioToken> implements NaServiceInterface, Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	private NaUsuarioTokenRepository dao;

	@Override
	public GenericRepository<NaUsuarioToken> getRepository() {
		return this.dao;
	}

	/**
	 * Busca o Token por Usuario
	 *
	 * @param usuario
	 * @return
	 */
	public NaUsuarioToken findByNaUsuario(NaUsuario usuario) {
		return this.dao.findByNaUsuario(usuario);
	}

//	/**
//	 * Valida se o token informado é valido. Caso o mesmo exista, atualiza a
//	 * data do ultimo acesso.
//	 *
//	 * @param token
//	 * @return
//	 */
//	public boolean validarToken(String token) {
//		NaUsuarioToken naUsuarioToken = this.findByToken(token);
//
//		if (naUsuarioToken == null)
//			return false;
//
//		Date dataExpiracao = NeaDate.somaSegundosData(naUsuarioToken.getDataUltimoAcesso(), LIMITE_TEMPO_TOKEN);
//		// Caso o token esteja expirado, entao remove da base de dados
//		if (dataExpiracao.before(new Date())) {
//			this.delete(naUsuarioToken);
//			return false;
//		}
//
//		// Caso o token esteja valido, entao atualiza a data do ultimo acesso
//		atualizaData(naUsuarioToken);
//		return true;
//	}
	
	
	public NaUsuario validarToken(String token){
		NaUsuarioToken naUsuarioToken = this.findByToken(token);

		if (naUsuarioToken == null)
			return null;

		Date dataExpiracao = NeaDate.somaSegundosData(naUsuarioToken.getDataUltimoAcesso(), LIMITE_TEMPO_TOKEN);
		// Caso o token esteja expirado, entao remove da base de dados
		if (dataExpiracao.before(new Date())) {
			this.delete(naUsuarioToken);
			return null;
		}

		// Caso o token esteja valido, entao atualiza a data do ultimo acesso
		atualizaData(naUsuarioToken);
		return naUsuarioToken.getUsuario();
	}

	public NaUsuarioToken findByToken(String token) {
		return this.dao.findByToken(token);
	}

	@Asynchronous
	private void atualizaData(NaUsuarioToken naUsuarioToken) {
		naUsuarioToken.setDataUltimoAcesso(new Date());
		this.noAudit().save(naUsuarioToken);
	}

	/**
	 * Gera o Token de Autenticação baseado no CPF / CNPJ informado, utilizando
	 * o nome do sistema para gerar a assinatura.
	 *
	 * @param cpfCnpj
	 * @return
	 */
	public String gerarToken(String cpfCnpj) {
		return Jwts.builder().setSubject(cpfCnpj).signWith(SignatureAlgorithm.HS256, getNeaInfrastructureSistemaNome()).compact();
	}
}
