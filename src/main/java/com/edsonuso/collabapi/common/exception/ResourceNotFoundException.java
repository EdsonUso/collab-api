package com.edsonuso.collabapi.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String resource, String identifier) {
        super("%s não encontrado: %s".formatted(resource, identifier), HttpStatus.NOT_FOUND);
    }
}
