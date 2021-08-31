package com.udacity.vehicles.client.prices;

import com.udacity.vehicles.api.CarError;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class PriceRestTemplate {

    private static final Logger log = LoggerFactory.getLogger(PriceRestTemplate.class);

    private ModelMapper mapper;
    private RestTemplate restTemplate;

    @Value("${pricing.endpoint}")
    String priceEndpoint;

    public PriceRestTemplate(ModelMapper mapper, RestTemplate restTemplate) {
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    // POST
    public String getNewPriceForVehicle(Long vehicleId) {

        String url = priceEndpoint + "/prices/";
        Price price = new Price(vehicleId);

        try {

            price = restTemplate.postForObject(url, price, Price.class);

        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new CarError("Price Couldn't Created with Vehicle ID: " + vehicleId);
            }
        }

        if (price == null || price.getPrice() == null) {
            throw new CarError("Price Couldn't Created.");
        }

        return price.toString();
    }


    // GET
    public String getPriceForVehicle(Long vehicleId) {

        String url = priceEndpoint + "/prices/" + vehicleId;
        Price price = null;

        try {
            price = restTemplate.getForObject(url, Price.class);

            if (price != null && price.getPrice() != null) {
                return price.toString();
            }

        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new CarError("Price Not Found with Vehicle ID: " + vehicleId);
            }
        }

        return "(consult price)";
    }

    // DELETE
    public void deletePrice(Long vehicleId) {
        restTemplate.delete(priceEndpoint + "/prices/" + vehicleId);
    }

}
