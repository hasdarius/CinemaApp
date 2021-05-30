package com.cinemaapp.demo.entity;

import lombok.*;

import javax.persistence.*;

import java.util.Set;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;
    @ManyToMany
    @JoinTable(
            name = "selected_seats",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "seat_id"))
    Set<Seat> selectedSeats;
    @ManyToOne
    @JoinColumn(name="screening_id", nullable=false)
    private Screening screening;

}
