package com.ainnotate.aidas.config;

import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.repository.AuthorityRepository;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.IdpUserService;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@WebFilter(urlPatterns = {"/*" },asyncSupported = true)
public class IdpSyncFilter implements Filter {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private IdpUserService idpUserService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        Map<String,Object> claims = ((JwtAuthenticationToken)authentication).getToken().getClaims();
        try {
            user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
            if ((user != null && user.getKeycloakId() == null) || (user!=null && user.getKeycloakId()!=null && user.getKeycloakId().equals("test"))) {
                user.setKeycloakId(claims.get("sid").toString());
                userRepository.save(user);
                UserResource myUser = usersRessource.get(user.getKeycloakId());
                List<String> userAttrsVals = new ArrayList<>();
                if(user.getAuthority()!=null && user.getAuthority().getName()!=null) {
                    userAttrsVals.add(user.getAuthority().getName());
                }
                else {
                    Authority authority = authorityRepository.getById(6l);
                    userAttrsVals.add(authority.getName());
                }
                userAttrsVals.add(String.valueOf(user.getId()));
                if (myUser.toRepresentation().getAttributes() != null) {
                    myUser.toRepresentation().getAttributes().put("current_role", userAttrsVals);
                    myUser.toRepresentation().getAttributes().put("id", userAttrsVals);
                } else {
                    Map<String, List<String>> userAttrs = new HashMap<>();
                    userAttrs.put("current_role",userAttrsVals);
                    userAttrs.put("id",userAttrsVals);
                    myUser.toRepresentation().setAttributes(userAttrs);
                }
            }else{

            }
        }catch (Exception e){
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken authToken =  ((JwtAuthenticationToken) authentication);
                idpUserService.saveNewUserFromAuthentication(authToken);
            }
        }*/
        chain.doFilter(req,res);
    }
}
