package com.movies.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Set;

@Schema(description = "DTO for creating or updating a Genre entity with optional movie associations.")
public class GenreDTO {
    @Schema(description = "Name of the genre", example = "Science Fiction")
    @NotBlank(message = "Genre name cannot be blank.")
    @Size(max = 100, message = "Genre name length must not exceed 100 characters.")
    private String name;
    @Schema(description = "List of movie IDs associated with this genre", example = "[1, 2, 3]")
    private List<Long> movieIds;  // Optional for associating with movies

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Long> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<Long> movieIds) {
        this.movieIds = movieIds;
    }
}
