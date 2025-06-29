package backend.recimeclone.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;


import java.util.List;

public record RecipeRequestDto(
        @NotBlank(message = "Title is required")
        @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
        String title,

        String description, // Optional

        @Min(value = 1, message = "Servings must be at least 1")
        int servings,

        // imageUrl can be null or blank if not required
        String imageUrl,

        @NotNull(message = "Ingredients list cannot be null")
        @Size(min = 1, message = "Recipe must have at least one ingredient")
        List<String> ingredients,

        @NotNull(message = "Methods list cannot be null")
        @Size(min = 1, message = "Recipe must have at least one method step")
        List<String> methods,

        @Min(value = 0, message = "Preparation time cannot be negative")
        int prepTime,

        @Min(value = 0, message = "Cook time cannot be negative")
        int cookTime,

        List<String> tags, // Optional
        List<String> categories, // Optional
        String notes, // Optional

        @Min(value = 0, message = "Rating must be between 0 and 5")
        @Max(value = 5, message = "Rating must be between 0 and 5")
        Integer rating
) {}