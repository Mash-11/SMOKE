package backend.recimeclone.repos;

import backend.recimeclone.models.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepo extends MongoRepository<UserModel, String> {
    UserModel findByEmail(String email);

    }
