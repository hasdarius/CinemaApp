package com.cinemaapp.demo.dao;

import com.cinemaapp.demo.entity.Movie;
import com.cinemaapp.demo.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening,Long> {
    List<Screening> findAllByTimeAfter(Date time);
    List<Screening> findAllByMovie(Movie movie);
    List<Screening> findAllByTime(Date date);

}
