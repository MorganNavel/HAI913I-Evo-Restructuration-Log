package models;

import generated.com.example.demo.models.Product;
import generated.com.example.demo.models.User;
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