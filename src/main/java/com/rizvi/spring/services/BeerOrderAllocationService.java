package com.rizvi.spring.services;

import com.rizvi.spring.domain.BeerInventory;
import com.rizvi.spring.domain.BeerOrder;
import com.rizvi.spring.domain.BeerOrderLine;
import com.rizvi.spring.domain.OrderStatusEnum;
import com.rizvi.spring.repositories.BeerInventoryRepository;
import com.rizvi.spring.repositories.BeerOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class BeerOrderAllocationService {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerInventoryRepository beerInventoryRepository;


    public BeerOrderAllocationService(BeerOrderRepository beerOrderRepository, BeerInventoryRepository beerInventoryRepository) {
        this.beerOrderRepository = beerOrderRepository;
        this.beerInventoryRepository = beerInventoryRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 5000)
    public void runBeerOrderAllocation(){
        log.debug("Starting Beer Order Allocation");

        List<BeerOrder> newOrders = beerOrderRepository.findAllByOrderStatus(OrderStatusEnum.NEW);

        if(newOrders.size() > 0){

            log.debug("Number of orders found to allocate: "+newOrders.size());

            newOrders.forEach(beerOrder -> {
                log.debug("Allocating Order "+beerOrder.getCustomerRef());

                AtomicInteger totalOrdered = new AtomicInteger();
                AtomicInteger totalAllocated = new AtomicInteger();

                beerOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                    if((beerOrderLine.getOrderQuantity() - beerOrderLine.getQuantityAllocated()) > 0){
                        allocateBeerOrderLine(beerOrderLine);
                    }
                    totalOrdered.set(totalOrdered.get() + beerOrderLine.getQuantityAllocated());
                    totalAllocated.set(totalAllocated.get() + beerOrderLine.getQuantityAllocated());
                });

                if(totalOrdered.get() == totalAllocated.get()){
                    log.debug("Order Completly Allocated: "+ beerOrder.getCustomerRef());
                    beerOrder.setOrderStatus(OrderStatusEnum.READY);
                }
            });
        } else {
            log.debug("No Orders To Allocate");
        }

        beerOrderRepository.saveAll(newOrders);
    }

    private void allocateBeerOrderLine(BeerOrderLine beerOrderLine) {
        List<BeerInventory> beerInventoryList = beerInventoryRepository.findAllByBeer(beerOrderLine.getBeer());

        beerInventoryList.forEach(beerInventory -> {
            int inventory = (beerInventory.getQuantityOnHand() == null ? 0 : beerInventory.getQuantityOnHand());
            int orderQty = (beerOrderLine.getOrderQuantity() == null ? 0 : beerOrderLine.getOrderQuantity());
            int allocatedQty = (beerOrderLine.getQuantityAllocated() == null ? 0 : beerOrderLine.getOrderQuantity());
            int qtyToAllocate = orderQty - allocatedQty;


            if (inventory >= qtyToAllocate){
                inventory = inventory - qtyToAllocate;
                beerOrderLine.setQuantityAllocated(orderQty);
                beerInventory.setQuantityOnHand(inventory);
            } else if (inventory > 0){
                beerOrderLine.setQuantityAllocated(allocatedQty + inventory);
                beerInventory.setQuantityOnHand(0);
            }
        });

        beerInventoryRepository.saveAll(beerInventoryList);

        List<BeerInventory> zeroRecs = new ArrayList<>();

        beerInventoryList.stream()
                .filter(beerInventory -> beerInventory.getQuantityOnHand() == 0)
                .forEach(zeroRecs::add);
        beerInventoryRepository.deleteAll(zeroRecs);

    }


}