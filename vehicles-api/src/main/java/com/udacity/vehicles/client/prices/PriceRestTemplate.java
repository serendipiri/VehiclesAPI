package com.udacity.vehicles.client.prices;

import com.udacity.vehicles.api.CarError;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class PriceRestTemplate {

    private static final Logger log = LoggerFactory.getLogger(PriceRestTemplate.class);

    private final ModelMapper mapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pricing.endpoint}")
    String priceEndpoint;

    public PriceRestTemplate(ModelMapper mapper) {
        this.mapper = mapper;
    }

    // POST request
    public String getNewPriceForVehicle(Long vehicleId) {

        String url = priceEndpoint + "/prices/";
        Price price = new Price(vehicleId);
        price = restTemplate.postForObject(url, price, Price.class);

        if (price == null || price.getPrice() == null) {
            throw new CarError("Price Not Found.");
        }

        return price.toString();
    }


    // GET
    public String getPriceForVehicle(Long vehicleId) {

        String url = priceEndpoint + "/prices/" + vehicleId;
        Price price = restTemplate.getForObject(url, Price.class);
        if (price == null) {
            throw new CarError("Price Not Found.");
        }

        return price.toString();
    }


}
