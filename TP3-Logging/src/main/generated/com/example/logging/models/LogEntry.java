package com.example.logging.models;
import com.example.tp3logging.models.Product;
import com.example.tp3logging.models.User;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class LogEntry {
    private String operation;

    private long userId;

    private long productId;

    private User userDetails;

    private Product productDetails;
}