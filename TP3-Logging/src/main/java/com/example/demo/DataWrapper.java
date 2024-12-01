package com.example.demo;

import com.example.demo.models.Product;
import com.example.demo.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataWrapper {
    private List<User> users;
    private List<Product> products;
}