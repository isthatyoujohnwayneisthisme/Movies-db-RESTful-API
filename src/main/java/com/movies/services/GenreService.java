package com.movies.services;

import com.movies.DTOs.GenreDTO;
import com.movies.entities.Genre;
import com.movies.entities.Movie;
import com.movies.exceptions.DuplicateEntityException;
import com.movies.exceptions.ResourceNotFoundException;
import com.movies.interfaces.GenreRepository;
import com.movies.interfaces.MovieRepository;
import com.movies.utils.AssociationUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreRepository genreRepository;
    private final MovieRepository movieRepository;

    public GenreService(GenreRepository genreRepository, MovieRepository movieRepository) {
        this.genreRepository = genreRepository;
        this.movieRepository = movieRepository;
    }

    public Page<Genre> getAllGenres(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Genre not found with id " + id));
    }

    public Page<Genre> getGenresByName(String name, Pageable pageable) {
        return genreRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Genre createGenre(GenreDTO genreDTO) {
        Optional<Genre> existingGenre = genreRepository.findByName(genreDTO.getName());
        if (existingGenre.isPresent()) {
            throw new DuplicateEntityException("Genre with name '" + genreDTO.getName() +
                    "' already exists with id " + existingGenre.get().getId());
        }

        Genre newGenre = new Genre();
        newGenre.setName(genreDTO.getName());

        // Associate existing movies if movieIds are provided
        if (genreDTO.getMovieIds() != null && !genreDTO.getMovieIds().isEmpty()) {
            Set<Movie> movies = AssociationUtils.getAssociatedMovies(genreDTO.getMovieIds(), movieRepository);

            // Set the movies on the new genre
            newGenre.setMovies(movies);

            // Synchronize by adding the new genre to each movie's genres set
            movies.forEach(movie -> movie.getGenres().add(newGenre));
        }

        return genreRepository.save(newGenre);
    }


    public Genre partialUpdateGenre(Long id, GenreDTO genreDTO) {
        Genre genre = getGenreById(id); // Retrieve existing genre for updates

        // Update only the provided fields
        if (genreDTO.getName() != null) {
            genre.setName(genreDTO.getName());
        }

        if (genreDTO.getMovieIds() != null && !genreDTO.getMovieIds().isEmpty()) {
            // Fetch movies based on provided IDs
            Set<Movie> movies = AssociationUtils.getAssociatedMovies(genreDTO.getMovieIds(), movieRepository);

            // Synchronize associations: clear existing and add new
            genre.getMovies().forEach(movie -> movie.getGenres().remove(genre));
            movies.forEach(movie -> movie.getGenres().add(genre));
            genre.setMovies(movies);
        }

        // Save the updated genre
        return genreRepository.save(genre);
    }


    public String deleteGenre(Long id, boolean forceDelete) {
        Genre genre = getGenreById(id);
        // Check if the genre is associated with any movies
        if (!genre.getMovies().isEmpty()) {
            if (!forceDelete) {
                return "Unable to delete genre '" + genre.getName() + "' as it is associated with " + genre.getMovies().size() + " movies.";
            } else {
                // Remove associations with all movies
                genre.getMovies().forEach(movie -> movie.getGenres().remove(genre));
                genre.getMovies().clear();
                genreRepository.save(genre); // Save updated genre with cleared associations
            }
        }

        // Proceed to delete the genre
        genreRepository.deleteById(id);
        return "Genre '" + genre.getName() + "' deleted successfully.";
    }

}