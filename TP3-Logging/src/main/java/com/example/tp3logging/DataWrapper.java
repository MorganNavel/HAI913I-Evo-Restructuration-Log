package com.example.tp3logging;

import com.example.tp3logging.models.Product;
import com.example.tp3logging.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DataWrapper {
    private List<User> users;
    private List<Product> products;
}