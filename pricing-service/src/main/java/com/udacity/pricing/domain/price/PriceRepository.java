package com.udacity.pricing.domain.price;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PriceRepository extends CrudRepository<Price, Long> {

    Price findByVehicleId(Long vehicleId);

    Long deleteByVehicleId(Long vehicleId);

}
