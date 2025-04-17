package com.saksham.booking_application.common.exceptions;

import java.util.Map;

public class ValidationErrorResponse extends ErrorResponse {
    private Map<String, String> validationErrors;

    public ValidationErrorResponse(String errorCode, String errorMessage, Map<String, String> validationErrors) {
        super(errorCode, errorMessage);
        this.validationErrors = validationErrors;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }
}
