package com.example.UberProject_AuthService.services;

import com.example.UberProject_AuthService.helpers.AuthPassengerDetails;
import com.example.UberProject_AuthService.models.Passenger;
import com.example.UberProject_AuthService.repositories.PassengerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*
  this class is responsible for loading the user in the form of UserDetails object for auth
 */

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PassengerRepository passengerRepository;

    public UserDetailsServiceImpl(PassengerRepository passengerRepository) {
        this.passengerRepository = passengerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Passenger> passenger=passengerRepository.findPassengerByEmail(username);
        //email is unique identifier
        if(passenger.isPresent())
        {
            return new AuthPassengerDetails(passenger.get());
        }
        else{
            throw new UsernameNotFoundException("Cannot find the passenger by the given Email");
        }
    }
}
