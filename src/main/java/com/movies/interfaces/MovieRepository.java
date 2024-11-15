package com.movies.interfaces;

import com.movies.entities.Actor;
import com.movies.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Movie> findByReleaseYear(int releaseYear, Pageable pageable);
    Page<Movie> findByActorsId(Long actorId, Pageable pageable);
    Optional<Movie> findByTitleAndReleaseYearAndDuration(String title, int releaseYear, int duration);
    Page<Movie> findByGenresId(Long genreId, Pageable pageable);
}
