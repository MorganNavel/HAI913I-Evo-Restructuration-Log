package com.example.tp3logging.controllers;
import com.example.tp3logging.models.User;
import com.example.tp3logging.repositories.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> list() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable
    Long userId) {
        logger.info("{  \"user\": \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "  \"operate\": \"READ\",  \"userId\": " + userId + ",  \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "}");
        return userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ("User with id " + userId) + " not found"));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable
    Long userId) {
        logger.info("{  \"user\": \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "  \"operate\": \"DELETE\",  \"userId\": " + userId + ",  \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "}");
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ("User with id " + userId) + " not found");
        }
        userRepository.deleteById(userId);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User update(@PathVariable
    Long userId, @Valid
    @RequestBody
    User user) {
        logger.info("{  \"user\": \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "  \"operate\": \"WRITE\",  \"userId\": " + userId + ",  \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "}");
        User existingUser = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ("User with id " + userId) + " not found"));
        BeanUtils.copyProperties(user, existingUser, "user_id");
        return userRepository.saveAndFlush(existingUser);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid
    @RequestBody
    User user, @RequestParam
    Long userId) {
        logger.info("{  \"user\": \"userDetails\": " +userRepository.findById(user.getUserId()).orElse(null).toString()+ "  \"operate\": \"WRITE\",  \"userId\": " + user.getUserId() + ",  \"userDetails\": " +userRepository.findById(user.getUserId()).orElse(null).toString()+ "}");
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use.");
        }
        return userRepository.save(user);
    }

    private Logger logger = org.apache.logging.log4j.LogManager.getLogger(UserController.class);
}