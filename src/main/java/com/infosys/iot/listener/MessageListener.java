package com.infosys.iot.listener;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infosys.iot.models.MessageData;
import com.infosys.iot.repo.MessageDataDAO;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class MessageListener {
	@Autowired
	MessageDataDAO messageDataDao;

	@SqsListener("iot-channel")
	public void listen(String message) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		MessageData car = objectMapper.readValue(message, MessageData.class);
		messageDataDao.save(car);
		log.info(message);
	}
}
