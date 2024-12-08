package com.example.tp3logging.models;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import java.util.Date;
import lombok.Data;
@Data
@Entity(name = "products")
@Access(AccessType.FIELD)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private long productId;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    @Min(value = 0L, message = "Price should not be less than 0")
    private double price;

    @Column(name = "expiration_date")
    private Date expirationDate;

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            System.err.println("Error while converting Product to JSON");
            return "{}";// Retourne un JSON vide en cas d'erreur

        }
    }
}