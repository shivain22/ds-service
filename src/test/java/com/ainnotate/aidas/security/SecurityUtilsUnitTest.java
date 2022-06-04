package com.ainnotate.aidas.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;

import java.time.Instant;
import java.util.*;

import com.ainnotate.aidas.constants.AidasConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

/**
 * Test class for the {@link SecurityUtils} utility class.
 */
class SecurityUtilsUnitTest {

    @BeforeEach
    @AfterEach
    void cleanup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        Optional<String> login = SecurityUtils.getCurrentUserLogin();
        assertThat(login).contains("admin");
    }

    @Test
    void testGetCurrentUserLoginForOAuth2() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Map<String, Object> claims = new HashMap<>();
        claims.put("groups", AidasConstants.VENDOR_USER);
        claims.put("sub", 123);
        claims.put("preferred_username", "admin");
        OidcIdToken idToken = new OidcIdToken(ID_TOKEN, Instant.now(), Instant.now().plusSeconds(60), claims);
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AidasConstants.VENDOR_USER));
        OidcUser user = new DefaultOidcUser(authorities, idToken);
        OAuth2AuthenticationToken auth2AuthenticationToken = new OAuth2AuthenticationToken(user, authorities, "oidc");
        securityContext.setAuthentication(auth2AuthenticationToken);
        SecurityContextHolder.setContext(securityContext);

        Optional<String> login = SecurityUtils.getCurrentUserLogin();

        assertThat(login).contains("admin");
    }

    @Test
    void testExtractAuthorityFromClaims() {
        Map<String, Object> claims = new HashMap<>();
        claims.put("groups", Arrays.asList(AidasConstants.ADMIN, AidasConstants.VENDOR_USER));

        List<GrantedAuthority> expectedAuthorities = Arrays.asList(
            new SimpleGrantedAuthority(AidasConstants.ADMIN),
            new SimpleGrantedAuthority(AidasConstants.VENDOR_USER)
        );

        List<GrantedAuthority> authorities = SecurityUtils.extractAuthorityFromClaims(claims);

        assertThat(authorities).isNotNull().isNotEmpty().hasSize(2).containsAll(expectedAuthorities);
    }

    @Test
    void testExtractAuthorityFromClaims_NamespacedRoles() {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityUtils.CLAIMS_NAMESPACE + "roles", Arrays.asList(AidasConstants.ADMIN, AidasConstants.VENDOR_USER));

        List<GrantedAuthority> expectedAuthorities = Arrays.asList(
            new SimpleGrantedAuthority(AidasConstants.ADMIN),
            new SimpleGrantedAuthority(AidasConstants.VENDOR_USER)
        );

        List<GrantedAuthority> authorities = SecurityUtils.extractAuthorityFromClaims(claims);

        assertThat(authorities).isNotNull().isNotEmpty().hasSize(2).containsAll(expectedAuthorities);
    }

    @Test
    void testIsAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("admin", "admin"));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        assertThat(isAuthenticated).isTrue();
    }

    @Test
    void testAnonymousIsNotAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AidasConstants.ANONYMOUS));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("anonymous", "anonymous", authorities));
        SecurityContextHolder.setContext(securityContext);
        boolean isAuthenticated = SecurityUtils.isAuthenticated();
        assertThat(isAuthenticated).isFalse();
    }

    @Test
    void testHasCurrentUserThisAuthority() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AidasConstants.VENDOR_USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        assertThat(SecurityUtils.hasCurrentUserThisAuthority(AidasConstants.VENDOR_USER)).isTrue();
        assertThat(SecurityUtils.hasCurrentUserThisAuthority(AidasConstants.ADMIN)).isFalse();
    }

    @Test
    void testHasCurrentUserAnyOfAuthorities() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AidasConstants.VENDOR_USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        assertThat(SecurityUtils.hasCurrentUserAnyOfAuthorities(AidasConstants.VENDOR_USER, AidasConstants.ADMIN)).isTrue();
        assertThat(SecurityUtils.hasCurrentUserAnyOfAuthorities(AidasConstants.ANONYMOUS, AidasConstants.ADMIN)).isFalse();
    }

    @Test
    void testHasCurrentUserNoneOfAuthorities() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(AidasConstants.VENDOR_USER));
        securityContext.setAuthentication(new UsernamePasswordAuthenticationToken("user", "user", authorities));
        SecurityContextHolder.setContext(securityContext);

        assertThat(SecurityUtils.hasCurrentUserNoneOfAuthorities(AidasConstants.VENDOR_USER, AidasConstants.ADMIN)).isFalse();
        assertThat(SecurityUtils.hasCurrentUserNoneOfAuthorities(AidasConstants.ANONYMOUS, AidasConstants.ADMIN)).isTrue();
    }
}
