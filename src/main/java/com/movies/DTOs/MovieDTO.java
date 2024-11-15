package com.movies.DTOs;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "DTO for creating or updating a Movie entity with details and associations.")
public class MovieDTO {

    @Schema(description = "Title of the movie", example = "Inception")
    @NotBlank(message = "Title cannot be empty.")
    @Size(max = 255, message = "Title length must not exceed 255 characters.")
    private String title;

    @Schema(description = "Release year of the movie", example = "2010", minimum = "1888")
    @NotNull(message = "Release year is required.")
    @Min(value = 1888, message = "Release year must be no earlier than 1888.")
    private Integer releaseYear;

    @Schema(description = "Duration of the movie in minutes", example = "148", minimum = "1")
    @Min(value = 1, message = "Duration must be at least 1 minute.")
    private Integer duration;

    @Schema(description = "List of actor IDs associated with the movie", example = "[1, 2, 3]")
    private List<Long> actorIds;

    @Schema(description = "List of genre IDs associated with the movie", example = "[1, 4, 5]")
    private List<Long> genreIds;

    @Schema(description = "List of new actor objects for creating associated actors")
    @Valid
    private List<ActorDTO> actors;

    @Schema(description = "List of new genre objects for creating associated genres")
    @Valid
    private List<GenreDTO> genres;

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public List<Long> getActorIds() {
        return actorIds;
    }

    public void setActorIds(List<Long> actorIds) {
        this.actorIds = actorIds;
    }

    public List<Long> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Long> genreIds) {
        this.genreIds = genreIds;
    }

    public List<ActorDTO> getActors() {
        return actors;
    }

    public List<GenreDTO> getGenres() {
        return genres;
    }
}
