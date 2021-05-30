package com.cinemaapp.demo.Service;


import com.cinemaapp.demo.dto.BookingDto;
import com.cinemaapp.demo.dto.UserDto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    UserDto save(UserDto userDto);

    Optional<UserDto> save(Long id, UserDto userDto);

    List<UserDto> findAll();

    Optional<UserDto> findById(Long id);

    Optional<UserDto> findByUsername(String username);


    void deleteById(Long id);
}
