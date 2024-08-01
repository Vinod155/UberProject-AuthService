package com.example.UberProject_AuthService.dto;

import com.example.UberProject_AuthService.models.Passenger;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDTO {
    private String id;

    private String name;

    private String email;

    private String password;//encrypted password

    private String phoneNumber;

    private Date createAt;

    public static PassengerDTO from(Passenger p)
    {
        PassengerDTO passenger=PassengerDTO.builder()
                .email(p.getEmail())
                .name(p.getName())
                .password(p.getPassword())
                .phoneNumber(p.getPhoneNumber())
                .id(p.getId().toString())
                .createAt(p.getCreatedAt())
                .build();
        return passenger;
    }
}
