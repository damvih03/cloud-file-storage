package com.damvih.storage.util;

import com.damvih.storage.exception.InvalidQueryException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class QueryValidator {

    public void validate(String query) {
        if (query.isBlank()) {
            throw new InvalidQueryException("Query is blank.");
        }
    }

}
