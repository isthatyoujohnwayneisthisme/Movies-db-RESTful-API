package com.movies.controller;

import com.movies.DTOs.GenreDTO;
import com.movies.entities.Genre;
import com.movies.services.GenreService;
import com.movies.utils.PaginationUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.web.bind.annotation.RequestBody;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/genres")
@SecurityRequirement(name = "bearerAuth")
public class GenreController {

    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    @Operation(summary = "Get all genres", description = "Retrieve a paginated list of all genres.")
    public ResponseEntity<Page<Genre>> getAllGenres(
            @Parameter(description = "Page number (zero-based index)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtils.createPageRequest(page, size);

        Page<Genre> genres = genreService.getAllGenres(pageable);
        if (genres.isEmpty()) {
            return ResponseEntity.noContent().build();  // Returns 204 No Content if no genres are found
        }
        return ResponseEntity.ok(genres);  // Returns 200 OK with the paginated list of genres
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a genre by ID", description = "Retrieve a genre by its ID.")
    public ResponseEntity<Genre> getGenreById(
            @Parameter(description = "ID of the genre to retrieve") @PathVariable Long id) {
        Genre genre = genreService.getGenreById(id);
        return ResponseEntity.ok(genre);
    }

    @GetMapping("/search")
    @Operation(summary = "Search genres by name", description = "Retrieve genres that match the given name.")
    public ResponseEntity<Page<Genre>> getGenresByName(
            @Parameter(description = "Name to search for") @RequestParam String name,
            @Parameter(description = "Page number (zero-based index)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Genre> genres = genreService.getGenresByName(name, pageable);

        if (genres.isEmpty()) {
            return ResponseEntity.noContent().build();  // 204 No Content if no genres match
        }

        return ResponseEntity.ok(genres);  // 200 OK with paginated genres if found
    }

    @PostMapping
    @Operation(summary = "Create a new genre", description = "Create a new genre with the provided details.")
    public ResponseEntity<Genre> createGenre(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Genre data transfer object") @Valid @RequestBody GenreDTO genreDTO) {
        Genre savedGenre = genreService.createGenre(genreDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedGenre);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a genre", description = "Update an existing genre's details.")
    public ResponseEntity<Genre> updateGenre(
            @Parameter(description = "ID of the genre to update") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated genre data") @Valid @RequestBody GenreDTO genreDTO) {
        Genre updatedGenre = genreService.partialUpdateGenre(id, genreDTO);
        return ResponseEntity.ok(updatedGenre);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a genre", description = "Delete a genre by ID, with an option to force delete.")
    public ResponseEntity<String> deleteGenre(
            @Parameter(description = "ID of the genre to delete") @PathVariable Long id,
            @Parameter(description = "Set to true to force delete") @RequestParam(defaultValue = "false") boolean forceDelete) {
        String message = genreService.deleteGenre(id, forceDelete);
        if (message.startsWith("Unable to delete")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
        return ResponseEntity.noContent().build();  // Return 204 No Content if deletion is successful
    }
}
