package com.example.tp3logging;
import com.example.tp3logging.models.Product;
import com.example.tp3logging.models.User;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
@Setter
@Getter
public class DataWrapper {
    private List<User> users;

    private List<Product> products;
}