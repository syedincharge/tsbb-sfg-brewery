package com.rizvi.spring.services;

import com.rizvi.spring.web.model.BeerDto;
import com.rizvi.spring.web.model.BeerPagedList;
import com.rizvi.spring.web.model.BeerStyleEnum;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface BeerService {
    BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest);

    BeerDto findBeerById(UUID beerId);
}
