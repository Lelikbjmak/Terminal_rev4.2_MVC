package com.example.Terminal_rev42.Config;

import com.example.Terminal_rev42.SeviceImplementation.UserDetailsPasswordServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig{

    @Qualifier("UserDetailsServiceImpl")  // our ovverride userdetilsSerice (implementing of UDS)
    @Autowired
    private UserDetailsService userDetailsService;

    @Qualifier("UserDetailsPasswordServiceImpl")
    @Autowired
    private UserDetailsPasswordServiceImpl userDetailsPasswordService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(16, new SecureRandom());
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeRequests()
//                .antMatchers(HttpMethod.DELETE)
//                .hasRole("ADMIN")

//                .antMatchers("/Barclays/admin/**")
//                .hasAnyRole("ADMIN")

                .antMatchers("/Barclays/bill/**", "/Barclays/service/**")
                .hasAnyRole("USER", "ADMIN")

                .antMatchers("/Barclays", "/Barclays/reg", "/Barclays/client/**",  "/Barclays/bad", "/Barclays/success", "/Barclays/authorisation")
                .permitAll()

                .antMatchers("/Terminal/**")
                .permitAll()

                .anyRequest()
                .authenticated()

                .and()
                .formLogin()
                .loginPage("/Barclays/authorisation")
                .defaultSuccessUrl("/Barclays")
                .failureUrl("/Barclays/authorisation?message=Invalid%20Username%20or%20Password!")
                .permitAll()

                .and()
                .rememberMe()

                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/Barclays")
                //.tokenValiditySeconds()   2 weaks - default


                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .expiredUrl("/Barclays/bad?ms=Session%20expire");




        return http.build();
    }
    @Bean
    public AuthenticationManager customAuthenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception{
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder)
                .userDetailsPasswordManager(userDetailsPasswordService)
                .and()
                .build();
    }

    @Bean  // to control sessions
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

}




