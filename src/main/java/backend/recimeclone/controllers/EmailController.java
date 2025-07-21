package backend.recimeclone.controllers;

import backend.recimeclone.service.EmailService; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/mail")
public class EmailController {

   // Logger for this class
   private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

   // Inject the EmailService instead of JavaMailSender directly
   private final EmailService emailService;

   // Use constructor injection for EmailService
   @Autowired
   public EmailController(EmailService emailService) {
      this.emailService = emailService;
   }

   /**
    * Endpoint to send a test email using the generic sendEmail method in EmailService.
    * This replaces the direct JavaMailSender usage from the previous version.
    * @return A success or error message.
    */
   @RequestMapping("/send-email")
   public String sendTestEmail() {
      try {
         // Use the EmailService to send a generic test email
         emailService.sendEmail(
                 "martinafful070@gmail.com", // An email for testing
                 "Test email from Mixit (via service)",
                 "This is a test email from your Recime Clone backend, sent via EmailService."
         );
         return "SUCCESS: Test email sent via service";
      } catch (Exception e) {
         logger.error("Error sending test email: {}", e.getMessage(), e);
         return "ERROR: " + e.getMessage();
      }
   }

   /**
    * Endpoint to manually trigger sending an OTP email for testing purposes.
    * @param to The recipient's email address.
    * @param otp The OTP code to send.
    * @return A success or error message.
    */
   @GetMapping("/send-otp")
   public String sendOtp(@RequestParam String to, @RequestParam String otp) {
      try {
         emailService.sendOtpEmail(to, otp);
         return " OTP sent to " + to;
      } catch (Exception e) {
         logger.error("Error sending OTP email: {}", e.getMessage(), e);
         return " OTP failed: " + e.getMessage();
      }
   }

   /**
    * Endpoint to manually trigger sending a welcome email for testing purposes.
    * @param to The recipient's email address.
    * @return A success or error message.
    */
   @GetMapping("/send-welcome")
   public String sendWelcome(@RequestParam String to) {
      try {
         emailService.sendWelcomeEmail(to);
         return " Welcome email sent to " + to;
      } catch (Exception e) {
         System.err.println("Error sending welcome email: " + e.getMessage());
         return " Welcome email failed: " + e.getMessage();
      }
   }
}
