package br.com.neainformatica.infrastructure.enumeration;


import br.com.neainformatica.infrastructure.enumeration.EnumNaParametroTipo;
import br.com.neainformatica.infrastructure.enumeration.EnumNivelUsuario;
import br.com.neainformatica.infrastructure.enumeration.EnumSimNao;
import br.com.neainformatica.infrastructure.interfaces.EnumParametroGrupoInterface;
import br.com.neainformatica.infrastructure.interfaces.EnumParametroInterface;

public enum EnumParametroFramework implements EnumParametroInterface {
	EMAIL_SMTP("EMAIL_SMTP", "Host smtp", "smtplw.com.br", EnumNaParametroTipo.TEXTO, EnumParametroGrupo.DEFAULT, EnumNivelUsuario.SUPORTE,"endereço do host que será utilizado para para todas as rotinas de envio de e-mail do sistema", EnumSimNao.SIM),
	EMAIL_PORT("EMAIL_PORT", "porta do host smtp", "587", EnumNaParametroTipo.TEXTO, EnumParametroGrupo.DEFAULT, EnumNivelUsuario.SUPORTE,"porta do host que será utilizado para para todas as rotinas de envio de e-mail do sistema", EnumSimNao.SIM),
	EMAIL_SMTP_USER("EMAIL_SMTP_USER", "Nome do usuário para autenticação no servidor smtp", "neainformatica", EnumNaParametroTipo.TEXTO, EnumParametroGrupo.DEFAULT, EnumNivelUsuario.SUPORTE,"nome de usuário utilizado para autenticação no servidor de smtp", EnumSimNao.SIM),
	EMAIL_USUARIO("EMAIL_USUARIO", "usuário de e-mail", "nainfo@neainformatica.com.br", EnumNaParametroTipo.TEXTO, EnumParametroGrupo.DEFAULT, EnumNivelUsuario.SUPORTE,"e-mail que será utilizado para para todas as rotinas de envio de e-mail do sistema", EnumSimNao.SIM),
	EMAIL_SENHA("EMAIL_SENHA", "senha do e-mail", "wkALZHIX0787", EnumNaParametroTipo.SENHA, EnumParametroGrupo.DEFAULT, EnumNivelUsuario.SUPORTE,"senha que será utilizado para para todas as rotinas de envio de e-mail do sistema", EnumSimNao.SIM),
	EMAILS_NOTIFICADOS("EMAILS_NOTIFICADOS", "E-mail que serão notificados", "elielcio.santos@neainformatica.com.br", EnumNaParametroTipo.TEXTO, EnumParametroGrupo.DEFAULT, EnumNivelUsuario.SUPORTE,"Estes e-mails receberão uma notificação caso algum erro acontece durante o processo de atualização de base", EnumSimNao.SIM),
	
	PROXY_HOST("PROXY_HOST", "Proxy para acesso a rede", "", EnumNaParametroTipo.TEXTO, EnumParametroGrupo.DEFAULT, EnumNivelUsuario.ADMINISTRADOR, "Qual o proxy utilizado para acesso a rede", EnumSimNao.SIM),
    PROXY_PORT("PROXY_PORT", "Porta do proxy para acesso a rede", "", EnumNaParametroTipo.TEXTO, EnumParametroGrupo.DEFAULT, EnumNivelUsuario.ADMINISTRADOR, "Qual a porta do proxy utilizado para acesso a rede", EnumSimNao.SIM);

	private String chave;
	private String descricao;
	private String nome;
	private String valorPadrao;
	private EnumNaParametroTipo tipo;
	private EnumParametroGrupoInterface grupo;
	private EnumNivelUsuario nivelUsuario;
	private EnumSimNao valorFixo;

	private EnumParametroFramework(String chave, String nome, String valorPadrao, EnumNaParametroTipo tipo, EnumParametroGrupoInterface grupo, EnumNivelUsuario nivelUsuario, String descricao,
			EnumSimNao valorFixo) {
		this.chave = chave;
		this.nome = nome;
		this.valorPadrao = valorPadrao;
		this.tipo = tipo;
		this.grupo = grupo;
		this.nivelUsuario = nivelUsuario;
		this.descricao = descricao;
		this.valorFixo = valorFixo;
	}

	public String getChave() {
		return chave;
	}

	public String getDescricao() {
		return descricao;
	}

	public String getNome() {
		return nome;
	}

	public String getValorPadrao() {
		return valorPadrao;
	}

	public EnumNaParametroTipo getTipo() {
		return tipo;
	}

	public EnumParametroGrupoInterface getGrupo() {
		return grupo;
	}

	public EnumNivelUsuario getNivelUsuario() {
		return nivelUsuario;
	}

	public EnumSimNao getValorFixo() {
		return valorFixo;
	}

}