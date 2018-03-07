package br.com.neainformatica.infrastructure.controller;

import java.io.File;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jfree.util.Log;
import org.picketlink.event.IdentityConfigurationEvent;
import org.picketlink.idm.config.IdentityConfigurationBuilder;
import org.picketlink.idm.model.basic.Grant;
import org.picketlink.idm.model.basic.Group;
import org.picketlink.idm.model.basic.GroupMembership;
import org.picketlink.idm.model.basic.Realm;
import org.picketlink.idm.model.basic.Role;
import org.picketlink.idm.model.basic.User;

public class IDMConfiguration {

	@Inject
	InfrastructureController infraController;

	@SuppressWarnings("unchecked")
	public void observeIdentityConfigurationEvent(@Observes IdentityConfigurationEvent event) {

		String nomeSistema = getNameWar();

		IdentityConfigurationBuilder builder = event.getConfig();
		builder.named("default.config").stores().file().supportType(User.class, Role.class, Group.class, Realm.class)
				.supportGlobalRelationship(Grant.class, GroupMembership.class).supportCredentials(true).preserveState(true)
				.workingDirectory(File.separator + "tmp" + File.separator + nomeSistema + File.separator + "picketlink-quickstart-identity-model");
	}

	public String getNameWar() {
		String url = "";
		try {
			url = IDMConfiguration.class.getResource("").toString();
			url = url.substring(0, url.indexOf(".war"));
			String[] resultado = url.split("/");
			return resultado[resultado.length - 1].toString();
		} catch (Exception e) {
			Log.error("Não foi possivel WAR através do diretório: " + url);
			return "";
		}
	}
}