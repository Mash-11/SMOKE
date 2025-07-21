package backend.recimeclone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class); // Add Logger

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.logo.url}")
    private String logoUrl;

    /**
     * Sends an OTP (One-Time Password) email to the specified recipient.
     * The email includes a dynamically generated OTP code and your application's logo.
     * @param toEmail The recipient's email address.
     * @param otpCode The 6-digit OTP code to send.
     */
    public void sendOtpEmail(String toEmail, String otpCode) {
        String subject = "🔐 Your One-Time Password (OTP) for Mixit";
        String htmlBody = buildOtpEmailHtml(otpCode);
        sendEmail(toEmail, subject, htmlBody);
    }

    /**
     * Sends a welcome email to a new user after successful registration and OTP verification.
     * The email includes your application's logo and helpful links for new users.
     * @param toEmail The recipient's email address.
     */
    public void sendWelcomeEmail(String toEmail) {
        String subject = "🎉 Welcome to Mixit!";
        String htmlBody = buildWelcomeEmailHtml();
        sendEmail(toEmail, subject, htmlBody);
    }

    /**
     * Constructs the HTML content for the OTP email.
     * Includes basic styling for responsiveness and embeds the logo and OTP code.
     * @param otpCode The OTP code to display.
     * @return A String containing the complete HTML body for the OTP email.
     */
    private String buildOtpEmailHtml(String otpCode) {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Your OTP Code</title>" +
                "    <style>" +
                "        body { font-family: 'Inter', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                "        .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                "        .header { background-color: #4CAF50; color: #ffffff; padding: 20px; text-align: center; border-top-left-radius: 8px; border-top-right-radius: 8px; }" +
                "        .header img { max-width: 120px; height: auto; border-radius: 8px; margin-bottom: 10px; }" +
                "        .content { padding: 30px; text-align: center; color: #333333; }" +
                "        .otp-code { font-size: 36px; font-weight: bold; color: #4CAF50; letter-spacing: 3px; background-color: #e8ffe8; padding: 15px 25px; border-radius: 8px; display: inline-block; margin: 20px 0; }" +
                "        .footer { background-color: #f0f0f0; color: #777777; padding: 20px; text-align: center; font-size: 14px; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px; }" +
                "        p { line-height: 1.6; }" +
                "        .important { color: #d9534f; font-weight: bold; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <img src='" + logoUrl + "' alt='Mixit Logo'>" + // Logo inserted here
                "            <h2>Your One-Time Password</h2>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Hello 👋</p>" +
                "            <p>Your One-Time Password (OTP) for Mixit is:</p>" +
                "            <span class='otp-code'>" + otpCode + "</span>" +
                "            <p>It expires in 5 minutes. Please do not share this code with anyone.</p>" +
                "            <p class='important'>If you did not request this OTP, please ignore this email.</p>" +
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>&copy; " + java.time.Year.now().getValue() + " Mixit Team. All rights reserved.</p>" +
                "            <p><a href='#' style='color: #4CAF50; text-decoration: none;'>Visit our website</a></p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * Constructs the HTML content for the welcome email.
     * Includes basic styling, logo, and example links for getting started.
     * @return A String containing the complete HTML body for the welcome email.
     */
    private String buildWelcomeEmailHtml() {
        return "<!DOCTYPE html>" +
                "<html lang='en'>" +
                "<head>" +
                "    <meta charset='UTF-8'>" +
                "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "    <title>Welcome to Mixit!</title>" +
                "    <style>" +
                "        body { font-family: 'Inter', sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }" +
                "        .container { max-width: 600px; margin: 20px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); }" +
                "        .header { background-color: #4CAF50; color: #ffffff; padding: 20px; text-align: center; border-top-left-radius: 8px; border-top-right-radius: 8px; }" +
                "        .header img { max-width: 120px; height: auto; border-radius: 8px; margin-bottom: 10px; }" +
                "        .content { padding: 30px; text-align: center; color: #333333; }" +
                "        .button { display: inline-block; background-color: #4CAF50; color: #ffffff; padding: 12px 25px; border-radius: 5px; text-decoration: none; font-weight: bold; margin-top: 20px; }" +
                "        .footer { background-color: #f0f0f0; color: #777777; padding: 20px; text-align: center; font-size: 14px; border-bottom-left-radius: 8px; border-bottom-right-radius: 8px; }" +
                "        p { line-height: 1.6; }" +
                "        ul { list-style: none; padding: 0; text-align: left; max-width: 400px; margin: 20px auto; }" +
                "        ul li { background-color: #e8ffe8; margin-bottom: 10px; padding: 10px 15px; border-left: 5px solid #4CAF50; border-radius: 4px; }" +
                "        ul li a { color: #333333; text-decoration: none; font-weight: bold; }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class='container'>" +
                "        <div class='header'>" +
                "            <img src='" + logoUrl + "' alt='Mixit Logo'>" + // Logo inserted here
                "            <h1>Welcome to Mixit!</h1>" +
                "        </div>" +
                "        <div class='content'>" +
                "            <p>Hi there,</p>" +
                "            <p>Thank you for signing up for Mixit! We're absolutely thrilled to have you join our community.</p>" +
                "            <p>Mixit is your go-to place for all things recipes. Get ready to discover, share, and create delicious meals! 🍳</p>" +
                "            <p>To help you get started, here are some quick links:</p>" +
                "            <ul>" +
                "                <li><a href='#' target='_blank'>Complete Your Profile</a></li>" + // Replace # with actual URL
                "                <li><a href='#' target='_blank'>Explore Recipes</a></li>" +      // Replace # with actual URL
                "                <li><a href='#' target='_blank'>Read Our Getting Started Guide</a></li>" + // Replace # with actual URL
                "            </ul>" +
                "            <a href='#' class='button'>Start Cooking Now!</a>" + // Replace # with actual URL
                "            <p style='margin-top: 30px;'>If you have any questions, feel free to reply to this email or visit our <a href='#' style='color: #4CAF50; text-decoration: none;'>support page</a>.</p>" + // Replace # with actual URL
                "        </div>" +
                "        <div class='footer'>" +
                "            <p>&copy; " + java.time.Year.now().getValue() + " Mixit Team. All rights reserved.</p>" +
                "            <p><a href='#' style='color: #4CAF50; text-decoration: none;'>Unsubscribe</a> | <a href='#' style='color: #4CAF50; text-decoration: none;'>Privacy Policy</a></p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    /**
     * The core method to send an email.
     * It uses JavaMailSender to construct and dispatch the email.
     * @param toEmail The recipient's email address.
     * @param subject The subject line of the email.
     * @param htmlBody The HTML content of the email body.
     */
    public void sendEmail(String toEmail, String subject, String htmlBody) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            // true indicates a multipart message, necessary for HTML content
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true indicates HTML content

            javaMailSender.send(message);
            logger.info("✅ Email sent to: {} with subject: {}", toEmail, subject); // Using logger
        } catch (Exception e) {
            logger.error("❌ Email sending failed for {}: {}", toEmail, e.getMessage(), e); // Using logger
            throw new RuntimeException("Failed to send email", e);
        }
    }
}