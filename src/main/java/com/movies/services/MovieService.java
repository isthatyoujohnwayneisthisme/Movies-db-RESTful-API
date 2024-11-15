package com.movies.services;


import com.movies.DTOs.ActorDTO;
import com.movies.DTOs.GenreDTO;
import com.movies.DTOs.MovieDTO;
import com.movies.entities.Actor;
import com.movies.entities.Genre;
import com.movies.entities.Movie;
import com.movies.exceptions.AssociationAlreadyExistsException;
import com.movies.exceptions.AssociationNotFoundException;
import com.movies.exceptions.DuplicateEntityException;
import com.movies.exceptions.ResourceNotFoundException;
import com.movies.interfaces.ActorRepository;
import com.movies.interfaces.GenreRepository;
import com.movies.interfaces.MovieRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class MovieService {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final GenreRepository genreRepository;
    private final ActorService actorService;
    private final GenreService genreService;


    @Autowired
    public MovieService(MovieRepository movieRepository,
                        ActorRepository actorRepository,
                        GenreRepository genreRepository,
                        ActorService actorService,
                        GenreService genreService) {
        this.movieRepository = movieRepository;
        this.actorRepository = actorRepository;
        this.genreRepository = genreRepository;
        this.actorService = actorService;
        this.genreService = genreService;
    }

    public Page<Movie> getAllMovies(Pageable pageable) {
        return movieRepository.findAll(pageable);
    }

    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + id));
    }

    public Page<Movie> getMoviesByTitle(String title, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable);
    }

    public Page<Movie> getMoviesByYear(int releaseYear, Pageable pageable) {
        return movieRepository.findByReleaseYear(releaseYear, pageable);
    }

    public Movie createMovieWithAssociations(MovieDTO dto) {
        // Check if a similar movie already exists
        Optional<Movie> existingMovie = movieRepository.findByTitleAndReleaseYearAndDuration(
                dto.getTitle(), dto.getReleaseYear(), dto.getDuration()
        );
        if (existingMovie.isPresent()) {
            throw new DuplicateEntityException(
                    "Movie '" + dto.getTitle() + "' from year " + dto.getReleaseYear() +
                            " with duration " + dto.getDuration() + " already exists with id " + existingMovie.get().getId()
            );
        }

        Movie movie = new Movie();
        movie.setTitle(dto.getTitle());
        movie.setReleaseYear(dto.getReleaseYear());
        movie.setDuration(dto.getDuration());

        Set<Actor> actors = new HashSet<>();

        // Add existing actors by IDs
        if (dto.getActorIds() != null) {
            for (Long actorId : dto.getActorIds()) {
                actors.add(actorService.getActorById(actorId));
            }
        }

        // Add new actors specified by ActorDTO
        if (dto.getActors() != null) {
            for (ActorDTO actorDTO : dto.getActors()) {
                try {
                    actors.add(actorService.createActor(actorDTO));
                } catch (DuplicateEntityException e) {
                    Long existingActorId = extractEntityIdFromMessage(e.getMessage());
                    actors.add(actorService.getActorById(existingActorId));
                }
            }
        }
        movie.setActors(actors);

        Set<Genre> genres = new HashSet<>();

        // Add existing genres by IDs
        if (dto.getGenreIds() != null) {
            for (Long genreId : dto.getGenreIds()) {
                genres.add(genreService.getGenreById(genreId));
            }
        }

        // Add new genres specified by GenreDTO
        if (dto.getGenres() != null) {
            for (GenreDTO genreDTO : dto.getGenres()) {
                try {
                    genres.add(genreService.createGenre(genreDTO));
                } catch (DuplicateEntityException e) {
                    Long existingGenreId = extractEntityIdFromMessage(e.getMessage());
                    genres.add(genreService.getGenreById(existingGenreId));
                }
            }
        }
        movie.setGenres(genres);

        return movieRepository.save(movie);
    }

    private Long extractEntityIdFromMessage(String message) { // helper method to extract ids for create and update methods - boy im tired and this is so unnecessary lolol
        Pattern pattern = Pattern.compile("with id (\\d+)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return Long.parseLong(matcher.group(1));
        }
        throw new IllegalArgumentException("Entity id not found in message: " + message);
    }

    public String deleteMovie(Long id, boolean forceDelete) {
        Movie movie = getMovieById(id);
        // Check if the movie has associated actors
        if (!movie.getActors().isEmpty()) {
            if (!forceDelete) {
                // Return a message instead of throwing an exception
                return "Unable to delete movie '" + movie.getTitle() + "' as it is associated with " + movie.getActors().size() + " actors.";
            } else {
                // Remove associations with all actors
                for (Actor actor : movie.getActors()) {
                    actor.getMovies().remove(movie);
                    actorRepository.save(actor); // Save updated actor without the movie association
                }
                movie.getActors().clear(); // Clear the actors set in the movie before deletion
                movieRepository.save(movie); // Save updated movie with cleared associations
            }
        }

        // Proceed to delete the movie
        movieRepository.deleteById(id);
        return "Movie '" + movie.getTitle() + "' has been deleted successfully.";
    }

    public Movie updateMovieWithAssociations(Long movieId, MovieDTO dto) {
        Movie movie = getMovieById(movieId);  // Retrieve existing movie, throws 404 if not found

        // Update title, year, and duration if provided
        if (dto.getTitle() != null) {
            movie.setTitle(dto.getTitle());
        }
        if (dto.getReleaseYear() != null) {
            movie.setReleaseYear(dto.getReleaseYear());
        }
        if (dto.getDuration() != null) {
            movie.setDuration(dto.getDuration());
        }

        // Update actors if actorIds are provided
        if (dto.getActorIds() != null) {
            Set<Actor> actors = dto.getActorIds().stream()
                    .map(actorService::getActorById)  // Retrieve each actor, handling 404 if not found
                    .collect(Collectors.toSet());
            movie.setActors(actors);  // Replace existing actors with the new set
        }

        // Update genres if genreIds are provided
        if (dto.getGenreIds() != null) {
            Set<Genre> genres = dto.getGenreIds().stream()
                    .map(genreService::getGenreById)  // Retrieve each genre, handling 404 if not found
                    .collect(Collectors.toSet());
            movie.setGenres(genres);  // Replace existing genres with the new set
        }

        return movieRepository.save(movie);  // Save and return the updated movie
    }

    public Page<Movie> getMoviesByActorId(Long actorId, Pageable pageable) {
        // Ensure the actor exists by calling getActorById; throws ResourceNotFoundException if not found
        actorService.getActorById(actorId);

        // Return a paginated list of movies for the actor
        return movieRepository.findByActorsId(actorId, pageable);
    }

    public Movie addActorToMovie(Long movieId, Long actorId) {
        Movie movie = getMovieById(movieId);  // Fetch movie and handle potential 404 with ResourceNotFoundException
        Actor actor = actorService.getActorById(actorId);  // Fetch actor and handle potential 404

        // Check if the association already exists to prevent duplicates
        if (movie.getActors().contains(actor)) {
            throw new AssociationAlreadyExistsException("Actor is already associated with this movie.");
        }

        // Add the actor to the movie and vice versa for bidirectional consistency
        movie.getActors().add(actor);
        actor.getMovies().add(movie);

        // Save both entities to ensure the bidirectional relationship is updated
        actorRepository.save(actor);  // Save the actor with the new movie association
        return movieRepository.save(movie);  // Save the movie with the updated actor association
    }

    public void removeActorFromMovie(Long movieId, Long actorId) {
        // Use helper methods to retrieve the entities, throwing ResourceNotFoundException if not found
        Movie movie = getMovieById(movieId);
        Actor actor = actorService.getActorById(actorId);

        // Check if the actor is actually associated with the movie before attempting to remove
        if (!movie.getActors().contains(actor)) {
            throw new AssociationNotFoundException("Actor with ID " + actorId + " is not associated with Movie ID " + movieId);
        }

        // Remove the association on both sides
        movie.getActors().remove(actor);
        actor.getMovies().remove(movie);

        // Save both entities to persist the change
        movieRepository.save(movie);
        actorRepository.save(actor);
    }

    public Set<Actor> getActorsInMovie(Long movieId) {
        Movie movie = getMovieById(movieId);  // Use getMovieById to benefit from ResourceNotFoundException
        return movie.getActors();
    }

    public Set<Genre> getGenresInMovie(Long movieId) {
        Movie movie = getMovieById(movieId);  // Use getMovieById to leverage ResourceNotFoundException for 404 handling
        return movie.getGenres();
    }

    public Page<Movie> getMoviesByGenre(Long genreId, Pageable pageable) {
        // Ensure the genre exists by calling getGenreById; throws ResourceNotFoundException if not found
        genreService.getGenreById(genreId);

        // Fetch movies associated with the genre in a paginated format
        return movieRepository.findByGenresId(genreId, pageable);
    }

    public Movie addGenreToMovie(Long movieId, Long genreId) {
        Movie movie = getMovieById(movieId); // Throws ResourceNotFoundException if not found
        Genre genre = genreService.getGenreById(genreId); // Also throws ResourceNotFoundException if not found

        // Check if the genre is already associated with the movie
        if (movie.getGenres().contains(genre)) {
            throw new AssociationAlreadyExistsException("Genre with id " + genreId + " is already associated with movie id " + movieId);
        }

        movie.getGenres().add(genre);
        genre.getMovies().add(movie);

        genreRepository.save(genre); // Ensure the genre side is updated
        return movieRepository.save(movie); // Save the movie with the updated list of genres
    }

    public void removeGenreFromMovie(Long movieId, Long genreId) {
        Movie movie = getMovieById(movieId);  // Reuse method for 404 handling
        Genre genre = genreService.getGenreById(genreId);  // Reuse method for 404 handling

        // Check if the association exists before attempting removal
        if (!movie.getGenres().contains(genre)) {
            throw new AssociationNotFoundException("Genre with id " + genreId + " is not associated with Movie id " + movieId);
        }

        // Remove the association on both sides
        movie.getGenres().remove(genre);
        genre.getMovies().remove(movie);

        // Save both entities to persist the change
        movieRepository.save(movie);
        genreRepository.save(genre);
    }
}
