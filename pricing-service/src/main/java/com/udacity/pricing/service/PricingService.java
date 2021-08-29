package com.udacity.pricing.service;

import com.udacity.pricing.domain.price.Price;
import com.udacity.pricing.domain.price.PriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * Implements the pricing service to get prices for each vehicle.
 */
@Service
public class PricingService {

    PriceRepository priceRepository;

    public PricingService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public Price getPrice(Long vehicleId) {
        return priceRepository.findByVehicleId(vehicleId);
    }

    /**
     * Uses price if there is a price with given vehicleId
     * Else Creates a new random price for given vehicleId
     *
     * @param vehicleId ID number of the vehicle the price is requested for.
     * @return price of the requested vehicle
     */
    public Price getNewPrice(Long vehicleId)  {
        Price priceObj = priceRepository.findByVehicleId(vehicleId);
        if (priceObj != null && priceObj.getPrice() != null) {
            return priceObj;
        }
        return priceRepository.save(new Price("USD", randomPrice(), vehicleId));
    }

    /**
     * Gets a random price to fill in for a given vehicle ID.
     * @return random price for a vehicle
     */
    private BigDecimal randomPrice() {
        return new BigDecimal(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal(5000d)).setScale(2, RoundingMode.HALF_UP);
    }

    public void deletePrice(Long vehicleId) {
        priceRepository.deleteByVehicleId(vehicleId);
    }
}
