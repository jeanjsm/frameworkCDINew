package br.com.neainformatica.infrastructure.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;

import br.com.neainformatica.infrastructure.controller.InfrastructureController;
import br.com.neainformatica.infrastructure.dao.NaClienteRepository;
import br.com.neainformatica.infrastructure.enumeration.EnumParametroFramework;

@Named
public class EmailService implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	protected Log log;

	@Inject
	InfrastructureController infrastructureController;

	@Inject
	private NaClienteRepository daoCliente;

	private String smtpHost;
	private String smtpPort;
	private String smtpUsuario;
	private String usuario;
	private String nomeUsuario;
	private String senha;
	private String protocolo;

	private void configuraSmtpPadrao() {
		this.smtpHost = infrastructureController.getParametroInfraestrutura(EnumParametroFramework.EMAIL_SMTP).getValor();
		this.smtpPort = infrastructureController.getParametroInfraestrutura(EnumParametroFramework.EMAIL_PORT).getValor();
		this.smtpUsuario = infrastructureController.getParametroInfraestrutura(EnumParametroFramework.EMAIL_SMTP_USER).getValor();
		this.usuario = infrastructureController.getParametroInfraestrutura(EnumParametroFramework.EMAIL_USUARIO).getValor();
		this.senha = infrastructureController.getParametroInfraestrutura(EnumParametroFramework.EMAIL_SENHA).getValor();
		this.protocolo = "smtp";
	}

	public void sendMail(String emailDestinatario, String assunto, StringBuilder corpoMensagem)
			throws AddressException, MessagingException, UnsupportedEncodingException {

		sendMail(convertStringToInternetAdrres(emailDestinatario), null, assunto, corpoMensagem, null);
	}

	public void sendMail(String emailDestinatario, String emailDestinatarioCco, String assunto, StringBuilder corpoMensagem, MimeBodyPart anexo)
			throws AddressException, MessagingException, UnsupportedEncodingException {
		
		List<MimeBodyPart> anexos = new ArrayList<>();
		anexos.add(anexo);

		sendMail(convertStringToInternetAdrres(emailDestinatario), convertStringToInternetAdrres(emailDestinatarioCco), assunto, corpoMensagem, anexos);
	}
	
	public void sendMail(String emailDestinatario, String emailDestinatarioCco, String assunto, StringBuilder corpoMensagem, List<MimeBodyPart> anexos)
			throws AddressException, MessagingException, UnsupportedEncodingException {
		
		sendMail(convertStringToInternetAdrres(emailDestinatario), convertStringToInternetAdrres(emailDestinatarioCco), assunto, corpoMensagem, anexos);
	}

	public void sendMail(String emailDestinatario, String emailDestinatarioCco, String assunto, StringBuilder corpoMensagem)
			throws AddressException, MessagingException, UnsupportedEncodingException {

		sendMail(convertStringToInternetAdrres(emailDestinatario), convertStringToInternetAdrres(emailDestinatarioCco), assunto, corpoMensagem, null);
	}

	public void sendMail(List<String> emailDestinatario, List<String> emailDestinatarioCco, String assunto, StringBuilder corpoMensagem)
			throws AddressException, MessagingException, UnsupportedEncodingException {

		sendMail(convertStringToInternetAdrres(emailDestinatario), convertStringToInternetAdrres(emailDestinatarioCco), assunto, corpoMensagem, null);
	}

	public void sendMail(Address[] emailDestinatario, Address[] emailDestinatarioCco, String assunto, StringBuilder corpoMensagem, List<MimeBodyPart> anexos)
			throws AddressException, MessagingException, UnsupportedEncodingException {
		log.debug("Envio de e-mail (configurando ambiente)...");

		if (this.smtpHost == null || this.smtpHost.trim().equals(""))
			configuraSmtpPadrao();

		Properties conf = new Properties();
		conf.put("mail.transport.protocol", this.protocolo);
		conf.put("mail.mime.charset", "ISO-8859-1");
		conf.put("mail.smtp.host", this.smtpHost);
		conf.put("mail.smtp.port", this.smtpPort);
		conf.put("mail.smtp.auth", "true");
		// conf.put("mail.debug", "false");

		// Authenticator auth = null;
		// auth = this.new SMTPAuthenticator(this.usuario, this.senha);

		// Session sessao = Session.getDefaultInstance(conf, auth);
		Session sessao = Session.getDefaultInstance(conf, null);
		sessao.setDebug(false);

		MimeMessage msg = new MimeMessage(sessao);
		msg.setSubject(assunto);

		if (this.nomeUsuario == null || this.nomeUsuario.equals(""))
			this.nomeUsuario = daoCliente.buscarCliente().getNomeAbreviado();

		msg.setFrom(new InternetAddress(this.usuario, this.nomeUsuario));
		msg.setRecipients(Message.RecipientType.TO, emailDestinatario);

		if (emailDestinatarioCco != null)
			msg.setRecipients(Message.RecipientType.BCC, emailDestinatarioCco);

		Multipart multipart = new MimeMultipart();

		// montando o corpo da mensagem
		MimeBodyPart attachment0 = new MimeBodyPart();
		attachment0.setContent(corpoMensagem.toString(), "text/html; charset=iso-8859-1");
		multipart.addBodyPart(attachment0);

		if (anexos != null && anexos.size() > 0){			
			for (MimeBodyPart mimeBodyPart : anexos) {
				multipart.addBodyPart(mimeBodyPart);				
			}	
		}

		msg.setContent(multipart);

		log.debug("enviando...");
		// Transport.send(msg);

		Transport tr = sessao.getTransport(protocolo);
		tr.connect(this.smtpHost, this.smtpUsuario, this.senha);

		msg.saveChanges();

		tr.sendMessage(msg, msg.getAllRecipients());
		tr.close();

		log.debug("email enviado com sucesso");
	}

	private class SMTPAuthenticator extends Authenticator {
		private PasswordAuthentication passwordAuthentication;

		public SMTPAuthenticator(String user, String password) {
			passwordAuthentication = new PasswordAuthentication(user, password);
		}

		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			return passwordAuthentication;
		}

	}

	/**
	 * converte a lista de e-mails passado como parâmetro para
	 * List<InternetAddress>
	 * 
	 * @param emailDestinatario
	 * @return
	 * @throws AddressException
	 */
	private Address[] convertStringToInternetAdrres(List<String> emailDestinatario) throws AddressException {

		List<InternetAddress> address = new ArrayList<InternetAddress>();

		for (String emails : emailDestinatario) {
			address.add(new InternetAddress(emails.trim().toLowerCase()));
		}

		return (Address[]) address.toArray(new Address[address.size()]);
	}

	/**
	 * Converte a lista passada como parâmetro para List<InternetAddress>, os
	 * e-mail devem ser passados entre virgulas ou ponto e virgula
	 * 
	 * @param emails
	 * @return
	 * @throws AddressException
	 */
	private Address[] convertStringToInternetAdrres(String emails) throws AddressException {

		if (emails != null && !emails.equals("")) {

			String[] emailDestinatario = null;

			// se veio concatenado com virgulas
			if (emails.contains(","))
				emailDestinatario = emails.split(",");

			// se veio concatenado com ponto e virgulas
			if (emails.contains(";"))
				emailDestinatario = emails.split(";");

			// se veio somente um e-mail
			if (emailDestinatario == null) {
				emailDestinatario = new String[1];
				emailDestinatario[0] = emails;
			}

			List<InternetAddress> address = new ArrayList<InternetAddress>();

			for (int i = 0; i < emailDestinatario.length; i++) {

				String mail = emailDestinatario[i].trim().toLowerCase();

				if (mail == null || mail.trim().equals(""))
					continue;

				if (!address.contains(new InternetAddress(mail)))
					address.add(new InternetAddress(mail));
			}

			return (Address[]) address.toArray(new Address[address.size()]);
		} else {
			return null;
		}
	}

	public Address[] convertStringToInternetAddres(String email, String nome) throws UnsupportedEncodingException {

		List<InternetAddress> address = new ArrayList<InternetAddress>();
		address.add(new InternetAddress(email, nome));

		return (Address[]) address.toArray(new Address[address.size()]);

	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

}
