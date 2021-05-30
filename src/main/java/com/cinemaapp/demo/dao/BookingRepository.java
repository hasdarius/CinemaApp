package com.cinemaapp.demo.dao;

import com.cinemaapp.demo.entity.Booking;
import com.cinemaapp.demo.entity.Screening;
import com.cinemaapp.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {
    List<Booking> findAllByUser(User user);
    List<Booking> findAllByScreening(Screening screening);
}
