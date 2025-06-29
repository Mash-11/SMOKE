package backend.recimeclone.repos;

import backend.recimeclone.models.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> { // Use String for the ID type

    // Spring Data MongoDB will automatically implement this method
    // to search for users by the 'email' field.
    Optional<UserModel> findByEmail(String email);
}