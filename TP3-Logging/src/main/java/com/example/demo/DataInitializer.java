package com.example.demo;

import com.example.demo.repositories.ProductRepository;
import com.example.demo.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;



    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/data.json");

        DataWrapper wrapper = mapper.readValue(inputStream, DataWrapper.class);

        if (userRepository.count() == 0) {
            userRepository.saveAllAndFlush(wrapper.getUsers());
        }
        if (productRepository.count() == 0){
            productRepository.saveAllAndFlush(wrapper.getProducts());
        }
    }
}

