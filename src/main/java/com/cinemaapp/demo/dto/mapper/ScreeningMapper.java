package com.cinemaapp.demo.dto.mapper;

import com.cinemaapp.demo.dto.ScreeningDto;
import com.cinemaapp.demo.entity.Screening;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class ScreeningMapper {
    public static List<ScreeningDto> from(final List<Screening> screenings) {
        if (CollectionUtils.isEmpty(screenings))
            return emptyList();

        return screenings
                .stream()
                .map(ScreeningMapper::from)
                .collect(Collectors.toList());
    }


    public static Screening from(final ScreeningDto screeningDto) {
        if (screeningDto == null)
            return null;

        final var screening = new Screening();
        screening.setID(screeningDto.getID());
        screening.setTime(screeningDto.getTime());

        return screening;
    }

    public static ScreeningDto from(final Screening screening) {
        if (screening == null)
            return null;

        final var screeningDto = new ScreeningDto();
        screeningDto.setID(screening.getID());
        screeningDto.setTime(screening.getTime());
        screeningDto.setMovieID(screening.getMovie().getID());
        Set<Long> availableSeats = new LinkedHashSet<>();
        Set<Long> bookings = new LinkedHashSet<>();
        screening.getAvailableSeats().forEach(seat -> availableSeats.add(seat.getID()));
        screening.getBookingSet().forEach(booking -> bookings.add(booking.getID()));
        screeningDto.setAvailableSeatsIDs(availableSeats);
        screeningDto.setBookingSetIDs(bookings);
        return screeningDto;
    }
}
