package com.rizvi.spring.web.mappers;

import com.rizvi.spring.domain.Beer;
import com.rizvi.spring.web.model.BeerDto;
import org.mapstruct.Mapper;

@Mapper(uses = DateMapper.class)
public interface BeerMapper {

    BeerDto beerToBeerDto(Beer beer);

    Beer beerDtoToBeer(BeerDto beerDto);
}
