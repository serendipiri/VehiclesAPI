package com.udacity.vehicles.api;

import com.udacity.vehicles.client.maps.MapsRestTemplate;
import com.udacity.vehicles.client.prices.PriceRestTemplate;
import com.udacity.vehicles.domain.Condition;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.Details;
import com.udacity.vehicles.domain.manufacturer.Manufacturer;
import com.udacity.vehicles.service.CarService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Collections;

import static com.udacity.vehicles.domain.Condition.NEW;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Implements testing of the CarController class.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceRestTemplate priceRestTemplate;

    @MockBean
    private MapsRestTemplate mapsRestTemplate;


    private static URI _URI;
    private Long carId = 1L;


    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @Before
    public void setup() throws URISyntaxException {
        _URI = new URI("/cars");
        Car car = getCar();
        car.setId(carId);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                        post(_URI)
                                .content(json.write(car).getJson())
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(carId.intValue())))
                .andExpect(jsonPath("$.details.body", is(car.getDetails().getBody())))
                .andExpect(jsonPath("$.details.fuelType", is(car.getDetails().getFuelType())))
                .andExpect(jsonPath("$.details.modelYear", is(car.getDetails().getModelYear())));

        verify(carService, times(1)).save(any());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {

        MvcResult mvcResult = mvc.perform(get(_URI))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$").isNotEmpty())
                .andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(carService, times(1)).list();

    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {

        Car car = getCar();
        car.setId(carId);

        MvcResult result = mvc.perform(get("/cars/" + car.getId()))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        Car fetchedCar = json.parseObject(result.getResponse().getContentAsString());
        assertTrue(fetchedCar.getId() == car.getId());
        assertTrue(fetchedCar.getCondition().name().equals(car.getCondition().name()));
        assertTrue(fetchedCar.getDetails().getModelYear().intValue() == car.getDetails().getModelYear().intValue());
        assertTrue(fetchedCar.getDetails().getEngine().equals(car.getDetails().getEngine()));
    }


    @Test
    public void updateCarTest() throws Exception {

        Car car = getCar();
        car.setId(carId);
        car.setCondition(NEW);
        car.getDetails().setManufacturer(new Manufacturer(103, "BMW"));
        car.getDetails().setNumberOfDoors(3);
        car.getDetails().setMileage(0);
        car.getDetails().setExternalColor("red");

        when(carService.save(any(Car.class))).thenReturn(car);

        mvc.perform(
                        put(new URI("/cars/" + car.getId()))
                                .content(json.write(car).getJson())
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.condition", is(NEW.name())))
                .andExpect(jsonPath("$.details.numberOfDoors", is(car.getDetails().getNumberOfDoors())))
                .andExpect(jsonPath("$.details.mileage", is(car.getDetails().getMileage())))
                .andExpect(jsonPath("$.details.manufacturer.code", is(car.getDetails().getManufacturer().getCode())))
                .andExpect(jsonPath("$.details.externalColor", is(car.getDetails().getExternalColor())))
                .andDo(print());

    }


    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        Car car = getCar();
        car.setId(carId);
        mvc.perform(delete(new URI("/cars/" + car.getId()))).andExpect(status().is(204));
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        car.setCondition(Condition.USED);
        return car;
    }

    @TestConfiguration
    static class Config {

        @Bean
        public RestTemplateBuilder restTemplateBuilder() {
            return new RestTemplateBuilder().setConnectTimeout(Duration.ofSeconds(1))
                    .setReadTimeout(Duration.ofSeconds(1));
        }

    }
}