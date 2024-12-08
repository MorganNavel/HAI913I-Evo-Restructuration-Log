package com.example.tp3logging.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringExclude;

import java.util.HashMap;
import java.util.Map;

@Data
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    @Email(message = "Email should be valid")
    private String email;

    @Column(name = "password")
    @ToStringExclude
    private String password;

    @Column(name = "age")
    @Min(value = 0, message = "Age should not be less than 0")
    private int age;

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        userMap.put("name", name);
        userMap.put("email", email);
        userMap.put("age", age);
        try {
            return objectMapper.writeValueAsString(userMap);
        } catch (JsonProcessingException e) {
            System.err.println("Error while converting User to JSON");
            return "null"; // Retourne un JSON vide en cas d'erreur
        }
    }

}