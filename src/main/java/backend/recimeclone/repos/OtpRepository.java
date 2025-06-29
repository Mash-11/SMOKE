package backend.recimeclone.repos;

import backend.recimeclone.models.OtpModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OtpRepository extends MongoRepository<OtpModel, String> {
    Optional<OtpModel> findTopByEmailOrderByCreatedAtDesc(String email);

    Optional<OtpModel> findByEmailAndOtpAndUsedFalse(String email, String otp);
}
