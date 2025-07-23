package backend.recimeclone.service;

import backend.recimeclone.models.Recipe;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class RecipeScraperService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Recipe scrapeRecipeFromUrl(String url) throws Exception {
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .timeout(10000)
                .get();

        // Try JSON-LD structured data first
        Elements jsonLdElements = doc.select("script[type=application/ld+json]");
        for (Element element : jsonLdElements) {
            String jsonContent = element.html();
            if (jsonContent.contains("Recipe")) {
                Recipe recipe = parseJsonLdRecipe(jsonContent);
                if (recipe != null && recipe.getTitle() != null && !recipe.getTitle().isEmpty()) {
                    return recipe;
                }
            }
        }

        // Fallback to HTML parsing
        return parseHtmlRecipe(doc);
    }

    public Recipe parseRecipeFromText(String text) {
        Recipe recipe = new Recipe();
        String[] lines = text.split("\n");

        List<String> ingredients = new ArrayList<>();
        List<String> instructions = new ArrayList<>();

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String lowerLine = line.toLowerCase();

            // Extract title (first significant line)
            if (recipe.getTitle() == null && line.length() > 5 && !hasIngredientPattern(line)) {
                recipe.setTitle(line);
                continue;
            }

            // Extract cook time
            if (lowerLine.contains("cook") && (lowerLine.contains("min") || lowerLine.contains("hour"))) {
                Integer cookTime = extractTimeFromText(line);
                if (cookTime != null) recipe.setCookTime(cookTime);
                continue;
            }

            // Extract prep time
            if (lowerLine.contains("prep") && (lowerLine.contains("min") || lowerLine.contains("hour"))) {
                Integer prepTime = extractTimeFromText(line);
                if (prepTime != null) recipe.setPrepTime(prepTime);
                continue;
            }

            // Extract servings
            if (lowerLine.contains("serves") || lowerLine.contains("serving")) {
                Integer servings = extractServingsFromText(line);
                if (servings != null) recipe.setServings(servings);
                continue;
            }

            // Extract ingredients
            if (hasIngredientPattern(line)) {
                ingredients.add(line);
                continue;
            }

            // Extract instructions
            if (hasInstructionPattern(line)) {
                instructions.add(line);
            }
        }

        recipe.setIngredients(ingredients);
        recipe.setMethods(instructions);

        // Set defaults if not found
        if (recipe.getTitle() == null || recipe.getTitle().isEmpty()) {
            recipe.setTitle("Scanned Recipe");
        }
        if (recipe.getDescription() == null) {
            recipe.setDescription("Recipe extracted from text");
        }
        if (recipe.getServings() == 0) {
            recipe.setServings(4); // Default servings
        }

        // Set timestamps
        recipe.setCreationDate(LocalDateTime.now());
        recipe.setUpdateDate(LocalDateTime.now());

        return recipe;
    }

    private Recipe parseJsonLdRecipe(String jsonContent) {
        try {
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            if (rootNode.isArray()) {
                for (JsonNode node : rootNode) {
                    if ("Recipe".equals(node.path("@type").asText())) {
                        return extractRecipeFromJsonNode(node);
                    }
                }
            } else if ("Recipe".equals(rootNode.path("@type").asText())) {
                return extractRecipeFromJsonNode(rootNode);
            }
        } catch (Exception e) {
            // Ignore JSON parsing errors, fallback to HTML
        }
        return null;
    }

    private Recipe extractRecipeFromJsonNode(JsonNode node) {
        Recipe recipe = new Recipe();

        recipe.setTitle(node.path("name").asText());
        recipe.setDescription(node.path("description").asText());

        // Extract ingredients
        List<String> ingredients = new ArrayList<>();
        JsonNode ingredientNode = node.path("recipeIngredient");
        if (ingredientNode.isArray()) {
            for (JsonNode ingredient : ingredientNode) {
                ingredients.add(ingredient.asText());
            }
        }
        recipe.setIngredients(ingredients);

        // Extract instructions
        List<String> instructions = new ArrayList<>();
        JsonNode instructionNode = node.path("recipeInstructions");
        if (instructionNode.isArray()) {
            for (JsonNode instruction : instructionNode) {
                if (instruction.has("text")) {
                    instructions.add(instruction.path("text").asText());
                } else {
                    instructions.add(instruction.asText());
                }
            }
        }
        recipe.setMethods(instructions);

        // Extract times
        Integer cookTime = extractTimeFromText(node.path("cookTime").asText());
        if (cookTime != null) recipe.setCookTime(cookTime);

        Integer prepTime = extractTimeFromText(node.path("prepTime").asText());
        if (prepTime != null) recipe.setPrepTime(prepTime);

        // Extract servings
        int servings = node.path("recipeYield").asInt(4);
        recipe.setServings(servings);

        // Set timestamps
        recipe.setCreationDate(LocalDateTime.now());
        recipe.setUpdateDate(LocalDateTime.now());

        return recipe;
    }

    private Recipe parseHtmlRecipe(Document doc) {
        Recipe recipe = new Recipe();

        // Extract title
        recipe.setTitle(extractText(doc, "h1, .recipe-title, .entry-title, [itemprop=name]"));

        // Extract description
        recipe.setDescription(extractText(doc, ".recipe-description, .recipe-summary, [itemprop=description]"));

        // Extract ingredients
        List<String> ingredients = new ArrayList<>();
        Elements ingredientElements = doc.select(".recipe-ingredient, .ingredient, [itemprop=recipeIngredient], .recipe-ingredients li");
        for (Element element : ingredientElements) {
            String text = element.text().trim();
            if (!text.isEmpty()) {
                ingredients.add(text);
            }
        }
        recipe.setIngredients(ingredients);

        // Extract instructions
        List<String> instructions = new ArrayList<>();
        Elements instructionElements = doc.select(".recipe-instruction, .instruction, [itemprop=recipeInstructions], .recipe-instructions li, .recipe-method li");
        for (Element element : instructionElements) {
            String text = element.text().trim();
            if (!text.isEmpty()) {
                instructions.add(text);
            }
        }
        recipe.setMethods(instructions);

        // Extract times
        Integer cookTime = extractTimeFromText(extractText(doc, "[itemprop=cookTime], .cook-time, .cooking-time"));
        if (cookTime != null) recipe.setCookTime(cookTime);

        Integer prepTime = extractTimeFromText(extractText(doc, "[itemprop=prepTime], .prep-time, .preparation-time"));
        if (prepTime != null) recipe.setPrepTime(prepTime);

        // Set default servings if not found
        recipe.setServings(4);

        // Set timestamps
        recipe.setCreationDate(LocalDateTime.now());
        recipe.setUpdateDate(LocalDateTime.now());

        return recipe;
    }

    private String extractText(Document doc, String selector) {
        Element element = doc.selectFirst(selector);
        return element != null ? element.text().trim() : "";
    }

    private boolean hasIngredientPattern(String line) {
        Pattern[] patterns = {
                Pattern.compile("\\d+\\s*(cup|cups|tsp|tbsp|tablespoon|teaspoon|oz|lb|kg|g|ml|l|pound|ounce)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("\\d+/\\d+"),
                Pattern.compile("\\d+\\.\\d+"),
                Pattern.compile("(salt|pepper|sugar|flour|oil|butter|eggs?|onion|garlic)", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : patterns) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    private boolean hasInstructionPattern(String line) {
        Pattern[] patterns = {
                Pattern.compile("^\\d+[\\.)\\s]"),
                Pattern.compile("(heat|cook|bake|mix|add|stir|combine|pour|place|chop|dice|slice)", Pattern.CASE_INSENSITIVE),
                Pattern.compile("^(first|then|next|finally|meanwhile)", Pattern.CASE_INSENSITIVE)
        };

        for (Pattern pattern : patterns) {
            if (pattern.matcher(line).find()) {
                return true;
            }
        }
        return false;
    }

    private Integer extractTimeFromText(String text) {
        if (text == null || text.isEmpty()) return null;

        Pattern timePattern = Pattern.compile("(\\d+)\\s*(min|minute|minutes|hour|hours|hrs?)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = timePattern.matcher(text);

        if (matcher.find()) {
            int value = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase();

            if (unit.startsWith("hour") || unit.startsWith("hr")) {
                return value * 60; // Convert to minutes
            }
            return value;
        }
        return null;
    }

    private Integer extractServingsFromText(String text) {
        Pattern servingPattern = Pattern.compile("(\\d+)", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = servingPattern.matcher(text);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return null;
    }
}