package com.kilostudios.vaccines.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kilostudios.vaccines.models.Location;

@Repository
public interface LocationsRepository extends JpaRepository<Location, Long> {

	@Query("SELECT loc FROM Location loc") 
    List<Location> getLocations();
	
}
