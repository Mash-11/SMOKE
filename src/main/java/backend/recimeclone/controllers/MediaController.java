package backend.recimeclone.controllers;

import backend.recimeclone.service.CloudinaryService; // Using 'service' as per your provided package name

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map; // Used for Map.of()

// Import for Logger (add this line)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // Import LoggerFactory (add this line)

@RestController
@RequestMapping("/api/media")
public class MediaController {

    // 1. Declare a static final Logger instance (add this line)
    private static final Logger logger = LoggerFactory.getLogger(MediaController.class);

    // Declare the service as final to indicate it's initialized once in the constructor
    private final CloudinaryService cloudinaryService;

    // --- Constructor for Dependency Injection ---
    // Spring will automatically inject an instance of CloudinaryService here.
    // This is the recommended way to inject dependencies.
    public MediaController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("Upload endpoint active");
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            // Use logger for info/warning messages too (changed this line)
            logger.warn("Upload attempt failed: No file provided.");
            return ResponseEntity.badRequest().body("Please select a file to upload.");
        }
        try {
            // Call the uploadImage method from your CloudinaryService
            String imageUrl = cloudinaryService.uploadImage(file);

            // Log success (add this line)
            logger.info("Image uploaded successfully. URL: {}", imageUrl);

            // Return a successful response with the image URL
            return ResponseEntity.ok(Map.of("url", imageUrl));
        } catch (Exception e) {
            // 2. Replace e.printStackTrace() with logger.error() (changed this line)
            // Pass the exception object as the last argument to include its stack trace in the log
            logger.error("Upload failed for file: {}. Error: {}", file.getOriginalFilename(), e.getMessage(), e);
            // Return an error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + e.getMessage());
        }
    }
}