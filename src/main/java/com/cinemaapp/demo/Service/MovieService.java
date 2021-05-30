package com.cinemaapp.demo.Service;

import com.cinemaapp.demo.dto.MovieDto;
import com.cinemaapp.demo.entity.Seat;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface MovieService {
    MovieDto save(MovieDto movieDto);

    Optional<MovieDto> save(Long id, MovieDto movieDto);

    List<MovieDto> findAll();

    Optional<MovieDto> findById(Long id);

    MovieDto findByTitle(String title);

    List<MovieDto> findAllByGenre(String genre);

    void deleteById(Long id);
}
