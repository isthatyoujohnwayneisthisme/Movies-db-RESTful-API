package com.movies.services;

import com.movies.DTOs.ActorDTO;
import com.movies.entities.Actor;
import com.movies.entities.Movie;
import com.movies.exceptions.DuplicateEntityException;
import com.movies.exceptions.ResourceNotFoundException;
import com.movies.interfaces.ActorRepository;
import com.movies.interfaces.MovieRepository;
import com.movies.utils.AssociationUtils;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ActorService {
    private final ActorRepository actorRepository;
    private final MovieRepository movieRepository;

    public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
        this.actorRepository = actorRepository;
        this.movieRepository = movieRepository;
    }

    public Page<Actor> getAllActors(Pageable pageable) {
        return actorRepository.findAll(pageable);
    }

    public Actor getActorById(Long id) {
        return actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id " + id));
    }

    public Page<Actor> getActorsByName(String name, Pageable pageable) {
        return actorRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Actor createActor(ActorDTO actorDTO) {
        Optional<Actor> existingActor = actorRepository.findByNameAndBirthDate(actorDTO.getName(), actorDTO.getBirthDate());
        if (existingActor.isPresent()) {
            throw new DuplicateEntityException("Actor with name '" + actorDTO.getName() +
                    "' and birth date '" + actorDTO.getBirthDate() +
                    "' already exists with id " + existingActor.get().getId());
        }

        Actor newActor = new Actor();
        newActor.setName(actorDTO.getName());
        newActor.setBirthDate(actorDTO.getBirthDate());

        // Save actor first to avoid transient exception
        Actor savedActor = actorRepository.save(newActor);

        // Add movie associations if provided
        if (actorDTO.getMovieIds() != null && !actorDTO.getMovieIds().isEmpty()) {
            Set<Movie> movies = AssociationUtils.getAssociatedMovies(actorDTO.getMovieIds(), movieRepository);

            // Synchronize both sides of the relationship
            for (Movie movie : movies) {
                movie.getActors().add(savedActor);  // Add savedActor to each movie's actor set
            }

            savedActor.setMovies(movies);  // Set movies to savedActor
            movieRepository.saveAll(movies);  // Persist updated movies to synchronize relationship
        }

        return actorRepository.save(savedActor);  // Persist savedActor with synchronized relationships
    }


    public String deleteActor(Long id, boolean forceDelete) {
        Actor actor = getActorById(id);
        // Check if the actor is associated with any movies
        if (!actor.getMovies().isEmpty()) {
            if (!forceDelete) {
                // Return a message indicating the actor cannot be deleted due to associations
                return "Unable to delete actor '" + actor.getName() + "' as they are associated with " + actor.getMovies().size() + " movies.";
            } else {
                // Force delete: remove the actor from all associated movies
                for (Movie movie : actor.getMovies()) {
                    movie.getActors().remove(actor);
                    movieRepository.save(movie); // Ensure movie is updated to remove the actor
                }
                actor.getMovies().clear(); // Clear actor's movie associations
                actorRepository.save(actor); // Save updated actor with cleared associations
            }
        }

        // Proceed to delete the actor
        actorRepository.deleteById(id);
        return "Actor '" + actor.getName() + "' deleted successfully.";
    }

    public Actor partialUpdateActor(Long id, ActorDTO actorDTO) {
        Actor actor = getActorById(id); // Retrieve existing actor for updates

        // Update only the provided fields
        if (actorDTO.getName() != null) {
            actor.setName(actorDTO.getName());
        }

        if (actorDTO.getBirthDate() != null) {
            actor.setBirthDate(actorDTO.getBirthDate());
        }

        if (actorDTO.getMovieIds() != null && !actorDTO.getMovieIds().isEmpty()) {
            // Fetch movies based on provided IDs
            Set<Movie> movies = AssociationUtils.getAssociatedMovies(actorDTO.getMovieIds(), movieRepository);

            // Synchronize associations: clear existing and add new
            actor.getMovies().forEach(movie -> movie.getActors().remove(actor));
            movies.forEach(movie -> movie.getActors().add(actor));
            actor.setMovies(movies);
        }

        // Save the updated actor
        return actorRepository.save(actor);
    }
}
