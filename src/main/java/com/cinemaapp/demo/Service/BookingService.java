package com.cinemaapp.demo.Service;

import com.cinemaapp.demo.dto.BookingDto;

import java.util.List;
import java.util.Optional;

public interface BookingService {
    BookingDto save(BookingDto bookingDto);

    Optional<BookingDto> save(Long id, BookingDto bookingDto);

    List<BookingDto> findAll();

    Optional<BookingDto> findById(Long id);

    List<BookingDto> findAllByUserID(Long userID);

    List<BookingDto> findAllByScreeningID(Long screeningID);


    void deleteById(Long id);
}
