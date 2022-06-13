package com.ainnotate.aidas.config;

import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.repository.UserRepository;
import com.ainnotate.aidas.security.SecurityUtils;
import com.ainnotate.aidas.service.IdpUserService;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@Component
@WebFilter(urlPatterns = {"/*" },asyncSupported = true)
public class IdpSyncFilter implements Filter {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private IdpUserService idpUserService;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        try {
            user = userRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
            if(user !=null && user.getKeycloakId().equals("test")){

            }
        }catch (Exception e){
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken authToken =  ((JwtAuthenticationToken) authentication);
                idpUserService.saveNewUserFromAuthentication(authToken);
            }
        }
        chain.doFilter(req,res);
    }
}
