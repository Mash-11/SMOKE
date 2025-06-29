package backend.recimeclone.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyOtpDto(
        @NotBlank(message = "Email cannot be empty")
        @Email(message = "Email must be a valid email address")
        String email,

        @NotBlank(message = "OTP cannot be empty")
        @Pattern(regexp = "\\d{6}", message = "OTP must be a 6-digit number") // Assuming 6-digit OTP
        String otp
) {}
