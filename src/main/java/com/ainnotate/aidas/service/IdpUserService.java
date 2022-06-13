package com.ainnotate.aidas.service;

import com.ainnotate.aidas.config.Constants;
import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.repository.AuthorityRepository;
import com.ainnotate.aidas.repository.UserRepository;
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

    private final UserRepository userRepository;

    private final AidasUserSearchRepository aidasUserSearchRepository;

    private final AuthorityRepository authorityRepository;

    private final Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    public IdpUserService(Keycloak keycloak, UserRepository userRepository, AidasUserSearchRepository aidasUserSearchRepository, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.aidasUserSearchRepository = aidasUserSearchRepository;
        this.authorityRepository = authorityRepository;
        this.keycloak = keycloak;
    }



    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<Authority> getAuthorities() {
        return authorityRepository.findAll();
    }

    /**
     * Returns the user from an OAuth 2.0 login or resource server with JWT.
     * Synchronizes the user in the local repository.
     *
     * @param authToken the authentication token.
     * @return the user from the authentication.
     */
    @Transactional
    public User saveNewUserFromAuthentication(AbstractAuthenticationToken authToken) {
        Map<String, Object> attributes;
        if (authToken instanceof OAuth2AuthenticationToken) {
            attributes = ((OAuth2AuthenticationToken) authToken).getPrincipal().getAttributes();
        } else if (authToken instanceof JwtAuthenticationToken) {
            attributes = ((JwtAuthenticationToken) authToken).getTokenAttributes();
        } else {
            throw new IllegalArgumentException("AuthenticationToken is not OAuth2 or JWT!");
        }
        User user = getUser(attributes);
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersResource = realmResource.users();
        UserResource userResource = usersResource.get(user.getKeycloakId());
        UserRepresentation userRep = userResource.toRepresentation();
        Collection<GrantedAuthority> grantedAuthorities = authToken.getAuthorities();
        Authority currentAuthority =null;
        for(GrantedAuthority ga: grantedAuthorities){
            Authority authority = authorityRepository.findByName(ga.getAuthority().trim());
            if(authority ==null){
                authority = new Authority();
                authority.setName(ga.getAuthority());
                authorityRepository.save(authority);
            }
            user.getAidasAuthorities().add(authority);
            currentAuthority = authority;
        }
        user.setLocked(false);
        user.setCreatedDate(Instant.now());
        user.setLastModifiedDate(Instant.now());
        user.setCurrentAidasAuthority(currentAuthority);
        user.setPassword(" ");
        user.setDeleted(false);
        User result = userRepository.save(user);
        if(userRep.getAttributes()!=null) {
            List<String> userAttrsVals = new ArrayList<>();
            userAttrsVals.add(currentAuthority.getName());
            userRep.getAttributes().put("current_role", userAttrsVals);
            userAttrsVals = new ArrayList<>();
            userAttrsVals.add(String.valueOf(result.getId()));
            userRep.getAttributes().put("id", userAttrsVals);
        }
        else{
            Map<String,List<String>> userAttrs = new HashMap<>();
            List<String> userAttrsVals = new ArrayList<>();
            userAttrsVals.add(currentAuthority.getName());
            userAttrs.put("current_role",userAttrsVals);
            userAttrsVals = new ArrayList<>();
            userAttrsVals.add(String.valueOf(result.getId()));
            userAttrs.put("id",userAttrsVals);
            userRep.setAttributes(userAttrs);
        }
        userResource.update(userRep);
        return  userRepository.save(user);
    }

    private static User getUser(Map<String, Object> details) {
        User user = new User();
        Boolean activated = Boolean.TRUE;
        if (details.get("uid") != null) {
            user.setKeycloakId((String) details.get("uid"));
            user.setLogin((String) details.get("sub"));
        } else {
            user.setKeycloakId((String) details.get("sub"));
        }

        if (details.get("preferred_username") != null) {
            user.setLogin(((String) details.get("preferred_username")).toLowerCase());
        } else if (user.getLogin() == null) {
            user.setLogin(user.getLogin());
        }
        if (details.get("given_name") != null) {
            user.setFirstName((String) details.get("given_name"));
        } else if (details.get("name") != null) {
            user.setFirstName((String) details.get("name"));
        }
        if (details.get("family_name") != null) {
            user.setLastName((String) details.get("family_name"));
        }
        if (details.get("email_verified") != null) {
            activated = (Boolean) details.get("email_verified");
        }
        if (details.get("email") != null) {
            user.setEmail(((String) details.get("email")).toLowerCase());
        } else {
            user.setEmail((String) details.get("sub"));
        }


        if (details.get("langKey") != null) {
            user.setLangKey((String) details.get("langKey"));
        } else if (details.get("locale") != null) {
            // trim off country code if it exists
            String locale = (String) details.get("locale");
            if (locale.contains("_")) {
                locale = locale.substring(0, locale.indexOf('_'));
            } else if (locale.contains("-")) {
                locale = locale.substring(0, locale.indexOf('-'));
            }
            user.setLangKey(locale.toLowerCase());
        } else {
            // set langKey to default if not specified by IdP
            user.setLangKey(Constants.DEFAULT_LANGUAGE);
        }
        if (details.get("picture") != null) {
            user.setImageUrl((String) details.get("picture"));
        }
        user.setActivated(activated);
        return user;
    }
}
