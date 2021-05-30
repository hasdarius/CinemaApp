package com.cinemaapp.demo.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @DateTimeFormat(pattern = "dd/MM/yyyy h:mm a")
    private Date time;
    @ManyToOne
    @JoinColumn(name="movie_id", nullable=false)
    private Movie movie;
    @ManyToMany
    @JoinTable(
            name = "available_seats",
            joinColumns = @JoinColumn(name = "screening_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id"))
    Set<Seat> availableSeats;
    @OneToMany(mappedBy="screening")
    private Set<Booking> bookingSet;


}
