package com.cinemaapp.demo.dto.mapper;

import com.cinemaapp.demo.dto.UserDto;
import com.cinemaapp.demo.entity.User;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class UserMapper {
    public static List<UserDto> from(final List<User> users) {
        if (CollectionUtils.isEmpty(users))
            return emptyList();

        return users
                .stream()
                .map(UserMapper::from)
                .collect(Collectors.toList());
    }


    public static User from(final UserDto userDto) {
        if (userDto == null)
            return null;

        final var user = new User();
        user.setID(userDto.getID());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());

        return user;
    }

    public static UserDto from(final User user) {
        if (user == null)
            return null;

        final var userDto = new UserDto();
        userDto.setID(user.getID());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        Set<Long> bookings = new LinkedHashSet<>();
        user.getBookingSet().forEach(booking -> bookings.add(booking.getID()));
        userDto.setBookingSetIDs(bookings);
        return userDto;
    }
}
