package backend.recimeclone.controllers;

import backend.recimeclone.dtos.RecipeRequestDto; // Import the new DTO
import backend.recimeclone.models.Recipe;
import backend.recimeclone.repos.RecipeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder; // For getting authenticated user ID

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional; // For findById

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid; // For validating DTOs

@RestController
@RequestMapping("/api/v1/recipes") // CORRECTED: lowercase 'recipes'
public class RecipeController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

    private final RecipeRepository recipeRepository;

    public RecipeController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Retrieves all recipes from the database. If the database is empty,
     * it creates and saves a fake recipe for initial testing purposes.
     * @return A ResponseEntity containing a list of Recipe objects and HTTP status 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes(){
        List<Recipe> recipes = recipeRepository.findAll();

        if (recipes.isEmpty()) {
            logger.info("Database is empty. Creating and saving a dummy recipe for initial testing.");
            Recipe dummyRecipe = new Recipe();
            dummyRecipe.setUserId("initial_dummy_user"); // Fake user ID for this initial recipe
            dummyRecipe.setTitle("Spicy Chicken Stir-fry (Dummy)");
            dummyRecipe.setDescription("A quick and flavorful chicken stir-fry perfect for busy weeknights.");
            dummyRecipe.setServings(4);
            dummyRecipe.setImageUrl("https://via.placeholder.com/400/FF0000/FFFFFF?text=Mixit+Recipe");
            dummyRecipe.setIngredients(Arrays.asList("500g chicken breast", "1 tbsp soy sauce", "1 tsp ginger", "Mixed vegetables"));
            dummyRecipe.setMethods(Arrays.asList("Cut chicken", "Stir-fry vegetables", "Add sauce and chicken"));
            dummyRecipe.setPrepTime(15);
            dummyRecipe.setCookTime(20);
            dummyRecipe.setCreationDate(LocalDateTime.now());
            dummyRecipe.setUpdateDate(LocalDateTime.now());
            dummyRecipe.setTags(Arrays.asList("chicken", "stir-fry", "dinner", "quick"));
            dummyRecipe.setCategories(Arrays.asList("main course", "asian"));
            dummyRecipe.setNotes("Adjust spice level to your preference.");
            dummyRecipe.setRating(4);

            recipeRepository.save(dummyRecipe);
            recipes = Collections.singletonList(dummyRecipe);
            logger.info("Dummy recipe saved and returned.");
        }

        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    /**
     * Creates a new recipe based on the provided data.
     * @param requestDto The RecipeRequestDto sent from the client (e.g., frontend).
     * @return A ResponseEntity containing the created Recipe object and HTTP status 201 Created.
     */
    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody RecipeRequestDto requestDto) {
        // Extract userId from an authenticated user's context (JWT token)
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName(); // Assuming username is email

        // Convert DTO to Recipe model
        Recipe newRecipe = new Recipe();
        newRecipe.setUserId(currentUserId); // Set userId from the authenticated user
        newRecipe.setTitle(requestDto.title());
        newRecipe.setDescription(requestDto.description());
        newRecipe.setServings(requestDto.servings());
        newRecipe.setImageUrl(requestDto.imageUrl());
        newRecipe.setIngredients(requestDto.ingredients());
        newRecipe.setMethods(requestDto.methods());
        newRecipe.setPrepTime(requestDto.prepTime());
        newRecipe.setCookTime(requestDto.cookTime());
        newRecipe.setTags(requestDto.tags());
        newRecipe.setCategories(requestDto.categories());
        newRecipe.setNotes(requestDto.notes());
        newRecipe.setRating(requestDto.rating()); // Rating might be null if not provided in DTO

        newRecipe.setCreationDate(LocalDateTime.now()); // Set the creation timestamp
        newRecipe.setUpdateDate(LocalDateTime.now());   // Set initial update timestamp

        Recipe savedRecipe = recipeRepository.save(newRecipe);
        logger.info("New recipe created with ID: {} by user: {}", savedRecipe.getId(), currentUserId);
        return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
    }

    /**
     * Retrieves a single recipe by its ID.
     * @param id The ID of the recipe to retrieve.
     * @return A ResponseEntity containing the Recipe object and HTTP status 200 OK, or 404 Not Found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable String id) {
        Optional<Recipe> recipe = recipeRepository.findById(id);
        if (recipe.isPresent()) {
            logger.info("Retrieved recipe with ID: {}", id);
            return new ResponseEntity<>(recipe.get(), HttpStatus.OK);
        } else {
            logger.warn("Recipe with ID: {} not found.", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Updates an existing recipe by its ID.
     * @param id The ID of the recipe to update.
     * @param requestDto The RecipeRequestDto with updated data.
     * @return A ResponseEntity containing the updated Recipe object and HTTP status 200 OK, 404 Not Found, or 403 Forbidden.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable String id, @Valid @RequestBody RecipeRequestDto requestDto) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if (optionalRecipe.isEmpty()) {
            logger.warn("Attempt to update non-existent recipe with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Recipe existingRecipe = optionalRecipe.get();

        // Security check: Ensure the authenticated user owns this recipe
        if (!existingRecipe.getUserId().equals(currentUserId)) {
            logger.warn("User {} attempted to update recipe {} which belongs to {}. Access denied.", currentUserId, id, existingRecipe.getUserId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }

        // Update fields from DTO to an existing Recipe model
        existingRecipe.setTitle(requestDto.title());
        existingRecipe.setDescription(requestDto.description());
        existingRecipe.setServings(requestDto.servings());
        existingRecipe.setImageUrl(requestDto.imageUrl());
        existingRecipe.setIngredients(requestDto.ingredients());
        existingRecipe.setMethods(requestDto.methods());
        existingRecipe.setPrepTime(requestDto.prepTime());
        existingRecipe.setCookTime(requestDto.cookTime());
        existingRecipe.setTags(requestDto.tags());
        existingRecipe.setCategories(requestDto.categories());
        existingRecipe.setNotes(requestDto.notes());
        existingRecipe.setRating(requestDto.rating());

        existingRecipe.setUpdateDate(LocalDateTime.now()); // Update timestamp

        Recipe updatedRecipe = recipeRepository.save(existingRecipe);
        logger.info("Recipe with ID: {} updated by user: {}", id, currentUserId);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    /**
     * Deletes a recipe by its ID.
     * @param id The ID of the recipe to delete.
     * @return A ResponseEntity with HTTP status 204 No Content, 404 Not Found, or 403 Forbidden.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String id) {
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<Recipe> optionalRecipe = recipeRepository.findById(id);
        if (optionalRecipe.isEmpty()) {
            logger.warn("Attempt to delete non-existent recipe with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Recipe recipeToDelete = optionalRecipe.get();

        // Security check: Ensure the authenticated user owns this recipe
        if (!recipeToDelete.getUserId().equals(currentUserId)) {
            logger.warn("User {} attempted to delete recipe {} which belongs to {}. Access denied.", currentUserId, id, recipeToDelete.getUserId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN); // 403 Forbidden
        }

        recipeRepository.deleteById(id); // Performs the actual deletion
        logger.info("Recipe with ID: {} deleted by user: {}", id, currentUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }
}