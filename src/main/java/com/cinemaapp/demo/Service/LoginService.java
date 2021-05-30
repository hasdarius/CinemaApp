package com.cinemaapp.demo.Service;

import com.cinemaapp.demo.entity.User;
import com.cinemaapp.demo.security.AdminLogin;
import com.cinemaapp.demo.security.CustomerLogin;
import com.cinemaapp.demo.dao.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService implements UserDetailsService {

    private final UserRepository userRepository;

    public LoginService(UserRepository adminRepository) {
        this.userRepository = adminRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserDetails userDetails = null;

        Optional<User> user =  userRepository.findByUsername(s);
        if(user.isPresent()) {
            if (user.get().getUsername().equals("admin")) {
                userDetails = new AdminLogin(user.get());
            } else {
                userDetails = new CustomerLogin(user.get());
            }
        }


        return userDetails;
    }
}

