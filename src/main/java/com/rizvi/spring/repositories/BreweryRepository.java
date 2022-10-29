package com.rizvi.spring.repositories;

import com.rizvi.spring.domain.Brewery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BreweryRepository extends JpaRepository<Brewery, UUID> {


}
