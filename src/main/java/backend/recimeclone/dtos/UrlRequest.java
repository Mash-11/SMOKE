package backend.recimeclone.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UrlRequest(
        @NotBlank(message = "URL is required")
        @Pattern(regexp = "^https?://.*", message = "Must be a valid HTTP/HTTPS URL")
        String url
) {}