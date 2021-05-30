package com.cinemaapp.demo.Service;

import com.cinemaapp.demo.dao.BookingRepository;
import com.cinemaapp.demo.dao.MovieRepository;
import com.cinemaapp.demo.dao.ScreeningRepository;
import com.cinemaapp.demo.dto.MovieDto;
import com.cinemaapp.demo.dto.mapper.MovieMapper;
import com.cinemaapp.demo.entity.Movie;
import com.cinemaapp.demo.entity.Screening;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MovieServiceImpl implements MovieService{

    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;
    private final BookingRepository bookingRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public MovieServiceImpl(MovieRepository movieRepository, ScreeningRepository screeningRepository, BookingRepository bookingRepository) {
        this.movieRepository = movieRepository;
        this.screeningRepository = screeningRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public MovieDto save(MovieDto movieDto) {

        if(!movieRepository.findByName(movieDto.getName()).equals(Optional.empty()))
            return null;
        Movie movie= MovieMapper.from(movieDto);
        movie.setScreeningSet(new HashSet<>());
        movieRepository.save(movie);
        return MovieMapper.from(movie);
    }

    @Override
    public Optional<MovieDto> save(Long id, MovieDto movieDto) {
        Optional<Movie> byID=movieRepository.findById(id);
        byID.ifPresent(movie -> {
            movie.setName(movieDto.getName());
            movie.setDescription(movieDto.getDescription());
            movie.setGenre(movieDto.getGenre());
            movie.setPhoto(movieDto.getPhoto());
            Set<Screening> screeningSet = new HashSet<>();
            movieDto.getScreeningSetIDs().forEach(screeningID -> {
                screeningRepository.findById(screeningID).ifPresent(screeningSet::add);
            });
            movie.setScreeningSet(screeningSet);
        });
        movieRepository.save(byID.orElseThrow());
        return byID.map(MovieMapper::from);
    }

    @Override
    public List<MovieDto> findAll() {
        return MovieMapper.from(movieRepository.findAll());
    }

    @Override
    public Optional<MovieDto> findById(Long id) {
        Optional<Movie> movie=movieRepository.findById(id);
        return movie.map(MovieMapper::from);
    }

    @Override
    public MovieDto findByTitle(String title) {
        return MovieMapper.from(movieRepository.findByName(title).orElseThrow());
    }

    @Override
    public List<MovieDto> findAllByGenre(String genre) {

        return MovieMapper.from(movieRepository.findAllByGenre(genre));
    }

    @Override
    public void deleteById(Long id) {
        Optional<Movie> movie=movieRepository.findById(id);
        movie.ifPresent(movie1 -> {
            movie1.getScreeningSet().forEach(screening -> {
                bookingRepository.findAllByScreening(screening).forEach(booking->bookingRepository.deleteById(booking.getID()));
                screeningRepository.deleteById(screening.getID());
            });

        });
        movieRepository.deleteById(id);
    }
}
