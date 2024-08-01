package com.example.UberProject_AuthService.services;

import com.example.UberProject_AuthService.dto.PassengerDTO;
import com.example.UberProject_AuthService.dto.PassengerSignupRequestDTO;
import com.example.UberProject_AuthService.models.Passenger;
import com.example.UberProject_AuthService.repositories.PassengerRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PassengerRepository passengerRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(PassengerRepository passengerRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.passengerRepository = passengerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public PassengerDTO signUpPassenger(PassengerSignupRequestDTO passengerSignupRequestDTO)
    {
        Passenger passenger=Passenger.builder()
                .email(passengerSignupRequestDTO.getEmail())
                .name(passengerSignupRequestDTO.getName())
                .password(bCryptPasswordEncoder.encode(passengerSignupRequestDTO.getPassword())) //do encrypt
                .phoneNumber(passengerSignupRequestDTO.getPhoneNumber())
                .build();

        passengerRepository.save(passenger);
        return PassengerDTO.from(passenger);
    }
}
