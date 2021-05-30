package com.cinemaapp.demo.dao;

import com.cinemaapp.demo.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat,Long> {

}
