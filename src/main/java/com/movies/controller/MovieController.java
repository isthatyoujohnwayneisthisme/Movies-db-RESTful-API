package com.movies.controller;

import com.movies.DTOs.MovieDTO;
import com.movies.DTOs.MovieMinimalDTO;
import com.movies.entities.Actor;
import com.movies.entities.Genre;
import com.movies.entities.Movie;
import com.movies.services.MovieService;
import com.movies.utils.PaginationUtils;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import java.util.Set;

@RestController
@RequestMapping("/api/movies")
@SecurityRequirement(name = "bearerAuth")
public class MovieController {

    private final MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved movies",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Movie.class)))),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    @Operation(summary = "Get a list of movies", description = "Retrieve movies, optionally filtering by genre, year, or actor.")
    public ResponseEntity<Page<MovieMinimalDTO>> getMovies(
            @Parameter(description = "Filter by genre ID") @RequestParam(required = false) Long genre,
            @Parameter(description = "Filter by release year") @RequestParam(required = false) Integer year,
            @Parameter(description = "Filter by actor ID") @RequestParam(required = false) Long actor,
            @Parameter(description = "Page number (zero-based index)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Movie> movies;

        if (actor != null) {
            movies = movieService.getMoviesByActorId(actor, pageable);
        } else if (genre != null) {
            movies = movieService.getMoviesByGenre(genre, pageable);
        } else if (year != null) {
            movies = movieService.getMoviesByYear(year, pageable);
        } else {
            movies = movieService.getAllMovies(pageable);
        }

        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Map Movie entities to MovieMinimalDTOs
        Page<MovieMinimalDTO> minimalMoviesPage = movies.map(MovieMinimalDTO::new);

        return ResponseEntity.ok(minimalMoviesPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a movie by ID", description = "Retrieve a movie by its ID.")
    public ResponseEntity<Movie> getMovieById(
            @Parameter(description = "ID of the movie to retrieve") @PathVariable Long id) {
        Movie movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    @GetMapping("/search")
    @Operation(summary = "Search movies by title", description = "Retrieve movies that match the given title.")
    public ResponseEntity<Page<Movie>> getMoviesByTitle(
            @Parameter(description = "Title to search for") @RequestParam String title,
            @Parameter(description = "Page number (zero-based index)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Movie> movies = movieService.getMoviesByTitle(title, pageable);

        if (movies.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(movies);
    }

    @PostMapping
    @Operation(summary = "Create a new movie", description = "Create a new movie with the provided details.")
    public ResponseEntity<Movie> createMovie(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Movie data transfer object")
            @RequestBody MovieDTO dto) {
        Movie savedMovie = movieService.createMovieWithAssociations(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMovie);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a movie", description = "Delete a movie by ID, with an option to force delete.")
    public ResponseEntity<String> deleteMovie(
            @Parameter(description = "ID of the movie to delete") @PathVariable Long id,
            @Parameter(description = "Set to true to force delete") @RequestParam(defaultValue = "false") boolean forceDelete) {
        String result = movieService.deleteMovie(id, forceDelete);
        if (result.startsWith("Unable to delete")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result); // Return 409 Conflict with a message
        }
        return ResponseEntity.noContent().build(); // Return 204 No Content for successful deletion
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a movie", description = "Update an existing movie's details.")
    public ResponseEntity<Movie> updateMovie(
            @Parameter(description = "ID of the movie to update") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated movie data")
            @RequestBody MovieDTO dto) {
        Movie updatedMovie = movieService.updateMovieWithAssociations(id, dto);
        return ResponseEntity.ok(updatedMovie);
    }

    @GetMapping("/{movieId}/actors")
    @Operation(summary = "Get actors in a movie", description = "Retrieve all actors associated with a given movie.")
    public ResponseEntity<Set<Actor>> getActorsInMovie(
            @Parameter(description = "ID of the movie") @PathVariable Long movieId) {
        Set<Actor> actors = movieService.getActorsInMovie(movieId);

        if (actors.isEmpty()) {
            return ResponseEntity.noContent().build();  // Return 204 No Content if no actors are associated with the movie
        }

        return ResponseEntity.ok(actors);  // Return 200 OK with the set of actors if found
    }

    @PostMapping("/{movieId}/actors/{actorId}")
    @Operation(summary = "Add an actor to a movie", description = "Associate an actor with a movie.")
    public ResponseEntity<Movie> addActorToMovie(
            @Parameter(description = "ID of the movie") @PathVariable Long movieId,
            @Parameter(description = "ID of the actor to add") @PathVariable Long actorId) {
        Movie movie = movieService.addActorToMovie(movieId, actorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie);  // Return 201 Created on successful association
    }

    @GetMapping("/{movieId}/genres")
    @Operation(summary = "Get genres of a movie", description = "Retrieve all genres associated with a given movie.")
    public ResponseEntity<Set<Genre>> getGenresInMovie(
            @Parameter(description = "ID of the movie") @PathVariable Long movieId) {
        Set<Genre> genres = movieService.getGenresInMovie(movieId);

        if (genres.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content if no genres are associated
        }

        return ResponseEntity.ok(genres);  // 200 OK if genres are found
    }

    @PostMapping("/{movieId}/genres/{genreId}")
    @Operation(summary = "Add a genre to a movie", description = "Associate a genre with a movie.")
    public ResponseEntity<Movie> addGenreToMovie(
            @Parameter(description = "ID of the movie") @PathVariable Long movieId,
            @Parameter(description = "ID of the genre to add") @PathVariable Long genreId) {
        Movie movie = movieService.addGenreToMovie(movieId, genreId);
        return ResponseEntity.status(HttpStatus.CREATED).body(movie); // 201 Created for successful association
    }

    @DeleteMapping("/{movieId}/actors/{actorId}")
    @Operation(summary = "Remove an actor from a movie", description = "Disassociate an actor from a movie.")
    public ResponseEntity<String> removeActorFromMovie(
            @Parameter(description = "ID of the movie") @PathVariable Long movieId,
            @Parameter(description = "ID of the actor to remove") @PathVariable Long actorId) {
        movieService.removeActorFromMovie(movieId, actorId);
        return ResponseEntity.ok("Actor removed from movie successfully.");
    }

    @DeleteMapping("/{movieId}/genres/{genreId}")
    @Operation(summary = "Remove a genre from a movie", description = "Disassociate a genre from a movie.")
    public ResponseEntity<String> removeGenreFromMovie(
            @Parameter(description = "ID of the movie") @PathVariable Long movieId,
            @Parameter(description = "ID of the genre to remove") @PathVariable Long genreId) {
        movieService.removeGenreFromMovie(movieId, genreId);
        return ResponseEntity.ok("Genre removed from movie successfully.");
    }

}