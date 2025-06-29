package backend.recimeclone.service;

import backend.recimeclone.models.UserModel;
import backend.recimeclone.repos.AuthRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private AuthRepo authRepo;

    public UserModel getUser(String id) {
        return authRepo.findById(id).orElse(null);
    }
}