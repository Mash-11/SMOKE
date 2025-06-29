package backend.recimeclone.controllers;

import backend.recimeclone.dtos.AuthResponseDto;
import backend.recimeclone.dtos.LoginDto;
import backend.recimeclone.dtos.RegisterDto;
import backend.recimeclone.dtos.VerifyOtpDto;
import backend.recimeclone.models.UserModel;
import backend.recimeclone.repos.AuthRepo;
import backend.recimeclone.service.AuthService;
import backend.recimeclone.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime; // Changed to LocalDateTime for consistency with UserModel
import org.slf4j.Logger; // Import Logger
import org.slf4j.LoggerFactory; // Import LoggerFactory
import jakarta.validation.Valid; // <--- THIS IS THE CRUCIAL IMPORT FOR VALIDATION

@RestController
@RequestMapping("/auths")
public class AuthController {

    // Logger instance for the controller
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthRepo authRepo;

    // (Your constructor if you had one, or keep @Autowired as you mentioned)

    /**
     * Handles user login.
     * @param data Login credentials.
     * @return ResponseEntity with AuthResponseDto containing a token or error message.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDto data) { // <--- Added @Valid
        AuthResponseDto response = authService.login(data);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    /**
     * Handles user registration.
     * @param data Registration details.
     * @return ResponseEntity with a String message.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto data) { // <--- Added @Valid
        // Check if an email is already registered
        if (authRepo.findByEmail(data.email()) != null) {
            logger.warn("Registration attempt with already registered email: {}", data.email()); // Using logger
            return ResponseEntity.badRequest().body("Email already registered");
        }

        // Create a new UserModel instance
        UserModel user = new UserModel();
        user.setEmail(data.email());
        user.setUsername(data.username());
        // Set and encode the password using AuthService's passwordEncoder directly
        // This ensures the password is hashed before saving to the database
        user.setPassword(authService.getPasswordEncoder().encode(data.password())); // Correctly encode and set a password
        user.setCreatedAt(LocalDateTime.now()); // Set the creation timestamp
        user.setVerified(false); // User is not verified initially
        authRepo.save(user); // Save the user to the database

        try {
            // Generate and send OTP for email verification
            String otp = authService.generateAndStoreOtp(data.email());
            emailService.sendOtpEmail(data.email(), otp);
            logger.info("User registered and OTP sent to email: {}", data.email()); // Using logger
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration successful, OTP sent");
        } catch (Exception e) {
            logger.error("Failed to send OTP email during registration for {}: {}", data.email(), e.getMessage(), e); // Using logger
            // If OTP sending fails, inform the user but acknowledge account creation
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    "Account created and OTP generated successfully, but failed to send OTP email due to a server issue. Request a resend or contact support.");
        }
    }

    /**
     * Verifies the OTP provided by the user.
     * @param dto VerifyOtpDto containing email and OTP.
     * @return ResponseEntity with AuthResponseDto containing a token or error message.
     */
    @PostMapping("/verifyOtp")
    // Removed @RequestParam String email as the email is already in the DTO
    public ResponseEntity<AuthResponseDto> verifyOtp(@Valid @RequestBody VerifyOtpDto dto) { // <--- Added @Valid
        // Basic validation for DTO content (though @Valid handles most of it)
        if (dto.email() == null || dto.email().isBlank() || dto.otp() == null || dto.otp().isBlank()) {
            logger.warn("Verify OTP attempt with missing email or OTP in DTO."); // Using logger
            return ResponseEntity.badRequest().body(new AuthResponseDto("Email and OTP are required", null, 400));
        }

        AuthResponseDto response = authService.verifyOtp(dto);
        return ResponseEntity.status(response.statusCode()).body(response);
    }

    /**
     * Resends an OTP to the user's email.
     * @param email The recipient's email address.
     * @return ResponseEntity with a String message.
     */
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestParam String email) {
        if (email == null || email.isBlank()) {
            logger.warn("Resend OTP attempt with missing email."); // Using logger
            return ResponseEntity.badRequest().body("Email is required for OTP resend.");
        }

        UserModel user = authRepo.findByEmail(email);
        if (user == null) {
            logger.warn("Resend OTP attempt for non-existent user: {}", email); // Using logger
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        try {
            String otp = authService.generateAndStoreOtp(email);
            emailService.sendOtpEmail(email, otp);
            logger.info("OTP resent successfully to: {}", email); // Using logger
            return ResponseEntity.ok("OTP resent successfully");
        } catch (Exception e) {
            logger.error("Error sending OTP during resend for {}: {}", email, e.getMessage(), e); // Using logger
            return ResponseEntity.internalServerError().body("Error sending OTP: " + e.getMessage());
        }
    }
}