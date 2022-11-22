package com.kilostudios.vaccines.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.kilostudios.vaccines.models.LocationSlots;

@Repository
public interface LocationSlotsRepository extends JpaRepository<LocationSlots, Long> {

	@Query("SELECT ls FROM LocationSlots ls WHERE appt_date >= date_trunc('day', now()) ORDER BY appt_date, location_code, type ASC") 
    List<LocationSlots> getLocationData();
	
}
