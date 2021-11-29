package com.ainnotate.aidas.config;

import com.ainnotate.aidas.domain.AidasUser;
import com.ainnotate.aidas.repository.AidasUserRepository;
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
@WebFilter(urlPatterns = {"/*" })
public class IdpSyncFilter implements Filter {

    @Autowired
    AidasUserRepository aidasUserRepository;

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
        AidasUser aidasUser = null;
        try {
            aidasUser = aidasUserRepository.findByLogin(SecurityUtils.getCurrentUserLogin().get()).get();
        }catch (Exception e){
            if (authentication instanceof JwtAuthenticationToken) {
                JwtAuthenticationToken authToken =  ((JwtAuthenticationToken) authentication);
                idpUserService.saveNewUserFromAuthentication(authToken);
            }
        }
        chain.doFilter(req,res);
    }
}
