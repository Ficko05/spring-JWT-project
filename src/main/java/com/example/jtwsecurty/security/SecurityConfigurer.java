package com.example.jtwsecurty.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
// let's spring know this is a security configuration file
@EnableWebSecurity
public class SecurityConfigurer   {


    private JwtAuthEnteryPoint jwtAuthEnteryPoint;

    // here we inject the custom user details service, idk if autowierd is better here??
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    public SecurityConfigurer(CustomUserDetailsService customUserDetailsService, JwtAuthEnteryPoint  jwtAuthEnteryPoint) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthEnteryPoint = jwtAuthEnteryPoint;
    }



    @Bean
    // this is a customization of the default spring security configuration
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // csrf is disabled to allow for testing and to avoid issues down the line
        http
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthEnteryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // cant have settions with JWT
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll() // we can permit this by roles and others
                // any request need to me authenticated
                .anyRequest().authenticated()
                .and()
                .httpBasic();  // httpbasic is just to keep it http rather than https

        // this is the filter that we add to the chain of filters
        http.addFilterBefore(jwtAuthenticatoinFilter(), UsernamePasswordAuthenticationFilter.class);
        // this build a chain for us
        return http.build();

    }


//    @Bean
//    // this is the in memory users
//    public UserDetailsService users(){
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password("admin")
//                .roles("ADMIN")
//                .build();
//        UserDetails user = User.builder()
//                .username("user")
//                .password("user")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin,user);
//    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // password encoder
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public JWTAuthenticationFilter jwtAuthenticatoinFilter(){
        return new JWTAuthenticationFilter();
    }


}
