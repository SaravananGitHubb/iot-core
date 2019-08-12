package com.infosys.iot.repo;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.infosys.iot.models.MessageData;

@Repository
public interface MessageDataDAO extends JpaRepository<MessageData, Long> {
	@Query("SELECT u FROM MessageData u WHERE u.sensorId = ?1 and u.timestamp>= ?2")
	public List<MessageData> getMessagesForSensor(String sensorId, Date timestamp);

	@Query("SELECT distinct u.sensorId FROM MessageData u")
	public List<String> getDistinctSensor();
}
