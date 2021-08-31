package com.udacity.vehicles.service;

import com.udacity.vehicles.api.CarError;
import com.udacity.vehicles.client.maps.MapsRestTemplate;
import com.udacity.vehicles.client.prices.PriceRestTemplate;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private static final Logger log = LoggerFactory.getLogger(CarService.class);

    private final CarRepository repository;
    private final PriceRestTemplate priceRestTemplate;
    private final MapsRestTemplate mapsRestTemplate;


    public CarService(CarRepository repository, PriceRestTemplate priceRestTemplate, MapsRestTemplate mapsRestTemplate) {
        this.repository = repository;
        this.priceRestTemplate = priceRestTemplate;
        this.mapsRestTemplate = mapsRestTemplate;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {

        List<Car> cars = (List<Car>) repository.findAll();
        cars.forEach(car -> {
                    car.setPrice(priceRestTemplate.getPriceForVehicle(car.getId()));
                    car.setLocation(mapsRestTemplate.getAddress(car.getLocation()));
                }
        );

        return cars;
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);
        car.setPrice(priceRestTemplate.getPriceForVehicle(car.getId()));
        car.setLocation(mapsRestTemplate.getAddress(car.getLocation()));

        return car;
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        Car newCar = null;

        try {
            car.setLocation(mapsRestTemplate.getAddress(car.getLocation()));

            if (car.getId() != null) {
                return repository.findById(car.getId())
                        .map(carToBeUpdated -> {
                            carToBeUpdated.setDetails(car.getDetails());
                            carToBeUpdated.setLocation(car.getLocation());
                            carToBeUpdated.setPrice(priceRestTemplate.getNewPriceForVehicle(car.getId()));
                            return repository.save(carToBeUpdated);
                        }).orElseThrow(CarNotFoundException::new);
            }

            newCar = repository.save(car);
            newCar.setPrice(priceRestTemplate.getNewPriceForVehicle(car.getId()));

            log.info("Car saved with ID: " + car.getId());

        } catch (Exception e) {
            if (e instanceof CarNotFoundException || e instanceof CarError) {
                log.error(e.getMessage());
                throw e;
            } else {
                e.printStackTrace();
                throw new CarError("Something went wrong - Saving a Car");
            }
        }

        return newCar;
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    @Transactional
    public void delete(Long id) {

        Car car = repository.findById(id).orElseThrow(CarNotFoundException::new);
        repository.delete(car);
        priceRestTemplate.deletePrice(car.getId());

    }

}
