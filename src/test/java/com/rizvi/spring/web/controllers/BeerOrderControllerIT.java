package com.rizvi.spring.web.controllers;


import com.rizvi.spring.domain.Customer;
import com.rizvi.spring.repositories.BeerOrderRepository;
import com.rizvi.spring.repositories.CustomerRepository;
import com.rizvi.spring.web.model.BeerOrderPagedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerOrderControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    CustomerRepository customerRepository;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = customerRepository.findAll().get(0);
    }

    @Test
    void testListOrders() {

        String uri = "/api/v1/customers/"+customer.getId().toString()+"/orders";

        BeerOrderPagedList pagedList = restTemplate.getForObject(uri, BeerOrderPagedList.class);

        assertThat(pagedList.getContent()).hasSize(1);
    }
}
