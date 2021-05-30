package com.cinemaapp.demo.dto;

import com.cinemaapp.demo.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserDto {
    private Long ID;
    private String username;
    private String password;
    private Set<Long> bookingSetIDs;

    public UserDto(){
        bookingSetIDs=new HashSet<>();
    }
}
