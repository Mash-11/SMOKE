package backend.recimeclone.service;

import backend.recimeclone.dtos.AuthResponseDto;
import backend.recimeclone.dtos.LoginDto;
import backend.recimeclone.dtos.RegisterDto;
import backend.recimeclone.dtos.ResponseDto;
import backend.recimeclone.dtos.VerifyOtpDto;
import backend.recimeclone.models.OtpModel;
import backend.recimeclone.models.UserModel; // Ensure this is correctly imported
import backend.recimeclone.repos.OtpRepository;
import backend.recimeclone.repos.UserRepository; // Changed AuthRepo to UserRepository for clarity
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Added @Transactional import for register method

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

// Import for Logger (add this line)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
@RequiredArgsConstructor // Automatically generates a constructor for all final fields
public class AuthService {

    // Logger instance for the service
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository; // Corrected to UserRepository
    private final OtpRepository otpRepository;
    private final EmailService emailService;

    // PasswordEncoder is already final and injected by @RequiredArgsConstructor
    @Getter // Still need @Getter if AuthController calls getPasswordEncoder()
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Handles user registration.
     * Creates a new user, saves them, and triggers OTP for verification.
     * @param dto Contains registration details.
     * @return A response DTO with a message and status code.
     */
    @Transactional // Ensures atomicity: user save and OTP generation/save are one transaction
    public ResponseDto register(RegisterDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            logger.warn("Registration attempt with already registered email: {}", dto.email());
            return new ResponseDto("Email is already registered.", 409);
        }

        UserModel user = new UserModel();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password())); // Password hashing is correct here
        user.setVerified(false); // User is not verified initially
        userRepository.save(user); // Save the user to the database

        try {
            // Generate and send OTP for email verification
            String otp = generateAndStoreOtp(dto.email());
            emailService.sendOtpEmail(dto.email(), otp);
            logger.info("User registered and OTP sent to email: {}", dto.email());
            return new ResponseDto("Registration successful. Please check your email for the OTP.", 201);
        } catch (Exception e) {
            // Use logger.error for proper error logging
            logger.error("Failed to send OTP email during registration for {}: {}", dto.email(), e.getMessage(), e);
            // If OTP sending fails, inform the user but acknowledge account creation
            return new ResponseDto("Account created, but failed to send OTP. Please request a new one.", 500);
        }
    }

    /**
     * Authenticates a user and returns a JWT if successful.
     * @param dto Contains login credentials.
     * @return An AuthResponseDto containing a message, JWT token, and status code.
     */
    public AuthResponseDto login(LoginDto dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.email(), dto.password())
            );
        } catch (AuthenticationException e) {
            logger.warn("Login failed for email: {}. Reason: {}", dto.email(), e.getMessage());
            return new AuthResponseDto("Invalid email or password.", null, 401);
        }

        var user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after successful authentication. This should not happen."));

        if (!user.isVerified()) {
            logger.warn("Unverified account login attempt for email: {}", dto.email());
            return new AuthResponseDto("Account not verified. Please verify your OTP first.", null, 403);
        }

        // Removed redundant (UserDetails) cast
        String jwtToken = jwtService.generateToken(user);
        logger.info("User logged in successfully: {}", dto.email());
        return new AuthResponseDto("Login successful.", jwtToken, 200);
    }

    /**
     * Verifies a user's OTP and returns a JWT upon success.
     * @param dto Contains email and OTP.
     * @return An AuthResponseDto containing a message, JWT token, and status code.
     */
    @Transactional // Ensure OTP update and user verification are atomic
    public AuthResponseDto verifyOtp(VerifyOtpDto dto) {
        var userOptional = userRepository.findByEmail(dto.email());
        if (userOptional.isEmpty()) {
            logger.warn("OTP verification attempt for non-existent user: {}", dto.email());
            return new AuthResponseDto("User not found.", null, 404);
        }

        // Use isOtpValid to perform all OTP checks
        if (!isOtpValid(dto.email(), dto.otp())) {
            logger.warn("Invalid or expired OTP provided for email: {}", dto.email());
            return new AuthResponseDto("Invalid or expired OTP.", null, 400);
        }

        UserModel user = userOptional.get();
        user.setVerified(true); // Mark user as verified
        userRepository.save(user); // Save the updated user status

        // Mark the specific OTP as used (important for single-use OTPs)
        // Find the latest OTP that matches email and OTP value and is not yet used
        // This implicitly assumes OTPs are single-use, and you want to mark the successful one.
        // Your findTopByEmailOrderByCreatedAtDesc is general.
        // It's better to specifically find the one verified.
        // However, if isOtpValid already found and marked it, this might be redundant.
        // Let's ensure there isOtpValid handles marking as used.
        // Re-evaluating isOtpValid logic below based on this.

        try {
            emailService.sendWelcomeEmail(dto.email());
            logger.info("Welcome email sent to {} after OTP verification.", dto.email());
        } catch (Exception e) {
            // Use logger.error for proper error logging
            logger.error("Failed to send welcome email to {} after OTP verification: {}", dto.email(), e.getMessage(), e);
        }

        // Removed redundant (UserDetails) cast
        String jwtToken = jwtService.generateToken(user);
        logger.info("OTP verified successfully for email: {}", dto.email());
        return new AuthResponseDto("OTP verified successfully. You are now logged in.", jwtToken, 200);
    }

    /**
     * Resends an OTP to a user's email.
     * @param email The user's email.
     * @return A ResponseDto with a message and status code.
     */
    @Transactional // Ensure OTP generation/save and email sending are atomic
    public ResponseDto resendOtp(String email) {
        if (userRepository.findByEmail(email).isEmpty()) {
            logger.warn("Resend OTP attempt for non-existent user: {}", email);
            return new ResponseDto("User not registered with this email.", 404);
        }

        try {
            String otp = generateAndStoreOtp(email);
            emailService.sendOtpEmail(email, otp);
            logger.info("New OTP sent to email: {}", email);
            return new ResponseDto("A new OTP has been sent to your email.", 200);
        } catch (Exception e) {
            logger.error("Failed to resend OTP to {}: {}", email, e.getMessage(), e);
            return new ResponseDto("Failed to resend OTP due to a server error.", 500);
        }
    }

    /**
     * Generates a 6-digit OTP, hashes it, and stores it in the database.
     * @param email The email for which the OTP is generated.
     * @return The raw (unhashed) OTP code.
     */
    // This method is correctly designed to be called internally by AuthService
    public String generateAndStoreOtp(String email) {
        String otpCode = String.valueOf(new Random().nextInt(900000) + 100000);
        String hashedOtp = passwordEncoder.encode(otpCode);
        OtpModel otp = new OtpModel();
        otp.setEmail(email);
        otp.setOtp(hashedOtp);
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // Assuming 5 minutes expiry as configured
        otp.setUsed(false);
        otpRepository.save(otp);
        logger.debug("Generated and stored OTP for email: {}", email); // Debug level for internal actions
        return otpCode;
    }

    /**
     * Validates an OTP provided by the user against the stored, latest OTP for that email.
     * Checks for correctness, expiry, and whether it has been used.
     * If valid, it also marks the OTP as used.
     * @param email The user's email.
     * @param inputOtp The OTP code provided by the user.
     * @return true if the OTP is valid and unused, false otherwise.
     */
    private boolean isOtpValid(String email, String inputOtp) {
        Optional<OtpModel> optionalOtp = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);

        if (optionalOtp.isEmpty()) {
            logger.warn("Validation failed: No OTP found for email: {}", email);
            return false;
        }

        OtpModel storedOtp = optionalOtp.get();

        // 1. Check if the provided OTP matches the stored (hashed) OTP
        if (!passwordEncoder.matches(inputOtp, storedOtp.getOtp())) {
            logger.warn("Validation failed: Incorrect OTP provided for email: {}", email);
            return false;
        }

        // 2. Check if the OTP is already used
        if (storedOtp.isUsed()) {
            logger.warn("Validation failed: OTP already used for email: {}", email);
            return false;
        }

        // 3. Check if the OTP has expired
        if (storedOtp.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("Validation failed: OTP expired for email: {}", email);
            // Optionally, mark expired OTPs as used to prevent further attempts on them
            storedOtp.setUsed(true);
            otpRepository.save(storedOtp);
            return false;
        }

        // If all checks pass, mark the OTP as used and save it
        storedOtp.setUsed(true);
        otpRepository.save(storedOtp);
        logger.info("OTP successfully validated and marked as used for email: {}", email);
        return true;
    }
}