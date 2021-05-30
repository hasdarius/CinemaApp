package com.cinemaapp.demo.controller;

import com.cinemaapp.demo.ReportGenerator.PdfReportGenerator;
import com.cinemaapp.demo.Service.BookingService;
import com.cinemaapp.demo.Service.MovieService;
import com.cinemaapp.demo.Service.ScreeningService;
import com.cinemaapp.demo.Service.UserService;
import com.cinemaapp.demo.dto.BookingDto;
import com.cinemaapp.demo.dto.MovieDto;
import com.cinemaapp.demo.dto.ScreeningDto;
import com.cinemaapp.demo.dto.UserDto;
import com.cinemaapp.demo.upload.FileUploadUtil;
import com.itextpdf.text.DocumentException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@Controller
public class AdminController {

    private final UserService userService;
    private final ScreeningService screeningService;
    private final MovieService movieService;
    private final BookingService bookingService;

    public AdminController(UserService userService, ScreeningService screeningService, MovieService movieService, BookingService bookingService) {
        this.userService = userService;
        this.screeningService = screeningService;
        this.movieService = movieService;
        this.bookingService = bookingService;
    }

    @GetMapping({"/admin"})
    public String renderAdminStartPage(Model model, HttpServletRequest request){
        model.addAttribute("users", userService.findAll());
        return "admin";
    }

    @GetMapping({"/admin/users"})
    public String renderAdminUserPage(Model model, HttpServletRequest request){
        model.addAttribute("users", userService.findAll());
        return "adminSeeUsers";
    }

    @GetMapping({"/admin/movies"})
    public String renderAdminMoviePage(Model model, HttpServletRequest request){
        model.addAttribute("movies", movieService.findAll());
        return "adminSeeMovies";
    }

    @GetMapping({"/admin/screenings"})
    public String renderAdminScreeningPage(Model model, HttpServletRequest request){
        model.addAttribute("screenings", screeningService.findAll());
        return "adminSeeScreenings";
    }

    @GetMapping("/admin/deleteUser/{id}")
    public String deleteUser(Model model, @PathVariable String id, RedirectAttributes redirectAttributes){
        Optional<UserDto> userDto = userService.findById(Long.parseLong(id));
        userDto.ifPresent(userDto1 -> userService.deleteById(userDto1.getID()));
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/deleteMovie/{id}")
    public String deleteMovie(Model model, @PathVariable String id, RedirectAttributes redirectAttributes){
        Optional<MovieDto> movieDto = movieService.findById(Long.parseLong(id));
        movieDto.ifPresent(movieDto1 -> movieService.deleteById(movieDto1.getID()));
        return "redirect:/admin/movies";
    }

    @GetMapping("/admin/deleteScreening/{id}")
    public String deleteScreening(Model model, @PathVariable String id, RedirectAttributes redirectAttributes){
        Optional<ScreeningDto> screeningDto = screeningService.findById(Long.parseLong(id));
        screeningDto.ifPresent(screeningDto1 -> screeningService.deleteById(screeningDto1.getID()));
        return "redirect:/admin/screenings";
    }

    @GetMapping("/admin/addUser")
    public String getUserForm(Model model){
        if(!model.containsAttribute("user")) {
            UserDto user = new UserDto();
            model.addAttribute("user", user);
        }
        return "adduser";
    }

    @PostMapping("/admin/addUser/submit")
    public String addUser(@ModelAttribute UserDto user, Model model, RedirectAttributes redirectAttributes){
        if(user.equals(userService.save(user))){
            redirectAttributes.addFlashAttribute("success", "User successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
        }

        return "redirect:/admin/users";
    }

    @GetMapping("/admin/updateUser/{id}")
    public String findUser(Model model, @PathVariable String id,RedirectAttributes redirectAttributes) {
        Optional<UserDto> userDto = userService.findById(Long.parseLong(id));
        userDto.ifPresent(user->{
            model.addAttribute("user", user);
        });
        if(model.containsAttribute("user"))
            return "updateUser";
        return "redirect:/admin/users";
    }
    @PostMapping("/admin/updateUser/submit/{id}")
    public String addUser(@ModelAttribute UserDto userDto, Model model,@PathVariable String id, RedirectAttributes redirectAttributes){
        userService.save(Long.parseLong(id),userDto);
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/addMovie")
    public String getMovieForm(Model model){
        if(!model.containsAttribute("movie")) {
            MovieDto movieDto = new MovieDto();
            model.addAttribute("movie", movieDto);
        }
        return "addMovie";
    }

    @PostMapping("/admin/addMovie/submit")
    public String addMovie(@ModelAttribute MovieDto movieDto, @RequestParam("image") MultipartFile multipartFile, Model model, RedirectAttributes redirectAttributes) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        if(fileName.equals(""))
            return "redirect:/admin/addMovie";
        movieDto.setPhoto(fileName);
        String uploadDir = "cinemaApp/" + movieDto.getName();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        if(movieDto.equals(movieService.save(movieDto))){
            redirectAttributes.addFlashAttribute("success", "User successfully added!");
        }else{
            redirectAttributes.addFlashAttribute("success", "Error!");
        }

        return "redirect:/admin/movies";
    }

    @GetMapping("/admin/updateMovie/{id}")
    public String findMovie(Model model, @PathVariable String id,RedirectAttributes redirectAttributes) {
        Optional<MovieDto> movieDto = movieService.findById(Long.parseLong(id));
        movieDto.ifPresent(movie->{
            model.addAttribute("movie", movie);
        });
        if(model.containsAttribute("movie"))
            return "updateMovie";
        return "redirect:/admin/movies";
    }
    @PostMapping("/admin/updateMovie/submit/{id}")
    public String addMovie(@ModelAttribute MovieDto movieDto,  @RequestParam("image") MultipartFile multipartFile, Model model,@PathVariable String id, RedirectAttributes redirectAttributes) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        movieDto.setPhoto(fileName);
        String uploadDir = "cinemaApp/" + movieDto.getName();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        movieService.save(Long.parseLong(id),movieDto);
        return "redirect:/admin/movies";
    }

    @GetMapping("/admin/addScreening")
    public String getScreeningForm(Model model){
        if(!model.containsAttribute("screening")) {
            ScreeningDto screeningDto = new ScreeningDto();
            model.addAttribute("screening", screeningDto);
            model.addAttribute("hoursOfAllExistingScreenings",screeningService.findDatesOfAllScreenings());
        }
        return "addScreening";
    }

    @PostMapping("/admin/addScreening/submit")
    public String addScreening(@ModelAttribute ScreeningDto screeningDto, Model model, RedirectAttributes redirectAttributes) throws IOException {
        if(screeningDto.getMovieID()!=null) {
            if (screeningDto.equals(screeningService.save(screeningDto))) {
                redirectAttributes.addFlashAttribute("success", "User successfully added!");
            } else {
                redirectAttributes.addFlashAttribute("success", "Error!");
            }
        }
        return "redirect:/admin/screenings";
    }

    @GetMapping("/admin/generateUserReport")
    public String generateUserReport( Model model, RedirectAttributes redirectAttributes) throws IOException, DocumentException, org.dom4j.DocumentException {
        List<UserDto> userDtos=userService.findAll();
        List<BookingDto> bookingDtos=new LinkedList<>();
        userDtos.forEach(userDto -> bookingService.findById(userDto.getID()).ifPresent(bookingDtos::add));
        PdfReportGenerator.generateUserReport(userDtos, bookingDtos);
        return "redirect:/admin";
    }

    @GetMapping("/admin/generateScreeningsReport")
    public String generateScreeningsReport( Model model, RedirectAttributes redirectAttributes) throws IOException, DocumentException, org.dom4j.DocumentException {
        Map<String,List<ScreeningDto>> genreScreeningMap = new HashMap<>();
        List<ScreeningDto> screeningDtos=screeningService.findAll();
        List<MovieDto> movieDtos=movieService.findAll();
        for(var movieDto:movieDtos){
            var genre = movieDto.getGenre();
            genreScreeningMap.putIfAbsent(genre,new LinkedList<>());
            for(var screeningDto:screeningDtos){
                if(screeningDto.getMovieID().equals(movieDto.getID())){
                    genreScreeningMap.get(genre).add(screeningDto);
                }
            }
        }
        PdfReportGenerator.generateScreeningsReport(genreScreeningMap);

        return "redirect:/admin";
    }


}


