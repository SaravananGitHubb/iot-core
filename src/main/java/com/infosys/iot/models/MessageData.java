package com.infosys.iot.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Entity
public class MessageData {
	@Id
	@GeneratedValue
	private Long id;
	@JsonProperty("sensorId")
	String sensorId;
	@JsonProperty("temperature")
	Double temperature;
	@JsonProperty("timestamp")
	Date timestamp;
}
