package com.movies.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "DTO for creating or updating an Actor entity with optional movie associations.")
public class ActorDTO {

    @Schema(description = "Name of the actor", example = "Leonardo DiCaprio")
    @NotBlank(message = "Actor's name cannot be blank.")
    @Size(max = 100, message = "Actor name length must not exceed 100 characters.")
    private String name;

    @Schema(description = "Birth date of the actor in 'yyyy-MM-dd' format", example = "1974-11-11")
    @NotNull(message = "Birth date cannot be null.")
    @PastOrPresent(message = "Birth date must be in the past or present.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    @Schema(description = "List of movie IDs associated with this actor", example = "[1, 2, 3]")
    private List<Long> movieIds;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public List<Long> getMovieIds() {
        return movieIds;
    }

    public void setMovieIds(List<Long> movieIds) {
        this.movieIds = movieIds;
    }
}
