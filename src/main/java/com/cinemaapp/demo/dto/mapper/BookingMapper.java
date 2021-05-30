package com.cinemaapp.demo.dto.mapper;

import com.cinemaapp.demo.dto.BookingDto;
import com.cinemaapp.demo.entity.Booking;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class BookingMapper {
    public static List<BookingDto> from(final List<Booking> bookings) {
        if (CollectionUtils.isEmpty(bookings))
            return emptyList();

        return bookings
                .stream()
                .map(BookingMapper::from)
                .collect(Collectors.toList());
    }


    public static Booking from(final BookingDto bookingDto) {
        if (bookingDto == null)
            return null;

        final var booking = new Booking();
        booking.setID(bookingDto.getID());

        return booking;
    }

    public static BookingDto from(final Booking booking) {
        if (booking == null)
            return null;

        final var bookingDto = new BookingDto();
        bookingDto.setID(booking.getID());
        bookingDto.setUserID(booking.getUser().getID());
        bookingDto.setScreeningID(booking.getScreening().getID());
        Set<Long> selectedSeats = new LinkedHashSet<>();
        booking.getSelectedSeats().forEach(seat -> selectedSeats.add(seat.getID()));
        bookingDto.setSelectedSeatsIds(selectedSeats);
        return bookingDto;
    }
}
