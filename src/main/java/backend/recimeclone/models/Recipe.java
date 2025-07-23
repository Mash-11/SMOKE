package backend.recimeclone.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Document(collection = "Recipes")
@Data
@NoArgsConstructor
public class Recipe {
@Id
    private String id;

    private String userId;

    private String title;

    private String description;

    private int servings;

    private String imageUrl;

    private List<String> ingredients;

    private List<String> methods ;

    private int prepTime;

    private int cookTime;

    private LocalDateTime creationDate;

    private LocalDateTime updateDate;

    private LocalDateTime deletionDate;

    private List<String> tags;

    private List<String> categories;

    private String notes;

    private int rating;

}
