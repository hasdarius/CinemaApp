package com.cinemaapp.demo.dto.mapper;

import com.cinemaapp.demo.dto.MovieDto;
import com.cinemaapp.demo.entity.Movie;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class MovieMapper {
    public static List<MovieDto> from(final List<Movie> movies) {
        if (CollectionUtils.isEmpty(movies))
            return emptyList();

        return movies
                .stream()
                .map(MovieMapper::from)
                .collect(Collectors.toList());
    }


    public static Movie from(final MovieDto movieDto) {
        if (movieDto == null)
            return null;

        final var movie = new Movie();
        movie.setID(movieDto.getID());
        movie.setPhoto(movieDto.getPhoto());
        movie.setGenre(movieDto.getGenre());
        movie.setName(movieDto.getName());
        movie.setDescription(movieDto.getDescription());

        return movie;
    }

    public static MovieDto from(final Movie movie) {
        if (movie == null)
            return null;

        final var movieDto = new MovieDto();
        movieDto.setID(movie.getID());
        movieDto.setDescription(movie.getDescription());
        movieDto.setPhoto(movie.getPhoto());
        movieDto.setGenre(movie.getGenre());
        movieDto.setName(movie.getName());
        Set<Long> screenings = new LinkedHashSet<>();
        movie.getScreeningSet().forEach(screening -> screenings.add(screening.getID()));
        movieDto.setScreeningSetIDs(screenings);
        return movieDto;
    }
}
