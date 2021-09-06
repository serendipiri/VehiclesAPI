package com.udacity.vehicles;

import com.udacity.vehicles.client.maps.MapsRestTemplate;
import com.udacity.vehicles.client.prices.PriceRestTemplate;
import com.udacity.vehicles.domain.car.Car;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VehiclesApiApplicationTests {

    @LocalServerPort
    private int port;


    @Test
    public void contextLoads() {
    }

}
