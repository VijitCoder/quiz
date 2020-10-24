package engine.service;

import engine.dto.UserDetailsDto;
import engine.dto.UserDto;
import engine.entity.User;
import engine.exception.NotUniqueValueException;
import engine.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
// DON'T use this import. "User" is already occupied.
// import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Add new user to database
     */
    public void addUser(UserDto user) throws NotUniqueValueException {
        String email = user.getEmail();
        if (repo.findByEmail(email).isPresent()) {
            throw new NotUniqueValueException("Email " + email + " occupied already");
        }

        User userEntity = new User()
            .setEmail(email)
//           .setPassword(user.getPassword());
            .setPassword(passwordEncoder.encode(user.getPassword()));

        repo.save(userEntity);
    }

    /**
     * <a href="https://habr.com/ru/company/otus/blog/488418/">Аутентификация REST API с помощью Spring Security...</a>
     * <a href="https://www.youtube.com/watch?v=TNt3GHuayXs">JetBrains: Spring Security + MySQL</a>
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.repo
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found by " + email));

        return new UserDetailsDto(user);
    }
}
