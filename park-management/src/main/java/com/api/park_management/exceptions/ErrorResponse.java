package com.api.park_management.exceptions;

import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private final String message;
    private final HttpStatus status;
}