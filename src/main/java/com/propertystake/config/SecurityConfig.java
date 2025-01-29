package com.propertystake.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



import java.util.List;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    // Configuration de la sécurité
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Nouvelle syntaxe pour désactiver CSRF
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Autoriser toutes les requêtes
                .httpBasic(httpBasic -> httpBasic.disable()); // Désactiver l'authentification Basic
        return http.build();
    }

    // Configuration des converters pour gérer "application/json;charset=UTF-8"
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setSupportedMediaTypes(List.of(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8));
        converters.add(jsonConverter);
    }

}
