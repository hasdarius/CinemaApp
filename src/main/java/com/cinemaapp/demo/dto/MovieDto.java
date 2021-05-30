package com.cinemaapp.demo.dto;

import com.cinemaapp.demo.entity.Screening;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class MovieDto {
    private Long ID;
    private String name;
    private String photo;
    private String genre;
    private String description;
    private Set<Long> screeningSetIDs;

    public MovieDto(){
        screeningSetIDs=new HashSet<>();
    }

    @Transient
    public String getPhotosImagePath() {
        if (photo == null || ID == null) return null;

        return "/cinemaApp/" + name + "/" + photo;
    }
}
