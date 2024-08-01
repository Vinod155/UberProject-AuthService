package com.example.UberProject_AuthService.controllers;

import com.example.UberProject_AuthService.dto.PassengerDTO;
import com.example.UberProject_AuthService.dto.PassengerSignupRequestDTO;
import com.example.UberProject_AuthService.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/signup/passenger")
    public ResponseEntity<?> signUp(@RequestBody  PassengerSignupRequestDTO passengerSignupRequestDTO)
    {
        PassengerDTO response=authService.signUpPassenger(passengerSignupRequestDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
