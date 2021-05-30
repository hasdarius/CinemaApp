package com.cinemaapp.demo.Service;

import com.cinemaapp.demo.dao.BookingRepository;
import com.cinemaapp.demo.dao.ScreeningRepository;
import com.cinemaapp.demo.dao.UserRepository;
import com.cinemaapp.demo.dto.UserDto;
import com.cinemaapp.demo.dto.mapper.UserMapper;
import com.cinemaapp.demo.entity.Booking;
import com.cinemaapp.demo.entity.User;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ScreeningRepository screeningRepository;
    @PersistenceContext
    private EntityManager entityManager;

    public UserServiceImpl(UserRepository userRepository, BookingRepository bookingRepository, ScreeningRepository screeningRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.screeningRepository = screeningRepository;
    }

    @Override
    public UserDto save(UserDto userDto) {
        if(!findByUsername(userDto.getUsername()).equals(Optional.empty()))
            return null;
        User user= UserMapper.from(userDto);
        user.setBookingSet(new HashSet<>());
        userRepository.save(user);
        return UserMapper.from(user);
    }

    @Override
    public Optional<UserDto> save(Long id, UserDto userDto) {
        if(!userDto.getUsername().equals("admin")) {
            Optional<User> byId = userRepository.findById(id);
            byId.ifPresent(dto -> {
                dto.setUsername(userDto.getUsername());
                dto.setPassword(userDto.getPassword());
                Set<Booking> bookingSet = new LinkedHashSet<>();
                userDto.getBookingSetIDs().forEach(booking -> bookingRepository.findById(booking).ifPresent(bookingSet::add));
                dto.setBookingSet(bookingSet);
            });
            userRepository.save(byId.orElseThrow());
            return byId.map(UserMapper::from);
        }
        return Optional.of(userDto);
    }

    @Override
    public List<UserDto> findAll() {

        return UserMapper.from(userRepository.findAll());
    }

    @Override
    public Optional<UserDto> findById(Long id) {
        Optional<User> user=userRepository.findById(id);
        return user.map(UserMapper::from);
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        Optional<User> user=userRepository.findByUsername(username);
        return user.map(UserMapper::from);
    }
    @Override
    public void deleteById(Long id) {
        Optional<User> user=userRepository.findById(id);
        if(user.isPresent() && user.get().getUsername().equals("admin")) {
            return;
        }
        user.ifPresent(byId->bookingRepository.findAllByUser(byId).forEach(booking->
        {
            //bookingService.deleteById(booking.getID());
            var screening=screeningRepository.findById(booking.getScreening().getID());
            screening.ifPresent(screening1 -> {
                screening1.getBookingSet().remove(booking);
                booking.getSelectedSeats().forEach(seat->screening1.getAvailableSeats().add(seat));
                screeningRepository.save(screening1);
            });
                bookingRepository.deleteById(booking.getID());
        }));
        userRepository.deleteById(id);
    }
}
