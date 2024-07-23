package com.example.user_app.config.oauth2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

@Configuration
public class Oauth2Config {
    @Bean
    public OidcUserService oidcUserService(){
        return new OidcUserService();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer(){
        return (web) -> web.debug(false)
                .ignoring()
                .requestMatchers("/webjars/**", "/images/**", "/css/**", "/assets/**", "/favicon.ico");
    }
}
