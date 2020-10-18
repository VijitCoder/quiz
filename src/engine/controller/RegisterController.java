package engine.controller;

import engine.dto.UserDto;
import engine.exception.NotUniqueValueException;
import engine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController()
@RequestMapping("/api")
public class RegisterController {
    private final UserService service;

    @Autowired
    public RegisterController(UserService service) {
        this.service = service;
    }

    /**
     * Add a new user
     */
    @PostMapping("/register")
    public void createUser(@Valid @RequestBody UserDto user) {
        try {
            service.addUser(user);
        } catch (NotUniqueValueException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
