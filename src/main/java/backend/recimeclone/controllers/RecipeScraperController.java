package backend.recimeclone.controllers;

import backend.recimeclone.dtos.UrlRequest;
import backend.recimeclone.dtos.TextRequest;
import backend.recimeclone.models.Recipe;
import backend.recimeclone.repos.RecipeRepository;
import backend.recimeclone.service.RecipeScraperService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recipe-scraper")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecipeScraperController {

    private static final Logger logger = LoggerFactory.getLogger(RecipeScraperController.class);

    private final RecipeScraperService scraperService;
    private final RecipeRepository recipeRepository;

    @PostMapping("/extract")
    public ResponseEntity<?> extractRecipeFromUrl(@Valid @RequestBody UrlRequest request) {
        try {
            logger.info("Extracting recipe from URL: {}", request.url());
            Recipe recipe = scraperService.scrapeRecipeFromUrl(request.url());

            if (recipe.getTitle() == null || recipe.getTitle().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Could not extract a valid recipe from the provided URL");
            }

            logger.info("Successfully extracted recipe: {}", recipe.getTitle());
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            logger.error("Failed to extract recipe from URL: {}", request.url(), e);
            return ResponseEntity.badRequest()
                    .body("Failed to extract recipe: " + e.getMessage());
        }
    }

    @PostMapping("/extract-text")
    public ResponseEntity<?> extractRecipeFromText(@Valid @RequestBody TextRequest request) {
        try {
            logger.info("Parsing recipe from text (length: {} chars)", request.text().length());
            Recipe recipe = scraperService.parseRecipeFromText(request.text());

            if (recipe.getIngredients().isEmpty() && recipe.getMethods().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Could not parse a valid recipe from the provided text");
            }

            logger.info("Successfully parsed recipe: {}", recipe.getTitle());
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            logger.error("Failed to parse recipe from text", e);
            return ResponseEntity.badRequest()
                    .body("Failed to parse recipe text: " + e.getMessage());
        }
    }

    @PostMapping("/extract-and-save")
    public ResponseEntity<?> extractAndSaveRecipe(@Valid @RequestBody UrlRequest request) {
        try {
            String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

            Recipe recipe = scraperService.scrapeRecipeFromUrl(request.url());

            if (recipe.getTitle() == null || recipe.getTitle().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Could not extract a valid recipe from the provided URL");
            }

            // Set user ID for the extracted recipe
            recipe.setUserId(currentUserId);

            // Save to database
            Recipe savedRecipe = recipeRepository.save(recipe);

            logger.info("Extracted and saved recipe: {} by user: {}", savedRecipe.getTitle(), currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
        } catch (Exception e) {
            logger.error("Failed to extract and save recipe from URL: {}", request.url(), e);
            return ResponseEntity.badRequest()
                    .body("Failed to extract and save recipe: " + e.getMessage());
        }
    }

    @PostMapping("/parse-and-save")
    public ResponseEntity<?> parseAndSaveRecipe(@Valid @RequestBody TextRequest request) {
        try {
            String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();

            Recipe recipe = scraperService.parseRecipeFromText(request.text());

            if (recipe.getIngredients().isEmpty() && recipe.getMethods().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body("Could not parse a valid recipe from the provided text");
            }

            // Set user ID for the parsed recipe
            recipe.setUserId(currentUserId);

            // Save to database
            Recipe savedRecipe = recipeRepository.save(recipe);

            logger.info("Parsed and saved recipe: {} by user: {}", savedRecipe.getTitle(), currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
        } catch (Exception e) {
            logger.error("Failed to parse and save recipe from text", e);
            return ResponseEntity.badRequest()
                    .body("Failed to parse and save recipe: " + e.getMessage());
        }
    }
}