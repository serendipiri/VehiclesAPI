package com.udacity.vehicles.client.maps;

import com.udacity.vehicles.domain.Location;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
public class MapsRestTemplate {

    private static final Logger log = LoggerFactory.getLogger(MapsRestTemplate.class);

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${maps.endpoint}")
    String mapEndpoint;


    public Location getAddress(Location location) {
        try {

            String url = mapEndpoint + "/maps?lat=" + location.getLat() + "&lon=" + location.getLon();
            Address address = restTemplate.getForObject(url, Address.class);

            mapper.map(Objects.requireNonNull(address), location);

            return location;

        } catch (Exception e) {
            log.warn("Map service is down");
            return location;
        }
    }
}
