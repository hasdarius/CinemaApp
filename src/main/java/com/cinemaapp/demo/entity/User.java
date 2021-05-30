package com.cinemaapp.demo.entity;

import lombok.*;

import javax.persistence.*;

import java.util.Set;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String username;
    private String password;
    @OneToMany(mappedBy="user")
    private Set<Booking> bookingSet;


}
