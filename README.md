# Movie Database API

This API is designed for managing a movie database, including movies, genres, and actors with bidirectional associations. This document provides a comprehensive overview of the features, setup, and usage of the API.

## Overview

This API allows you to:
- Manage movies, genres, and actors.
- Associate actors with movies and genres with movies.
- Perform CRUD operations for each entity.
- Filter and paginate results.
- Ensure proper bidirectional synchronization between associations.
- Handle partial updates for flexible updates to individual fields.
- Avoid circular JSON references through DTOs.
- **Secure endpoints with Authentication and Authorization**.
- **Explore API documentation interactively via Swagger UI**.

## Technologies

- **Java 21**
- **Spring Boot**
- **Hibernate (JPA)**
- **SQLite** (for development and testing)
- **Maven** (for dependency management)
- **Postman** (for testing and documentation)
- **Spring Security** (for Authentication & Authorization)
- **Swagger/OpenAPI 3.0** (for API documentation)

## Table of Contents

1. [Getting Started](#getting-started)
2. [Database Setup](#database-setup)
3. [Endpoints](#endpoints)
   - [Movies](#movies)
   - [Genres](#genres)
   - [Actors](#actors)
4. [Pagination](#pagination)
5. [Authentication & Authorization](#authentication--authorization)
6. [OpenAPI Documentation](#openapi-documentation)
7. [Error Handling](#error-handling)
8. [Sample Data](#sample-data)
9. [Postman Collection](#postman-collection)
10. [Additional Features](#additional-features)
11. [Contact](#contact)

---

## Getting Started

1. **Clone the repository**:
    ```bash
    git clone https://gitea.kood.tech/vsevolodbursa/kmdb.git
    cd kmdb
    ```

2. **Build the project**:
    ```bash
    mvn clean install
    ```

3. **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

   The application will start on `http://localhost:8080`.

## Database Setup

The project uses **SQLite** as the database, and the database file `movie-db.sqlite` is located in the `src/main/resources` directory.

**Note:** While embedding the database within the `resources` directory simplifies initial setup, it's generally not recommended for production environments because the `resources` directory is packaged into the executable JAR/WAR, making it effectively read-only at runtime.

For development and testing purposes, this setup is acceptable.

## Endpoints

### Movies

- **POST /api/movies**: Create a new movie. Accepts a JSON payload with optional `actorIds` and `genreIds` to associate existing actors and genres.
- **GET /api/movies**: Retrieve all movies. Supports pagination.
- **GET /api/movies?genre={genreId}**: Retrieve movies filtered by genre.
- **GET /api/movies?year={releaseYear}**: Retrieve movies filtered by release year.
- **PATCH /api/movies/{id}**: Partially update a movie, including associations with genres and actors.
- **DELETE /api/movies/{id}**: Delete a movie. Force delete option available to remove associations.

### Genres

- **POST /api/genres**: Create a new genre. Supports `movieIds` for associating movies at creation.
- **GET /api/genres**: Retrieve all genres with pagination support.
- **GET /api/genres?name={name}**: Search genres by name.
- **PATCH /api/genres/{id}**: Partially update a genre, including updating movie associations.
- **DELETE /api/genres/{id}**: Delete a genre. Force delete available.

### Actors

- **POST /api/actors**: Create a new actor. Supports `movieIds` to associate with existing movies.
- **GET /api/actors**: Retrieve all actors with pagination.
- **GET /api/actors?name={name}**: Search actors by name.
- **PATCH /api/actors/{id}**: Partially update an actor, including updating movie associations.
- **DELETE /api/actors/{id}**: Delete an actor. Force delete available.

## Pagination

To paginate results for movies, genres, or actors, use `page` and `size` query parameters. For example:

- **page**: Defines the page number (default is 0).
- **size**: Defines the number of records per page (default is 10, maximum is 100).

For instance:

```
GET /api/movies?page=0&size=10
```

---

## Authentication & Authorization

The API is secured using **Spring Security** with **JWT (JSON Web Tokens)** for authentication and authorization. This ensures that only authenticated users can access protected endpoints and perform actions based on their roles.

### 1. User Roles

- **ROLE_USER**: Standard user with permissions to view and manage movies.
- **ROLE_ADMIN**: Administrator with elevated permissions, including managing genres and actors.

### 2. Secured Endpoints

- **Public Endpoints**:
   - **POST /api/auth/register**: Register a new user.
   - **POST /api/auth/login**: Authenticate a user and obtain a JWT.

- **Protected Endpoints**:
   - **All CRUD operations for Movies, Genres, and Actors** require authentication.
   - **Role-Based Access Control**: Certain actions (e.g., deleting genres) may require specific roles like `ROLE_ADMIN`.

### 3. Obtaining a JWT
`

1. **Authenticate and Receive JWT**:
    ```http
    POST /api/auth/login
    Content-Type: application/json

    {
        "username": "user",
        "password": "password"
    }
    ```

   **Response**:
    ```json
    {
        "token": "eyJhbGciOiJIUzI1NiIsInR..."
    }
    ```

2. **Use the JWT in Subsequent Requests**:

   Include the JWT in the `Authorization` header for protected endpoints:
    ```
    Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR...
    ```

### 4. Password Encoding

Passwords are securely stored using **BCrypt** hashing to ensure they are not stored in plain text.

---

## OpenAPI Documentation

The API includes comprehensive **OpenAPI 3.0** documentation, which can be accessed interactively via **Swagger UI**. This documentation provides detailed information about all available endpoints, request/response schemas, authentication mechanisms, and more.

### Accessing Swagger UI

After running the application, navigate to the following URL to access the Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

### Features of Swagger UI

- **Interactive API Explorer**: Test API endpoints directly from the browser.
- **Detailed Endpoint Descriptions**: Understand the purpose, parameters, and responses for each endpoint.
- **Authentication Integration**: Easily include JWT tokens to authenticate requests within Swagger UI.

### Example Usage

#### Create a New Movie
```http
POST /api/movies
Content-Type: application/json

{
    "title": "Inception",
    "year": 2010,
    "duration": 148,
    "actors": [
        { "name": "Leonardo DiCaprio", "birthDate": "1974-11-11" },
        { "name": "Ellen Page", "birthDate": "1987-02-21" }
    ],
    "genres": [
        { "name": "Science Fiction" },
        { "name": "Thriller" }
    ]
}
```

#### Update an Existing Movie’s Actors
```http
PATCH /api/movies/1
Content-Type: application/json

{
    "actorIds": [1, 2, 3]
}
```

#### Retrieve Movies by Title with Pagination
```http
GET /api/movies/search?title=Inception&page=0&size=10
```

#### Delete a Movie
```http
DELETE /api/movies/1
```
## Error Handling

The API includes custom error handling with detailed error messages for:

- **ResourceNotFoundException**: Thrown when a requested resource (e.g., movie, genre, actor) does not exist.
- **DuplicateEntityException**: Thrown when attempting to create a resource that already exists.
- **InvalidPaginationParameterException**: Thrown for invalid pagination parameters.
- **AssociationNotFoundException**: Thrown when trying to delete or update a non-existent association.


### Testing

A Postman collection named Movie Database API is available in the postman folder, that is in project root folder, including all endpoints and sample requests for testing. It is necessary to obtain a jwt token thru login request first. Import this collection into Postman to test each endpoint interactively.

### Additional features

1. **Nested Entity Creation**: Movies can be created with new & existing associated genres and actors seamlessly in one POST request. When creating a movie in such way, the program will check if the genres / actors are already in the db and will only add new entities, whilst associating the existing ones. Actors and genres can be created with relations to existing movies only.
2. **openAPI 3.0 documentation**: accessed via http://localhost:8080/swagger-ui/index.html#/.
3. **Extended filtering & search**: all search & filter methods by name / title employ case-insensitive partial search methods.
4. **Authentication & Authorization**: Secured endpoints ensure that only authenticated users with appropriate roles can perform certain actions. This feature enhances the security and integrity of the application by controlling access based on user roles.

---

### Contact

For any questions or suggestions, please reach out to Discord vb609.