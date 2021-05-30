package com.cinemaapp.demo.dto;


import com.cinemaapp.demo.entity.Seat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class ScreeningDto {
    private Long ID;
    private Date time;
    private Long movieID;
    Set<Long> availableSeatsIDs;
    private Set<Long> bookingSetIDs;

    public ScreeningDto(){
        availableSeatsIDs=new HashSet<>();
        bookingSetIDs=new HashSet<>();
    }
}
