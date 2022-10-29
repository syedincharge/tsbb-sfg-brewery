package com.rizvi.spring.services;

import com.rizvi.spring.domain.Beer;
import com.rizvi.spring.repositories.BeerRepository;
import com.rizvi.spring.web.mappers.BeerMapper;
import com.rizvi.spring.web.model.BeerDto;
import com.rizvi.spring.web.model.BeerPagedList;
import com.rizvi.spring.web.model.BeerStyleEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BeerServiceImpl implements BeerService{


    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    public BeerServiceImpl(BeerRepository beerRepository, BeerMapper beerMapper) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
    }


    @Override
    public BeerPagedList listBeers(String beerName, BeerStyleEnum beerStyle, PageRequest pageRequest) {

        BeerPagedList beerPagedList;
        Page<Beer> beerPage;

        if (!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)){
            beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName,beerStyle,pageRequest);

        } else if (!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);

        } else if (!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
            beerPage = beerRepository.findAllByBeerStyle(beerStyle,pageRequest);
        }else {
            beerPage = beerRepository.findAll(pageRequest);
        }

        beerPagedList = new BeerPagedList(beerPage
                .getContent()
                .stream()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList()),
                PageRequest
                        .of(beerPage.getPageable().getPageNumber(),
                                beerPage.getPageable().getPageSize()),
                                beerPage.getTotalElements());
        return beerPagedList;
    }

    @Override
    public BeerDto findBeerById(UUID beerId) {

        Optional<com.rizvi.spring.domain.Beer> beerOptional = beerRepository.findById(beerId);

        if (beerOptional.isPresent()) {
            return beerMapper.beerToBeerDto(beerOptional.get());
        } else {
            throw new RuntimeException("Not Found");
        }
       }
    }

