package com.mcqueide.dockertest.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri}")
    private String authorizationUrl;

    @Value("${spring.security.oauth2.client.provider.keycloak.token-uri}")
    private String tokenUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("People API")
                .version("1.0.0")
                .description("People Management API with Keycloak Authentication"))
            .components(new Components()
                .addSecuritySchemes("keycloak", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .flows(new OAuthFlows()
                        .authorizationCode(new OAuthFlow()
                            .authorizationUrl(authorizationUrl)
                            .tokenUrl(tokenUrl)
                            .scopes(new Scopes()
                                .addString("openid", "OpenID Connect")
                                .addString("profile", "User profile")
                                .addString("email", "User email"))))))
            .addSecurityItem(new SecurityRequirement().addList("keycloak"));
    }
}