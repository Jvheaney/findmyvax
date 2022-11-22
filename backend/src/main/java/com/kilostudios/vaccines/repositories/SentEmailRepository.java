package com.kilostudios.vaccines.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kilostudios.vaccines.models.SentEmail;

@Repository
public interface SentEmailRepository extends JpaRepository<SentEmail, Long> {

	@Transactional
	@Modifying
	@Query(value="UPDATE sent_emails SET success=:success, time_sent=:time_sent WHERE userid = :userid AND email_transaction_id=:email_transaction_id", nativeQuery=true) 
    void updateReceipt(@Param("userid") String userid, @Param("email_transaction_id") String email_transaction_id, @Param("success") Boolean success, @Param("time_sent") Date time_sent);
	
}
