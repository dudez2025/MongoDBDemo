package com.example.mongodbdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                // Публичные endpoint'ы
                .antMatchers("/api/public/**").permitAll()
                // Все остальные endpoint'ы требуют аутентификации
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/api/public/info", true)
                .and()
            .logout()
                .permitAll()
                .logoutSuccessUrl("/api/public/info")
                .and()
            .csrf().disable();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                // Пользователи для демонстрации безопасности на уровне методов
                .withUser("reader")
                    .password(passwordEncoder().encode("reader"))
                    .roles("READ")
                .and()
                .withUser("writer")
                    .password(passwordEncoder().encode("writer"))
                    .roles("WRITE")
                .and()
                .withUser("deleter")
                    .password(passwordEncoder().encode("deleter"))
                    .roles("DELETE")
                .and()
                .withUser("editor")
                    .password(passwordEncoder().encode("editor"))
                    .roles("READ", "WRITE")
                .and()
                .withUser("admin")
                    .password(passwordEncoder().encode("admin"))
                    .roles("READ", "WRITE", "DELETE")
                .and()
                // Пользователь из предыдущего задания
                .withUser("user")
                    .password(passwordEncoder().encode("password"))
                    .roles("USER");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}