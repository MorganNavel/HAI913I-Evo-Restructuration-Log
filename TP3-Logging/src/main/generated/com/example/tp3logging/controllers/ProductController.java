package com.example.tp3logging.controllers;
import com.example.tp3logging.models.Product;
import com.example.tp3logging.repositories.ProductRepository;
import com.example.tp3logging.repositories.UserRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.apache.logging.log4j.Logger;
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
    public List<Product> listProducts(@RequestParam
    Long userId) {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            logger.info("{ \"user\": \"" + userId + "\", \"userDetails\": " + userRepository.findById(userId).orElse(null).toString() + ", \"operation\": \"READ\", \"productId\": " + product.getProductId() + ", \"productName\": \"" + product.getName() + "\" }");
        };
        return products;
    }

    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Product get(@PathVariable
    Long productId, @RequestParam
    Long userId) {
        logger.info("{  \"user\": \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "  \"operate\": \"READ\",  \"productId\": " + productId + ",\n  \"productDetails\": " +productRepository.findById(productId).orElse(null).toString()+ "}");
        return productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ("Product with id " + productId) + " not found"));
    }

    @PostMapping("/product")
    @ResponseStatus(HttpStatus.CREATED)
    public Product create(@Valid
    @RequestBody
    Product product, @RequestParam
    Long userId) {
        return productRepository.saveAndFlush(product);
    }

    @DeleteMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable
    Long productId, @RequestParam
    Long userId) {
        logger.info("{  \"user\": \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "  \"operate\": \"DELETE\",  \"productId\": " + productId + ",\n  \"productDetails\": " +productRepository.findById(productId).orElse(null).toString()+ "}");
        if (!productRepository.existsById(productId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ("Product with id " + productId) + " not found");
        }
        productRepository.deleteById(productId);
    }

    @PutMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public Product update(@PathVariable
    Long productId, @Valid
    @RequestBody
    Product product, @RequestParam
    Long userId) {
        logger.info("{  \"user\": \"userDetails\": " +userRepository.findById(userId).orElse(null).toString()+ "  \"operate\": \"WRITE\",  \"productId\": " + productId + ",\n  \"productDetails\": " +productRepository.findById(productId).orElse(null).toString()+ "}");
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, ("Product with id " + productId) + " not found"));
        BeanUtils.copyProperties(product, existingProduct, "product_id");
        return productRepository.saveAndFlush(existingProduct);
    }

    private Logger logger = org.apache.logging.log4j.LogManager.getLogger(ProductController.class);
}