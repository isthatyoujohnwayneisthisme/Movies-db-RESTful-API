package com.movies.controller;

import com.movies.DTOs.ActorDTO;
import com.movies.entities.Actor;
import com.movies.exceptions.ResourceNotFoundException;
import com.movies.services.ActorService;
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
@RequestMapping("/api/actors")
@SecurityRequirement(name = "bearerAuth")
public class ActorController {

    private final ActorService actorService;

    @Autowired
    public ActorController(ActorService actorService) {
        this.actorService = actorService;
    }

    @GetMapping
    @Operation(summary = "Get a list of actors", description = "Retrieve a paginated list of actors, optionally filtering by name.")
    public ResponseEntity<Page<Actor>> getActors(
            @Parameter(description = "Filter actors by name") @RequestParam(required = false) String name,
            @Parameter(description = "Page number (zero-based index)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of records per page") @RequestParam(defaultValue = "10") int size) {

        // Use centralized pagination creation and validation
        Pageable pageable = PaginationUtils.createPageRequest(page, size);
        Page<Actor> actors;

        if (name != null && !name.isEmpty()) {
            actors = actorService.getActorsByName(name, pageable);
        } else {
            actors = actorService.getAllActors(pageable);
        }

        if (actors.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 No Content if no actors are found
        }

        return ResponseEntity.ok(actors); // 200 OK with paginated actors
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an actor by ID", description = "Retrieve an actor by their ID.")
    public ResponseEntity<Actor> getActorById(
            @Parameter(description = "ID of the actor to retrieve") @PathVariable Long id) {
        Actor actor = actorService.getActorById(id);
        return ResponseEntity.ok(actor);
    }

    @PostMapping
    @Operation(summary = "Create a new actor", description = "Create a new actor with the provided details.")
    public ResponseEntity<Actor> createActor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Actor data transfer object")
            @Valid @RequestBody ActorDTO actorDTO) {
        Actor savedActor = actorService.createActor(actorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedActor);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an actor", description = "Delete an actor by ID, with an option to force delete.")
    public ResponseEntity<String> deleteActor(
            @Parameter(description = "ID of the actor to delete") @PathVariable Long id,
            @Parameter(description = "Set to true to force delete") @RequestParam(defaultValue = "false") boolean forceDelete) {
        String result = actorService.deleteActor(id, forceDelete);
        if (result.startsWith("Unable to delete")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(result);  // Return conflict message if unable to delete
        }
        return ResponseEntity.noContent().build();  // Return 204 No Content for successful deletion
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update an actor", description = "Update an existing actor's details.")
    public ResponseEntity<Actor> updateActor(
            @Parameter(description = "ID of the actor to update") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Updated actor data")
            @Valid @RequestBody ActorDTO actorDTO) {
        Actor updatedActor = actorService.partialUpdateActor(id, actorDTO);
        return ResponseEntity.ok(updatedActor);
    }
}
