package com.example.demo.controllers;
import com.example.demo.models.Product;
import com.example.demo.models.User;
import com.example.demo.repositories.ProductRepository;
import java.util.List;
import java.util.Optional;

import com.example.demo.repositories.UserRepository;
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
@RequestMapping("/")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/products")
    public List<Product> list() {
        return productRepository.findAll();
    }

    @GetMapping("/user/{userId}/product/{productId}")
    public Product get(@PathVariable Long userId,@PathVariable Long productId) {
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) return productRepository.findById(productId).orElse(null);
        throw new IllegalArgumentException("Accès refusé");
    }

    @PostMapping("/product")
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@RequestBody
    Product product) {
        return productRepository.saveAndFlush(product);
    }

    @DeleteMapping("/product/{productId}")
    public void delete(@PathVariable
    Long productId) {
        productRepository.deleteById(productId);
    }

    @PutMapping("/product/{productId}")
    public Product update(@PathVariable
    Long productId, @RequestBody
    Product product) {
        Product existingProduct = productRepository.findById(productId).orElse(null);
        if (existingProduct == null) {
            return null;
        }
        BeanUtils.copyProperties(product, existingProduct, "product_id");
        return productRepository.saveAndFlush(existingProduct);
    }

}