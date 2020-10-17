package engine.service;

import engine.dto.UserDto;
import engine.entity.User;
import engine.exception.NotUniqueValueException;
import engine.repository.UserCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// DON'T use this import. "User" is already occupied.
// import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * <a href="https://habr.com/ru/company/otus/blog/488418/">Аутентификация REST API с помощью Spring Security...</a>
 */
@Component
public class UserService implements UserDetailsService {
    private static final String USER_ROLE = "user";

    @Autowired
    private UserCrudRepository repo;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Add new user to database
     */
    public void addUser(UserDto user) throws NotUniqueValueException {
        String email = user.getEmail();
        if (isEmailExist(email)) {
            throw new NotUniqueValueException("Email " + email  + " occupied already");
        }

        User userEntity = new User()
            .setEmail(email)
//           .setPassword(user.getPassword());
            .setPassword(passwordEncoder.encode(user.getPassword()));

        repo.save(userEntity);
    }

    private boolean isEmailExist(String email) {
        User user = repo.findByEmail(email);
        return user != null;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = repo.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(USER_ROLE));

        return new org.springframework.security.core.userdetails
            .User(user.getEmail(), user.getPassword(), authorities);
    }
}
