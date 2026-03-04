package com.example.mongodbdemo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity(
    prePostEnabled = true,   // Включает @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter
    securedEnabled = true,   // Включает @Secured
    jsr250Enabled = true     // Включает @RolesAllowed
)
public class MethodSecurityConfig {
}