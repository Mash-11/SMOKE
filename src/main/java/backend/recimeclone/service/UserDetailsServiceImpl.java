package backend.recimeclone.service;

import backend.recimeclone.repos.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user-specific data during authentication.
 * It fetches user details from the UserRepository based on the email (which serves as the username).
 */
@Service
@RequiredArgsConstructor // Lombok annotation for constructor injection of final fields
public class UserDetailsServiceImpl implements UserDetailsService {

    // Injects UserRepository to fetch user data from MongoDB
    private final UserRepository userRepository;

    /**
     * Locates the user based on the username (email in this application).
     * @param email The email address of the user.
     * @return A UserDetails object (which is implemented by UserModel).
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Attempts to find the user by email. If not found, throw UsernameNotFoundException.
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}
