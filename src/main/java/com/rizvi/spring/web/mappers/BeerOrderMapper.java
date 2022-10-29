package com.rizvi.spring.web.mappers;


import com.rizvi.spring.domain.Beer;
import com.rizvi.spring.domain.BeerOrder;
import com.rizvi.spring.domain.BeerOrderLine;
import com.rizvi.spring.web.model.BeerOrderDto;
import com.rizvi.spring.web.model.BeerOrderLineDto;
import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
public interface BeerOrderMapper {

    BeerOrderDto beerOrderToDto(BeerOrder beerOrder);

    BeerOrder dtoToBeerOrder(BeerOrderDto dto);

    BeerOrderLineDto beerOrderLineToDto(BeerOrderLineDto line);

    default BeerOrderLine dtoToBeerOrder(BeerOrderLineDto dto){
        return BeerOrderLine.builder()
                .orderQuantity(dto.getOrderQuantity())
                .beer(Beer.builder().id(dto.getBeerId()).build()).build();
    }
}
