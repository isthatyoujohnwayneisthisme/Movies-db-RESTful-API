package com.movies.utils;

import com.movies.exceptions.InvalidPaginationParameterException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


public class PaginationUtils {
    private static final int MAX_SIZE = 100;

    public static Pageable createPageRequest(int page, int size) {
        if (page < 0 || size <= 0 || size > MAX_SIZE) {
            throw new InvalidPaginationParameterException(
                    "Invalid pagination parameters: 'page' must be 0 or greater, and 'size' must be between 1 and " + MAX_SIZE + "."
            );
        }
        return PageRequest.of(page, size);
    }
}
