package com.example.Terminal_rev42.Config;

import com.example.Terminal_rev42.SecurityCustomImpl.CustomAuthenticationFailureHandler;
import com.example.Terminal_rev42.SecurityCustomImpl.CustomAuthenticationSuccessHandler;
import com.example.Terminal_rev42.SecurityCustomImpl.HttpSessionListener;
import com.example.Terminal_rev42.SeviceImplementation.UserDetailedServiceImpl;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig{

    //@Qualifier("UserDetailsServiceImpl")  // our override userDetailsService (implementing of UDS) cauze we renamed it

    @Autowired
    private UserDetailedServiceImpl userDetailedService;

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

                .antMatchers("/Barclays/Bill/**", "/Barclays/service/**")
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
                .failureHandler(authenticationFailureHandler())   // if we have logged in successfully that handler will process this case
                .successHandler(authenticationSuccessHandler())  // if we have failed bid to login
                .permitAll()

                .and()
                .rememberMe() // ACTIVATE remember me property
                .key("uniqueAndSecret")   // can add TokenRepository to save all user tokens
                .tokenValiditySeconds(86400) // 24 hours

                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")   // delete info about user session after logout
                .logoutSuccessUrl("/Barclays")

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)   // add Policy to create a new Session ONLY if it's required
                .sessionFixation().migrateSession()   // If we try to login again and exceed max count of sessions -> migrate session (create new, none etc)  // can be custom
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false) // throw SessionAuthenticationException if the number of sessions exceed maximum; we manage this situation (if 'false' -> after our login again with already open session previous sess will be destroyed without warning)
                .expiredUrl("/Barclays/authorisation?message=Session%20expired.") // add Page if session become expired during
                .sessionRegistry(sessionRegistry());  // define session register which will create and drop all sessions in application

        return http.build();
    }

    @Bean
    public AuthenticationManager customAuthenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder) throws Exception{
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailedService)
                .passwordEncoder(bCryptPasswordEncoder)
                .userDetailsPasswordManager(userDetailsPasswordService)
                .and()
                .build();
    }


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

    @Bean
    public SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler();
    }

}




