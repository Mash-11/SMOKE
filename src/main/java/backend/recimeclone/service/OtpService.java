package backend.recimeclone.service;

import backend.recimeclone.models.OtpModel;
import backend.recimeclone.repos.OtpRepository;
import org.springframework.transaction.annotation.Transactional; // Ensure this import is there
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // Ensure this import is there
import org.springframework.security.crypto.password.PasswordEncoder; // Ensure this import is there
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class OtpService {

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    // Consider making this final
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private final OtpRepository otpRepository; // Changed to final
    private final EmailService mailService;     // Changed to final
    private final PasswordEncoder passwordEncoder; // Changed to final

    @Value("${otp.expiry.minutes:5}")
    private int otpExpiryMinutes;

    // --- Constructor Injection for all dependencies ---
    public OtpService(OtpRepository otpRepository, EmailService mailService, PasswordEncoder passwordEncoder) {
        this.otpRepository = otpRepository;
        this.mailService = mailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional // Ensures atomicity for saving OTP
    public void sendOtpToUser(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            logger.error("Invalid email: {}", email);
            throw new IllegalArgumentException("Invalid email address");
        }

        String rawOtp = generateOtp();
        String hashedOtp = passwordEncoder.encode(rawOtp); // Hashing here

        OtpModel otp = new OtpModel();
        otp.setEmail(email);
        otp.setOtp(hashedOtp); // Storing hashed OTP
        otp.setCreatedAt(LocalDateTime.now());
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otp.setUsed(false);

        try {
            otpRepository.save(otp);
            mailService.sendOtpEmail(email, rawOtp); // Send raw OTP to a user
            logger.info("OTP sent to {}", email);
        } catch (Exception e) {
            logger.error("Failed to send OTP to {}: {}", email, e.getMessage(), e); // Added 'e' for stack trace
            throw new RuntimeException("Failed to send OTP", e);
        }
    }

    public boolean verifyOtp(String email, String inputOtp) {
        if (email == null || inputOtp == null || inputOtp.trim().isEmpty()) {
            logger.warn("Invalid input: email={}, otp={}", email, "[REDACTED]"); // Redacted OTP for logs
            return false;
        }

        // Use findTopByEmailOrderByCreatedAtDesc to get the latest OTP
        Optional<OtpModel> optionalOtp = otpRepository.findTopByEmailOrderByCreatedAtDesc(email);
        if (optionalOtp.isEmpty()) {
            logger.warn("No OTP found for email: {}", email);
            return false;
        }

        OtpModel otp = optionalOtp.get();

        // Check if OTP is used or expired first
        if (otp.isUsed()) {
            logger.warn("OTP already used for email: {}", email);
            return false;
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            logger.warn("OTP expired for email: {}", email);
            // Optionally, mark as used even if expired to prevent repeated attempts
            otp.setUsed(true);
            otpRepository.save(otp);
            return false;
        }

        // Use PasswordEncoder to match raw OTP with a hashed one
        if (!passwordEncoder.matches(inputOtp, otp.getOtp())) {
            logger.warn("Invalid OTP provided for email: {}", email); // Avoid logging raw inputOtp
            return false;
        }

        // If all checks pass, mark OTP as used
        otp.setUsed(true);
        otpRepository.save(otp);
        logger.info("OTP verified successfully for email: {}", email);

        return true;
    }

    private String generateOtp() {
        SecureRandom secureRandom = new SecureRandom();
        // Generates a 6-digit number (100,000 to 999,999)
        int otp = secureRandom.nextInt(900000) + 100000;
        return String.format("%06d", otp); // Ensures it's always 6 digits with leading zeros if necessary
    }
}