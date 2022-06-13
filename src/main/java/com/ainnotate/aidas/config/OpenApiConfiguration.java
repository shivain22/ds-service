package com.ainnotate.aidas.config;

//import static springfox.documentation.builders.PathSelectors.regex;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
/*import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;*/
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.apidoc.customizer.SpringfoxCustomizer;

@Configuration
@Profile(JHipsterConstants.SPRING_PROFILE_API_DOCS)
public class OpenApiConfiguration {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
            .group("default")
            .pathsToMatch( "/**/v3/**","/api/**","/management/**","/v2/api-docs","/v3/api-docs","/swagger-resources","/swagger-ui/**")
            .build();
    }
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
            .info(new Info().title("SpringShop API")
                .description("Spring shop sample application")
                .version("v0.0.1")
                .license(new License().name("Apache 2.0").url("http://springdoc.org")))
            .externalDocs(new ExternalDocumentation()
                .description("SpringShop Wiki Documentation")
                .url("https://springshop.wiki.github.org/docs"));
    }
    /*@Bean
    public SpringfoxCustomizer noApiFirstCustomizer() {
        return docket -> docket.select().apis(RequestHandlerSelectors.basePackage("com.ainnotate.aidas.web.api").negate());
    }

    @Bean
    public Docket apiFirstDocket(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.ApiDocs property = jHipsterProperties.getApiDocs();
        Contact contact = new Contact(property.getContactName(), property.getContactUrl(), property.getContactEmail());

        ApiInfo apiInfo = new ApiInfo(
            "API First " + property.getTitle(),
            property.getDescription(),
            property.getVersion(),
            property.getTermsOfServiceUrl(),
            contact,
            property.getLicense(),
            property.getLicenseUrl(),
            new ArrayList<>()
        );

        return new Docket(DocumentationType.OAS_30)
            .groupName("openapi")
            .host(property.getHost())
            .protocols(new HashSet<>(Arrays.asList(property.getProtocols())))
            .apiInfo(apiInfo)
            .useDefaultResponseMessages(property.isUseDefaultResponseMessages())
            .forCodeGeneration(true)
            .directModelSubstitute(ByteBuffer.class, String.class)
            .genericModelSubstitutes(ResponseEntity.class)
            .ignoredParameterTypes(Pageable.class)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.ainnotate.aidas.web.api"))
            .paths(regex(property.getDefaultIncludePattern()))
            .build();
    }*/
}
