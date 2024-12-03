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

    public List<User> getUsers() {
        return users;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}