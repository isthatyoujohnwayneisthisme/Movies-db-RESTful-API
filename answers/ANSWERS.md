## Answers to task questions

### Student can describe the structure of a REST API URL, including base URL, resource, and query parameters.

The structure of a REST API URL typically consists of three main parts:

1. Base URL
   The base URL is the foundational part of the URL that points to the domain or IP address where the API is hosted. It provides the starting point to access the API and remains consistent for all endpoints within the same API.
   Example: https://api.example.com or http://localhost:8080 for local development.
   In this project, an example base URL for a local server could be http://localhost:8080/api.
2. Resource Path
   The resource path specifies the resource or entity that the API endpoint deals with, such as movies, actors, or genres. It defines the type of resource the client is interacting with and is usually placed immediately after the base URL.
   This path may also include identifiers to specify particular instances of resources, such as /movies/1 to refer to a movie with ID 1.
   Example: https://api.example.com/movies/1 would refer to the resource for a specific movie with ID 1.
3. Query Parameters
   Are optional parameters appended to the end of the URL, typically used to filter, sort, paginate, or customize the response.
   They follow a ? symbol and are specified in key-value pairs, separated by & if there are multiple parameters. Query parameters allow clients to control certain aspects of the resource data returned without affecting the core resource path.
   Example: https://api.example.com/movies?genre=1&year=2022&page=1&size=10
   In this example:
   genre=1 might filter for movies in genre with ID 1.
   year=2022 might filter for movies released in 2022.
   page=1 and size=10 are pagination parameters, where page specifies the page number and size indicates the number of items per page.

EXAMPLE:
GET http://localhost:8080/api/movies?genre=2&page=0&size=5
This URL breaks down as follows:

Base URL: http://localhost:8080/api
Resource Path: /movies
Query Parameters:
genre=2: Filters movies of genre ID 2.
page=0&size=5: Limits results to the first page, showing 5 movies per page.
This structure makes REST API URLs readable, logical, and allows clients to specify exactly the data they need in a standardized format.

### Student can explain the four main HTTP methods used in this project (GET, POST, PATCH, DELETE) and their purposes.

1. GET
   Purpose: Retrieve data from the server.

   Function in Project:
   
   The GET method is used to fetch resources like movies, genres, and actors.
   Examples include retrieving all movies, finding actors by name, filtering movies by genre or release year, and paginating results.
   Characteristics:
   
   GET requests are idempotent (multiple identical requests produce the same result) and safe (do not alter the server’s state).
   Data is retrieved in response to client queries and often returned in JSON format.
   Examples in Your Project:
   http
   Copy code
   GET /api/movies            # Retrieve all movies
   GET /api/movies/1          # Retrieve the movie with ID 1
   GET /api/actors?name=John  # Retrieve actors filtered by name "John"
2. POST
   Purpose: Create a new resource on the server.

   Function in Project:
   
   The POST method is used for creating new entries, such as adding a new movie, actor, or genre.
   For example, when adding a new movie, the request may include details like title, release year, genres, and associated actors.
   Characteristics:
   
   POST requests are not idempotent (repeated identical requests create multiple resources).
   POST usually includes a request body with the resource data, which the server processes and stores.
   Examples in Project:
   http
   Copy code
   POST /api/movies            # Create a new movie
   POST /api/genres            # Create a new genre
   POST /api/actors            # Create a new actor
3. PATCH
   Purpose: Partially update an existing resource on the server.

   Function in Project:
   
   The PATCH method is used for making partial updates to resources, like updating specific fields of a movie, actor, or genre.
   It allows clients to modify only certain properties without sending the entire resource, which is more efficient.
   Characteristics:
   
   PATCH requests are typically not idempotent if the operation modifies a resource’s state in a non-repeatable way.
   The request body usually includes only the fields that should be updated.
   Examples in Project:
   
   http
   Copy code
   PATCH /api/movies/1         # Partially update the movie with ID 1
   PATCH /api/actors/2         # Partially update the actor with ID 2
4. DELETE
   Purpose: Remove a resource from the server.

   Function in Project:
   
   The DELETE method is used to delete resources, such as removing a movie, actor, or genre by its ID.
   This includes optional parameters like forceDelete, which can be used to override constraints (e.g., removing an actor despite associations).
   Characteristics:
   
   DELETE requests are idempotent; multiple identical DELETE requests for the same resource result in the same outcome (the resource remains deleted).
   A successful DELETE returns a 204 No Content status to indicate the resource was deleted without returning additional data.
   Examples in Project:
   http
   Copy code
   DELETE /api/movies/1        # Delete the movie with ID 1
   DELETE /api/genres/5        # Delete the genre with ID 5
   DELETE /api/actors/3        # Delete the actor with ID 3

Summary of Purposes in Project
Each of these methods in RESTful APIs aligns with CRUD operations:

Create (POST) – Adds new resources.
Read (GET) – Retrieves resources.
Update (PATCH) – Modifies existing resources.
Delete (DELETE) – Removes resources.
Using these methods as per HTTP standards in your project ensures predictable behavior, standardized data handling, and logical, readable operations.

### Student understands the concept of CRUD operations and their importance in database management.

See above.

### Student can explain what dependency injection is and how it's used in this project.

Dependency Injection (DI) is a design pattern and technique used in software development to achieve Inversion of Control (IoC). Instead of classes creating and managing their dependencies, DI allows those dependencies to be provided externally, often by a DI framework or container (like Spring’s DI container). This approach promotes loose coupling, enhances testability, and improves code readability and maintainability.

Key Components of Dependency Injection
Dependencies: These are the objects or services a class needs to function. For example, ActorService depends on ActorRepository and MovieRepository to retrieve and store data.
Injection: The dependencies are injected, or provided, to the dependent class (rather than being created within the class). This can be done via constructor injection, setter injection, or field injection.

Inversion of Control (IoC): The process of creating and managing dependencies is handled outside the dependent class, often by a DI container (like Spring's).

Constructor Injection: Dependencies are provided through the constructor of a class.
Field Injection: Dependencies are directly injected into the fields of a class (using @Autowired in Spring).
Setter Injection: Dependencies are provided via setter methods.

1. Service Layer Dependency Injection
   In service classes (ActorService, GenreService, MovieService), you inject repository dependencies. For example:
   ```
   java
   Copy code
   @Service
   public class ActorService {
   private final ActorRepository actorRepository;
   private final MovieRepository movieRepository;
   
       public ActorService(ActorRepository actorRepository, MovieRepository movieRepository) {
           this.actorRepository = actorRepository;
           this.movieRepository = movieRepository;
       }
   
       // Methods...
   }
   ```
   In this case:
   
   ActorService requires ActorRepository and MovieRepository to perform operations like creating, retrieving, or deleting actors.
   Instead of creating new instances of these repositories, the Spring framework injects them via the constructor. This promotes loose coupling, as ActorService only depends on the interfaces (ActorRepository and MovieRepository), making the service easier to test and maintain.
2. Controller Layer Dependency Injection
   In your controllers (ActorController, GenreController, MovieController), you inject service dependencies to handle requests. For example:
   ```
   @RestController
   @RequestMapping("/api/actors")
   public class ActorController {
   private final ActorService actorService;
   
       public ActorController(ActorService actorService) {
           this.actorService = actorService;
       }
   
       // Methods...
   }
   ```
   Here:
   
   ActorController requires ActorService to manage actor-related requests, like retrieving and updating actors.
   By injecting ActorService through the constructor, the controller does not need to know how the service is instantiated, promoting separation of concerns and making the controller easier to test with mock services.
3. Utility Class Dependency Injection
   In some cases, utility classes like AssociationUtils may have methods that don’t require stateful objects, so dependency injection is unnecessary. Instead, these methods are called statically, allowing shared functionality without instantiation. However, if a utility class does depend on other components, it can also be injected through DI as needed.

Benefits of Using Dependency Injection
Loose Coupling: Components (e.g., services and repositories) are less dependent on the specific implementations of each other, as they rely on abstractions (interfaces). This makes it easy to replace or modify parts of the code independently.
Testability: DI allows us to inject mock dependencies during testing, enabling isolated and straightforward unit testing.
Scalability and Maintainability: Since classes only depend on abstractions, the code is easier to extend and maintain over time, especially when adding new functionality or modifying existing ones.
In summary, dependency injection allows project’s components to interact with each other through interfaces and constructor-based injection, which streamlines testing, maintains loose coupling, and improves modularity. Spring manages this dependency wiring, reducing the need for manual setup and ensuring efficient resource management across your application.

### Student can demonstrate various relationship scenarios.

- **GET http://localhost:8080/api/movies/1**: 
   ```
   {
    "id": 1,
    "title": "Inception",
    "releaseYear": 2010,
    "duration": 148,
    "actors": [
        {
            "id": 3,
            "name": "Joseph Gordon-Levitt"
        },
        {
            "id": 1,
            "name": "Leonardo DiCaprio"
        },
        {
            "id": 2,
            "name": "Ellen Page"
        }
    ],
    "genres": [
        {
            "id": 2,
            "name": "Thriller"
        },
        {
            "id": 1,
            "name": "Science Fiction"
        }
    ]
   }
   ```

- **GET http://localhost:8080/api/movies?actor=1&page=0&size=10**:
   ```
   {
    "content": [
        {
            "id": 1,
            "title": "Inception",
            "releaseYear": 2010,
            "duration": 148
        },
        {
            "id": 2,
            "title": "Titanic",
            "releaseYear": 1997,
            "duration": 195
        },
        {
            "id": 22,
            "title": "Shutter Island",
            "releaseYear": 2010,
            "duration": 138
        },
        {
            "id": 23,
            "title": "Zodiac",
            "releaseYear": 2007,
            "duration": 157
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "sorted": false,
            "empty": true,
            "unsorted": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 4,
    "size": 10,
    "number": 0,
    "sort": {
        "sorted": false,
        "empty": true,
        "unsorted": true
    },
    "first": true,
    "numberOfElements": 4,
    "empty": false
   }
   ```

### Student can explain what a JpaRepository is and at least three methods it provides out of the box.

JpaRepository is a part of Spring Data JPA, an abstraction layer that simplifies data access and database operations for JPA (Java Persistence API). JpaRepository extends the CrudRepository and PagingAndSortingRepository interfaces, providing additional JPA-specific functionality, including CRUD operations, pagination, and sorting capabilities. When creating a repository interface (e.g., MovieRepository) and extending JpaRepository, Spring Data JPA automatically generates the implementation, so we don’t have to write boilerplate code.

Common Methods Provided by JpaRepository:
findAll():

Returns a list of all entities of a particular type from the database.
Useful for retrieving all records, though it’s typically combined with pagination in larger datasets.
```
List<Movie> movies = movieRepository.findAll();
```

findById(ID id):

Fetches an entity by its primary key. It returns an Optional containing the entity if found, or an empty Optional if not.
This method is often used when looking up specific records by their unique ID.
```
Optional<Movie> movie = movieRepository.findById(1L);
```

save(S entity):

Saves an entity to the database. If the entity’s ID is null, it performs an insert operation (creating a new record); if the ID is present, it performs an update operation.
Useful for both creating new records and updating existing ones.
```
Movie savedMovie = movieRepository.save(movie);
```

deleteById(ID id):
Deletes an entity by its ID, making it a convenient way to remove records without loading the full entity.

findAll(Pageable pageable):
Provides paginated results, returning a Page of entities, which is very useful when working with large datasets.
Using JpaRepository can significantly reduce the amount of code we write for data operations, making it a valuable tool in building and managing persistence layers in Spring applications.

### Student can explain the purpose of the @SpringBootApplication annotation.

The @SpringBootApplication annotation is a core annotation in Spring Boot that combines three important annotations into one, simplifying the setup and configuration of a Spring Boot application. Its primary purpose is to mark the main class of a Spring Boot application and enable essential features for auto-configuration, component scanning, and Spring Boot configuration.

Here's a breakdown of what @SpringBootApplication does:

@Configuration:
Indicates that the class is a source of bean definitions. It allows us to define beans within the annotated class that will be managed by Spring's IoC (Inversion of Control) container.
This enables Spring to manage and inject dependencies throughout the application.
This configuration enables dependency injection, allowing components such as ActorService, MovieService, and GenreService to be injected automatically into their respective controllers, reducing boilerplate code and enhancing modularity.

@EnableAutoConfiguration:
Enables Spring Boot’s auto-configuration feature. Spring Boot automatically configures various components based on the dependencies added to the project, such as database configurations since spring-boot-starter-data-jpa is present.
This reduces the need for manual configuration by attempting to match project requirements with default configurations automatically.
Spring Boot configures database properties, setting up connections, and enabling features like JpaRepository. This way, we don't have to manually configure aspects like the database connection, entity management, and other common tasks related to data access.
Auto-configuration also means that features like transaction management are enabled automatically. Our project’s methods, like creating or updating movies and actors, use transactional behavior provided by Spring, which is enabled by auto-configuration.

@ComponentScan:
Enables component scanning, which allows Spring to discover and register beans (i.e., components, services, controllers) in the application context.
By default, it scans the package where the main class resides, so you can place components within this package or its sub-packages to have them automatically picked up by Spring.
This is why our @Service, @Repository, and @RestController classes are automatically detected, and their beans are registered in the application context.

### Student can explain the purpose of the @Entity annotation.

The @Entity annotation in our project is a key JPA (Java Persistence API) annotation that marks a Java class as a persistent entity, meaning it is mapped to a table in a relational database. This annotation enables classes to represent tables in the database, with each instance of the class corresponding to a row in that table.

### Student can describe the difference between eager and lazy loading in JPA and which is the default for @ManyToMany relationships.

In JPA, eager and lazy loading refer to when related entities are loaded from the database in relation to a primary entity.
With lazy loading, related entities are fetched from the database only when they are accessed for the first time in code.
With eager loading, related entities are loaded immediately when the primary entity is loaded from the database.
In JPA, @ManyToMany relationships are lazy-loaded by default. This means that when we retrieve a Movie entity, the related Actor entities are not loaded until explicitly accessed. However, we do explicitly access them sine we have Set<Actor> and Set<Genre> properties in our Movie entity - so, Jackson attempts to serialize these collections, too.

### Student can describe the role of service layer in a Spring Boot application.

In a Spring Boot application, the service layer acts as an intermediary between the controller layer (which handles HTTP requests) and the data access layer (repositories that interact with the database). It encapsulates the business logic of the application, ensuring that operations are performed consistently and according to the business rules.

### Student understands the difference between GET, POST, PATCH, and DELETE HTTP methods.

See above, second question from the top.

### Student can explain the purpose of the application.properties file in a Spring Boot project. Ask the student to explain the contents of application.properties file for SQLite configuration.

The application.properties file in a Spring Boot project is used to configure application settings. It allows us to set properties that control the behavior of Spring Boot, the application itself, and connected resources, such as databases. By centralizing these settings in application.properties, we make the application more flexible, portable, and easier to maintain, as these configurations can be adjusted without changing the actual code.

Since we’re using SQLite in our movie database project, the application.properties file needs to specify SQLite as the database and configure its connection path. Below are the entries we have for configuring an SQLite database in application.properties:
```
# Specify the database platform
spring.datasource.platform=sqlite

# JDBC URL to connect to the SQLite database file
spring.datasource.url=jdbc:sqlite:movie-db.sqlite

# Driver class name for SQLite
spring.datasource.driver-class-name=org.sqlite.JDBC

# Hibernate settings
spring.jpa.database-platform=org.hibernate.dialect.SQLiteDialect
# allow Hibernate to automatically create or update the database schema as per the entity classes without losing data. 
spring.jpa.hibernate.ddl-auto=update
```
This configuration allows us to manage SQLite without any additional code, centralizing all relevant settings in one place.

### Student understands the importance of input validation in API development.

1. Ensuring Data Integrity
   Input validation enforces rules on the data submitted to the API, ensuring it meets expected formats, types, and constraints. For instance, in your project:
   title and name fields should not be blank, so applying @NotBlank ensures that empty strings aren’t accepted.
   releaseYear and birthDate fields should follow specific date rules, such as being in the past or meeting a valid date format.
   duration fields should be positive values, verified with annotations like @Min.
   Without validation, incorrect or malformed data could enter the system, leading to inconsistencies or requiring manual cleanup.
2. Preventing Security Vulnerabilities
   APIs are susceptible to security risks such as SQL Injection (less of a problem in Spring apps) and Cross-Site Scripting (XSS). Input validation acts as the first line of defense against these attacks by:
   - Type Checking: Ensuring only expected data types are processed. For instance, only numbers should be accepted for id fields, preventing harmful scripts or unexpected data formats.
   - Constraint Enforcement: Limiting data to valid, predefined formats or ranges, such as numeric IDs, strings, or dates, helps reduce the risk of malicious payloads.
   In our project, validation constraints like @Pattern on dates prevent attackers from injecting malicious code into date fields by ensuring inputs adhere strictly to a specified format.
3. Error Handling and User Feedback
     Proper input validation allows the API to catch errors early and return meaningful messages, improving the user experience and making debugging easier. For example:
     Invalid data types (like entering “abc” for releaseYear) can be intercepted with a clear error message like "Invalid value 'abc' for releaseYear. Expected an integer."
     Providing these specific error messages, especially for date formats or type mismatches, helps API consumers understand and correct their requests.
     By defining clear error responses, the project becomes easier to work with, reducing frustration and enhancing documentation accuracy.
4. Optimizing Database and System Resources
   Accepting only valid inputs minimizes the workload on our database and other backend components. Invalid data would otherwise be processed, stored, or trigger cascading errors across services.
   In our project, @PastOrPresent on dates (e.g., birthDate) ensures that future dates aren't allowed, thereby preventing unrealistic entries that could complicate data management or analytics.
5. Enhancing Code Readability and Maintainability
   Adding annotations such as @NotNull, @Min, @Pattern, and @Size makes validation rules explicit, providing documentation for the expected structure of data directly within the code.
   This practice also promotes consistency across entities, DTOs, and services, making it easier to understand and maintain the API.
   Examples of Input Validation in Your Project


### Student can explain what a 404 HTTP status code means and when it should be used in this project.

A 404 HTTP status code means "Not Found" and is returned when a client requests a resource (such as data or a page) that the server cannot locate. In the context of our project, a 404 status code is used to signal that a specific requested entity—such as a movie, actor, or genre—does not exist in the database when it's explicitly accessed.

### Student can describe the purpose of the @Valid annotation in controller methods.

The @Valid annotation is used in Spring Boot controller methods to trigger validation on the fields of an incoming request body, ensuring that data adheres to the defined constraints before it’s processed further. In our project, this annotation is essential for enforcing the validation rules specified in DTOs, helping to maintain data integrity and reliability.
When applied to a DTO object, the @Valid annotation initiates a validation process based on the constraints defined in the DTO’s fields. If any of the validation rules fail, the framework will immediately return an error response with a 400 Bad Request status and detailed validation error message specified, rather than allowing the request to proceed further into the controller logic.

## Answers to Erik's questions

### Why not validate input fields in input dto?

Copied validation to DTOs as it makes more sense.

### Why use JsonIdentityInfo?

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") is used by Jackson during JSON serialization and deserialization. It tells Jackson to track objects by their id property (the one generated in the database), so that cyclic references between objects can be managed without causing infinite loops.
When two entities reference each other (like Movie references Actor and Actor references Movie), a cyclic reference can occur, causing infinite loops during JSON serialization. @JsonIdentityInfo helps prevent this by assigning an identity to each object, allowing Jackson to recognize and avoid repeated serialization of the same object.
In other words, when an object with @JsonIdentityInfo is serialized, Jackson outputs only the id property for any object it has already encountered in the serialization process, instead of re-serializing the full object with all nested properties.
This helps prevent the infinite recursion issue in nested, bidirectional relationships (like Movie and Actor both referencing each other), but also leads to the same actor/genre being shown as ids only if we aim to produce a list of movies that contain repeating actors/genres.
HOWEVER: I switched to using @JsonManagedReference and @JsonBackReference. They avoid infinite recursion by defining one side of the relationship as the “parent” (managed) and the other as the “child” (back).

### What is SQLITE zone identifier?
- SQLite does not store time zone information directly with date-time fields.
- By default, SQLite stores date and time values as UTC.
- It’s recommended to normalize all dates to UTC before storing them in SQLite.
- When retrieving dates, format them as needed for display in the user’s time zone if required.

But it has no relation to our project since we only deal with dates.

For any additional questions or suggestions, please reach out to Discord vb609.