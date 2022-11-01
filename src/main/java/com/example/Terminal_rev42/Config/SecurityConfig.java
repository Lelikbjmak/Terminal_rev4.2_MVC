package com.example.Terminal_rev42.Config;

import com.example.Terminal_rev42.SecurityCustomImpl.CustomAuthenticationFailureHandler;
import com.example.Terminal_rev42.SecurityCustomImpl.HttpSessionListener;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

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
        http
                .cors().and()   // enable cross domain communication
                .csrf().disable()
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
                .failureHandler(authenticationFailureHandler())
                //.failureUrl("/Barclays/authorisation?message=Invalid%20Username%20or%20Password!")  we can handle it with Custom AuthenticationFailureHandler
                .permitAll()

                .and()
                .rememberMe()
                .key("uniqueAndSecret")   // can add TokenRepository to save all user tokens
                .tokenValiditySeconds(86400) // 24 hours

                .and()
                .logout()
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/Barclays")


                .and() // turn off creating session for Anonymous users
                .sessionManagement() // add Policy to create a new Session ONLY if it's required
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation().migrateSession()   // If we try to login again and exceed max count of sessions -> migrate session (create new, none etc)  // can be custom
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)  // throw SessionAuthenticationException if the number of sessions exceed maximum; we manage this situation (if 'false' -> after our login again with already open session previous sess will be destroyed without warning)
                .expiredUrl("/Barclays/authorisation?message=Session%20expired.")
                .sessionRegistry(sessionRegistry());


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

//    @Bean  // to control sessions, exactly to invalidate session after logout
//    public HttpSessionEventPublisher httpSessionEventPublisher() {
//        return new HttpSessionEventPublisher();
//    }


//    @Bean
//    public ServletListenerRegistrationBean<HttpSessionListener> sessionListenerWithMetrics() {
//        ServletListenerRegistrationBean<HttpSessionListener> listenerRegBean =
//                new ServletListenerRegistrationBean<>();
//
//        listenerRegBean.setListener(new HttpSessionListener());
//
//        return listenerRegBean;
//    }

    @Bean
    public HttpSessionListener getHttpSessionListener(){
        return new HttpSessionListener();
    }

    @Bean
        public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler(){
        return new CustomAuthenticationFailureHandler();
    }

}




