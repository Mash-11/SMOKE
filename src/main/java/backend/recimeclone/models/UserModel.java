package backend.recimeclone.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections; // For simple roles

@Data
@NoArgsConstructor
@Document(collection = "users")
public class UserModel implements UserDetails { // Implement UserDetails

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String password;
    private String username;

    @Field("user_profile_url")
    private String userProfileUrl;

    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    private Boolean verified = false;

    // The unused constructor has been removed from here.

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isVerified() {
        return Boolean.TRUE.equals(verified);
    }

    // --- UserDetails Interface Implementations ---

    /**
     * Returns the authorities granted to the user.
     * For simplicity, we'll assign a default "USER" role.
     * In a real application, you might fetch roles from a database.
     * @return A collection of GrantedAuthority objects.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Returns the password used to authenticate the user.
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the username used to authenticate the user.
     * In your case, the email is used as the username.
     * @return The user's email.
     */
    @Override
    public String getUsername() {
        return this.email; // Use email as the username for Spring Security
    }

    /**
     * Indicates whether the user's account has expired.
     * @return true if the user's account is valid (non-expired), false otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true; // Assuming accounts do not expire
    }

    /**
     * Indicates whether the user is locked or unlocked.
     * @return true if the user is not locked, false otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // Assuming accounts are not locked
    }

    /**
     * Indicates whether the user's credentials (password) have expired.
     * @return true if the user's credentials are valid (non-expired), false otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Assuming credentials do not expire
    }

    /**
     * Indicates whether the user is enabled or disabled.
     * We'll link this to your `verified` field.
     * @return true if the user is enabled (verified), false otherwise.
     */
    @Override
    public boolean isEnabled() {
        return this.isVerified(); // User is enabled if their email is verified
    }
}