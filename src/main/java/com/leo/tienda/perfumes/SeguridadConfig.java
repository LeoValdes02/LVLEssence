package com.leo.tienda.perfumes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Configuration
public class SeguridadConfig {
    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean(name = "filterMultipartResolver") StandardServletMultipartResolver filterMultipartResolver() {
        return new StandardServletMultipartResolver();
    }
    @Bean FilterRegistrationBean<MultipartFilter> multipartBeforeSecurity() {
        // Los formularios con imagen deben exponer su token CSRF antes del filtro de seguridad.
        FilterRegistrationBean<MultipartFilter> registration = new FilterRegistrationBean<>(new MultipartFilter());
        registration.setOrder(-101);
        return registration;
    }

    @Bean SecurityFilterChain seguridad(HttpSecurity http) throws Exception {
        // Spring Security aporta CSRF; la autorización administrativa sigue en InterceptorAdmin.
        return http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .build();
    }
}
