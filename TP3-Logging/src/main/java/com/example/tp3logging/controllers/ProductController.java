package com.example.tp3logging.controllers;

import com.example.tp3logging.models.Product;
import com.example.tp3logging.models.User;
import com.example.tp3logging.repositories.ProductRepository;
import java.util.List;

import com.example.tp3logging.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProductController(ProductRepository productRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Product> list() {
        return productRepository.findAll();
    }

    @GetMapping("/{productId}")
    public Product get(@PathVariable Long productId, @RequestParam Long userId) {
        if (userRepository.existsById(userId)) {
            return productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id " + productId + " not found"));
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
    }

    @PostMapping("/product")
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid @RequestBody Product product) {
        return productRepository.saveAndFlush(product);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id " + productId + " not found");
        }
        productRepository.deleteById(productId);
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Product update(@PathVariable Long productId, @Valid @RequestBody Product product) {
        Product existingProduct = productRepository.findById(productId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product with id " + productId + " not found"));
        BeanUtils.copyProperties(product, existingProduct, "product_id");
        return productRepository.saveAndFlush(existingProduct);
    }

}