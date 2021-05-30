package com.cinemaapp.demo.dao;

import com.cinemaapp.demo.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie,Long> {
    Optional<Movie> findByName(String name);
    List<Movie> findAllByGenre(String genre);

}
