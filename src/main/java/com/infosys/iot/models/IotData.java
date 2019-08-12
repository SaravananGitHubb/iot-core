package com.infosys.iot.models;

import lombok.Data;

@Data
public class IotData {
	String binlocation;
	String starttime;
	String sensor;
	String endtime;// : "12-10-2018 11:00:00", dd-MM-yyyy HH:mm:ss
	String temperature;
}
