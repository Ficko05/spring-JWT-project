package com.example.jtwsecurty.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    private CustomUserDetailsService  customUserDetailsService;

    // DofilterInternal() method is invoked for every request that is passed through the filter chain. to check if there is a token in the header
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = getJWTFromRequest(request);
        //validate the token. this is also a security filter chain of sorts
        if(StringUtils.hasText(token) && jwtTokenGenerator.validateToken(token) ){
            String username = jwtTokenGenerator.getUsernameFromToken(token);
            //load the user details
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            //authenticate the user
            UsernamePasswordAuthenticationToken  authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            // set the authentication details in the request object
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // set the authentication in the security context
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        }
        // this is where the request is passed to the next filter in the chain
        filterChain.doFilter(request, response);

    }

    private String getJWTFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}
