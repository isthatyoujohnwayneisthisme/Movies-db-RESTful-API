package com.movies.config;

import com.movies.entities.User;
import com.movies.interfaces.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("DataInitializer run method started");

        // Delete existing users
        userRepository.deleteAll();
        logger.info("Deleted all existing users");

        // Create a standard user
        User user = new User();
        user.setUsername("user");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEnabled(true);
        user.setRoles(Set.of("ROLE_USER"));
        userRepository.save(user);
        logger.info("Created user: {}", user.getUsername());

        // Create an admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setEnabled(true);
        admin.setRoles(Set.of("ROLE_ADMIN"));
        userRepository.save(admin);
        logger.info("Created user: {}", admin.getUsername());

        logger.info("Initialized users in the database.");
    }
}