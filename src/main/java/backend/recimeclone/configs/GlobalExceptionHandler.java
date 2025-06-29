package backend.recimeclone.configs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice // This annotation makes this class capable of handling exceptions across the application
public class GlobalExceptionHandler {

    /**
     * Handles MethodArgumentNotValidException, which is thrown when @Valid annotation fails.
     * @param ex The exception thrown by Spring Validation.
     * @return A ResponseEntity containing a map of field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // Get the field name (e.g., "password")
            String errorMessage = error.getDefaultMessage(); // Get the message from your @Size, @NotBlank, etc.
            errors.put(fieldName, errorMessage);
        });
        // Return a 400 Bad Request status with the map of errors
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


}