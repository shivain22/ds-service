package com.ainnotate.aidas;

import com.ainnotate.aidas.config.ApplicationProperties;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import javax.annotation.PostConstruct;

import com.ainnotate.aidas.config.KeycloakConfig;
import com.ainnotate.aidas.domain.Authority;
import com.ainnotate.aidas.domain.User;
import com.ainnotate.aidas.repository.AuthorityRepository;
import com.ainnotate.aidas.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.core.env.Environment;
import tech.jhipster.config.DefaultProfileUtil;
import tech.jhipster.config.JHipsterConstants;

@SpringBootApplication
@ServletComponentScan
@EnableConfigurationProperties({KeycloakConfig.class, LiquibaseProperties.class, ApplicationProperties.class })
public class AinnotateserviceApp {

    private static final Logger log = LoggerFactory.getLogger(AinnotateserviceApp.class);

    private final Environment env;

    public AinnotateserviceApp(Environment env) {
        this.env = env;
    }

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private KeycloakConfig keycloakConfig;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;


    /**
     * Initializes ainnotateservice.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @PostConstruct
    public void initApplication() {
        RealmResource realmResource = keycloak.realm(keycloakConfig.getClientRealm());
        UsersResource usersRessource = realmResource.users();
        List<User> aidasUsers =  userRepository.findAll();
        List<UserRepresentation> users = usersRessource.list();
        for(UserRepresentation user:users) {
            if (userRepository.findByLogin(user.getUsername()).isPresent()) {

                User myUser = userRepository.findByLogin(user.getUsername()).get();
                if (!myUser.getKeycloakId().equals(user.getId())) {
                    myUser.setKeycloakId(user.getId());
                    userRepository.save(myUser);
                }
                List<String> userAttrsVals = new ArrayList<>();
                if(myUser.getAuthority()!=null && myUser.getAuthority().getName()!=null) {
                    userAttrsVals.add(myUser.getAuthority().getName());
                }
                else {
                    Authority authority = authorityRepository.getById(6l);
                    userAttrsVals.add(authority.getName());
                }
                List<String> userAttrVals1 = new ArrayList<>();
                userAttrVals1.add(String.valueOf(user.getId()));
                if (user.getAttributes() != null) {
                    user.getAttributes().put("current_role", userAttrsVals);
                    user.getAttributes().put("id", userAttrVals1);
                } else {
                    Map<String, List<String>> userAttrs = new HashMap<>();
                    userAttrs.put("current_role",userAttrsVals);
                    userAttrs.put("id",userAttrVals1);
                    user.setAttributes(userAttrs);
                }
                UserResource userResource = usersRessource.get(myUser.getKeycloakId());
                userResource.update(user);
            }
        }
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)
        ) {
            log.error(
                "You have misconfigured your application! It should not run " + "with both the 'dev' and 'prod' profiles at the same time."
            );
        }
        if (
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) &&
            activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)
        ) {
            log.error(
                "You have misconfigured your application! It should not " + "run with both the 'dev' and 'cloud' profiles at the same time."
            );
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AinnotateserviceApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = Optional.ofNullable(env.getProperty("server.ssl.key-store")).map(key -> "https").orElse("http");
        String serverPort = env.getProperty("server.port");
        String contextPath = Optional
            .ofNullable(env.getProperty("server.servlet.context-path"))
            .filter(StringUtils::isNotBlank)
            .orElse("/");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info(
            "\n----------------------------------------------------------\n\t" +
            "Application '{}' is running! Access URLs:\n\t" +
            "Local: \t\t{}://localhost:{}{}\n\t" +
            "External: \t{}://{}:{}{}\n\t" +
            "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles()
        );

        String configServerStatus = env.getProperty("configserver.status");
        if (configServerStatus == null) {
            configServerStatus = "Not found or not setup for this application";
        }
        log.info(
            "\n----------------------------------------------------------\n\t" +
            "Config Server: \t{}\n----------------------------------------------------------",
            configServerStatus
        );
    }
}
