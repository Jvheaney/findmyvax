package com.kilostudios.vaccines.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kilostudios.vaccines.models.User;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Transactional
	@Modifying
	@Query("UPDATE User SET longitude=:longitude, latitude=:latitude WHERE userid = :userid") 
    void setLongLatForUser(@Param("userid") String userid, @Param("longitude") Double longitude, @Param("latitude") Double latitude);
	
	@Transactional
	@Modifying
	@Query("UPDATE User SET send_email='f', allow_notifications='f' WHERE userid = :userid") 
    void unsubscribeUser(@Param("userid") String userid);
	
	@Transactional
	@Modifying
	@Query("UPDATE User SET allow_notifications='f' WHERE userid = :userid") 
    void stopNotifications(@Param("userid") String userid);
	
	@Transactional
	@Modifying
	@Query("UPDATE User SET send_email='t', allow_notifications='t' WHERE userid = :userid") 
    void subscribeUser(@Param("userid") String userid);
	
	@Query(value="SELECT longitude, latitude FROM geoip_blocks WHERE postal_code=:postal_code AND registered_country_geoname_id='6251999' LIMIT 1;", nativeQuery=true) 
    List<String[]> fetchLongLatFromPostalCode(@Param("postal_code") String postal_code);

    @Query(value="SELECT longitude, latitude FROM geoip_blocks WHERE network>>=:ip\\:\\:inet AND registered_country_geoname_id='6251999' LIMIT 1;", nativeQuery=true) 
    List<String[]> fetchLongLatFromIP(@Param("ip") String ip);
    
	@Transactional
	@Modifying
	@Query(value="INSERT INTO locations_slots (location_code, type, slots_open, appt_date, delta) VALUES (:location_code, :type, :slots_open, :appt_date, :slots_open) ON CONFLICT ON CONSTRAINT " + 
			"uniq_loc_type_date DO UPDATE SET slots_open=:slots_open, last_updated=now(), delta=:slots_open-locations_slots.slots_open WHERE locations_slots.location_code=:location_code " + 
			"AND locations_slots.type=:type AND locations_slots.appt_date=:appt_date", nativeQuery=true) 
    int insertLocations(@Param("location_code") String location_code, @Param("type") Integer type, @Param("slots_open") Integer slots_open, @Param("appt_date") Date appt_date);

	@Query("SELECT u FROM User u WHERE allow_notifications='t' AND send_email='t'") 
    List<User> getAllUsers();
	
}
