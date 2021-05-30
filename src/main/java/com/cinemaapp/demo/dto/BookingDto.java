package com.cinemaapp.demo.dto;


import com.cinemaapp.demo.entity.Booking;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class BookingDto {
    private Long ID;
    private Long userID;
    Set<Long> selectedSeatsIds;
    private Long screeningID;

    public BookingDto(){
        selectedSeatsIds=new HashSet<>();
    }
}
