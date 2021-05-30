package com.cinemaapp.demo.controller;

import com.cinemaapp.demo.Service.BookingService;
import com.cinemaapp.demo.Service.MovieService;
import com.cinemaapp.demo.Service.ScreeningService;
import com.cinemaapp.demo.Service.UserService;
import com.cinemaapp.demo.dto.BookingDto;
import com.cinemaapp.demo.dto.MovieDto;
import com.cinemaapp.demo.dto.UserDto;
import com.cinemaapp.demo.entity.Seat;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private final UserService userService;
    private final ScreeningService screeningService;
    private final MovieService movieService;
    private final BookingService bookingService;

    public UserController(UserService userService, ScreeningService screeningService, MovieService movieService, BookingService bookingService) {
        this.userService = userService;
        this.screeningService = screeningService;
        this.movieService = movieService;
        this.bookingService = bookingService;
    }

    @GetMapping("/signUp")
    public String getUserForm(Model model){
        if(!model.containsAttribute("newUser")) {
            UserDto user = new UserDto();
            model.addAttribute("newUser", user);
        }
        return "signUp";
    }

    @PostMapping("/signUp/submit")
    public String addUser(@ModelAttribute UserDto user, Model model, RedirectAttributes redirectAttributes){
        if(!userService.findByUsername(user.getUsername()).equals(Optional.empty())){
            return "redirect:/signUp";
        }
        if(user.equals(userService.save(user))){
            redirectAttributes.addFlashAttribute("success", "User successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
        }

        return "redirect:/login";
    }

    @GetMapping({"/home"})
    public String renderHomeStartPage(Model model, HttpServletRequest request){
        model.addAttribute("movies", movieService.findAll());
        return "home";
    }

    @GetMapping({"/screenings"})
    public String renderHomeScreeningPage(Model model, HttpServletRequest request){
        String pattern = "dd/MM/yyyy h:mm a";
        DateFormat df = new SimpleDateFormat(pattern);
        model.addAttribute("dateFormat",df);
        model.addAttribute("screenings", screeningService.findAll());
        return "screenings";
    }

    @GetMapping({"/user/{username}"})
    public String renderUserHomePage(Model model, HttpServletRequest request, @PathVariable String username){
        Optional<UserDto> userDto=userService.findByUsername(username);
        userDto.ifPresent(user-> model.addAttribute("user",user));
        model.addAttribute("movies", movieService.findAll());
        return "user";
    }

    @GetMapping({"/user/screenings/{username}"})
    public String renderUserScreeningPage(Model model, HttpServletRequest request, @PathVariable String username){
        String pattern = "dd/MM/yyyy h:mm a";
        DateFormat df = new SimpleDateFormat(pattern);
        model.addAttribute("dateFormat",df);
        Optional<UserDto> userDto=userService.findByUsername(username);
        userDto.ifPresent(user-> model.addAttribute("user",user));
        model.addAttribute("screenings", screeningService.findAll());
        return "userScreenings";
    }

    @GetMapping({"/user/history/{username}"})
    public String renderUserHistoryPage(Model model, HttpServletRequest request, @PathVariable String username){
        Optional<UserDto> userDto=userService.findByUsername(username);
        userDto.ifPresent(user->{
            String pattern = "dd/MM/yyyy h:mm a";
            DateFormat df = new SimpleDateFormat(pattern);
            model.addAttribute("dateFormat",df);
            model.addAttribute("user",user);
            model.addAttribute("bookings", bookingService.findAllByUserID(user.getID()));
        });
        return "userHistory";
    }

    @GetMapping("/user/{username}/{id}/addBooking")
    public String getBooking(Model model, @PathVariable String id, @PathVariable String username){

        if(!model.containsAttribute("newBooking")) {
            String pattern = "dd/MM/yyyy h:mm a";
            DateFormat df = new SimpleDateFormat(pattern);
            model.addAttribute("dateFormat",df);
            BookingDto bookingDto = new BookingDto();
            userService.findByUsername(username)
                    .ifPresent(user -> {
                        bookingDto.setUserID(user.getID());
                        model.addAttribute("user", user);
                    });
            screeningService.findById(Long.valueOf(id))
                    .ifPresent(screening-> {
                        bookingDto.setScreeningID(screening.getID());
                        model.addAttribute("screening", screening);
                    });
            screeningService.findById(Long.valueOf(id))
                    .flatMap(screening -> movieService.findById(screening.getMovieID()))
                    .ifPresent(movie -> model.addAttribute("movie", movie.getName()));

            List<Seat> seatsList=new ArrayList<>(screeningService.
                    getSeatAvailabilityMapOfScreeningByID(Long.valueOf(id)).keySet());

            Collections.sort(seatsList);
            model.addAttribute("seatList",seatsList);
            Map<Seat,Boolean> map=screeningService.getSeatAvailabilityMapOfScreeningByID(Long.valueOf(id));
            model.addAttribute("seatMap",map);
            model.addAttribute("newBooking", bookingDto);
        }
        return "addBooking";
    }

    @PostMapping("/user/{username}/{id}/addBooking/submit")
    public String addBooking(@ModelAttribute BookingDto bookingDto, Model model, RedirectAttributes redirectAttributes, @PathVariable String id, @PathVariable String username){
        BookingDto newBooking=bookingService.save(bookingDto);
        if(Objects.isNull(newBooking)){
            return "redirect:/user/{username}/{id}/confirmBooking/-1";
        }
        else if(newBooking.getSelectedSeatsIds().equals(bookingDto.getSelectedSeatsIds())){
            return "redirect:/user/{username}/{id}/confirmBooking/"+newBooking.getID();
        }
        else return "redirect:/user/{username}/{id}/confirmBooking/-1";
    }

    @GetMapping("/user/{username}/{screeningID}/confirmBooking/{bookingID}")
    public String renderNewBookingPage(Model model, HttpServletRequest request, @PathVariable String username, @PathVariable String screeningID, @PathVariable String bookingID) {

        if (!bookingID.equals("-1")) {
            bookingService.findById(Long.valueOf(bookingID)).ifPresent(booking-> model.addAttribute("booking",booking));
        String pattern = "dd/MM/yyyy h:mm a";
        DateFormat df = new SimpleDateFormat(pattern);
        model.addAttribute("dateFormat", df);
        screeningService.findById(Long.valueOf(screeningID))
                .ifPresent(screening -> {
                    model.addAttribute("screening", screening);
                });
        screeningService.findById(Long.valueOf(screeningID))
                .flatMap(screening -> movieService.findById(screening.getMovieID()))
                .ifPresent(movie -> model.addAttribute("movie", movie.getName()));

    }
        model.addAttribute("user",username);
        model.addAttribute("bookingID",bookingID);
        return "confirmBooking";
    }

    @GetMapping("/user/{id}/{username}/delete")
    public String deleteBooking(Model model, @PathVariable String id, RedirectAttributes redirectAttributes, @PathVariable String username){
        Optional<BookingDto> bookingDto = bookingService.findById(Long.parseLong(id));
        bookingDto.ifPresent(booking -> bookingService.deleteById(booking.getID()));
        return "redirect:/user/"+username;
    }

    @MessageMapping("/newMovie")
    @SendTo("/topic/movies")
    /**
     * function used for websocket communication in order to notify pages that a new movie is added
     */
    public MovieDto greeting(MovieDto movieDto) throws Exception {
        Thread.sleep(1000); // simulated delay
        return movieDto;
    }

    @MessageMapping("/newScreening")
    @SendTo("/topic/screenings")
    /**
     * function used for websocket communication in order to notify pages that a new screening is added
     */
    public MovieDto greeting2(MovieDto movieDto) throws Exception {
        Thread.sleep(1000); // simulated delay
        return movieDto;
    }



}
