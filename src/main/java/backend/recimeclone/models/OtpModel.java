package backend.recimeclone.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Document(collection = "Otps")
public class OtpModel {
    @Id
    private String id;// The id that will be generated in the MongoDB
    private String email; // The email to which the otp will be sent
    private String otp; // The otp it's self
    private LocalDateTime createdAt;// The time that the otp was created
    private LocalDateTime expiresAt; // Time it will take to expire
    private boolean used; // Either the otp is used or not

   }

