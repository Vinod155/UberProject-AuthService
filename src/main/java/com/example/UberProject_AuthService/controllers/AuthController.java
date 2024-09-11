package com.example.UberProject_AuthService.controllers;

import com.example.UberProject_AuthService.dto.AuthRequestDto;
import com.example.UberProject_AuthService.dto.AuthResponseDto;
import com.example.UberProject_AuthService.dto.PassengerDTO;
import com.example.UberProject_AuthService.dto.PassengerSignupRequestDTO;
import com.example.UberProject_AuthService.services.AuthService;
import com.example.UberProject_AuthService.services.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Value("${cookie.expiry}")
    private int cookieExpiry;

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }


    @PostMapping("/signup/passenger")
    public ResponseEntity<?> signUp(@RequestBody  PassengerSignupRequestDTO passengerSignupRequestDTO)
    {
        PassengerDTO response=authService.signUpPassenger(passengerSignupRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/signin/passenger")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDto authRequestDto, HttpServletResponse response)
    {
        Authentication authentication=this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getEmail(),authRequestDto.getPassword()));
        if(authentication.isAuthenticated())
        {
             //it will add custom with value 12345 without even returning as it gets automatically returned
            String jwtToken=jwtService.createToken(authRequestDto.getEmail());
            ResponseCookie cookie=ResponseCookie.from("JwtToken",jwtToken)
                                   .httpOnly(true)
                                    .secure(false)
                                     .path("/")
                                    .maxAge(cookieExpiry).build();
            response.setHeader(HttpHeaders.SET_COOKIE,cookie.toString());

            return new ResponseEntity<>(AuthResponseDto.builder().success(true).build(),HttpStatus.OK);
        }
        else {
            throw new UsernameNotFoundException("User Not Found");
        }
      
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request)
    {
        for(Cookie cookie:request.getCookies())
        {
            System.out.println(cookie.getName()+" "+cookie.getValue());
        }
       return new ResponseEntity<>("Success",HttpStatus.OK);
    }
}
