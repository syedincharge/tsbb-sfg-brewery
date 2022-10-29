package com.rizvi.spring.services;

import com.rizvi.spring.domain.Beer;
import com.rizvi.spring.domain.BeerOrder;
import com.rizvi.spring.domain.Customer;
import com.rizvi.spring.domain.OrderStatusEnum;
import com.rizvi.spring.repositories.BeerOrderRepository;
import com.rizvi.spring.repositories.BeerRepository;
import com.rizvi.spring.repositories.CustomerRepository;
import com.rizvi.spring.web.mappers.BeerOrderMapper;
import com.rizvi.spring.web.model.BeerOrderDto;
import com.rizvi.spring.web.model.BeerOrderPagedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

   private final BeerOrderRepository beerOrderRepository;
   private final CustomerRepository customerRepository;
   private final BeerRepository beerRepository;
   private final BeerOrderMapper beerOrderMapper;

    public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository, CustomerRepository customerRepository, BeerRepository beerRepository, BeerOrderMapper beerOrderMapper) {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.beerRepository = beerRepository;
        this.beerOrderMapper = beerOrderMapper;
    }

    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()){
            Page<BeerOrder> beerOrderPage =
                    beerOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new BeerOrderPagedList(beerOrderPage
                    .stream()
                    .map(beerOrderMapper::beerOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                     beerOrderPage.getPageable().getPageNumber(),
                    beerOrderPage.getPageable().getPageSize()),
                    beerOrderPage.getTotalElements());
        } else {
            return null;
        }
    }
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()){
            BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null);
            beerOrder.setCustomer(customerOptional.get());
            beerOrder.setOrderStatus(OrderStatusEnum.NEW);

            beerOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                Optional<Beer> beerOptional = beerRepository.findById(beerOrderLine.getBeer().getId());

                if (beerOptional.isPresent()){
                    beerOrderLine.setBeer(beerOptional.get());
                }else{
                    throw new RuntimeException("Beer ID Not Found:  "+beerOrderLine.getBeer().getId());
                }
            });

            BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);

            log.debug("Saved Beer Order:  "+beerOrder.getId());

            return beerOrderMapper.beerOrderToDto(savedBeerOrder);
        }
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {

        return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);

        beerOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);

        beerOrderRepository.save(beerOrder);

    }

    private BeerOrder getOrder(UUID customerId, UUID orderId){
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()){
            Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(orderId);

            if (beerOrderOptional.isPresent()){
                BeerOrder beerOrder = beerOrderOptional.get();


                if(beerOrder.getCustomer().getId().equals(customerId)){
                    return beerOrder;
                }
            }
            throw new RuntimeException("Beer Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
