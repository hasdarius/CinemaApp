package com.cinemaapp.demo.Service;

import com.cinemaapp.demo.dao.BookingRepository;
import com.cinemaapp.demo.dao.MovieRepository;
import com.cinemaapp.demo.dao.ScreeningRepository;
import com.cinemaapp.demo.dao.SeatRepository;
import com.cinemaapp.demo.dto.ScreeningDto;
import com.cinemaapp.demo.dto.mapper.ScreeningMapper;
import com.cinemaapp.demo.entity.Booking;
import com.cinemaapp.demo.entity.Screening;
import com.cinemaapp.demo.entity.Seat;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ScreeningServiceImpl implements ScreeningService{

    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public ScreeningServiceImpl(ScreeningRepository screeningRepository, MovieRepository movieRepository, SeatRepository seatRepository, BookingRepository bookingRepository) {
        this.screeningRepository = screeningRepository;
        this.movieRepository = movieRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ScreeningDto save(ScreeningDto screeningDto) {
        if(!screeningRepository.findAllByTime(screeningDto.getTime()).isEmpty())
            return null;
        Screening screening= ScreeningMapper.from(screeningDto);
        System.out.println(screeningDto.getMovieID());
        movieRepository.findById(screeningDto.getMovieID()).ifPresent(screening::setMovie);
        screening.setAvailableSeats(new HashSet<>(seatRepository.findAll()));
        screening.setBookingSet(new HashSet<>());
        screeningRepository.save(screening);
        return ScreeningMapper.from(screening);
    }

    @Override
    public Optional<ScreeningDto> save(Long id, ScreeningDto screeningDto) {
        Optional<Screening> byId=screeningRepository.findById(id);
        byId.ifPresent(screening -> {
            screening.setTime(screeningDto.getTime());
            movieRepository.findById(screeningDto.getMovieID()).ifPresent(screening::setMovie);
            Set<Seat> availableSeats=new HashSet<>();
            screeningDto.getAvailableSeatsIDs().forEach(seat-> seatRepository.findById(seat).ifPresent(availableSeats::add));
            Set<Booking> bookings=new HashSet<>();
            screeningDto.getBookingSetIDs().forEach(booking->bookingRepository.findById(booking).ifPresent(bookings::add));
            screening.setAvailableSeats(availableSeats);
            screening.setBookingSet(bookings);
        });
        screeningRepository.save(byId.orElseThrow());
        return byId.map(ScreeningMapper::from);
    }

    @Override
    public List<ScreeningDto> findAll() {
        return ScreeningMapper.from(screeningRepository.findAllByTimeAfter(new Date(System.currentTimeMillis())));
    }

    @Override
    public Optional<ScreeningDto> findById(Long id) {
        Optional<Screening> screening=screeningRepository.findById(id);
        return screening.map(ScreeningMapper::from);
    }

    @Override
    public List<ScreeningDto> findAllByMovieID(Long userID) {
        if(movieRepository.findById(userID).isPresent()){
            return ScreeningMapper.from(screeningRepository.findAllByMovie(movieRepository.findById(userID).get()));

        }
        return new LinkedList<>();
    }

    @Override
    public Map<Seat, Boolean> getSeatAvailabilityMapOfScreeningByID(Long id) {
        Map<Seat,Boolean> seatMap=new HashMap<>();
        Optional<Screening> screening=screeningRepository.findById(id);
        screening.ifPresent(screening1 -> {
            List<Seat> seats=seatRepository.findAll();
            Collections.sort(seats);
            seats.forEach(seat -> {
                if(screening1.getAvailableSeats().contains(seat)){
                    seatMap.putIfAbsent(seat,true);
                }
                else {
                    seatMap.putIfAbsent(seat,false);
                }
            });
        });
        return seatMap;
    }

    @Override
    public List<Date> findDatesOfAllScreenings() {
        List<Date> dates=new LinkedList<>();
        screeningRepository.findAll().forEach(screening -> dates.add(screening.getTime()));
        return dates;
    }

    @Override
    public void deleteById(Long id) {
        Optional<Screening> screening=screeningRepository.findById(id);
        screening.ifPresent(screening1 -> {
            bookingRepository.findAllByScreening(screening1).forEach(booking->bookingRepository.deleteById(booking.getID()));
        });
        screeningRepository.deleteById(id);
    }
}
