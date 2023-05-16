package com.example.jtwsecurty.controllers;


import com.example.jtwsecurty.dto.AuthResponseDto;
import com.example.jtwsecurty.dto.LoginDto;
import com.example.jtwsecurty.dto.RegisterDto;
import com.example.jtwsecurty.entitys.Roles;
import com.example.jtwsecurty.entitys.UserEntity;
import com.example.jtwsecurty.repository.RoleRepository;
import com.example.jtwsecurty.repository.UserRepository;
import com.example.jtwsecurty.security.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
