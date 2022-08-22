package com.example.Terminal_rev42.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Qualifier("UserDetailsServiceImpl")
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }


//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring()
//                .antMatchers("/Terminal/**");
//    }


    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeRequests()
                //permit any identity to visit that pages
                //.antMatchers("/Barclays", "/Barclays/reg", "/Barclays/client/add").not().fullyAuthenticated()
                .antMatchers("/","/Barclays", "/Barclays/reg", "/Barclays/client/add", "/Terminal/**").permitAll()
                .antMatchers("/Barclays/service/**").hasAnyRole("USER", "ADMIN")
                //.antMatchers("/", "/Terminal/css", "/Terminal/") for static resources
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/Barclays/authorisation")
                .defaultSuccessUrl("/Barclays/service")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/Barclays");

        }


    @Bean
    public AuthenticationManager customAuthenticationManager() throws Exception{
        return authenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }

}




