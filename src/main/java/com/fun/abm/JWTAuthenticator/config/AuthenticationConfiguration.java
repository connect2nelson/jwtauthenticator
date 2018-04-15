package com.fun.abm.JWTAuthenticator.config;

import com.fun.abm.JWTAuthenticator.domain.JWTAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class AuthenticationConfiguration extends WebMvcConfigurerAdapter {

    private JWTAuthenticator jwtAuthenticator;

    @Autowired
    public AuthenticationConfiguration(JWTAuthenticator jwtAuthenticator) {
        this.jwtAuthenticator = jwtAuthenticator;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        registry.addInterceptor(jwtAuthenticator);
    }
}
