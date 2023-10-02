package com.ainnotate.aidas.service;

import java.util.ArrayList;
import java.util.List;

import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

public class KeycloakAdminClientExample {

	public static void main(String[] args) throws Exception {

		Keycloak kc = KeycloakBuilder.builder() //
				.serverUrl("http://aidas-auth.atparui.com:1080/auth") //
				.realm("master")//
				.username("admin") //
				.password("admin") //
				.clientId("admin-cli") //
				.resteasyClient(new ResteasyClientBuilder().connectionPoolSize(10).build())
				.build();

		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue("test123");
		credential.setTemporary(false);

		List<UserRepresentation> users = kc.realm("aidac").users().list();
		
		for (UserRepresentation u : users) {
			if (!u.getEmail().equals("aidac@haidata.ai"))
				kc.realm("aidac").users().get(u.getId()).remove();
			//u.setRequiredActions(new ArrayList<>());
			//System.out.println(u.getId());
		}

	}
}