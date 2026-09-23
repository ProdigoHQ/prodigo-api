package com.licode.prodigoerp.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PathConfig {

    @Bean(name = "publicPaths")
    public List<String> publicPaths() {
        return List.of(
                "/api/1.0/auth/register",
                "/api/1.0/auth/login",
                "/api/1.0/auth/refresh",
                "/api/1.0/auth/logout",
                "/api/1.0/modules/public",
                "/api-docs",
                "/swagger-ui/**"
        );
    }

    @Bean("securePaths")
    public List<String> securePaths() {
        return List.of("/api/1.0/**");
    }
}
