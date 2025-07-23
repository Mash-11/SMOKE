package backend.recimeclone.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TextRequest(
        @NotBlank(message = "Text is required")
        @Size(min = 10, message = "Text must be at least 10 characters long")
        String text
) {}