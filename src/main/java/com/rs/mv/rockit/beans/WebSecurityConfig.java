package com.rs.mv.rockit.beans;

import com.rs.mv.rockit.User;
import com.rs.mv.rockit.dao.UserDAO;
import com.rs.mv.rockit.exception.DAOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.session.SessionManagementFilter;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDAO userDAO;

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//            .antMatchers(
//                    "/",
//                    "/lib/**",
//                    "/img/**",
//                    "/css/**",
//                    "/js/**",
//                    "/fonts/**",
//                    "/favicon.png"
//            ).permitAll()
//            .anyRequest().authenticated()
//            .and()
//            .formLogin()
//            .defaultSuccessUrl("/")
//            .loginProcessingUrl("/login")
//            .loginPage("/index.html")
//            .and()
//            .logout()
//            .permitAll();
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .httpBasic()
            .authenticationEntryPoint(new HttpAuthenticationEntryPoint())
            .and()
            .authorizeRequests()
            .antMatchers(
                    "/",
                "/lib/**",
                "/img/**",
                "/css/**",
                "/js/**",
                "/fonts/**",
                "/favicon.png",
                "/healthcheck")
            .permitAll()
            .anyRequest()
            .authenticated();

        http.logout().logoutUrl("/logout").logoutSuccessUrl("/").permitAll();
        http.csrf().disable();
        // @formatter:on
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        List<User> users;
        try {
            users = userDAO.getAll();
        } catch (DAOException daoe) {
            return null;
        }

        List<UserDetails> userDetails = new ArrayList<>(users);
        return new InMemoryUserDetailsManager(userDetails);
    }}