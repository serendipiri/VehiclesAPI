package com.udacity.pricing.api;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.service.PriceException;
import com.udacity.pricing.service.PricingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Implements a REST Data-based controller for the pricing service.
 */

@RepositoryRestController
public class PricingController {

    private static final Logger log = LoggerFactory.getLogger(PricingController.class);

    PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    //TODO: error handling

    @RequestMapping(method = POST, value = "/prices")
    public @ResponseBody ResponseEntity<?> createPrice(@RequestBody Price price) {

        try {
            log.error("***************SEREN*************");
            if (price == null || price.getVehicleId() == null) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Price Param Not Fetched Correctly");
            }

            price = pricingService.getNewPrice(price.getVehicleId());

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(price, HttpStatus.CREATED);
    }


    /**
     * Gets the price for a requested vehicle.
     * @param vehicleId ID number of the vehicle for which the price is requested
     * @return price of the vehicle, or error that it was not found.
     */
    @RequestMapping(method = GET, value = "/prices/{vehicleId}")
    public @ResponseBody ResponseEntity<?> getByVehicleId(@PathVariable Long vehicleId) {

        Price price = null;
        try {

            price = pricingService.getPrice(vehicleId);

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Price Not Found", ex);
        }

        return new ResponseEntity<>(price, HttpStatus.OK);
    }

    @RequestMapping(method = DELETE, value = "/prices/{vehicleId}")
    public @ResponseBody ResponseEntity<?> deleteByVehicleId(@PathVariable Long vehicleId) {

        try {

            pricingService.deletePrice(vehicleId);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Price Not Found", ex);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
