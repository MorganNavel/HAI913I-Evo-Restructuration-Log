package com.example.tp3logging;
import com.example.tp3logging.repositories.ProductRepository;
import com.example.tp3logging.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    private final ProductRepository productRepository;

    @Autowired
    public DataInitializer(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/data.json");
        DataWrapper wrapper = mapper.readValue(inputStream, DataWrapper.class);
        if (userRepository.count() == 0) {
            userRepository.saveAllAndFlush(wrapper.getUsers());
        }
        if (productRepository.count() == 0) {
            productRepository.saveAllAndFlush(wrapper.getProducts());
        }
    }
}