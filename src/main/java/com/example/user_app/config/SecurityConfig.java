package com.example.user_app.config;

import com.example.user_app.config.oauth2.Oauth2Config;
import com.example.user_app.security.AuthEntryPointJwt;
import com.example.user_app.service.OAuth2Service;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig implements WebMvcConfigurer {
    @Autowired
    AppConfig appConfig;

    @Autowired
    Oauth2Config oauth2Config;

    @Autowired
    AuthEntryPointJwt authEntryPointJwt;

    @Autowired
    OAuth2Service oAuth2Service;

    @Autowired
    JwtLogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt))
                .authorizeHttpRequests( auth ->
                    auth
//                            .requestMatchers("/auth/**").permitAll()
//                            .requestMatchers("/login").permitAll()
//                            .requestMatchers("/user/{id}").permitAll()
////                            .requestMatchers("/oauth/**").permitAll()
//                            .anyRequest().authenticated()
                            .anyRequest().permitAll()
                )
                .authenticationProvider(appConfig.authenticationProvider())
                .addFilterBefore(appConfig.authTokenFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(
                                userInfoEndpointConfig -> userInfoEndpointConfig
                                        .oidcUserService(oauth2Config.oidcUserService())
                        )
                        .successHandler((request, response, authentication) -> {
                            try {
                                DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
                                oAuth2Service.createUserfromOauth2(
                                        oidcUser.getAttribute("email"),
                                        oidcUser.getAttribute("email")
                                );
                                response.sendRedirect("/oauth/gmail/success");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
//                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(((request, response, authentication) -> {
                            SecurityContextHolder.clearContext();
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.sendRedirect("/logout-page");
                        }))
                );
        return http.build();
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(true)
                .defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:8086"));
        configuration.setAllowedMethods(List.of("GET","POST"));
        configuration.setAllowedHeaders(List.of("Authorization","Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**",configuration);

        return source;
    }



}
