package com.rizvi.spring.bootstrap;

import com.rizvi.spring.domain.*;
import com.rizvi.spring.repositories.*;
import com.rizvi.spring.web.model.BeerStyleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.CustomSQLErrorCodesTranslation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Component
public class DefaultBreweryLoader {


    private final BreweryRepository breweryRepository;
    private final BeerRepository beerRepository;
    private final BeerInventoryRepository beerInventoryRepository;
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;

    public DefaultBreweryLoader(BreweryRepository breweryRepository, BeerRepository beerRepository, BeerInventoryRepository beerInventoryRepository, BeerOrderRepository beerOrderRepository, CustomerRepository customerRepository) {
        this.breweryRepository = breweryRepository;
        this.beerRepository = beerRepository;
        this.beerInventoryRepository = beerInventoryRepository;
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
    }

    public void run(String... args) throws Exception {
        loadBreweryData();
    }

    private synchronized void loadBreweryData() {
        log.debug("Loading initial data. Count is: {}", beerRepository.count() );

        if(breweryRepository.count() == 0){
            breweryRepository.save(Brewery
                    .builder()
                    .breweryName("Cage Brewing")
                    .build());


            Beer mangoBobs = Beer.builder()
                    .beerName("Mango Bobs")
                    .beerStyle(BeerStyleEnum.IPA)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(337010000001L)
                    .build();

            beerRepository.save(mangoBobs);


            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(mangoBobs)
                    .quantityOnHand(100)
                    .build());


            Beer galaxyCat = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PALE_ALE)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(337010000002L)
                    .build();

            beerRepository.save(galaxyCat);


            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(galaxyCat)
                    .quantityOnHand(100)
                    .build());


            Beer pinBall = Beer.builder()
                    .beerName("Galaxy Cat")
                    .beerStyle(BeerStyleEnum.PORTER)
                    .minOnHand(12)
                    .quantityToBrew(200)
                    .upc(337010000003L)
                    .build();

            beerRepository.save(pinBall);


            beerInventoryRepository.save(BeerInventory.builder()
                    .beer(pinBall)
                    .quantityOnHand(100)
                    .build());

         Customer testCustomer = customerRepository.save(Customer
                     .builder()
                     .customerName("Test 1").apiKey(UUID.randomUUID())
                     .build());

         Set<BeerOrderLine> orderLine1 = new HashSet<>();
         orderLine1.add(BeerOrderLine.builder().beer(galaxyCat).orderQuantity(15).quantityAllocated(0).build());
         orderLine1.add(BeerOrderLine.builder().beer(pinBall).orderQuantity(7).quantityAllocated(0).build());

         BeerOrder testOrder1 = beerOrderRepository.save(BeerOrder.builder()
                 .orderStatus(OrderStatusEnum.NEW)
                 .customer(testCustomer)
                 .customerRef("testOrder1")
                 .orderStatusCallbackUrl("http;//example.com/post")
                 .beerOrderLines(orderLine1)
                 .build());

         orderLine1.forEach(line ->line.setBeerOrder(testOrder1));

            log.debug("Beer Records loaded: {}", "beerRepository");
        }
    }

}
