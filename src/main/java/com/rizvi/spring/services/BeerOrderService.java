package com.rizvi.spring.services;

import com.rizvi.spring.web.model.BeerOrderDto;
import com.rizvi.spring.web.model.BeerOrderPagedList;
import org.springframework.data.domain.Pageable;


import java.util.UUID;

public interface BeerOrderService {
    BeerOrderPagedList listOrders(UUID customerId, Pageable pageable);

    BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto);

    BeerOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);


}
