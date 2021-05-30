package com.cinemaapp.demo.Service;

import com.cinemaapp.demo.dto.ScreeningDto;
import com.cinemaapp.demo.entity.Seat;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ScreeningService {
    ScreeningDto save(ScreeningDto screeningDto); //check with timestamp so no other screening is done at the same time (minimum 2 hours in between)

    Optional<ScreeningDto> save(Long id, ScreeningDto screeningDto);

    List<ScreeningDto> findAll();

    Optional<ScreeningDto> findById(Long id);

    List<ScreeningDto> findAllByMovieID(Long userID);

    Map<Seat,Boolean> getSeatAvailabilityMapOfScreeningByID(Long id); //put true if in availableSeat set, false otherwise

    List<Date> findDatesOfAllScreenings();

    void deleteById(Long id);
}
