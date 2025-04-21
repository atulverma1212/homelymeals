package org.bits.pilani.homely.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   AuthenticationSuccessHandler successHandler) throws Exception {
//        http.authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/icon/**").permitAll() // Allow access to static resources
////                        .requestMatchers(new AntPathRequestMatcher("/css/**")).permitAll() // Allow access to CSS files
//                        .requestMatchers(new AntPathRequestMatcher("/js/**")).permitAll()  // Allow access to JavaScript files
//                        .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll() // Allow access to images
//                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
//                        .requestMatchers(new AntPathRequestMatcher("/user/**")).hasRole("USER")
//                        .anyRequest().authenticated()
//                )
//            .formLogin(form -> form
//                .loginPage("/login")
//                    .successHandler(successHandler)
//                .permitAll()
//            )
//            .logout(logout -> logout
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/login?logout")
//                .permitAll()
//            );
        http.authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}