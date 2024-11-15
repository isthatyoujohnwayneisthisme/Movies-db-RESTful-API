package com.movies.utils;

import com.movies.entities.Movie;
import com.movies.exceptions.ResourceNotFoundException;
import com.movies.interfaces.MovieRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AssociationUtils {

    /**
     * Finds and returns a set of movies based on provided movie IDs.
     *
     * @param movieIds List of movie IDs to fetch
     * @param movieRepository MovieRepository to retrieve movies
     * @return Set of movies associated with the provided IDs
     */
    public static Set<Movie> getAssociatedMovies(List<?> movieIds, MovieRepository movieRepository) {
        Set<Movie> movies = new HashSet<>();
        for (Object movieId : movieIds) {
            if (movieId instanceof Number) {
                Long idValue = ((Number) movieId).longValue(); // Ensure it's cast as Long
                Movie movie = movieRepository.findById(idValue)
                        .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id " + idValue));
                movies.add(movie);
            } else {
                throw new IllegalArgumentException("Movie ID should be a number");
            }
        }
        return movies;
    }
}
