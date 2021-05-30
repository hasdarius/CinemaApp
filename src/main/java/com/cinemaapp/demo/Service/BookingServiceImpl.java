package com.cinemaapp.demo.Service;

import com.cinemaapp.demo.dao.BookingRepository;
import com.cinemaapp.demo.dao.ScreeningRepository;
import com.cinemaapp.demo.dao.SeatRepository;
import com.cinemaapp.demo.dao.UserRepository;
import com.cinemaapp.demo.dto.BookingDto;
import com.cinemaapp.demo.dto.mapper.BookingMapper;
import com.cinemaapp.demo.dto.mapper.UserMapper;
import com.cinemaapp.demo.entity.Booking;
import com.cinemaapp.demo.entity.Seat;
import com.cinemaapp.demo.entity.User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public BookingServiceImpl(BookingRepository bookingRepository, ScreeningRepository screeningRepository, UserRepository userRepository, SeatRepository seatRepository) {
        this.bookingRepository = bookingRepository;
        this.screeningRepository = screeningRepository;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
    }

    @Override
    public BookingDto save(BookingDto bookingDto) {
        Booking booking= BookingMapper.from(bookingDto);
        screeningRepository.findById(bookingDto.getScreeningID()).ifPresent(booking::setScreening);
        userRepository.findById(bookingDto.getUserID()).ifPresent(booking::setUser);
        Set<Seat> selectedSeats=new HashSet<>();
        bookingDto.getSelectedSeatsIds().forEach(id->{
            seatRepository.findById(id).ifPresent(selectedSeats::add);
        });
        booking.setSelectedSeats(selectedSeats);
        screeningRepository.findById(bookingDto.getScreeningID()).ifPresent(
                screening -> {
                    Set<Seat> availableSeats=screening.getAvailableSeats();
                    for (Seat seat : selectedSeats) {
                        if (!availableSeats.contains(seat)) {
                            selectedSeats.clear();
                            break;
                        }
                    }
                    if(!selectedSeats.isEmpty()){
                        for (Seat seat : selectedSeats) {
                            availableSeats.remove(seat);
                            }
                        screeningRepository.save(screening);
                        }
                }
        );
        if(selectedSeats.isEmpty())
            return null;
        else
        {
            bookingRepository.save(booking);
            return BookingMapper.from(booking);
        }
    }

    @Override
    public Optional<BookingDto> save(Long id, BookingDto bookingDto) {
        Optional<Booking> byId=bookingRepository.findById(id);
        byId.ifPresent(booking -> {
            screeningRepository.findById(bookingDto.getScreeningID()).ifPresent(booking::setScreening);
            userRepository.findById(bookingDto.getUserID()).ifPresent(booking::setUser);
            Set<Seat> selectedSeats=new HashSet<>();
            bookingDto.getSelectedSeatsIds().forEach(seatID-> seatRepository.findById(seatID).ifPresent(selectedSeats::add));
            screeningRepository.findById(bookingDto.getScreeningID()).ifPresent(
                    screening -> {
                        Set<Seat> availableSeats=screening.getAvailableSeats();
                        for (Seat seat : selectedSeats) {
                            if (!availableSeats.contains(seat)) {
                                selectedSeats.clear();
                                break;
                            }
                        }
                        if(!selectedSeats.isEmpty()){
                            for (Seat seat : selectedSeats) {
                                availableSeats.remove(seat);
                            }
                            screeningRepository.save(screening);
                        }
                    }
            );
        });
        if(byId.isPresent()){
            if(byId.get().getSelectedSeats().isEmpty()) {
                deleteById(id);
            }
            else
                bookingRepository.save(byId.orElseThrow());
            return byId.map(BookingMapper::from);
        }
        return Optional.empty();

    }

    @Override
    public List<BookingDto> findAll() {
        return BookingMapper.from(bookingRepository.findAll());
    }

    @Override
    public Optional<BookingDto> findById(Long id) {
        Optional<Booking> booking=bookingRepository.findById(id);
        return booking.map(BookingMapper::from);
    }

    @Override
    public List<BookingDto> findAllByUserID(Long userID) {
       if(userRepository.findById(userID).isPresent()){
           List<Booking> finalBookings=bookingRepository.findAllByUser(userRepository.findById(userID).get());
           finalBookings.removeIf(booking -> booking.getScreening().getTime().before(new Date(System.currentTimeMillis())));
            return BookingMapper.from(finalBookings);
       }
       return new LinkedList<>();
    }

    @Override
    public List<BookingDto> findAllByScreeningID(Long screeningID) {
        if(screeningRepository.findById(screeningID).isPresent()){
            return BookingMapper.from(bookingRepository.findAllByScreening(screeningRepository.findById(screeningID).get()));
        }
        return new LinkedList<>();
    }

    @Override
    public void deleteById(Long id) {
        Optional<Booking> booking=bookingRepository.findById(id);
        if(booking.isPresent()) {
            var screening = screeningRepository.findById(booking.get().getScreening().getID());
            screening.ifPresent(screening1 -> {
                screening1.getBookingSet().remove(booking.get());
                booking.get().getSelectedSeats().forEach(seat -> screening1.getAvailableSeats().add(seat));
                screeningRepository.save(screening1);
            });
            bookingRepository.deleteById(booking.get().getID());
        }

    }
}
