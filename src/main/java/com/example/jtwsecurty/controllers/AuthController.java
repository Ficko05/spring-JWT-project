package com.example.jtwsecurty.controllers;


import com.example.jtwsecurty.dto.AuthResponseDto;
import com.example.jtwsecurty.dto.LoginDto;
import com.example.jtwsecurty.dto.RegisterDto;
import com.example.jtwsecurty.entitys.Roles;
import com.example.jtwsecurty.entitys.UserEntity;
import com.example.jtwsecurty.repository.RoleRepository;
import com.example.jtwsecurty.repository.UserRepository;
import com.example.jtwsecurty.security.JwtTokenGenerator;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenGenerator jwtTokenGenerator;

// not needed but this was his constructor in video
//@Autowired
//public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository,
//                      RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenGenerator jwtGenerator) {
//    this.authenticationManager = authenticationManager;
//    this.userRepository = userRepository;
//    this.roleRepository = roleRepository;
//    this.passwordEncoder = passwordEncoder;
//    this.jwtTokenGenerator = jwtGenerator;
//}


    // login endpoint
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        // authenticate user and return the details
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));


        // holds all the authtication details
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenGenerator.generateToken(authentication);

        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);

    }




    // validate Bearer token
    @GetMapping("/validate")
    public ResponseEntity<String> validate(HttpServletRequest request){

        // Retrieve the Authorization header from the request
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Extract the token from the Authorization header
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

            // TODO: Implement token validation logic
            // You can use your preferred method for token validation, such as JWT validation or session-based validation.
            // Perform the necessary validation checks, such as token expiration, signature verification, or database lookups.
            if(jwtTokenGenerator.validateToken(token)){
                // If the token is valid, return a success response
                return ResponseEntity.ok("Token is valid");
            }

            // If the token is invalid, return an error response
            // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        // If the Authorization header is missing or does not start with "Bearer ",
        // return an error response
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");

    }

    // register user endpoint
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if(userRepository.existsByUsername(registerDto.getUsername())){
            return ResponseEntity.badRequest().body("Username is already taken");
        }
        UserEntity user = new UserEntity();

        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        Roles roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);


        return new ResponseEntity<>("User created", HttpStatus.OK);
    }



}
