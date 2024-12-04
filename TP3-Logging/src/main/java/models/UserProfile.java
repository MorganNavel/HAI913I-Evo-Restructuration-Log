package models;

import generated.com.example.demo.models.User;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Getter
@Component
public class UserProfile {
    private final long userId;
    private int readCount;
    private int writeCount;
    private final List<Double> productPrices;
    private final User user;
    private boolean boughtMostExpensive = false;
    public UserProfile(User user) {
        this.userId = user.getUserId();
        this.readCount = 0;
        this.writeCount = 0;
        this.productPrices = new ArrayList<>();
        this.user = user;
    }

    public void incrementReadCount() {
        this.readCount++;
    }

    public void incrementWriteCount() {
        this.writeCount++;
    }

    public void trackProductPrice(double price) {
        if (!productPrices.contains(price)) this.productPrices.add(price);
    }

    public boolean isMostlyRead() {
        return readCount >= writeCount;
    }

    public boolean isMostlyWrite() {
        return writeCount >= readCount;
    }

    /*
    public boolean searchedExpensiveProducts() {
        List<Product> products = productRepository.findAll();
        double mostExpensive = 0;
        for (Product product : products){
            double price = product.getPrice();
            if(mostExpensive<price) mostExpensive = price;
        }
        boughtMostExpensive = productPrices.contains(mostExpensive);
        return boughtMostExpensive;
    }
    */
}
