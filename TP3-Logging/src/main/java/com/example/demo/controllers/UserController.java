package com.example.demo.controllers;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import java.util.List;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<User> list() {
        return userRepository.findAll();
    }

    @GetMapping("/{userId}")
    public User get(@PathVariable
    Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable
    Long userId) {
        userRepository.deleteById(userId);
    }

    @PutMapping("/{userId}")
    public User update(@PathVariable
    Long userId, @RequestBody
    User user) {
        User existingUser = userRepository.findById(userId).orElse(null);
        if (existingUser == null) {
            return null;
        }
        BeanUtils.copyProperties(user, existingUser, "user_id");
        return userRepository.saveAndFlush(existingUser);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@RequestBody
    User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Cet email est déjà utilisé.");
        }
        return userRepository.save(user);
    }

}