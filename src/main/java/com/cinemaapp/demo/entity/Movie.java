package com.cinemaapp.demo.entity;

import lombok.*;

import javax.persistence.*;

import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String name;
    @Column(length = 64)
    private String photo;
    private String genre;
    private String description;
    @OneToMany(mappedBy="movie")
    private Set<Screening> screeningSet;
}
