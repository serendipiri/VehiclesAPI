package com.udacity.vehicles;

import com.udacity.vehicles.client.prices.Price;
import com.udacity.vehicles.client.prices.PriceClient;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VehiclesApiApplicationTests {

    @MockBean(name = "pricing")
    private WebClient pricing;

    @Autowired
    private PriceClient priceClient;

    @LocalServerPort
    private int port;

    @Value("${pricing.endpoint}")
    String priceEndpoint;

    @Test
    public void contextLoads() {
    }

    @Test
    public void getPrice() {
        RestTemplate restTemplate = new RestTemplate();
        String url = priceEndpoint + "/prices/" + 2L;
        Price price = restTemplate.getForObject(url, Price.class);
        Assert.assertTrue(Objects.equals(price.toString(), "75850.00 USD"));
    }
}
