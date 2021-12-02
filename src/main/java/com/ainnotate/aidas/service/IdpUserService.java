package com.ainnotate.aidas.service;

import com.ainnotate.aidas.config.Constants;
import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.AidasAuthority;
import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.repository.AidasAuthorityRepository;
import com.ainnotate.aidas.repository.AidasUserRepository;
import com.ainnotate.aidas.repository.search.AidasUserSearchRepository;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

/**
 * Service class for managing users.
 */
@Service
public class IdpUserService {

    private final Logger log = LoggerFactory.getLogger(IdpUserService.class);

    private final AidasUserRepository aidasUserRepository;

    private final AidasUserSearchRepository aidasUserSearchRepository;

    private final AidasAuthorityRepository aidasAuthorityRepository;

    private final Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    public IdpUserService(Keycloak keycloak, AidasUserRepository aidasUserRepository, AidasUserSearchRepository aidasUserSearchRepository, AidasAuthorityRepository aidasAuthorityRepository) {
        this.aidasUserRepository = aidasUserRepository;
        this.aidasUserSearchRepository = aidasUserSearchRepository;
        this.aidasAuthorityRepository = aidasAuthorityRepository;
        this.keycloak = keycloak;
    }



    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<AidasAuthority> getAuthorities() {
        return aidasAuthorityRepository.findAll();
    }

    /**
     * Returns the user from an OAuth 2.0 login or resource server with JWT.
     * Synchronizes the user in the local repository.
     *
     * @param authToken the authentication token.
     * @return the user from the authentication.
     */
    @Transactional
    public AidasUser saveNewUserFromAuthentication(AbstractAuthenticationToken authToken) {
        Map<String, Object> attributes;
        if (authToken instanceof OAuth2AuthenticationToken) {
            attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
        } else if (authToken instanceof JwtAuthenticationToken) {
            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }
        AidasUser aidasUser = getUser(attributes);
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(aidasUser.getKeycloakId());
        UserRepresentation userRep = userResource.toRepresentation();
        Collection<GrantedAuthority> grantedAuthorities = authToken.getAuthorities();
        AidasAuthority currentAidasAuthority=null;
        for(GrantedAuthority ga: grantedAuthorities){
            AidasAuthority aidasAuthority = aidasAuthorityRepository.findByName(ga.getAuthority().trim());
            if(aidasAuthority==null){
                aidasAuthority = new AidasAuthority();
                aidasAuthority.setName(ga.getAuthority());
                aidasAuthorityRepository.save(aidasAuthority);
            }
            aidasUser.getAidasAuthorities().add(aidasAuthority);
            currentAidasAuthority= aidasAuthority;
        }
        aidasUser.setLocked(false);
        aidasUser.setCreatedDate(Instant.now());
        aidasUser.setLastModifiedDate(Instant.now());
        aidasUser.setCurrentAidasAuthority(currentAidasAuthority);
        AidasUser result = aidasUserRepository.save(aidasUser);
        if(userRep.getAttributes()!=null) {
            List<String> userAttrsVals = new ArrayList<>();
            userAttrsVals.add(currentAidasAuthority.getName());
            userRep.getAttributes().put("current_role", userAttrsVals);
            userAttrsVals = new ArrayList<>();
            userAttrsVals.add(String.valueOf(result.getId()));
            userRep.getAttributes().put("aidas_id", userAttrsVals);
        }
        else{
            Map<String,List<String>> userAttrs = new HashMap<>();
            List<String> userAttrsVals = new ArrayList<>();
            userAttrsVals.add(currentAidasAuthority.getName());
            userAttrs.put("current_role",userAttrsVals);
            userAttrsVals = new ArrayList<>();
            userAttrsVals.add(String.valueOf(result.getId()));
            userAttrs.put("aidas_id",userAttrsVals);
            userRep.setAttributes(userAttrs);
        }
        userResource.update(userRep);
        return  aidasUserRepository.save(aidasUser);
    }

    private static AidasUser getUser(Map<String, Object> details) {
        AidasUser aidasUser = new AidasUser();
        Boolean activated = Boolean.TRUE;
        if (details.get("uid") != null) {
            aidasUser.setKeycloakId((String) details.get("uid"));
            aidasUser.setLogin((String) details.get("sub"));
        } else {
            aidasUser.setKeycloakId((String) details.get("sub"));
        }

        if (details.get("preferred_username") != null) {
            aidasUser.setLogin(((String) details.get("preferred_username")).toLowerCase());
        } else if (aidasUser.getLogin() == null) {
            aidasUser.setLogin(aidasUser.getLogin());
        }
        if (details.get("given_name") != null) {
            aidasUser.setFirstName((String) details.get("given_name"));
        } else if (details.get("name") != null) {
            aidasUser.setFirstName((String) details.get("name"));
        }
        if (details.get("family_name") != null) {
            aidasUser.setLastName((String) details.get("family_name"));
        }
        if (details.get("email_verified") != null) {
            activated = (Boolean) details.get("email_verified");
        }
        if (details.get("email") != null) {
            aidasUser.setEmail(((String) details.get("email")).toLowerCase());
        } else {
            aidasUser.setEmail((String) details.get("sub"));
        }


        if (details.get("langKey") != null) {
            aidasUser.setLangKey((String) details.get("langKey"));
        } else if (details.get("locale") != null) {
            // trim off country code if it exists
            String locale = (String) details.get("locale");
            if (locale.contains("_")) {
                locale = locale.substring(0, locale.indexOf('_'));
            } else if (locale.contains("-")) {
                locale = locale.substring(0, locale.indexOf('-'));
            }
            aidasUser.setLangKey(locale.toLowerCase());
        } else {
            // set langKey to default if not specified by IdP
            aidasUser.setLangKey(Constants.DEFAULT_LANGUAGE);
        }
        if (details.get("picture") != null) {
            aidasUser.setImageUrl((String) details.get("picture"));
        }
        aidasUser.setActivated(activated);
        return aidasUser;
    }
}
